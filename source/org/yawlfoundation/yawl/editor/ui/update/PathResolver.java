package org.yawlfoundation.yawl.editor.ui.update;

import org.yawlfoundation.yawl.util.XNode;

import java.util.HashMap;
import java.util.Map;

public class PathResolver {

    String host;
    String base;
    Map<String, String> paths;

    PathResolver(XNode pathsNode) {
        paths = new HashMap<String, String>();
        if (pathsNode != null) {
            for (XNode pathNode : pathsNode.getChildren()) {
                String id = pathNode.getAttributeValue("id");
                String value = pathNode.getText();
                if ("host".equals(id)) {
                    host = value;
                }
                else if ("base".equals(id)) {
                    base = value;
                }
                else {
                    paths.put(id, value);
                }
            }
        }
    }


    String get(String id) {
        String path = paths.get(id);
        return path != null ? host + base + path : "";
    }

}
