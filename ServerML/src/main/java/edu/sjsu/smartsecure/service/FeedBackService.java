package edu.sjsu.smartsecure.service;

import edu.sjsu.smartsecure.dataAccess.FeedBackHandler;
import org.json.JSONObject;

/**
 * Created by mallika on 4/11/16.
 */
public class FeedBackService {
    public void processFeedback(JSONObject feedbackObject) {
        FeedBackHandler feedBackHandler = new FeedBackHandler();
        feedBackHandler.processFeedback(feedbackObject);
    }
}
