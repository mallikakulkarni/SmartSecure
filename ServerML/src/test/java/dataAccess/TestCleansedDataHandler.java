package dataAccess;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import edu.sjsu.smartsecure.dataAccess.CleansedDataHandler;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by mallika on 4/18/16.
 */
public class TestCleansedDataHandler {
    @Test
    public void testInsertIntoNewCleansedCollection() {
        CleansedDataHandler cleansedDataHandler = new CleansedDataHandler();
        List<String> result = new ArrayList<String>();
        result.add("Safe");
        JSONObject jsonObject = getJSONObject();
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(0, jsonObject);
        JSONObject parObject = new JSONObject();
        parObject.put("realtimedata", jsonArray);
        cleansedDataHandler.insertIntoNewCleansedCollection(result, parObject);
    }

    private JSONObject getJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("appAccessedDuration", 31473);
        double timeStamp = 1460760129730d;
        jsonObject.put("lastAccessedTimeStamp", timeStamp);
        jsonObject.put("appname", "minesweeper");
        jsonObject.put("lastKnownLat", 37.369);
        jsonObject.put("lastKnownLong", -122.064);
        jsonObject.put("network", "secure");
        jsonObject.put("totalRxBytes", 5297149);
        jsonObject.put("totalTxBytes", 1531312);
        jsonObject.put("wiFiName", "iFiW-Guest");
        jsonObject.put("frequentLocation", "Yes");
        return jsonObject;
    }
}
