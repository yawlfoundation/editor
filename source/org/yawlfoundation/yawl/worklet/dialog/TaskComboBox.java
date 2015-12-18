package org.yawlfoundation.yawl.worklet.dialog;

import org.yawlfoundation.yawl.editor.ui.elements.model.AtomicTask;
import org.yawlfoundation.yawl.editor.ui.elements.model.YAWLAtomicTask;
import org.yawlfoundation.yawl.editor.ui.net.NetGraphModel;
import org.yawlfoundation.yawl.editor.ui.net.utilities.NetUtilities;
import org.yawlfoundation.yawl.editor.ui.specification.SpecificationModel;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

/**
 * @author Michael Adams
 * @date 17/12/2015
 */
public class TaskComboBox extends JComboBox {

    private final ListCellRenderer BASIC_RENDERER = new BasicComboBoxRenderer();
    private final ListCellRenderer TASK_RENDERER = new AtomicTaskRenderer();
    private final Vector<AtomicTask> TASK_LIST = getTaskList();

    private AtomicTask _selectedTask;
    private boolean _adding;


    public TaskComboBox(AtomicTask task) {
        super();
        _selectedTask = task;
        setEnabled(false);
    }


    // only add dialog listens, and when it does this combo must listen too
    public void listenForSelections() {
        super.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED && !_adding) {
                    _selectedTask = (AtomicTask) getSelectedItem();
                }
            }
        });
    }


    public AtomicTask getSelectedTask() {
        if (_selectedTask == null && ! TASK_LIST.isEmpty()) {
            _selectedTask = TASK_LIST.elementAt(0);
            setSelectedItem(_selectedTask);
        }
        return _selectedTask;
    }


    // adding dialog
    public void setItems() {
        reset();
        _adding = true;                                 // suppress selection changes
        for (AtomicTask atomicTask : TASK_LIST) {
            addItem(atomicTask);
        }
        setSelectedItem(_selectedTask);
        setEnabled(true);
        switchRenderer(TASK_RENDERER);
        _adding = false;
    }


    // replacing & viewing dialog
    public void setItem(String taskID) {
        reset();
        if (taskID != null) {
            addItem(taskID);
        }
        switchRenderer(BASIC_RENDERER);
    }


    private void reset() {
        removeAllItems();
        setEnabled(false);
    }


    private void switchRenderer(ListCellRenderer renderer) {
        if (! renderer.equals(getRenderer())) {
            setRenderer(renderer);
        }
    }


    private Vector<AtomicTask> getTaskList() {
        Vector<AtomicTask> taskVector = new Vector<AtomicTask>();
        for (NetGraphModel model : SpecificationModel.getNets()) {
            for (YAWLAtomicTask netTask : NetUtilities.getAtomicTasks(model)) {
                 if (netTask instanceof AtomicTask) {
                     taskVector.add((AtomicTask) netTask);
                 }
            }
        }

        Collections.sort(taskVector, new Comparator<AtomicTask>() {
            @Override
            public int compare(AtomicTask t1, AtomicTask t2) {
                if (t1 == null) return -1;
                if (t2 == null) return 1;
                return t1.getID().compareTo(t2.getID());
            }
        });

        return taskVector;
    }


    /*********************************************************************/

    class AtomicTaskRenderer implements ListCellRenderer {

        protected final DefaultListCellRenderer defaultRenderer =
                new DefaultListCellRenderer();

        public Component getListCellRendererComponent(JList jList, Object o,
                                                      int i, boolean b, boolean b1) {
            JLabel label = (JLabel) defaultRenderer.getListCellRendererComponent(
                    jList, o, i, b, b1);
            label.setText(o != null ? ((YAWLAtomicTask) o).getLabel() : null);
            return label;
        }

    }

}
