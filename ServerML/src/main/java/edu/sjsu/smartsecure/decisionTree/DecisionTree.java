package edu.sjsu.smartsecure.decisionTree;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by mallika on 3/27/16.
 */
public class DecisionTree {
    private static DecisionTree decisionTree = null;
    private Node root;

    private DecisionTree() {
        root = null;
    }
    static Logger decisionTreeLog = LoggerFactory.getLogger("decisionTree");

    public static DecisionTree getDecisionTreeInstance() {
        if (decisionTree == null) {
            decisionTree = new DecisionTree();
        }
        return decisionTree;
    }

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public List<String> processTestData(JSONObject jObject) {
        JSONArray jsonArray = (JSONArray) jObject.get("realtimedata");
        Iterator<?> iterator = jsonArray.iterator();
        List<String> list = new ArrayList<String>();
        while (iterator.hasNext()) {
            JSONObject jsonObject = (JSONObject) iterator.next();
            decisionTreeLog.debug("Incoming JsonObject "+jsonObject);
            decisionTreeLog.debug("Starting DT Traversal");
            String result = traverseDecisionTreeForResult(root, jsonObject);
            list.add(result);
        }
        return list;
    }

    private String traverseDecisionTreeForResult(Node node, JSONObject jsonObject) {
        String nodeId = node.getNodeId();
        decisionTreeLog.debug("Node "+nodeId);
        String inputValue = jsonObject.get(nodeId).toString();
        decisionTreeLog.debug("Object Value "+inputValue);
        List<String> attributes = node.getAttributes();
        int i;
        for (i = 0; i < attributes.size(); i++) {
            System.out.println(attributes.get(i));
            if (attributes.get(i) == null || attributes.get(i).equals(inputValue)) {
                break;
            }
        }
        if (i == attributes.size()) {
            return "Safe";
        }
        Node child = node.getChildren().get(i);
        decisionTreeLog.debug("Going to child "+child.getNodeId());
        if (child.getResult() != null) {
            decisionTreeLog.debug("Getting Result "+child.getResult());
            return getResultMessage(child);
        }
        return traverseDecisionTreeForResult(child, jsonObject);
    }

    private String getResultMessage(Node node) {
        if (node.getResult() == -1) {
            return "Safe";
        }
        String result = "";
        switch (node.getResult()) {
            case 1:
                result = "Warning: Other users don't trust the " +node.getCorrespondingAttribute() + " app";
                break;
            case 2:
                result = "Warning: You are on an unsecure Wi-fi network";
                break;
            case 3:
                result = "Warning: Data Usage is very high";
                break;
            case 4:
                result = "Alert: You have used your phone at an unusual time.";
                break;
            case 5:
                result = "Alert: You have used your phone at an unusual time.";
                break;
            case 6:
                result = "Safe";
                break;
            case 7:
                result = "Warning: The usage statistics for all apps is very high";
                break;
            case 8:
                result = "Alert: You are in an unknown location.";
                break;
        }
        return result;
    }

    public DecisionTree cleanDecisionTreeForEasyView() {
        DecisionTree newDt = new DecisionTree();
        Node node = new Node(root.getNodeId());
        cleanDecisionTreeForEasyView(root, node);
        newDt.setRoot(node);
        return newDt;
    }

    private void cleanDecisionTreeForEasyView(Node orig, Node node) {
        if (orig.getChildren().size() > 3) {
            List<Node> children = new ArrayList<>();
            Node safeChild = new Node("Safe");
            List<String> safeChildList = new ArrayList<>();
            Node unsafeChild = new Node("Unsafe");
            List<String> unsafeChildList = new ArrayList<>();
            List<Node> newChildren = new ArrayList<>();
            for (Node child : orig.getChildren()) {
                if (child.getResult() == null) {
                    Node newChild = new Node(child.getNodeId());
                    newChildren.add(newChild);
                    cleanDecisionTreeForEasyView(child, newChild);
                    //node.setChildren(orig.getChildren());
                    node.setResult(orig.getResult());
                    node.setColumn(orig.getColumn());
                    node.setCorrespondingAttribute(orig.getCorrespondingAttribute());
                    node.setParent(orig.getParent());
                } else if (child.getResult() == -1) {
                    node.setColumn(orig.getColumn());
                    node.setCorrespondingAttribute(orig.getCorrespondingAttribute());
                    safeChildList.add(child.getColumn() + " " + child.getCorrespondingAttribute());
                } else {
                    node.setColumn(orig.getColumn());
                    node.setCorrespondingAttribute(orig.getCorrespondingAttribute());
                    unsafeChildList.add(child.getColumn() + " " + child.getCorrespondingAttribute());
                }
            }
            node.setChildren(newChildren);
            safeChild.setSafeChildList(safeChildList);
            unsafeChild.setUnsafeChildList(unsafeChildList);
            node.getChildren().add(safeChild);
            node.getChildren().add(unsafeChild);

        } else {
            List<Node> newChildren = new ArrayList<>();
            for (Node child : orig.getChildren()) {
                Node newChild = new Node(child.getNodeId());
                newChildren.add(newChild);
                cleanDecisionTreeForEasyView(child, newChild);
            }
            node.setChildren(newChildren);
            node.setResult(orig.getResult());
            node.setColumn(orig.getColumn());
            node.setCorrespondingAttribute(orig.getCorrespondingAttribute());
        }
        return;
    }

    public void getAllAttributesOfDecisionTree() {
        printChildren(root);
    }

    private void printChildren(Node node) {
        node.printAttributes();
        for (Node child : node.getChildren()) {
            child.printAttributes();
        }
    }

    public static DecisionTree dummyDecisionTree() {
        decisionTree = new DecisionTree();
        Node node = new Node("frequentLocation");
        decisionTree.setRoot(node);
        Node noChild = new Node("UnSafeFreqLoc");
        /* Adding frequency to yes requent location */
        Node yesChild = new Node("frequency");
        noChild.setResult(7);
        noChild.setCorrespondingAttribute("No");
        noChild.setColumn("freQuentLocation");
        yesChild.setColumn("freQuentLocation");
        yesChild.setCorrespondingAttribute("Yes");
        List<String> locAttributes = new ArrayList<>();
        locAttributes.add("Yes");
        locAttributes.add("No");
        node.setAttributes(locAttributes);
        List<Node> locChildren = new ArrayList<>();
        locChildren.add(yesChild);
        locChildren.add(noChild);
        node.setChildren(locChildren);
        List<String> freqAttributes = new ArrayList<>();
        freqAttributes.add("high");
        freqAttributes.add("medium");
        freqAttributes.add("low");
        yesChild.setAttributes(freqAttributes);
        Node freqHighChild = new Node("appName");
        freqHighChild.setColumn("Frequency");
        freqHighChild.setCorrespondingAttribute("High");
        Node freqMedChild = new Node("timeOfTheDay");
        freqMedChild.setColumn("Frequency");
        freqMedChild.setCorrespondingAttribute("Medium");
        Node freqLowChild = new Node("demographic");
        freqLowChild.setColumn("Frequency");
        freqLowChild.setCorrespondingAttribute("Low");
        List<Node> freqChildren = new ArrayList<>();
        freqChildren.add(freqHighChild);
        freqChildren.add(freqMedChild);
        freqChildren.add(freqLowChild);
        yesChild.setChildren(freqChildren);
        List<String> highFreqAppNames = new ArrayList<>();
        highFreqAppNames.add("com.android.chrome");
        highFreqAppNames.add("com.panu");
        highFreqAppNames.add("com.whatsapp");
        highFreqAppNames.add("com.facebook.katana");
        highFreqAppNames.add("com.quora.android");
        highFreqAppNames.add("com.google.android.youtube");
        highFreqAppNames.add("com.facebook.orca");
        Node chromeHighFreq = new Node("com.android.chromeunsafe");
        Node panuHighFreq = new Node("com.panuunsafe");
        Node whatsappHighFreq = new Node("com.whatsappsafe");
        Node facebookkHighFreq = new Node("com.facebook.katanaunsafe");
        Node quoraHighFreq = new Node("com.quora.androidunsafe");
        Node youtubeHighFreq = new Node("com.google.android.youtubeunsafe");
        Node facebookoHighFreq = new Node("com.facebook.orcasafe");
        chromeHighFreq.setColumn("appName");
        panuHighFreq.setColumn("appName");
        whatsappHighFreq.setColumn("appName");
        facebookkHighFreq.setColumn("appName");
        quoraHighFreq.setColumn("appName");
        youtubeHighFreq.setColumn("appName");
        facebookoHighFreq.setColumn("appName");
        chromeHighFreq.setCorrespondingAttribute("High");
        panuHighFreq.setCorrespondingAttribute("High");
        whatsappHighFreq.setCorrespondingAttribute("High");
        facebookkHighFreq.setCorrespondingAttribute("High");
        quoraHighFreq.setCorrespondingAttribute("High");
        youtubeHighFreq.setCorrespondingAttribute("High");
        facebookoHighFreq.setCorrespondingAttribute("High");
        facebookoHighFreq.setColumn("appName");
        chromeHighFreq.setResult(7);
        panuHighFreq.setResult(7);
        whatsappHighFreq.setResult(-1);
        facebookkHighFreq.setResult(7);
        quoraHighFreq.setResult(7);
        youtubeHighFreq.setResult(7);
        facebookoHighFreq.setResult(-1);
        List<Node> freqHighChildren = new ArrayList<>();
        freqHighChildren.add(chromeHighFreq);
        freqHighChildren.add(panuHighFreq);
        freqHighChildren.add(whatsappHighFreq);
        freqHighChildren.add(facebookkHighFreq);
        freqHighChildren.add(quoraHighFreq);
        freqHighChildren.add(youtubeHighFreq);
        freqHighChildren.add(facebookoHighFreq);
        freqHighChild.setAttributes(highFreqAppNames);
        freqHighChild.setChildren(freqHighChildren);
        List<String> freqMedAttributes = new ArrayList<>();
        freqMedAttributes.add("early morning");
        freqMedAttributes.add("evening");
        freqMedAttributes.add("midnight");
        freqMedAttributes.add("morning");
        freqMedAttributes.add("noon");
        freqMedAttributes.add("afternoon");
        Node emor = new Node("early morning safe");
        Node even = new Node("network");
        Node midn = new Node("midnight unsafe");
        Node morn = new Node("morning safe");
        Node noon = new Node("dayOfTheWeek");
        Node aftn = new Node("afternoon unsafe");
        emor.setColumn("timeOfTheDay");
        even.setColumn("timeOfTheDay");
        midn.setColumn("timeOfTheDay");
        morn.setColumn("timeOfTheDay");
        noon.setColumn("timeOfTheDay");
        aftn.setColumn("timeOfTheDay");
        emor.setCorrespondingAttribute("Early Morning");
        even.setCorrespondingAttribute("Evening");
        midn.setCorrespondingAttribute("Midnight");
        morn.setCorrespondingAttribute("Morning");
        noon.setCorrespondingAttribute("Noon");
        aftn.setCorrespondingAttribute("Afteroon");
        emor.setResult(-1);
        midn.setResult(4);
        morn.setResult(-1);
        aftn.setResult(4);
        List<Node> timeChildren = new ArrayList<>();
        timeChildren.add(emor);
        timeChildren.add(even);
        timeChildren.add(midn);
        timeChildren.add(morn);
        timeChildren.add(noon);
        timeChildren.add(aftn);
        freqMedChild.setChildren(timeChildren);
        freqMedChild.setAttributes(freqMedAttributes);
        List<String> evenNetwork = new ArrayList<>();
        evenNetwork.add("secure");
        evenNetwork.add("unsecure");
        evenNetwork.add("mobile");
        even.setAttributes(evenNetwork);
        Node networksec = new Node("NwSafe");
        Node networkuns = new Node("NwUnsafeSafe");
        Node networkmob = new Node("NwMob");
        networksec.setResult(-1);
        networkuns.setResult(2);
        networkmob.setResult(-1);
        networksec.setColumn("Network");
        networkuns.setColumn("Network");
        networkmob.setColumn("Network");
        networksec.setCorrespondingAttribute("Secure");
        networkuns.setCorrespondingAttribute("Unsecure");
        networkmob.setCorrespondingAttribute("Mobile");
        List<Node> nwnodes = new ArrayList<>();
        nwnodes.add(networksec);
        nwnodes.add(networkuns);
        nwnodes.add(networkmob);
        even.setChildren(nwnodes);
        List<String> noonday = new ArrayList<>();
        noonday.add("Sunday");
        noonday.add("Monday");
        noonday.add("Tuesday");
        noonday.add("Wedday");
        noonday.add("Thursday");
        noonday.add("Friday");
        noonday.add("Satday");
        noon.setAttributes(noonday);
        Node sunday = new Node("SundaySafe");
        Node monday = new Node("MondaySafe");
        Node tueday = new Node("TuesdayUnSafe");
        Node wedday = new Node("WeddaySafe");
        Node turday = new Node("ThursdaySafe");
        Node friday = new Node("FridaySafe");
        Node satday = new Node("SatdaySafe");
        sunday.setResult(-1);
        monday.setResult(-1);
        tueday.setResult(5);
        wedday.setResult(-1);
        turday.setResult(-1);
        friday.setResult(-1);
        satday.setResult(-1);
        sunday.setColumn("DayOfTheWeek");
        monday.setColumn("DayOfTheWeek");
        tueday.setColumn("DayOfTheWeek");
        wedday.setColumn("DayOfTheWeek");
        turday.setColumn("DayOfTheWeek");
        friday.setColumn("DayOfTheWeek");
        satday.setColumn("DayOfTheWeek");
        sunday.setCorrespondingAttribute("noon");
        monday.setCorrespondingAttribute("noon");
        tueday.setCorrespondingAttribute("noon");
        wedday.setCorrespondingAttribute("noon");
        turday.setCorrespondingAttribute("noon");
        friday.setCorrespondingAttribute("noon");
        satday.setCorrespondingAttribute("noon");
        List<Node> noonnodes = new ArrayList<>();
        noonnodes.add(sunday);
        noonnodes.add(monday);
        noonnodes.add(tueday);
        noonnodes.add(wedday);
        noonnodes.add(turday);
        noonnodes.add(friday);
        noonnodes.add(satday);
        noon.setChildren(noonnodes);
        List<String> freqLowAtts = new ArrayList<>();
        freqLowAtts.add("Male");
        freqLowAtts.add("Female");
        freqLowChild.setAttributes(freqLowAtts);
        Node male = new Node("MaleUnsafe");
        Node fema = new Node("FemaleSafe");
        male.setResult(3);
        fema.setResult(-1);
        male.setCorrespondingAttribute("Male");
        fema.setCorrespondingAttribute("Female");
        male.setColumn("Demographic");
        fema.setColumn("Demographic");
        List<Node> demoList = new ArrayList<>();
        demoList.add(male);
        demoList.add(fema);
        freqLowChild.setChildren(demoList);
        return decisionTree;
    }

}
