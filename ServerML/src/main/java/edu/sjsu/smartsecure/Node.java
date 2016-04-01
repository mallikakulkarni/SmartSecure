package edu.sjsu.smartsecure;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mallika on 3/27/16.
 */
public class Node {
    private String nodeId;
    private float probability;
    private List<Node> forks;

    public Node(String nodeId) {
        probability = 0;
        this.nodeId = nodeId;
        forks = new ArrayList<Node>();
    }

    public Node(String nodeId, float probability) {
        this.nodeId = nodeId;
        this.probability = probability;
        forks = new ArrayList<Node>();
    }

    public float getProbability() {
        return probability;
    }

    public void setProbability(float probability) {
        this.probability = probability;
    }

    public List<Node> getForks() {
        return forks;
    }

    public void setForks(List<Node> forks) {
        this.forks = forks;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
}
