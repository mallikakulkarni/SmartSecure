package edu.sjsu.smartsecure.dataAccess;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by mallika on 4/5/16.
 */
public class DecisionTreeHandler {
    private long totalRecordCount;

    public Map<String, Long> getCountsOfSafeAndUnsafeData() {
        DBCollection collection = MongoFactory.getCollection("TrainingData");
        Map<String, Long> recordMap = new HashMap<String, Long>();
        recordMap.put("Total", getTotalRecordCount(collection));
        recordMap.put("Safe", getQueryRecordCount(collection, true));
        recordMap.put("Unsafe", getQueryRecordCount(collection, false));
        return recordMap;
    }

    private long getTotalRecordCount(DBCollection collection) {
        totalRecordCount = collection.count();
        return totalRecordCount;
    }

    private long getQueryRecordCount(DBCollection collection, boolean isSafe) {
        DBObject query = new BasicDBObject("isSafe", isSafe);
        return collection.count(query);
    }

    public long getTotalRecordCount() {
        return totalRecordCount;
    }

    public Map<String, Map<String,Long>> getAttributeCountMap(String attribute) {
        return null;
    }

    public List<String> getForks(String attribute) {
        return null;
    }
}
