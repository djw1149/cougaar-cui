
package org.cougaar.lib.uiframework.query.generic;

import org.cougaar.lib.uiframework.transducer.elements.*;

import java.util.*;

/**
 *  <p>
 *  A DimPath is the fusion of the structure of a QueryDimension with the
 *  scoping information provided within a query Structure.  It contains a
 *  prototypical result subtree and a prototypical sequence of nodes within
 *  that tree for which field values are supposed to be expressed.  Upon demand,
 *  the DimPath instance can create copies of these structures, in pairs, so
 *  that calculated values can be easily inserted.  Each QueryDimension is
 *  responsible for providing DimPath instances for scopes claiming to be of
 *  that dimension.
 *  </p><p>
 *  Note:  this implementation is far from thread-safe.  It should never have
 *  to be used in multiple threads simultaneously, but if it ever is, then
 *  problems are almost certain to arise.
 *  </p>
 */
public class DimPath {
  private QueryDimension dimension = null;
  private ListElement root = null;
  private VisitSeq sequence = null;
  private boolean singleton = false;

  private ListElement currentRoot = null;
  private VisitSeq currentSeq = null;

  private VisitSeq workingSeq = null;

  public DimPath (QueryDimension d, ListElement r, VisitSeq s) {
    dimension = d;
    root = r;
    sequence = s;
  }

  public void setSingleton (boolean boo) {
    singleton = boo;
  }

  public boolean isSingleton () {
    return singleton;
  }

  public QueryDimension getDimension () {
    return dimension;
  }

  public ListElement getResultSet () {
    return currentRoot;
  }

  public VisitSeq getEvaluationSeq () {
    return currentSeq;
  }

  public void copyPrototypes () {
    currentSeq = new VisitSeq();
    workingSeq = sequence.duplicate();
    currentRoot = recursiveCopy(root);
  }

  private ListElement recursiveCopy (ListElement le) {
    ListElement ret = new ListElement();
    for (Enumeration e = le.getAttributes(); e.hasMoreElements(); )
      ret.addAttribute(copyAttribute((Attribute) e.nextElement()));

    // This check for the location match must occur here before the children
    // are considered.  Otherwise bad things will happen.
    VisitLocus loc = null;
    if (workingSeq.hasCurrent() &&
        (loc = workingSeq.getCurrentLocus()).record == le)
    {
      currentSeq.addLocus(new VisitLocus(loc.location, ret));
      workingSeq.next();
    }

    // Consider the children of this node
    for (Enumeration e = le.getChildren(); e.hasMoreElements(); )
      ret.addChild(recursiveCopy((ListElement) e.nextElement()));

    return ret;
  }

  private Attribute copyAttribute (Attribute att) {
    Attribute ret = new Attribute(att.getName());
    for (Enumeration e = att.getAttributes(); e.hasMoreElements(); )
      ret.addAttribute(copyAttribute((Attribute) e.nextElement()));
    for (Enumeration e = att.getChildren(); e.hasMoreElements(); ) {
      ValElement val = ((Element) e.nextElement()).getAsValue();
      if (val != null)
        ret.addChild(val);
    }
    return ret;
  }
}