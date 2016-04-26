package org.yawlfoundation.yawl.worklet.dialog;

import org.yawlfoundation.yawl.editor.ui.elements.model.YAWLAtomicTask;
import org.yawlfoundation.yawl.editor.ui.net.NetGraphModel;
import org.yawlfoundation.yawl.editor.ui.net.utilities.NetUtilities;
import org.yawlfoundation.yawl.editor.ui.specification.SpecificationModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.Vector;

/**
 * @author Michael Adams
 * @date 17/12/2015
 */
public class TaskComboBox extends JComboBox {

    private final Vector<YAWLAtomicTask> TASK_LIST = getTaskList();

    private YAWLAtomicTask _selectedTask;
    private boolean _addingItems;


    public TaskComboBox(YAWLAtomicTask task) {
        super();
        _selectedTask = task;
        setRenderer(new AtomicTaskRenderer());
        setEnabled(false);
    }


    // add & view dialogs listen, and when they do this combo must listen too
    public void listenForSelections() {
        super.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED && !_addingItems) {
                    _selectedTask = (YAWLAtomicTask) getSelectedItem();
                }
            }
        });
    }


    public YAWLAtomicTask getSelectedTask() {
        if (_selectedTask == null && getItemCount() > 0) {
            _selectedTask = (YAWLAtomicTask) getItemAt(0);
            setSelectedItem(_selectedTask);
        }
        return _selectedTask;
    }


    // adding dialog
    public void setItems() {
        setItems(TASK_LIST);
    }


    // viewing dialog
    public void setItems(Set<String> taskIDs) {
        Vector<YAWLAtomicTask> tasks = new Vector<YAWLAtomicTask>();
        if (taskIDs != null) {
            for (String taskID : taskIDs) {
                YAWLAtomicTask task = getTask(taskID);
                if (task != null) {
                    tasks.add(task);
                }
            }
            sortTaskList(tasks);
        }
        setItems(tasks);
    }


    // replacing dialog
    public void setItem(String taskID) {
        removeAllItems();
        if (taskID != null) {
            YAWLAtomicTask task = getTask(taskID);
            if (task != null) {
                addItem(task);
            }
        }
        setEnabled(false);
    }


    private void setItems(Vector<YAWLAtomicTask> tasks) {
        removeAllItems();
        boolean hasItems = ! (tasks == null || tasks.isEmpty());
        if (hasItems) {
            _addingItems = true;                          // suppress selection changes
            for (YAWLAtomicTask atomicTask : tasks) {
                addItem(atomicTask);
            }
            if (_selectedTask != null) {
                setSelectedItem(_selectedTask);
            }
            else setSelectedIndex(0);                     // show first item

            _addingItems = false;
        }
        setEnabled(hasItems);
    }


    private Vector<YAWLAtomicTask> getTaskList() {
        Vector<YAWLAtomicTask> taskVector = new Vector<YAWLAtomicTask>();
        for (NetGraphModel model : SpecificationModel.getNets()) {
            for (YAWLAtomicTask netTask : NetUtilities.getAtomicTasks(model)) {
                taskVector.add(netTask);
            }
        }
        sortTaskList(taskVector);
        return taskVector;
    }


    private void sortTaskList(Vector<YAWLAtomicTask> taskVector) {
        Collections.sort(taskVector, new Comparator<YAWLAtomicTask>() {
            @Override
            public int compare(YAWLAtomicTask t1, YAWLAtomicTask t2) {
                if (t1 == null) return -1;
                if (t2 == null) return 1;
                return t1.getID().compareTo(t2.getID());
            }
        });
    }


    private YAWLAtomicTask getTask(String taskID) {
        for (YAWLAtomicTask task : TASK_LIST) {
            if (task.getID().equals(taskID)) {
                return task;
            }
        }
        return null;
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
