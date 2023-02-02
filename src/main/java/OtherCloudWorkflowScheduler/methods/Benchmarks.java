package OtherCloudWorkflowScheduler.methods;

import OtherCloudWorkflowScheduler.setting.Solution;
import OtherCloudWorkflowScheduler.setting.Task;
import OtherCloudWorkflowScheduler.setting.VM;
import OtherCloudWorkflowScheduler.setting.Workflow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Benchmarks {

    private Solution cheapSchedule, fastSchedule;

    public Benchmarks(Workflow wf) {
        fastSchedule = bLevelEST(wf);        //VM�̶�ΪFASTEST�ҿ�������������ȡ���Ƶ����ʱ��
        cheapSchedule = slowestVMEST(wf);    //uses one slowest VM
    }

    // in one slowest VM, use EST to allocate tasks
    private Solution slowestVMEST(Workflow wf) {
        Solution solution = new Solution();

        VM vm = new VM(VM.SLOWEST);
        for (Task task : wf) {
            double EST = solution.calcEST(task, vm);
            solution.addTaskToVM(vm, task, EST, true);
        }
        return solution;
    }

    //list scheduling based on bLevel and EST; a kind of HEFT
    private Solution bLevelEST(Workflow wf) {
        Solution solution = new Solution();

        List<Task> tasks = new ArrayList<Task>(wf);
        Collections.sort(tasks, new Task.BLevelComparator());    //sort based on bLevel
        Collections.reverse(tasks);    // larger first

        for (Task task : tasks) {                //select VM based on EST
            double minEST = Double.MAX_VALUE;
            VM selectedVM = null;
            for (VM vm : solution.keySet()) {                // calculate EST of task on all the used VMs
                double EST = solution.calcEST(task, vm);
                if (EST < minEST) {
                    minEST = EST;
                    selectedVM = vm;
                }
            }
            //������ʹ����Ҫ��չ�ĵ㣺��ʱ������VM
            double EST = solution.calcEST(task, null);    //whether minEST can be shorten if a new vm is added
            if (EST < minEST) {
                minEST = EST;
                selectedVM = new VM(VM.FASTEST);
            }
            solution.addTaskToVM(selectedVM, task, minEST, true);    //allocation
        }
        return solution;
    }

    //----------------------------getters-------------------------------------
    public Solution getCheapSchedule() {
        return cheapSchedule;
    }

    public Solution getFastSchedule() {
        return fastSchedule;
    }
}