package org.yawlfoundation.yawl.worklet.menu;

import org.yawlfoundation.yawl.editor.ui.actions.YAWLBaseAction;
import org.yawlfoundation.yawl.editor.ui.swing.menu.YAWLMenuItem;
import org.yawlfoundation.yawl.editor.ui.swing.menu.YAWLToolBarButton;

import javax.swing.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Adams
 * @date 2/12/2015
 */
public class MenuBuilder {

    public JToolBar getToolBar() {
        JToolBar toolBar = new JToolBar("Worklet Mgt", JToolBar.HORIZONTAL);
        toolBar.setRollover(true);
        addButtons(toolBar);
        return toolBar;
    }


    public JMenu getMenu() {
        JMenu menu = new JMenu("Worklet Mgt");
        addItems(menu);
        menu.setIcon(getWorkletMenuIcon("worklet"));
        menu.setMnemonic('W');
        return menu;
    }


    private void addButtons(JToolBar toolbar) {
        for (MenuAction menuAction : getMenuActions()) {
            if (menuAction != null) {
                toolbar.add(newButton(menuAction.action, menuAction.iconName));
            }
            else {
                toolbar.addSeparator();
            }
        }
    }


    private void addItems(JMenu menuBar) {
         for (MenuAction menuAction : getMenuActions()) {
             if (menuAction != null) {
                 menuBar.add(newItem(menuAction.action, menuAction.iconName));
             }
             else {
                 menuBar.addSeparator();
             }
         }
     }


    private List<MenuAction> getMenuActions() {
        List<MenuAction> actions = new ArrayList<MenuAction>();
        actions.add(new MenuAction(new LoadWorkletAction(), "load"));
        actions.add(new MenuAction(new SaveWorkletAction(), "save"));
        actions.add(null);
        actions.add(new MenuAction(new AddRuleAction(), "add"));
        actions.add(new MenuAction(new ReplaceWorkletAction(), "settings"));
        actions.add(new MenuAction(new AddRuleSetAction(), "addSet"));
        actions.add(new MenuAction(new ViewRuleSetAction(), "view"));
        actions.add(null);
        actions.add(new MenuAction(new RemoveOrphanWorkletsAction(), "removeOrphans"));
        actions.add(new MenuAction(new RemoveRuleSetAction(), "removeSet"));
        actions.add(null);
        actions.add(new MenuAction(new SettingsAction(), "settings"));
        return actions;
    }


    private YAWLMenuItem newItem(YAWLBaseAction action, String iconName) {
        setIcon(action, iconName);
        YAWLMenuItem item = new YAWLMenuItem(action);
        setToolTipText(item, action);
        return item;
    }


    private YAWLToolBarButton newButton(YAWLBaseAction action, String iconName) {
        setIcon(action, iconName);
        YAWLToolBarButton button = new YAWLToolBarButton(action);
        setToolTipText(button, action);
        return button;
    }


    private void setIcon(YAWLBaseAction action, String iconName) {
        action.putValue(Action.SMALL_ICON, getWorkletMenuIcon(iconName));
    }


    private void setToolTipText(JComponent c, YAWLBaseAction action) {
        c.setToolTipText((String) action.getValue(Action.LONG_DESCRIPTION));
    }


    private ImageIcon getWorkletMenuIcon(String name) {
        URL url = this.getClass().getResource("icon/" + name + ".png");
        return url != null ? new ImageIcon(url) : null;
    }


    /****************************************************************************/

    class MenuAction {

        YAWLBaseAction action;
        String iconName;

        MenuAction(YAWLBaseAction a, String s) { action = a; iconName = s; }

    }

}
