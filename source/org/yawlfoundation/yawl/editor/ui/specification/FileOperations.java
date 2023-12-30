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

package org.yawlfoundation.yawl.editor.ui.specification;

import org.yawlfoundation.yawl.analyser.util.alloy.AlloyTranslator;
import org.yawlfoundation.yawl.analyser.util.alloy.AlloyTestGenerator;
import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.editor.ui.specification.io.SpecificationWriter;
import org.yawlfoundation.yawl.editor.ui.specification.pubsub.FileState;
import org.yawlfoundation.yawl.editor.ui.specification.pubsub.FileStateListener;
import org.yawlfoundation.yawl.editor.ui.specification.pubsub.Publisher;
import org.yawlfoundation.yawl.editor.ui.specification.validation.AnalysisResultsParser;
import org.yawlfoundation.yawl.editor.ui.specification.validation.SpecificationValidator;
import org.yawlfoundation.yawl.editor.ui.specification.validation.ValidationResultsParser;
import org.yawlfoundation.yawl.editor.ui.util.CursorUtil;
import org.yawlfoundation.yawl.elements.YNet;

/**
 * @author Michael Adams
 * @date 26/07/12
 */
public class FileOperations {

    public static void open() {
        processAction(Action.Open);
    }

    public static void open(String fileName) {
        processAction(Action.OpenFile, fileName);
    }

    public static void convertToAlloy() {
        processAction(Action.ConvertToAlloy);
    }

    public static void testWithAlloy_RACC() {
        processAction(Action.TestWithAlloy_RACC);
    }
    public static void mutateRACCTests() {
        processAction(Action.MutateAlloyTests);
    }

    public static void validate() {
        processAction(Action.Validate);
    }

    public static void analyse() {
        processAction(Action.Analyse);
    }

    public static void save() {
        processAction(Action.Save);
    }

    public static void saveAs() {
        processAction(Action.SaveAs);
    }

    public static void close() {
        processAction(Action.Close);
    }

    public static void exit() {
        processAction(Action.Exit);
    }

    private static void processAction(Action action, String... args) {
        CursorUtil.showWaitCursor();
        SpecificationFileHandler handler = new SpecificationFileHandler();
        Publisher publisher = Publisher.getInstance();
        publisher.publishFileBusyEvent();

        switch (action) {
            case Open -> handler.openFile();
            case OpenFile -> {
                handler.openFile(args[0]);
            }
            case ConvertToAlloy -> {
                YNet rootNet = new SpecificationWriter().cleanSpecification().getRootNet();
                String code = new AlloyTranslator().translate(rootNet);
//                System.out.println(code);
                YAWLEditor.getInstance().showAlloyCode(code);
            }
            case TestWithAlloy_RACC -> {
                YNet rootNet = new SpecificationWriter().cleanSpecification().getRootNet();
                String code = new AlloyTestGenerator(rootNet).test(rootNet);
                YAWLEditor.getInstance().showAlloyRACCTestResults(code);
            }
            case MutateAlloyTests -> {
                YNet rootNet1 = new SpecificationWriter().cleanSpecification().getRootNet();
                String code1 = new AlloyTestGenerator(rootNet1).testWithMutations(rootNet1);
                YAWLEditor.getInstance().showAlloyRACCTestResults(code1);
            }
            case Validate -> {
                YAWLEditor.getInstance().showProblemList("Validation Results",
                        new ValidationResultsParser().parse(
                                new SpecificationValidator().getValidationResults()));
            }
            case Analyse -> {
                new AnalysisResultsParser().showAnalysisResults();
            }
            case Save -> {
                handler.saveFile();
            }
            case SaveAs -> {
                handler.saveFileAs();
            }
            case Close -> {
                handler.closeFile();
            }
            case Exit -> {

                // need to listen for file save completion (or file close)
                FileStateListener fsListener = state -> {
                    if (state == FileState.Closed) {
                        System.exit(0);
                    }
                };
                publisher.subscribe(fsListener);

                if (!handler.closeFileOnExit()) {
                    publisher.unsubscribe(fsListener);       // exit cancelled
                }
            }
        }

        publisher.publishFileUnbusyEvent();
        CursorUtil.showDefaultCursor();
    }


    private enum Action {Open, OpenFile, Validate, Analyse, ConvertToAlloy, Save, SaveAs, Close, Exit,
        TestWithAlloy_RACC, MutateAlloyTests}

}
