/*
 * <copyright>
 *  Copyright 1997-2001 BBNT Solutions, LLC
 *  under sponsorship of the Defense Advanced Research Projects Agency (DARPA).
 * 
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published by
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).
 * 
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 *  PROVIDED 'AS IS' WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.
 * </copyright>
 */
/*
	File: AngleControlPanel.java
	9/3/96  Larry Barowski

  The following comment is to comply with GPLv2:
     This source file was modified during February 2001.
*/


   package org.cougaar.lib.uiframework.ui.ohv.VGJ.gui;


   import java.awt.Panel;
   import java.awt.Button;
   import java.awt.Event;
   import java.awt.Label;

   import EDU.auburn.VGJ.util.DPoint;

/**
 *   An AngleControl, along with a label and buttons for XY plane,
 *   YZ plane, XZ plane.
 *   </p>Here is the <a href="../gui/AngleControlPanel.java">source</a>.
 *
 *@author	Larry Barowski
**/

   class AngleControlPanel extends LPanel
      {
      public static int	ANGLE = 38793;
      public static int	DONE = 38794;
   
      private AngleControl angle_;
   
      public AngleControlPanel(int width, int height)
         {
	 super();

	 constraints.insets.top = constraints.insets.bottom = 0;
	 addLabel("Viewing Angles", 0, 0, 1.0, 0.0, 0, 0);
	 constraints.insets.top = constraints.insets.bottom = 0;
         angle_ = new AngleControl(width, height);
	 addComponent(angle_, 0, 0, 1.0, 1.0, 3, 0);
	 constraints.insets.top = constraints.insets.bottom = 0;
	 addLabel("Plane:", 1, -1, 0.0, 0.0, 0, 0);
         addButton("XY", 1, 0, 1.0, 0.0, 0, 0);
         addButton("XZ", 1, 0, 1.0, 0.0, 0, 0);
         addButton("YZ", 0, 0, 1.0, 0.0, 0, 0);

	 finish();
         }
   
   
      public boolean handleEvent(Event event)
      {
         if(event.target instanceof AngleControl) {
            if(event.id == AngleControl.ANGLE) {
               DPoint	angles = (DPoint)event.arg;
               postEvent(new Event((Object)this, ANGLE,
                  new DPoint(angles.x, angles.y)));
               }
	    else if(event.id == AngleControl.DONE) {
               DPoint	angles = (DPoint)event.arg;
               postEvent(new Event((Object)this, DONE,
                  new DPoint(angles.x, angles.y)));
	    }
         }
         return super.handleEvent(event);
      }
   
   
      public boolean action(Event event, Object what)
         {
         if(event.target instanceof Button)
            {
            if(((String)what).equals("XY"))
               {
               angle_.setAngles(0.0, Math.PI / 2.0);
               postEvent(new Event((Object)this, DONE,
                  new DPoint(0.0, Math.PI / 2.0)));
               }
            else if(((String)what).equals("XZ"))
               {
               angle_.setAngles(0.0, 0.0);
               postEvent(new Event((Object)this, DONE,
                  new DPoint(0.0, 0.0)));
               }
            else if(((String)what).equals("YZ"))
               {
               angle_.setAngles(Math.PI / 2.0, 0.0);
               postEvent(new Event((Object)this, DONE,
                  new DPoint(Math.PI / 2.0, 0.0)));
               }
            }
         return true;
         }
      }


