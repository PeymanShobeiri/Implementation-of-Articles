package OtherCloudWorkflowScheduler.methods;


import OtherCloudWorkflowScheduler.setting.Solution;
import OtherCloudWorkflowScheduler.setting.Workflow;

public interface Scheduler {
    Solution schedule(Workflow wf);
}
