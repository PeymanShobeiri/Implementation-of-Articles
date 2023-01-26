package IaaSCloudWorkflowScheduler;

import utils.Board;
import utils.Table;

import java.util.*;

public abstract class WorkflowPolicy {
    protected final long MB = 1000000;
    protected final double pricePerMB = 0;
    protected WorkflowGraph graph;
    protected ResourceSet resources;
    protected InstanceSet instances;
    protected long bandwidth;

    public WorkflowPolicy(WorkflowGraph g, ResourceSet rs, long bw) {
        graph = g;
        resources = rs;
        instances = new InstanceSet(resources);
        bandwidth = bw;
    }

    abstract public float schedule(int startTime, int deadline);

    protected void setRuntimes() {
        int maxMIPS = resources.getMaxMIPS();
        for (WorkflowNode node : graph.getNodes().values())
            node.setRunTime(Math.round((float) node.getInstructionSize() / maxMIPS));
    }

    protected void computeLSTandLFT(int deadline) {
        Queue<String> candidateNodes = new LinkedList<>();
        Map<String, WorkflowNode> nodes = graph.getNodes();
        WorkflowNode curNode, parentNode, childNode;

        curNode = nodes.get(graph.getEndId());
        curNode.setLFT(deadline);
        curNode.setLST(deadline);
        curNode.setScheduled();
        for (Link parent : curNode.getParents())
            candidateNodes.add(parent.getId());

        while (!candidateNodes.isEmpty()) {
            double thisTime, minTime;
            curNode = nodes.get(candidateNodes.remove());
            minTime = Integer.MAX_VALUE;
            for (Link child : curNode.getChildren()) {
                childNode = nodes.get(child.getId());
                thisTime = childNode.getLFT() - childNode.getRunTime();
                thisTime -= Math.round((float) child.getDataSize() / bandwidth);
                if (thisTime < minTime)
                    minTime = thisTime;
            }
            curNode.setLFT((int) minTime);
            curNode.setLST((int) (minTime - Math.round(curNode.getRunTime())));
            curNode.setScheduled();

            for (Link parent : curNode.getParents()) {
                boolean isCandidate = true;
                parentNode = nodes.get(parent.getId());
                for (Link child : parentNode.getChildren())
                    if (!nodes.get(child.getId()).isScheduled())
                        isCandidate = false;
                if (isCandidate)
                    candidateNodes.add(parent.getId());
            }
        }

        for (WorkflowNode node : nodes.values())
            node.setUnscheduled();
    }

    protected void computeESTandEFT(int startTime) {
        Queue<String> candidateNodes = new LinkedList<>();
        Map<String, WorkflowNode> nodes = graph.getNodes();
        WorkflowNode curNode, parentNode, childNode;

        curNode = nodes.get(graph.getStartId());
        curNode.setEST(startTime);
        curNode.setEFT(startTime);
        curNode.setScheduled();
        for (Link child : curNode.getChildren())
            candidateNodes.add(child.getId());

        while (!candidateNodes.isEmpty()) {
            double thisTime, maxTime;
            curNode = nodes.get(candidateNodes.remove());
            maxTime = -1;
            for (Link parent : curNode.getParents()) {
                parentNode = nodes.get(parent.getId());
                thisTime = parentNode.getEFT() + Math.round((float) parent.getDataSize() / bandwidth);
                if (thisTime > maxTime)
                    maxTime = thisTime;
            }
            curNode.setEST((int) maxTime);
            curNode.setEFT((int) (maxTime + Math.round(curNode.getRunTime())));
            curNode.setScheduled();

            for (Link child : curNode.getChildren()) {
                boolean isCandidate = true;
                childNode = nodes.get(child.getId());
                for (Link parent : childNode.getParents())
                    if (!nodes.get(parent.getId()).isScheduled())
                        isCandidate = false;
                if (isCandidate)
                    candidateNodes.add(child.getId());
            }
        }
        for (WorkflowNode node : nodes.values())
            node.setUnscheduled();
    }

    protected void computeUpRank() {
        Queue<String> candidateNodes = new LinkedList<>();
        Map<String, WorkflowNode> nodes = graph.getNodes();
        WorkflowNode curNode, parentNode, childNode;

        curNode = nodes.get(graph.getEndId());
        curNode.setUpRank(0);
        curNode.setScheduled();
        for (Link parent : curNode.getParents())
            candidateNodes.add(parent.getId());

        while (!candidateNodes.isEmpty()) {
            int thisTime, maxTime, maxMIPS;
            curNode = nodes.get(candidateNodes.remove());
            maxTime = -1;
            for (Link child : curNode.getChildren()) {
                childNode = nodes.get(child.getId());
                thisTime = childNode.getUpRank() + Math.round((float) child.getDataSize() / bandwidth);
                if (thisTime > maxTime)
                    maxTime = thisTime;
            }
            maxMIPS = resources.getMeanMIPS();
            maxTime += Math.round((float) curNode.getInstructionSize() / maxMIPS);
            curNode.setUpRank(maxTime);
            curNode.setScheduled();

            for (Link parent : curNode.getParents()) {
                boolean isCandidate = true;
                parentNode = nodes.get(parent.getId());
                for (Link child : parentNode.getChildren())
                    if (!nodes.get(child.getId()).isScheduled())
                        isCandidate = false;
                if (isCandidate)
                    candidateNodes.add(parent.getId());
            }
        }

        for (WorkflowNode node : nodes.values())
            node.setUnscheduled();
    }

    protected long getDataSize(WorkflowNode parent, WorkflowNode child) {
        long size = 0;
        for (Link link : parent.getChildren())
            if (link.getId().equals(child.getId())) {
                size = link.getDataSize();
                break;
            }
        return (size);
    }

    void setEndNodeAST() {
        double endTime = -1;
        WorkflowNode endNode = graph.getNodes().get(graph.getEndId());

        for (Link parent : endNode.getParents()) {
            double curEndTime = graph.getNodes().get(parent.getId()).getAFT();
            if (endTime < curEndTime)
                endTime = curEndTime;
        }
        endNode.setAST((int) Math.round(endTime));
        endNode.setAFT((int) Math.round(endTime));
    }

    protected void initializeStartEndNodes(int startTime, int deadline) {
        Map<String, WorkflowNode> nodes = graph.getNodes();
        nodes.get(graph.getStartId()).setScheduled();
        nodes.get(graph.getEndId()).setScheduled();
    }

    String solutionAsString() {
        List<String> headersList = Arrays.asList("N", "R", "EST", "runtime", "EFT", "DeadLine", "cost");
        List<List<String>> rowsList = new ArrayList<>();
        for (WorkflowNode node : this.graph.nodes.values()) {
            rowsList.add(Arrays.asList(
                    node.getId() + "",
                    node.getSelectedResource() + "",
                    node.getEST() + "",
                    node.getRunTime() + "",
                    (node.getEFT()) + "",
                    node.getDeadline() + "",
                    node.getSelectedResource() == -1 ? "" : resources.getResource(node.getSelectedResource()).getCost() + ""
            ));
        }

        Board board = new Board(100);
        return board.setInitialBlock(new Table(board, 100, headersList, rowsList).tableToBlocks()).build().getPreview();
    }

    public float computeFinalCost() {
        float totalCost = 0, curCost;

        for (int instId = 0; instId < instances.getSize(); instId++) {
            Instance inst = instances.getInstance(instId);

            if (inst.getFinishTime() == 0)
                break;
            WorkflowNode first = graph.getNodes().get(inst.getFirstTask()), last = graph.getNodes().get(inst.getLastTask());
            curCost = (float) Math.ceil((double) (last.getEFT() - first.getEST()) / (double) resources.getInterval()) * inst.getType().getCost();
            totalCost += curCost;
        }

        return (totalCost);
    }

    public static class UpRankComparator implements Comparator<WorkflowNode> {
        public int compare(WorkflowNode node1, WorkflowNode node2) {
            return Double.compare(node2.getUpRank(), node1.getUpRank());
        }
    }

    public static class ASTComparator implements Comparator<WorkflowNode> {
        public int compare(WorkflowNode node1, WorkflowNode node2) {
            return Double.compare(node1.getAST(), node2.getAST());
        }
    }

    public static class childComparator implements Comparator<WorkflowNode> {
        public int compare(WorkflowNode node1, WorkflowNode node2) {
            return Double.compare(node2.getChildren().size(), node1.getChildren().size());
        }
    }

    public static class allChildComparator implements Comparator<WorkflowNode> {

        WorkflowGraph graph;
        public allChildComparator(WorkflowGraph graph) {
            this.graph = graph;
        }

        public int compare(WorkflowNode node1, WorkflowNode node2) {
            int childNumber1 = this.computeTotalChild(node1);
            setAllChildUnscheduled(node1);
            int childNumber2 = this.computeTotalChild(node2);
            setAllChildUnscheduled(node2);
            return Integer.compare(childNumber2, childNumber1);
        }

        public int computeTotalChild(WorkflowNode node){
            int number = 0;
            for (Link child : node.getChildren()){
                WorkflowNode childNode = this.graph.getNodes().get(child.getId());
                if (!childNode.isScheduled()) {
                    number += computeTotalChild(childNode);
                    number++;
                    childNode.setScheduled();
                }
            }
            return number;
        }

        public void setAllChildUnscheduled(WorkflowNode node){
            for (Link child : node.getChildren()){
                WorkflowNode childNode = this.graph.getNodes().get(child.getId());
                    setAllChildUnscheduled(childNode);
                    childNode.setUnscheduled();
            }
        }
    }


}
