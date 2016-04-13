package edu.sjsu.smartsecure.decisionTree;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mallika on 3/27/16.
 */
public class Node {
    private String nodeId;
    private List<String> attributes;
    private List<Node> children;
    private Integer result;
    private Node parent;
    private String correspondingAttribute;

    public Node(String nodeId) {
        this.nodeId = nodeId;
        attributes = new ArrayList<String>();
        children = new ArrayList<Node>();
        result = null;
        parent = null;

    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public String getCorrespondingAttribute() {
        return correspondingAttribute;
    }

    public void setCorrespondingAttribute(String correspondingAttribute) {
        this.correspondingAttribute = correspondingAttribute;
    }
}
