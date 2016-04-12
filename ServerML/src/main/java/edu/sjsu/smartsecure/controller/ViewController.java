package edu.sjsu.smartsecure.controller;

import edu.sjsu.smartsecure.decisionTree.DecisionTree;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by mallika on 4/12/16.
 */
@Controller
@RequestMapping("smartsecure")
public class ViewController {

    @RequestMapping(value="/GetTree", method = RequestMethod.GET)
    public String getDecisionTreeVisualization(Model model) {
        DecisionTree decisionTree = DecisionTree.getDecisionTreeInstance();
        model.addAttribute("DecisionTree", decisionTree);
        model.addAttribute("Root", decisionTree.getRoot().getNodeId());
        return "Tree";
    }
}
