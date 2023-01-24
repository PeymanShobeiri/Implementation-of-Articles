package IaaSCloudWorkflowScheduler.aco;


import isula.aco.algorithms.acs.AcsConfigurationProvider;
import isula.aco.algorithms.maxmin.MaxMinConfigurationProvider;
import isula.aco.exception.ConfigurationException;

public class CloudAcoConfigurationProvider implements MaxMinConfigurationProvider, AcsConfigurationProvider {

    private int antNumber = 300;
    private double evaporationRatio = 0.1;
    private double heuristicRatio = 0.6;
    private double pheromoneRatio = 0.4;
    private int iteration = 200;
    private double Q0 = 0.9;


    private CloudAcoEnvironment environment;
    private double initialPheromone = Float.MIN_VALUE;

    public CloudAcoConfigurationProvider() {
        this.initialPheromone = Math.pow(getMinCost() / getMaxCost(), 2);
        //this.initialPheromone = 0.0001;
    }

    public int getNumberOfAnts() {
        return antNumber;
    }

    public double getEvaporationRatio() {
        return evaporationRatio;
    }

    public int getNumberOfIterations() {
        return iteration;
    }

    public double getInitialPheromoneValue() {
        return this.initialPheromone;
    }

    public double getMinCost() {
        return 50.0;
    }

    public double getMaxCost() {
        return 15000;
    }

    public double getHeuristicImportance() {
        return heuristicRatio;
    }

    public double getPheromoneImportance() {
        return pheromoneRatio;
    }

    public CloudAcoEnvironment getEnvironment() {
        return environment;
    }

    public void setEnvironment(CloudAcoEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public double getBestChoiceProbability() {
        return Q0;
    }


    @Override
    public double getMaximumPheromoneValue() {
        throw new ConfigurationException(
                "We don't use this parameter in this version of the Algorithm");
    }

    @Override
    public double getMinimumPheromoneValue() {
        throw new ConfigurationException(
                "We don't use this parameter in this version of the Algorithm");
    }
}
