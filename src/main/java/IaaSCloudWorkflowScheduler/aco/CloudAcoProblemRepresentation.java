package IaaSCloudWorkflowScheduler.aco;

import IaaSCloudWorkflowScheduler.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CloudAcoProblemRepresentation {

    final static int START_NODE_ID = -1;
    final static double START_NODE_PHEROMONE = 0.0001;
    private WorkflowGraph graph;
    private ResourceSet resourceSet;
    private long bandwidth;
    private int deadline;
    private CloudAcoProblemNode start, end;
    private CloudAcoResourceInstanceSet instanceSet;
    private List<CloudAcoProblemNode> problemNodeList;
    private Map<WorkflowNode, List<CloudAcoProblemNode>> neighbours;
    private List<WorkflowNode> sortedWorkflowNodes;
    private List<WorkflowNode> lacoSortedWorkflowNodes;

    public CloudAcoProblemRepresentation(WorkflowGraph graph, ResourceSet resourceSet, long bandwidth, int deadline, int instanceCount) {
        super();
        this.graph = graph;
        this.resourceSet = resourceSet;
        this.instanceSet = new CloudAcoResourceInstanceSet(resourceSet, instanceCount);
        this.bandwidth = bandwidth;
        this.deadline = deadline;
        this.sortedWorkflowNodes = topologicalSort();

        this.problemNodeList = this.createProblemNodeList(graph, resourceSet);
        this.neighbours = calculateConstantNeighbours();
    }

    public void resetNodes() {
        problemNodeList.forEach(CloudAcoProblemNode::resetNode);
    }

    public List<CloudAcoProblemNode> getNeighbours(WorkflowNode node) {
        return this.neighbours.get(this.sortedWorkflowNodes.get(this.sortedWorkflowNodes.indexOf(node) + 1));
    }

    public WorkflowGraph getGraph() {
        return graph;
    }

    public void setGraph(WorkflowGraph graph) {
        this.graph = graph;
    }

    public int getGraphSize() {
        return graph.getNodeNum();
    }

    CloudAcoProblemNode getStartNode() {
        return new CloudAcoProblemNode();
    }

    public List<CloudAcoProblemNode> getProblemNodeList() {
        return problemNodeList;
    }

    public long getBandwidth() {
        return bandwidth;
    }

    public CloudAcoResourceInstanceSet getInstanceSet() {
        return instanceSet;
    }

    public int getDeadline() {
        return deadline;
    }


    public void lacoSort(List<String> sortedTaskIds) {
        this.lacoSortedWorkflowNodes = new ArrayList<>(sortedWorkflowNodes);
        lacoSortedWorkflowNodes.sort((o1, o2) -> sortedTaskIds.indexOf(o1.getId()) > sortedTaskIds.indexOf(o2.getId()) ? 1 : -1);
        this.sortedWorkflowNodes = lacoSortedWorkflowNodes;
    }

    @SuppressWarnings("ALL")
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
            maxMIPS = resourceSet.getMeanMIPS();
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

    @SuppressWarnings("ALL")
    private List<CloudAcoProblemNode> createProblemNodeList(WorkflowGraph graph, ResourceSet resourceSet) {

        AtomicInteger id = new AtomicInteger();
        List<CloudAcoProblemNode> problemNodeList = new ArrayList<>();
        int numbersOfTasks = graph.getNodeNum();

        Map<String, WorkflowNode> nodes = graph.getNodes();

        PriorityQueue<WorkflowNode> queue = new PriorityQueue<WorkflowNode>(graph.nodes.size(), new WorkflowPolicy.allChildComparator(graph));
        PcpD2Policy2.result r;
        int bestFinish = Integer.MAX_VALUE;

        computeUpRank();

        for (WorkflowNode curNode : this.sortedWorkflowNodes) {
            curNode.setUnscheduled();
            instanceSet.getInstances().values().forEach(instances -> {
                if (curNode.getId().equals("start")) {
                    start = new CloudAcoProblemNode(curNode, instances.get(0), id.get());
                    problemNodeList.add(start);
                } else if (curNode.getId().equals("end")) {
                    end = new CloudAcoProblemNode(curNode, instances.get(0), id.get());
                    problemNodeList.add(end);
                } else {
                    for (CloudAcoResourceInstance instance : instances) {
                        CloudAcoProblemNode node = new CloudAcoProblemNode(curNode, instance, id.get());
                        id.getAndIncrement();
                        problemNodeList.add(node);
                    }
                }
            });
        }

        return problemNodeList;
    }

    private ArrayList<WorkflowNode> topologicalSort() {
        PriorityQueue<WorkflowNode> nodesPriorityQueue = new PriorityQueue<WorkflowNode>(graph.nodes.size(), new WorkflowPolicy.allChildComparator(graph));
        ArrayList<WorkflowNode> workflowNodes = new ArrayList<>();

        WorkflowNode curNode = graph.getNodes().get("start");
        workflowNodes.add(curNode);

        for (int i = 1; i < this.getGraph().getNodeNum(); i++) {
            curNode = selectNextTopoNode(curNode, nodesPriorityQueue, workflowNodes);
            workflowNodes.add(curNode);
        }
        return workflowNodes;
    }

    private WorkflowNode selectNextTopoNode(WorkflowNode node, PriorityQueue<WorkflowNode> nodesPriorityQueue, ArrayList workflowNodes) {
        for (Link child : node.getChildren()) {
            WorkflowNode childNode = this.graph.getNodes().get(child.getId());
            Boolean isChildValid = true;

            for (Link parent : childNode.getParents()) {
                WorkflowNode parentNode = this.graph.getNodes().get(parent.getId());

                if (!workflowNodes.contains(parentNode)) {
                    isChildValid = false;
                    break;
                }
            }

            if (isChildValid) {
                nodesPriorityQueue.add(childNode);
            }
        }
        return nodesPriorityQueue.remove();
    }

    public CloudAcoProblemNode getStart() {
        return start;
    }

    public CloudAcoProblemNode getEnd() {
        return end;
    }

    private Map<WorkflowNode, List<CloudAcoProblemNode>> calculateConstantNeighbours() {
        Map<WorkflowNode, List<CloudAcoProblemNode>> neighbours = new HashMap<>();
        for (WorkflowNode node : graph.nodes.values()) {
            List<CloudAcoProblemNode> neighboursList = new ArrayList<>();

            for (CloudAcoProblemNode cloudAcoProblemNode : problemNodeList) {
                if (cloudAcoProblemNode.getNode().equals(node)) {
                    neighboursList.add(cloudAcoProblemNode);
                }
            }
            neighbours.put(node, neighboursList);
        }
        return neighbours;
    }

    private Map<WorkflowNode, List<CloudAcoProblemNode>> calculateNeighbours() {
        Map<WorkflowNode, List<CloudAcoProblemNode>> neighbours = new HashMap<>();
        for (WorkflowNode node : graph.nodes.values()) {
            List<CloudAcoProblemNode> neighboursList = new ArrayList<>();

            List<WorkflowNode> childes = new ArrayList<>();
            for (Link child : node.getChildren()) {
                WorkflowNode childNode = this.graph.getNodes().get(child.getId());
                childes.addAll(computeChildes(childNode));
            }

            List<WorkflowNode> parents = computeParents(node);

            for (CloudAcoProblemNode cloudAcoProblemNode : problemNodeList) {
                if (!cloudAcoProblemNode.getNode().equals(node) && !childes.contains(cloudAcoProblemNode.getNode()) &&
                        !parents.contains(cloudAcoProblemNode.getNode())) {
                    neighboursList.add(cloudAcoProblemNode);
                }
            }
            neighbours.put(node, neighboursList);
        }
        return neighbours;
    }

    private ArrayList<WorkflowNode> computeParents(WorkflowNode node) {
        ArrayList<WorkflowNode> workflowNodes = new ArrayList<>();
        for (Link parent : node.getParents()) {
            WorkflowNode parentNode = this.graph.getNodes().get(parent.getId());
            workflowNodes.addAll(computeParents(parentNode));
            workflowNodes.add(parentNode);
        }
        return workflowNodes;
    }

    private ArrayList<WorkflowNode> computeChildes(WorkflowNode node) {
        ArrayList<WorkflowNode> workflowNodes = new ArrayList<>();
        for (Link child : node.getChildren()) {
            WorkflowNode childNode = this.graph.getNodes().get(child.getId());
            workflowNodes.addAll(computeChildes(childNode));
            workflowNodes.add(childNode);
        }
        return workflowNodes;
    }


    void updateChildrenEST(WorkflowNode parentNode) {
        for (Link child : parentNode.getChildren()) {
            WorkflowNode childNode = graph.getNodes().get(child.getId());
            int newEST;

            if (!childNode.isScheduled()) {
                newEST = parentNode.getAFT();
                if (childNode.getEST() < newEST) {
                    childNode.setEST(newEST);
                    childNode.setEFT((int) (newEST + Math.round(childNode.getRunTime())));
                    updateChildrenEST(childNode);
                }
            }
        }
    }

    void updateNeighboures(CloudAcoProblemNode cloudAcoProblemNode) {
        for (Map.Entry<WorkflowNode, List<CloudAcoProblemNode>> nodeListMap : this.neighbours.entrySet()) {
            List<CloudAcoProblemNode> nodeList = nodeListMap.getValue();
            for (int i = 0; i < nodeList.size(); i++) {
                CloudAcoProblemNode problemNode = nodeList.get(i);
                if (problemNode.getNode().getId().equals(cloudAcoProblemNode.getNode().getId())) {
                    nodeList.remove(i);
                    i--;
                }
            }

        }
    }


}
