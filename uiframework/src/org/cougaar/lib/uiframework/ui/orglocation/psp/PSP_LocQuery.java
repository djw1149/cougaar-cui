/*
 * <copyright>
 * Copyright 1997-2001 Defense Advanced Research Projects
 * Agency (DARPA) and ALPINE (a BBN Technologies (BBN) and
 * Raytheon Systems Company (RSC) Consortium).
 * This software to be used only in accordance with the
 * COUGAAR licence agreement.
 * </copyright>
 */

package org.cougaar.lib.uiframework.ui.orglocation.psp;

import java.text.*;
import java.util.*;

import org.w3c.dom.*;

import org.cougaar.util.ConfigFinder;
import org.cougaar.lib.planserver.*;

import org.cougaar.lib.uiframework.query.*;
import org.cougaar.lib.uiframework.query.generic.*;
import org.cougaar.lib.uiframework.ui.orglocation.data.DayBaseModel;

/**
 *  This class is a container for a QueryInterpreter designed to answer queries
 *  about the locations (latitude and longitude) of organizations.  It is
 *  intended to run inside an Aggregation Agent Cluster, where relevant data
 *  is available (provided by a LocSchedulePlugIn).
 */
public class PSP_LocQuery extends PSP_QueryBase {
  private GenericInterpreter responder = null;

  /**
   *  Provide a reference to the QueryInterpreter to be used by this PSP.  The
   *  abstract algorithm calls upon this method to supply the interpreter when
   *  it is processing a query.
   *  @return the QueryInterpreter implementation
   */
  protected QueryInterpreter getQueryInterpreter () {
    return responder;
  }

  /**
   *  Override this method with code to initialize the QueryInterpreter
   *  instance used by this PSP for answering requests.  In some cases, all of
   *  the initialization can be performed in the implementation class's
   *  constructor.  This method, however, is called after the PlugInDelegate
   *  reference becomes available.
   */
  protected void initQueryInterpreter (PlanServiceContext psc) {
    // set up DayBaseModel
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    try {
      DayBaseModel.baseTimeMillis = sdf.parse("2005/08/15").getTime();
    }
    catch (Exception buzz_off) {
      buzz_off.printStackTrace();
    }

    // create the query interpreter
    responder = new GenericInterpreter();

    // add the attributes
    responder.addAttribute(new TPLocAttribute(TPLocAttribute.START_TIME));
    responder.addAttribute(new TPLocAttribute(TPLocAttribute.END_TIME));
    responder.addAttribute(new TPLocAttribute(TPLocAttribute.LATITUDE));
    responder.addAttribute(new TPLocAttribute(TPLocAttribute.LONGITUDE));

    // Add the dimension
    responder.addDimension(createDimension(psc));

    // Debug mode:
    // echo_queries = true;
    // echo_results = true;
  }

  private QueryDimension createDimension (PlanServiceContext psc) {
    TPLocDimension ret = new TPLocDimension();
    ret.setName("OrgLocations");
    ret.setPlugIn(psc.getServerPlugInSupport().getDirectDelegate());
    return ret;
  }
}
