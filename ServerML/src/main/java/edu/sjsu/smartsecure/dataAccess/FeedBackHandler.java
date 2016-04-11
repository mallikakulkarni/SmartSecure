package edu.sjsu.smartsecure.dataAccess;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.json.JSONObject;

/**
 * Created by mallika on 4/11/16.
 */
public class FeedBackHandler {
    public void processFeedback(JSONObject jsonObject) {
        DBCollection collection = MongoFactory.getCollection("TrainingData");
        try {
            BasicDBObject query = new BasicDBObject("_id", jsonObject.get("MongoOid"));
            DBObject result = collection.findOne(query);
            Object abc = jsonObject.get("result");
            String ab= "ac";
        } catch (NullPointerException npe) {
            System.out.println("Returned");
            return;
        }

    }
}
