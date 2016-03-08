package org.yawlfoundation.yawl.editor.ui.update;

import org.yawlfoundation.yawl.util.StringUtil;
import org.yawlfoundation.yawl.util.XNode;

class FileNode {
    String name;
    String md5;
    int size;
    String url;

    FileNode(XNode node, String url) {
        name = node.getAttributeValue("name");
        md5 = node.getAttributeValue("md5");
        size = StringUtil.strToInt(node.getAttributeValue("size"), 0);
        this.url = url;
    }

    boolean matches(FileNode other) { return md5.equals(other.md5); }
}
