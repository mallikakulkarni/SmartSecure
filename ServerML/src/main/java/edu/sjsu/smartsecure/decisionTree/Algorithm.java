package edu.sjsu.smartsecure.decisionTree;

import edu.sjsu.smartsecure.NodeDictionary;
import edu.sjsu.smartsecure.dataAccess.DecisionTreeHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mallika on 4/1/16.
 */
public class Algorithm {

    public static void createDecisionTree() {
        DecisionTreeHandler decisionTreeHandler = new DecisionTreeHandler();
        Map<String, Long> resultMap = new HashMap<String, Long>();
        double informationGain = getInformationGain(resultMap);
        String rootId = getRoot(informationGain);
    }

    private static double getInformationGain(Map<String, Long> resultMap) {
        long total = resultMap.get("Total");
        long safe = resultMap.get("Safe");
        long unsafe = resultMap.get("Unsafe");
        double log2safe = Math.log10(safe/total) / Math.log10(2);
        double log2unsafe = Math.log10(unsafe/total) / Math.log10(2);
        double informationGain = (double) (((safe/total) * log2safe) + ((unsafe/total) * log2unsafe));
        return informationGain;
    }

    private static String getRoot(double informationGain) {
        op
        return "";
    }


}
