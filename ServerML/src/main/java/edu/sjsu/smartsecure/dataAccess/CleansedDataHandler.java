package edu.sjsu.smartsecure.dataAccess;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import org.bson.BSONObject;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.logging.Logger;

/**
 * Created by mallika on 4/4/16.
 */
public class CleansedDataHandler {
    private DBCollection collection;

    public CleansedDataHandler() {
        collection = MongoFactory.getCollection("TrainingData");
    }
    final static org.slf4j.Logger decisionTreeLog = LoggerFactory.getLogger("decisionTree");
    public void insertIntoNewCleansedCollection(List<Integer> result, JSONObject jsonObject) {
        JSONArray jsonArray = (JSONArray)  jsonObject.get("realtimedata");
        int count = 0;
        Iterator<?> iterator = jsonArray.iterator();
        while (iterator.hasNext()) {
            JSONObject appDetails = (JSONObject) iterator.next();
            boolean res = processResult(result.get(count));
            count++;
            appDetails.put("class", res);
            decisionTreeLog.debug("Inserting into Training data "+appDetails.toString());
            DBObject dbObject = new BasicDBObject();
            Iterator<?> keysIterator = appDetails.keys();
            while (keysIterator.hasNext()) {
                String key = (String) keysIterator.next();
                dbObject.put(key, appDetails.get(key));
            }
            decisionTreeLog.debug("dbObject " + dbObject.toString());
            collection.insert(dbObject);
            decisionTreeLog.debug("Inserted");
        }
    }

    private boolean processResult(int result) {
        return result == -1 ? true : false;
    }

}
