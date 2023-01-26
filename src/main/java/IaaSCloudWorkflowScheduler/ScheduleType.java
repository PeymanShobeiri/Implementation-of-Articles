package IaaSCloudWorkflowScheduler;


public enum ScheduleType {
    Fastest("Fastest"), Cheapest("Cheapest"), IC_PCP("IC-PCP"), IC_PCPD2("IC-PCPD2"), List("List"), IC_PCP2("IC-PCP2"),
    IC_PCPD2_2("IC-PCPD2-2"), List2("List2"), IC_Loss("IC-Loss");
    private final String value;

    private ScheduleType(String value) {
        this.value = value;
    }

    public static ScheduleType convert(String val) {
        for (ScheduleType inst : values()) {
            if (inst.toString().equals(val)) {
                return inst;
            }
        }
        return null;
    }

    public String toString() {
        return value;
    }
}
