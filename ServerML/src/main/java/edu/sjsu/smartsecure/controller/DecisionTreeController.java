package edu.sjsu.smartsecure.controller;

import edu.sjsu.smartsecure.context.AppContext;
import edu.sjsu.smartsecure.decisionTree.Algorithm;
import edu.sjsu.smartsecure.decisionTree.DecisionTree;
import edu.sjsu.smartsecure.decisionTree.Node;
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
        ApplicationContext context = AppContext.getApplicationContextInstance();
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
        DecisionTree decisionTree = DecisionTree.getDecisionTreeInstance();
//        decisionTree.setRoot(new Node("Parent"));
//        Node child1 = new Node("child1");
//        Node child2 = new Node("child2");
//        List<Node> children1 = new ArrayList<>();
//        children1.add(child1);
//        children1.add(child2);
//        decisionTree.getRoot().setChildren(children1);
//        Node grandchild1 = new Node("grandchild1");
//        List<Node> children2 = new ArrayList<>();
//        children2.add(grandchild1);
//        child1.setChildren(children2);
//        Node grandchild2 = new Node("grandchild2");
//        Node grandchild3 = new Node("grandchild3");
//        Node grandchild4 = new Node("grandchild4");
//        List<Node> children3 = new ArrayList<>();
//        children3.add(grandchild2);
//        children3.add(grandchild3);
//        children3.add(grandchild4);
//        child2.setChildren(children3);
//        Node greatgrandchild1 = new Node("greatgrandchild1");
//        Node greatgrandchild2 = new Node("greatgrandchild2");
//        Node greatgrandchild3 = new Node("greatgrandchild3");
//        List<Node> children4 = new ArrayList<>();
//        children4.add(greatgrandchild1);
//        children4.add(greatgrandchild2);
//        children4.add(greatgrandchild3);
//        grandchild3.setChildren(children4);
        decisionTreeLog.debug("Decision Tree created");
        decisionTree.getAllAttributesOfDecisionTree();
        SpringApplication.run(DecisionTreeController.class);
    }
}
