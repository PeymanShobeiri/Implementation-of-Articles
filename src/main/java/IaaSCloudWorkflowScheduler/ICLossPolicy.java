package IaaSCloudWorkflowScheduler;

import java.util.PriorityQueue;


public class ICLossPolicy extends WorkflowPolicy {

    public ICLossPolicy(WorkflowGraph g, ResourceSet rs, long bw) {
        super(g, rs, bw);

    }

    public float schedule(int startTime, int deadline) {
        float cost;

        setRuntimes();
        computeESTandEFT(startTime);
        computeLSTandLFT(deadline);
        initializeStartEndNodes(startTime, deadline);

        planning();
        removeEmptyInstances();

        setEndNodeEST();
        cost = super.computeFinalCost();
        return (cost);
    }

    private void planning() {
        PriorityQueue<WorkflowNode> queue = new PriorityQueue<WorkflowNode>(graph.nodes.size(), new WorkflowPolicy.UpRankComparator());
        result r;


        computeUpRank();
        for (WorkflowNode node : graph.nodes.values())
            if (!node.getId().equals(graph.getStartId()) && !node.getId().equals(graph.getEndId()))
                queue.add(node);

        //initial assignment
        while (!queue.isEmpty()) {
            WorkflowNode curNode = queue.remove();
            int bestInst = -1;
            int bestFinish = Integer.MAX_VALUE;

            for (int curInst = 0; curInst < instances.getSize(); curInst++) {
                r = checkInstance(curNode, instances.getInstance(curInst));
                if (r.finishTime < bestFinish) {
                    bestFinish = r.finishTime;
                    bestInst = curInst;
                }
            }

            //now checking lunching a new instance
            Instance inst = new Instance(instances.getSize(), resources.getResource(0));
            r = checkInstance(curNode, inst);
            if (r.finishTime < bestFinish) {
                bestFinish = r.finishTime;
                bestInst = inst.getId();
                instances.addInstance(inst);
            }

            setInstance(curNode, instances.getInstance(bestInst));
            updateParentsLFT(curNode);
        }

        //first making empty instances
        InstanceSet emptyInstances = new InstanceSet(resources);
        for (int curRes = 0; curRes < resources.getSize(); curRes++) {
            Instance inst = new Instance(emptyInstances.getSize(), resources.getResource(curRes));
            emptyInstances.addInstance(inst);
        }


        //refining the initial schedule
        boolean contSw = true;
        do {
            float minWeight = Float.MAX_VALUE;
            WorkflowNode minNode = null;
            int minInst = -10, minRes = -10;

            for (WorkflowNode curNode : graph.nodes.values()) {
                float curCost, weight;
                long curTime, estInterval, eftInterval, instStartTime;

                if (curNode.getId().equals(graph.getStartId()) || curNode.getId().equals(graph.getEndId()))
                    break;
				
				/*curTime = curNode.getEFT();
				instStartTime = instances.getInstance(curNode.getSelectedResource()).getStartTime();
				estInterval = (curNode.getEST()-instStartTime)/resources.getInterval();
				eftInterval = (curNode.getEST()-instStartTime)/resources.getInterval();
				if (instStartTime != curNode.getEST())
					curCost = (eftInterval-estInterval) * instances.getInstance(curNode.getSelectedResource()).getType().getCost();
				else 
					curCost = (eftInterval-estInterval+1) * instances.getInstance(curNode.getSelectedResource()).getType().getCost();*/

                curTime = curNode.getEFT() - curNode.getEST();
                curCost = (float) curTime / (float) resources.getInterval() * instances.getInstance(curNode.getSelectedResource()).getType().getCost();
                instStartTime = instances.getInstance(curNode.getSelectedResource()).getStartTime();


                //first checking cur instances
                for (int curInst = 0; curInst < instances.getSize(); curInst++) {
                    r = checkInstanceWithLFT(curNode, instances.getInstance(curInst));
                    if (r.cost < Float.MAX_VALUE) {

                        r.finishTime -= curNode.getEST();
                        r.cost = (float) r.finishTime / (float) resources.getInterval() * instances.getInstance(curInst).getType().getCost();

                        if (curCost - r.cost > 0 && r.finishTime - curTime > 0)
                            //weight = (r.finishTime-curTime)/(curCost-r.cost);
                            weight = 1 / (curCost - r.cost);
                        else weight = Float.MAX_VALUE;
                        if (weight < minWeight) {
                            minWeight = weight;
                            minNode = curNode;
                            minInst = curInst;
                        }
                    }
                }

                //now checking for launching a new instance
                for (int curInst = 0; curInst < emptyInstances.getSize(); curInst++) {
                    r = checkInstanceWithLFT(curNode, emptyInstances.getInstance(curInst));
                    if (r.cost < Float.MAX_VALUE) {

                        r.finishTime -= curNode.getEST();
                        r.cost = (float) r.finishTime / (float) resources.getInterval() * emptyInstances.getInstance(curInst).getType().getCost();

                        if (curCost - r.cost > 0 && r.finishTime - curTime > 0)
                            //weight = (r.finishTime-curTime)/(curCost-r.cost);
                            weight = 1 / (curCost - r.cost);
                        else weight = Float.MAX_VALUE;
                        if (weight < minWeight) {
                            minWeight = weight;
                            minNode = curNode;
                            minInst = -1;
                            minRes = curInst;
                        }
                    }
                }
            } //end for workflow node

            if (minWeight < Float.MAX_VALUE) {
                removeNodeFromInstance(minNode);

                if (minInst == -1) {
                    Instance inst = new Instance(instances.getSize(), resources.getResource(minRes));
                    instances.addInstance(inst);
                    setInstance(minNode, inst);
                } else {
                    setInstance(minNode, instances.getInstance(minInst));
                }
                updateParentsLFT(minNode);
                rescheduleChildren(minNode);
            } else contSw = false;

        } while (contSw);


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
        if ((finishTime != 0 && curStart > curIntervalFinish)) {//difference with PCPD2
            r.finishTime = Integer.MAX_VALUE;
            r.cost = Float.MAX_VALUE;
        } else
            r.cost = (float) (Math.ceil((double) (curFinish - startTime) / (double) interval) * curInst.getType().getCost() - curCost);

        return (r);
    }

    private result checkInstanceWithLFT(WorkflowNode curNode, Instance curInst) {
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
        if ((finishTime != 0 && curStart > curIntervalFinish) || curFinish > curNode.getLFT()) {
            r.cost = Float.MAX_VALUE;
            r.finishTime = Integer.MAX_VALUE;
        } else
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
        curNode.setRunTime(curFinish - curStart);
        curNode.setScheduled();

        if (curInst.getFinishTime() == 0) {
            curInst.setStartTime(curStart);
            curInst.setFirstTask(curNode.getId());
        }
        curInst.setFinishTime(curFinish);
        curInst.setLastTask(curNode.getId());
        curInst.addTask(curNode);
    }

    protected void rescheduleChildren(WorkflowNode curNode) {
        int start;

        for (Link child : curNode.getChildren()) {
            WorkflowNode childNode = graph.getNodes().get(child.getId());

            start = curNode.getEFT();
            if (curNode.getSelectedResource() != childNode.getSelectedResource())
                start += Math.round((float) child.getDataSize() / bandwidth);
            if (start > childNode.getEST()) {
                rescheduleNode(childNode, start);
                rescheduleChildren(childNode);
                updateParentsLFT(childNode);
            }
        }
    }

    protected void rescheduleNode(WorkflowNode node, int newStart) {
        Instance inst = instances.getInstance(node.getSelectedResource());
        long prevInterval = (node.getEST() - inst.getStartTime()) / resources.getInterval();
        long newInterval = (newStart - inst.getStartTime()) / resources.getInterval();
        int newFinish, bestFinish = Integer.MAX_VALUE, bestInst = 0;
        result r = new result();


        if (prevInterval == newInterval) {
            node.setEST(newStart);
            newFinish = newStart + Math.round((float) node.getInstructionSize() / inst.getType().getMIPS());
            node.setEFT(newFinish);
            if (inst.getLastTask().equals(node.getId()))
                inst.setFinishTime(newFinish);
            if (inst.getFirstTask().equals(node.getId()))
                inst.setStartTime(newStart);
        } else {
            removeNodeFromInstance(node);

            for (int i = 0; i < instances.getSize(); i++) {
                Instance curInst = instances.getInstance(i);
                if (curInst.getType().getId() == inst.getType().getId()) {
                    r = checkInstanceWithLFT(node, curInst);
                    if (r.finishTime < bestFinish) {
                        bestFinish = r.finishTime;
                        bestInst = i;
                    }
                }
            }

            //now checking lunching a new instance
            Instance newInst = new Instance(instances.getSize(), inst.getType());
            r = checkInstanceWithLFT(node, inst);
            if (r.finishTime < bestFinish) {
                bestFinish = r.finishTime;
                bestInst = newInst.getId();
                instances.addInstance(newInst);
            }
            setInstance(node, instances.getInstance(bestInst));
        }
    }

    protected void rescheduleTasks() {
        PriorityQueue<WorkflowNode> queue = new PriorityQueue<WorkflowNode>(graph.nodes.size(), new WorkflowPolicy.UpRankComparator());
        result r;
        InstanceSet newInstances = new InstanceSet(resources);


        computeUpRank();
        for (WorkflowNode node : graph.nodes.values())
            if (!node.getId().equals(graph.getStartId()) && !node.getId().equals(graph.getEndId()))
                queue.add(node);

        while (!queue.isEmpty()) {
            WorkflowNode curNode = queue.remove();
            int curResId = instances.getInstance(curNode.getSelectedResource()).getType().getId();
            int bestInst = -1;
            int bestFinish = Integer.MAX_VALUE;

            for (int curInst = 0; curInst < newInstances.getSize(); curInst++) {
                Instance inst = newInstances.getInstance(curInst);
                if (inst.getType().getId() == curResId) {
                    r = checkInstanceWithLFT(curNode, inst);
                    if (r.finishTime < bestFinish) {
                        bestFinish = r.finishTime;
                        bestInst = curInst;
                    }
                }
            }

            //now checking lunching a new instance
            Instance inst = new Instance(newInstances.getSize(), resources.getResource(curResId));
            r = checkInstanceWithLFT(curNode, inst);
            if (r.finishTime < bestFinish) {
                bestFinish = r.finishTime;
                bestInst = inst.getId();
                newInstances.addInstance(inst);
            }

            setInstance(curNode, newInstances.getInstance(bestInst));
        }

        instances = newInstances;
    }

    protected void removeNodeFromInstance(WorkflowNode node) {
        Instance inst = instances.getInstance(node.getSelectedResource());

        if (inst.getFirstTask().equals(node.getId())) {
            if (inst.getLastTask().equals(node.getId())) {
                inst.setStartTime(0);
                inst.setFinishTime(0);
                inst.setFirstTask("");
                inst.setLastTask("");
            } else {
                inst.setFirstTask(inst.getTasks().get(1).getId());
                inst.setStartTime(inst.getTasks().get(1).getEST());
            }
            inst.getTasks().remove(0);
        } else {
            int place = inst.getTasks().indexOf(node);
            if (inst.getLastTask().equals(node.getId())) {
                inst.setFinishTime(inst.getTasks().get(place - 1).getEFT());
                inst.setLastTask(inst.getTasks().get(place - 1).getId());
                inst.getTasks().remove(place);
            } else {
                long firstInterval = (inst.getTasks().get(place - 1).getEFT() - inst.getStartTime()) / resources.getInterval();
                long secondInterval = (inst.getTasks().get(place + 1).getEST() - inst.getStartTime()) / resources.getInterval();
                if (firstInterval != secondInterval) {
                    inst.setFinishTime(inst.getTasks().get(place - 1).getEFT());
                    inst.setLastTask(inst.getTasks().get(place - 1).getId());

                    Instance newInst = new Instance(instances.getSize(), inst.getType());
                    instances.addInstance(newInst);
                    for (int i = place + 1; i < inst.getTasks().size(); i++) {
                        setInstance(inst.getTasks().get(i), newInst);
                        updateChildrenEST(inst.getTasks().get(i));
                        updateParentsLFT(inst.getTasks().get(i));
                        inst.getTasks().remove(i);
                    }
                }
                inst.getTasks().remove(place);
            }
        }
    }

    void removeEmptyInstances() {
        for (int i = 0; i < instances.getSize(); i++) {
            Instance curInst = instances.getInstance(i);

            if (curInst.getFinishTime() == 0) {
                instances.removeInstance(curInst);
                i--;
            }
        }
    }

    protected void updateChildrenEST(WorkflowNode parentNode) {
        for (Link child : parentNode.getChildren()) {
            WorkflowNode childNode = graph.getNodes().get(child.getId());
            int newEST;

            newEST = parentNode.getEFT();
            if (parentNode.getSelectedResource() != childNode.getSelectedResource())
                newEST += Math.round((float) child.getDataSize() / bandwidth);
            if (childNode.getEST() < newEST) {
                childNode.setEST(newEST);
                childNode.setEFT((int) (newEST + Math.round(childNode.getRunTime())));

                updateChildrenEST(childNode);
            }
        }
    }

    protected void updateParentsLFT(WorkflowNode childNode) {
        for (Link parent : childNode.getParents()) {
            WorkflowNode parentNode = graph.getNodes().get(parent.getId());
            int newLFT;

            newLFT = childNode.getLST();
            if (parentNode.getSelectedResource() != childNode.getSelectedResource())
                newLFT -= Math.round((float) parent.getDataSize() / bandwidth);
            if (parentNode.getLFT() > newLFT) {
                parentNode.setLFT(newLFT);
                parentNode.setLST((int) (newLFT - Math.round(parentNode.getRunTime())));
                updateParentsLFT(parentNode);
            }
        }
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

