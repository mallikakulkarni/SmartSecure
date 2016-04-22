package edu.sjsu.smartsecure.service;

import edu.sjsu.smartsecure.dataAccess.CleansedDataHandler;
import edu.sjsu.smartsecure.decisionTree.DecisionTree;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by mallika on 4/8/16.
 */
public class OutlierDetectionService {
    DecisionTree decisionTree;

    public OutlierDetectionService() {
        decisionTree = DecisionTree.getDecisionTreeInstance();
    }
    static Logger decisionTreeLog = LoggerFactory.getLogger("decisionTree");
    public List<String> getSafeUnsafeResult(JSONObject jsonObject) {
        decisionTreeLog.debug("Processing input through decision tree");
        List<String> result = decisionTree.processTestData(jsonObject);
        CleansedDataHandler cleansedDataHandler = new CleansedDataHandler();
        cleansedDataHandler.insertIntoNewCleansedCollection(result, jsonObject);
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < result.size(); i++) {
            list.add(result.get(i));
        }
        truncateResultList(list);
        decisionTreeLog.debug("Result Object "+list.toString());
        return list;
    }

    public List<String> truncateResultList(List<String> resultList) {
        int i = 0;
        Set<String> dictionary = new HashSet<String>();
        while (i < resultList.size()) {
            String result = resultList.get(i);
            if (result.equals("Safe") || dictionary.contains(result)) {
                resultList.remove(i);
            } else {
                dictionary.add(result);
                i++;
            }
        }
        return resultList;
    }

}
