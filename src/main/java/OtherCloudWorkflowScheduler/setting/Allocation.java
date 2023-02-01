package OtherCloudWorkflowScheduler.setting;
import java.lang.Math;

public class Allocation {

    private Task task;
    private VM vm;
    private double startTime;
    private double finishTime;

    public Allocation(VM vm, Task task, double startTime) {
        this.vm = vm;
        this.task = task;
        this.startTime = startTime;
        if (vm != null && task != null)
            this.finishTime = startTime + Math.abs(task.getInstructionSize() / vm.getMIPS());
        if (this.finishTime < this.startTime)
            System.out.println("this is a sheit show");
    }

    //-------------------------------------only for ICPCP---------------------------
    public Allocation(int vmId, Task task, double startTime) {
        this.vm = null;
        this.task = task;
        this.startTime = startTime;
        this.finishTime = startTime + (task.getInstructionSize() / (VM.SPEEDS[vmId] * 100.0f));
    }

    //-------------------------------------getters & setters--------------------------------
    public VM getVM() {
        return vm;
    }

    public void setVM(VM vm) {
        this.vm = vm;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public double getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(double finishTime) {
        this.finishTime = finishTime;
    }

    //-------------------------------------overrides--------------------------------
    public String toString() {
        return "Allocation [task=" + task + ", startTime="
                + startTime + ", finishTime=" + finishTime + "]";
    }
}