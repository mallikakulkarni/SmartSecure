package edu.sjsu.smartsecure.controller;

import edu.sjsu.smartsecure.decisionTree.Algorithm;
import edu.sjsu.smartsecure.decisionTree.DecisionTree;
import edu.sjsu.smartsecure.service.EvalDataCleanseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    static {
        System.out.println("" + Thread.currentThread().getContextClassLoader().getResource(".").getPath());
        System.setProperty("LOG_DIR", Thread.currentThread().getContextClassLoader().getResource(".").getPath());
    }

    static Logger general = LoggerFactory.getLogger(DecisionTreeController.class);
    static Logger decisionTreeLog = LoggerFactory.getLogger("decisionTree");
    public static void main(String[] args) {

        //TODO: can move the training data part to a separate thread to train data every 24hrs
        try {
            EvalDataCleanseService evalDataCleanseService = new EvalDataCleanseService();
            //evalDataCleanseService.cleanTrainData();
           //evalDataCleanseService.TrainData();

        }catch(Exception e){
            e.printStackTrace();
        }

        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        //BasicConfigurator.configure();
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
        decisionTreeLog.debug("Decision Tree created");
        SpringApplication.run(DecisionTreeController.class);
    }
}
