package edu.sjsu.smartsecure.dataAccess;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import java.net.UnknownHostException;

/**
 * Created by mallika on 4/4/16.
 */
public class AggregationAndMasterUserTableHandler {
    //TODO Change this
    public String getProfession(String userId) {
        DBCollection masterCollection = MongoFactory.getCollection("MasterUserCollection");
        BasicDBObject dbo = new BasicDBObject("userId", userId);
        DBObject userRecord = masterCollection.findOne(dbo);
        return userRecord.get("profession").toString();
    }


}
