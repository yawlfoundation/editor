package org.yawlfoundation.yawl.worklet.upload;

import org.jdom2.Document;
import org.jdom2.Element;
import org.yawlfoundation.yawl.engine.YSpecificationID;
import org.yawlfoundation.yawl.util.JDOMUtil;
import org.yawlfoundation.yawl.util.StringUtil;
import org.yawlfoundation.yawl.worklet.client.WorkletClient;
import org.yawlfoundation.yawl.worklet.support.WorkletSpecification;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @author Michael Adams
 * @date 22/01/2016
 */
public class UpLoader {

    protected static final String RULE_FILE = ".xrs";
    protected static final String WORKLET_FILE = ".yawl";

    private int _workletCount;
    private int _rulesCount;
    private final WorkletClient _client = WorkletClient.getInstance();


    public UpLoader() { }


    public List<String> upload(File f) {
        resetCounters();
        String error = null;
        if (f.isDirectory()) {
            return loadDir(f);
        }
        else if (f.isFile()) {
            error = loadFile(f);
        }
        return error != null ? Collections.singletonList(error) :
                Collections.<String>emptyList();
    }


    public int getWorkletCount() { return _workletCount; }

    public int getRulesCount() { return _rulesCount; }


    private String loadFile(File f) {
        if (! f.exists()) {
           return "File does not exist: " + f.getAbsolutePath();
        }
        else if (isWorkletFile(f)) {
            return addWorklet(f);
        }
        else if (isRulesFile(f)) {
            return addRuleSet(f);
        }
        return "Invalid file extension: " + f.getAbsolutePath();
    }


    private List<String> loadDir(File dir) {
        List<String> errors = new ArrayList<String>();
        for (File f : getFilesRecursive(dir)) {
            if (hasCorrectExtension(f)) {
                String error = loadFile(f);
                if (error != null) {
                    errors.add(error);
                }
            }
        }
        return errors;
    }


    // if f is a dir, returns a list of all files in this dir and its subdirs
    // else returns a single element list
    private List<File> getFilesRecursive(File f) {
        List<File> fileList = new ArrayList<File>();
        if (! f.isDirectory()) {
            fileList.add(f);
        }
        else {
            File[] files = f.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) fileList.add(file);
                    else fileList.addAll(getFilesRecursive(file));
                }
            }
        }
        return fileList;
    }


    private boolean hasCorrectExtension(File f) {
        return isWorkletFile(f) || isRulesFile(f);
    }


    private boolean isWorkletFile(File f) { return isFileExtension(f, WORKLET_FILE); }


    private boolean isRulesFile(File f) { return isFileExtension(f, RULE_FILE); }


    private boolean isFileExtension(File f, String extension) {
         return f.getName().endsWith(extension);
    }


    private String addWorklet(File f) {
        String xml = StringUtil.fileToString(f);
        if (xml == null) {
            return "File has no content: " + f.getAbsolutePath();
        }

        Document doc = JDOMUtil.stringToDocument(xml);      // quick structure check
        if (doc == null) {
            return "Invalid file content: " + f.getAbsolutePath();
        }

        WorkletSpecification wSpec = new WorkletSpecification(xml);
        try {
            _client.addWorklet(wSpec.getSpecID(), xml);
            _workletCount++;
            return null;
        }
        catch (IOException ioe) {
            return ioe.getMessage();
        }
    }


    private String addRuleSet(File f) {
        Document doc = JDOMUtil.fileToDocument(f);
        if (doc == null) {
            return "Invalid file content: " + f.getAbsolutePath();
        }

        YSpecificationID specID = getSpecID(doc);
        if (specID != null) {
            return addRuleSet(specID, doc);
        }

        String processName = getProcessName(doc);
        if (processName != null) {
            return addRuleSet(processName, doc);
        }

        return "Missing required specification or process attributes in file: " +
                f.getAbsolutePath();
    }


    private String addRuleSet(YSpecificationID specID, Document doc) {
        try {
            _client.addRuleSet(specID, JDOMUtil.documentToString(doc));
            _rulesCount++;
            return null;
        }
        catch (IOException ioe) {
            return ioe.getMessage();
        }
    }


    private String addRuleSet(String processName, Document doc) {
        try {
            _client.addRuleSet(processName, JDOMUtil.documentToString(doc));
            return null;
        }
        catch (IOException ioe) {
            return ioe.getMessage();
        }
    }


    private YSpecificationID getSpecID(Document doc) {
        Element root = doc.getRootElement();
        if (root != null) {
            String uri = root.getAttributeValue("uri");
            if (uri != null) {
                String version = root.getAttributeValue("version");
                String identifier = root.getAttributeValue("identifier");
                return new YSpecificationID(identifier, version, uri);
            }
        }
        return null;
    }


    private String getProcessName(Document doc) {
        Element root = doc.getRootElement();
        if (root != null) {
            return root.getAttributeValue("name");
        }
        return null;
    }


    private void resetCounters() {
        _workletCount = 0;
        _rulesCount = 0;
    }

}
