package IaaSCloudWorkflowScheduler;

import IaaSCloudWorkflowScheduler.aco.CloudAcoEnvironment;
import IaaSCloudWorkflowScheduler.aco.CloudAcoProblemNode;
import IaaSCloudWorkflowScheduler.aco.CloudAcoProblemRepresentation;
import IaaSCloudWorkflowScheduler.aco.CloudAcoResourceInstance;
import utils.Board;
import utils.Table;

import java.util.*;
import java.util.stream.Collectors;

public class CloudACO {
    private static final double H_RATIO = 0.6;
    private static final double P_RATIO = 0.4;
    private static final double EVAP_RATIO = 0.1;
    private static final double Q0 = 0.9;
    Random random = new Random();
    private int iterCount = 100;
    private int antCount = 150;
    private double[][] pheromone;
    private double[] heuristic;
    private double[] probability;
    private Ant[] colony;
    private Ant bestAnt = null;
    private CloudAcoEnvironment environment;
    private HashMap<CloudACO.HeuristicCondition, Double> heuristicCache;

    public void schedule(CloudAcoEnvironment environment, double deadline) {
        CloudAcoProblemRepresentation workflow = environment.getProblemGraph();
        this.environment = environment;
        initPheromone(environment.getProblemGraph().getProblemNodeList().size() * 2);
        for (int itr = 0; itr < iterCount; itr++) {
            initColony(antCount, workflow.getGraphSize());
            Ant currentAnt;
            heuristicCache = new HashMap<>();
            CloudAcoProblemNode dest;
            for (int antNum = 0; antNum < antCount; antNum++) {
                currentAnt = colony[antNum];
                currentAnt.currentNode = workflow.getStart();
                currentAnt.currentPosition = 0;
                while (!currentAnt.isCompleted) {
                    List<CloudAcoProblemNode> candidateNodes
                            = currentAnt.currentNode.getNeighbourhood(environment);
                    int bestOptionByHeuristic = calculateHeuristic(candidateNodes, currentAnt.currentPosition);
                    int bestOptionByProbability = calculateProbability(currentAnt, candidateNodes);

                    if (random.nextDouble() < Q0) {
                        dest = candidateNodes.get(bestOptionByProbability);
                    } else {
                        dest = rwsSelection(candidateNodes, probability);
                    }
                    currentAnt.currentNode.setVisited(environment);
                    dest.setVisited(environment);
                    dest.getResource().setCurrentTask(dest);
                    currentAnt.setDest(dest);
                }

                CloudAcoProblemNode end = currentAnt.solution[currentAnt.solution.length - 1];
                currentAnt.makeSpan = end.getNode().getAFT();

                currentAnt.solutionCost = getSolutionCost();

                if (bestAnt == null && currentAnt.makeSpan <= deadline) {
                    bestAnt = currentAnt;
                    bestAnt.saveSolution();
                    System.out.println("best ant: " + bestAnt.solutionCost);
                } else if (currentAnt.solutionCost <= bestAnt.solutionCost && currentAnt.makeSpan <= deadline) {
                    bestAnt = currentAnt;
                    bestAnt.saveSolution();
                    System.out.println("best ant: " + bestAnt.solutionCost);
                }
                environment.getProblemGraph().getInstanceSet().resetPerAnt();


            }
            updatePheromone();
        }

        System.out.println("best ant: " + bestAnt);
    }

    private double getSolutionCost() {
        return environment.getProblemGraph()
                .getInstanceSet()
                .getInstances()
                .entrySet()
                .stream()
                .flatMap(e -> e.getValue().stream())
                .collect(Collectors.toList())
                .stream().mapToDouble(CloudAcoResourceInstance::getTotalCost)
                .sum();
    }

    private void updatePheromone() {
        for (int j = 0; j < pheromone.length; j++)
            for (int i = 0; i < pheromone[j].length; i++)
                pheromone[j][i] *= EVAP_RATIO;

        if (bestAnt != null)
            releasePheromone(bestAnt);

        for (int j = 0; j < pheromone.length; j++)
            for (int i = 0; i < pheromone[j].length; i++)
                if (pheromone[j][i] > 1)
                    pheromone[j][i] = 1;
                else if (pheromone[j][i] < 0.2)
                    pheromone[j][i] = 0.2;
    }

    private int calculateProbability(Ant current, List<CloudAcoProblemNode> candidateNodes) {
        double best = -1;
        int bestIndex = -1;
        for (int i = 0; i < candidateNodes.size(); i++) {
            probability[i] = Math.pow(heuristic[i], H_RATIO)
                    * Math.pow(pheromone[current.currentNode.getId()][candidateNodes.get(i).getId()], P_RATIO);

            if (probability[i] >= best) {
                best = probability[i];
                bestIndex = i;
            }
        }
        return bestIndex;
    }


    public void releasePheromone(Ant bestAnt) {
        double value = 1 / bestAnt.solutionCost + 0.5;
        for (int i = 0; i < bestAnt.solution.length - 1 && !bestAnt.solution[i + 1].getNode().getId().equalsIgnoreCase("end"); i++)
            pheromone[bestAnt.solution[i].getId()][bestAnt.solution[i + 1].getId()] += value;
    }

    /**
     * index 0 -> best option heuristic index
     * index 1 -> best option runtime
     * index 2 -> best option cost
     *
     * @param candidateNodes
     * @param positionInSolution
     * @return
     */
    private int calculateHeuristic(List<CloudAcoProblemNode> candidateNodes, int positionInSolution) {
        int bestOptionIndex = -1;
        double bestOption = -1;
        for (int i = 0; i < candidateNodes.size(); i++) {
            heuristic[i] = getHeuristicValue(candidateNodes.get(i), positionInSolution);
            candidateNodes.get(i).h = heuristic[i];
            if (heuristic[i] >= bestOption) {
                bestOption = heuristic[i];
                bestOptionIndex = i;
            }
        }
        return bestOptionIndex;
    }

    /**
     * index 0 -> heuristic value
     * index 1 -> option runtime
     * index 2 -> option cost
     *
     * @param destination
     * @param positionInSolution
     * @return
     */
    public double getHeuristicValue(CloudAcoProblemNode destination, Integer positionInSolution) {

        double curCost = destination.getResource().getCost(destination.getNode());
        double currentDuration = destination.getResource().getTaskDuration(destination.getNode());
        double currentST = Math.max(destination.getResource().getInstanceReleaseTime(), destination.getNode().getEST());
        double currentFT = currentST + currentDuration;

        CloudACO.HeuristicCondition hc = new CloudACO.HeuristicCondition(currentDuration, curCost, currentST, destination.getResource().getId());
        if (heuristicCache.containsKey(hc)) {
            return heuristicCache.get(hc);
        }
        double h1;
        double h2;

        if (!destination.getNode().getId().equalsIgnoreCase("end") &&
                !destination.getNode().getId().equalsIgnoreCase("start")) {
            if ((currentFT) > (destination.getNode().getLFT())) {
                return 0.0;
            }
        }

        boolean bad = false;

        if (currentFT < destination.getNode().getDeadline()) {
            h1 = 1;
        } else {
            h1 = (Math.max(0, (((destination.getNode().getLFT() - currentFT) + 1)
                    / ((destination.getNode().getLFT() - destination.getNode().getDeadline() + 1) * 1.0f))));
            bad = true;
        }

        double maxCost = -1;
        double minCost = Float.MAX_VALUE;
        double fastest = Double.MAX_VALUE, slowest = 0;
        double temp, tempDuration;

        for (Map.Entry<Resource, List<CloudAcoResourceInstance>> entry :
                environment.getProblemGraph().getInstanceSet().getInstances().entrySet()) {
            for (CloudAcoResourceInstance instance : entry.getValue()) {
                temp = instance.getCost(destination.getNode());
                tempDuration = instance.getTaskDuration(destination.getNode());
                if (temp > maxCost) {
                    maxCost = temp;
                } else if (temp < minCost) {
                    minCost = temp;
                }

                if (tempDuration > slowest) {
                    slowest = tempDuration;
                } else if (tempDuration < fastest) {
                    fastest = tempDuration;
                }
            }
        }

        double h3 = (slowest - currentDuration + 1) / (slowest - fastest + 1);

        h2 = ((maxCost - curCost + 1) / (maxCost - minCost + 1));

        int p1 = 5, p2 = 5, p3 = 5;
        double ratio = p1 + p2 + p3;


        if (positionInSolution < environment.getProblemGraph().getGraphSize() / 3) {
            p1 = 2;
            p2 = 11;
            p3 = 2;
        } else if (positionInSolution > (2 * (environment.getProblemGraph().getGraphSize() / 3))) {
            p1 = 2;
            p2 = 11;
            p3 = 2;
        }

        double result = ((h1 * p1) + (h2 * p2) + (h3 * p3)) / ratio;

        if (bad)
            result = Math.pow(result, 2);

        heuristicCache.put(hc, result);
        return result;
    }


    private CloudAcoProblemNode rwsSelection(List<CloudAcoProblemNode> candidates, double[] probabilities) {
        double value = random.nextDouble();
        double total = 0.0D;
        CloudAcoProblemNode node = null;

        for (int i = 0; i < candidates.size() && total < value; i++) {
            node = candidates.get(i);
            Double probability = probabilities[i];
            if (probability.isNaN()) {
                throw new RuntimeException("The probability for component " + node + " is not a number.");
            }

            total += probability;
        }

        return node;
    }


    private void initColony(int antCount, int graphSize) {
        CloudAcoProblemNode start = environment.getProblemGraph().getStart();
        this.colony = new Ant[antCount];
        for (int i = 0; i < antCount; i++) {
            Ant ant = new Ant(i, graphSize);
            ant.solution[0] = start;
            this.colony[i] = ant;
        }
    }

    private void initPheromone(int size) {
        heuristic = new double[size];
        probability = new double[size];
        pheromone = new double[size][size];

        for (int i = 0; i < pheromone.length; i++)        //initialize pheromone
            for (int j = 0; j < pheromone[i].length; j++)
                pheromone[i][j] = 0.000011;
    }


    public static class Ant {
        boolean isCompleted = false;
        private int id;
        private CloudAcoProblemNode[] solution;
        private String solutionString;
        private double solutionCost = 0;
        private double makeSpan = 0;
        private CloudAcoProblemNode currentNode;
        private int currentPosition = 0;

        public Ant(int id, int size) {
            this.id = id;
            this.solution = new CloudAcoProblemNode[size];
        }

        public Ant() {
        }


        public int setDest(CloudAcoProblemNode node) {
            this.currentNode = node;
            this.solution[++this.currentPosition] = node;
            if (node.getNode().getId().equalsIgnoreCase("end") || currentPosition == solution.length) {
                isCompleted = true;
            }
            return this.currentPosition;
        }

        @Override
        public String toString() {
            return solutionString;
        }

        public void saveSolution() {


            List<String> headersList = Arrays.asList("N", "R", "I", "I-cost", "R-cost", "AST", "runtime", "AFT", "SD", "LFT", "I-start", "H");
            List<List<String>> rowsList = new ArrayList<>();
//bookmark 1
            for (CloudAcoProblemNode node : solution) {
                if (node == null)
                    continue;

                double runtime = node.getResource().getTaskDuration(node.getNode());
                rowsList.add(Arrays.asList(
                        node.getNode().getId() + "",
                        node.getResource().getId() + "",
                        node.getResource().getInstanceId() + "",
                        node.getResource().getTotalCost() + "",
                        node.getResource().getResource().getCost() + "",
                        node.getNode().getAST() + "",
                        Double.toString(runtime).substring(0, Math.min(4, Double.toString(runtime).length())) + "",
                        (node.getNode().getAFT()) + "",
                        (node.getNode().getDeadline()) + "",
                        node.getNode().getLFT() + "",
                        node.getResource().getInstanceStartTime() + "",
                        Double.toString(node.h).substring(0, Math.min(4, Double.toString(node.h).length())) + ""));
            }

            Board board = new Board(110);
            solutionString = "\n" + board.setInitialBlock(new Table(board, 100, headersList, rowsList).tableToBlocks()).build().getPreview() + "\n cost: " + solutionCost;
        }
    }

    public static class HeuristicCondition {
        private double curDuration;
        private double curCost;
        private double curStartTime;
        private int instanceId;

        public HeuristicCondition(double curDuration, double curCost, double curStartTime, int instanceId) {
            this.curDuration = curDuration;
            this.curCost = curCost;
            this.curStartTime = curStartTime;
            this.instanceId = instanceId;
        }

        public double getCurDuration() {
            return curDuration;
        }

        public void setCurDuration(double curDuration) {
            this.curDuration = curDuration;
        }

        public double getCurCost() {
            return curCost;
        }

        public void setCurCost(double curCost) {
            this.curCost = curCost;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CloudACO.HeuristicCondition)) return false;
            CloudACO.HeuristicCondition that = (CloudACO.HeuristicCondition) o;
            return Double.compare(that.curDuration, curDuration) == 0 &&
                    Double.compare(that.curCost, curCost) == 0 &&
                    Double.compare(that.curStartTime, curStartTime) == 0 &&
                    instanceId == that.instanceId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(curDuration, curCost, curStartTime, instanceId);
        }
    }
}
