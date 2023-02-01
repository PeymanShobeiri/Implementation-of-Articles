package OtherCloudWorkflowScheduler.setting;

import IaaSCloudWorkflowScheduler.Constants;

// virtual machine, i.e., cloud service resource
public class VM {
    public static final double LAUNCH_TIME = 0;
    public static final long NETWORK_SPEED = 20000000;

    public static final int TYPE_NO = 10;
    public static final double[] SPEEDS = {1, 0.9, 0.8, 0.7, 0.6, 0.5, 0.4, 0.3, 0.25, 0.2};
    public static final double[] MIPS = {100, 90, 80, 70, 60, 50, 40, 30, 25, 20};
    public static final double[] UNIT_COSTS = {20, 16.2, 12.8, 9.8, 7.2, 5, 3.2, 1.8, 1.25, 0.8};

    public static final double INTERVAL = 300;    //one hour, billing interval

    public static final int FASTEST = 0;
    public static final int SLOWEST = 9;

    private static int internalId = 0;
    private int id;
    private int type;

    public VM(int type) {
        this.type = type;
        this.id = internalId++;
    }

    static void resetInternalId() {    //called by the constructor of Solution
        internalId = 0;
    }

    public float getMIPS() {
        return (float) (MIPS[type]);
    }

    public double getUnitCost() {
        return UNIT_COSTS[type];
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    //------------------------getters && setters---------------------------
    void setType(int type) {        //can only be invoked in the same package, e.g., Solution
        this.type = type;
    }

    //-------------------------------------overrides--------------------------------
    public String toString() {
        return "VM [id=" + id + ", type=" + type + "]";
    }
}