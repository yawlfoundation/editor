package org.yawlfoundation.yawl.editor.ui.properties.data.validation;

import net.sf.saxon.s9api.SaxonApiException;
import org.jdom2.Document;
import org.yawlfoundation.yawl.util.SaxonUtil;
import org.yawlfoundation.yawl.util.StringUtil;

import java.util.Collections;
import java.util.List;

/**
 * @author Michael Adams
 * @date 6/12/17
 */
public class XQueryEvaluator {

    private XQueryEvaluator() {
    }

    public static String evaluate(String query, Document document, boolean isMultiInstance)
            throws SaxonApiException {
        query = prepare(query);
        String evaluated = SaxonUtil.evaluateQuery(query, document);
        return cleanup(evaluated, isMultiInstance);
    }


    public static List<String> compile(String query) {
        try {
            SaxonUtil.compileXQuery(prepare(query));
            return SaxonUtil.getCompilerMessages();
        }
        catch (SaxonApiException e) {
            String message = e.getMessage();
            if (message.contains("\n")) {
                message = message.split("\n")[1].trim();
            }
            return Collections.singletonList(message);
        }
    }


    protected static String prepare(String query) {
        if (query == null) return null;
        query = query.startsWith("<") ? wrap(query) : enclose(query);
        if (query.contains("[")) {
            query = maskIndex(query);
        }
        return query;
    }


    private static String unWrap(String evaluated) {
        return evaluated.substring(9, evaluated.length() - 10);
    }


    private static String maskIndex(String query) {
        int i = query.indexOf('[');
        int j = query.lastIndexOf(']');
        if (i > -1 && j > -1 && i < j) {
            StringBuilder sb = new StringBuilder();
            sb.append(query.substring(0, i+1));
            sb.append('1');
            sb.append(query.substring(j));
            return sb.toString();
        }
        return query;
    }

    
    private static String cleanup(String evaluated, boolean isMultiInstance) {
        if (evaluated.equals("<foo_bar/>")) {
            return "";
        }
        evaluated = unWrap(evaluated);
        if (isMultiInstance) {
            evaluated = StringUtil.unwrap(StringUtil.unwrap(evaluated));
        }
        return evaluated;
    }


    private static String wrap(String query) {
        return StringUtil.wrap(query, "foo_bar");
    }


    private static String enclose(String query) {
        StringBuilder sb = new StringBuilder(query.length() + 21);
        sb.append("<foo_bar>{").append(query).append("}</foo_bar>");
        return sb.toString();
    }

}
