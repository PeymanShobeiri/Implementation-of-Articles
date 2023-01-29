package IaaSCloudWorkflowScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class PcpD2Policy extends WorkflowPolicy {

    public PcpD2Policy(WorkflowGraph g, ResourceSet rs, long bw) {
        super(g, rs, bw);

    }

    public float schedule(int startTime, int deadline) {
        float cost;

        setRuntimes();
        computeESTandEFT(startTime);
        computeLSTandLFT(deadline);
        initializeStartEndNodes(startTime, deadline);

        distributeDeadline();
        planning();

        setEndNodeEST();
        cost = super.computeFinalCost();
        return (cost);

    }


    public void distributeDeadline() {
        assignParents(graph.getNodes().get(graph.getEndId()));
        for (WorkflowNode node : graph.getNodes().values())
            node.setUnscheduled();
    }

    private void assignParents(WorkflowNode curNode) {
        List<WorkflowNode> criticalPath;

        criticalPath = findPartialCriticalPath(curNode);
        if (criticalPath.isEmpty())
            return;

        assignPath(criticalPath);
        for (int i = 0; i < criticalPath.size(); i++) {
            updateChildrenEST(criticalPath.get(i));
            updateParentsLFT(criticalPath.get(i));
        }
        for (int i = 0; i < criticalPath.size(); i++)
            assignParents(criticalPath.get(i));

        assignParents(curNode);
    }

    private void assignPath(List<WorkflowNode> path) {
        int last = path.size() - 1;
        int pathEST = path.get(0).getEST();
        int pathEFT = path.get(last).getEFT();
        int PSD = path.get(last).getLFT() - pathEST;


        for (int i = 0; i <= last; i++) {
            WorkflowNode curNode = path.get(i);
            ///////////////////////////////////////////////////////////////////////////////////
            int subDeadline = pathEST + (int) Math.floor((float) (curNode.getEFT() - pathEST) / (float) (pathEFT - pathEST) * PSD);

            curNode.setDeadline(subDeadline);
            curNode.setScheduled();

            if (i > 0) {
                int newEST = path.get(i - 1).getDeadline() + Math.round((float) getDataSize(path.get(i - 1), curNode) / bandwidth);
                if (newEST > curNode.getEST())
                    curNode.setEST(newEST);
            }
        }
    }

    protected void updateChildrenEST(WorkflowNode parentNode) {
        for (Link child : parentNode.getChildren()) {
            WorkflowNode childNode = graph.getNodes().get(child.getId());
            int newEST;

            if (!childNode.isScheduled()) {
                if (parentNode.isScheduled())
                    newEST = parentNode.getDeadline() + Math.round((float) child.getDataSize() / bandwidth);
                else
                    newEST = parentNode.getEFT() + Math.round((float) child.getDataSize() / bandwidth);

                if (childNode.getEST() < newEST) {
                    childNode.setEST(newEST);
                    childNode.setEFT((int) (newEST + Math.round(childNode.getRunTime())));
                    updateChildrenEST(childNode);
                }
            }
        }
    }

    protected void updateParentsLFT(WorkflowNode childNode) {
        for (Link parent : childNode.getParents()) {
            WorkflowNode parentNode = graph.getNodes().get(parent.getId());
            int newLFT;

            if (!parentNode.isScheduled()) {
                if (childNode.isScheduled())
                    newLFT = childNode.getEST() - Math.round((float) parent.getDataSize() / bandwidth);
                else
                    newLFT = childNode.getLST() - Math.round((float) parent.getDataSize() / bandwidth);

                if (parentNode.getLFT() > newLFT) {
                    parentNode.setLFT(newLFT);
                    parentNode.setLST((int) (newLFT - Math.round(parentNode.getRunTime())));
                    updateParentsLFT(parentNode);
                }
            }
        }
    }

    protected WorkflowNode findCriticalParent(WorkflowNode child) {
        WorkflowNode criticalPar = null;
        int criticalParStart = -1, curStart;

        for (Link parentLink : child.getParents()) {
            WorkflowNode parentNode = graph.getNodes().get(parentLink.getId());
            if (parentNode.isScheduled())
                continue;

            curStart = parentNode.getEFT() + Math.round((float) parentLink.getDataSize() / bandwidth);
            if (curStart > criticalParStart) {
                criticalParStart = curStart;
                criticalPar = parentNode;
            }
        }
        return (criticalPar);
    }

    protected List<WorkflowNode> findPartialCriticalPath(WorkflowNode curNode) {
        List<WorkflowNode> criticalPath = new ArrayList<WorkflowNode>();

        do {
            curNode = findCriticalParent(curNode);
            if (curNode != null)
                criticalPath.add(0, curNode);
        } while (curNode != null);
        return (criticalPath);
    }

    private void planning() {
        PriorityQueue<WorkflowNode> queue = new PriorityQueue<WorkflowNode>(graph.nodes.size(), new WorkflowPolicy.UpRankComparator());
        result r;
        int bestFinish = Integer.MAX_VALUE;

        computeUpRank();
        for (WorkflowNode node : graph.nodes.values())
            if (!node.getId().equals(graph.getStartId()) && !node.getId().equals(graph.getEndId()))
                queue.add(node);

        while (!queue.isEmpty()) {
            WorkflowNode curNode = queue.remove();
            int bestInst = -1;
            float bestCost = Float.MAX_VALUE;

            for (int curInst = 0; curInst < instances.getSize(); curInst++) {
                r = checkInstance(curNode, instances.getInstance(curInst));
                if (r.cost < bestCost) {
                    bestCost = r.cost;
                    bestFinish = r.finishTime;
                    bestInst = curInst;
                } else if (bestCost < Float.MAX_VALUE && r.cost == bestCost && r.finishTime < bestFinish) {
                    bestFinish = r.finishTime;
                    bestInst = curInst;
                }
            }
            if (bestInst == -1)
                for (int curRes = resources.getSize() - 1; curRes >= 0; curRes--) { //because the cheapest one is the last
                    Instance inst = new Instance(instances.getSize(), resources.getResource(curRes));
                    r = checkInstance(curNode, inst);
                    if (r.cost < Float.MAX_VALUE) {
                        bestInst = inst.getId();
                        instances.addInstance(inst);
                        break;
                    }
                }

            setInstance(curNode, instances.getInstance(bestInst));
        }
    }

    private result checkInstance(WorkflowNode curNode, Instance curInst) {
        long finishTime = curInst.getFinishTime();
        long startTime = curInst.getStartTime();
        int interval = resources.getInterval();
        double curCost = Math.ceil((double) (finishTime - startTime) / (double) interval) * curInst.getType().getCost();
        long curIntervalFinish = startTime + (long) Math.ceil((double) (finishTime - startTime) / (double) interval) * interval;
        int start, curStart = (int) finishTime, curFinish;

        for (Link parent : curNode.getParents()) {
            WorkflowNode parentNode = graph.getNodes().get(parent.getId());

            start = parentNode.getEFT();
            if (parentNode.getSelectedResource() != curInst.getId())
                start += Math.round((float) parent.getDataSize() / bandwidth);
            if (start > curStart)
                curStart = start;
        }

        if (finishTime == 0)
            startTime = curStart;

        result r = new result();
        curFinish = (int) (curStart + Math.ceil((float) curNode.getInstructionSize() / curInst.getType().getMIPS()));
        r.finishTime = curFinish;
        if ((finishTime != 0 && curStart > curIntervalFinish) || curFinish > curNode.getDeadline())
            r.cost = Float.MAX_VALUE;
        else
            r.cost = (float) (Math.ceil((double) (curFinish - startTime) / (double) interval) * curInst.getType().getCost() - curCost);

        return (r);
    }

    private void setInstance(WorkflowNode curNode, Instance curInst) {
        int start, curStart = (int) curInst.getFinishTime(), curFinish;

        for (Link parent : curNode.getParents()) {
            WorkflowNode parentNode = graph.getNodes().get(parent.getId());

            start = parentNode.getEFT();
            if (parentNode.getSelectedResource() != curInst.getId())
                start += Math.round((float) parent.getDataSize() / bandwidth);
            if (start > curStart)
                curStart = start;
        }

        curFinish = curStart + Math.round((float) curNode.getInstructionSize() / curInst.getType().getMIPS());
        curNode.setEST(curStart);
        curNode.setEFT(curFinish);
        curNode.setSelectedResource(curInst.getId());
        curNode.setScheduled();

        if (curInst.getFinishTime() == 0) {
            curInst.setStartTime(curStart);
            curInst.setFirstTask(curNode.getId());
        }
        curInst.setFinishTime(curFinish);
        curInst.setLastTask(curNode.getId());
        curInst.addTask(curNode);
    }

    protected void setEndNodeEST() {
        int endTime = -1;
        WorkflowNode endNode = graph.getNodes().get(graph.getEndId());

        for (Link parent : endNode.getParents()) {
            int curEndTime = graph.getNodes().get(parent.getId()).getEFT();
            if (endTime < curEndTime)
                endTime = curEndTime;
        }
        endNode.setEST(endTime);
        endNode.setEFT(endTime);
    }

    private class result {
        float cost;
        int finishTime;
    }
}

