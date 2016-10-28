package org.yawlfoundation.yawl.views.menu;

import org.yawlfoundation.yawl.editor.ui.actions.net.YAWLSelectedNetAction;
import org.yawlfoundation.yawl.editor.ui.swing.menu.YAWLCheckBoxMenuItem;
import org.yawlfoundation.yawl.editor.ui.swing.menu.YAWLToggleToolBarButton;
import org.yawlfoundation.yawl.views.resource.ResourceViewHandler;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

class ResourceViewAction extends YAWLSelectedNetAction implements ViewsPluginAction {

    private final static ResourceViewAction INSTANCE = new ResourceViewAction();
    private boolean _selected;
    private ResourceViewHandler _viewHandler;
    private YAWLToggleToolBarButton _toolBarButton;
    private YAWLCheckBoxMenuItem _menuItem;

    {
        putValue(Action.SHORT_DESCRIPTION, "Resource View Overlay");
        putValue(Action.NAME, "Resource View");
        putValue(Action.LONG_DESCRIPTION, "Colours tasks by selected roles");
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);

        _viewHandler = new ResourceViewHandler();
    }

    private ResourceViewAction() {
        super();
    }

    public static ResourceViewAction getInstance() {
        return INSTANCE;
    }

    public void register(YAWLCheckBoxMenuItem item) {
        _menuItem = item;
    }

    public void register(YAWLToggleToolBarButton button) {
        _toolBarButton = button;
    }


    public void actionPerformed(ActionEvent event) {
        _selected = ((AbstractButton) event.getSource()).isSelected();
        if (event.getSource() instanceof YAWLToggleToolBarButton) {
            if (_menuItem != null) _menuItem.setSelected(_selected);
        }
        else {
            if (_toolBarButton != null) _toolBarButton.setSelected(_selected);
        }
        _viewHandler.enableView(_selected);
    }


    public ResourceViewHandler getViewHandler() {
        return _viewHandler;
    }


    public boolean isSelected() {
        return _selected;
    }

}
