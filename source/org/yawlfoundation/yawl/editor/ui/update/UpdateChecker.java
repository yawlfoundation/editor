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

package org.yawlfoundation.yawl.editor.ui.update;

import org.yawlfoundation.yawl.editor.ui.util.FileLocations;
import org.yawlfoundation.yawl.util.HttpUtil;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
* @author Michael Adams
* @date 23/05/2014
*/
public class UpdateChecker extends SwingWorker<Void, Void> {

    private VersionDiffer _differ;
    private String _error;

    public UpdateChecker() { super(); }


    public Void doInBackground() {
        try {
            UpdateConstants.init();
            _differ = new VersionDiffer(loadLatestCheckSums(), loadCurrentCheckSums());
        }
        catch (IOException ioe) {
            _error = "Error: " + ioe.getMessage();
        }
        return null;
    }


    public VersionDiffer getDiffer() { return _differ; }

    public String getError() { return _error; }


    public boolean isNewVersion() {
        return _differ != null && _differ.isNewVersion();
    }


    public boolean hasUpdate() {
        return _differ != null && _differ.hasUpdates();
    }


    public boolean hasError() {
        return _error != null || getCurrentBuildNumber() == null ||
                getLatestBuildNumber() == null;
    }


    public String getErrorMessage() {
        if (_error != null) return _error;
        if (getCurrentBuildNumber() == null) {
            return "Error: Unable to determine current version";
        }
        if (getLatestBuildNumber() == null) {
            return "Error: Unable to determine latest version";
        }
        return null;
    }


    private File loadCurrentCheckSums() throws IOException {
        File current = new File(FileLocations.getLibPath(), UpdateConstants.CHECK_FILE);
        if (! current.exists()) {
            throw new IOException("Unable to determine current build version");
        }
        return current;
    }

    private File loadLatestCheckSums() throws IOException {
        File latest = new File(getTmpDir(), UpdateConstants.CHECK_FILE);
        URL url = UpdateConstants.getCheckUrl();
        HttpUtil.download(url, latest);
        if (! latest.exists()) {
            throw new IOException("Unable to determine latest build version");
        }
        return latest;
    }


    private File getTmpDir() {
        return new File(System.getProperty("java.io.tmpdir"));
    }


    private String getCurrentBuildNumber() {
        return _differ != null ? _differ.getCurrentBuild() : null;
    }

    private String getLatestBuildNumber() {
        return _differ != null ? _differ.getLatestBuild() : null;
    }

}
