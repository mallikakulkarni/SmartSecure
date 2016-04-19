package edu.sjsu.smartsecure.dataAccess;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import edu.sjsu.smartsecure.domain.CleansedRecord;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
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

    public void insertIntoNewCleansedCollection(List<Integer> result, JSONObject jsonObject) {
        JSONArray jsonArray = (JSONArray)  jsonObject.get("realtimedata");
        int count = 0;
        Iterator<?> iterator = jsonArray.iterator();
        while (iterator.hasNext()) {
            JSONObject appDetails = (JSONObject) iterator.next();
            boolean res = processResult(result.get(count));
            count++;
            appDetails.put("class", res);
            DBObject dbObject = new BasicDBObject();
            Iterator<?> keysIterator = appDetails.keys();
            while (keysIterator.hasNext()) {
                String key = (String) keysIterator.next();
                dbObject.put(key, appDetails.get(key));
            }
            collection.insert(dbObject);
        }
    }

    private boolean processResult(int result) {
        return result == -1 ? true : false;
    }

}
