/*
 * <copyright>
 * Copyright 1997-2001 Defense Advanced Research Projects
 * Agency (DARPA) and ALPINE (a BBN Technologies (BBN) and
 * Raytheon Systems Company (RSC) Consortium).
 * This software to be used only in accordance with the
 * COUGAAR licence agreement.
 * </copyright>
 */
/*
 * File: ParseError.java
 *
 * 5/3/96   Larry Barowski
 *
*/
/*
  The following comment is to comply with GPLv2:
     This source file was modified during February 2001.
*/


package org.cougaar.lib.uiframework.ui.ohv.VGJ.graph;


import java.lang.Exception;




/**
 *	A parse error Exception.
 * </p>Here is the <a href="../graph/ParseError.java">source</a>.
 *
 *@author	Larry Barowski
**/
public class ParseError extends Exception
{
	public ParseError(String string)
		{ super(string); }
}
