package org.yawlfoundation.yawl.worklet.dialog;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Adams
 * @date 9/12/2015
 */
public class ErrorMessageShortener {


    public String getConditionError(String msg) {
        if (msg == null) {
            return "";
        }
        if (msg.equals("Cannot evaluate tree: condition is empty")) {
            return "Condition required";
        }
        if (msg.equals("Cannot evaluate tree: data element is null")) {
            return "No data for condition evaluation";
        }
        if (msg.equals("Expression does not evaluate to a boolean value")) {
            return "Condition does not evaluate to true";
        }
        if (msg.startsWith("Invalid XPath")) {
            return "Invalid XPath expression";
        }
        if (msg.startsWith("Expression contains an invalid token")) {
            return "Invalid char at position" + msg.substring(msg.lastIndexOf(' '));
        }
        if (msg.startsWith("Expression is invalid")) {
            return "Mis-ordered tokens";
        }
        if (msg.equals("Expression contains unterminated literal string")) {
            return "Unterminated literal string";
        }
        if (msg.equals("Expression contains an invalid literal numeric token")) {
            return "Invalid literal numeric value";
        }
        if (msg.startsWith("Left and right operands")) {
            return "Incompatible operands";
        }
        if (msg.endsWith("operands")) {
            return "Invalid operator for operands";
        }

        return msg;
    }


    public List<String> getExletError(String msg) {
        List<String> list = new ArrayList<String>();
        list.add(msg);
        if (msg.startsWith("A conclusion with a 'select'")) {
            list.add(0, "Select not allowed in mixed exlet");
        }
        else if (msg.contains("' is invalid for action '")) {
            list.add(0, "Invalid target " + msg.substring(msg.indexOf('[')));
        }
        else if (msg.contains("' is missing a valid")) {
            list.add(0, "Invalid worklet target " + msg.substring(msg.indexOf('[')));
        }
        else if (msg.contains("a finalized state")) {
            list.add(0, msg.substring(0, msg.indexOf('.')) +
                    msg.substring(msg.indexOf('[') -1));
        }
        else if (msg.startsWith("Invalid 'continue' action.")) {
            list.add(0, msg.substring(0, msg.indexOf('.')) +
                    msg.substring(msg.indexOf('[') -1));
        }
        else if (msg.contains("left in a suspended state")) {
            list.add(0, "Invalid action set");
        }
        return list;
    }
}
