/*
 * <copyright>
 *  Copyright 2001 BBNT Solutions, LLC
 *  under sponsorship of the Defense Advanced Research Projects Agency (DARPA).
 * 
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published by
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).
 * 
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 *  PROVIDED 'AS IS' WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.
 * </copyright>
 */
package mil.darpa.log.alpine.blackjack.assessui.society;

import java.util.*;
import java.io.*;
import java.lang.System;

import org.cougaar.core.society.UID;

import org.cougaar.lib.aggagent.dictionary.glquery.samples.CustomQueryBaseAdapter;
import org.cougaar.domain.glm.ldm.Constants;

import mil.darpa.log.alpine.blackjack.assessui.util.AggInfoStructure;
import mil.darpa.log.alpine.blackjack.assessui.util.AggInfoEncoder;

import org.cougaar.domain.glm.plugins.TaskUtils;

import org.cougaar.domain.planning.ldm.asset.Asset;
import org.cougaar.domain.planning.ldm.asset.TypeIdentificationPG;

import org.cougaar.domain.planning.ldm.measure.CountRate;
import org.cougaar.domain.planning.ldm.measure.FlowRate;
import org.cougaar.domain.planning.ldm.measure.Rate;
import org.cougaar.domain.planning.ldm.plan.AspectType;
import org.cougaar.domain.planning.ldm.plan.PlanElement;
import org.cougaar.domain.planning.ldm.plan.Preference;
import org.cougaar.domain.planning.ldm.plan.PrepositionalPhrase;
import org.cougaar.domain.planning.ldm.plan.Task;

public class DemandQueryAdapter extends CustomQueryBaseAdapter {

  private static final String METRIC = "Demand";

  private StringBuffer output_xml;
  private boolean send_xml;
  private AggInfoEncoder myEncoder = new AggInfoEncoder();
  private AggInfoStructure myStructure;

  private long c_time_msec = 0L;
  private static long dayMillis = 24L * 60L * 60L * 1000L;

  public void execute (Collection matches, String eventName) {

    Iterator iter = matches.iterator();
    int index;
    String org = null;
    UID cluster_object_name = null;

    index = 0;
    output_xml = null;
    send_xml = false;

    extractCDateFromSociety ();

    System.out.print ("(0d)");

    while (iter.hasNext()) {

      Object o = (Object) iter.next();

      if (o instanceof Task) {

        Task t = (Task) o;
        String item = null;
        String start_time = null;
        String end_time = null;
        long time_long = 0;
        Double time_double = new Double(0.0);
        String rate = null;

        if (t.getVerb().equals(Constants.Verb.PROJECTSUPPLY) == true) {

          // Pull out the start time and put it in a string

          Preference start_pref = t.getPreference (AspectType.START_TIME);
          if (start_pref != null) {
            time_double = new Double (start_pref.getScoringFunction().getBest().getAspectValue().getValue());
          }

          time_long = time_double.longValue ();
          start_time = String.valueOf (convertMSecToCDate(time_long));

          // Pull out the end time and put it in a string

          Preference end_pref = t.getPreference (AspectType.END_TIME);

          if (end_pref != null) {
            time_double = new Double (end_pref.getScoringFunction().getBest().getAspectValue().getValue());
          }

          time_long = time_double.longValue ();
          end_time = String.valueOf (convertMSecToCDate(time_long));

          // Find the organization in the "For" preposition

          PrepositionalPhrase for_phrase = t.getPrepositionalPhrase ("For");

          if (for_phrase != null) {
            org = (String) for_phrase.getIndirectObject();
            if (org.startsWith ("UIC/")) {
              org = org.substring (4);
            }
          }

          PlanElement pe = t.getPlanElement();

          if (pe == null) {
//            System.out.println ("WARNING: no plan element for demand task");
            continue;
          }

          cluster_object_name = pe.getUID();

          if (cluster_object_name == null) {
//            System.out.println ("WARNING: no UID for plan element of demand task");
            continue;
          }

          if (org.compareTo(cluster_object_name.getOwner()) != 0) {
//              System.out.print ("demand cluster " +
//                              cluster_object_name.getOwner());
//              System.out.println (" not the same as io " + org);
              continue;
          }

          // Find the item name in direct object's type identification

          Asset direct_object = t.getDirectObject();

          if (direct_object == null) {
//            System.out.println ("WARNING: No direct object found");
            continue;
          }

          TypeIdentificationPG type_id_pg = direct_object.getTypeIdentificationPG();

          if (type_id_pg == null) {
//            System.out.println ("WARNING: no typeIdentificationPG for asset");
            continue;
          }

          item = type_id_pg.getTypeIdentification();

          // Find what the demand rate is and put it in a string

          Rate demand_rate = TaskUtils.getRate (t);

          if ((demand_rate != null) && (demand_rate instanceof CountRate)) {
            CountRate cr = (CountRate) demand_rate;
            rate = new String ("" + cr.getEachesPerDay());
          }
          else if ((demand_rate != null) && (demand_rate instanceof FlowRate)) {
            FlowRate fr = (FlowRate) demand_rate;
            rate = new String ("" + fr.getGallonsPerDay());
          }
          else
          {
//            System.out.println ("WARNING: No rate for org " + org + ", item " + item);
            continue;
          }

//          System.out.println ("item is: " + item);

          myStructure = new AggInfoStructure (item, start_time, end_time, rate);

          if (!send_xml) {
//            System.out.println ("org is " + org);
            output_xml = myEncoder.encodeStartOfXML(org, METRIC);
          }

          myEncoder.encodeDataAtom(output_xml, myStructure);

          send_xml = true;

          index++;

          if ((index % 500) == 0)
            System.out.print ("(" + index + "d)");

        } /* if verb is "ProjectSupply" */
      } /* if o is Task */
    } /* while iter */

    System.out.println ("");
    System.out.println ("**************************************************************************");
    System.out.println ("DemandQueryAdapter: " + index + " records for org " + cluster_object_name.getOwner());
    System.out.println ("**************************************************************************");

    if (send_xml) {
      myEncoder.encodeEndOfXML(output_xml);
    }
  } /* end of execute */

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

  public void returnVal (OutputStream out) {

    PrintWriter p = new PrintWriter (out);

    if (send_xml)
    {
      p.println (output_xml.toString());
      p.flush ();
//      System.out.println (output_xml);
    }

    send_xml = false;
  } /* end of returnVal */

}
