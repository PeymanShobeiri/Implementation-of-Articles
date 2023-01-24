package IaaSCloudWorkflowScheduler.aco;


import isula.aco.Environment;
import isula.aco.exception.InvalidInputException;

import java.util.ArrayList;
import java.util.List;

public class CloudAcoEnvironment extends Environment {

    private CloudAcoProblemRepresentation problemGraph;

    public CloudAcoEnvironment(double[][] problemRepresentation) throws InvalidInputException {
        super(problemRepresentation);
    }


    public CloudAcoEnvironment(CloudAcoProblemRepresentation problemGraph) throws InvalidInputException {
        super(new double[100][100]);
        this.problemGraph = problemGraph;
        this.setPheromoneMatrix(createPheromoneMatrix());
        this.createPheromoneMatrix();
        this.populatePheromoneMatrix(0.01);

    }

    protected double[][] createPheromoneMatrix() {
        if (problemGraph == null)
            return new double[100][100];
        return new double[problemGraph.getProblemNodeList().size()][problemGraph.getProblemNodeList().size()];
    }

    public List<Double> getPheromoneValues() {
        List<Double> a = new ArrayList<>();
        int size = getProblemGraph().getGraphSize();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (!a.contains(getPheromoneMatrix()[i][j])) {
                    a.add(getPheromoneMatrix()[i][j]);
                }
            }
        }
        return a;
    }


    public CloudAcoProblemRepresentation getProblemGraph() {
        return problemGraph;
    }
}
