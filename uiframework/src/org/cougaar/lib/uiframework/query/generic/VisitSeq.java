
package org.cougaar.lib.uiframework.query.generic;

/**
 *  A VisitSeq is an ordered sequence of nodes within a given dimension.
 *  Specifically, they represent the ones for which values should be calculated
 *  based on the query that was submitted.
 */
public class VisitSeq {
  private static class Link {
    public Link next = null;
    public VisitLocus locus = null;

    public Link (VisitLocus v) {
      locus = v;
    }
  }

  // variables associated with permanent state
  private Link first = null;
  private Link last = null;
  private int length = 0;

  // variables that change during iteration
  private Link cursor = null;

  /**
   *  A shallow copy of the data structure.  Regardless of this instance's
   *  state at the time of the call, the copy is initialized so that its first
   *  element is "current".
   *  @return a copy of this sequence
   */
  public VisitSeq duplicate () {
    VisitSeq ret = new VisitSeq();
    for (Link q = first; q != null; q = q.next)
      ret.addLocus(q.locus);
    return ret;
  }

  /**
   *  During an iteration through the list, this reports on which element is
   *  under the "cursor" at the time of the call.
   *  @return the current locus
   */
  public VisitLocus getCurrentLocus () {
    if (cursor != null)
      return cursor.locus;
    return null;
  }

  /**
   *  Advance the iterating "cursor" to the next element in the list, if any.
   */
  public void next () {
    if (cursor != null)
      cursor = cursor.next;
  }

  /**
   *  Return the interating "cursor" to the initial position, if it exists.
   */
  public void reset () {
    cursor = first;
  }

  /**
   *  Report whether the iterating "cursor" rests on an actual element.  This
   *  method returns false when all elements have been visited during an
   *  iteration.
   *  @return true iff the cursor rests on a valid element
   */
  public boolean hasCurrent () {
    return cursor != null;
  }

  /**
   *  Add a new locus to this list (during the construction phase, mainly).
   *  @param v the new locus
   */
  public void addLocus (VisitLocus v) {
    Link p = new Link(v);
    if (last == null)
      cursor = first = p;
    else
      last.next = p;

    last = p;
    length++;
  }

  /**
   *  Report the number of places that this sequence visits.
   *  @return the length of the sequence
   */
  public int getLength () {
    return length;
  }
}