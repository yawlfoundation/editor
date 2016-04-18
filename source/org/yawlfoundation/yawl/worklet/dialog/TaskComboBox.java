package org.yawlfoundation.yawl.worklet.dialog;

import org.yawlfoundation.yawl.editor.ui.elements.model.AtomicTask;
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

    private final Vector<AtomicTask> TASK_LIST = getTaskList();

    private AtomicTask _selectedTask;
    private boolean _addingItems;


    public TaskComboBox(AtomicTask task) {
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
                    _selectedTask = (AtomicTask) getSelectedItem();
                }
            }
        });
    }


    public AtomicTask getSelectedTask() {
        if (_selectedTask == null && getItemCount() > 0) {
            _selectedTask = (AtomicTask) getItemAt(0);
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
        Vector<AtomicTask> tasks = new Vector<AtomicTask>();
        if (taskIDs != null) {
            for (String taskID : taskIDs) {
                AtomicTask task = getTask(taskID);
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
            AtomicTask task = getTask(taskID);
            if (task != null) {
                addItem(task);
            }
        }
        setEnabled(false);
    }


    private void setItems(Vector<AtomicTask> tasks) {
        removeAllItems();
        boolean hasItems = ! (tasks == null || tasks.isEmpty());
        if (hasItems) {
            _addingItems = true;                          // suppress selection changes
            for (AtomicTask atomicTask : tasks) {
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


    private Vector<AtomicTask> getTaskList() {
        Vector<AtomicTask> taskVector = new Vector<AtomicTask>();
        for (NetGraphModel model : SpecificationModel.getNets()) {
            for (YAWLAtomicTask netTask : NetUtilities.getAtomicTasks(model)) {
                 if (netTask instanceof AtomicTask) {
                     taskVector.add((AtomicTask) netTask);
                 }
            }
        }
        sortTaskList(taskVector);
        return taskVector;
    }


    private void sortTaskList(Vector<AtomicTask> taskVector) {
        Collections.sort(taskVector, new Comparator<AtomicTask>() {
            @Override
            public int compare(AtomicTask t1, AtomicTask t2) {
                if (t1 == null) return -1;
                if (t2 == null) return 1;
                return t1.getID().compareTo(t2.getID());
            }
        });
    }


    private AtomicTask getTask(String taskID) {
        for (AtomicTask task : TASK_LIST) {
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
