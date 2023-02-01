package OtherCloudWorkflowScheduler.setting;

import IaaSCloudWorkflowScheduler.Constants;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Task {
    private static int internalId = 0;
    private int id;
    private String name;
    private double runtime;
    private int instructionSize; //in MI
    //adjacent list to store workflow graph
    private List<Edge> outEdges = new ArrayList<Edge>();
    private List<Edge> inEdges = new ArrayList<Edge>();
    private int topoCount = 0;//used for topological sort
    private double bLevel;//blevel
    private double tLevel;//tLevel
    private double sLevel;
    private double ALAP;
    private double pURank; //Probabilistic Upward  Rank
    //---------------------task properties used in ICPCP algorithm---------------------------
    private double EST = -1, EFT = -1, LFT = -1, AST = -1, AFT = -1;  //'-1' means the value has not been set
    private Task criticalParent;
    private boolean isAssigned = false;

    public Task(String name, double runtime) {
        this.id = internalId++;
        this.name = name;
        this.runtime = runtime;
        this.instructionSize = (int) (Math.round(runtime) * Constants.STANDARD_MIPS);
    }

    static void resetInternalId() {        //invoked by the constructor of Workflow
        internalId = 0;
    }

    //-------------------------------------getters&setters--------------------------------
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getbLevel() {
        return bLevel;
    }

    public void setbLevel(double bLevel) {
        this.bLevel = bLevel;
    }

    public double gettLevel() {
        return tLevel;
    }

    public void settLevel(double tLevel) {
        this.tLevel = tLevel;
    }

    public double getsLevel() {
        return sLevel;
    }

    public void setsLevel(double sLevel) {
        this.sLevel = sLevel;
    }

    public double getALAP() {
        return ALAP;
    }

    public void setALAP(double aLAP) {
        ALAP = aLAP;
    }

    public List<Edge> getOutEdges() {
        return outEdges;
    }

    public List<Edge> getInEdges() {
        return inEdges;
    }

    public void insertInEdge(Edge e) {
        if (e.getDestination() != this)
            throw new RuntimeException();
        inEdges.add(e);
    }

    public void insertOutEdge(Edge e) {
        if (e.getSource() != this)
            throw new RuntimeException();
        outEdges.add(e);
    }


    public int getInstructionSize() {
        return instructionSize;
    }

    public void setInstructionSize(int instructionSize) {
        this.instructionSize = instructionSize;
    }

    public int getTopoCount() {
        return topoCount;
    }

    public void setTopoCount(int topoCount) {
        this.topoCount = topoCount;
    }

    public double getpURank() {
        return pURank;
    }

    public void setpURank(double pURank) {
        this.pURank = pURank;
    }

    //-------------------------------------overrides--------------------------------
    public String toString() {
        return "Task [id=" + name + ", runtime=" + runtime + "]";
    }

    public double getEST() {
        return EST;
    }

    public void setEST(double eST) {
        EST = eST;
    }

    public double getEFT() {
        return EFT;
    }

    public void setEFT(double eFT) {
        EFT = eFT;
    }

    public double getLFT() {
        return LFT;
    }

    public void setLFT(double lFT) {
        LFT = lFT;
    }

    public Task getCriticalParent() {
        return criticalParent;
    }

    public void setCriticalParent(Task criticalParent) {
        this.criticalParent = criticalParent;
    }

    public boolean isAssigned() {
        return isAssigned;
    }

    public void setAssigned(boolean isAssigned) {
        this.isAssigned = isAssigned;
    }

    public double getAST() {
        return AST;
    }

    public void setAST(double aST) {
        AST = aST;
    }

    public double getAFT() {
        return AFT;
    }

    public void setAFT(double aFT) {
        AFT = aFT;
    }

    //-------------------------------------comparators--------------------------------
    public static class BLevelComparator implements Comparator<Task> {
        public int compare(Task o1, Task o2) {
            // to keep entry node ranking last, and exit node first
            if (o1.getName().equals("entry") || o2.getName().equals("exit"))
                return 1;
            if (o1.getName().equals("exit") || o2.getName().equals("entry"))
                return -1;
            if (o1.getbLevel() > o2.getbLevel())
                return 1;
            else if (o1.getbLevel() < o2.getbLevel())
                return -1;
            else {
                return 0;
            }
        }
    }

    public static class PURankComparator implements Comparator<Task> {
        public int compare(Task o1, Task o2) {
            // to keep entry node ranking last, and exit node first
            if (o1.getName().equals("entry") || o2.getName().equals("exit"))
                return 1;
            if (o1.getName().equals("exit") || o2.getName().equals("entry"))
                return -1;
            if (o1.getpURank() > o2.getpURank())
                return 1;
            else if (o1.getpURank() < o2.getpURank())
                return -1;
            else {
                return 0;
            }
        }
    }

    public static class TLevelComparator implements Comparator<Task> {
        public int compare(Task o1, Task o2) {
            if (o1.getName().equals("entry") || o2.getName().equals("exit"))
                return -1;
            if (o1.getName().equals("exit") || o2.getName().equals("entry"))
                return 1;
            if (o1.gettLevel() > o2.gettLevel())
                return 1;
            else if (o1.gettLevel() < o2.gettLevel())
                return -1;
            else {
                return 0;
            }
        }
    }

    // used to calculate the largest number of parallel tasks in workflow
    public static class ParallelComparator implements Comparator<Task> {
        public int compare(Task o1, Task o2) {
            int d1 = o1.getOutEdges().size() - o1.getInEdges().size();
            int d2 = o2.getOutEdges().size() - o2.getInEdges().size();
            if (d1 > d2)                // because of the use of PriorityQueue, here the comparison is reverse
                return -1;
            else if (d1 < d2)
                return 1;
            else
                return 0;
        }
    }
}