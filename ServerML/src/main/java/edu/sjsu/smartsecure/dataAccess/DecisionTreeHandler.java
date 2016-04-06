package edu.sjsu.smartsecure.dataAccess;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by mallika on 4/5/16.
 */
public class DecisionTreeHandler {

    private Map<String, Long> getCountsOfSafeAndUnsafeData() {
        DBCollection collection = MongoFactory.getCollection("TrainingData");
        Map<String, Long> recordMap = new HashMap<String, Long>();
        recordMap.put("Total", getTotalRecordCount(collection));
        recordMap.put("Safe", getQueryRecordCount(collection, true));
        recordMap.put("Unsafe", getQueryRecordCount(collection, false));
        return recordMap;
    }

    private long getTotalRecordCount(DBCollection collection) {
        return collection.count();
    }

    private long getQueryRecordCount(DBCollection collection, boolean isSafe) {
        DBObject query = new BasicDBObject("isSafe", isSafe);
        return collection.count(query);
    }
}
