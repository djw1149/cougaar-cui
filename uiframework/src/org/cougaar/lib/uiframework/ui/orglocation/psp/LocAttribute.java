
package org.cougaar.lib.uiframework.ui.orglocation.psp;

import java.text.*;
import java.util.*;

import org.cougaar.util.UnaryPredicate;

import org.cougaar.lib.uiframework.query.*;
import org.cougaar.lib.uiframework.query.generic.*;
import org.cougaar.lib.uiframework.ui.orglocation.data.*;
import org.cougaar.lib.uiframework.ui.orglocation.plugin.TableWrapper;

public class LocAttribute extends QueryAttribute {
  public static final byte LATITUDE = 0;
  public static final byte LONGITUDE = 1;

  private byte lat_or_lon = -1;

  private DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
  private PSP_QueryBase lpSupport = null;

  private static class TableSeeker implements UnaryPredicate {
    public boolean execute (Object o) {
      return (o instanceof TableWrapper) &&
        ((TableWrapper) o).getName().equals("OrgLocTable");
    }
  }
  private UnaryPredicate locTable = new TableSeeker();

  public LocAttribute (PSP_QueryBase base, byte type) {
    lpSupport = base;
    lat_or_lon = type;
    if (lat_or_lon == LATITUDE)
      setName("latitude");
    else
      setName("longitude");
  }

  public String eval (EvaluationLocus l) throws QueryException {
    String orgName = l.getCoordinate("Organization").location.getName();
    int day = getDayNumber(l.getCoordinate("Time").location.getName());

    Hashtable t = null;
    TPLocation thing = null;
    Collection lpStuff = lpSupport.getFromLogplan(locTable);
    Iterator i = lpStuff.iterator();
    if (i.hasNext())
      t = ((TableWrapper) i.next()).getTable();
    if (i.hasNext())
      System.out.println("More than one Loc. Table on the plan--what gives?");

    if (t == null)
      System.out.println("Loc. Table not found");
    else
      thing = (TPLocation) t.get(orgName);

    Date d = new Date(DayBaseModel.getMillisForDay(day));
    if (thing != null && thing.isInScope(d)) {
      if (lat_or_lon == LATITUDE)
        return String.valueOf(thing.getLocation(d).getLatitude());
      else
        return String.valueOf(thing.getLocation(d).getLongitude());
    }
    return "null";
  }

  private int getDayNumber (String s) throws QueryException {
    StringTokenizer tok = new StringTokenizer(s, " ", false);
    if (tok.hasMoreTokens()) {
      try {
        return Integer.parseInt(tok.nextToken());
      }
      catch (Exception eek) { }
    }
    throw new QueryException("Malformed day spec:  \"" + s + "\"");
  }
}