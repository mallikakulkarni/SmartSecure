package edu.sjsu.smartsecure.dataAccess;

import com.mongodb.*;

import java.util.*;

/**
 * Created by mallika on 4/5/16.
 */
public class DecisionTreeHandler {
    private long totalRecordCount;


    private long getTotalRecordCount(DBCollection collection, BasicDBObject query) {
        return collection.count(query);
    }

    private long getQueryRecordCount(DBCollection collection, BasicDBObject query) {
        return collection.count(query);
    }

    public long getTotalRecordCount() {
        DBCollection collection = MongoFactory.getCollection("MallikaTrainingData");
        totalRecordCount = collection.count();
        return totalRecordCount;
    }

    public Map<String, Map<String,Long>> getAttributeCountMap(String attribute) {
        Map<String, Map<String, Long>> resultMap = new HashMap<String, Map<String, Long>>();
        DBCollection collection = MongoFactory.getCollection("MallikaTrainingData");
        List<String> cursor = collection.distinct(attribute);
        BasicDBObject query = null;
        for (String value : cursor) {
            query = new BasicDBObject(attribute, value);
            long total = getTotalRecordCount(collection, query);
            List<BasicDBObject> conditionList = new ArrayList<BasicDBObject>();
            conditionList.add(query);
            conditionList.add(new BasicDBObject("class", true));
            query = new BasicDBObject("$and", conditionList);
            long safe = getQueryRecordCount(collection, query);
            conditionList.remove(conditionList.size() - 1);
            conditionList.add(new BasicDBObject("class", false));
            query = new BasicDBObject("$and", conditionList);
            long unsafe = getQueryRecordCount(collection, query);
            Map<String, Long> valueMap = new HashMap<String, Long>();
            valueMap.put("Total", total);
            valueMap.put("Safe", safe);
            valueMap.put("Unsafe", unsafe);
            resultMap.put(value, valueMap);
        }
        return resultMap;
    }



    public List<String> getForks(String attribute) {
        return MongoFactory.getCollection("MallikaTrainingData").distinct(attribute);
    }

    public Map<String, Long> getCountsOfSafeAndUnsafeData() {
        DBCollection collection = MongoFactory.getCollection("MallikaTrainingData");
        Map<String, Long> recordMap = new HashMap<String, Long>();
        List<BasicDBObject> conditionList = new ArrayList<BasicDBObject>();
        BasicDBObject query = new BasicDBObject();
        recordMap.put("Total", getTotalRecordCount(collection, query));
        conditionList.add(new BasicDBObject("class", true));
        query = new BasicDBObject("$and", conditionList);
        recordMap.put("Safe", getQueryRecordCount(collection, query));
        conditionList.remove(conditionList.size()-1);
        conditionList.add(new BasicDBObject("class", false));
        query = new BasicDBObject("$and", conditionList);
        recordMap.put("Unsafe", getQueryRecordCount(collection, query));
        return recordMap;
    }

    public Map<String, Long> getCountsOfSafeAndUnsafeData(List<String> conditions, List<String> values) {
        DBCollection collection = MongoFactory.getCollection("MallikaTrainingData");
        Map<String, Long> recordMap = new HashMap<String, Long>();
        List<BasicDBObject> conditionList = new ArrayList<BasicDBObject>();
        for (int i = 0; i < conditions.size(); i++) {
            conditionList.add(new BasicDBObject(conditions.get(i), values.get(i)));
        }
        BasicDBObject query = new BasicDBObject("$and", conditionList);
        recordMap.put("Total", getTotalRecordCount(collection, query));
        conditionList.add(new BasicDBObject("class", true));
        query = new BasicDBObject("$and", conditionList);
        recordMap.put("Safe", getQueryRecordCount(collection, query));
        conditionList.remove(conditionList.size()-1);
        conditionList.add(new BasicDBObject("class", false));
        query = new BasicDBObject("$and", conditionList);
        recordMap.put("Unsafe", getQueryRecordCount(collection, query));
        return recordMap;
    }

    public long getTotalRecordCountForChild(List<String> conditions, List<String> values) {
        DBCollection collection = MongoFactory.getCollection("MallikaTrainingData");
        BasicDBObject query = new BasicDBObject();
        List<BasicDBObject> conditionList = new ArrayList<BasicDBObject>();
        for (int i = 0; i < conditions.size(); i++) {
            query = new BasicDBObject(conditions.get(i), values.get(i));
            conditionList.add(query);
        }
        query = new BasicDBObject("$and", conditionList);
        return getQueryRecordCount(collection, query);
    }

    public long getSafeRecordCountForChild(List<String> conditions, List<String> values, boolean isSafe) {
        DBCollection collection = MongoFactory.getCollection("MallikaTrainingData");
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
