/*
 * <copyright>
 * Copyright 1997-2001 Defense Advanced Research Projects
 * Agency (DARPA) and ALPINE (a BBN Technologies (BBN) and
 * Raytheon Systems Company (RSC) Consortium).
 * This software to be used only in accordance with the
 * COUGAAR licence agreement.
 * </copyright>
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
//import com.bbn.openmap.layer.location.*;

import org.cougaar.lib.uiframework.transducer.XmlInterpreter;
import org.cougaar.lib.uiframework.transducer.elements.Structure;
import org.cougaar.lib.uiframework.ui.map.util.NamedLocationM;
// import org.cougaar.lib.uiframework.ui.map.query.TimedLocationQuery;

class XmlLayerModel {
    // class State {
    //class XmlLayerModelInner {
    OMGraphicList markers = new OMGraphicList();
	//Vector markers = new Vector();
	   // Vector latoff = new Vector();
	   // Vector lonoff = new Vector();
	Hashtable units=new Hashtable();

    void resetMarkers() {
      markers=new OMGraphicList();   // do not clear()
      units=new Hashtable();         // do not clear()
   }


  OMGraphic findClosest(int x, int y, float limit) {
    return markers.findClosest(x, y, limit);
  }

    UnitTypeDictionary unitTypeDictionary=new UnitTypeDictionary();

	private void makeInfantryUnit(float lat, float lon, Color color,
				      String label, String msg, Hashtable data) 
	{
	    OMGraphic omgraphic;

	    omgraphic = new InfantryVecIcon(lat, lon, color); 
	    ((InfantryVecIcon) omgraphic).addToMessage(msg);
	    ((InfantryVecIcon) omgraphic).setLabel(label);
	    markers.add(omgraphic);
	    // omList.add(omgraphic);	

	    Unit unit = new Unit(label, omgraphic, data);
	    Unit u2 = (Unit) units.put(omgraphic, unit);
	    if (u2 != null) {
		System.err.println("units.put returned non-null value-- inserted unit: "
				   +unit+" key: "+omgraphic+" returned unit: "+u2);
	    }
	    
	}

	private void makeArmoredUnit(float lat, float lon, Color color, 
				      String label, String msg, Hashtable data) 
	{
	    OMGraphic omgraphic;

	    omgraphic = new ArmoredVecIcon(lat, lon, color); 
	    ((ArmoredVecIcon) omgraphic).addToMessage(msg);
	    ((ArmoredVecIcon) omgraphic).setLabel(label);
	    markers.add(omgraphic); 
	    // omList.add(omgraphic);	

	    Unit unit = new Unit(label, omgraphic, data);
	    Unit u2 = (Unit) units.put(omgraphic, unit);
	    if (u2 != null) {
		System.err.println("units.put returned non-null value-- inserted unit: "
				   +unit+" key: "+omgraphic+" returned unit: "+u2);
	    }
	    
	}

	private OMGraphic makeIconGraphic(float lat, float lon, Color color, 
					  String type) 
	{
	    OMGraphic ret=null;
	    if (type.equalsIgnoreCase("Armored")) {
		ret=new ArmoredVecIcon(lat, lon, color);
	    }
	    if (type.equalsIgnoreCase("Infantry")) {
		ret=new InfantryVecIcon(lat, lon, color);
	    }
	    if (ret==null) {
		ret=new VecIcon(lat, lon, color);
	    }
	    return ret;
	}

	private void makeUnit(float lat, float lon, Color color,
			      String label, String msg, Hashtable data) 
	{
	    OMGraphic omgraphic;

	    // omgraphic = new VecIcon(lat, lon, color); 
	    String type="infantry";
	    type=getUnitType(label);
	    System.out.println("-- Xlym.getUnitIconType("+label+") returns: ["+type+"]");
	    omgraphic = makeIconGraphic(lat, lon, color, type);
	    ((VecIcon) omgraphic).addToMessage(msg);
	    ((VecIcon) omgraphic).setLabel(label);	    
	    markers.add(omgraphic); 
	    // omList.add(omgraphic);	

	    Unit unit = new Unit(label, omgraphic, data);
	    Unit u2 = (Unit) units.put(omgraphic, unit);
	    if (u2 != null) {
		System.err.println("units.put returned non-null value-- inserted unit: "
				   +unit+" key: "+omgraphic+" returned unit: "+u2);
	    }
	    
	}
	
    
    protected String getUnitType(String unitName) {
	return unitTypeDictionary.getUnitType(unitName);	
    }

    /*
    class UnitTypeDictionary {
	public UnitTypeDictionary() {
	    initialize();
	}
	public String getUnitType(String unitName) { 
	    String type = dictionary.getProperty(unitName);
	    if (type==null) { 
		type="unknown"; 
	    }
	    return type;
	}
    Properties dictionary=new Properties();

    private void initialize() {
	dictionary.setProperty("23INBN","infantry");
	dictionary.setProperty("30INBN","infantry");
	dictionary.setProperty("31INBN","infantry");
	dictionary.setProperty("3-7-INBN","infantry");
	dictionary.setProperty("4-7-INBN","infantry");
	dictionary.setProperty("5-7-INBN","infantry");
	dictionary.setProperty("6-7-INBN","infantry");
	dictionary.setProperty("7-7-INBN","infantry");
	dictionary.setProperty("3-69-ARBN","armored");
	dictionary.setProperty("3ID","other");
	dictionary.setProperty("1BDE","other");
    }

    }
    */
	Unit getUnit(OMGraphic g) {
	    return (Unit) units.get(g);
	}

	Unit setUnit(Unit unit) { return (Unit) units.put(unit.getGraphic(), unit); }

// 	private Hashtable createData(float a, float b, float c)
// 	{
// 	    Hashtable hs=new Hashtable();
// 	    hs.put("metric1", new Float(a));
// 	    hs.put("metric2", new Float(b));
// 	    hs.put("metric3", new Float(c));
// 	    hs.put("metric4", new Float(a-c));
// 	    hs.put("metric5", new Float(b-a));
// 	    hs.put("metric6", new Float(c-b));
// 	    return hs;
// 	}


     // NamedLocationTMModel nltmmodel;
    // void setTime(long time) {
    //   curTime=time;
    //   markers=new Vector();
		//   for (Iterator itr=nltmmodel.iteratorAt(curTime); itr.hasNext();) {
		//    NamedLocationTM nl=(NamedLocationTM)itr.next();
		//    makeUnit(nl.getLatitude(),nl.getLongitude(), Color.white, // red,
		//	     nl.getName(),
		//	     nl.getName(),      //			     " MSG ",
		//	     nl.getMetrics()    // createData(50, 100, 250)
		//	     );
		//   }
    // }

    String uriString=null;
    String fString=null;
    boolean smartUpdate=false;

    XmlLayerModel() {
	// OMGraphicList omlist=omList;
	OMGraphic omgraphic;
	// Vector list = markers;
	HashSet hs;
	String unitTypeFile = Environment.get("xml.unitTypeFile");
	uriString = Environment.get("xml.locations.url");
	fString= Environment.get("xml.locations");
	
	String smartUpdateString=null;
	smartUpdateString= Environment.get("xml.smart.update");
	if (smartUpdateString!=null && smartUpdateString.equalsIgnoreCase("true") ) {
	    smartUpdate=true;
	}
	System.out.println("XMLlayer.XmlLayerModel *** uriString from environment is "+uriString);
	if (uriString==null) {
	    uriString="file:/c:/dev/ui/gmc/tdcr/minilltcsm.xml";
	    uriString="file:/c:/dev/opmp/reltimedloc_h.xml";
	    System.out.println("Using default uriString: "+uriString);
	}
	if (fString==null) {
	    fString="c:\\dev\\opmp\\mplustcm.xml";
	    System.out.println("No fs_parm use: "+fString);
	}
	if (unitTypeFile==null) {
	    System.err.println("Warning:  Unit type file is not defined.");
	} else {
	    try {
		FileInputStream fin = new FileInputStream(unitTypeFile);
		unitTypeDictionary.load(fin);
	    } catch (Exception ex) {
		System.err.println("Warning:  Exception thrown while reading unit type data from ["+unitTypeFile+"].");	      
	    }
	}
	
	obtainData();
	
	System.out.println("leaving State()");
	
    }
    
    public void obtainData() {
	System.err.println("XMLLayerModel obtainData()");
 	try {
 	    XmlInterpreter xint = new XmlInterpreter();
 	    Structure str;
	    
	    try {
		System.err.println("Attempting to read from URL: "+uriString);
		URL url = new URL(uriString);
		
// 		//         InputStream fin = url.openStream();
// 		// 		    str = xint.readXml(fin);
// 		// 		    fin.close();		
// 		// replaced prior 3 lines with these
// 		int day=0;
// 		str = TimedLocationQuery.performTimedQuery(day, url);
// 		// should replace the above line with the following:
// 		// str = TimedLocationQuery.performQuery(url);

		InputStream fin = url.openStream();
		str = xint.readXml(fin);
		fin.close();		

// 		// replaced prior 3 lines with these
// 		int day=0;
// 		str = TimedLocationQuery.performTimedQuery(day, url);
// 		// should replace the above line with the following:
// 		// str = TimedLocationQuery.performQuery(url);
		
		if (str==null) {
		    str = readFileInput(xint, fString);
		}
		System.err.println("After query");
	    } catch (Exception exc) {
		str = readFileInput(xint, fString);
	    }
	    System.err.println("Structure: ");
	    xint.writeXml(str, System.err);
	    
	    load(str);
	    
	    
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    protected Structure readFileInput(XmlInterpreter xint, String fString) 
    throws java.io.IOException {
	System.err.println("Attempting to read: "+fString);
	FileInputStream fin = new FileInputStream(fString);
	Structure str = xint.readXml(fin);
	fin.close();
	return str;
    }

   void load(Structure s) {
    System.out.println("XmlLayerModel.load()");

    Vector vec=NamedLocationM.generate(s);

    // nltmmodel=new NamedLocationTMModel(urlString);
    // make nltmmodel a member of State
    // setTime(NamedLocationTMModel.getEpoch());


		for (Iterator itr=vec.iterator(); itr.hasNext();) {
		    NamedLocationM nl=(NamedLocationM)itr.next();
		    makeUnit(nl.getLatitude(),nl.getLongitude(), Color.white, // red,
			     nl.getName(),
			     nl.getName(),
//			     " MSG ",
			     nl.getMetrics()
			     // createData(50, 100, 250)
			     );
		}
  }
	  Iterator markerIterator() {
      Vector vec=markers.getTargets();
      if (vec==null) {
        vec=new Vector();
      }
	    return vec.iterator();
    }
  }




