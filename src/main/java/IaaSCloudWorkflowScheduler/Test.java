package IaaSCloudWorkflowScheduler;

import IaaSCloudWorkflowScheduler.aco.CloudAcoEnvironment;
import IaaSCloudWorkflowScheduler.aco.CloudAcoProblemRepresentation;
import OtherCloudWorkflowScheduler.methods.LACO;
import OtherCloudWorkflowScheduler.methods.PSO;
import OtherCloudWorkflowScheduler.methods.ProLiS;
import OtherCloudWorkflowScheduler.methods.Scheduler;
import OtherCloudWorkflowScheduler.setting.Solution;
import OtherCloudWorkflowScheduler.setting.Workflow;
import IaaSCloudWorkflowScheduler.PcpPolicy;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;

@SuppressWarnings("ALL")
public class Test {
    public static void main(String[] args) {
        scheduleWorkflow();

//        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
//            public void run() {
////                CloudAcoWorkflow.printSolution();
//            }
//        }));
    }

    @SuppressWarnings("static-access")
    private static void scheduleWorkflow() {
        String workflowPath = "WfDescFiles/Epigenomics_997.xml";
        int startTime = 0, deadline = 71, finishTime, MH, MC;
        float cost, CC, CH;
        WorkflowBroker wb = null;
        PrintWriter out = null;
        long realStartTime, realFinishTime;
        try {
                wb = new WorkflowBroker(workflowPath, ScheduleType.IC_PCP);
            } catch (Exception e) {
                e.printStackTrace();
            }
        cost = wb.schedule(startTime, deadline);
        System.out.println( "cost of this algorithm is : "  + cost );


//        MH = computeFastest(workflowPath, startTime, deadline);
//        MC = computeCheapest(workflowPath, startTime, deadline);

//        for (float alpha = (float) 1.5; alpha <= 5; alpha += 1) {
//
//            deadline = Math.round(alpha * MH);
//
//            try {
//                wb = new WorkflowBroker(workflowPath, ScheduleType.IC_PCPD2);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            realStartTime = System.currentTimeMillis();
//            cost = wb.schedule(startTime, deadline);
//            wb.getPolicy().computeESTandEFT(startTime);
//            wb.getPolicy().computeLSTandLFT(deadline);
//            realFinishTime = System.currentTimeMillis();
//            realFinishTime -= realStartTime;
//            finishTime = wb.graph.getNodes().get(wb.graph.getEndId()).getEST();
//
//            String message = "\n\nICPC finishT < deadline: "
//                    + (finishTime < deadline)
//                    + "\n" + "deadline : " + deadline
//                    + "\t\tcost of icpc: " + cost + "\n" +
//                    "solution: \n" +
//                    wb.getPolicy().solutionAsString();
//
//            System.out.println(message);
//            try {
//                wb = new WorkflowBroker(workflowPath, ScheduleType.IC_PCPD2_2);
//                wb.getPolicy().computeESTandEFT(startTime);
//                wb.getPolicy().computeLSTandLFT(deadline);
//                ((PcpD2Policy2) wb.getPolicy()).distributeDeadline();
//
//
//                //PSO
//
//                Solution lacoSOL = null;
//                Scheduler[] methods = new Scheduler[]{new PSO(100), new LACO(100), new ProLiS(2)};
//                for (Scheduler scheduler : methods) {
//                    Workflow wf = new Workflow(ClassLoader.getSystemResource(workflowPath).getFile());
//                    wf.setDeadline(deadline);
//                    System.out.printf("\n\n=============" + scheduler.getClass().getCanonicalName() + "==================\n\n");
//
//                    System.out.println("The current algorithm: " + scheduler.getClass().getCanonicalName());
//
//                    Solution sol = scheduler.schedule(wf);
//                    if (sol == null)
//                        continue;
//                    if (scheduler.getClass().equals(LACO.class))
//                        lacoSOL = sol;
////                    int isSatisfied = sol.calcMakespan() <= deadline + PSO.E ? 1 : 0;
//                    if (sol.validate(wf) == false)
//                        throw new RuntimeException();
//                    System.out.println(sol);
//                }
//
//
//                System.out.println("==================================MY_ACO");
//
//                CloudAcoProblemRepresentation problemRepresentation = new CloudAcoProblemRepresentation(wb.graph, wb.resources, Constants.BANDWIDTH, deadline, 10);
//                CloudAcoEnvironment environment = new CloudAcoEnvironment(problemRepresentation);
//
//                CloudACO cloudACO = new CloudACO();
//
//                cloudACO.schedule(environment, deadline);
//
//                System.out.println("==================================MY_ACO");
//
//
////                CloudAcoWorkflow.OptimiseWorkFlow(wb.graph, wb.resources, Constants.BANDWIDTH, deadline)
////                        .setLacoSort(lacoSOL.getSortedTask()
////                                .stream()
////                                .map(t -> t.getName())
////                                .collect(Collectors.toList()))
////                        .solve();
//
//                return;
//            } catch (Exception e) {
//                System.out.println("EEEEEException");
//                e.printStackTrace();
//                return;
//            }
//        }
    }

    private static int computeCheapest(String wfFile, int startTime, int deadline) {
        int MC = 0;
        try {
            WorkflowBroker wb = new WorkflowBroker(wfFile, ScheduleType.Cheapest);
            float CC = wb.schedule(startTime, deadline);
            MC = wb.graph.getNodes().get(wb.graph.getEndId()).getAST();
            wb.getPolicy().computeESTandEFT(startTime);
            wb.getPolicy().computeLSTandLFT(deadline);
            System.out.println("Cheapest: cost=" + CC + " time=" + MC);

        } catch (Exception e) {
            System.out.println("Error in creating workflow broker!!!" + e.getLocalizedMessage());
        }
        return MC;
    }

    private static int computeFastest(String WfFile, int startTime, int deadline) {
        int MH = 0;
        try {
            WorkflowBroker wb = new WorkflowBroker(WfFile, ScheduleType.Fastest);
            float CH = wb.schedule(startTime, deadline);
            wb.getPolicy().computeESTandEFT(startTime);
            wb.getPolicy().computeLSTandLFT(deadline);
            MH = wb.graph.getNodes().get(wb.graph.getEndId()).getAST();
            System.out.println("Fastest: cost=" + CH + " time=" + MH);
        } catch (Exception e) {
            System.out.println("Error in creating workflow broker!!!" + e.getLocalizedMessage());
        }
        return MH;
    }

    private static void printWorkflow(WorkflowGraph g) {
        PriorityQueue<WorkflowNode> queue = new PriorityQueue<WorkflowNode>(g.nodes.size(),
                new Comparator<WorkflowNode>() {
                    public int compare(WorkflowNode o1, WorkflowNode o2) {
                        return o1.getDeadline() >= o2.getDeadline() ? 1 : -1;
                    }
                });

        ArrayList<WorkflowNode> nodes = new ArrayList<WorkflowNode>();
        for (WorkflowNode node : g.nodes.values()) {
            if (!node.getId().equals(g.getStartId()) && !node.getId().equals(g.getEndId())) {
                queue.add(node);
                nodes.add(node);
            }
        }

        Collections.sort(nodes, new WorkflowPolicy.UpRankComparator());
        System.out.println("===========UpRankComparator=========");
        for (int i = 0; i < nodes.size(); i++) {
            WorkflowNode n = nodes.get(i);
            if (n.getRunTime() > 0)
                System.out.println("Id=" + n.getId() + " RT=" + n.getRunTime() + " SR=" + n.getSelectedResource()
                        + " EST=" + n.getEST() + " AFT=" + n.getEFT() + " Deadline=" + n.getDeadline() + " AST= " + n.getAST());

        }

        System.out.println("===========ASTComparator=========");

        Collections.sort(nodes, new WorkflowPolicy.ASTComparator());
        for (int i = 0; i < nodes.size(); i++) {
            WorkflowNode n = nodes.get(i);
            if (n.getRunTime() > 0)
                System.out.println("Id=" + n.getId() + " RT=" + n.getRunTime() + " SR=" + n.getSelectedResource()
                        + " EST=" + n.getEST() + " AFT=" + n.getEFT() + " Deadline=" + n.getDeadline() + " AST= " + n.getAST());

        }

        System.out.println("================================");

        while (!queue.isEmpty()) {
            WorkflowNode n = queue.remove();
            if (n.getRunTime() > 0)
                System.out.println("Id=" + n.getId() + " RT=" + n.getRunTime() + " SR=" + n.getSelectedResource()
                        + " EST=" + n.getEST() + " AFT=" + n.getEFT() + " Deadline=" + n.getDeadline() + " AST= " + n.getAST());
        }
    }

    private static void printInstances(InstanceSet instances, WorkflowGraph g) {
        System.out.println("Instances = " + instances.getSize());
        for (int i = 0; i < instances.getSize(); i++) {
            Instance cur = instances.getInstance(i);
            System.out.println("id=" + cur.getId() + " type=" + cur.getType().getId() + " start="
                    + g.nodes.get(cur.getFirstTask()).getEST() + " end= " + g.nodes.get(cur.getLastTask()).getEFT()
                    + " number=" + cur.getTasks().size());
        }
    }

    static void temp() {
        int[][] times = {{5, 8, 12}, {8, 12, 20}, {6, 12, 15}};
        int[][] costs = {{8, 5, 3}, {12, 6, 4}, {8, 5, 3}};
        int totalTime, totalCost;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    totalTime = times[0][i] + times[1][j] + times[2][k];
                    totalCost = costs[0][i] + costs[1][j] + costs[2][k];
                    System.out.println("time= " + totalTime + " cost=" + totalCost);
                }
            }
        }
    }
}
