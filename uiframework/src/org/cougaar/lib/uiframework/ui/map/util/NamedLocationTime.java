
package org.cougaar.lib.uiframework.ui.map.util;

import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.BorderLayout;
import org.cougaar.lib.uiframework.transducer.*;
import org.cougaar.lib.uiframework.transducer.elements.*;
import java.awt.event.*;

import org.cougaar.lib.uiframework.ui.map.layer.Unit;
import org.cougaar.domain.planning.ldm.plan.ScheduleImpl;
import org.cougaar.domain.planning.ldm.plan.ScheduleElementImpl;

public class NamedLocationTime extends ScheduleElementImpl  {

    String title="";
    String name="";
    String latstr="";
    String lonstr="";
    MetricTable metrics;
    float lat, lon;
    long startTime, endTime;
    Exception exception=null;
    Unit unit=null;

    public Unit getUnit() { return unit; }
    public void setUnit(Unit lunit) { unit=lunit; }
    
    public String getTitle() { return title; }
    public NamedLocationTime (String name, String lat, String lon,
                          String startTime, String endTime,
                          MetricTable metrics) {
    long start, end;
    this.name=name;
    this.latstr=lat;
    this.lonstr=lon;
    this.metrics=metrics;
    if (metrics==null) {
	this.metrics=new MetricTable();
    }
    try {
        this.lat = Float.parseFloat(latstr);
        this.lon = Float.parseFloat(lonstr);

        if (startTime!=null&&!startTime.equals("")) {
          start=Long.parseLong(startTime);
        } else {
          start=Long.MIN_VALUE;
        }

        if (endTime!=null&&!endTime.equals("")) {
          end=Long.parseLong(endTime);
        } else {
          end=Long.MAX_VALUE;
        }
      setStartTime(start);
      setEndTime(end);

    } catch (Exception ex) {
       this.exception=ex;
    }
  }

    public Float getMetric(String metName) { return metrics.get(metName); }
    public Hashtable getMetrics() { return metrics.asHashtable(); }
    public float getLatitude() { return lat; }
    public float getLongitude() { return lon; }
    public long getStartTime() { return super.getStartTime(); }
    public long getEndTime() { return super.getEndTime(); }
    public String  getName() { return name; }
    public String toString() {
      return "\n { NamedLocationTime "+name+" (lat, lon) ("+lat+", "+lon+") "
              +" (stTime, eTime) ("+getStartTime()+", "+getEndTime()
              +") metrics: "+metrics+" }";
    }

public boolean isValid() { return exception==null;}
    static  void handleInvalid(NamedLocationTime ne) {
        System.err.println("Invalid NamedLocationTime: "+ne+" Strings of (lat, lon) ("+ne.latstr+", "+ne.lonstr+") }");
    }
    static public Vector generate(Structure str) {
	Vector vec=null;
    String title;
    NamedLocationTime nlm;
    
    // Attribute atr=str.getAttribute("NamedLocationList");
    // title= getFirstValForAttribute(atr);

	// get first list in structure
	ListElement me = str.getContentList();
	if (me != null) {
	    System.out.println("me!=null : "+me);
	    vec = new Vector();

	    for (Enumeration en=me.getChildren(); en.hasMoreElements(); ) {
		    // for each child of le that is a list 
		    ListElement child=((Element)en.nextElement()).getAsList();
		    if (child != null) {
			System.out.println("child!=null : "+child);

		        String name = getNode(child, "name");
			System.out.println("name: "+name);

			/* new */			
			String attrName="values";
			Hashtable hs=new Hashtable();
			getChildAttributes(child, attrName,hs);

		        String lat =  (String)hs.get("latitude");
			System.out.println("lat: "+lat);

		        String lon =  (String)hs.get("longitude");
			System.out.println("lon: "+lon);

		        String start =  (String)hs.get("startDate");
			System.out.println("start: "+start);

		        String end = (String)hs.get("thruDate");
			System.out.println("thru: "+end);

		        nlm=new NamedLocationTime(name, lat, lon, start, end, null);
			/* end new */

			/* old ==========================================
		        String lat = getNode(child, "latitude");
		        String lon = getNode(child, "longitude");
		        String start = getNode(child, "startDate");
		        String end = getNode(child, "thruDate");

			MetricTable hs=new MetricTable();
			getChildAttributes(child, "Metrics",hs);
			System.out.println("Name: "+name+" has metrics: "+hs);
		        nlm=new NamedLocationTime(name, lat, lon, start, end, hs);
			============== end old ============================ */

		        if (nlm.isValid()) {
		            vec.add(nlm);
		        } else {
		            handleInvalid(nlm);
		        }
		    }
	    }
	}
	return vec;
    }

//     static Hashtable getChildAttributes(ListElement le, String attrName) {
// 	MetricTable retht=new MetricTable();
// 	getChildAttributes(le, attrName, retht);
// 	return retht.asHashtable();
//     }

    static MetricTable getChildAttributes(ListElement le, String attrName) {
	MetricTable retht=new MetricTable();
	getChildAttributes(le, attrName, retht);
	return retht;
    }

    static void getChildAttributes(ListElement le, String attrName, MetricTable retht) {
	Attribute atr=le.getAttribute(attrName);
	Attribute tmpAtr;
	String name;
	String valStr;
	System.out.println("getChildAttributes -- attribute for name: "
			   +attrName+" atr: "+atr);

	Enumeration atren=null;
	if (atr != null) {
	    atren=atr.getAttributes();
	    if (atren!=null) {
		while(atren.hasMoreElements()) {
		    tmpAtr=(Attribute)atren.nextElement();
		    if (tmpAtr!=null) {
			name=tmpAtr.getName();
			valStr=getFirstValForAttribute(tmpAtr);
			if (valStr!=null) {
			    retht.put(name, valStr);
			} else { 
			    System.err.println("valStr null for "+name); 
			}
		    } else { 
			System.err.println("tmpAtr null"); 
		    }
		}
	    }
	}
    }


    static void getChildAttributes(ListElement le, String attrName, Hashtable retht) {
	Attribute atr=le.getAttribute(attrName);
	Attribute tmpAtr;
	String name;
	String valStr;
	System.out.println("getChildAttributes -- attribute for name: "
			   +attrName+" atr: "+atr);

	Enumeration atren=null;
	if (atr != null) {
	    atren=atr.getAttributes();
	    if (atren!=null) {
		while(atren.hasMoreElements()) {
		    tmpAtr=(Attribute)atren.nextElement();
		    if (tmpAtr!=null) {
			name=tmpAtr.getName();
			valStr=getFirstValForAttribute(tmpAtr);
			if (valStr!=null) {
			    retht.put(name, valStr);
			} else { 
			    System.err.println("valStr null for "+name); 
			}
		    } else { 
			System.err.println("tmpAtr null"); 
		    }
		}
	    }
	}
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


   public static Collection getNamedLocationsAtTime(ScheduleImpl sched, long time) {
      return sched.getScheduleElementsWithTime(time);
    }
    public static TreeSet getTransitionTimes(ScheduleImpl sched) {
      TreeSet transitionTimes = null;
    //if (transitionTimes==null) {
        transitionTimes = new TreeSet();
        for (Iterator it=sched.iterator(); it.hasNext(); ) {
          NamedLocationTime nltm=(NamedLocationTime)it.next();
          transitionTimes.add(new Long(nltm.getStartTime()));
          transitionTimes.add(new Long(nltm.getEndTime()));
        }
    //}
      return transitionTimes;
    }

    public static void main(String[] argv) {
	JFrame frame;
	//NamedLocation nl = null;
  // c:\\dev\\opmp\\reltimedloc_h.xml
	if (argv.length < 1)
	    System.out.println("Please specify NamedLocation file.");
	else {
	    System.out.println("filenmae: "+argv[0]);
	    try {
		XmlInterpreter xint = new XmlInterpreter();

		FileInputStream fin = new FileInputStream(argv[0]);
		Structure s = xint.readXml(fin);
		fin.close();
		
		Vector vec=NamedLocationTime.generate(s);
		if (vec.size()<1) {
		    System.err.println("No NamedLocations.  Make sure that your file parses correctly.");
		} else {
		    System.err.println("NamedLocationTimes: "+vec);
        ScheduleImpl sched=new ScheduleImpl(vec);

        Collection times=getTransitionTimes(sched);
        for (Iterator it=times.iterator(); it.hasNext(); ) {
          Long ttime=(Long)it.next();
          Collection nls=getNamedLocationsAtTime(sched, ttime.longValue());
          System.out.println("NamedLocationTimes at time: "+ttime+": ");
          System.out.println(new Vector(nls));
        }


   }

	    } catch (Exception ex) {
		System.err.println("Exception: "+ex.getMessage());
		ex.printStackTrace();
	    }
	}
    }
}
