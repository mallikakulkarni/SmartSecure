package service;

import edu.sjsu.smartsecure.decisionTree.Algorithm;
import edu.sjsu.smartsecure.service.OutlierDetectionService;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mallika on 4/10/16.
 */
public class TestOutlierDetectorService {

    @Before
    public void setUp() {
        List<String> columnHeaders = new ArrayList<String>();
        columnHeaders.add("Outlook");
        columnHeaders.add("humidity");
        columnHeaders.add("temperature");
        columnHeaders.add("wind");
        Algorithm.createDecisionTree(columnHeaders);
    }

    @Test
    public void testGetSafeUnsafeResult() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Outlook", "Sunny");
        jsonObject.put("wind", "Weak");
        jsonObject.put("temperature", "Hot");
        jsonObject.put("humidity", "High");
        OutlierDetectionService service = new OutlierDetectionService();
        Assert.assertEquals(300, service.getSafeUnsafeResult(jsonObject));
    }



    @Test
    public void testTruncateResult() {
        List<String> result = new ArrayList<String>();
        result.add("Safe");
        result.add("Hi");
        result.add("Hello");
        result.add("Hi");
        result.add("Safe");
        OutlierDetectionService outlierDetectionService = new OutlierDetectionService();
        result = outlierDetectionService.truncateResultList(result);
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("Hi", result.get(0));
        Assert.assertEquals("Hello", result.get(1));
    }


}
