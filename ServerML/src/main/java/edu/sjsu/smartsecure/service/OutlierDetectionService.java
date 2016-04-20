package edu.sjsu.smartsecure.service;

import edu.sjsu.smartsecure.dataAccess.CleansedDataHandler;
import edu.sjsu.smartsecure.decisionTree.DecisionTree;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by mallika on 4/8/16.
 */
public class OutlierDetectionService {
    DecisionTree decisionTree;

    public OutlierDetectionService() {
        decisionTree = DecisionTree.getDecisionTreeInstance();
    }
    static Logger decisionTreeLog = LoggerFactory.getLogger("decisionTree");
    public JSONObject getSafeUnsafeResult(JSONObject jsonObject) {
        decisionTreeLog.debug("Processing input through decision tree");
        List<Integer> result = decisionTree.processTestData(jsonObject);
        CleansedDataHandler cleansedDataHandler = new CleansedDataHandler();
        cleansedDataHandler.insertIntoNewCleansedCollection(result, jsonObject);
        JSONObject resultObject = new JSONObject();
        for (int i = 0; i < result.size(); i++) {
            resultObject.put(""+i, result.get(i));
        }
        decisionTreeLog.debug("Result Object "+resultObject.toString());
        return resultObject;
    }

    public boolean verifyTestData(JSONObject jsonObject) {
        boolean testResult = (Boolean) jsonObject.get("class");
        jsonObject.remove("class");
        List<Integer> list = decisionTree.processTestData(jsonObject);
        for (int actResult : list) {
            if (actResult == -1 && testResult == true) {
                return true;
            } else if (actResult != -1 && testResult == true) {
                return false;
            } else if (actResult == -1 && testResult == false) {
                return false;
            }
        }
        return true;
    }

}
