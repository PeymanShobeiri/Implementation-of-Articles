package IaaSCloudWorkflowScheduler;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class FastestPolicy extends WorkflowPolicy {
    public FastestPolicy(WorkflowGraph g, ResourceSet rs, long bw) {
        super(g, rs, bw);
    }

    public float schedule(int startTime, int deadline) {
        Queue<String> candidateNodes = new LinkedList<String>();
        Map<String, WorkflowNode> nodes = graph.getNodes();
        WorkflowNode curNode, parentNode, childNode;


        setRuntimes();

        curNode = nodes.get(graph.getStartId());
        curNode.setAFT(startTime);
        curNode.setScheduled();
        for (Link child : curNode.getChildren())
            candidateNodes.add(child.getId());

        while (!candidateNodes.isEmpty()) {
            int thisTime, maxTime;
            curNode = nodes.get(candidateNodes.remove());
            maxTime = -1;
            for (Link parent : curNode.getParents()) {
                parentNode = nodes.get(parent.getId());
                thisTime = parentNode.getAFT(); //+ Math.round((float)parent.getDataSize() / bandwidth);
                if (thisTime > maxTime)
                    maxTime = thisTime;
            }
            curNode.setAST(maxTime);
            curNode.setAFT((int) (maxTime + Math.round(curNode.getRunTime())));
            curNode.setScheduled();
            curNode.setSelectedResource(0);

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

        super.setEndNodeAST();
        return (0);
    }
}
