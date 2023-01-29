package IaaSCloudWorkflowScheduler.utils;

import IaaSCloudWorkflowScheduler.WorkflowGraph;
import IaaSCloudWorkflowScheduler.WorkflowNode;

import java.util.*;

public class GraphUtils {


    public static WorkflowGraph generateTopologicalSort(WorkflowGraph wg) {
        WorkflowGraph tSortedGraph = new WorkflowGraph();
        LinkedList<WorkflowNode> sortedNode = new LinkedList<WorkflowNode>();
        for (Map.Entry<String, WorkflowNode> entry : wg.getNodes().entrySet()) {
            sortedNode.add(new WorkflowNode(entry.getValue()));
        }
        Collections.sort(sortedNode, new Comparator<WorkflowNode>() {
            public int compare(WorkflowNode o1, WorkflowNode o2) {
                if (o1.getDeadline() > o2.getDeadline())
                    return 1;
                else if (o1.getDeadline() < o2.getDeadline())
                    return -1;
                else
                    //generate random topological sort from workflow
                    return new Random().nextInt() % 2 == 0 ? 1 : -1;
            }
        });
        WorkflowNode prevNode = null;
        while (sortedNode.iterator().hasNext()) {
            WorkflowNode workflowNode = sortedNode.iterator().next();
            System.out.println("Node ID: +" + workflowNode.getId() + "\tDeadline: " + workflowNode.getDeadline());
            if (prevNode != null) {
                //   workflowNode.addParent(prevNode,);
            }
        }
        return null;
    }
}
