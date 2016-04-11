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

    public JSONObject getSafeUnsafeResult(JSONObject jsonObject) {
        int result = decisionTree.processTestData(jsonObject);
        JSONObject resultObject = new JSONObject();
        resultObject.put("MongoOid", jsonObject.get("MongoOid"));
        resultObject.put("result", result);
        return resultObject;
    }

    public boolean verifyTestData(JSONObject jsonObject) {
        boolean testResult = (Boolean) jsonObject.get("class");
        jsonObject.remove("class");
        int actResult = decisionTree.processTestData(jsonObject);
        if (actResult == -1 && testResult == true) {
            return true;
        } else if (actResult != -1 && testResult == true) {
            return false;
        } else if (actResult == -1 && testResult == false) {
            return false;
        }
        return true;
    }

}
