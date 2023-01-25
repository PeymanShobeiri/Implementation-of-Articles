package IaaSCloudWorkflowScheduler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ResourceSet implements Serializable {
    private ArrayList<Resource> resources;
    private int maxMIPS, meanMIPS, minMIPS;
    private float maxCost, minCost;
    private int size = 0;
    private int timeInterval;

    public ResourceSet(int interval) {
        resources = new ArrayList<Resource>();
        timeInterval = interval;
    }

    public int getMaxMIPS() {
        return (maxMIPS);
    }

    public int getMinMIPS() {
        return (minMIPS);
    }

    public int getMeanMIPS() {
        return (meanMIPS);
    }

    public float getMaxCost() {
        return (maxCost);
    }

    public float getMinCost() {
        return (minCost);
    }

    public int getSize() {
        return (size);
    }

    public int getInterval() {
        return (timeInterval);
    }

    public void addResource(Resource res) {
        resources.add(res);
        size++;
    }

    public Resource getResource(int index) {
        if (index < size)
            return (resources.get(index));
        else
            return null;
    }

    public Resource getMinResource() {
        return (resources.get(size - 1));
    }

    public int getMinId() {
        return (size - 1);
    }

    public Resource getMaxResource() {
        return (resources.get(0));
    }

    public int getMaxId() {
        return (0);
    }

    public void sort() {
        Collections.sort(resources, new MIPSComparator());

        maxMIPS = resources.get(0).getMIPS();
        maxCost = resources.get(0).getCost();
        minMIPS = resources.get(size - 1).getMIPS();
        minCost = resources.get(size - 1).getCost();

        meanMIPS = 0;
        for (Resource res : resources)
            meanMIPS += res.getMIPS();
        meanMIPS /= size;
    }

    public void computeParameters() {
        maxMIPS = resources.get(0).getMIPS();
        maxCost = resources.get(0).getCost();
        minMIPS = resources.get(size - 1).getMIPS();
        minCost = resources.get(size - 1).getCost();

        meanMIPS = 0;
        for (Resource res : resources)
            meanMIPS += res.getMIPS();
        meanMIPS /= size;
    }


    private class MIPSComparator implements Comparator<Resource> {
        public int compare(Resource res1, Resource res2) {
            if (res1.getMIPS() < res2.getMIPS())
                return (1);
            else if (res1.getMIPS() > res2.getMIPS())
                return (-1);
            else
                return (0);
        }
    }
}
