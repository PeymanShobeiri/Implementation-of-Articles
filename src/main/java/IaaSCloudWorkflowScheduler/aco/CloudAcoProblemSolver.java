package IaaSCloudWorkflowScheduler.aco;


import isula.aco.*;

import javax.naming.ConfigurationException;
import java.util.ArrayList;
import java.util.List;

public class CloudAcoProblemSolver extends AcoProblemSolver<CloudAcoProblemNode, CloudAcoEnvironment> {

    private CloudAcoProblemNode[] bestSolution;
    private CloudAcoEnvironment environment;
    private CloudAcoAntColony antColony;

    private ConfigurationProvider configurationProvider;

    private List<DaemonAction<CloudAcoProblemNode, CloudAcoEnvironment>> daemonActions = new ArrayList<>();

    /**
     * Prepares the solver for problem resolution.
     *
     * @param environment Environment instance, with problem-related information.
     * @param colony      The Ant Colony with specialized ants.
     * @param config      Algorithm configuration.
     */
    void initialize(CloudAcoEnvironment environment, CloudAcoAntColony colony, ConfigurationProvider config) {
        colony.buildColony(environment);
        this.setConfigurationProvider(config);
        this.setEnvironment(environment);
        this.setCloudAcoAntColony(colony);
    }

    /**
     * Applies all daemon actions of a specific type.
     *
     * @param daemonActionType Daemon action type.
     */
    private void applyDaemonActions(DaemonActionType daemonActionType) {
        for (DaemonAction<CloudAcoProblemNode, CloudAcoEnvironment> daemonAction : daemonActions) {
            if (daemonActionType.equals(daemonAction.getAcoPhase())) {
                daemonAction.applyDaemonAction(this.getConfigurationProvider());
            }
        }
    }

    @SafeVarargs
    final void addCloudACODaemonActions(DaemonAction<CloudAcoProblemNode, CloudAcoEnvironment>... daemonActions) {
        for (DaemonAction<CloudAcoProblemNode, CloudAcoEnvironment> daemonAction : daemonActions) {
            this.addDaemonAction(daemonAction);
        }
    }

    private void addDaemonAction(DaemonAction<CloudAcoProblemNode, CloudAcoEnvironment> daemonAction) {
        daemonAction.setAntColony(this.antColony);
        daemonAction.setEnvironment(this.environment);
        daemonAction.setProblemSolver(this);
        this.daemonActions.add(daemonAction);
    }


    public void solveProblem() throws ConfigurationException {
        this.applyDaemonActions(DaemonActionType.INITIAL_CONFIGURATION);
        int numberOfIterations = this.configurationProvider.getNumberOfIterations();
        if (numberOfIterations < 1) {
            throw new ConfigurationException("No iterations are programed for this solver. Check your Configuration Provider.");
        } else {
            for (int iteration = 0; iteration < numberOfIterations; ++iteration) {
                this.antColony.clearAntSolutions();
                this.antColony.buildSolutions(this.environment, this.configurationProvider);

                this.applyDaemonActions(DaemonActionType.AFTER_ITERATION_CONSTRUCTION);
                System.out.println("Current iteration: " + iteration + " Best solution cost: " + antColony.getSolutionCost());
            }
            System.out.println("Best solution cost: " + this.antColony.getSolutionCost());
            System.out.println("Best solution:\n" + this.antColony.getSolution());
        }
    }

    public CloudAcoEnvironment getEnvironment() {
        return environment;
    }

    public void setEnvironment(CloudAcoEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public CloudAcoAntColony getAntColony() {
        return antColony;
    }

    @Override
    public void setAntColony(AntColony<CloudAcoProblemNode, CloudAcoEnvironment> antColony) {
        super.setAntColony(antColony);
    }


    private void setCloudAcoAntColony(CloudAcoAntColony cloudAcoAntColony) {
        this.antColony = cloudAcoAntColony;
    }

    public ConfigurationProvider getConfigurationProvider() {
        return configurationProvider;
    }

    public void setConfigurationProvider(ConfigurationProvider configurationProvider) {
        this.configurationProvider = configurationProvider;
    }

    public CloudAcoProblemNode[] getBestSolution() {
        return bestSolution;
    }

    public double getBestSolutionCost() {
        return this.antColony.getSolutionCost();
    }

    public void setBestSolutionCost(double bestSolutionCost) {
        // we do not need this method
    }

    public String getBestSolutionAsString() {
        return this.antColony.getSolution();
    }


}
