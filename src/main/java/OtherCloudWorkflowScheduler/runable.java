package OtherCloudWorkflowScheduler;

import OtherCloudWorkflowScheduler.methods.ICPCP;
import OtherCloudWorkflowScheduler.setting.Workflow;
import OtherCloudWorkflowScheduler.methods.Scheduler;

public class runable {
    public static void main(String[] args){
        System.out.println("hello");
        String file = "../resources/WfDescFiles/Montage_25.dax";
        Workflow wf = new Workflow(ClassLoader.getSystemResource(file).getFile());
        ICPCP tmp = new ICPCP();
        tmp.schedule(wf);
    }
}
