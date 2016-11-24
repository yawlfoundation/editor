package org.yawlfoundation.yawl.views.menu;

import org.yawlfoundation.yawl.editor.ui.actions.net.YAWLSelectedNetAction;
import org.yawlfoundation.yawl.editor.ui.swing.menu.YAWLCheckBoxMenuItem;
import org.yawlfoundation.yawl.editor.ui.swing.menu.YAWLToggleToolBarButton;
import org.yawlfoundation.yawl.views.resource.ResourceViewHandler;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ResourceOverlayAction extends YAWLSelectedNetAction implements ViewsPluginAction {

    private final static ResourceOverlayAction INSTANCE = new ResourceOverlayAction();
    private boolean _selected;
    private ResourceViewHandler _viewHandler;
    private YAWLToggleToolBarButton _toolBarButton;
    private YAWLCheckBoxMenuItem _menuItem;

    {
        putValue(Action.SHORT_DESCRIPTION, "Resource View Overlay");
        putValue(Action.NAME, "Resource Color Overlay");
        putValue(Action.LONG_DESCRIPTION, "Color tasks by their roles");
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);

        _viewHandler = new ResourceViewHandler();
    }

    private ResourceOverlayAction() {
        super();
    }

    public static ResourceOverlayAction getInstance() {
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


    public void deselect() {
        if (_menuItem != null) _menuItem.setSelected(false);
        if (_toolBarButton != null) _toolBarButton.setSelected(false);
        _viewHandler.enableView(false);
    }


    public ResourceViewHandler getViewHandler() {
        return _viewHandler;
    }


    public boolean isSelected() {
        return _selected;
    }

}
