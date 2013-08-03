/*
 * Created on 07/10/2003
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

package org.yawlfoundation.yawl.editor.ui.swing.menu;

import org.yawlfoundation.yawl.editor.ui.swing.YSplashScreen;

import javax.swing.*;

public class YAWLMenuBar extends JMenuBar {
  
  private static final long serialVersionUID = 1L;

  public YAWLMenuBar(YSplashScreen splashScreen) {
    super();
    int progress = 0;

    add(new SpecificationMenu());
      splashScreen.updateProgress(progress+=10);
      add(new EditMenu());
      splashScreen.updateProgress(progress+=10);
    add(new NetMenu());
      splashScreen.updateProgress(progress+=10);
    add(new ElementsMenu());
      splashScreen.updateProgress(progress+=10);
    add(new SettingsMenu());
      splashScreen.updateProgress(progress+=10);
    add(new ViewMenu());
      splashScreen.updateProgress(progress+=10);
    add(new HelpMenu());
   }
}