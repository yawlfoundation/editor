/*
 * Copyright (c) 2004-2013 The YAWL Foundation. All rights reserved.
 * The YAWL Foundation is a collaboration of individuals and
 * organisations who are committed to improving workflow technology.
 *
 * This file is part of YAWL. YAWL is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation.
 *
 * YAWL is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with YAWL. If not, see <http://www.gnu.org/licenses/>.
 */

package org.yawlfoundation.yawl.editor.ui.specification.validation;

import org.yawlfoundation.yawl.analyser.YAnalyser;
import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.editor.ui.swing.AnalysisDialog;
import org.yawlfoundation.yawl.editor.ui.util.UserSettings;
import org.yawlfoundation.yawl.util.StringUtil;
import org.yawlfoundation.yawl.util.XNode;
import org.yawlfoundation.yawl.util.XNodeParser;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AnalysisResultsParser implements AnalysisCanceller {

    private YAnalyser _analyser;


    // triggered from menu or toolbar - do via swing worker
    public void showAnalysisResults() {
        final AnalysisWorker worker = new AnalysisWorker();
        System.out.println("show analysis results");
        worker.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getNewValue() == SwingWorker.StateValue.DONE) {
                    YAWLEditor.getInstance().showProblemList("Analysis Results",
                            new ValidationResultsParser().parse(
                                    parseRawResultsIntoList(worker.getResult(), "resetAnalysisResults", "Reset Net")));
                    YAWLEditor.getInstance().showAlloyProblemList("ALLoy Analysis Results",
                            new ValidationResultsParser().parse(
                                    parseRawResultsIntoList(worker.getAlloyValidationResult(), "alloyAnalysisResults", "Alloy")));
                }
            }
        });

        worker.execute();
    }


    // triggered on file save with analyse option - already in a swing worker
    public List<String> getAnalysisResults(String specXML) {
        AnalysisDialog messageDlg = AnalysisUtil.createDialog(this);
        _analyser = new YAnalyser();
        String result = AnalysisUtil.analyse(_analyser, messageDlg, specXML);
        return parseRawResultsIntoList(result, "resetAnalysisResults", "Reset Net");
    }

    // triggered on file save with alloy analyse option - already in a swing worker
    public List<String> getAlloyAnalysisResults(String specXML) {
        AnalysisDialog messageDlg = AnalysisUtil.createDialog(this);
        _analyser = new YAnalyser();
        String result = AnalysisUtil.alloyAnalyse(_analyser, messageDlg, specXML);
        System.out.printf("alloy analysis result: %s%n",result);
        return parseRawResultsIntoList(result, "alloyAnalysisResults", "Alloy");
    }


    public void cancel() {
        if (_analyser != null) _analyser.cancelAnalysis();
    }


    protected List<String> parseRawResultsIntoList(String rawAnalysisXML, String childNodeName, String messagePrefix) {
        System.out.println("-----------------------------------------");
        System.out.println(rawAnalysisXML);
        System.out.println("-----------------------------------------");
        if (StringUtil.isNullOrEmpty(rawAnalysisXML)) {
            return Collections.emptyList();
        }
        if (rawAnalysisXML.startsWith("<error>") || rawAnalysisXML.startsWith("<cancelled>")) {
            return Collections.singletonList(StringUtil.unwrap(rawAnalysisXML));
        }
        List<String> resultList = new ArrayList<String>();
        XNode parentNode = new XNodeParser().parse(rawAnalysisXML);
        if (parentNode != null) {
            parseAnalysisResults(resultList, parentNode, messagePrefix, childNodeName);
            parseWofYawlResults(resultList, parentNode);
        }
        else {
            resultList.add("Analysis Error: Malformed analysis results.");
        }
        System.out.println("-----------------------------------------");
        for (String message: resultList) {
            System.out.println(message);
        }
        System.out.println("-----------------------------------------");
        return resultList;
    }


    protected void parseAnalysisResults(List<String> resultsList, XNode parentNode, String messagePrefix, String childNodeName) {
        XNode analyseNode = parentNode.getChild(childNodeName);
        if (analyseNode != null) {
            String cancelMsg = analyseNode.getChildText("cancelled");
            if (cancelMsg != null) {
                resultsList.add(cancelMsg);
            }
            else {
                parseAnalyseErrors(resultsList, analyseNode, messagePrefix);
                parseAnalyseWarnings(resultsList, analyseNode, messagePrefix);
                if (UserSettings.getShowObservations()) {
                    parseAnalyseObservations(resultsList, analyseNode, messagePrefix);
                }
            }
        }
    }


    protected void parseAnalyseErrors(List<String> resultsList, XNode resetNode, String messagePrefix) {
        String prefix = String.format("%s Analysis Error: ", messagePrefix);
        parseResultsIntoList("error", prefix, resultsList, resetNode);
    }


    protected void parseAnalyseWarnings(List<String> resultsList, XNode resetNode, String messagePrefix) {
        String prefix = String.format("%s Analysis Warning: ", messagePrefix);
        parseResultsIntoList("warning", prefix, resultsList, resetNode);
    }


    protected void parseAnalyseObservations(List<String> resultsList, XNode resetNode, String messagePrefix) {
        String prefix = String.format("%s Analysis Observation: ", messagePrefix);
        parseResultsIntoList("observation", prefix, resultsList, resetNode);
    }


    protected void parseWofYawlResults(List<String> resultsList, XNode parentNode) {
        XNode wofYawlNode = parentNode.getChild("wofYawlAnalysisResults");
        if (wofYawlNode != null) {
            parseWofYawlStructuralWarnings(resultsList, wofYawlNode);
            parseWofYawlBehaviouralWarnings(resultsList, wofYawlNode);
        }
    }


    protected void parseWofYawlStructuralWarnings(List<String> resultsList,
                                                  XNode wofYawlNode) {
        String prefix = "WofYAWL Structural Warning: ";
        parseResultsIntoList("structure", prefix, resultsList, wofYawlNode);
    }


    protected void parseWofYawlBehaviouralWarnings(List<String> resultsList,
                                                   XNode wofYawlNode) {
        String prefix = "WofYAWL Behavioural Warning: ";
        parseResultsIntoList("behavior", prefix, resultsList, wofYawlNode);
    }


    private void parseResultsIntoList(String childName, String prefix,
                                      List<String> resultsList, XNode node) {
        for (XNode childNode : node.getChildren(childName)) {
            String msg = childNode.getText();
            if (msg != null) resultsList.add(prefix + msg);
        }
    }

}
