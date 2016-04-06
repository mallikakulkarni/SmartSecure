package edu.sjsu.smartsecure.decisionTree;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mallika on 3/27/16.
 */
public class Node {
    private String nodeId;
    private List<String> attributes;

    public Node(String nodeId) {
        this.nodeId = nodeId;
        attributes = new ArrayList<String>();
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setForks(List<String> attributes) {
        this.attributes = attributes;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
}
