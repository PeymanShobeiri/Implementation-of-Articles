package IaaSCloudWorkflowScheduler.aco;


import isula.aco.Ant;
import isula.aco.ConfigurationProvider;
import isula.aco.algorithms.maxmin.MaxMinConfigurationProvider;
import isula.aco.algorithms.maxmin.UpdatePheromoneMatrixForMaxMin;
import isula.aco.exception.ConfigurationException;

public class WorkflowUpdatePheromoneMatrix extends UpdatePheromoneMatrixForMaxMin<CloudAcoProblemNode, CloudAcoEnvironment> {

    @Override
    protected double getNewPheromoneValue(Ant<CloudAcoProblemNode, CloudAcoEnvironment> ant, int pre,
                                          CloudAcoProblemNode next, MaxMinConfigurationProvider configurationProvider) {

        double cost = ((CloudAcoAntForWorkflow) ant).getLastSolutionCost();
        double makeSpan = ((CloudAcoAntForWorkflow) ant).getLastMakeSpan();
        int deadline = this.getEnvironment().getProblemGraph().getDeadline();
        double minCost = ((CloudAcoConfigurationProvider) configurationProvider).getMinCost();
        double maxCost = ((CloudAcoConfigurationProvider) configurationProvider).getMaxCost();

       /* double contribution = ((CloudAcoAntForWorkflow) ant).getLastRawSolutionCost() / ((CloudAcoAntForWorkflow) ant).getLastSolutionCost();
        double newValue = ant.getPheromoneTrailValue(solutionComponent, positionInSolution, getEnvironment());
        newValue += contribution;
        return newValue;*/

        double K;

        if (makeSpan > deadline) {
            K = (deadline / makeSpan) + (minCost / maxCost);
        } else {
            K = 1 + (minCost / cost);
        }
        return 0.9 * ant.getPheromoneTrailValue(next, pre, getEnvironment()) + 0.1 * K;

//        double contribution =  ((CloudAcoAntForWorkflow)ant).getLastRawSolutionCost() / ((CloudAcoAntForWorkflow)ant).getLastSolutionCost();
//
//     double newValue = ant.getPheromoneTrailValue(solutionComponent, positionInSolution, getEnvironment());
//        newValue += contribution;
//        return newValue;
    }

    @Override
    protected double getMaximumPheromoneValue(MaxMinConfigurationProvider configurationProvider) {
        return 1;
    }

    @Override
    protected double getMinimumPheromoneValue(MaxMinConfigurationProvider configurationProvider) {
        return 0.0001;
    }

    @Override
    public void applyDaemonAction(ConfigurationProvider provider) {

        MaxMinConfigurationProvider configurationProvider = (MaxMinConfigurationProvider) provider;
        double[][] pheromoneMatrix = getEnvironment().getPheromoneMatrix();

        int matrixRows = pheromoneMatrix.length;
        int matrixColumns = pheromoneMatrix[0].length;
        double t0 = configurationProvider.getEvaporationRatio() * configurationProvider.getInitialPheromoneValue();


        for (int i = 0; i < matrixRows; i++) {
            for (int j = 0; j < matrixColumns; j++) {
                double newValue = pheromoneMatrix[i][j]
                        * configurationProvider.getEvaporationRatio();

                if (newValue >= getMinimumPheromoneValue(configurationProvider))
                    pheromoneMatrix[i][j] = newValue;
                else
                    pheromoneMatrix[i][j] = getMinimumPheromoneValue(configurationProvider);

                validatePheromoneValue(pheromoneMatrix[i][j]);
            }
        }

        Ant<CloudAcoProblemNode, CloudAcoEnvironment> bestAnt = getAntColony().getBestPerformingAnt(getEnvironment());
        CloudAcoProblemNode[] bestSolution = bestAnt.getSolution();

        //assigning the pheromon of best solution
        for (int componentIndex = 1; componentIndex < bestSolution.length; componentIndex += 1) {
            CloudAcoProblemNode solutionComponent = bestSolution[componentIndex];
            CloudAcoProblemNode lastPos = bestSolution[componentIndex - 1];
            double newValue = this.getNewPheromoneValue(bestAnt, lastPos.getId(), solutionComponent, configurationProvider);
            if (newValue <= this.getMaximumPheromoneValue(configurationProvider)) {
                bestAnt.setPheromoneTrailValue(lastPos, solutionComponent.getId(), this.getEnvironment(), newValue);
            } else {
                bestAnt.setPheromoneTrailValue(lastPos, solutionComponent.getId(), this.getEnvironment(),
                        this.getMaximumPheromoneValue(configurationProvider));
            }
            this.validatePheromoneValue(bestAnt.getPheromoneTrailValue(lastPos, solutionComponent.getId(),
                    this.getEnvironment()));
        }
    }

    private void validatePheromoneValue(double v) {
        if (Double.isInfinite(v) || Double.isNaN(v)) {
            throw new ConfigurationException("The pheromone value calculated is not a valid number: " + v);
        }
    }


}
