package IaaSCloudWorkflowScheduler;

import java.util.ArrayList;
import java.util.List;

public class WorkflowNode {
    private String id, name;
    private long inputFileSize, outputFileSize;
    private int instructionSize; //in MI
    private double runTime;
    private int topologicaSortCount = 0;
    private int numPE;
    private List<Link> parents, children;

    //these will be filled in scheduling time
    private boolean scheduled;
    private int EST, EFT, AST, AFT, LFT, LST, MET;
    private int upRank;
    private int subDeadline;
    private int selectedResource = -1;

    public WorkflowNode(String nodeId) {
        id = new String(nodeId);
        name = new String();
        inputFileSize = outputFileSize = 0;
        runTime = instructionSize = 0;
        parents = new ArrayList<Link>();
        children = new ArrayList<Link>();
        scheduled = false;
    }

    public WorkflowNode(String nodeId, String nodeName, long inSize, long outSize, int rt) {
        id = new String(nodeId);
        name = new String(nodeName);
        inputFileSize = inSize;
        outputFileSize = outSize;
        runTime = rt;
        parents = new ArrayList<Link>();
        children = new ArrayList<Link>();
        scheduled = false;
    }

    public WorkflowNode(WorkflowNode node) {
        id = new String(node.id);
        name = new String(node.name);
        inputFileSize = node.inputFileSize;
        outputFileSize = node.outputFileSize;
        runTime = node.runTime;
        instructionSize = node.instructionSize;
        scheduled = node.scheduled;
        EST = node.EST;
        EFT = node.EFT;
        LST = node.LST;
        AST = node.AST;
        LFT = node.LFT;
        MET = node.MET;
        upRank = node.upRank;
        subDeadline = node.subDeadline;
        selectedResource = node.selectedResource;
        parents = new ArrayList<Link>();
        children = new ArrayList<Link>();
        for (Link link : node.parents)
            parents.add(new Link(link.getId(), link.getDataSize()));
        for (Link link : node.children)
            children.add(new Link(link.getId(), link.getDataSize()));
    }


    public int getTopologicaSortCount() {
        return topologicaSortCount;
    }

    public void setTopologicaSortCount(int topologicaSortCount) {
        this.topologicaSortCount = topologicaSortCount;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        name = newName;
    }

    public long getInputFileSize() {
        return (inputFileSize);
    }

    public void setInputFileSize(long newSize) {
        if (newSize >= 0)
            inputFileSize = newSize;
    }

    public long getOutputFileSize() {
        return (outputFileSize);
    }

    public void setOutputFileSize(long newSize) {
        if (newSize >= 0)
            outputFileSize = newSize;
    }

    public double getRunTime() {
        return (runTime);
    }

    public void setRunTime(double newRunTime) {
        if (newRunTime >= 0)
            runTime = newRunTime;
    }

    public int getNumPE() {
        return (numPE);
    }

    public void setNumPE(int n) {
        numPE = n;
    }

    public void addParent(String parentId, long size) {
        parents.add(new Link(parentId, size));
    }

    public void addChild(String childId, long size) {
        children.add(new Link(childId, size));
    }

    public ArrayList<Link> getParents() {
        return (ArrayList<Link>) parents;
    }

    public ArrayList<Link> getChildren() {
        return (ArrayList<Link>) children;
    }

    public boolean hasChild() {
        if (children.size() == 0) return (false);
        else return (true);
    }


    public boolean hasParent() {
        if (parents.size() == 0) return (false);
        else return (true);
    }

    public boolean isScheduled() {
        return (scheduled);
    }

    public void setScheduled() {
        scheduled = true;
    }

    public void setUnscheduled() {
        scheduled = false;
    }

    public int getEST() {
        return EST;
    }

    public void setEST(int EST) {
        this.EST = EST;
    }

    public int getEFT() {
        return EFT;
    }

    public void setEFT(int EFT) {
        this.EFT = EFT;
    }

    public int getAST() {
        return AST;
    }

    public void setAST(int AST) {
        this.AST = AST;
    }

    public int getAFT() {
        return AFT;
    }

    public void setAFT(int AFT) {
        this.AFT = AFT;
    }

    public int getLFT() {
        return LFT;
    }

    public void setLFT(int LFT) {
        this.LFT = LFT;
    }

    public int getLST() {
        return LST;
    }

    public void setLST(int LST) {
        this.LST = LST;
    }

    public int getSelectedResource() {
        return (selectedResource);
    }

    public void setSelectedResource(int resIndex) {
        selectedResource = resIndex;
    }

    public int getMET() {
        return (MET);
    }

    public void setMET(int time) {
        MET = time;
    }

    public int getInstructionSize() {
        return (instructionSize);
    }

    public void setInstructionSize(int size) {
        instructionSize = size;
    }

    public int getUpRank() {
        return (upRank);
    }

    public void setUpRank(int ur) {
        upRank = ur;
    }

    public int getDeadline() {
        return subDeadline;
    }

    public void setDeadline(int d) {
        subDeadline = d;
    }

    public long getChildDataSize(String childId) {
        for (Link child : children) {
            if (child.getId().equals(childId))
                return (child.getDataSize());
        }
        return (0);
    }

    public long getParentDataSize(String parentId) {
        for (Link parent : parents) {
            if (parent.getId().equals(parentId))
                return (parent.getDataSize());
        }
        return (0);
    }

}
