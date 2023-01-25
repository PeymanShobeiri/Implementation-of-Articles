package IaaSCloudWorkflowScheduler;

public class Link {
    String id;
    long dataSize;

    public Link(String newId, long newSize) {
        id = newId;
        dataSize = newSize;

    }

    public long getDataSize() {
        return (dataSize);
    }

    public String getId() {
        return (id);
    }
}