package edu.sjsu.smartsecure.decisionTree;

import edu.sjsu.smartsecure.NodeDictionary;
import edu.sjsu.smartsecure.dataAccess.DecisionTreeHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mallika on 4/1/16.
 */
public class Algorithm {
    private static DecisionTreeHandler decisionTreeHandler;

    public static void createDecisionTree(List<String> attributes) {
        decisionTreeHandler = new DecisionTreeHandler();
        Map<String, Long> resultMap = decisionTreeHandler.getCountsOfSafeAndUnsafeData();
        double informationGain = getInformationGain(resultMap);
        String rootId = getRoot(informationGain, attributes);
        Node root = new Node(rootId);
        root.setForks(decisionTreeHandler.getForks(rootId));

    }

    private static double getInformationGain(Map<String, Long> resultMap) {
        long total = resultMap.get("Total");
        long safe = resultMap.get("Safe");
        long unsafe = resultMap.get("Unsafe");
        double log2safe = getLogBase2(safe / total);
        double log2unsafe = getLogBase2(unsafe / total);
        double informationGain = (double) -1 * (((safe/total) * log2safe) + ((unsafe/total) * log2unsafe));
        return informationGain;
    }

    private static String getRoot(double informationGain, List<String> attributes) {
        Map<String, Double> entropyMap = new HashMap<String, Double>();
        for (String attribute : attributes) {
            double entropy = calculateEntropy(attribute);
            double gain = informationGain - entropy;
            entropyMap.put(attribute, gain);
        }
        return getHighestgain(entropyMap);
    }

    private static double calculateEntropy(String attribute) {
        long totalRecordCount = decisionTreeHandler.getTotalRecordCount();
        Map<String, Double> entropyMap = new HashMap<String, Double>();
        Map<String, Map<String,Long>> attributeMap = decisionTreeHandler.getAttributeCountMap(attribute);
        double infoOutlook = 0;
        for (String keyAttribute : attributeMap.keySet()) {
            Map<String, Long> counts = attributeMap.get(keyAttribute);
            long total = counts.get("Total");
            long yes = counts.get("Yes");
            long no = counts.get("No");
            double infofork = (total/totalRecordCount) * -1 * (((yes/total) * getLogBase2(yes/total)) + ((no/total) * getLogBase2(no/total)));
            long multiplier = total/totalRecordCount;
            infoOutlook += (multiplier * infofork);
        }
        return infoOutlook;
    }

    private static double getLogBase2(double num) {
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


}
