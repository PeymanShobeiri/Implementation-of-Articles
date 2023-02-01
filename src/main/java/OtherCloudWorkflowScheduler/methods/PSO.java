package OtherCloudWorkflowScheduler.methods;

import OtherCloudWorkflowScheduler.setting.Solution;
import OtherCloudWorkflowScheduler.setting.Task;
import OtherCloudWorkflowScheduler.setting.VM;
import OtherCloudWorkflowScheduler.setting.Workflow;

import java.util.Random;

/*Rodriguez, Maria Alejandra, and Rajkumar Buyya. "Deadline based resource provisioning and scheduling
   algorithm for scientific workflows on clouds." IEEE Transactions on Cloud Computing 2.2 (2014): 222-235.*/
public class PSO implements Scheduler {

    public static final double wdamp = 0.99;
    private static final int POPSIZE = 100;
    private final int NO_OF_ITE;
    private static  double W = 0.5f, C1 = 2f, C2 = 2f;

    private Workflow wf;
    private int range;
    private Random rnd = new Random();

    private int dimension;
    private VM[] vmPool;

    public PSO(int itr) {
        this.NO_OF_ITE = itr;
    }

    @Override
    public Solution schedule(Workflow wf) {
        this.wf = wf;
        this.dimension = wf.size();
        this.range = wf.getMaxParallel() * VM.TYPE_NO;
        this.vmPool = new VM[range];
        for (int i = 0; i < vmPool.length; i++) {
            vmPool[i] = new VM(i / wf.getMaxParallel()); // in vmPool, VMType ascends
        }

        double xMin = 0, xMax = range - 1;    //boundary
        double vMax = 0.1*(xMax-xMin);                    //maximum velocity
        double vMin = -1 * vMax;

        double[] globalBestPos = new double[dimension];    //global Best Position
        Solution globalBestSol = null;

        Particle[] particles = new Particle[POPSIZE];
        for (int i = 0; i < POPSIZE; i++) {        //initialize particles
            particles[i] = new Particle(vMax, xMin, xMax);
            particles[i].generateSolution();

            if (globalBestSol == null || particles[i].sol.isBetterThan(globalBestSol, wf.getDeadline())) {
                for (int j = 0; j < dimension; j++)
                    globalBestPos[j] = particles[i].position[j];
                globalBestSol = particles[i].sol;
            }
        }
        System.out.println("the best initial solution:" + globalBestSol.calcCost() + ";\t" + globalBestSol.calcMakespan());

        for (int iteIndex = 0; iteIndex < NO_OF_ITE; iteIndex++) {
//
            for (int i = 0; i < POPSIZE; i++) {
                for (int j = 0; j < dimension; j++) {
                    particles[i].speed[j] = (W * particles[i].speed[j]
                            + C1 * rnd.nextDouble() * (particles[i].bestPos[j] - particles[i].position[j])
                            + C2 * rnd.nextDouble() * (globalBestPos[j] - particles[i].position[j])) ;  //ȫ�����λ����Ϊ�ھ�
                    particles[i].speed[j] = Math.max(particles[i].speed[j], vMin);
                    particles[i].speed[j] = Math.min(particles[i].speed[j], vMax);
//                    System.out.println( particles[i].speed[j]);
                    particles[i].position[j] = particles[i].position[j] + particles[i].speed[j]; // updating position


                    particles[i].position[j] = Math.max(particles[i].position[j], xMin);    //bound
                    particles[i].position[j] = Math.min(particles[i].position[j], xMax);
                }
                particles[i].generateSolution();
//                System.out.println("*************************************");
                //record a better solution
                if (globalBestSol == null || particles[i].sol.isBetterThan(globalBestSol, wf.getDeadline())) {
                    for (int j = 0; j < dimension; j++)
                        globalBestPos[j] = particles[i].position[j];
                    globalBestSol = particles[i].sol;

                    System.out.printf("Iteration index: %3d\t%5.2f\t%5.2f\n", iteIndex,
                            globalBestSol.calcCost(), globalBestSol.calcMakespan());
                }
            }
//            W = W*wdamp;
        }
        System.out.println("Globle best is :" + globalBestSol.calcCost() + ";\t" + globalBestSol.calcMakespan());
        return globalBestSol;
    }

    private class Particle {
        private double[] position = new double[dimension];
        private double[] speed = new double[dimension];
        private double[] bestPos = new double[dimension];
        private Solution sol, bestSol = null;

        //initialize a particle
        public Particle(double vMax, double xMin, double xMax) {
            for (int i = 0; i < dimension; i++) {
                this.position[i] = rnd.nextDouble() * (xMax - xMin) + xMin;
                this.speed[i] = vMax * rnd.nextDouble() - vMax / 2;
                this.bestPos[i] = this.position[i];
            }
        }

        public void generateSolution() {        //generate solution from position
            this.sol = new Solution();
            for (int i = 0; i < position.length; i++) {
//                System.out.println(i);
                Task task = wf.get(i);        // tasks in wf is a topological sort
                int vmIndex = (int) (Math.floor(position[i]));
                VM vm = vmPool[vmIndex];
                double startTime = sol.calcEST(task, vm);
                sol.addTaskToVM(vm, task, startTime, true);
            }

            //record the best solution this particle has found
            if (bestSol == null || this.sol.isBetterThan(bestSol, wf.getDeadline())) {
                for (int j = 0; j < dimension; j++)
                    this.bestPos[j] = this.position[j];
                this.bestSol = this.sol;
            }
        }

        public String toString() {
            if (sol != null)
                return "Particle [" + sol.calcCost() + ", " + sol.calcMakespan() + "]";
            return "";
        }
    }
}