package mil.darpa.log.alpine.blackjack.assessui.society;

import java.util.*;
import java.io.*;

import java.lang.Double;

import org.cougaar.lib.aggagent.dictionary.glquery.samples.CustomQueryBaseAdapter;
import org.cougaar.domain.glm.ldm.Constants;

import mil.darpa.log.alpine.blackjack.assessui.util.AggInfoStructure;
import mil.darpa.log.alpine.blackjack.assessui.util.AggInfoEncoder;

import org.cougaar.core.society.UID;

import org.cougaar.domain.glm.plugins.TaskUtils;
import org.cougaar.domain.glm.ldm.asset.Inventory;
import org.cougaar.domain.glm.ldm.asset.InventoryLevelsPG;
import org.cougaar.domain.glm.ldm.asset.ScheduledContentPG;
import org.cougaar.domain.glm.ldm.plan.QuantityScheduleElementImpl;
import org.cougaar.domain.glm.ldm.plan.RateScheduleElementImpl;
import org.cougaar.domain.planning.ldm.plan.ScheduleElementImpl;

import org.cougaar.domain.planning.ldm.measure.Rate;
import org.cougaar.domain.planning.ldm.plan.AspectType;
import org.cougaar.domain.planning.ldm.plan.Preference;
import org.cougaar.domain.planning.ldm.plan.PrepositionalPhrase;
import org.cougaar.domain.planning.ldm.plan.Schedule;
import org.cougaar.domain.planning.ldm.asset.Asset;
import org.cougaar.domain.planning.ldm.asset.TypeIdentificationPG;

public class TargetLevelQueryAdapter extends CustomQueryBaseAdapter {

  private static final String METRIC = "Target Level";

  private StringBuffer output_xml;
  private boolean send_xml;
  private int xml_count;
  private AggInfoEncoder myEncoder = new AggInfoEncoder();
  private AggInfoStructure nextStructure;
  String org;

  private long c_time_msec = 0L;
  private static long dayMillis = 24L * 60L * 60L * 1000L;

  public void execute (Collection matches, String eventName) {

    Iterator iter = matches.iterator();
    int index;

    index = 0;
    xml_count = 0;
    output_xml = null;
    send_xml = false;
    nextStructure = null;
    org = null;

    extractCDateFromSociety ();

    System.out.print ("(0t)");

try {

    while (iter.hasNext()) {

      Object o = (Object) iter.next();

      if (o instanceof Inventory) {

        Inventory in = (Inventory) o;

        String item = null;
        String start_time = null;
        String end_time = null;
        long start_time_long = 0;
        long end_time_long = 0;
        Double mydouble = new Double(0.0);
        String rate = null;
        double rate_double = 0.0;

        long first_inventory_time = 0;

        // Get the org id
        UID inventory_object_name = in.getUID();

        if (inventory_object_name == null) {
//          System.out.println ("WARNING: no UID for inventory asset");
          continue;
        }

        org = inventory_object_name.getOwner();

//        System.out.print ("(" + index + ") org: " + org + ";");

        // Find the item id
        ScheduledContentPG scheduled_content =
                                    in.getScheduledContentPG();

        if (scheduled_content == null)
          continue;

        Asset a1 = scheduled_content.getAsset();
        Schedule s1 = scheduled_content.getSchedule();

        if (a1 == null) {
          System.out.println ("WARNING: no asset in scheduledContentPG");
          continue;
        }

        if (s1 == null) {
          System.out.println ("WARNING: no schedule in scheduledContentPG");
          continue;
        }

        // Find the first day of inventory

        Enumeration schedule_list = s1.getAllScheduleElements();

        while (schedule_list.hasMoreElements()) {
          ScheduleElementImpl element;
          double temp_rate;

          element = (ScheduleElementImpl) schedule_list.nextElement();

          if (element instanceof QuantityScheduleElementImpl) {
              QuantityScheduleElementImpl q_element = (QuantityScheduleElementImpl) element;
              mydouble = new Double(q_element.getQuantity());

              if (mydouble.isNaN())
                temp_rate = 0.0;
              else
                temp_rate = q_element.getQuantity();
          }
          else if (element instanceof RateScheduleElementImpl) {
              RateScheduleElementImpl r_element = (RateScheduleElementImpl) element;
              mydouble = new Double(r_element.getRate());

              if (mydouble.isNaN())
                temp_rate = 0.0;
              else
                temp_rate = r_element.getRate();
          }
          else {
              System.out.println ("Not sure what kind of ScheduleElementImpl this is.");
              continue;
          }

          if (temp_rate > 0.0) {
              // Pull out the start time

              mydouble = new Double (element.getStartTime());

              start_time_long = mydouble.longValue ();
              first_inventory_time = convertMSecToCDate(start_time_long);

              // And, get out of the loop
              break;
          }

        }

        TypeIdentificationPG type_id_pg = a1.getTypeIdentificationPG();

        if (type_id_pg == null) {
          System.out.println ("WARNING: no typeIdentificationPG for asset");
          continue;
        }

        item = type_id_pg.getTypeIdentification();

//        System.out.print ("item: " + item + ";");

        // Work on getting the target level information

        InventoryLevelsPG invLevPG = in.getInventoryLevelsPG();

        if (invLevPG == null)
          continue;

        Schedule goal_schedule = invLevPG.getGoalLevelSchedule();
        Schedule reorder_schedule = invLevPG.getReorderLevelSchedule();

        if (goal_schedule == null) {
          System.out.println ("WARNING: no goal schedule in InventoryLevelsPG");
          continue;
        }

        if (reorder_schedule == null) {
          System.out.println ("WARNING: no reorder schedule in InventoryLevelsPG");
          continue;
        }

        Enumeration goal_list = goal_schedule.getAllScheduleElements();

        Collection reorder_collection = null;
        QuantityScheduleElementImpl goal_element = null;
        QuantityScheduleElementImpl reorder_element = null;

        while (goal_list.hasMoreElements()) {

          goal_element = (QuantityScheduleElementImpl) goal_list.nextElement();

          mydouble = new Double(goal_element.getQuantity());

          if (mydouble.isNaN())
            rate_double = 0.0;
          else
            rate_double = goal_element.getQuantity();

          // Skip the record if the goal level is zero.  The unit is
          // meaningless at that point
          if (rate_double == 0.0)
             continue;

          // Pull out the start time and put it in a string

          mydouble = new Double (goal_element.getStartTime());

          start_time_long = mydouble.longValue ();
          start_time = String.valueOf (convertMSecToCDate(start_time_long));

          // If there isn't any inventory yet, skip this record
          if (convertMSecToCDate(start_time_long) < first_inventory_time)
              continue;

          // Pull out the end time and put it in a string

          mydouble = new Double (goal_element.getEndTime());

          end_time_long = mydouble.longValue ();
          end_time = String.valueOf (convertMSecToCDate(end_time_long));

          reorder_collection = reorder_schedule.getEncapsulatedScheduleElements
                                       (start_time_long, end_time_long);

          // The target level is the average of the reorder level and
          // the goal level
          if (reorder_collection.size() == 1) {
              double reorder_double;

              Iterator reorder_iter = reorder_collection.iterator();
              reorder_element = (QuantityScheduleElementImpl) reorder_iter.next();
              mydouble = new Double(reorder_element.getQuantity());

              if (mydouble.isNaN())
                reorder_double = 0.0;
              else
                reorder_double = reorder_element.getQuantity();

              // Skip the record if the reorder level is zero.  The unit is
              // meaningless at that point
              if (reorder_double == 0.0)
                 continue;

              rate_double = (rate_double + reorder_double) / 2.0;

              rate = "" + rate_double;
          }
          else {
              System.out.println ("reorder schedule time range not present or not unique.");
              continue;
          }

          // One more record parsed
          index++;

          // If the structure is null, create one
          if (nextStructure == null) {
            nextStructure = new AggInfoStructure (item, start_time, end_time, rate);
          }
          // If the rates are the same, and the start time of the new record
          // is the same as the end time of the old record, then combine
          // the records
          else if ((rate.compareTo (nextStructure.getRate ()) == 0) &&
                   (start_time.compareTo (nextStructure.getEndTime()) == 0)) {
            nextStructure.setEndTime (end_time);
          }
          // Output the record and start a new one
          else {
            writeStructureToXML (org, nextStructure);
            nextStructure = new AggInfoStructure (item, start_time, end_time, rate);
          }
        } /* end of while */

//        System.out.println ("");

      } /* if o is Inventory */
    } /* while iter */

} catch (Exception e) {

    System.out.println ("Exception caught!");
    e.printStackTrace();
}

    if (nextStructure != null) {
      writeStructureToXML (org, nextStructure);
    }

    System.out.println ("");
    System.out.println ("**************************************************************************");
    System.out.println ("TargetLevelQueryAdapter: " + index + " records, " + xml_count + " xml records for org " + org);
    System.out.println ("**************************************************************************");

    if (send_xml) {
      myEncoder.encodeEndOfXML(output_xml);
    }
  } /* end of execute */

  public void returnVal (OutputStream out) {

    PrintWriter p = new PrintWriter (out);

    if (send_xml)
    {
      p.println (output_xml.toString());
      p.flush ();
    }

/*
    if (xml_count > 0)
      System.out.println (output_xml);
*/

    send_xml = false;
  } /* end of returnVal */

  private void writeStructureToXML (String org, AggInfoStructure new_structure) {
    // We are about to send a new one
    if (!send_xml) {
      output_xml = myEncoder.encodeStartOfXML(org, METRIC);
    }

    myEncoder.encodeDataAtom (output_xml, new_structure);

    xml_count++;

    send_xml = true;

    if ((xml_count % 500) == 0)
      System.out.print ("(" + xml_count + "t)");

  } /* end of writeStructureToXML */

  private void extractCDateFromSociety () {

    String cdate_property = System.getProperty("org.cougaar.core.cluster.startTime");
    String timezone_property = System.getProperty("user.timezone");

    TimeZone tz = TimeZone.getTimeZone(timezone_property);

    GregorianCalendar gc = new GregorianCalendar (tz);

    StringTokenizer st = new StringTokenizer (cdate_property, "/");

    String c_time_month_string = st.nextToken();
    String c_time_day_string = st.nextToken();
    String c_time_year_string = st.nextToken();

    // Month is offset from zero, others are not
    // Last three are hour, minute, second

    gc.set (Integer.parseInt (c_time_year_string),
            Integer.parseInt (c_time_month_string) - 1,
            Integer.parseInt (c_time_day_string),
            0, 0, 0);


    c_time_msec = gc.getTime().getTime();

    // This was needed to ensure that the milliseconds were set to 0
    c_time_msec = c_time_msec / 1000;
    c_time_msec *= 1000;

  } /* end of extractCDateFromSociety */

  private int convertMSecToCDate (long current_time_msec) {
    return (int) ((current_time_msec - c_time_msec) / dayMillis);
  }

}