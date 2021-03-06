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

package org.yawlfoundation.yawl.editor.ui.elements.view;

import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.VertexView;

import java.awt.*;

public class OutputConditionView extends VertexView {

    private static final OutputConditionRenderer renderer = new OutputConditionRenderer();

    public OutputConditionView(Object vertex) {
        super(vertex);
    }

    public CellViewRenderer getRenderer() {
        return renderer;
    }
}

class OutputConditionRenderer extends ConditionView.ConditionRenderer {

    protected void drawVertex(Graphics graphics, Dimension size) {
        overrideFill(graphics, size);                  // white b/g for input condition
        graphics.setColor(Color.RED);
        graphics.fillRect(Math.round(size.width/4),
                Math.round(size.height/4),
                Math.round(size.width/2),
                Math.round(size.height/2));
        graphics.setColor(Color.black);
        graphics.drawRect(Math.round(size.width/4),
                Math.round(size.height/4),
                Math.round(size.width/2 - 1),
                Math.round(size.height/2 - 1));
        super.drawVertex(graphics, size);
    }
}
