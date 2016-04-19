package edu.sjsu.smartsecure.service;

import org.json.JSONObject;
import org.json.JSONArray;
import com.mongodb.*;
import java.util.*;

/**
 * Created by mallika on 4/10/16.
 */
public class EvalDataCleanseService {

    private String uri = "mongodb://smartsecureteam:SJSU2016@ds015909.mlab.com:15909/smartsecure";
    private String mastertableCollection = "MasterUserTable";
    private String newCleansedCollection = "TrainingData";
    private static HashMap<String, Integer> AppFrequency;
    private static long lastMapUpdate = 0;

    public DB getConnection(String uri) throws Exception{
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

    public DBCollection getCollection(DB db, String collection) throws Exception{
        try {
            return db.getCollection(collection);
        } catch (Exception uhe) {
            return null;
        }
    }

    public String getUser(JSONObject jsonObject) throws Exception{
        try {
            return (String)jsonObject.get("userId");
        } catch (Exception uhe) {
            return null;
        }
    }

    public String getUser(DBObject dbObject) throws Exception{
        try {
            return (String)dbObject.get("userId");
        } catch (Exception uhe) {
            return null;
        }
    }

    public String getDemographics(String user, DBCollection masterCollection) throws Exception{
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

    public String getAppName(JSONObject obj) throws Exception{
        try {
            return (String)obj.get("appname");
        } catch (Exception uhe) {
            return null;
        }
    }

    public String getNetwork(JSONObject obj) throws Exception{
        try {
            return (String)obj.get("network");
        } catch (Exception uhe) {
            return null;
        }
    }

    public String getDataUsage(JSONObject obj) throws Exception{
        try {
            System.out.println(obj.get("totalRxBytes").getClass());
            Integer rxBytes =  (Integer) obj.get("totalRxBytes");
            Integer txBytes = (Integer) obj.get("totalTxBytes");
            Integer dataVal = (rxBytes + txBytes)/(1024*1024);
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

    public String getDayofWeek(JSONObject obj) throws Exception{
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

    public String getTimeOfDay(JSONObject obj) throws Exception{
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

    private double calculateDistance(Double lastKnownLat, Double lastKnownLon, Double knownLat, Double knownLon) throws Exception {
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

    public String getFrequentLoc(String user, DBCollection masterCollection, JSONObject obj) throws Exception{
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

    private static String getFrequency(JSONObject obj) throws Exception{
        try {
            String appName = (String)obj.get("appname");
            String freq = "low";
            long updateTime = (System.currentTimeMillis() - lastMapUpdate)/(1000*60*60);
            if(updateTime > 24) //the hashmap is cleared every 24hrs to determine the app frequency
            {
                AppFrequency.clear();
                lastMapUpdate = System.currentTimeMillis();
            }
            Integer frequency = AppFrequency.get(appName);
            if(frequency == null)
            {
                AppFrequency.put(appName, new Integer(0));
            }else {
                int val = frequency.intValue();
                if(val <= 5 ){freq = "low";}
                else if(val > 5 && val<= 10){freq = "medium";}
                else if(val >10){freq = "high";}
                val+=1;
                AppFrequency.remove(appName);
                AppFrequency.put(appName, new Integer(val));
            }
            return freq;
        } catch (Exception uhe) {
            return null;
        }
    }

    private static String calculateResult(DBObject obj) throws Exception{
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

    public void cleanTrainData() throws Exception{
        try{
            DBCollection trainingCollection = getCollection(getConnection(uri), newCleansedCollection);
            DBCursor cursor = trainingCollection.find();
            while (cursor.hasNext()) {
                trainingCollection.remove(cursor.next());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void trainData() throws Exception{
        try{
            String collectionName = "smartsecuretest";
            DB db = getConnection(uri);
            DBCollection collection = getCollection(db, collectionName);
            DBCollection masterCollection = getCollection(db, mastertableCollection);
            DBCollection trainingCollection = getCollection(db, newCleansedCollection);
            DBCursor curs = collection.find();
            Iterator<DBObject> fields = curs.iterator();
            while(fields.hasNext()){
                DBObject curr = fields.next();
                String user = getUser(curr);
                String demo = getDemographics(user, masterCollection);
                BasicDBList appList = (BasicDBList)curr.get("appTestList");
                Iterator<?> iterator = appList.iterator();
                while (iterator.hasNext()) {
                    DBObject appObject     = (DBObject) iterator.next();
                    BasicDBObject newObject = new BasicDBObject();
                    Set<String> keys = appObject.keySet();
                    for (String key : keys) {
                        newObject.put(key, appObject.get(key));
                    }
                    String result = calculateResult(newObject);
                    newObject.put("class", result);
                    trainingCollection.insert(newObject);
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject cleanRealTimeData(JSONObject jsonObject) {

        JSONObject realTimeRecord = null;
        try{
            realTimeRecord = new JSONObject();
            JSONArray realTimeArray = new JSONArray();

            DB db = getConnection(uri);
            DBCollection masterCollection = getCollection(db, mastertableCollection);
            DBCollection trainingCollection = getCollection(db, newCleansedCollection);
//            DBObject curr = new BasicDBObject();
//            Iterator<?> keys = jsonObject.keys();
//            while (keys.hasNext()) {
//                String key = (String) keys.next();
//                curr.put(key, jsonObject.get(key));
//            }
            String user = getUser(jsonObject);
            String demo = getDemographics(user, masterCollection);
            JSONArray appList = (JSONArray) jsonObject.get("appTestList");
            Iterator<?> iterator = appList.iterator();
            while (iterator.hasNext()) {
                JSONObject appObject = (JSONObject) iterator.next();
                BasicDBObject newObject = new BasicDBObject();
                JSONObject realTimeApp = new JSONObject();
                Iterator<?> keys = appObject.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    newObject.put(key, appObject.get(key));
                    realTimeApp.put(key, appObject.get(key));
                }
//                String result = calculateResult(newObject);
//                newObject.put("class", result);
//                trainingCollection.insert(newObject);
                realTimeArray.put(realTimeApp);
            }
            realTimeRecord.put("realtimedata",realTimeArray);
            return realTimeRecord;
        }catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
