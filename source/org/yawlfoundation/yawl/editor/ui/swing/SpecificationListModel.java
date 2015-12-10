package org.yawlfoundation.yawl.editor.ui.swing;

import org.yawlfoundation.yawl.engine.YSpecificationID;

import javax.swing.*;

 public abstract class SpecificationListModel extends DefaultListModel {

    protected final java.util.List<?> items;

    protected SpecificationListModel(java.util.List<?> items) { this.items = items; }

    public int getSize() { return items != null ? items.size() : 0; }

    public Object getElementAt(int i) { return items.get(i).toString(); }

    public abstract YSpecificationID getSelectedID(int i);

}
