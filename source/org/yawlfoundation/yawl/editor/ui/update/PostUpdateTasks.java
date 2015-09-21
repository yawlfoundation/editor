/*
 * Copyright (c) 2004-2015 The YAWL Foundation. All rights reserved.
 * The YAWL Foundation is a collaboration of individuals and
 * organisations who are committed to improving workflow technology.
 *
 * This file is part of YAWL. YAWL is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation.
 *
 * YAWL is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with YAWL. If not, see <http://www.gnu.org/licenses/>.
 */

package org.yawlfoundation.yawl.editor.ui.update;

import org.yawlfoundation.yawl.editor.core.util.FileUtil;
import org.yawlfoundation.yawl.editor.ui.util.BuildProperties;
import org.yawlfoundation.yawl.editor.ui.util.FileLocations;
import org.yawlfoundation.yawl.util.StartMenuUpdater;
import org.yawlfoundation.yawl.util.StringUtil;
import org.yawlfoundation.yawl.util.XNode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Adams
 * @date 29/03/15
 */
public class PostUpdateTasks {

    // change this method to suit
    public boolean go() {
        updateOSMenus();
        fixSetEnvBat();
        return cleanup();
    }


    private boolean cleanup() {
        BuildProperties props = new BuildProperties();
        XNode node = props.getNode("cleanup");
        if (node != null) {
            List<XNode> failedToDelete = new ArrayList<XNode>();
            String homeDir = FileLocations.getHomeDir();
            for (XNode child : node.getChildren()) {
                if (! deleteFile(homeDir, child.getAttributeValue("name"))) {
                    failedToDelete.add(child);
                }
            }
            if (! failedToDelete.isEmpty()) {
                XNode newNode = new XNode("cleanup");
                newNode.addChildren(failedToDelete);
                if (props.replaceNode(newNode)) {
                    props.write();
                }
                return false;               // unable to delete all
            }
        }
        return true;    // none left to delete
    }


    private boolean deleteFile(String homeDir, String fileName) {
        File f = FileUtil.makeFile(homeDir, fileName);
        return ! f.exists() || f.delete();
    }


    private void updateOSMenus() {
        if (! FileUtil.isWindows()) return;

        File parentDir = getInstalledRootDir();
        String parentFolder = parentDir.getAbsolutePath();
        StartMenuUpdater smu = new StartMenuUpdater();
        smu.getSettings()
                .setFolderName("YAWL - " + new BuildProperties().getVersion())
                .setDescription("YAWL Process Editor")
                .setTargetPath(parentFolder + File.separator + "YAWL.bat")
                .setArguments("editor")
                .setWorkingDir(parentFolder)
                .setMenuName("Editor")
                .setIconLocation(parentFolder + File.separator + "icons" +
                        File.separator + "editor32.ico");
        smu.update();
        smu.checkBatFile(parentDir);
    }


    private void fixSetEnvBat() {
        if (! FileUtil.isWindows()) return;

        File parentDir = getInstalledRootDir();
        File setEnvBat = new File(parentDir, "engine/apache-tomcat-7.0.55/bin/setenv.bat");
        String entry = "set CATALINA_OPTS=-Xms1024m -Xmx1024m -XX:NewSize=256m " +
                "-XX:MaxNewSize=356m -XX:PermSize=256m -XX:MaxPermSize=356m\n";
        StringUtil.stringToFile(setEnvBat, entry);
    }


    private File getInstalledRootDir() {
        return new File(FileLocations.getHomeDir()).getParentFile();
    }

}
