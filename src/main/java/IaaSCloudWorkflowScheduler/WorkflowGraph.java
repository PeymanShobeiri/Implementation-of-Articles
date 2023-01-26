package IaaSCloudWorkflowScheduler;

import DAG.Adag;
import DAG.Adag.Child;
import DAG.Adag.Child.Parent;
import DAG.Adag.Job;
import DAG.FilenameType;
import DAG.LinkageType;

import java.util.HashMap;
import java.util.Map;

public class WorkflowGraph {
    private final String startNodeId = "start";
    private final String endNodeId = "end";
    private final int jobNumPE = 1;
    //private Map<String, WorkflowNode>  nodes;
    public Map<String, WorkflowNode> nodes;
    private int nodeNum;
    private int maxParallel = 0;


    public WorkflowGraph() {
        nodes = new HashMap<String, WorkflowNode>();
        nodeNum = 0;
    }

    public boolean convertDagToWorkflowGraph(Adag dag) {
        long inputFilesSize, outputFilesSize, IOsize;
        int runTime;

        nodes.clear();
        if (dag == null)
            return (false);
        for (Job job : dag.getJobList()) {
            inputFilesSize = outputFilesSize = 0;
            for (FilenameType file : job.getUseList()) {
                if (file.getLink() == LinkageType.INPUT && Long.valueOf(file.getSize()) > 0)
                    inputFilesSize += Long.valueOf(file.getSize());
                else if (file.getLink() == LinkageType.OUTPUT && Long.valueOf(file.getSize()) > 0)
                    outputFilesSize += Long.valueOf(file.getSize());
            }

            //just for test
            //inputFilesSize = outputFilesSize = 0;

            runTime = Math.round(Float.valueOf(job.getRuntime()));
            if (runTime <= 0)
                runTime = 1;
            WorkflowNode wfNode = new WorkflowNode(job.getId(), job.getName(), inputFilesSize, outputFilesSize, runTime);

            //temporary
            wfNode.setInstructionSize(runTime * Constants.STANDARD_MIPS);
            wfNode.setNumPE(jobNumPE);
            nodes.put(job.getId(), wfNode);
        }

        for (Child child : dag.getChildList()) {
            String childId = child.getRef();
            if (!nodes.containsKey(childId)) {
                System.out.println("id= " + childId + " doesn't exist!");
                return (false);
            }
            for (Parent parent : child.getParentList()) {
                IOsize = computeIOsize(dag, parent.getRef(), childId);

                //just for test
                //IOsize = 0;

                nodes.get(childId).addParent(parent.getRef(), IOsize);
                nodes.get(parent.getRef()).addChild(childId, IOsize);
            }
        }

        WorkflowNode startNode = new WorkflowNode(startNodeId, startNodeId, 0, 0, 0);
        WorkflowNode endNode = new WorkflowNode(endNodeId, endNodeId, 0, 0, 0);
        startNode.setInstructionSize(0);
        endNode.setInstructionSize(0);
        startNode.setNumPE(0);
        endNode.setNumPE(0);
        for (WorkflowNode node : nodes.values()) {
            if (!node.hasParent()) {
                startNode.addChild(node.getId(), 0);
                node.addParent(startNode.getId(), 0);
            }
            if (!node.hasChild()) {
                node.addChild(endNode.getId(), 0);
                endNode.addParent(node.getId(), 0);
            }
        }
        nodes.put(startNodeId, startNode);
        nodes.put(endNodeId, endNode);
        nodeNum = nodes.size();

        unifyRunTimes();

        return (true);
    }

    public void unifyRunTimes() {
        HashMap<String, counter> jobTypes = new HashMap<String, counter>();

        for (WorkflowNode node : nodes.values()) {
            String curJob = node.getName();
            if (jobTypes.containsKey(curJob))
                jobTypes.get(curJob).add(node.getInstructionSize());
            else {
                counter c = new counter(node.getInstructionSize());
                jobTypes.put(curJob, c);
            }
        }

        for (counter c : jobTypes.values())
            c.computeMean();

        for (WorkflowNode node : nodes.values()) {
            String curJob = node.getName();
            node.setInstructionSize(jobTypes.get(curJob).getMean());
        }
    }

    private long computeIOsize(Adag dag, String parentId, String childId) {
        long size = 0;

        for (Job parentJob : dag.getJobList()) {
            if (parentJob.getId().equals(parentId)) {
                for (Job childJob : dag.getJobList()) {
                    if (childJob.getId().equals(childId)) {
                        for (FilenameType outFile : parentJob.getUseList()) {
                            if (outFile.getLink() == LinkageType.OUTPUT) {
                                for (FilenameType inFile : childJob.getUseList()) {
                                    if (inFile.getLink() == LinkageType.INPUT && inFile.getFile().equals(outFile.getFile())) {
                                        double curSize = Long.valueOf(inFile.getSize());
                                        if (curSize > 0)
                                            size += curSize;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return (size);
    }

    public int getMaxParallel() {
        return maxParallel;
    }

    public void setMaxParallel(int maxParallel) {
        this.maxParallel = maxParallel;
    }

    public Map<String, WorkflowNode> getNodes() {
        return (nodes);
    }

    public void setNodes(Map<String, WorkflowNode> newNodes) {
        nodes = newNodes;
    }

    public String getStartId() {
        return (startNodeId);
    }

    public String getEndId() {
        return (endNodeId);
    }

    public int getNodeNum() {
        return (nodeNum);
    }

    private class counter {
        private long meanInstSize;
        private int maxInstSize;
        private int no;

        public counter() {
            maxInstSize = -1;
            meanInstSize = 0;
            no = 0;
        }

        public counter(int instSize) {
            maxInstSize = instSize;
            meanInstSize = instSize;
            no = 1;
        }

        public void add(int instSize) {
            meanInstSize += instSize;
            if (instSize > maxInstSize)
                maxInstSize = instSize;
            no++;
        }

        public int computeMean() {
            meanInstSize /= no;
            return ((int) meanInstSize);
        }

        public int getMean() {
            return ((int) meanInstSize);
        }

        public int getMax() {
            return (maxInstSize);
        }
    }

}
