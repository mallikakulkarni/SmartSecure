package service;

import edu.sjsu.smartsecure.service.FeedBackService;
import org.bson.BSONObject;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.junit.Test;

/**
 * Created by mallika on 4/11/16.
 */
public class TestFeedBAckService {
    @Test
    public void testFeedbackService() {
        FeedBackService feedBackService = new FeedBackService();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", true);
        jsonObject.put("MongoOid", new ObjectId("570acefe5388fa244886213a"));
        feedBackService.processFeedback(jsonObject);
    }
}
