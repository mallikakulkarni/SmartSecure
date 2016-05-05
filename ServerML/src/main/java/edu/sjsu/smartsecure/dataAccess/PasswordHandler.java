package edu.sjsu.smartsecure.dataAccess;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * Created by mallika on 5/3/16.
 */
public class PasswordHandler {

    public void saveNewPassCode(String passcode, String email) {
        DBCollection collection = MongoFactory.getCollection("MasterUserTable");
        DBObject updateQuery = new BasicDBObject("email", email);
        DBObject update = new BasicDBObject("password", passcode);
        collection.update(updateQuery, update);
    }
}
