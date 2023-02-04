package OtherCloudWorkflowScheduler;

import IaaSCloudWorkflowScheduler.PcpPolicy;
import OtherCloudWorkflowScheduler.methods.*;
import OtherCloudWorkflowScheduler.setting.Workflow;

public class EvaluateRuntime {
    private static final double DEADLINE_FACTOR = 0.2;
    private static final int FILE_INDEX_MAX = 1;  //10
    private static final String[] WORKFLOWS = {"MONTAGE"};//

    private static final Scheduler[] METHODS = {new PSO(100)}; //new PSO(),  new LACO(),
    private static final int FILE_SIZE_MAX = 2;  //10

    public static void main(String[] args) {

        double sum = 0;
        String file = "WfDescFiles/Inspiral_1000.xml";
        double x = 10000.0f;
        Workflow wf = new Workflow(ClassLoader.getSystemResource(file).getFile());
        wf.setDeadline(4795);
        for(int i=0;i<7;i++) {
            System.out.println("evalouation num :  " + i);
//            ICPCP pp = new ICPCP();
//            System.out.println(pp.schedule(wf));
//            sum += pp.schedule(wf).calcCost();
////
//            LACO tmp = new LACO(200);
//            sum += tmp.schedule(wf).calcCost();

              PSO ps = new PSO(26500);
              sum += ps.schedule(wf).calcCost();

//            ProLiS pl = new ProLiS(2);
//            System.out.println(pl.schedule(wf));

        }
        System.out.println(" the average is : " + sum/7);
        System.out.println("min is : " + x);

//        ICPCP pp = new ICPCP();
//        sum += pp.schedule(wf).calcCost();

//        PSO ps = new PSO(200);
//        sum += ps.schedule(wf).calcCost();

//        LACO tmp = new LACO(50);
//        sum += tmp.schedule(wf).calcCost();




        System.out.println("###################################");






    }
}
