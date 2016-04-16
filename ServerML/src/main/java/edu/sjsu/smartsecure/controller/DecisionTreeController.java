package edu.sjsu.smartsecure.controller;

import edu.sjsu.smartsecure.decisionTree.Algorithm;
import edu.sjsu.smartsecure.service.EvalDataCleanseService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mallika on 4/9/16.
 */
@SpringBootApplication
public class DecisionTreeController {
    public static void main(String[] args) {
        try {
            EvalDataCleanseService evalDataCleanseService = new EvalDataCleanseService();
            evalDataCleanseService.TrainData();
        }catch(Exception e){
            e.printStackTrace();
        }
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        List<String> columnHeaders = new ArrayList<String>();
        columnHeaders.add("appName");
        columnHeaders.add("network");
        columnHeaders.add("datausage");
        columnHeaders.add("dayOfTheWeek");
        columnHeaders.add("timeOfTheDay");
        columnHeaders.add("demographic");
        columnHeaders.add("frequency");
        columnHeaders.add("frequentLocation");
        Algorithm.createDecisionTree(columnHeaders);
        SpringApplication.run(DecisionTreeController.class);
    }
}
