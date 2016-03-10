package org.yawlfoundation.yawl.editor.ui.update;

import org.yawlfoundation.yawl.util.StringUtil;
import org.yawlfoundation.yawl.util.XNode;

import java.net.MalformedURLException;
import java.net.URL;

class FileNode {
    String name;
    String md5;
    int size;
    String urlStr;

    FileNode(XNode node, String url) {
        name = node.getAttributeValue("name");
        md5 = node.getAttributeValue("md5");
        size = StringUtil.strToInt(node.getAttributeValue("size"), 0);
        urlStr = url;
    }

    boolean matches(FileNode other) { return md5.equals(other.md5); }

    URL getAbsoluteURL() {
        try {
            return new URL(urlStr + name);
        }
        catch (MalformedURLException mue) {
            return null;
        }
    }
}
