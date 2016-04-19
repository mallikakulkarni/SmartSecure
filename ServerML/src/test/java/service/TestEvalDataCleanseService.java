package service;

import com.mongodb.*;
import edu.sjsu.smartsecure.service.EvalDataCleanseService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by mallika on 4/18/16.
 */
public class TestEvalDataCleanseService {
    @Test
    public void testGetUser() {
        EvalDataCleanseService evalDataCleanseService = new EvalDataCleanseService();
        JSONObject jsonObject = getJSONObject();
        String userId = "";
        try {
            userId = evalDataCleanseService.getUser(jsonObject);
        } catch (Exception e) {
        }
        Assert.assertEquals("obulicrusader@gmail.com", userId);
    }

    @Test
    public void testGetDemographics() {
        EvalDataCleanseService evalDataCleanseService = new EvalDataCleanseService();
        JSONObject jsonObject = getJSONObject();
        try {
            String userId = evalDataCleanseService.getUser(jsonObject);
            DB db = evalDataCleanseService.getConnection("mongodb://smartsecureteam:SJSU2016@ds015909.mlab.com:15909/smartsecure");
            DBCollection collection = evalDataCleanseService.getCollection(db, "MasterUserTable");
            String demographic = evalDataCleanseService.getDemographics(userId, collection);
            Assert.assertEquals("male", demographic);
        } catch (Exception e) {
            System.out.println("Failed");
        }

    }

    @Test
    public void testGetAppName() {
        EvalDataCleanseService evalDataCleanseService = new EvalDataCleanseService();
        JSONObject jsonObject = getArray();
        try {
            String appName = evalDataCleanseService.getAppName(jsonObject);
            Assert.assertEquals("com.mobstac.thehindu", appName);
        } catch (Exception e) {
            System.out.println("Failed");
        }
    }

    @Test
    public void testNetwork() {
        EvalDataCleanseService evalDataCleanseService = new EvalDataCleanseService();
        JSONObject jsonObject = getArray();
        try {
            String network = evalDataCleanseService.getNetwork(jsonObject);
            Assert.assertEquals("secure", network);
        } catch (Exception e) {
            System.out.println("Failed");
        }
    }

    @Test
    public void testDataUsage() {
        EvalDataCleanseService evalDataCleanseService = new EvalDataCleanseService();
        JSONObject jsonObject = getArray();
        try {
            String dataUsage = evalDataCleanseService.getDataUsage(jsonObject);
            Assert.assertEquals("medium", dataUsage);
        } catch (Exception e) {
            System.out.println("Failed");
        }
    }

    @Test
    public void testGetDayOfWeek() {
        EvalDataCleanseService evalDataCleanseService = new EvalDataCleanseService();
        JSONObject jsonObject = getArray();
        try {
            String dayOfWeek = evalDataCleanseService.getDayofWeek(jsonObject);
            Assert.assertEquals("friday", dayOfWeek);
        } catch (Exception e) {
            System.out.println("Failed");
        }
    }

    @Test
    public void testTimeOfDay() {
        EvalDataCleanseService evalDataCleanseService = new EvalDataCleanseService();
        JSONObject jsonObject = getArray();
        try {
            String timeOfDay = evalDataCleanseService.getTimeOfDay(jsonObject);
            Assert.assertEquals("afternoon", timeOfDay);
        } catch (Exception e) {
            System.out.println("Failed");
        }
    }

    @Test
    public void testGetFrequentLocation() {
        EvalDataCleanseService evalDataCleanseService = new EvalDataCleanseService();
        JSONObject jsonObject = getArray();
        try {
            String userId = "obulicrusader@gmail.com";
            DB db = evalDataCleanseService.getConnection("mongodb://smartsecureteam:SJSU2016@ds015909.mlab.com:15909/smartsecure");
            DBCollection collection = evalDataCleanseService.getCollection(db, "MasterUserTable");
            String frequentLocation = evalDataCleanseService.getFrequentLoc(userId, collection, jsonObject);
            Assert.assertEquals("No", frequentLocation);
        } catch (Exception e) {
            System.out.println("Failed");
        }

    }

    @Test
    public void testCleanRealTimeData() {
        EvalDataCleanseService evalDataCleanseService = new EvalDataCleanseService();
        JSONObject jsonObject = getJSONObject();
        JSONObject realTimeObject = evalDataCleanseService.cleanRealTimeData(jsonObject);
        JSONArray actObject = (JSONArray) realTimeObject.get("realtimedata");
        Iterator<?> iterator = actObject.iterator();
        while (iterator.hasNext()) {
            JSONObject js = (JSONObject) iterator.next();
            Iterator<?> keysIterator = js.keys();
            while (keysIterator.hasNext()) {
                String key = (String) keysIterator.next();
                System.out.println(key + ": " +js.get(key));
            }
            System.out.println();
        }


    }

    private JSONObject getArray() {
        JSONObject appList = new JSONObject();
        long timeStamp = 1460760129730l;
        appList.put("lastAccessedTimeStamp", timeStamp);
        appList.put("appname", "com.mobstac.thehindu");
        appList.put("lastKnownLat", 37.369);
        appList.put("lastKnownLong", -122.064);
        appList.put("network", "secure");
        appList.put("totalRxBytes", 5297149);
        appList.put("totalTxBytes", 1531312);
        appList.put("wiFiName", "iFiW-Guest");
        appList.put("frequentLocation", "Yes");
        return appList;
    }

    private JSONObject getJSONObject() {
        JSONObject jsonObject = new JSONObject();
        JSONObject idObject = new JSONObject();
        idObject.put("$oid", "57116e4be4b06398c8d77c2b");
        jsonObject.put("_id", idObject);
        jsonObject.put("androidId", "643f16492dd0195b");
        JSONObject appList = new JSONObject();
        appList.put("appAccessedDuration", 31473);
        double timeStamp = 1460760129730d;
        appList.put("lastAccessedTimeStamp", timeStamp);
        appList.put("appname", "com.mobstac.thehindu");
        appList.put("lastKnownLat", 37.369);
        appList.put("lastKnownLong", -122.064);
        appList.put("network", "secure");
        appList.put("totalRxBytes", 5297149);
        appList.put("totalTxBytes", 1531312);
        appList.put("wiFiName", "iFiW-Guest");
        appList.put("frequentLocation", "Yes");
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(0, appList);
        jsonObject.put("appTestList", jsonArray);
        jsonObject.put("incorrectPswdAttemptCount", 0);
        jsonObject.put("userId", "obulicrusader@gmail.com");
        return jsonObject;
    }

}
