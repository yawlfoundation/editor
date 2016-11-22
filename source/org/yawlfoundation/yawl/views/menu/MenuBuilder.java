package org.yawlfoundation.yawl.views.menu;

import org.yawlfoundation.yawl.editor.ui.actions.YAWLBaseAction;
import org.yawlfoundation.yawl.editor.ui.swing.menu.YAWLCheckBoxMenuItem;
import org.yawlfoundation.yawl.editor.ui.swing.menu.YAWLMenuItem;
import org.yawlfoundation.yawl.editor.ui.swing.menu.YAWLToggleToolBarButton;
import org.yawlfoundation.yawl.editor.ui.swing.menu.YAWLToolBarButton;
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

    private JToolBar _toolbar;
    private JMenu _menu;
    private ResourceViewHandler _viewHandler;


    public JToolBar getToolBar() {
        if (_toolbar == null) {
            _toolbar = new JToolBar("Process Views", JToolBar.HORIZONTAL);
            _toolbar.setRollover(true);
            addButtons(_toolbar);
        }
        return _toolbar;
    }


    public JMenu getMenu() {
        if (_menu == null) {
            _menu = new JMenu("Process Views");
            addItems(_menu);
            _menu.setIcon(getMenuIcon("view"));
            _menu.setMnemonic('V');
        }
        return _menu;
    }

    public ResourceViewHandler getViewHandler() {
        return _viewHandler;
    }


    private void addButtons(JToolBar toolbar) {
        for (MenuAction menuAction : getMenuActions()) {
            if (menuAction != null) {
                toolbar.add(newButton(menuAction));
            }
            else {
                toolbar.addSeparator();
            }
        }
    }


    private void addItems(JMenu menuBar) {
        for (MenuAction menuAction : getMenuActions()) {
            if (menuAction != null) {
                menuBar.add(newItem(menuAction));
            }
            else {
                menuBar.addSeparator();
            }
        }
    }


    // populates both the menubar and toolbar
    private List<MenuAction> getMenuActions() {
        List<MenuAction> actions = new ArrayList<MenuAction>();
        ResourceViewAction rvAction = ResourceViewAction.getInstance();
        actions.add(new MenuAction(rvAction, "resource", true));
        _viewHandler = rvAction.getViewHandler();

        GraphViewAction gvAction = new GraphViewAction();
        actions.add(new MenuAction(gvAction, "graphview", false));

        DataViewAction dvAction = new DataViewAction();
        actions.add(new MenuAction(dvAction, "dataview", false));

        QueryAction qAction = new QueryAction();
        actions.add(new MenuAction(qAction, "search", false));

        WriteTriplesAction wrAction = new WriteTriplesAction();
        actions.add(new MenuAction(wrAction, "triples", false));

        SaveOntologyAction svAction = new SaveOntologyAction();
        actions.add(new MenuAction(svAction, "save", false));

        return actions;
    }


    private JMenuItem newItem(MenuAction menuAction) {
        JMenuItem item;
        setIcon(menuAction.action, menuAction.iconName);
        if (menuAction.isToggle) {
            item = new YAWLCheckBoxMenuItem(menuAction.action);
            ((ViewsPluginAction) menuAction.action)
                    .register((YAWLCheckBoxMenuItem) item);
        }
        else {
            item = new YAWLMenuItem(menuAction.action);

        }
        setToolTipText(item, menuAction.action);
        return item;
    }


    private AbstractButton newButton(MenuAction menuAction) {
        AbstractButton button;
        setIcon(menuAction.action, menuAction.iconName);
        if (menuAction.isToggle) {
            button = new YAWLToggleToolBarButton(menuAction.action);
            ((ViewsPluginAction) menuAction.action)
                    .register((YAWLToggleToolBarButton) button);
        }
        else {
            button = new YAWLToolBarButton(menuAction.action);
        }
        setToolTipText(button, menuAction.action);
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
        boolean isToggle;

        MenuAction(YAWLBaseAction a, String s, boolean b) {
            action = a;
            iconName = s;
            isToggle = b;
        }

    }

}
