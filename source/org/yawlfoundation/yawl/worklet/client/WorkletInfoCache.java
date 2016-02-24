package org.yawlfoundation.yawl.worklet.client;

import org.yawlfoundation.yawl.worklet.support.WorkletInfo;

import java.io.IOException;
import java.util.*;

/**
 * @author Michael Adams
 * @date 22/02/2016
 */
public class WorkletInfoCache {

    private Map<String, WorkletInfo> _infoMap;
    private boolean _invalidated;


    public WorkletInfoCache() { invalidate(); }


    public void invalidate() { _invalidated = true; }


    public WorkletInfo get(String key) {
        validate();
        return _infoMap.get(key);
    }


    public Set<String> getKeySet() {
        validate();
        return new HashSet<String>(_infoMap.keySet());
    }


    public List<WorkletInfo> getWorkletList() {
        validate();
        List<WorkletInfo> workletList = new ArrayList<WorkletInfo>(_infoMap.values());
        Collections.sort(workletList);
        return workletList;
    }


    public List<String> getURIsForTarget(String target) {
        validate();
        List<String> uriList = new ArrayList<String>();
        for (String key : extractKeysFromTarget(target)) {
            WorkletInfo info = _infoMap.get(key);
            if (info != null) {
                uriList.add(info.getSpecID().getUri());
            }
        }
        return uriList;
    }


    private List<String> extractKeysFromTarget(String target) {
        String[] keys = target.split(";");
        for (int i=0; i < keys.length; i++) {
             keys[i] = keys[i].trim();
        }
        return Arrays.asList(keys);
    }



    private void validate() {
        if (_invalidated) {
            _infoMap = getWorkletMap();
            _invalidated = false;
        }
    }


    private Map<String, WorkletInfo> getWorkletMap() {
        Map<String, WorkletInfo> infoMap = new HashMap<String, WorkletInfo>();
        try {
            for (WorkletInfo info : WorkletClient.getInstance().getWorkletInfoList()) {
                infoMap.put(info.getSpecID().getKey(), info);
            }
        }
        catch (IOException ignore) {

        }
        return infoMap;
    }

}
