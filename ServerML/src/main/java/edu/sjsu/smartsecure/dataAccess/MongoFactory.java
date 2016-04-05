package edu.sjsu.smartsecure.dataAccess;

import com.mongodb.*;

import java.net.UnknownHostException;

/**
 * Created by mallika on 4/4/16.
 */
public class MongoFactory {
    public static DB getConnection() throws UnknownHostException
    {
        MongoClientURI uri = new MongoClientURI("mongodb://smartsecureteam:SJSU2016@ds015909.mlab.com:15909/smartsecure");
        MongoClient mongoClient = new MongoClient(uri);
        DB db = mongoClient.getDB(uri.getDatabase());
        mongoClient.setWriteConcern(WriteConcern.JOURNALED);
        return db;
    }

    public static DBCollection getCollection(String collection) {
        try {
            return MongoFactory.getConnection().getCollection(collection);
        } catch (UnknownHostException uhe) {
            return null;
        }
    }
}
