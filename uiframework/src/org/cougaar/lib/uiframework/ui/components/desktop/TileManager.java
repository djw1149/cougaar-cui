/* 
 * <copyright> 
 *  Copyright 1997-2001 Clark Software Engineering (CSE)
 *  under sponsorship of the Defense Advanced Research Projects 
 *  Agency (DARPA). 
 *  
 *  This program is free software; you can redistribute it and/or modify 
 *  it under the terms of the Cougaar Open Source License as published by 
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).  
 *  
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS  
 *  PROVIDED "AS IS" WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR  
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF  
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT  
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT  
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL  
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,  
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR  
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.  
 *  
 * </copyright> 
 */

package org.cougaar.lib.uiframework.ui.components.desktop;

/***********************************************************************************************************************
<b>Description</b>: This class provides a standard interface for implementing window tiling capabilities within the
                    Cougaar Desktop application.

@author Eric B. Martin, &copy;2001 Clark Software Engineering, Ltd. & Defense Advanced Research Projects Agency (DARPA)
@version 1.0
***********************************************************************************************************************/
public interface TileManager
{
	/*********************************************************************************************************************
  <b>Description</b>: Called when the implmenting manager should tile the desktop's windows.  The tile manager is given
                      the desktop pane object of the desktop it is to tile.

  <br>
  @param desktopPane Desktop pane with windows to tile
	*********************************************************************************************************************/
  public void tile(CDesktopPane desktopPane);
//  public JMenuItem[] getMenu(CDesktopPane desktopPane);
}