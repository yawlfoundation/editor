package org.yawlfoundation.yawl.editor.ui.util;

import org.yawlfoundation.yawl.engine.interfce.Interface_Client;
import org.yawlfoundation.yawl.reporter.Report;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Michael Adams
 * @date 2/10/2015
 */
public class ErrorReporter extends Interface_Client {

    private static final String URI = "http://winyawl.win.tue.nl:8080/reporter/i";
    private static final String NA = "N/A";

    public Report prepare(String title, Exception e) throws IOException {
        return createReport(title, e);
    }

    public String send(Report report) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("report", report.toXML());
        return executePost(URI, params) ;
    }


    private Report createReport(String title, Exception e) {
        Report report = new Report(title);
        report.addContent("Exception message", e.getMessage());
        report.addContent("Exception Stack Trace", concatTrace(e));
        report.addContent("java.version", System.getProperty("java.version", NA));
        report.addContent("java.runtime.version",
                System.getProperty("java.runtime.version", NA));
        report.addContent("java.home", System.getProperty("java.home", NA));
        report.addContent("file.encoding", System.getProperty("file.encoding", NA));
        report.addContent("os.name", System.getProperty("os.name", NA));
        report.addContent("os.version", System.getProperty("os.version", NA));
        report.addContent("os.arch", System.getProperty("os.arch", NA));
        report.addContent("user.country", System.getProperty("user.country", NA));
        report.addContent("user.dir", System.getProperty("user.dir", NA));
        report.addContent("user.language", System.getProperty("user.language", NA));
        return report;
    }


    private String concatTrace(Exception e) {
        StackTraceElement[] elements = e.getStackTrace();
        if (elements != null) {
            StringBuilder s = new StringBuilder();
            for (StackTraceElement element : elements) {
                s.append(element.toString()).append('\n');
            }
            return s.toString();
        }
        return NA;
    }

    public static void main(String[] args) {
        Report r = new Report("test error");
        r.addContent("Error 1", "this is some text");
        r.addContent("Exception 2", "this is the exception text");
        ErrorReporter er = new ErrorReporter();
        try {
            System.out.println(er.send(r));
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}
