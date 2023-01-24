package IaaSCloudWorkflowScheduler.aco;

import IaaSCloudWorkflowScheduler.Constants;
import IaaSCloudWorkflowScheduler.Link;
import IaaSCloudWorkflowScheduler.Resource;
import IaaSCloudWorkflowScheduler.WorkflowNode;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class CloudAcoResourceInstance {

    private static int PERIOD_DURATION = 3600;
    private int instanceId;
    private Resource resource;
    private WorkflowNode currentTask;
    private double currentTaskDuration = 0;
    private Queue<WorkflowNode> processedTasks;
    private HashSet<String> processedTasksIds;
    private double currentStartTime;
    private double instanceFinishTime;
    private float totalCost = 0;
    private double instanceStartTime;


    CloudAcoResourceInstance(Resource resource) {
        this.resource = resource;
        this.processedTasks = new LinkedList<>();
        this.processedTasksIds = new HashSet<>();

    }

    CloudAcoResourceInstance(Resource resource, int id) {
        this(resource);
        this.instanceId = id;
    }

    public Resource getResource() {
        return resource;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(int instanceId) {
        this.instanceId = instanceId;
    }

    public int getId() {
        return resource.getId();
    }

    public double getInstanceStartTime() {
        return instanceStartTime;
    }

    public synchronized void setCurrentTask(CloudAcoProblemNode cloudAcoProblemNode) {
        WorkflowNode node = cloudAcoProblemNode.getNode();
        if (node.getId().equalsIgnoreCase("start"))
            return;

        double newTaskDuration = getTaskDuration(node);

        int countOfHoursToProvision = Math.max((int) Math.ceil((newTaskDuration - getInstanceRemainingTime(node.getEST())) / (PERIOD_DURATION * 1.0f)), 0);
        int addedTimeToProvision = (countOfHoursToProvision * PERIOD_DURATION);

        if (currentTask == null) {
            instanceStartTime = node.getEST();
            instanceFinishTime = addedTimeToProvision;
            currentStartTime = node.getEST();
            currentTaskDuration = newTaskDuration;
            currentTask = node;
            totalCost += countOfHoursToProvision * resource.getCost();
        } else {
            double remain = getInstanceRemainingTime(getInstanceReleaseTime());
            countOfHoursToProvision = Math.max((int) Math.round((newTaskDuration - remain) / (double) PERIOD_DURATION), 0);
            addedTimeToProvision = (countOfHoursToProvision * PERIOD_DURATION);
            instanceFinishTime += addedTimeToProvision;
            currentStartTime = Math.max((int) getInstanceReleaseTime(), node.getEST());
            currentTaskDuration = newTaskDuration;
            currentTask = node;
            totalCost += countOfHoursToProvision * resource.getCost();
        }
        node.setAST((int) Math.round(currentStartTime));
        node.setAFT((int) Math.round(currentStartTime + currentTaskDuration));
        node.setRunTime(newTaskDuration);
        node.setScheduled();
        processedTasks.add(node);
    }

    private double getBandwidthDuration(WorkflowNode node) {
        double duration = 0;
        for (Link parent : node.getParents()) {
            if (!processedTasksIds.contains(parent.getId())) {
                double tt = (double) parent.getDataSize() / (Constants.BANDWIDTH * 1.0f);
                if (tt > duration)
                    duration = tt;
            }
        }

        return duration;
    }

    /**
     * @return time of current task will be finished
     */
    public double getInstanceReleaseTime() {
        return currentStartTime + currentTaskDuration;
    }

    /**
     * @return remaining time of provisioning
     */
    double getInstanceRemainingTime(double time) {
        return Math.max(instanceFinishTime - time, 0);
    }

    public float getTotalCost() {
        return totalCost;
    }

    public int getMIPS() {
        return resource.getMIPS();
    }

    public float getCost(WorkflowNode node) {
        double newTaskDuration = getTaskDuration(node);
        int countOfHoursToProvision = (int) Math.ceil(newTaskDuration / ((double) PERIOD_DURATION));

        if (countOfHoursToProvision == 0)
            countOfHoursToProvision = 1;
        //not started yet!
        if (currentTask == null) {
            return getResource().getCost() * countOfHoursToProvision;
        } else {
            if (newTaskDuration <= getInstanceRemainingTime(getInstanceReleaseTime())) {
                return 0;
            } else {
                double lack = newTaskDuration - getInstanceRemainingTime(getInstanceReleaseTime());
                countOfHoursToProvision = (int) Math.ceil(lack / ((double) PERIOD_DURATION));
                return countOfHoursToProvision * resource.getCost();
            }
        }
    }

    void reset() {
        this.totalCost = 0;
        this.instanceStartTime = 0;
        this.currentTask = null;
        this.currentTaskDuration = 0;
        this.instanceFinishTime = 0;
        this.processedTasks.clear();
        this.currentStartTime = 0;
        this.processedTasksIds.clear();
    }

    double getInstanceFinishTime() {
        return instanceFinishTime;
    }

    public double getTaskDuration(WorkflowNode node) {
        double runtime = Math.round((float) node.getInstructionSize() / getMIPS());
        return runtime + getBandwidthDuration(node);
    }
}