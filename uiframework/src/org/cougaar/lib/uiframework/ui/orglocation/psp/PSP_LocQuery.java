
package org.cougaar.lib.uiframework.ui.orglocation.psp;

import java.util.*;

import org.cougaar.lib.uiframework.query.*;
import org.cougaar.lib.uiframework.query.generic.*;

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
   */
  protected void initQueryInterpreter () {
    responder = new GenericInterpreter();
    responder.addAttribute(new LocAttribute(this, LocAttribute.LATITUDE));
    responder.addAttribute(new LocAttribute(this, LocAttribute.LONGITUDE));
    responder.addDimension(createOrgDimension());
    responder.addDimension(createTimeDimension());
  }

  private QueryDimension createOrgDimension () {
    OrderlessDimension dim = new OrderlessDimension();
    dim.setName("Orgs");

    ListDimNode root = new ListDimNode("All Orgs");
    root.addMembers(new String[] {"Fred", "Joe", "Sam", "Herbie"});
    dim.setRoot(root);

    return dim;
  }

  private QueryDimension createTimeDimension () {
    IntegerDimension dim = new IntegerDimension();
    dim.setName("Time");
    dim.setRoot(new IntDimNode(-100, 100));
    return dim;
  }
}