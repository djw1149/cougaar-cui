/*
 * <copyright>
 * Copyright 1997-2001 Defense Advanced Research Projects
 * Agency (DARPA) and ALPINE (a BBN Technologies (BBN) and
 * Raytheon Systems Company (RSC) Consortium).
 * This software to be used only in accordance with the
 * COUGAAR licence agreement.
 * </copyright>
 */
package org.cougaar.lib.uiframework.ui.map.layer.cgmicon.cgm;

import java.io.*;

public class BeginMetafile extends Command
{	String S;

	public BeginMetafile (int ec, int eid, int l, DataInputStream in)
		throws IOException
	{	super(ec,eid,l,in);
		S=makeString();
	}
	
	public String toString ()
	{	return "Begin Metafile "+S;
	}
}
