package IaaSCloudWorkflowScheduler.aco;

import IaaSCloudWorkflowScheduler.Resource;
import IaaSCloudWorkflowScheduler.WorkflowNode;
//import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CloudAcoProblemNode {

    public double h;
    boolean setByRW;
    private WorkflowNode node;
    private WorkflowNode defaultNode;
    private CloudAcoResourceInstance resource;
    private boolean visited = false;
    private int id;

    public CloudAcoProblemNode(WorkflowNode node, CloudAcoResourceInstance resource, int id) {
        super();
        this.node = node;
        this.resource = resource;
        this.id = id;
        this.defaultNode = new WorkflowNode(node);
    }

    public CloudAcoProblemNode() {
        this.id = -1;
        this.node = new WorkflowNode("first");
        this.defaultNode = new WorkflowNode("first");
        this.resource = new CloudAcoResourceInstance(new Resource(-1, 1, 1));
    }

    void resetNode() {
        this.node.setRunTime(this.defaultNode.getRunTime());
        this.node.setAFT(this.defaultNode.getAFT());
        this.node.setAST(this.defaultNode.getAST());
        this.node.setEST(this.defaultNode.getEST());
        this.node.setEFT(this.defaultNode.getEFT());
        this.node.setLFT(this.defaultNode.getLFT());
        this.node.setLST(this.defaultNode.getLST());
        this.node.setUnscheduled();
        this.setUnvisited();
    }

    public WorkflowNode getNode() {
        return node;
    }

    public void setNode(WorkflowNode node) {
        this.node = node;
    }

//    @NotNull
    public CloudAcoResourceInstance getResource() {
        return resource;
    }


    public void setResource(CloudAcoResourceInstance resource) {
        this.resource = resource;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(CloudAcoEnvironment environment) {
        this.getNode().setScheduled();
        this.visited = true;
//        environment.getProblemGraph().updateNeighboures(this);
        List<CloudAcoProblemNode> problemNodes = environment.getProblemGraph().getProblemNodeList();
        for (CloudAcoProblemNode node : problemNodes) {
            if (node.getNode().getId().equals(this.getNode().getId())) {
                node.setVisited();
            }
        }


    }

    private void setVisited() {
        this.getNode().setScheduled();
        this.visited = true;
    }

    void setUnvisited() {
        this.visited = false;
        this.getNode().setUnscheduled();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getting the neighbor of every node.
     * In this method we check the possible movement for ant.
     */

    @SuppressWarnings("ALL")
    public List<CloudAcoProblemNode> getNeighbourhood(CloudAcoEnvironment environment) {
        List<CloudAcoProblemNode> finalNeighbours = new ArrayList<>();

        if (environment.getProblemGraph().getNeighbours(this.getNode()) == null) {
            finalNeighbours.add(environment.getProblemGraph().getStart());
            return finalNeighbours;
        } else {
            return environment.getProblemGraph().getNeighbours(this.getNode());
        }

        //TODO remove
        /*List<CloudAcoProblemNode> finalNeighbours = new ArrayList<>();
        List<CloudAcoProblemNode> neighbours = environment.getProblemGraph().getNeighbours(this.getNode());


        if(this.getId()==-1){
            for (CloudAcoProblemNode problemNode: environment.getProblemGraph().getProblemNodeList()){
                if (problemNode.getNode().getId().equals("start")){
                    finalNeighbours.add(problemNode);
                    return finalNeighbours;
                }
            }
        }
        finalNeighbours.add(environment.getProblemGraph().getStart());

        int visited = 0;
        for (int i = 0; i < neighbours.size(); i++) {
            visited = 0;
            if (!neighbours.get(i).visited) {
                if (neighbours.get(i).getNode().getParents().size() == 0) {
                    visited = 1;
                } else {
                    for (Link parent : neighbours.get(i).getNode().getParents()) {
                        WorkflowNode parentNode = environment.getProblemGraph().getGraph().getNodes().get(parent.getId());
                        if (!parentNode.isScheduled()) {
                            visited = 2;
                            break;
                        }
                    }
                    if (visited == 0)
                        visited = 1;
                }
            }

            if (visited == 1)
                finalNeighbours.add(neighbours.get(i));
        }

        // System.out.println("node: "+ node.getId() +"\tn: "+neighbours.size() + " fn: "+finalNeighbours.size());

        return finalNeighbours;*/

    }

    @Override
    public String toString() {
        return
                "id:" + this.getId()
                        + "node:" + this.getNode().getId()
                        + " resource: " + this.getResource().getId()
                        + " instance: " + this.getResource().getInstanceId()
                        + " EST : " + this.getNode().getEST()
                        + " AST : " + this.getNode().getAST()
                        + " AFT : " + this.getNode().getAFT()
                        + " LFT : " + this.getNode().getLFT()
                        + " DL : " + this.getNode().getDeadline()
                        + " H : " + this.h
                        + " setByRW : " + this.setByRW;
    }
}
