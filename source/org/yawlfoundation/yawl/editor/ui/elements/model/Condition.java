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

import org.yawlfoundation.yawl.elements.YCondition;

import java.awt.geom.Point2D;

public class Condition extends YAWLVertex {


    public Condition(Point2D startPoint) {
        super(startPoint);
    }

    public Condition(Point2D startPoint, YCondition shadow) {
        super(startPoint);
        setYCondition(shadow);
    }


    public void setYCondition(YCondition condition) {
        _yawlElement = condition;
    }

    public YCondition getYCondition() { return (YCondition) _yawlElement; }


    public String getType() {
        return "Condition";
    }

    public String getID() { return getYCondition().getID(); }

    public void setID(String id) { getYCondition().setID(id); }


    public String getName() { return getYCondition().getName(); }

    public void setName(String name) { getYCondition().setName(name); }


    public String getDocumentation() { return getYCondition().getDocumentation(); }

    public void setDocumentation(String doco) { getYCondition().setDocumentation(doco); }

}
