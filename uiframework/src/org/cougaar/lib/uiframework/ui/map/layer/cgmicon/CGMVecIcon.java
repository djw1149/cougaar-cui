/*
 * <copyright>
 *  Copyright 2001 BBNT Solutions, LLC
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
package org.cougaar.lib.uiframework.ui.map.layer.cgmicon;

import java.awt.Graphics;
import java.awt.Color;

import org.cougaar.lib.uiframework.ui.map.layer.VecIcon;

import com.bbn.openmap.proj.Projection;

import com.bbn.openmap.omGraphics.OMGraphic;

public class CGMVecIcon extends VecIcon
{

  OMCGM cgmIcon = null;

  public CGMVecIcon()
  {
  }

  public CGMVecIcon (OMCGM cgm, float lat, float lon, int sc)
  {
    super (lat, lon, Color.white, Color.black, sc);

    cgmIcon = cgm;
    cgmIcon.setLocation(lat, lon, OMGraphic.DECIMAL_DEGREES);

  }

  public CGMVecIcon (OMCGM cgm, float lat, float lon)
  {
     super (lat, lon, Color.white);
     cgmIcon = cgm;
     cgmIcon.setLocation(lat, lon, OMGraphic.DECIMAL_DEGREES);
  }


  public void setLocation(float latPoint, float lonPoint, int units)
  {
    cgmIcon.setLocation(latPoint, lonPoint, units);
  }

  public CGMVecIcon makeAnother()
  {

     try
     {
       CGMVecIcon newMe = new CGMVecIcon();

       if (cgmIcon instanceof OMCGMbyVisio)
       {
          newMe.cgmIcon = new OMCGMbyVisio();
       }
       else
       {
         newMe.cgmIcon = new OMCGM();
       }


       if (this.cgmIcon.unitSize != null)
         newMe.cgmIcon.unitSize = new String (this.cgmIcon.unitSize);
       newMe.cgmIcon.cgmFileName = new String (this.cgmIcon.cgmFileName);
       newMe.cgmIcon.omcgmdisp = (OpenMapCGMDisplay) (this.cgmIcon.omcgmdisp.makeAnother());

      newMe.setVisible(this.isVisible());

      newMe.locationNumber = locationNumber;
      newMe.assetBarGraphic = assetBarGraphic;

       return newMe;

     }

     catch (Throwable ioexc)
     {
       System.err.println ("can't create new copy of CGMVecIcon: " + ioexc.toString());
       ioexc.printStackTrace();
     }

     return null;

   }

   public void setEchelon (int echelon)
   {
     cgmIcon.setEchelon (echelon);
   }

   public boolean generate (Projection proj)
   {
     return super.generate(proj) & cgmIcon.generate(proj) ;
   }

  public void render (Graphics g)
  {
    super.render(g);
    cgmIcon.render(g);
  }

  public void updateScale ()
  {
    cgmIcon.updateScale();
  }
  
    protected void initSymbol() {}
    protected void initBoundingBox() {}
  
}
