package dataAccess;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import edu.sjsu.smartsecure.dataAccess.DecisionTreeHandler;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mallika on 4/5/16.
 */
public class TestDecisionTreeDataHandler {
    @Test
    public void testGetTotalRecordCount() {
        DecisionTreeHandler decisionTreeHandler = new DecisionTreeHandler();
        Assert.assertEquals(14, decisionTreeHandler.getTotalRecordCount());
    }

    @Test
    public void testGetAttributeCountMap() {
        DecisionTreeHandler decisionTreeHandler = new DecisionTreeHandler();
        List<String> conditions = new ArrayList<String>();
        List<String> values = new ArrayList<String>();
        conditions.add("Outlook");
        values.add("Sunny");
        Map<String, Map<String, Long>> map = decisionTreeHandler.getAttributeCountMap("humidity", conditions, values);
        Map<String, Long> highMap = map.get("High");
        Map<String, Long> normalMap = map.get("Normal");
        long totalhigh = highMap.get("Total");
        long safehigh = highMap.get("safe");
        long unsafehigh = highMap.get("unsafe");
        long totalnormal = normalMap.get("Total");
        long safenormal = normalMap.get("safe");
        long unsafenormal = normalMap.get("unsafe");
        Assert.assertEquals(3, totalhigh);
        Assert.assertEquals(0, safehigh);
        Assert.assertEquals(3, unsafehigh);
        Assert.assertEquals(2, totalnormal);
        Assert.assertEquals(2, safenormal);
        Assert.assertEquals(0, unsafenormal);
    }

    @Test
    public void testGetForks() {
        DecisionTreeHandler decisionTreeHandler = new DecisionTreeHandler();
        List<String> list = decisionTreeHandler.getForks("humidity");
        Assert.assertEquals(2, list.size());
    }

    @Test
    public void testGetCountsOfSafeAndUnsafeData() {
        DecisionTreeHandler decisionTreeHandler = new DecisionTreeHandler();
        Map<String, Long> recordMap = decisionTreeHandler.getCountsOfSafeAndUnsafeData();
        Assert.assertEquals(14, (long) recordMap.get("Total"));
        Assert.assertEquals(9, (long) recordMap.get("safe"));
        Assert.assertEquals(5, (long) recordMap.get("unsafe"));
    }

    @Test
    public void testGetCountsOfSafeAndUnsafeDataAttributes() {
        DecisionTreeHandler decisionTreeHandler = new DecisionTreeHandler();
        List<String> conditions = new ArrayList<String>();
        List<String> values = new ArrayList<String>();
        conditions.add("Outlook");
        values.add("Sunny");
        Map<String, Long> recordMap = decisionTreeHandler.getCountsOfSafeAndUnsafeData(conditions, values);
        Assert.assertEquals(5, (long) recordMap.get("Total"));
        Assert.assertEquals(2, (long) recordMap.get("safe"));
        Assert.assertEquals(3, (long) recordMap.get("unsafe"));
    }

    @Test
    public void getTotalRecordCountForChild() {
        DecisionTreeHandler decisionTreeHandler = new DecisionTreeHandler();
        List<String> conditions = new ArrayList<String>();
        List<String> values = new ArrayList<String>();
        conditions.add("Outlook");
        values.add("Sunny");
        Assert.assertEquals(5, decisionTreeHandler.getTotalRecordCountForChild(conditions, values));
    }

    @Test
    public void getSafeRecordCountForChild() {
        DecisionTreeHandler decisionTreeHandler = new DecisionTreeHandler();
        List<String> conditions = new ArrayList<String>();
        List<String> values = new ArrayList<String>();
        conditions.add("Outlook");
        values.add("Sunny");
        Assert.assertEquals(2, decisionTreeHandler.getSafeRecordCountForChild(conditions, values, true));
    }

}
