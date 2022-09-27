package org.yawlfoundation.yawl.analyser.util;

import org.yawlfoundation.yawl.analyser.YAnalyserEvent;
import org.yawlfoundation.yawl.analyser.YAnalyserEventListener;
import org.yawlfoundation.yawl.analyser.YAnalyserEventType;
import org.yawlfoundation.yawl.analyser.YAnalyserOptions;
import org.yawlfoundation.yawl.analyser.util.alloy.AlloyValidator;
import org.yawlfoundation.yawl.elements.YDecomposition;
import org.yawlfoundation.yawl.elements.YNet;
import org.yawlfoundation.yawl.elements.YSpecification;
import org.yawlfoundation.yawl.elements.YTask;
import org.yawlfoundation.yawl.util.StringUtil;

import java.util.Set;

public class YAWLAlloyAnalyser {

    private boolean _cancelled = false;
    private Set<YAnalyserEventListener> _listeners;


    public String analyse(YSpecification specification, YAnalyserOptions options,
                          Set<YAnalyserEventListener> listeners, int maxMarkings) {

        // short circuit if no reset options selected
        if (!options.isAlloyAnalysis()) return "";

        long startTime = System.currentTimeMillis();
        _listeners = listeners;
        announceProgressEvent(YAnalyserEventType.Init, null, null);

        StringBuilder msgBuffer = new StringBuilder(400);

        for (YDecomposition decomposition : specification.getDecompositions()) {
            announceProgressMessage("# Elements in the original YAWL net (" +
                    decomposition.getID() + "): " + decomposition.getAttributes().size());
            if (decomposition instanceof YNet yNet) {
                if (options.isAlloyOrJoinCycle()) {
                    String result = checkOrJoinCycles(yNet);
                    System.out.println(result);
                    msgBuffer.append(result);
                }
            }
        }

        announceProgressMessage("Duration: " + (System.currentTimeMillis() - startTime) +
                " milliseconds");
        announceProgressEvent(YAnalyserEventType.Completed, null, null);

        return _cancelled ? StringUtil.wrap("Analysis cancelled.", "cancelled") :
                formatXMLMessageResults(msgBuffer.toString());
    }

    public void cancel() {
        _cancelled = true;
    }  // break-out flag

    public boolean isCancelled() {
        return _cancelled;
    }


    /********************************************************************************/
    private String checkOrJoinCycles(YNet yNet) {
        try {
            return new AlloyValidator(yNet).checkOrJoinInLoop();
        } catch (Exception e) {
            return formatXMLMessage(e.getMessage(), false);
        }
    }


    private boolean containsORjoins(YNet yNet) {
        for (YTask task : yNet.getNetTasks()) {
            if (task.getJoinType() == YTask._OR) return true;
        }
        return false;
    }


    private String formatXMLMessageResults(String msg) {
        return StringUtil.wrap(msg, "alloyAnalysisResults");
    }

    /**
     * used for formatting xml messages.
     * Message could be a warning or observation.
     */
    private String formatXMLMessage(String msg, boolean isObservation) {
        return StringUtil.wrap(msg, isObservation ? "observation" : "warning");
    }


    public void announceProgressMessage(String message) {
        announceProgressEvent(YAnalyserEventType.Message, null, message);
    }


    protected void announceProgressEvent(YAnalyserEventType eventType,
                                         String source, String message) {
        announceProgressEvent(new YAnalyserEvent(eventType, source, message));
    }


    protected void announceProgressEvent(YAnalyserEvent event) {
        if (_listeners != null) {
            for (YAnalyserEventListener listener : _listeners) {
                listener.yAnalyserEvent(event);
            }
        }
    }

}