/* **********************************************************************
 *
 *  BBNT Solutions LLC, A part of GTE
 *  10 Moulton St.
 *  Cambridge, MA 02138
 *  (617) 873-2000
 *
 *  Copyright (C) 1998, 2000
 *  This software is subject to copyright protection under the laws of
 *  the United States and other countries.
 *
 * **********************************************************************
 *
 * $Source: /opt/rep/cougaar/cui/uiframework/src/org/cougaar/lib/uiframework/ui/map/layer/Attic/XmlLayerBase.java,v $
 * $Revision: 1.6 $
 * $Date: 2001-03-07 22:01:29 $
 * $Author: pfischer $
 *
 * **********************************************************************
 */

package org.cougaar.lib.uiframework.ui.map.layer;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.*;
import java.net.*;
import com.bbn.openmap.LatLonPoint;

import com.bbn.openmap.Layer;
import com.bbn.openmap.proj.Projection;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMPoly;
import com.bbn.openmap.omGraphics.OMRaster;
import com.bbn.openmap.omGraphics.*;
import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.Environment;

import com.bbn.openmap.event.*;
import org.cougaar.lib.uiframework.ui.components.*;
import org.cougaar.lib.uiframework.ui.util.*;
import mil.darpa.log.alpine.blackjack.assessui.client.UIConstants;
import mil.darpa.log.alpine.blackjack.assessui.client.LinePlotPanel;
import mil.darpa.log.alpine.blackjack.assessui.client.StoplightPanel;

/**
 * Layer objects are components which can be added to the MapBean to
 * make a map.
 * <p>
 * Layers implement the ProjectionListener interface to listen for
 * ProjectionEvents.  When the projection changes, they may need to
 * refetch, regenerate their graphics, and then repaint themselves
 * into the new view.
 */

public class XmlLayerBase extends Layer implements MapMouseListener {

    protected OMGraphicList graphics;
    // OMGraphicList omList = new OMGraphicList();

	ColorCodeUnit ccuA=new ColorCodeUnit();
	ColorCodeUnit ccuB=new ColorCodeUnit();
	ColorCodeUnit curCcu=ccuB;

    /**
     * Construct the layer.
     */
    public XmlLayerBase () {
	super();

	ccuA.add(Color.green, 60);
	// ccuA.add(new ColorCodeMaxValue(Color.gray, 110));
	ccuA.add(Color.yellow, 160);
	ccuA.add(Color.red, 260);

	ccuB.add(Color.green, 360);
	ccuB.add(Color.gray, 160);
	ccuB.add(Color.blue, 110);
	ccuB.add(Color.red, 60);


	graphics = new OMGraphicList(40);
	//createGraphics(graphics);
	// worker.start();
    }

    /**
     * Sets the properties for the <code>Layer</code>.  This allows
     * <code>Layer</code>s to get a richer set of parameters than the
     * <code>setArgs</code> method.
     * @param prefix the token to prefix the property names
     * @param props the <code>Properties</code> object
     */
    public void setProperties(String prefix, java.util.Properties props) {
	super.setProperties(prefix, props);
    }

    Projection projection=null;
    /**
     * Invoked when the projection has changed or this Layer has been
     * added to the MapBean.
     * @param e ProjectionEvent
     */
    public void projectionChanged (ProjectionEvent e) {
	projection = e.getProjection();
	repaintLayer();
    }

    public void repaintLayer() {
	if (projection != null) {
	    graphics.generate(projection);
	} else {
	    System.err.println("null projection after prch -- skipping graphics.generate ...");
	}
    }

    /**
     * Paints the layer.
     * @param g the Graphics context for painting
     */
    public void paint (Graphics g) {
	graphics.render(g);
    }

    Iterator markerIterator() {
      return myState.markerIterator();
    }
    /**
     * Create graphics.
     *//*
    protected void createGraphics (OMGraphicList list) {
	// NOTE: all this is very non-optimized...
	    System.out.println("cG list.size(): "+list.size());

	    // OMGraphic marker=null;
	OMCircle marker=null;
	OMGraphic obj=null;
	list.clear();
	for (Iterator it=markerIterator(); it.hasNext(); ) {
	    obj = (OMGraphic)it.next();
	    list.add(obj);
// 	    if (obj instanceof OMCircle) {
// 		marker = (OMCircle) obj;
// 		System.out.println("marker: "+marker+" "+marker.getLatLon());
// 	    } else {
// 		System.out.println("non circle object. ");
// 	    }
	}
    }
    */
    // State myState=new State();
    //XmlLayerModelInner  myState=new XmlLayerModelInner();
    XmlLayerModel  myState;
    /*
    void initMyState() {
    myState=new XmlLayerModel();
    }
    */
   //      public void setTime(long time) { myState.setTime(time); }

   //void resetMarkers() {
   // myState.resetMarkers();
   //}


    /**
     * Note: A layer interested in receiving mouse events should
     * implement this function.  Otherwise, return null (the default).
     */
    public synchronized MapMouseListener getMapMouseListener(){
        return this;
    }

    /**
     * Return a list of the modes that are interesting to the
     * MapMouseListener.  You MUST override this with the modes you're
     * interested in.
     */
    public String[] getMouseModeServiceList(){
	String[] services = {"Gestures" };
	return services;
    }

    CFrame cframe;

    protected void launchUI(final String uiName, final String org) {
        Cursor wait = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
        final Component root = XmlLayerBase.this.getTopLevelAncestor();
        XmlLayerBase.this.getParent().setCursor(wait);
        root.setCursor(wait);
        (new Thread() {
            public void run()
            {
	        try {
	            cframe = new CFrame();
                    VariableInterfaceManager vim = null;
                    if (uiName.equals(UIConstants.STOPLIGHT_UI_NAME))
                    {
	                StoplightPanel slp = new StoplightPanel(false);
                        cframe.getContentPane().add(slp);
                        vim = slp.getVariableInterfaceManager();
                    }
                    else
                    {
                        LinePlotPanel lpp = new LinePlotPanel(false);
                        cframe.getContentPane().add(lpp);
                        vim = lpp.getVariableInterfaceManager();
                    }
	            vim.getDescriptor("Org").setValue(org);
	            cframe.setVisible(true);
	        } catch (Exception ex) {
	            fireRequestMessage(
                        "Warning:  Attempt to display chart failed for " +
                        "organization: ["
                        + org + "].\n  Check connection to database.  Check" +
                        " that database has data for the organization.");
	            ex.printStackTrace();
	        } finally {
                    Cursor defaultc = Cursor.getDefaultCursor();
                    root.setCursor(defaultc);
                    XmlLayerBase.this.getParent().setCursor(defaultc);
                }
            }
        }).start();
    }

    OMGraphic findClosest(int x, int y, float limit) {
        return myState.findClosest(x,y,limit);
    }

    /**
     * Invoked when the mouse has been clicked on a component.
     * @param e MouseEvent
     * @return false
     */
    public boolean mouseClicked(MouseEvent e){
	OMGraphic omgr = findClosest(e.getX(),e.getY(),4);
	if(omgr != null){
	    if(e.getClickCount() >= 2){
	    	launchUI(UIConstants.STOPLIGHT_UI_NAME, getOrgName(omgr));
	    }
	} else {
	    return false;
	}
	return true;
    }


    Unit getUnit(OMGraphic omgr) {
        return myState.getUnit(omgr);
    }

    public void colorCodeUnits(String metric) {
	// for each unit
	//for (Iterator it=myState.markerIterator();it.hasNext();)
	for (Iterator it=markerIterator();it.hasNext();)

	{
	    OMGraphic omgr=(OMGraphic)it.next();
	    //Unit unit=myState.getUnit(omgr);
	    Unit unit=getUnit(omgr);
	    if (unit!=null) {
		ccuA.setColor(unit,metric);
	    }

	    Float fl2=(unit!=null)?unit.getData(metric):null;
	    String addon= (fl2==null) ?  "null" : fl2.toString();
	    String msg="Value for "+metric+": "+addon;
	    omgr=unit.getGraphic();
	    if (omgr instanceof VecIcon) {
		((VecIcon)omgr).setMessageAddon(msg);
	    }
	    System.err.println("colorCodeUnits: "+msg);

	}
	repaintLayer();
    }

    private String getOrgName(OMGraphic omgr) {
	String name="";
	if(omgr != null) {
	    if (omgr instanceof VecIcon) {
		name=((VecIcon)omgr).getLabel();
	    } else if (omgr instanceof InfantryVecIcon) {
		name=((InfantryVecIcon)omgr).getLabel();
	    }
	}
	return name;
    }

    public boolean mouseMoved(MouseEvent e){

	    // OMGraphic omgr = (OMGraphic)omList.findClosest(e.getX(),e.getY(),4.0f);
      //OMGraphic omgr = (OMGraphic)myState.findClosest(e.getX(),e.getY(),4);
     OMGraphic omgr = findClosest(e.getX(),e.getY(),4);

	    if(omgr != null) {

		// this is for the case of a non-VecIcon
		String msg;

		if (omgr instanceof VecIcon) {
		    msg=((VecIcon)omgr).getFullMessage();
		} else {
		    msg=getOrgName(omgr)
			+"  Double-Clicking on icon brings up a chart.  ";
		    // 		if (omgr instanceof ArmoredVecIcon) {
		    // 		    msg=((ArmoredVecIcon)omgr).getLabel()+"  "+msg;
		    // 		    // if (curCcu==ccuA) { curCcu=ccuB ; }
		    // 		    // else { curCcu=ccuA; }
		    // 		} else if (omgr instanceof InfantryVecIcon) {
		    // 		    msg=((InfantryVecIcon)omgr).getLabel()+"  "+msg;
		    // 		}
		    String metric="metric1";
		    //Unit unit=myState.getUnit(omgr);
		    Unit unit=getUnit(omgr);
		    Float fl2=(unit!=null)?unit.getData(metric):null;
		    String addon= (fl2==null) ?  "null" : fl2.toString();
		    msg+="Value for "+metric+": "+addon;
		    // curCcu.setColor(unit,metric);

		}

		fireRequestInfoLine(msg);
	    } else {
		fireRequestInfoLine("");
		// 	    if(lastSelected != null){
		// 		lastSelected.deselect();
		// 		lastSelected.generate(oldProjection);
		// 		lastSelected = null;
// 		repaint();
		// 		//System.out.println("MouseMove Kicking repaint");
		// 	    }
	    }

	    // 	if(omgr instanceof OMBitmap){
	    // 	    omgr.select();
	    // 	    omgr.generate(oldProjection);
	    // 	    lastSelected = omgr;
	    // 	    //System.out.println("MouseMove Kicking repaint");
	    // 	    repaint();
// 	}

	return true;
	}

    //// just here because mouse i/f requires

    /**
     * Invoked when a mouse button has been pressed on a component.
     * @param e MouseEvent
     * @return false
     */
    public boolean mousePressed(MouseEvent e){
        return false;
    }

    private class OrgPopupListener implements ActionListener {
        private String org;
        public OrgPopupListener(String org) {
            this.org = org;
        }
        public void actionPerformed(final ActionEvent e) {
            launchUI(((JMenuItem)e.getSource()).getText(), org);
        }
    }

    /**
     * Invoked when a mouse button has been released on a component.
     * @param e MouseEvent
     * @return false
     */
    public boolean mouseReleased(MouseEvent e){
	final OMGraphic omgr = findClosest(e.getX(),e.getY(),4);
	if(omgr != null){
            if (e.isPopupTrigger())
            {
                ActionListener orgListener =
                    new OrgPopupListener(getOrgName(omgr));
                final JPopupMenu popup = new JPopupMenu();
                JMenuItem stoplight =
                    new JMenuItem(UIConstants.STOPLIGHT_UI_NAME);
                stoplight.addActionListener(orgListener);
                popup.add(stoplight);
                JMenuItem lineplot =
                    new JMenuItem(UIConstants.LINEPLOT_UI_NAME);
                lineplot.addActionListener(orgListener);
                popup.add(lineplot);
                popup.show(XmlLayerBase.this, e.getX(), e.getY());
            }
	} else {
	    return false;
	}
	return true;
    }
    /**
     * Invoked when the mouse enters a component.
     * @param e MouseEvent
     */
    public void mouseEntered(MouseEvent e){
	return;
    }

    /**
     * Invoked when the mouse exits a component.
     * @param e MouseEvent
     */
    public void mouseExited(MouseEvent e){
	return;
    }
    /**
     * Invoked when a mouse button is pressed on a component and then
     * dragged.  The listener will receive these events if it
     * @param e MouseEvent
     * @return false
     */
    public boolean mouseDragged(MouseEvent e){
	return false;
    }
    /**
     * Handle a mouse cursor moving without the button being pressed.
     * Another layer has consumed the event.
     */
    public void mouseMoved(){
	//System.out.println("mouseMoved2 event consumed by other layer  Called");
    }

}


