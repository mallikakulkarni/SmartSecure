package edu.sjsu.smartsecure.dataAccess;

import com.mongodb.*;

import java.util.*;

/**
 * Created by mallika on 4/5/16.
 */
public class DecisionTreeHandler {
    private long totalRecordCount;
    private DBCollection collection;

    public DecisionTreeHandler() {
        collection = MongoFactory.getCollection("TrainingData");
    }

    private long getTotalRecordCount(DBCollection collection, BasicDBObject query) {
        return collection.count(query);
    }

    private long getQueryRecordCount(DBCollection collection, BasicDBObject query) {
        return collection.count(query);
    }

    public long getTotalRecordCount() {
        totalRecordCount = collection.count();
        return totalRecordCount;
    }

    public Map<String, Map<String,Long>> getAttributeCountMap(String attribute, List<String> conditions, List<String> values) {
        Map<String, Map<String, Long>> resultMap = new HashMap<String, Map<String, Long>>();
        List<String> cursor = collection.distinct(attribute);
        BasicDBObject query = null;
        List<BasicDBObject> parentConditions = new ArrayList<BasicDBObject>();
        if (conditions != null && values != null) {
            for (int i = 0; i < conditions.size(); i++) {
                BasicDBObject condition = new BasicDBObject(conditions.get(i), values.get(i));
                parentConditions.add(condition);
            }
        }
        for (String value : cursor) {
            List<BasicDBObject> conditionList = new ArrayList<BasicDBObject>();
            if (parentConditions != null) {
                conditionList.addAll(parentConditions);
            }
            conditionList.add(new BasicDBObject(attribute, value));
            query = new BasicDBObject("$and", conditionList);
            long total = getTotalRecordCount(collection, query);
            if (total == 0) {
                continue;
            }
            conditionList.add(new BasicDBObject("class", true));
            query = new BasicDBObject("$and", conditionList);
            long safe = getQueryRecordCount(collection, query);
            conditionList.remove(conditionList.size() - 1);
            conditionList.add(new BasicDBObject("class", false));
            query = new BasicDBObject("$and", conditionList);
            long unsafe = getQueryRecordCount(collection, query);
            Map<String, Long> valueMap = new HashMap<String, Long>();
            valueMap.put("Total", total);
            valueMap.put("safe", safe);
            valueMap.put("unsafe", unsafe);
            resultMap.put(value, valueMap);
        }
        return resultMap;
    }



    public List<String> getForks(String attribute) {
        return collection.distinct(attribute);
    }

    public Map<String, Long> getCountsOfSafeAndUnsafeData() {
        Map<String, Long> recordMap = new HashMap<String, Long>();
        List<BasicDBObject> conditionList = new ArrayList<BasicDBObject>();
        BasicDBObject query = new BasicDBObject();
        recordMap.put("Total", getTotalRecordCount(collection, query));
        conditionList.add(new BasicDBObject("class", true));
        query = new BasicDBObject("$and", conditionList);
        recordMap.put("safe", getQueryRecordCount(collection, query));
        conditionList.remove(conditionList.size()-1);
        conditionList.add(new BasicDBObject("class", false));
        query = new BasicDBObject("$and", conditionList);
        recordMap.put("unsafe", getQueryRecordCount(collection, query));
        return recordMap;
    }


    public Map<String, Long> getCountsOfSafeAndUnsafeData(List<String> conditions, List<String> values) {
        Map<String, Long> recordMap = new HashMap<String, Long>();
        List<BasicDBObject> conditionList = new ArrayList<BasicDBObject>();
        for (int i = 0; i < conditions.size(); i++) {
            conditionList.add(new BasicDBObject(conditions.get(i), values.get(i)));
        }
        BasicDBObject query = new BasicDBObject("$and", conditionList);
        recordMap.put("Total", getTotalRecordCount(collection, query));
        conditionList.add(new BasicDBObject("class", true));
        query = new BasicDBObject("$and", conditionList);
        recordMap.put("safe", getQueryRecordCount(collection, query));
        conditionList.remove(conditionList.size()-1);
        conditionList.add(new BasicDBObject("class", false));
        query = new BasicDBObject("$and", conditionList);
        recordMap.put("unsafe", getQueryRecordCount(collection, query));
        return recordMap;
    }

    public long getTotalRecordCountForChild(List<String> conditions, List<String> values) {
        BasicDBObject query;
        List<BasicDBObject> conditionList = new ArrayList<BasicDBObject>();
        for (int i = 0; i < conditions.size(); i++) {
            query = new BasicDBObject(conditions.get(i), values.get(i));
            conditionList.add(query);
        }
        query = new BasicDBObject("$and", conditionList);
        return getQueryRecordCount(collection, query);
    }

    public long getSafeRecordCountForChild(List<String> conditions, List<String> values, boolean isSafe) {
        BasicDBObject query = new BasicDBObject();
        List<BasicDBObject> conditionList = new ArrayList<BasicDBObject>();
        for (int i = 0; i < conditions.size(); i++) {
            query = new BasicDBObject(conditions.get(i), values.get(i));
            conditionList.add(query);
        }
        conditionList.add(new BasicDBObject("class", isSafe));
        query = new BasicDBObject("$and", conditionList);
        return getQueryRecordCount(collection, query);
    }

}
