package IaaSCloudWorkflowScheduler.aco;

import isula.aco.Ant;
import isula.aco.AntColony;
import isula.aco.ConfigurationProvider;
import isula.aco.exception.ConfigurationException;

import java.util.Iterator;

public class CloudAcoAntColony extends AntColony<CloudAcoProblemNode, CloudAcoEnvironment> {

    private String solution = "NOT_FOUND";
    private double solutionCost = Double.MAX_VALUE;

    CloudAcoAntColony(int numberOfAnts) {
        super(numberOfAnts);
    }

    @Override
    protected Ant<CloudAcoProblemNode, CloudAcoEnvironment> createAnt(CloudAcoEnvironment cloudAcoEnvironment) {
        return new CloudAcoAntForWorkflow(cloudAcoEnvironment);
    }

    @Override
    public Ant<CloudAcoProblemNode, CloudAcoEnvironment> getBestPerformingAnt(CloudAcoEnvironment environment) {
        CloudAcoAntForWorkflow bestAnt = (CloudAcoAntForWorkflow) getHive().get(0);
        Iterator var3 = getHive().iterator();
        double bestCost = Double.MAX_VALUE;
        while (var3.hasNext()) {
            CloudAcoAntForWorkflow ant = (CloudAcoAntForWorkflow) var3.next();
            if ((ant.getLastSolutionCost() < bestCost) && ant.isValidAnswer()) {
                bestAnt = ant;
                bestCost = ant.getLastSolutionCost();
            }
        }

        return bestAnt;
    }

    @Override
    public void buildSolutions(CloudAcoEnvironment environment, ConfigurationProvider configurationProvider) {
        if (getHive().isEmpty()) {
            throw new ConfigurationException("Your colony is empty: You have no ants to solve the problem. Have you called the buildColony() method?. Number of ants from configuration provider: " + configurationProvider.getNumberOfAnts());
        } else {
            for (Ant<CloudAcoProblemNode, CloudAcoEnvironment> ant : this.getHive()) {
                boolean failed = false;

                while (!ant.isSolutionReady(environment)) {
                    try {
                        ant.selectNextNode(environment, configurationProvider);
                    } catch (Exception e) {
                        //System.out.println("Colony: ");
                        //e.printStackTrace();
                        failed = true;
                        break;
                    }
                }
                if (!failed) {
                    ((CloudAcoAntForWorkflow) ant).setValidAnswer(true);
                    double antCost = ant.getSolutionCost(environment);

                    if (solutionCost > antCost && antCost != 0) {
                        this.solutionCost = antCost;
                        this.solution = ant.getSolutionAsString();
                        if (((CloudAcoAntForWorkflow) ant).isValidAnswer()) {
                            System.out.println("valid Answer found! cost: " + this.solutionCost);
                        }
                    }
                }

                ant.doAfterSolutionIsReady(environment, configurationProvider);
            }

            CloudAcoAntForWorkflow.resetCache();
        }
    }


    String getSolution() {
        return solution;
    }

    double getSolutionCost() {
        return solutionCost;
    }
}
