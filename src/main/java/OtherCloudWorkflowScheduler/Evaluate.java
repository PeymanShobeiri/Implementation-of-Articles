package OtherCloudWorkflowScheduler;

import OtherCloudWorkflowScheduler.methods.Benchmarks;
import OtherCloudWorkflowScheduler.methods.PSO;
import OtherCloudWorkflowScheduler.methods.Scheduler;
import OtherCloudWorkflowScheduler.setting.Solution;
import OtherCloudWorkflowScheduler.setting.Workflow;

import java.math.RoundingMode;

public class Evaluate {
    //because in Java float numbers can not be precisely stored, a very small number E is added before testing whether deadline is met
    public static final double E = 0.0000001;
    static final String WORKFLOW_LOCATION = "WfDescFiles/Montage_25.xml";
    //deadline factor.    tight, 0.005:0.005:0.05; loose, 0.05:0.05:0.5
    private static final double DF_START = 0.005, DF_INCR = 0.005, DF_END = 0.05;
    private static final int FILE_INDEX_MAX = 1;
    private static final int[] SIZES = {25};        //50, 100, 500
    private static final Scheduler[] METHODS = {new PSO(100)};
    //"GENOME", "CYBERSHAKE", "LIGO", "MONTAGE"    floodplain
    private static final String[] WORKFLOWS = {"MONTAGE"};
    //	static final String OUTPUT_LOCATION = "./Output";
    private static final java.text.DecimalFormat df = new java.text.DecimalFormat("0.000");

    static {
        df.setRoundingMode(RoundingMode.HALF_UP);
    }

    public static void main(String[] args) {
        int deadlineNum = (int) ((DF_END - DF_START) / DF_INCR + 1);

        for (String workflow : WORKFLOWS) {
            //three dimensions of these two arrays correspond to deadlines, methods, files, respectively
            double[][][] successResult = new double[deadlineNum][METHODS.length][FILE_INDEX_MAX * SIZES.length];
            double[][][] NCResult = new double[deadlineNum][METHODS.length][FILE_INDEX_MAX * SIZES.length];

            //store cost and time of fastSchedule and cheapSchedule
            double[] refValues = new double[4];

            for (int di = 0; di <= (DF_END - DF_START) / DF_INCR; di++) {
                for (int si = 0; si < SIZES.length; si++) {
                    int size = SIZES[si];
                    for (int fi = 0; fi < FILE_INDEX_MAX; fi++) {
                        test(WORKFLOW_LOCATION, di, fi, si, successResult, NCResult, refValues);
                    }
                }
            }
        }
    }

    private static void test(String file, int di, int fi, int si, double[][][] successResult,
                             double[][][] NCResult, double[] refValues) {
        Workflow wf = new Workflow(ClassLoader.getSystemResource(file).getFile());
        Benchmarks benSched = new Benchmarks(wf);
        System.out.println("Benchmark-FastSchedule:" + benSched.getFastSchedule());
        System.out.println("Benchmark-CheapSchedule:" + benSched.getCheapSchedule());

        double deadlineFactor = DF_START + DF_INCR * di;
//        double deadline = Math.ceil(Math.ceil(benSched.getFastSchedule().calcMakespan()) * 1.5);
        double deadline = 71;
        for (int mi = 0; mi < METHODS.length; mi++) {        //method index
            Scheduler method = METHODS[mi];
            wf.setDeadline(71);
            System.out.println("The current algorithm: " + method.getClass().getCanonicalName());

            Solution sol = method.schedule(wf);
            if (sol == null)
                continue;
            int isSatisfied = sol.calcMakespan() <= deadline + E ? 1 : 0;
            if (sol.validate(wf) == false)
                throw new RuntimeException();
            System.out.println(sol);
            successResult[di][mi][fi + si * FILE_INDEX_MAX] += isSatisfied;
            NCResult[di][mi][fi + si * FILE_INDEX_MAX] += sol.calcCost() / benSched.getCheapSchedule().calcCost();
            System.out.println("loop finish ***********@@@@@@@@@@@@@@*********");
        }
        refValues[0] += benSched.getFastSchedule().calcCost();
        refValues[1] += benSched.getFastSchedule().calcMakespan();
        refValues[2] += benSched.getCheapSchedule().calcCost();
        refValues[3] += benSched.getCheapSchedule().calcMakespan();
    }
}