package IaaSCloudWorkflowScheduler;

import java.util.ArrayList;

public class InstanceSet {
    private ResourceSet resources;
    private ArrayList<Instance> instances;
    private int size = 0;

    public InstanceSet(ResourceSet rs) {
        resources = rs;
        instances = new ArrayList<Instance>();
    }

    public void addInstance(Instance inst) {
        instances.add(inst);
        size++;
    }

    public void removeInstance(Instance inst) {
        instances.remove(inst);
        size--;
    }

    public Instance getInstance(int index) {
        if (index < size)
            return (instances.get(index));
        else
            return null;
    }

    public int getSize() {
        return (size);
    }

}
