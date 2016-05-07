package edu.sjsu.smartsecure.decisionTree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mallika on 3/27/16.
 */
public class Node {
    static Logger decisionTreeLog = LoggerFactory.getLogger("decisionTree");
    private String nodeId;
    private List<String> attributes;
    private List<Node> children;
    private Integer result;
    private Node parent;
    private String correspondingAttribute;
    private String column;
    private List<String> safeChildList;
    private List<String> unsafeChildList;

    public Node(String nodeId) {
        this.nodeId = nodeId;
        attributes = new ArrayList<String>();
        children = new ArrayList<Node>();
        result = null;
        parent = null;
        safeChildList = null;
        unsafeChildList = null;
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

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public List<String> getSafeChildList() {
        return safeChildList;
    }

    public void setSafeChildList(List<String> apps) {
        this.safeChildList = apps;
    }

    public List<String> getUnsafeChildList() {
        return unsafeChildList;
    }

    public void setUnsafeChildList(List<String> apps) {
        this.unsafeChildList = apps;
    }

    public void printAttributes() {
        decisionTreeLog.debug("nodeId: " + nodeId != null ? nodeId : "Empty");
        decisionTreeLog.debug("result: " + result != null ? nodeId : "Empty");
        decisionTreeLog.debug("parent: " + parent.getNodeId() != null ? nodeId : "Empty");
        decisionTreeLog.debug("correspondingAttribute: " + correspondingAttribute != null ? nodeId : "Empty");
        decisionTreeLog.debug("column: " + column != null ? nodeId : "Empty");
        for (String attribute : attributes) {
            decisionTreeLog.debug("attribute: " + attribute != null ? nodeId : "Empty");
        }
    }
}
