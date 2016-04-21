package org.yawlfoundation.yawl.worklet.graph;

/**
 * @author Michael Adams
 * @date 10/02/2016
 */
enum ExletActions {
    Remove("remove", "workitem"),
    RemoveAll("remove", "case"),
    RemoveAllCases("remove", "allcases"),
    Suspend("suspend", "workitem"),
    SuspendAll("suspend", "case"),
    SuspendAllCases("suspend", "allcases"),
    Continue("continue", "workitem"),
    ContinueAll("continue", "case"),
    ContinueAllCases("continue", "allcases"),
    Restart("restart", "workitem"),
    ForceComplete("complete", "workitem"),
    ForceFail("fail", "workitem"),
    Compensate("compensate", ""),
    Arc("", ""),
    Select("", "");


    private String _action;
    private String _target;

    ExletActions(String action, String target) {
        _action = action;
        _target = target;
    }

    String getAction() { return _action; }

    String getTarget() { return _target; }

}
