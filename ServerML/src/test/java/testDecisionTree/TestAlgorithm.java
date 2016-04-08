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
        columnHeaders.add("Outlook");
        columnHeaders.add("temperature");
        columnHeaders.add("humidity");
        columnHeaders.add("wind");
        Algorithm.createDecisionTree(columnHeaders);
    }


}
