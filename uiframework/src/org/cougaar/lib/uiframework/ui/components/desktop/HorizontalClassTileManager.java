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

import java.awt.*;
import java.util.*;

/***********************************************************************************************************************
<b>Description</b>: This class is an implementation of the tile manager interface that attempts to tile windows by
                    horizontal alignment based on class type of each frame's CougaarDesktopUI component.

@author Eric B. Martin, &copy;2001 Clark Software Engineering, Ltd. & Defense Advanced Research Projects Agency (DARPA)
@version 1.0
***********************************************************************************************************************/
public class HorizontalClassTileManager implements TileManager
{
  public void tile(CDesktopPane desktopPane)
  {
    CDesktopFrame[] frames = desktopPane.getAllDesktopFrames();
    Hashtable components = new Hashtable();
    
    // Sort all frames by component type
    for (int i=0; i<frames.length; i++)
    {
      Class classType = frames[i].getComponent().getClass();
      Vector list = (Vector)components.get(classType);
      if (list == null)
      {
        list = new Vector(1);
        components.put(classType, list);
      }
      
      list.add(frames[i]);
    }
    
    Dimension d = desktopPane.getSize();
    int currentX = 0;
    int currentY = 0;
    
    for (Enumeration e=components.elements(); e.hasMoreElements();)
    {
      Vector list = (Vector)e.nextElement();
      
      // Find the largest width and height
      int largestWidth = 0;
      int largestHeight = 0;
      for (int i=0, isize=list.size(); i<isize; i++)
      {
        CDesktopFrame frame = (CDesktopFrame)list.elementAt(i);
        largestWidth = (frame.getSize().width > largestWidth) ? frame.getSize().width : largestWidth;
        largestHeight = (frame.getSize().height > largestHeight) ? frame.getSize().height : largestHeight;
      }

      // Determie if the max width of one frame will fit in the desktop, and fix it if it cannot
      d.width = (d.width < largestWidth) ? largestWidth : d.width;

      // Move to the next row
      currentX = 0;
      currentY = currentY + largestHeight;

      // Set the frame sizes and move them on the desktop
      for (int i=0, isize=list.size(); i<isize; i++)
      {
        CDesktopFrame frame = (CDesktopFrame)list.elementAt(i);
        frame.setSize(largestWidth, largestHeight);
        if ((d.width - currentX) < largestWidth)
        {
          currentX = 0;
          currentY = currentY + largestHeight;
        }
        
        frame.setLocation(currentX, currentY - largestHeight);
        currentX += largestWidth;
      }
    }

    // Determie if the total height of the frames will fit in the desktop, and fix it if it cannot
    d.height = (d.height < currentY) ? currentY : d.height;

		desktopPane.setPreferredSize(d);
		desktopPane.revalidate();
  }
}
