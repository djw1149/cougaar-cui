package org.cougaar.lib.uiframework.ui.map.layer;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.*;
import com.bbn.openmap.LatLonPoint;

import com.bbn.openmap.Layer;
import com.bbn.openmap.proj.Projection;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMText;
import com.bbn.openmap.omGraphics.OMRaster;
import com.bbn.openmap.omGraphics.*;
import com.bbn.openmap.event.ProjectionEvent;

import com.bbn.openmap.event.*;
import com.bbn.openmap.layer.location.*;

public class IntelVecIcon extends VecIcon
{

    float basepixyf=.2f;
    float basepixxf=.1f;
    float pixyf=basepixyf;
    float pixxf=basepixxf;
    float lw=3;
    float lw2=5;
    int scale=1;
    OMText label;
       OMPoly bbox;

    OMGraphicList ogl=new OMGraphicList();
    String msg="Generic CEWI (Intelligence) Default";
//     public String getMessage() { return msg; }
//     public void setMessage(String str) { msg=str; }
//     public void addToMessage(String str) { msg+=": "+str; }

    public void setLabel(String lab) { label.setData(lab); }

    public String getLabel() { return label.getData(); }

    public IntelVecIcon(float lat, float lon, Color bc)
    {
      this(lat, lon, bc, Color.black, 1);
    }

    public IntelVecIcon(float lat, float lon, Color bc, Color c)
    {
      this(lat, lon, bc, c, 1);
    }

    private void setScale(int scale) {
    this.scale=scale; pixyf=scale*basepixyf; pixxf=scale*basepixxf;
    }

    int getScale() { return scale; }

    void setColor(Color bc) { 	bbox.setFillColor(bc); }

    // Constructor
    IntelVecIcon(float lat, float lon, Color bc, Color c, int sc)
    {

      setScale(sc);

      bbox = new OMPoly(
                  new float [] {
                                      lat, lon,
                                      lat, lon+pixyf,
                                      lat+pixxf, lon+pixyf,
                                      lat+pixxf, lon,
                                      lat, lon
                                  },
                                  OMGraphic.DECIMAL_DEGREES,
                                  OMGraphic.LINETYPE_STRAIGHT
                                  );
        bbox.setFillColor(bc);
        bbox.setLineColor(c);
//        bbox.setLineWidth(lw);

        OMFixedText cewiText = new OMFixedText ( lat+(pixxf/3),
                                                 lon+(pixyf/5),
                                                 (float) 0.1,
                                                 0,
                                                 0,
                                                 "CEWI",
                                                 OMText.JUSTIFY_LEFT);

        ogl.add(cewiText);
        ogl.add(bbox);

    }
    // OMGraphic requirements
    public float distance(int x, int y) { return ogl.distance(x,y); }
    public void render(Graphics g) { ogl.render(g); }
    public boolean generate(Projection  x) { return ogl.generate(x); }
} // end-class  public IntelVecIcon()
