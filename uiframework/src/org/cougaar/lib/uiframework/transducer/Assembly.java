
package org.cougaar.lib.uiframework.transducer;

import org.cougaar.lib.uiframework.transducer.elements.*;

/**
 *  The abstract SqlTransducer uses this interface to retrieve Structures from
 *  the database.  Concrete implementations of this interface that are
 *  consistent with a particular SqlTransducer instance should be supplied by
 *  the instance in question.
 */
public interface Assembly {
  /**
   *  Retrieve a Structure from the database.  Before this method is called,
   *  the implementation should be aware of where to look in the database for
   *  the desired Structure.  Different implementations will be required for
   *  different transducers.
   *  @return a Structure as found in the database
   */
  public Structure readFromDb ();
}