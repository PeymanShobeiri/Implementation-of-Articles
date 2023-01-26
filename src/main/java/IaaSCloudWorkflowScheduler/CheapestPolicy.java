package IaaSCloudWorkflowScheduler;

public class CheapestPolicy extends WorkflowPolicy {
    public CheapestPolicy(WorkflowGraph g, ResourceSet rs, long bw) {
        super(g, rs, bw);
    }

    public float schedule(int startTime, int deadline) {
        int minMIPS = resources.getMinMIPS();
        float minCost = resources.getMinCost(), totalCost;
        long totalTime = 0;

        for (WorkflowNode curNode : graph.getNodes().values())
            totalTime += Math.round((float) curNode.getInstructionSize() / minMIPS);

        totalCost = (float) (Math.ceil((double) totalTime / (double) resources.getInterval()) * minCost);
        graph.getNodes().get(graph.getEndId()).setAST((int) totalTime);
        return (totalCost);
    }
}
