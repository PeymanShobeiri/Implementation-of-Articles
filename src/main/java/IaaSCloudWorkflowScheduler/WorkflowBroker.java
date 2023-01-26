package IaaSCloudWorkflowScheduler;


import DAG.Adag;
import DAG.DagUtils;

public class WorkflowBroker {
    final int interval = 3600;
    final long bandwidth = Constants.BANDWIDTH;
    WorkflowGraph graph;
    ResourceSet resources;
    WorkflowPolicy policy;


    public WorkflowBroker(String wfDescFile, ScheduleType type) throws Exception {
        Adag dag = null;
        try {
            dag = DagUtils.readWorkflowDescription(wfDescFile);
        } catch (Throwable e) {
            System.out.println("Error reading Workflow File " + e);
        }
        graph = new WorkflowGraph();
        graph.convertDagToWorkflowGraph(dag);
        createResourceList();

        if (type == ScheduleType.Fastest)
            policy = new FastestPolicy(graph, resources, bandwidth);
        else if (type == ScheduleType.Cheapest)
            policy = new CheapestPolicy(graph, resources, bandwidth);
        else if (type == ScheduleType.IC_PCP)
            policy = new PcpPolicyInsertion(graph, resources, bandwidth);
        else if (type == ScheduleType.IC_PCPD2)
            policy = new PcpD2Policy(graph, resources, bandwidth);
        else if (type == ScheduleType.List)
            policy = new ListPolicy2(graph, resources, bandwidth);
        else if (type == ScheduleType.IC_PCP2)
            policy = new PcpPolicy2(graph, resources, bandwidth);
        else if (type == ScheduleType.IC_PCPD2_2)
            policy = new PcpD2Policy2(graph, resources, bandwidth);
        else if (type == ScheduleType.List2)
            policy = new ListPolicy3(graph, resources, bandwidth);
        else if (type == ScheduleType.IC_Loss)
            policy = new ICLossPolicy2(graph, resources, bandwidth);

    }

    public float schedule(int startTime, int deadline) {
        return (policy.schedule(startTime, deadline));
    }

    private void createResourceList() {
        resources = new ResourceSet(interval);

        resources.addResource(new Resource(0, 20, 100));
        resources.addResource(new Resource(1, (float) 16.2, 90));
        resources.addResource(new Resource(2, (float) 12.8, 80));
        resources.addResource(new Resource(3, (float) 9.8, 70));
        resources.addResource(new Resource(4, (float) 7.2, 60));
        resources.addResource(new Resource(5, 5, 50));
        resources.addResource(new Resource(6, (float) 3.2, 40));
        resources.addResource(new Resource(7, (float) 1.8, 30));
        resources.addResource(new Resource(8, (float) 1.25, 25));
        resources.addResource(new Resource(9, (float) 0.8, 20));
        resources.sort();
    }

    public WorkflowGraph getGraph() {
        return graph;
    }

    public void setGraph(WorkflowGraph graph) {
        this.graph = graph;
    }

    public ResourceSet getResources() {
        return resources;
    }

    public void setResources(ResourceSet resources) {
        this.resources = resources;
    }

    public WorkflowPolicy getPolicy() {
        return policy;
    }

    public void setPolicy(WorkflowPolicy policy) {
        this.policy = policy;
    }

    public int getInterval() {
        return interval;
    }

    public long getBandwidth() {
        return bandwidth;
    }


}
