/*
 * <copyright>
 *  Copyright 1997-2000 Defense Advanced Research Projects
 *  Agency (DARPA) and ALPINE (a BBN Technologies (BBN) and
 *  Raytheon Systems Company (RSC) Consortium).
 *  This software to be used only in accordance with the
 *  COUGAAR licence agreement.
 * </copyright>
 */
 
package org.cougaar.lib.uiframework.ui.components.desktop.dnd;

import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.io.PrintWriter;


import java.io.Serializable;
import java.io.File;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.BufferedInputStream;
import java.net.URLConnection;
import java.net.URL;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JTree;
import javax.swing.JScrollPane;
import javax.swing.event.*;
import javax.swing.tree.*;

import java.lang.reflect.Method;
import java.util.*;

import org.cougaar.core.cluster.ClusterIdentifier;
import org.cougaar.core.cluster.CollectionSubscription;
import org.cougaar.core.cluster.Subscription;
import org.cougaar.domain.planning.ldm.asset.Asset;
import org.cougaar.domain.planning.ldm.asset.TypeIdentificationPG;
import org.cougaar.domain.planning.ldm.plan.*;
import org.cougaar.lib.planserver.*;
import org.cougaar.core.util.*;
import org.cougaar.util.*;
import org.cougaar.lib.uiframework.ui.components.desktop.FileNode;
import org.cougaar.lib.uiframework.ui.components.desktop.FileInfo;

import org.cougaar.domain.glm.ldm.*;
import org.cougaar.domain.glm.ldm.*;
import org.cougaar.domain.glm.*;
import org.cougaar.domain.glm.ldm.asset.*;
import org.cougaar.domain.glm.ldm.oplan.*;
import org.cougaar.domain.glm.ldm.plan.*;
import org.cougaar.domain.glm.ldm.policy.*;


public class PSP_FileMover
  extends PSP_BaseAdapter
  implements PlanServiceProvider, UISubscriber
{
  private String myID;
  public String desiredOrganization;

  public PSP_FileMover() throws RuntimePSPException {
    super();
  }

  public PSP_FileMover(String pkg, String id) throws RuntimePSPException {
    setResourceLocation(pkg, id);
  }

  /* This PSP is referenced directly (in the URL from the client)
     and hence this shouldn't be called.
     */

  public boolean test(HttpInput query_parameters, PlanServiceContext psc) {
    super.initializeTest();
    return false; 
  }

  

  /*
    Called when a request is received from a client.
    Either gets the command ASSET to return the names of all the assets
    that contain a ScheduledContentPG or
    gets the name of the asset to plot from the client request.
  */

  public void execute( PrintStream out,
		       HttpInput query_parameters,
		       PlanServiceContext psc,
		       PlanServiceUtilities psu) throws Exception {
    try {
      myExecute(out, query_parameters, psc, psu);
    } catch (Exception e) {
      e.printStackTrace();
    };
  }

  /** The query data is one of:
    ASSET -- meaning return list of assets
    nomenclature:type id -- return asset matching nomenclature & type id
    UID: -- return asset with matching UID
    */

  private void myExecute( PrintStream out,
		       HttpInput query_parameters,
		       PlanServiceContext psc,
		       PlanServiceUtilities psu) throws Exception 
	{
		System.out.println("%%%% File Mover");
		
		if (query_parameters.hasBody()) 
     {
     	
     	//  we will reconnect to send large files, sending the fileinfo object
     	//  with part of the file in a byte array inside the fileinfo object
     	
     	FileInfo fi = (FileInfo) query_parameters.getBodyAsObject(); 
     	File xferfile = fi.getDropFile();
     	String destination = fi.getDestination();
     	
     	System.out.println("%%%% abs file path " + xferfile.getAbsolutePath());
     	File file = xferfile.getAbsoluteFile();
     	String newFilename = destination + "\\" + file.getName();
      FileOutputStream fos = new FileOutputStream(newFilename, fi.addFile);
     	System.out.println("%%%% psp_mover dest = " + destination);
      byte[] byteArray = fi.getByteArray();
      System.out.println("%%%% mover got bytes " + byteArray.length);     
      fos.write(byteArray);
      String response = "ok";
      Object o = (Object)response;
		 	ObjectOutputStream p = new ObjectOutputStream(out);
	    p.writeObject(o);
      System.out.println("%%%% mover psp got file "  + newFilename );
     }
     else 
     {
       System.out.println("WARNING: No body from Remote File Node");
       return;
     }
	 
  }
  
  
  
  public boolean returnsXML() {
    return true;
  }

  public boolean returnsHTML() {
    return false;
  }

  public String getDTD() {
    return "myDTD";
  }

  /* The UISubscriber interface.
     This PSP doesn't care if subscriptions change
     because it treats each request as a new request.
  */

  public void subscriptionChanged(Subscription subscription) 
  {
  }

  
  

}

 









