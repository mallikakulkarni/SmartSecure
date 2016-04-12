package edu.sjsu.smartsecure.controller;

import edu.sjsu.smartsecure.decisionTree.DecisionTree;
import edu.sjsu.smartsecure.service.EvalDataCleanseService;
import edu.sjsu.smartsecure.service.FeedBackService;
import edu.sjsu.smartsecure.service.OutlierDetectionService;
import org.json.JSONObject;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by mallika on 4/8/16.
 */

@RequestMapping("/smartsecure")
@org.springframework.web.bind.annotation.RestController
public class RestController {
    @RequestMapping(value="/EvalDataPost", method = RequestMethod.POST)
    public JSONObject getEvaluationResponse(@RequestBody JSONObject jsonObject) {
        EvalDataCleanseService evalDataCleanseService = new EvalDataCleanseService();
        jsonObject = evalDataCleanseService.cleanData(jsonObject);
        OutlierDetectionService outlierDetectionService = new OutlierDetectionService();
        JSONObject resultObject = outlierDetectionService.getSafeUnsafeResult(jsonObject);
        return resultObject;
    }

    @RequestMapping(value="/Feedback", method = RequestMethod.POST)
    public boolean postFeedback(JSONObject feedbackObject) {
        FeedBackService feedBackService = new FeedBackService();
        feedBackService.processFeedback(feedbackObject);
        return true;
    }

}
