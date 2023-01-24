package IaaSCloudWorkflowScheduler.aco;

import isula.aco.ConfigurationProvider;
import isula.aco.Environment;
import isula.aco.algorithms.acs.AcsConfigurationProvider;
import isula.aco.algorithms.antsystem.RandomNodeSelection;
import isula.aco.exception.ConfigurationException;
import isula.aco.exception.SolutionConstructionException;

import java.util.*;
import java.util.logging.Logger;

public class CloudAcoPseudoRandomNodeSelection<CloudAcoProblemNode, CloudAcoEnvironment extends Environment>
        extends RandomNodeSelection<CloudAcoProblemNode, CloudAcoEnvironment> {
    private static Logger logger = Logger.getLogger(CloudAcoProblemSolver.class.getName());
    private double lastMakeSpan;
    private int lastNodeId = -1;

    @Override
    public boolean applyPolicy(CloudAcoEnvironment environment, ConfigurationProvider configuration) {
        boolean nodeWasSelected = false;
        AcsConfigurationProvider configurationProvider = (AcsConfigurationProvider) configuration;
        HashMap<CloudAcoProblemNode, Double> componentsWithProbabilities = this.getComponentsWithProbabilities(environment, configurationProvider);
        if (this.selectMostConvenient(configurationProvider)) {
            logger.fine("Selecting the greedy choice\n");
            CloudAcoProblemNode nextNode = this.getMostConvenient(componentsWithProbabilities);
            if (nextNode != null) {
                ((IaaSCloudWorkflowScheduler.aco.CloudAcoProblemNode) nextNode).setByRW = false;
                ((IaaSCloudWorkflowScheduler.aco.CloudAcoProblemNode) nextNode).h = this.getAnt().getHeuristicValue(nextNode, getAnt().getCurrentIndex(), environment);
                nodeWasSelected = true;
                this.getAnt().visitNode(nextNode);
            }
        } else {
            logger.fine("Selecting the probabilistic choice\n");
            nodeWasSelected = this.RWSSelection(environment, componentsWithProbabilities);
            return nodeWasSelected;
        }

        if (!nodeWasSelected)
            this.doIfNoComponentsFound(environment, configurationProvider);

        return true;
    }

    private Boolean RWSSelection(CloudAcoEnvironment environment, HashMap<CloudAcoProblemNode, Double> componentsWithProbabilities) {
        Random random = new Random();
        CloudAcoProblemNode nextNode;
        double value = random.nextDouble();
        double total = 0.0D;
        Iterator<Map.Entry<CloudAcoProblemNode, Double>> iterator = componentsWithProbabilities.entrySet().iterator();
        Map.Entry<CloudAcoProblemNode, Double> componentWithProbability;
        do {
            if (!iterator.hasNext()) {
                // nextNode = (CloudAcoProblemNode) (componentsWithProbabilities.keySet().toArray())[(int) (componentsWithProbabilities.size() * value)];
                // this.getAnt().visitNode(nextNode);
                // printSolution(this.getAnt().getSolution());
                this.doIfNoComponentsFound(null, null);
            }

            componentWithProbability = iterator.next();
            Double probability = componentWithProbability.getValue();
            if (probability.isNaN()) {
                throw new ConfigurationException("The probability for component " + componentWithProbability.getKey() + " is not a number.");
            }

            total += probability;
        } while (total < value);
        nextNode = componentWithProbability.getKey();
        ((IaaSCloudWorkflowScheduler.aco.CloudAcoProblemNode) nextNode).setByRW = true;
        ((IaaSCloudWorkflowScheduler.aco.CloudAcoProblemNode) nextNode).h = this.getAnt().getHeuristicValue(nextNode, getAnt().getCurrentIndex(), environment);

        this.getAnt().visitNode(nextNode);
        return true;
    }

    private void printSolution(CloudAcoProblemNode[] solution) {
        for (CloudAcoProblemNode c : solution) {
            if (c != null)
                System.out.println(c.toString());
        }
    }

    public double getLastMakeSpan() {
        return lastMakeSpan;
    }

    public void setLastMakeSpan(double lastMakeSpan) {
        this.lastMakeSpan = lastMakeSpan;
    }

    private boolean selectMostConvenient(AcsConfigurationProvider configurationProvider) {
        double Q0 = configurationProvider.getBestChoiceProbability();
        Random random = new Random();
        double Q = random.nextDouble();
        return Q <= Q0;
    }

    private CloudAcoProblemNode getMostConvenient(HashMap<CloudAcoProblemNode, Double> possibleMoves) {
        CloudAcoProblemNode nextNode = null;
        double currentMaximumProbability = -100000.0D;

        for (Map.Entry<CloudAcoProblemNode, Double> componentWithProbability : possibleMoves.entrySet()) {
            CloudAcoProblemNode possibleMove = componentWithProbability.getKey();
            double currentProbability = componentWithProbability.getValue();
            if (!this.getAnt().isNodeVisited(possibleMove) && currentProbability > currentMaximumProbability) {
                nextNode = possibleMove;
                currentMaximumProbability = currentProbability;
            } else if (!this.getAnt().isNodeVisited(possibleMove) && currentProbability == currentMaximumProbability) {
                CloudAcoResourceInstance last = ((IaaSCloudWorkflowScheduler.aco.CloudAcoProblemNode) nextNode).getResource();
                CloudAcoResourceInstance current = ((IaaSCloudWorkflowScheduler.aco.CloudAcoProblemNode) possibleMove).getResource();

                if ((last.getInstanceRemainingTime(last.getInstanceReleaseTime()) * last.getMIPS())
                        == (current.getInstanceRemainingTime(current.getInstanceReleaseTime()) * current.getMIPS())) {
                    if (last.getResource().getCost() > current.getResource().getCost())
                        nextNode = possibleMove;
                } else if ((last.getInstanceRemainingTime(last.getInstanceReleaseTime()) * last.getMIPS())
                        < (current.getInstanceRemainingTime(current.getInstanceReleaseTime()) * current.getMIPS())) {
                    nextNode = possibleMove;
                }
            }
        }

        return nextNode;
    }


    @Override
    public HashMap<CloudAcoProblemNode, Double> getComponentsWithProbabilities(CloudAcoEnvironment environment, ConfigurationProvider configurationProvider) {
        HashMap<CloudAcoProblemNode, Double> componentsWithProbabilities = new LinkedHashMap<>();
        Double totalProbability = 0.0D;

        List<CloudAcoProblemNode> neighbourhood = this.getAnt().getNeighbourhood(environment);
        if (neighbourhood == null) {
            throw new SolutionConstructionException("The ant's neighbourhood is null. There are no candidate components to add.");
        } else {

            for (CloudAcoProblemNode possibleMove : this.getAnt().getNeighbourhood(environment)) {
                if (!this.getAnt().isNodeVisited(possibleMove) && this.getAnt().isNodeValid(possibleMove)) {
                    Double heuristicTimesPheromone = this.getCloudAcoHeuristicTimesPheromone(environment, configurationProvider, possibleMove);
                    totalProbability += heuristicTimesPheromone;
                    componentsWithProbabilities.put(possibleMove, heuristicTimesPheromone);
                }
            }

            //wrong path we should skip this solution
            if (totalProbability == 0) {
                return this.doIfNoComponentsFound(environment, configurationProvider);
            }

            for (Map.Entry<CloudAcoProblemNode, Double> entry : componentsWithProbabilities.entrySet()) {
                entry.setValue(entry.getValue() / totalProbability);
            }

            if (componentsWithProbabilities.size() < 1) {
                return this.doIfNoComponentsFound(environment, configurationProvider);
            } else {
                return componentsWithProbabilities;
            }
        }
    }

    private Double getCloudAcoHeuristicTimesPheromone(CloudAcoEnvironment environment, ConfigurationProvider configurationProvider, CloudAcoProblemNode possibleMove) {
        Double heuristicValue = this.getAnt().getHeuristicValue(possibleMove, this.getAnt().getCurrentIndex(), environment);
        Double pheromoneTrailValue = this.getAnt().getPheromoneTrailValue(possibleMove, this.getAnt().getCurrentIndex(), environment);

        if (heuristicValue != null && pheromoneTrailValue != null) {
            /*return Math.pow(configurationProvider.getHeuristicImportance(), heuristicValue)
             * Math.pow(pheromoneTrailValue, configurationProvider.getPheromoneImportance());
             */

            return Math.pow(heuristicValue, 0.6)
                    * Math.pow(pheromoneTrailValue, 0.4);
        } else {
            throw new SolutionConstructionException("The current ant is not producing valid pheromone/heuristic values " +
                    "for the solution component: " + possibleMove + " .Heuristic value " + heuristicValue +
                    " Pheromone value: " + pheromoneTrailValue);
        }
    }


    @Override
    protected HashMap<CloudAcoProblemNode, Double> doIfNoComponentsFound(CloudAcoEnvironment environment, ConfigurationProvider configurationProvider) {
        throw new SolutionConstructionException("We have no suitable components to add! wrong path we should skip this solution");
    }
}
