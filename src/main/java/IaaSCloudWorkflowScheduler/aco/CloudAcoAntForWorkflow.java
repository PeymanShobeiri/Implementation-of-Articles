package IaaSCloudWorkflowScheduler.aco;

import IaaSCloudWorkflowScheduler.Resource;
import isula.aco.Ant;
import isula.aco.ConfigurationProvider;
import utils.Board;
import utils.Table;

import java.util.*;
import java.util.stream.Collectors;

public class CloudAcoAntForWorkflow extends Ant<CloudAcoProblemNode, CloudAcoEnvironment> {

    private CloudAcoProblemNode currentNode;
    private CloudAcoEnvironment environment;
    private int id;
    private double lastSolutionCost = 0;
    private double lastMakeSpan = 0;
    private double lastRawSolutionCost = 0;
    private boolean validAnswer = false;
    private static HashMap<HeuristicCondition, Double> heuristicCache;
    private int lastNodeId = -1;
    private static int cacheUses = 0;

    CloudAcoAntForWorkflow(CloudAcoEnvironment environment) {
        this.environment = environment;
        this.currentNode = new CloudAcoProblemNode();
        this.setSolution(new CloudAcoProblemNode[environment.getProblemGraph().getGraphSize()]);
        this.setVisited(new HashMap<>());
        this.id = CloudAcoWorkflow.antId++;
        if (heuristicCache == null)
            heuristicCache = new HashMap<>();
    }

    public double getLastMakeSpan() {
        return lastMakeSpan;
    }

    public void setLastMakeSpan(double lastMakeSpan) {
        this.lastMakeSpan = lastMakeSpan;
    }

    @Override
    public boolean isSolutionReady(CloudAcoEnvironment cloudAcoEnvironment) {
        return getCurrentIndex() == environment.getProblemGraph().getGraphSize();
    }

    @Override
    public double getSolutionCost(CloudAcoEnvironment cloudAcoEnvironment) {
        double total;
        int deadline = environment.getProblemGraph().getDeadline();

        total = environment.getProblemGraph()
                .getInstanceSet()
                .getInstances()
                .entrySet()
                .stream()
                .flatMap(e -> e.getValue().stream())
                .collect(Collectors.toList())
                .stream().mapToDouble(CloudAcoResourceInstance::getTotalCost)
                .sum();

        lastSolutionCost = total;
        lastRawSolutionCost = total;
        lastMakeSpan = environment.getProblemGraph().getGraph().getNodes().get(environment.getProblemGraph().getGraph().getEndId()).getAFT();
        if (environment.getProblemGraph().getGraph().getNodes().get(environment.getProblemGraph().getGraph().getEndId()).getAFT() > deadline) {
            validAnswer = false;
            lastSolutionCost = total * (environment.getProblemGraph().getGraph().getNodes().get(environment.getProblemGraph().getGraph().getEndId()).getAFT() - deadline);
            return lastSolutionCost;
        }
        return total;
    }

    double getLastSolutionCost() {
        return lastSolutionCost;
    }

    double getLastRawSolutionCost() {
        return lastRawSolutionCost;
    }


    boolean isValidAnswer() {

        for (CloudAcoProblemNode n : getSolution()) {
            if (n == null)
                return false;
        }
        return validAnswer && isSolutionReady(environment);
    }

    void setValidAnswer(boolean validAnswer) {
        this.validAnswer = validAnswer;
    }

    @Override
    public String getSolutionAsString() {
        double total = environment.getProblemGraph()
                .getInstanceSet()
                .getInstances()
                .entrySet()
                .stream()
                .flatMap(e -> e.getValue().stream())
                .collect(Collectors.toList())
                .stream().mapToDouble(CloudAcoResourceInstance::getTotalCost)
                .sum();

        int deadline = environment.getProblemGraph().getDeadline();

        List<String> headersList = Arrays.asList("N", "R", "I", "I-cost", "R-cost", "AST", "runtime", "AFT", "SD", "LFT", "I-start", "H");
        List<List<String>> rowsList = new ArrayList<>();
//bookmark 1
        for (CloudAcoProblemNode node : getSolution()) {
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
        String tableString = board.setInitialBlock(new Table(board, 100, headersList, rowsList).tableToBlocks()).build().getPreview();

        tableString += "\n Total: " + total + " Deadline : " + deadline;
        return tableString;
    }

    public static void resetCache() {
        System.out.println("cache size:" + heuristicCache.size() + " uses time:" + cacheUses + " ario:"
                + ((cacheUses + heuristicCache.size() == 0 ? 0 : cacheUses / heuristicCache.size())));
        cacheUses = 0;
        heuristicCache.clear();
    }

    @Override
    public List<CloudAcoProblemNode> getNeighbourhood(CloudAcoEnvironment cloudAcoEnvironment) {
        return this.currentNode.getNeighbourhood(cloudAcoEnvironment);
    }

    @Override
    public Double getPheromoneTrailValue(CloudAcoProblemNode next, Integer positionInSolution,
                                         CloudAcoEnvironment cloudAcoEnvironment) {
        if (currentNode.getId() == CloudAcoProblemRepresentation.START_NODE_ID) {
            return CloudAcoProblemRepresentation.START_NODE_PHEROMONE;
        }
        double[][] pheromoneMatrix = environment.getPheromoneMatrix();

        return pheromoneMatrix[currentNode.getId()][next.getId()];
    }

    @Override
    public void setPheromoneTrailValue(CloudAcoProblemNode prevNode, Integer current,
                                       CloudAcoEnvironment cloudAcoEnvironment, Double value) {
        environment.getPheromoneMatrix()[prevNode.getId()][current] = value;
    }

    @Override
    public void visitNode(CloudAcoProblemNode visitedNode) {
        if (getCurrentIndex() < getSolution().length) {
            getSolution()[getCurrentIndex()] = visitedNode;
            visitedNode.getResource().setCurrentTask(visitedNode);
            visitedNode.getNode()
                    .setRunTime(visitedNode.getResource().getTaskDuration(visitedNode.getNode()));
            environment.getProblemGraph()
                    .updateChildrenEST(visitedNode.getNode());

//            visitedNode.setVisited(this.environment);
            this.getVisited().put(visitedNode, true);
            this.currentNode = visitedNode;
            setCurrentIndex(getCurrentIndex() + 1);
        }
    }

    @Override
    public void clear() {
        this.setCurrentIndex(0);
        if (getSolution() != null) {
            for (int i = 0; i < getSolution().length; i++) {
                getSolution()[i] = null;
            }
            setUnvisited();
        }
    }

    private void setUnvisited() {
        environment.getProblemGraph().resetNodes();
        currentNode.setUnvisited();
        currentNode = environment.getProblemGraph().getStartNode();
        getVisited().clear();
        environment.getProblemGraph().getInstanceSet().resetPerAnt();
    }

    @Override
    public void doAfterSolutionIsReady(CloudAcoEnvironment environment, ConfigurationProvider configurationProvider) {
        super.doAfterSolutionIsReady(environment, configurationProvider);
        setUnvisited();
    }

    @Override
    public Double getHeuristicValue(CloudAcoProblemNode destination, Integer positionInSolution, CloudAcoEnvironment cloudAcoEnvironment) {

        double curCost = destination.getResource().getCost(destination.getNode());
        double currentDuration = destination.getResource().getTaskDuration(destination.getNode());
        double currentST = Math.max(destination.getResource().getInstanceReleaseTime(), destination.getNode().getEST());
        double currentFT = currentST + currentDuration;

        HeuristicCondition hc = new HeuristicCondition(currentDuration, curCost, currentST, destination.getResource().getId());
        if (heuristicCache.containsKey(hc)) {
            cacheUses++;
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

        for (Map.Entry<Resource, List<CloudAcoResourceInstance>> entry : environment.getProblemGraph().getInstanceSet().getInstances().entrySet()) {
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

        public double getCurStartTime() {
            return curStartTime;
        }

        public void setCurStartTime(double curStartTime) {
            this.curStartTime = curStartTime;
        }

        public int getInstanceId() {
            return instanceId;
        }

        public void setInstanceId(int instanceId) {
            this.instanceId = instanceId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof HeuristicCondition)) return false;
            HeuristicCondition that = (HeuristicCondition) o;
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
