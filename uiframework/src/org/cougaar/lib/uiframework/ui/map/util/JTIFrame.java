/*
 * <copyright>
 *  Copyright 1997-2001 BBNT Solutions, LLC
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

package org.cougaar.lib.uiframework.ui.map.util;

import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.BorderLayout;
import org.cougaar.lib.uiframework.transducer.*;
import org.cougaar.lib.uiframework.transducer.elements.*;
import java.awt.event.*;

public class JTIFrame extends JFrame implements Serializable {

    JTree jtree=new JTree();
    String fname="";
    public void setFilename(String name) { fname=name; }
    public String getFilename() { return fname; }

  public JTIFrame () {
  }

    public JTIFrame (String fname) {
	init(fname);
    }

    public void init(String fname) {
	this.fname=fname;
	init();
    }

    public void show() {
      jtree.clearSelection();
      super.show();
    }

    protected boolean handleException(Exception e) {
	boolean retcode=false;
		    System.err.println("jtree -- Make sure that your file parses correctly.");
		    DefaultMutableTreeNode top = new DefaultMutableTreeNode("Metrics");
		    DefaultMutableTreeNode nn=new DefaultMutableTreeNode("1. Days Supply as % of Required days"); 
		    top.add(nn);
		    nn=new DefaultMutableTreeNode("2. Inventory as % of Capacity");    top.add(nn);
		    nn=new DefaultMutableTreeNode("3. Inventory as % of Safety Level");    top.add(nn);
		    nn=new DefaultMutableTreeNode("4. Cumulative Expenditures as % of Budget");    top.add(nn);
		    nn=new DefaultMutableTreeNode("5. Cumulative Contract Utilization as % of Ceiling");  top.add(nn);
		    nn=new DefaultMutableTreeNode("6. Contract Utilization as % of Monthly Ceiling");    top.add(nn);

		    jtree = new JTree(top);
		    retcode=true;
		    return retcode;

    }
    public void init() {
	try {
	    JTreeInterpreter jti = new JTreeInterpreter();
	    XmlInterpreter xint = new XmlInterpreter();
	    
	    try {
	    FileInputStream fin = new FileInputStream(fname);
	    Structure s = xint.readXml(fin);
	    fin.close();
	    
	    jtree=jti.generate(s);
	    } catch (Exception ex) {
		boolean okNow=handleException(ex);
		if (!okNow) {
		    throw ex;
		}

		System.err.println("ok.");
	    }
	    if (jtree==null) {
		System.err.println("NULL JTree.  Make sure that your file parses correctly.");
	    } else {
		getContentPane().add(jtree, BorderLayout.CENTER);
		setTitle(jti.getTitle());
		pack();
		setSize(150, 200);
	    }
	} catch (Exception ex) {
	    System.err.println("Exception: "+ex.getMessage());
	    ex.printStackTrace();
	}
    }
    
    public TreeSelectionModel getSelectionModel() { 
	return jtree.getSelectionModel(); 
    }
    public void addTreeSelectionListener(TreeSelectionListener tsl) { 
	jtree.addTreeSelectionListener(tsl); 
    }
    /*
    private void addMyMouseListener(JTree jtree) {
	MouseListener ml = new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
		    int selRow = tree.getRowForLocation(e.getX(), e.getY());
		    TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
		    if(selRow != -1) {
			if(e.getClickCount() == 1) {
			    mySingleClick(selRow, selPath);
			}
		    }
		}
	    };
	jtree.addMouseListener(ml);
    }
     */
    public String getSelectedString() {
	String retstring="";
	DefaultMutableTreeNode node = (DefaultMutableTreeNode)
	    jtree.getLastSelectedPathComponent();

	if (node != null) {
	    Object nodeInfo = node.getUserObject();
	    retstring = nodeInfo.toString();
	}
	return retstring;
    }

    public static void main(String[] argv) {
	if (argv.length < 1)
	    System.out.println("Please specify JTree file.");
	else {
	    JTIFrame jtiframe=new JTIFrame(argv[0]);
	    System.out.println("Pop up...");
	    jtiframe.setVisible(true);

	    try { Thread.sleep(5000l); } catch (Exception ex) {}
	    System.out.println("Pop down...");
	    jtiframe.setVisible(false);
	    try { Thread.sleep(1000l); } catch (Exception ex) {}
	    System.out.println("Pop up...");
	    jtiframe.setVisible(true);
	    try { Thread.sleep(5000l); } catch (Exception ex) {}
	    System.out.println("Pop down...");
	    jtiframe.setVisible(false);
	    try { Thread.sleep(1000l); } catch (Exception ex) {}
	    System.out.println("Pop up...");
	    jtiframe.setVisible(true);
	    try { Thread.sleep(5000l); } catch (Exception ex) {}

	    System.out.println("Times up -- I'm leaving...");
	    System.exit(0);

	}
    }
}
