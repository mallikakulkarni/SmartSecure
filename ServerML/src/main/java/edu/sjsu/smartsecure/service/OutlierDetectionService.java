package edu.sjsu.smartsecure.service;

import edu.sjsu.smartsecure.decisionTree.DecisionTree;
import org.json.JSONObject;

/**
 * Created by mallika on 4/8/16.
 */
public class OutlierDetectionService {
    DecisionTree decisionTree;

    public OutlierDetectionService() {
        decisionTree = DecisionTree.getDecisionTreeInstance();
    }

    public boolean getSafeUnsafeResult(JSONObject jsonObject) {
        return decisionTree.processTestData(jsonObject);
    }

    public boolean verifyTestData(JSONObject jsonObject) {
        boolean testResult = (Boolean) jsonObject.get("class");
        jsonObject.remove("class");
        boolean actResult = decisionTree.processTestData(jsonObject);
        return testResult == actResult;
    }

}
