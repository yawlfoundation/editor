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

/*
 * 
 * @author Moe Thandar Wynn
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

package org.yawlfoundation.yawl.analyser.reductionrules;

import org.yawlfoundation.yawl.elements.YCondition;
import org.yawlfoundation.yawl.elements.YExternalNetElement;
import org.yawlfoundation.yawl.elements.YNet;
import org.yawlfoundation.yawl.elements.YTask;

import java.util.Set;

/**
 * Reduction rule for YAWL net: FAPY rule
 */
public class FAPYrule extends YAWLReductionRule {

    /**
     * Innermost method for a reduction rule.
     * Implementation of the abstract method from superclass.
     * @param net YNet to perform reduction
     * @param netElement an  for consideration.
     * returns a reduced YNet or null if a given net cannot be reduced.
     */
    public YNet reduceElement(YNet net, YExternalNetElement netElement) {
        boolean isReducible = false;
        if (netElement instanceof YCondition) {
            YCondition condition = (YCondition) netElement;
            Set<YExternalNetElement> postSet = condition.getPostsetElements();
            Set<YExternalNetElement> preSet  = condition.getPresetElements();

            //check if all pre and post tasks are xor-splits and xor-joins  
            if (preSet.size() > 1 && postSet.size() > 1 &&
                    checkTaskSplitJoinType(preSet, true) &&
                    checkTaskSplitJoinType(postSet, false)) {

                // potential candidate exits so now try and find 
                // one or more other conditions
                for (YExternalNetElement element : net.getNetElements().values()) {
                    if (element instanceof YCondition) {
                        Set<YExternalNetElement> postSet2 = element.getPostsetElements();
                        Set<YExternalNetElement> preSet2  = element.getPresetElements();

                        //To do: cancellation
                        if (postSet.equals(postSet2) && preSet.equals(preSet2) &&
                                !element.equals(condition) &&
                                element.getCancelledBySet().equals(
                                        condition.getCancelledBySet())) {

                            isReducible = true;
                            net.removeNetElement(element);
                            condition.addToYawlMappings(element);
                            condition.addToYawlMappings(element.getYawlMappings());
                            setLabel(condition);
                        }
                    }
                }
            }
        }
        return isReducible ? net : null;
    }


    private boolean checkTaskSplitJoinType(Set<YExternalNetElement> elements,
                                           boolean checkSplit) {
        for (YExternalNetElement next : elements) {
            if (next instanceof YTask) {
                YTask task = (YTask) next;
                if (checkSplit) {
                    if (task.getSplitType() != YTask._XOR) return false;
                }
                else {
                    if (task.getJoinType() != YTask._XOR) return false;
                }
            }
        }
        return true;
    }
}