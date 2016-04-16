package edu.sjsu.smartsecure.service;

import org.bson.types.ObjectId;
import org.json.JSONObject;
import com.mongodb.*;
import java.util.*;

/**
 * Created by mallika on 4/10/16.
 */
public class EvalDataCleanseService {

    private String uri = "mongodb://smartsecureteam:SJSU2016@ds015909.mlab.com:15909/smartsecure";
    private String collectionName = "smartsecuretest";
    private String mastertableCollection = "MasterUserTable";
    private String newCleansedCollection = "TrainingData";

    private static DB getConnection(String uri) throws Exception{
        try{
            MongoClientURI mongoURI = new MongoClientURI(uri);
            MongoClient mongoClient = new MongoClient(mongoURI);
            DB db = mongoClient.getDB(mongoURI.getDatabase());
            mongoClient.setWriteConcern(WriteConcern.JOURNALED);
            return db;
        } catch(Exception e){
            return null;
        }
    }

    private static DBCollection getCollection(DB db, String collection) throws Exception{
        try {
            return db.getCollection(collection);
        } catch (Exception uhe) {
            return null;
        }
    }

    private static String getUser(DBObject obj) throws Exception{
        try {
            return (String)obj.get("userId");
        } catch (Exception uhe) {
            return null;
        }
    }

    private static String getId(DBObject obj) throws Exception{
        try {
            ObjectId id = (ObjectId)obj.get("_id");
            return id.toString();
        } catch (Exception uhe) {
            return null;
        }
    }

    private static String getDemographics(String user, DBCollection masterCollection) throws Exception{
        try {
            String demo = null;
            DBCursor curs = masterCollection.find();
            Iterator<DBObject> fields = curs.iterator();
            while(fields.hasNext()){
                DBObject curr = fields.next();
                String currUsr = (String)curr.get("email");
                if(currUsr.equals(user))
                {
                    demo = (String)curr.get("gender");
                }
            }
            return demo;
        } catch (Exception uhe) {
            return null;
        }
    }

    private static String getAppName(DBObject obj) throws Exception{
        try {
            return (String)obj.get("appname");
        } catch (Exception uhe) {
            return null;
        }
    }

    private static String getNetwork(DBObject obj) throws Exception{
        try {
            return (String)obj.get("network");
        } catch (Exception uhe) {
            return null;
        }
    }

    private static String getDataUsage(DBObject obj) throws Exception{
        try {
            Double Rxbytes = (Double)obj.get("totalRxBytes");
            Double Txbytes = (Double)obj.get("totalTxBytes");
            Double dataVal = (Rxbytes + Txbytes)/(1024*1024);
            String usage = null;
            if(dataVal > 10)
            {
                usage = "high";
            }
            else if(dataVal < 10 && dataVal >= 5)
            {
                usage = "medium";
            }
            else if(dataVal < 5)
            {
                usage = "low";
            }
            return usage;
        } catch (Exception uhe) {
            return null;
        }
    }

    private static String getDayofWeek(DBObject obj) throws Exception{
        try {
            Long lastime = (Long)obj.get("lastAccessedTimeStamp");
            Date dateVal = new Date(lastime.longValue());
            int day = dateVal.getDay();
            String dayOfWeek = null;

            if(day == 0){ dayOfWeek = "sunday";}
            else if(day == 1) { dayOfWeek = "monday";}
            else if(day == 2) { dayOfWeek = "tuesday";}
            else if(day == 3) { dayOfWeek = "wednesday";}
            else if(day == 4) { dayOfWeek = "thursday";}
            else if(day == 5) { dayOfWeek = "friday";}
            else if(day == 6) { dayOfWeek = "saturday";}

            return dayOfWeek;
        } catch (Exception uhe) {
            return null;
        }
    }

    private static String getTimeOfDay(DBObject obj) throws Exception{
        try {
            Long lastime = (Long)obj.get("lastAccessedTimeStamp");
            Date dateVal = new Date(lastime.longValue());
            int hours = dateVal.getHours();
            String timeOfDay = null;

            if(hours >= 0 && hours < 3 ){timeOfDay = "midnight";}
            if(hours >= 3 && hours < 6 ){timeOfDay = "early morning";}
            if(hours >= 6 && hours < 10 ){timeOfDay = "morning";}
            if(hours >= 10 && hours < 13 ){timeOfDay = "noon";}
            if(hours >= 13 && hours < 17 ){timeOfDay = "afternoon";}
            if(hours >= 17 && hours < 21 ){timeOfDay = "evening";}
            if(hours >= 21 && hours <= 23 ){timeOfDay = "night";}

            return timeOfDay;
        } catch (Exception uhe) {
            return null;
        }
    }

    private static double calculateDistance(Double lastKnownLat, Double lastKnownLon, Double knownLat, Double knownLon) throws Exception {
        try{
            double distance;
            final int R = 6371;
            Double latDistance = Math.toRadians(knownLat - lastKnownLat);
            Double lonDistance = Math.toRadians(knownLon - lastKnownLon);
            Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                    + Math.cos(Math.toRadians(lastKnownLat)) * Math.cos(Math.toRadians(knownLat))
                    * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
            Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            distance = R * c * 1000;
            distance = Math.pow(distance, 2);
            distance = Math.sqrt(distance);
            distance *= 0.00062137119;

            return distance;
        }catch(Exception e){
            return 0;
        }
    }

    private static String getFrequentLoc(String user, DBCollection masterCollection, DBObject obj) throws Exception{
        try {
            String freqLoc = "No";
            BasicDBList latlon = null;
            Double lastKnownLat = (Double)obj.get("lastKnownLat");
            Double lastKnownLon = (Double)obj.get("lastKnownLong");
            DBCursor curs = masterCollection.find();
            Iterator<DBObject> fields = curs.iterator();
            while(fields.hasNext()){
                DBObject curr = fields.next();
                String currUsr = (String)curr.get("email");
                if(currUsr.equals(user))
                {
                    latlon = (BasicDBList)curr.get("latLon");
                    break;
                }
            }

            for( Iterator< Object > it = latlon.iterator(); it.hasNext(); )
            {
                DBObject latlonObject = (DBObject)it.next();
                Double knownLat = (Double)latlonObject.get("lat");
                Double knownLon = (Double)latlonObject.get("lon");

                double distance = calculateDistance(lastKnownLat, lastKnownLon, knownLat, knownLon);
                if(distance < 1){
                    freqLoc = "Yes";
                    break;
                }
            }
            return freqLoc;
        } catch (Exception uhe) {
            return null;
        }
    }

    private static String getFrequency(DBObject obj) throws Exception{
        try {
            String freq = "10";
            return freq;
        } catch (Exception uhe) {
            return null;
        }
    }

    private static String calculateResult(BasicDBObject obj) throws Exception{
        try {
            String result = "true";
            String network = (String)obj.get("network");
            String data = (String)obj.get("datausage");
            String timeofday = (String)obj.get("timeOfTheDay");
            String freqLoc = (String)obj.get("frequentLocation");

            if( network.equals("unsecure"))
            {
                result = "false";
            }

            if(network.equals("mobile") &&
                    data.equals("high") &&
                    result.equals("true"))
            {
                result = "false";
            }

            if( timeofday.equals("midnight") &&
                    result.equals("true"))
            {
                result = "false";
            }

            if( freqLoc.equals("No") &&
                    result.equals("true"))
            {
                result = "false";
            }

            return result;
        } catch (Exception uhe) {
            return null;
        }
    }

    public void TrainData() throws Exception{
        try{
            DB db = getConnection(uri);
            DBCollection collection = getCollection(db, collectionName);
            DBCollection masterCollection = getCollection(db, mastertableCollection);
            DBCollection trainingCollection = getCollection(db, newCleansedCollection);


            DBCursor curs = collection.find();
            Iterator<DBObject> fields = curs.iterator();
            while(fields.hasNext()){
                DBObject curr = fields.next();
                String user = getUser(curr);

                String uniqueId = getId(curr);

                String demo = getDemographics(user, masterCollection);

                BasicDBList appList = (BasicDBList)curr.get("appTestList");
                for( Iterator< Object > it = appList.iterator(); it.hasNext(); )
                {
                    DBObject appObject     = (DBObject)it.next();
                    String appname = getAppName(appObject);
                    String dayofweek = getDayofWeek(appObject);
                    String timeOfDay = getTimeOfDay(appObject);
                    String dataUsage = getDataUsage(appObject);
                    String freqLoc = getFrequentLoc(user, masterCollection, appObject);
                    String frequency = getFrequency(appObject);
                    String network = getNetwork(appObject);

                    BasicDBObject newObject = new BasicDBObject();
                    newObject.put("uniqueId", uniqueId);
                    newObject.put("appName", appname);
                    newObject.put("network", network);
                    newObject.put("datausage", dataUsage);
                    newObject.put("dayOfTheWeek", dayofweek);
                    newObject.put("timeOfTheDay", timeOfDay);
                    newObject.put("demographic", demo);
                    newObject.put("frequency", frequency);
                    newObject.put("frequentLocation", freqLoc);

                    String result = calculateResult(newObject);
                    newObject.put("class", result);

                    trainingCollection.update(newObject, newObject, true, false);

                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject cleanData(JSONObject jsonObject) {
        return jsonObject;
    }


}
