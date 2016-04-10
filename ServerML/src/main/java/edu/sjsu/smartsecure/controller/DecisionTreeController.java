package edu.sjsu.smartsecure.controller;

import edu.sjsu.smartsecure.decisionTree.Algorithm;
import edu.sjsu.smartsecure.decisionTree.DecisionTree;
import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mallika on 4/9/16.
 */
public class DecisionTreeController {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        List<String> columnHeaders = new ArrayList<String>();
        columnHeaders.add("Outlook");
        columnHeaders.add("humidity");
        columnHeaders.add("temperature");
        columnHeaders.add("wind");
        Algorithm.createDecisionTree(columnHeaders);
        SpringApplication.run(DecisionTreeController.class);
    }
}
