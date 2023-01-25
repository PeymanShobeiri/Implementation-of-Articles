package IaaSCloudWorkflowScheduler;

import java.util.ArrayList;
import java.util.List;


public class Instance {
    private int id;
    private Resource type;
    private long startTime, finishTime;
    private String firstTaskId, lastTaskId;
    private List<WorkflowNode> tasks;

    public Instance(int newId, Resource t) {
        id = newId;
        type = t;
        startTime = 0;
        finishTime = 0;

        tasks = new ArrayList<WorkflowNode>();
    }

    public Instance(int newId, Resource t, long st, long ft) {
        id = newId;
        type = t;
        startTime = st;
        finishTime = ft;

        tasks = new ArrayList<WorkflowNode>();
    }

    public long getStartTime() {
        return (startTime);
    }

    public void setStartTime(long st) {
        if (st >= 0)
            startTime = st;
    }

    public long getFinishTime() {
        return (finishTime);
    }

    public void setFinishTime(long ft) {
        if (ft >= 0)
            finishTime = ft;
    }

    public int getId() {
        return (id);
    }

    public Resource getType() {
        return (type);
    }

    public String getFirstTask() {
        return (firstTaskId);
    }

    public void setFirstTask(String id) {
        firstTaskId = id;
    }

    public String getLastTask() {
        return (lastTaskId);
    }

    public void setLastTask(String id) {
        lastTaskId = id;
    }

    public List<WorkflowNode> getTasks() {
        return (tasks);
    }

    public void addTask(WorkflowNode task) {
        tasks.add(task);
    }
}
