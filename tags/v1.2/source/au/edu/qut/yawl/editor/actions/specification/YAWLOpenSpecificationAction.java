/*
 * Created on 9/10/2003
 * YAWLEditor v1.0 
 *
 * @author Lindsay Bradford
 * 
 * Copyright (C) 2003 Queensland University of Technology
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

import au.edu.qut.yawl.editor.actions.YAWLBaseAction;
import au.edu.qut.yawl.editor.specification.SpecificationFileModel;
import au.edu.qut.yawl.editor.specification.SpecificationFileModelListener;

class YAWLOpenSpecificationAction extends YAWLBaseAction 
                                implements SpecificationFileModelListener {

  private final SpecificationFileModel fileModel = 
    SpecificationFileModel.getInstance();

  {
    getFileModel().subscribe(this);   
  }                                  

  public void specificationFileModelStateChanged(int state) {
    switch(state) {
      case SpecificationFileModel.IDLE: {
        setEnabled(false);     
        break;    
      }
      case SpecificationFileModel.EDITING: {
        setEnabled(true);
        break;    
      }
      case SpecificationFileModel.BUSY: {
        setEnabled(false);     
        break;
      }
      default: {
        assert false : "Invalid state passed to updateState()";   
      }    
    }
  }

  public SpecificationFileModel getFileModel() {
    return fileModel;  
  }
}
