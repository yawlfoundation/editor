package org.yawlfoundation.yawl.views.menu;

import org.yawlfoundation.yawl.editor.ui.swing.menu.YAWLCheckBoxMenuItem;
import org.yawlfoundation.yawl.editor.ui.swing.menu.YAWLToggleToolBarButton;

/**
 * @author Michael Adams
 * @date 26/10/2016
 */
public interface ViewsPluginAction {

    void register(YAWLCheckBoxMenuItem item);

    void register(YAWLToggleToolBarButton button);

}
