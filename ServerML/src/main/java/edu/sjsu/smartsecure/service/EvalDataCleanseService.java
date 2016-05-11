package edu.sjsu.smartsecure.service;

import edu.sjsu.smartsecure.dataAccess.MongoFactory;
import edu.sjsu.smartsecure.decisionTree.DecisionTree;
import org.json.JSONObject;
import org.json.JSONArray;
import com.mongodb.*;
import java.util.*;
import com.mongodb.util.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mallika on 4/10/16.
 */
public class EvalDataCleanseService {

    private String uri = "mongodb://smartsecureteam:SJSU2016@ds015909.mlab.com:15909/smartsecure";
    private String mastertableCollection = "MasterUserTable";
    private String newCleansedCollection = "TrainingData";
   //private String newCleansedCollection = "trainingtestdata";
    public static HashMap<String, HashMap<String, Integer>> UserAppFreq;
    public static long lastMapUpdate = 0;
    static Logger decisionTreeLog = LoggerFactory.getLogger("decisionTree");

    public static DB getConnection(String uri) throws Exception{
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

    public static DBCollection getCollection(DB db, String collection) throws Exception{
        try {
            return db.getCollection(collection);
        } catch (Exception uhe) {
            return null;
        }
    }

    public static String getUser(DBObject obj) throws Exception{
        try {
            return (String)obj.get("userId");
        } catch (Exception uhe) {
            return null;
        }
    }

    public static String getDemographics(String user, DBCollection masterCollection) throws Exception{
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

    public static String getAppName(DBObject obj) throws Exception{
        try {
            return (String)obj.get("appname");
        } catch (Exception uhe) {
            return null;
        }
    }

    public static String getNetwork(DBObject obj) throws Exception{
        try {
            return (String)obj.get("network");
        } catch (Exception uhe) {
            return null;
        }
    }

    public static String getDataUsage(DBObject obj) throws Exception{
        try {
            boolean type = obj.get("totalRxBytes") instanceof String;
            String name = obj.get("totalRxBytes").getClass().getName();
            decisionTreeLog.debug("Data TYpe of Data Usage " + type + " " + name);

            Object rxBytesObj = obj.get("totalRxBytes");
            Object txBytesObj = obj.get("totalTxBytes");

            Double Rxbytes = 0.0d;
            Double Txbytes = 0.0d;

            if (Number.class.isAssignableFrom(rxBytesObj.getClass())) {
                Rxbytes = ((Number)rxBytesObj).doubleValue();
            } else {
                decisionTreeLog.debug("rxBytes cannot be converted to double. Type was " + rxBytesObj.getClass().getName());
            }

            if (Number.class.isAssignableFrom(txBytesObj.getClass())) {
                Txbytes = ((Number)txBytesObj).doubleValue();
            } else {
                decisionTreeLog.debug("txBytes cannot be converted to double. Type was " + txBytesObj.getClass().getName());
            }

            Double dataVal = (Rxbytes + Txbytes)/(1024*1024);
            String usage = "low";
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
            decisionTreeLog.debug("CastException", uhe.getMessage(), uhe);
            return "low";
        }
    }

    public static String getDayofWeek(DBObject obj) throws Exception{
        try {
            Long lastime = (Long)obj.get("lastAccessedTimeStamp");
            lastime-= 7*60*60*1000; //correcting time to PST
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

    public static String getTimeOfDay(DBObject obj) throws Exception{
        try {
            Long lastime = (Long)obj.get("lastAccessedTimeStamp");
            lastime-= 7*60*60*1000; //correcting time to PST
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

    public static double calculateDistance(Double lastKnownLat, Double lastKnownLon, Double knownLat, Double knownLon) throws Exception {
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

    public static String getFrequentLoc(String user, DBCollection masterCollection, DBObject obj) throws Exception{
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
                Double knownLat = latlonObject.get("lat") == (Integer) 0 ? 0.0 : (Double)latlonObject.get("lat");
                Double knownLon = latlonObject.get("lon") == (Integer) 0 ? 0.0 : (Double)latlonObject.get("lon");

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

    public static String getFrequency(String username, DBObject obj) throws Exception{
        try {
            HashMap<String, Integer> AppFrequency = null;
            String appName = (String)obj.get("appname");
            String freq = "low";
            if(lastMapUpdate == 0)
            {
                lastMapUpdate =  System.currentTimeMillis();
            }
            long updateTime = (System.currentTimeMillis() - lastMapUpdate)/(1000*60*60);
            if(updateTime > 24) //the hashmap is cleared every 24hrs to determine the app frequency
            {

                UserAppFreq.clear();
                //AppFrequency.clear();
                lastMapUpdate = System.currentTimeMillis();
            }
            if(UserAppFreq == null)
            {
                UserAppFreq = new HashMap<String, HashMap<String, Integer>>();
            }
            AppFrequency = UserAppFreq.get(username);
            if(AppFrequency == null)
            {
                AppFrequency = new HashMap<String, Integer>();
            }
            Integer frequency = AppFrequency.get(appName);
            if(frequency == null)
            {
                AppFrequency.put(appName, new Integer(1));
            }else {
                int val = frequency.intValue();
                if(val <= 20 ){freq = "low";}
                else if(val > 20 && val<= 50){freq = "medium";}
                else if(val >50){freq = "high";}
                val+=1;
                AppFrequency.remove(appName);
                AppFrequency.put(appName, new Integer(val));
            }
            UserAppFreq.remove(username);
            UserAppFreq.put(username, AppFrequency);
            return freq;
        } catch (Exception uhe) {
            return null;
        }
    }

    public static boolean calculateResult(BasicDBObject obj) throws Exception{
        try {
            boolean result = true;
            String network = (String)obj.get("network");
            String data = (String)obj.get("datausage");
            String timeofday = (String)obj.get("timeOfTheDay");
            String freqLoc = (String)obj.get("frequentLocation");
            String freq = (String)obj.get("frequency");

            if( network.equals("unsecure"))
            {
                result = false;
            }

            if(network.equals("mobile") &&
                    data.equals("high") &&
                    result == true)
            {
                result = false;
            }

            if( timeofday.equals("midnight") &&
                    result == true)
            {
                result = false;
            }

            if( freqLoc.equals("No") &&
                    result == true)
            {
                result = false;
            }
            if( freq.equals("high") &&
                    result == true)
            {
                result = false;
            }

            return result;
        } catch (Exception uhe) {
            return true;
        }
    }

    public void cleanTrainData() throws Exception{
        try{
            decisionTreeLog.debug("Inside cleanTrainData");
            DBCollection trainingCollection = getCollection(getConnection(uri), newCleansedCollection);
            decisionTreeLog.debug("Connected to newCleansedCOllection");
            DBCursor cursor = trainingCollection.find();
            decisionTreeLog.debug("Got cursor");
            while (cursor.hasNext()) {
                trainingCollection.remove(cursor.next());
            }
        } catch (Exception e) {
            decisionTreeLog.debug("Exception occured in cleanTrainData");
        }
    }

    public void TrainData() throws Exception{
        try{
            String collectionName = "smartsecuretest";
            DB db = getConnection(uri);
            decisionTreeLog.debug("Connected to smartsecuretest");
            DBCollection collection = getCollection(db, collectionName);
            DBCollection masterCollection = getCollection(db, mastertableCollection);
            DBCollection trainingCollection = getCollection(db, newCleansedCollection);


            DBCursor curs = collection.find();
            decisionTreeLog.debug("Into collection");
            Iterator<DBObject> fields = curs.iterator();
            while(fields.hasNext()){
                DBObject curr = fields.next();
                String user = getUser(curr);
                decisionTreeLog.debug("Getting user");
                String demo = getDemographics(user, masterCollection);
                decisionTreeLog.debug("Getting demographic");

                BasicDBList appList = (BasicDBList)curr.get("appTestList");
                for( Iterator< Object > it = appList.iterator(); it.hasNext(); )
                {
                    DBObject appObject     = (DBObject)it.next();
                    String appname = getAppName(appObject);
                    String dayofweek = getDayofWeek(appObject);
                    String timeOfDay = getTimeOfDay(appObject);
                    String dataUsage = getDataUsage(appObject);
                    String freqLoc = getFrequentLoc(user, masterCollection, appObject);
                    String frequency = getFrequency(user, appObject);
                    String network = getNetwork(appObject);
                    if( appname != null &&
                            dayofweek != null &&
                            timeOfDay != null &&
                            dataUsage != null &&
                            freqLoc != null &&
                            frequency != null &&
                            network != null)
                    {
                        BasicDBObject newObject = new BasicDBObject();
                        decisionTreeLog.debug("Putting into basicdbobject");
                        newObject.put("appName", appname);
                        newObject.put("network", network);
                        newObject.put("datausage", dataUsage);
                        newObject.put("dayOfTheWeek", dayofweek);
                        newObject.put("timeOfTheDay", timeOfDay);
                        newObject.put("demographic", demo);
                        newObject.put("frequency", frequency);
                        newObject.put("frequentLocation", freqLoc);

                        boolean result = calculateResult(newObject);
                        decisionTreeLog.debug("result is "+result);
                        newObject.put("class", result);

                        trainingCollection.insert(newObject);
                        decisionTreeLog.debug("Inserted");
                    }
                }
            }
        }catch(Exception e) {
            decisionTreeLog.debug("Exception accured in traindata");
            e.printStackTrace();
        }
    }

    public JSONObject cleanRealTimeData(JSONObject jsonObject) {
        decisionTreeLog.debug("Cleaning real time data " +jsonObject.toString());
        JSONObject realTimeRecord = null;
        try{
            realTimeRecord = new JSONObject();
            JSONArray realTimeArray = new JSONArray();
            decisionTreeLog.debug("Db Connection " +uri);
            DB db = getConnection(uri);
            DBCollection masterCollection = getCollection(db, mastertableCollection);
            DBCollection trainingCollection = getCollection(db, newCleansedCollection);

            DBObject curr = (DBObject)JSON.parse(jsonObject.toString());
            decisionTreeLog.debug("Getting user");
            String user = getUser(curr);
            decisionTreeLog.debug("User = "+user);
            decisionTreeLog.debug("Getting demographic");
            String demo = getDemographics(user, masterCollection);
            decisionTreeLog.debug("Gender = "+demo);

            BasicDBList appList = (BasicDBList)curr.get("appTestList");
            for( Iterator< Object > it = appList.iterator(); it.hasNext(); )
            {
                DBObject appObject     = (DBObject)it.next();
                decisionTreeLog.debug("appObject = "+appObject.toString());
                decisionTreeLog.debug("Getting AppName");
                String appname = getAppName(appObject);
                decisionTreeLog.debug("AppName = "+appname);
                decisionTreeLog.debug("Getting Day Of week");
                String dayofweek = getDayofWeek(appObject);
                decisionTreeLog.debug("Day Of the week = "+dayofweek);
                decisionTreeLog.debug("Getting Time Of Day");
                String timeOfDay = getTimeOfDay(appObject);
                decisionTreeLog.debug("Time Of The Day = "+timeOfDay);
                decisionTreeLog.debug("Getting Data usage");
                String dataUsage = getDataUsage(appObject);
                decisionTreeLog.debug("Data Usage = "+dataUsage);
                decisionTreeLog.debug("Getting Frequent Location Boolean");
                String freqLoc = getFrequentLoc(user, masterCollection, appObject);
                decisionTreeLog.debug("Frequent Location = "+freqLoc);
                decisionTreeLog.debug("Getting Frequency bucket");
                String frequency = getFrequency(user, appObject);
                decisionTreeLog.debug("Frequency bucket = "+frequency);
                decisionTreeLog.debug("Getting Network");
                String network = getNetwork(appObject);
                decisionTreeLog.debug("Network = "+network);
                if(appname != null &&
                        dayofweek != null &&
                        timeOfDay != null &&
                        dataUsage != null &&
                        freqLoc != null &&
                        frequency != null &&
                        network != null)
                {
                    BasicDBObject newObject = new BasicDBObject();
                    JSONObject realTimeApp = new JSONObject();
                    newObject.put("appName", appname);
                    realTimeApp.put("appName", appname);
                    newObject.put("network", network);
                    realTimeApp.put("network", network);
                    newObject.put("datausage", dataUsage);
                    realTimeApp.put("datausage", dataUsage);
                    newObject.put("dayOfTheWeek", dayofweek);
                    realTimeApp.put("dayOfTheWeek", dayofweek);
                    newObject.put("timeOfTheDay", timeOfDay);
                    realTimeApp.put("timeOfTheDay", timeOfDay);
                    newObject.put("demographic", demo);
                    realTimeApp.put("demographic", demo);
                    newObject.put("frequency", frequency);
                    realTimeApp.put("frequency", frequency);
                    newObject.put("frequentLocation", freqLoc);
                    realTimeApp.put("frequentLocation", freqLoc);

                    //                boolean result = calculateResult(newObject);
                    //                newObject.put("class", result);
                    //
                    //                trainingCollection.insert(newObject);
                    realTimeArray.put(realTimeApp);
                }
            }
            
            realTimeRecord.put("realtimedata", realTimeArray);
            decisionTreeLog.debug("Real Time Record " +realTimeRecord);
        }catch(Exception e) {
            e.printStackTrace();
        }
        return realTimeRecord;
    }

    public void moveDataIntoTestData() {
        DBCollection trainingCollection = MongoFactory.getCollection("TrainingData");
        DBCollection testData = MongoFactory.getCollection("TestData");
        long count = trainingCollection.count();
        try {
            DBCursor cursor = trainingCollection.find().limit((int) count/3);
            for (DBObject doc : cursor) {
                testData.insert(doc);
                trainingCollection.remove(doc);
            }
        } catch (Exception e) {
            decisionTreeLog.debug(e + e.getMessage() + e.getStackTrace());
        }
    }

    public void compareAgainstTrainingData() {
        int count = 0;
        DBCollection testData = MongoFactory.getCollection("TestData");
        DecisionTree decisionTree = DecisionTree.getDecisionTreeInstance();
        try {
            DBCursor cursor = testData.find();
            for (DBObject doc : cursor) {
                boolean res = (Boolean) doc.get("class");
                doc.removeField("class");
                JSONObject json = asJSON(doc);
                boolean result = decisionTree.processTestDataForTestData(json);
                if (result != res) {
                    count ++;
                }
            }
        } catch (Exception e) {
            decisionTreeLog.debug(e + e.getMessage() + e.getStackTrace());
        }
        long collCount = testData.count();
        float accuracy = (1 - (count/collCount)) * 100;
        decisionTreeLog.debug("Accuracy Of Decision Tree = "+accuracy);
    }

    private JSONObject asJSON(DBObject dbObject) {
        JSONObject jsonObject = new JSONObject();
        for (String key : dbObject.keySet()) {
            jsonObject.put(key, dbObject.get(key));
        }
        return jsonObject;
    }
}
