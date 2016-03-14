/*
 * Copyright (c) 2004-2014 The YAWL Foundation. All rights reserved.
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

package org.yawlfoundation.yawl.editor.ui.util;

import org.yawlfoundation.yawl.util.AbstractCheckSumTask;
import org.yawlfoundation.yawl.util.CheckSummer;
import org.yawlfoundation.yawl.util.XNode;

import java.io.File;
import java.io.IOException;

/**
 * @author Michael Adams
 * @date 5/07/2014
 */
public class CheckSumTask extends AbstractCheckSumTask {

    public String toXML(File checkDir, CheckSummer summer) throws IOException {
        EditorFileLocations locations = new EditorFileLocations(_antLocations);
        XNode root = new XNode("release");
        root.addOpeningComment(COMMENT);
        root.addChild("version", getProjectProperty("version"));
        root.addChild("build", getBuildNumber());
        root.addChild("timestamp", now());
        root.addChild(locations.getPaths());

        XNode filesNode = root.addChild("files");
        for (File file : getFileList(checkDir)) {
            if (shouldBeIncluded(file)) {
                String relPath = getRelativePath(checkDir, file.getAbsolutePath());
                XNode fileNode = filesNode.addChild("file");
                fileNode.addAttribute("name", relPath);
                fileNode.addAttribute("md5", summer.getMD5Hex(file));
                fileNode.addAttribute("size", file.length());
                fileNode.addAttribute("timestamp", formatTimestamp(file.lastModified()));
                fileNode.addAttribute("path", locations.get(relPath));
            }
        }

        return root.toPrettyString(true);
    }


    private int getBuildNumber() {
        String prevBuild = getProjectProperty("build.number");
        try {
            return Integer.parseInt(prevBuild) + 1;
        }
        catch (NumberFormatException nfe) {
            return 0;
        }
    }

    /******************************************************************************/

    class EditorFileLocations extends FileLocations {

        EditorFileLocations(String fileName) {
            super(fileName);
        }


        public void loadLocations(XNode root) {
            XNode files = root.getChild("files");
            for (XNode fNode : files.getChildren()) {
                String name = fNode.getAttributeValue("name");
                String path = fNode.getAttributeValue("path");
                locations.put(name, path);
            }
        }

    }

}
