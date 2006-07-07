/*
 * Created on 9/10/2003
 * YAWLEditor v1.0 
 *
 * @author Lindsay Bradford
 * 
 * 
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

package au.edu.qut.yawl.editor.actions.specification;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import au.edu.qut.yawl.editor.actions.specification.YAWLOpenSpecificationAction;
import au.edu.qut.yawl.editor.specification.ArchivingThread;
import au.edu.qut.yawl.editor.swing.TooltipTogglingWidget;

public class SaveSpecificationAsAction extends YAWLOpenSpecificationAction implements TooltipTogglingWidget {

  {
    putValue(Action.SHORT_DESCRIPTION, getDisabledTooltipText());
    putValue(Action.NAME, "Save Specification As...");
    putValue(Action.LONG_DESCRIPTION, "Save this specification to a different filename ");
    putValue(Action.SMALL_ICON, getIconByName("SaveAs"));
    putValue(Action.MNEMONIC_KEY, new Integer(java.awt.event.KeyEvent.VK_A));
  }
  
  public void actionPerformed(ActionEvent event) {
    ArchivingThread.getInstance().saveAs();
  }
  
  public String getEnabledTooltipText() {
    return " Save this specification to a different filename";
  }
  
  public String getDisabledTooltipText() {
    return " You must have an open specification" + 
           " to save it to a different filename ";
  }
}