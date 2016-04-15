package edu.sjsu.smartsecure.decisionTree;

import edu.sjsu.smartsecure.dataAccess.DecisionTreeHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mallika on 4/1/16.
 */
public class Algorithm {
    private static DecisionTreeHandler decisionTreeHandler;
    private static DecisionTree decisionTree;

    public static void createDecisionTree(List<String> columnHeaders) {
        decisionTree = DecisionTree.getDecisionTreeInstance();
        decisionTreeHandler = new DecisionTreeHandler();
        Map<String, Long> resultMap = decisionTreeHandler.getCountsOfSafeAndUnsafeData();
        double informationGain = getInformationGain(resultMap);
        Node root = buildDecisionTree(informationGain, columnHeaders, resultMap.get("Total"));
        decisionTree.setRoot(root);
        columnHeaders.remove(root.getNodeId());
        List<String> conditions = new ArrayList<String>();
        conditions.add(root.getNodeId());
        List<String> values = new ArrayList<String>();
        getChildren(root, conditions, columnHeaders, values);
    }

    private static Node buildDecisionTree(double informationGain, List<String> columnHeaders, long totalRecords) {
        String childId = getNode(informationGain, columnHeaders, totalRecords, null, null);
        Node child = new Node(childId);
        child.setAttributes(decisionTreeHandler.getForks(childId));
        return child;
    }

    private static Node buildDecisionTreeChild(double informationGain, List<String> columnHeaders, long totalRecords, List<String> conditions, List<String> values) {
        String childId = getNode(informationGain, columnHeaders, totalRecords, conditions, values);
        Node child = new Node(childId);
        child.setAttributes(decisionTreeHandler.getForks(childId));
        return child;
    }

    private static void getChildren(Node node, List<String> conditions, List<String> columnHeaders, List<String> values) {
        List<String> attributes = node.getAttributes();
        for (int i = 0; i < attributes.size(); i++) {
            values.add(attributes.get(i));
            long totalRecords = decisionTreeHandler.getTotalRecordCountForChild(conditions, values);
            long safeRecords = decisionTreeHandler.getSafeRecordCountForChild(conditions, values, true);
            if (safeRecords == totalRecords) {
                Node child = new Node(node.getNodeId()+"safe");
                child.setResult(-1);
                node.getChildren().add(i, child);
                child.setCorrespondingAttribute(attributes.get(i));
            } else if (safeRecords == 0) {
                Node child = new Node(node.getNodeId()+"unsafe");
                child.setResult(getIntegerValueOfParentNode(node.getNodeId()));
                node.getChildren().add(i, child);
                child.setCorrespondingAttribute(attributes.get(i));
            } else {
                Map<String, Long> resultMap = decisionTreeHandler.getCountsOfSafeAndUnsafeData(conditions, values);
                double informationGain = getInformationGain(resultMap);
                Node child = buildDecisionTreeChild(informationGain, columnHeaders, totalRecords, conditions, values);
                node.getChildren().add(i, child);
                child.setCorrespondingAttribute(attributes.get(i));
                List<String> newColumnHeaders = new ArrayList<String>(columnHeaders);
                columnHeaders.remove(child.getNodeId());
                List<String> newConditions = new ArrayList<String>(conditions);
                newConditions.add(child.getNodeId());
                List<String> newValues = new ArrayList<String>(values);
                getChildren(child, newConditions, newColumnHeaders, newValues);
            }
            values.remove(values.size() - 1);
        }
    }


    private static double getInformationGain(Map<String, Long> resultMap) {
        long total = resultMap.get("Total");
        long safe = resultMap.get("safe");
        long unsafe = resultMap.get("unsafe");
        double log2safe = getLogBase2((float) safe / total);
        double log2unsafe = getLogBase2((float) unsafe / total);
        double informationGain = (float) -1 * ((((float) safe/total) * log2safe) + (((float) unsafe/total) * log2unsafe));
        return informationGain;
    }

    private static String getNode(double informationGain, List<String> attributes, Long totalRecordCount, List<String> conditions, List<String> values) {
        Map<String, Double> entropyMap = new HashMap<String, Double>();
        for (String attribute : attributes) {
            double entropy = calculateEntropy(attribute, totalRecordCount, conditions, values);
            double gain = informationGain - entropy;
            entropyMap.put(attribute, gain);
        }
        return getHighestgain(entropyMap);
    }

    private static String getNode(double informationGain, List<String> attributes) {
        long totalRecordCount = decisionTreeHandler.getTotalRecordCount();
        Map<String, Double> entropyMap = new HashMap<String, Double>();
        for (String attribute : attributes) {
            double entropy = calculateEntropy(attribute, totalRecordCount, null, null);
            double gain = informationGain - entropy;
            entropyMap.put(attribute, gain);
        }
        return getHighestgain(entropyMap);
    }

    private static double calculateEntropy(String attribute, long totalRecordCount, List<String> conditions, List<String> values) {
        Map<String, Double> entropyMap = new HashMap<String, Double>();
        Map<String, Map<String,Long>> attributeMap = decisionTreeHandler.getAttributeCountMap(attribute, conditions, values);
        double infoOutlook = 0;
        for (String keyAttribute : attributeMap.keySet()) {
            Map<String, Long> counts = attributeMap.get(keyAttribute);
            long total = counts.get("Total");
            long yes = counts.get("safe");
            long no = counts.get("unsafe");
            float multiplier = -1 * (float) total/totalRecordCount;
            float safeFactor = (float) yes/total;
            float unsafeFactor = (float) no/total;
            if (safeFactor != 0) {
                safeFactor *= getLogBase2(safeFactor);
            }
            if (unsafeFactor != 0) {
                unsafeFactor *= getLogBase2(unsafeFactor);
            }
            float infofork = multiplier * (safeFactor + unsafeFactor);
            infoOutlook += infofork;
        }
        return infoOutlook;
    }

    private static double getLogBase2(float num) {
        return Math.log10(num) / Math.log10(2);
    }

    private static String getHighestgain(Map<String, Double> entropyMap) {
        Double highest = null;
        String root = "";
        for(String attribute : entropyMap.keySet()) {
            if (highest == null || entropyMap.get(attribute) > highest) {
                highest = entropyMap.get(attribute);
                root = attribute;
            }
        }
        return root;
    }

    private static Integer getIntegerValueOfParentNode(String parentId) {
        if (parentId.equals("Outlook")) {
            return 100;
        } else if (parentId.equals("temperature")) {
            return 200;
        } else if (parentId.equals("humidity")) {
            return 300;
        } else if (parentId.equals("wind")) {
            return 400;
        } else if (parentId.equals("appName")) {
            return 1;
        } else if (parentId.equals("network")) {
            return 2;
        } else if (parentId.equals("datausage")) {
            return 3;
        } else if (parentId.equals("dayOfTheWeek")) {
            return 4;
        } else if (parentId.equals("timeOfTheDay")) {
            return 5;
        } else if (parentId.equals("demographic")) {
            return 6;
        } else if (parentId.equals("frequency")) {
            return 7;
        } else if (parentId.equals("frequentLocation")) {
            return 8;
        }
        return 1000;
    }


}
