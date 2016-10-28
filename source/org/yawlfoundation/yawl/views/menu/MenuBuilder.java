package org.yawlfoundation.yawl.views.menu;

import org.yawlfoundation.yawl.editor.ui.actions.YAWLBaseAction;
import org.yawlfoundation.yawl.editor.ui.swing.menu.YAWLCheckBoxMenuItem;
import org.yawlfoundation.yawl.editor.ui.swing.menu.YAWLToggleToolBarButton;
import org.yawlfoundation.yawl.views.resource.ResourceViewHandler;

import javax.swing.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Adams
 * @date 2/12/2015
 */
public class MenuBuilder {

    private ResourceViewHandler _viewHandler;


    public JToolBar getToolBar() {
        JToolBar toolBar = new JToolBar("Process Views", JToolBar.HORIZONTAL);
        toolBar.setRollover(true);
        addButtons(toolBar);
        return toolBar;
    }


    public JMenu getMenu() {
        JMenu menu = new JMenu("Process Views");
        addItems(menu);
        menu.setIcon(getMenuIcon("views"));
        menu.setMnemonic('V');
        return menu;
    }

    public ResourceViewHandler getViewHandler() {
        return _viewHandler;
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


    // populates both the menubar and toolbar
    private List<MenuAction> getMenuActions() {
        List<MenuAction> actions = new ArrayList<MenuAction>();
        ResourceViewAction action = ResourceViewAction.getInstance();
        actions.add(new MenuAction(action, "resource"));
        _viewHandler = action.getViewHandler();
        return actions;
    }


    private YAWLCheckBoxMenuItem newItem(YAWLBaseAction action, String iconName) {
        setIcon(action, iconName);
        YAWLCheckBoxMenuItem item = new YAWLCheckBoxMenuItem(action);
        ((ViewsPluginAction) action).register(item);
        setToolTipText(item, action);
        return item;
    }


    private YAWLToggleToolBarButton newButton(YAWLBaseAction action, String iconName) {
        setIcon(action, iconName);
        YAWLToggleToolBarButton button = new YAWLToggleToolBarButton(action);
        ((ViewsPluginAction) action).register(button);
        setToolTipText(button, action);
        return button;
    }


    private void setIcon(YAWLBaseAction action, String iconName) {
        action.putValue(Action.SMALL_ICON, getMenuIcon(iconName));
    }


    private void setToolTipText(JComponent c, YAWLBaseAction action) {
        c.setToolTipText((String) action.getValue(Action.LONG_DESCRIPTION));
    }


    private ImageIcon getMenuIcon(String name) {
        URL url = this.getClass().getResource("icon/" + name + ".png");
        return url != null ? new ImageIcon(url) : null;
    }


    /****************************************************************************/

    class MenuAction {

        YAWLBaseAction action;
        String iconName;

        MenuAction(YAWLBaseAction a, String s) {
            action = a;
            iconName = s;
        }

    }

}
