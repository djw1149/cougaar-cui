

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
import com.bbn.openmap.layer.location.*;

import org.cougaar.lib.uiframework.transducer.XmlInterpreter;
import org.cougaar.lib.uiframework.transducer.elements.Structure;
import org.cougaar.lib.uiframework.ui.map.util.NamedLocationTM;
import org.cougaar.domain.planning.ldm.plan.ScheduleImpl;

class TimedXmlLayerModel extends XmlLayerModel {
    OMGraphicList curTimeMarkers;
    ScheduleImpl allNLUnits;
    Collection transitionTimes;

  OMGraphic findClosest(int x, int y, float limit) {
    return curTimeMarkers.findClosest(x, y, limit);
  }


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

	private Unit makeUnit(float lat, float lon, Color color,
			      String label, String msg, Hashtable data)
	{
	    OMGraphic omgraphic;

	    // omgraphic = new VecIcon(lat, lon, color); 
	    String type="infantry";
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
      return unit;
	    
	}

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

    
    void load(Structure s) {
      allNLUnits=new ScheduleImpl();
      curTimeMarkers=new OMGraphicList();

        System.out.println("TimedXmlLayerModel.load()");
		Vector vec=NamedLocationTM.generate(s);


    // nltmmodel=new NamedLocationTMModel(urlString);
    // make nltmmodel a member of State
    // setTime(NamedLocationTMModel.getEpoch());

    Unit unit;
		for (Iterator itr=vec.iterator(); itr.hasNext();) {
		    NamedLocationTM nl=(NamedLocationTM)itr.next();
		    unit = makeUnit(nl.getLatitude(),nl.getLongitude(), Color.white, // red,
			     nl.getName(),
			     nl.getName(),
//			     " MSG ",
			     nl.getMetrics()
			     // createData(50, 100, 250)
			     );
        nl.setUnit(unit);
        allNLUnits.add(nl);
		}

    
    transitionTimes=NamedLocationTM.getTransitionTimes(allNLUnits);
        for (Iterator it=transitionTimes.iterator(); it.hasNext(); ) {
          Long ttime=(Long)it.next();
          Collection nls=NamedLocationTM.getNamedLocationsAtTime(allNLUnits, ttime.longValue());

          System.out.println("NamedLocationTMs at time: "+ttime+": ");
          System.out.println(nls);
        }

  }

    Collection getTransitionTimes() {
      return transitionTimes;
    }


  void setTime(long time) {
      System.out.println("tmodel setTime");
      curTimeMarkers.clear();
      Collection nls=NamedLocationTM.getNamedLocationsAtTime(allNLUnits, time);
      for (Iterator it=nls.iterator(); it.hasNext(); ) {
          NamedLocationTM nltm=(NamedLocationTM)it.next();
     System.out.println("tmodel setTime "+nltm);

          if (nltm!=null && nltm.getUnit()!=null &&
            nltm.getUnit().getGraphic()!=null) {
              curTimeMarkers.add(nltm.getUnit().getGraphic());
              System.out.println("tmodel setTime +nltm added");
          }
      }
  }

  final static Vector emptyVector=new Vector();
  Iterator markerIterator() {
    Iterator rit;
      if (curTimeMarkers!=null&&curTimeMarkers.getTargets()!=null) {
        rit=curTimeMarkers.getTargets().iterator();
      } else {
        rit=emptyVector.iterator();
      }
	    return rit;
    }
  }




