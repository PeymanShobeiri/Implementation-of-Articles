package IaaSCloudWorkflowScheduler.aco;

import IaaSCloudWorkflowScheduler.ResourceSet;
import IaaSCloudWorkflowScheduler.WorkflowGraph;
import isula.aco.algorithms.antsystem.StartPheromoneMatrix;
import isula.aco.exception.InvalidInputException;

import javax.naming.ConfigurationException;
import java.util.List;

public class CloudAcoWorkflow {
    static int antId = 0;
    private static CloudAcoWorkflow mCloudAcoWorkflow;
    private CloudAcoProblemSolver solver;
    private CloudAcoAntColony colony;
    private CloudAcoEnvironment environment;
    private CloudAcoProblemRepresentation problemRepresentation;

    private CloudAcoWorkflow(WorkflowGraph graph, ResourceSet resourceSet, long bandwidth, int deadline) throws InvalidInputException {

        problemRepresentation = new CloudAcoProblemRepresentation(graph, resourceSet, bandwidth, deadline, 10);
        CloudAcoConfigurationProvider configurationProvider = new CloudAcoConfigurationProvider();
        colony = getAntColony(configurationProvider);

        environment = new CloudAcoEnvironment(problemRepresentation);
        configurationProvider.setEnvironment(environment);

        solver = new CloudAcoProblemSolver();
        solver.initialize(environment, colony, configurationProvider);
        StartPheromoneMatrix<CloudAcoProblemNode, CloudAcoEnvironment> startPheromoneMatrix = new StartPheromoneMatrix<>();
        startPheromoneMatrix.setEnvironment(environment);
        WorkflowUpdatePheromoneMatrix workflowUpdatePheromoneMatrix = new WorkflowUpdatePheromoneMatrix();
        workflowUpdatePheromoneMatrix.setEnvironment(environment);
        solver.addCloudACODaemonActions(startPheromoneMatrix, workflowUpdatePheromoneMatrix);

        solver.getAntColony().addAntPolicies(
                new CloudAcoPseudoRandomNodeSelection<>());

    }

    public static CloudAcoWorkflow OptimiseWorkFlow(WorkflowGraph graph, ResourceSet resourceSet, long bandwidth, int deadline)
            throws InvalidInputException {
        mCloudAcoWorkflow = new CloudAcoWorkflow(graph, resourceSet, bandwidth, deadline);
        return mCloudAcoWorkflow;
    }


    public CloudAcoWorkflow setLacoSort(List<String> sortedTaskIds) {
        problemRepresentation.lacoSort(sortedTaskIds);
        return mCloudAcoWorkflow;
    }

    public static void solve() throws ConfigurationException {
        mCloudAcoWorkflow.solver.solveProblem();
    }

    public static void printSolution() {
        System.out.println("Cost: " + mCloudAcoWorkflow.colony.getSolutionCost()
                + "\n" + mCloudAcoWorkflow.colony.getSolution());
    }

    private static CloudAcoAntColony getAntColony(CloudAcoConfigurationProvider configurationProvider) {
        return new CloudAcoAntColony(configurationProvider.getNumberOfAnts());
    }

}
