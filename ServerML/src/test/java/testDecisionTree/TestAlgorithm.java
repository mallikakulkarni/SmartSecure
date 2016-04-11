package testDecisionTree;

import edu.sjsu.smartsecure.decisionTree.Algorithm;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mallika on 4/7/16.
 */
public class TestAlgorithm {

    @Test
    public void testCreateDecisionTree() {
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
    }


}
