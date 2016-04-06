/**
 *
 */

import edu.sjsu.smartsecure.dt.*;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.*;


public class DecisionTreeTest {
  private DecisionTree makeOne() {
    return new DecisionTree();
  }

  private DecisionTree makeOutlookTree() {
    try {
      // example data from http://www.cise.ufl.edu/~ddd/cap6635/Fall-97/Short-papers/2.htm
      return makeOne().setAttributes(new String[]{"App",  "Internet", "Data"})
                      .addExample(   new String[]{"Gmail",      "Secure",   "Low"}, true)
                      .addExample(   new String[]{"Facebook",   "Unsecure", "Low"}, true)
                      .addExample(   new String[]{"Gmail",      "Cellular", "Low"}, true)
                      .addExample(   new String[]{"Facebook",   "Unsecure", "Low"}, true)
                      .addExample(   new String[]{"Instagram",  "Cellular", "Low"}, true)
                      .addExample(   new String[]{"Bofa",       "Secure",   "Low"}, true)
                      .addExample(   new String[]{"Bofa",       "Unsecure", "Low"}, false)
                      .addExample(   new String[]{"Expedia",    "Secure",   "Low"}, true)
                      .addExample(   new String[]{"Yahoo",      "Unsecure", "Low"}, true)
                      .addExample(   new String[]{"yahoomail",  "Secure",   "Low"}, true)
                      .addExample(   new String[]{"Gmail",      "Cellular", "Low"}, true)
                      .addExample(   new String[]{"Quora",      "Unsecure", "Low"}, true)
                      .addExample(   new String[]{"Facebook",   "Secure",   "Normal"}, true)
                      .addExample(   new String[]{"Facebook",   "Secure",   "Normal"}, true)
                      .addExample(   new String[]{"yahoomail",  "Secure",   "Low"}, true)
                      .addExample(   new String[]{"Pandora",    "Unsecure", "High"}, false);
    } catch ( UnknownDecisionException e ) {
      fail();
      return makeOne(); // this is here to shut up compiler.
    }
  }

//  @Test (expected=UnknownDecisionException.class) public void testUnknownDecisionThrowsException() throws UnknownDecisionException {
//    DecisionTree tree = makeOne().setAttributes(new String[]{"Outlook"})
//                                 .setDecisions("Outlook", new String[]{"Sunny", "Overcast"});
//
//    // this causes exception
//    tree.addExample(new String[]{"Rain"}, false);
//  }

  @Test public void testOutlookOvercastApplyReturnsTrue() throws BadDecisionException {
    Map<String, String> case1 = new HashMap<String, String>();
    case1.put("App", "Bofa");
    case1.put("Internet", "Unsecure");
    case1.put("Data", "Low");
    assertTrue(makeOutlookTree().apply(case1));
  }

  @Test (expected=BadDecisionException.class) public void testOutlookRainInsufficientDataThrowsException() throws BadDecisionException {
    Map<String, String> case1 = new HashMap<String, String>();
    case1.put("Outlook", "Rain");
    case1.put("Temperature", "Mild");
    makeOutlookTree().apply(case1);
  }

  public void attributeIsUsedOnlyOnceInTree(Attribute node, List<String> attributes) {
    for ( Attribute child : node.getDecisions().values() ) {
      if ( !child.isLeaf() ) {
        assertFalse( attributes.contains(child.getName()) );
        attributes.add(child.getName());
        attributeIsUsedOnlyOnceInTree(child, attributes);
      }
    }
  }

  @Test public void testAttributeIsUsedOnlyOnceInTree() {
    DecisionTree tree = makeOutlookTree();
    tree.compile();

    List<String> attributeList = new LinkedList<String>();
    attributeList.add(tree.getRoot().getName());
    attributeIsUsedOnlyOnceInTree(tree.getRoot(), attributeList);
  }


  public static void main(String args[]) {
    org.junit.runner.JUnitCore.main("DecisionTreeTest");
  }
}
