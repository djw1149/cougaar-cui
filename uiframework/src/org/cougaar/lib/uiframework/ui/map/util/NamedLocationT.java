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

package org.cougaar.lib.uiframework.ui.map.util;

import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.BorderLayout;
import org.cougaar.lib.uiframework.transducer.*;
import java.awt.event.*;
import org.cougaar.lib.uiframework.transducer.elements.*;

public class NamedLocationT  {

    String title="";
    String name="";
    String latstr="";
    String lonstr="";
    float lat, lon;
    Exception exception=null;
    
    public String getTitle() { return title; }
//  public NamedLocation () {
//  }
  public NamedLocationT (String name, String lat, String lon) {
    this.name=name;
    this.latstr=lat;
    this.lonstr=lon;
    try {
        this.lat = Float.parseFloat(latstr); 
        this.lon = Float.parseFloat(lonstr); 
    } catch (Exception ex) {
       this.exception=ex;
    }
  }

    public float getLatitude() { return lat; }
    public float getLongitude() { return lon; }
    public String  getName() { return name; }
public String toString() {
    return "\n { NamedLocation "+name+" (lat, lon) ("+lat+", "+lon+") }";
}

public boolean isValid() { return exception==null;}
    static  void handleInvalid(NamedLocation ne) {
        System.err.println("Invalid NamedLocation: "+ne+" Strings of (lat, lon) ("+ne.latstr+", "+ne.lonstr+") }");
    }
    static public Vector generate(Structure str) {
	Vector vec=null;
    String title;
    NamedLocation nl;
    
	Attribute atr=str.getAttribute("NamedLocationList");
	title= getFirstValForAttribute(atr);

	// get first list in structure
	ListElement me = str.getContentList();
	if (me != null) {
	    vec = new Vector();

	    for (Enumeration en=me.getChildren(); en.hasMoreElements(); ) {
		    // for each child of le that is a list 
		    ListElement child=((Element)en.nextElement()).getAsList();
		    if (child != null) {
		        String name = getNode(child, "UID");
		        String lat = getNode(child, "latitude");
		        String lon = getNode(child, "longitude");
		        nl=new NamedLocation(name, lat, lon);
		        if (nl.isValid()) {
		            vec.add(nl);
		        } else {
		            handleInvalid(nl);
		        }
		    }
	    }
	}
	return vec;
    }

    static String getFirstValForAttribute(Attribute atr) {
	String name="";
	Enumeration en1=null;
	ValElement val=null;
	
	if (atr != null) {
	    en1=atr.getChildren();
	}
	if (en1!=null && en1.hasMoreElements()) {
	    val=((Element)en1.nextElement()).getAsValue();
	}
	if (val!=null) {
	    name = val.getValue();
	}
	return name;
    }

    static private String getNode(ListElement le, String attrName) {
	Attribute atr=le.getAttribute(attrName);
	return getFirstValForAttribute(atr);
    }


    public static void main(String[] argv) {
	JFrame frame;
	//NamedLocation nl = null;
	
	if (argv.length < 1)
	    System.out.println("Please specify NamedLocation file.");
	else {
	    try {
		XmlInterpreter xint = new XmlInterpreter();
		
		FileInputStream fin = new FileInputStream(argv[0]);
		Structure s = xint.readXml(fin);
		fin.close();
		
		Vector vec=NamedLocation.generate(s);
		if (vec.size()<1) {
		    System.err.println("No NamedLocations.  Make sure that your file parses correctly.");
		} else {
		    System.err.println("NamedLocations: "+vec);
		}
	    } catch (Exception ex) {
		System.err.println("Exception: "+ex.getMessage());
		ex.printStackTrace();
	    }
	}
    }
}
