package mil.darpa.log.alpine.blackjack.assessui.society;

import java.util.*;
import java.io.*;

import org.cougaar.lib.aggagent.dictionary.glquery.samples.CustomQueryBaseAdapter;
import org.cougaar.domain.glm.ldm.Constants;

import mil.darpa.log.alpine.blackjack.assessui.util.AggInfoStructure;
import mil.darpa.log.alpine.blackjack.assessui.util.AggInfoEncoder;

import org.cougaar.core.society.UID;

import org.cougaar.domain.glm.plugins.TaskUtils;
import org.cougaar.domain.glm.ldm.asset.Inventory;
import org.cougaar.domain.glm.ldm.asset.ScheduledContentPG;
import org.cougaar.domain.glm.ldm.plan.QuantityScheduleElementImpl;

import org.cougaar.domain.planning.ldm.measure.Rate;
import org.cougaar.domain.planning.ldm.plan.AspectType;
import org.cougaar.domain.planning.ldm.plan.Preference;
import org.cougaar.domain.planning.ldm.plan.PrepositionalPhrase;
import org.cougaar.domain.planning.ldm.plan.Schedule;
import org.cougaar.domain.planning.ldm.asset.Asset;
import org.cougaar.domain.planning.ldm.asset.TypeIdentificationPG;

public class InventoryQueryAdapter extends CustomQueryBaseAdapter {

  private static final String METRIC = "Inventory";

  private StringBuffer output_xml;
  private boolean send_xml = false;
  private int xml_count;
  private AggInfoEncoder myEncoder = new AggInfoEncoder();
  private AggInfoStructure nextStructure = null;

  public void execute (Collection matches, String eventName) {

    Iterator iter = matches.iterator();
    int index;
    int inventory_count = 1;

    index = 0;
    xml_count = 0;

    output_xml = myEncoder.encodeStartOfXML(METRIC);

    while (iter.hasNext()) {

      Object o = (Object) iter.next();

      if (o instanceof Inventory) {

        Inventory in = (Inventory) o;

        String org = null;
        String item = null;
        String start_time = null;
        String end_time = null;
        long start_time_long = 0;
        long end_time_long = 0;
        Double time_double = new Double(0.0);
        String rate = null;

        UID inventory_object_name = in.getUID();

        if (inventory_object_name == null) {
          System.out.println ("WARNING: no UID for inventory asset");
          continue;
        }

        org = inventory_object_name.getOwner();

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

        TypeIdentificationPG type_id_pg = a1.getTypeIdentificationPG();

        if (type_id_pg == null) {
          System.out.println ("WARNING: no typeIdentificationPG for asset");
          continue;
        }

        item = type_id_pg.getTypeIdentification();

        Enumeration schedule_list = s1.getAllScheduleElements();

        System.out.print ("" + inventory_count + "org " + org + ", item " + item + "...");

        inventory_count++;

        int looping = 0;

        while (schedule_list.hasMoreElements()) {
          QuantityScheduleElementImpl element;

          element = (QuantityScheduleElementImpl) schedule_list.nextElement();

          rate = "" + element.getQuantity();

          looping++;
          System.out.print (" " + looping + " ");

          // Pull out the start time and put it in a string

          time_double = new Double (element.getStartTime());

          start_time_long = time_double.longValue ();
          start_time = String.valueOf (start_time_long);

          // Pull out the end time and put it in a string

          time_double = new Double (element.getEndTime());

          end_time_long = time_double.longValue ();
          end_time = String.valueOf (end_time_long);

          // One more record parsed
          index++;

          // If the structure is null, create one
          if (nextStructure == null) {
            nextStructure = new AggInfoStructure (org, item, start_time, end_time, rate);
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
            writeStructureToXML (nextStructure);
            nextStructure = new AggInfoStructure (org, item, start_time, end_time, rate);
          }
        } /* end of while */

        System.out.println ("");

      } /* if o is Inventory */
    } /* while iter */

    if (nextStructure != null) {
      writeStructureToXML (nextStructure);
    }

    System.out.println ("**************************************************************************");
    System.out.println ("Inventory sending " + index + " records, amounts to " + xml_count + " xml records");
    System.out.println ("**************************************************************************");

    myEncoder.encodeEndOfXML(output_xml);
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

  private void writeStructureToXML (AggInfoStructure new_structure) {
    myEncoder.encodeDataAtom (output_xml, new_structure);

    xml_count++;

    send_xml = true;

/*
    if (xml_count == 1) {
      System.out.println ("item is " + new_structure.getItem());
      System.out.println ("org is " + new_structure.getOrg());
      System.out.println ("start_time is " + new_structure.getStartTime());
      System.out.println ("end_time is " + new_structure.getEndTime());
      System.out.println ("rate is " + new_structure.getRate());
    }
*/

  } /* end of writeStructureToXML */

}
