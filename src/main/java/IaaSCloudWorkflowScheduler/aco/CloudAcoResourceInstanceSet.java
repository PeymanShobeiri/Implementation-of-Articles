package IaaSCloudWorkflowScheduler.aco;

import IaaSCloudWorkflowScheduler.Resource;
import IaaSCloudWorkflowScheduler.ResourceSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CloudAcoResourceInstanceSet {

    private HashMap<Resource, List<CloudAcoResourceInstance>> instances;
    private int count;


    CloudAcoResourceInstanceSet(ResourceSet resources, int count) {
        this.count = count;
        initialize(resources);
    }

    private void initialize(ResourceSet resources) {
        instances = new HashMap<>();
        int id = 0;
        for (int j = 0; j < resources.getSize(); j++) {
            Resource resource = resources.getResource(j);
            ArrayList<CloudAcoResourceInstance> instances = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                instances.add(new CloudAcoResourceInstance(resource, id++));
            }
            this.instances.put(resource, instances);
        }
    }


    public HashMap<Resource, List<CloudAcoResourceInstance>> getInstances() {
        return instances;
    }

    public int getCount() {
        return count;
    }

    @SuppressWarnings("ALL")
    public int getFinishTime() {
        AtomicInteger max = new AtomicInteger(-1);
        instances.values().forEach(instances -> {
            for (CloudAcoResourceInstance instance : instances) {
                if (instance.getInstanceFinishTime() > max.get())
                    max.set((int) instance.getInstanceFinishTime());
            }
        });
        return max.get();
    }


    public void resetPerAnt() {
        instances.values().forEach(instances -> {
            for (CloudAcoResourceInstance instance : instances) {
                instance.reset();
            }
        });

    }
}
