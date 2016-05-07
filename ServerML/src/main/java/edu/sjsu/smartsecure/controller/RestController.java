package edu.sjsu.smartsecure.controller;

import edu.sjsu.smartsecure.decisionTree.DecisionTree;
import edu.sjsu.smartsecure.service.EvalDataCleanseService;
import edu.sjsu.smartsecure.service.FeedBackService;
import edu.sjsu.smartsecure.service.OutlierDetectionService;
import edu.sjsu.smartsecure.service.PasswordService;
import org.json.JSONObject;
import org.json.JSONString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * Created by mallika on 4/8/16.
 */

@RequestMapping("/smartsecure")
@org.springframework.web.bind.annotation.RestController
public class RestController {
    static Logger decisionTreeLog = LoggerFactory.getLogger("decisionTree");
    @RequestMapping(value="/EvalDataPost", method = RequestMethod.POST)
    public String getEvaluationResponse(@RequestBody String input) {
        try {
            JSONObject jsonObject = new JSONObject(input);
            decisionTreeLog.debug("Received Post Request with JSON Object :- " +jsonObject);
            EvalDataCleanseService evalDataCleanseService = new EvalDataCleanseService();
            jsonObject = evalDataCleanseService.cleanRealTimeData(jsonObject);
            OutlierDetectionService outlierDetectionService = new OutlierDetectionService();
            List<String> resultObject = outlierDetectionService.getSafeUnsafeResult(jsonObject);
            return resultObject.toString();
        } catch (Exception e) {
            return "Safe";
        }
    }

    @RequestMapping(value="/Feedback", method = RequestMethod.POST)
    public boolean postFeedback(JSONObject feedbackObject) {
        FeedBackService feedBackService = new FeedBackService();
        feedBackService.processFeedback(feedbackObject);
        return true;
    }

    @RequestMapping(value="/ResetPassword", method = RequestMethod.GET)
    public String resetPassword(@RequestBody String email) {
        decisionTreeLog.debug("Received email " + email + " for resetting password");
        PasswordService passwordService = new PasswordService();
        String passcode =  passwordService.resetPassCode(email);
        return passcode;
    }

}
