package IaaSCloudWorkflowScheduler;

import java.util.PriorityQueue;

public class ListPolicy extends WorkflowPolicy {

    public ListPolicy(WorkflowGraph g, ResourceSet rs, long bw) {
        super(g, rs, bw);

    }

    public float schedule(int startTime, int deadline) {
        float cost;

        setRuntimes();
        computeESTandEFT(startTime);
        computeLSTandLFT(deadline);
        initializeStartEndNodes(startTime, deadline);

        planning();

        setEndNodeEST();
        cost = super.computeFinalCost();
        return (cost);
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
        curFinish = curStart + Math.round((float) curNode.getInstructionSize() / curInst.getType().getMIPS());
        r.finishTime = curFinish;
        if ((finishTime != 0 && curStart > curIntervalFinish) || curFinish > curNode.getLFT()) //difference with PCPD2
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

