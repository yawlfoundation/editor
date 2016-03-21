/*
 * Copyright (c) 2004-2013 The YAWL Foundation. All rights reserved.
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

package org.yawlfoundation.yawl.editor.ui.elements.model;

import org.yawlfoundation.yawl.elements.YDecomposition;
import org.yawlfoundation.yawl.elements.YMultiInstanceAttributes;
import org.yawlfoundation.yawl.elements.YTask;

import java.awt.geom.Point2D;

/**
 * Properties for this class are managed through its YMultiInstanceAttributes
 * instance. The accessors and mutators are provided for third party plugins
 * (such as the configuration plugin).
 */
public class MultipleAtomicTask extends YAWLTask
        implements YAWLMultipleInstanceTask, YAWLAtomicTask {

    private YMultiInstanceAttributes _miAttributes;

    private String _minimumInstances;
    private String _maximumInstances;
    private String _continuationThreshold;
    private String _instanceCreationType;


    /**
     * This constructor is to be invoked whenever we are creating a new
     * multiple atomic task from scratch. It also creates the correct ports needed for
     * the task  as an intended side-effect.
     */
    public MultipleAtomicTask(Point2D startPoint, YTask yTask) {
        super(startPoint, null);
        setYAWLElement(yTask);
        initialise();
    }


    public void setYAWLElement(YTask shadow) {
        super.setTask(shadow);
        _miAttributes = shadow.getMultiInstanceAttributes();

        // needs this to allow analysis of nets containing MI tasks with no data settings
        if (_miAttributes.getMIFormalInputParam() == null) {
            _miAttributes.setMIFormalInputParam("null");
        }
    }


    private void initialise() {
        _minimumInstances = "1";
        _maximumInstances = "2";
        _continuationThreshold = "1";
        _instanceCreationType = YMultiInstanceAttributes.CREATION_MODE_STATIC;
    }


    public long getMinimumInstances() { return _miAttributes.getMinInstances(); }

    public void setMinimumInstances(long instanceBound) {
        _minimumInstances = String.valueOf(instanceBound);
        setProperties();
    }


    public long getMaximumInstances() {
        return _miAttributes.getMaxInstances();
    }

    public void setMaximumInstances(long instanceBound) {
        _maximumInstances = String.valueOf(instanceBound);
        setProperties();
    }


    public long getContinuationThreshold() {
        return _miAttributes.getThreshold();
    }

    public void setContinuationThreshold(long threshold) {
        _continuationThreshold = String.valueOf(threshold);
        setProperties();
    }


    public String getInstanceCreationType() {
        return _instanceCreationType;
    }

    public void setInstanceCreationType(String creationType) {
        _instanceCreationType = creationType;
        setProperties();
    }


     public String getSplitterQuery() {
        return _miAttributes.getMISplittingQuery();
    }

    public void setSplitterQuery(String query) {
        _miAttributes.setUniqueInputMISplittingQuery(query);
    }


    public String getAggregateQuery() {
        return _miAttributes.getMIJoiningQuery();
    }

    public void setAggregateQuery(String query) {
        _miAttributes.setUniqueOutputMIJoiningQuery(query);
    }


    public void setDecomposition(YDecomposition decomposition) {
        super.setDecomposition(decomposition);
    }

    public YDecomposition getDecomposition() {
        return super.getDecomposition();
    }

    public String getType() {
        return "Multiple Atomic Task";
    }


    private void setProperties() {
        _miAttributes.setProperties(_minimumInstances, _maximumInstances,
                _continuationThreshold, _instanceCreationType);
    }

}
