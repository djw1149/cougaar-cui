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

/*
*
* Date		Author
* 12/4/96	Larry Barowski
*
*/
// The following comment is to comply with GPLv2:
//    This source file was modified during February 2001.


   package org.cougaar.lib.uiframework.ui.ohv.VGJ.algorithm.cgd;


   import org.cougaar.lib.uiframework.ui.ohv.VGJ.graph.Set;
   import EDU.auburn.VGJ.util.DDimension;
   import EDU.auburn.VGJ.util.DPoint;
   import org.cougaar.lib.uiframework.ui.ohv.VGJ.graph.Graph;
   import java.lang.String;



/**
 * A "Clan" tree class for CGD.
 * </p>Here is the <a href="../algorithm/cgd/ClanTree.java">source</a>.
 */
   public class ClanTree
   {
      public ClanTree	parent;
      public ClanTree	firstChild;
      public ClanTree   nextSibling;
      public Clan	clan;
   
      public double minx, maxx, centerx;
   
      public ClanTree leftSibling;
      public DDimension size;
      public DPoint position;
      public double extraheight;
      public boolean dummy;
   
      public int heightInTree;
   
   
      public ClanTree()
      {
         parent = firstChild = nextSibling = null;
         clan = null;
         size = new DDimension(0, 0);
         position = new DPoint(0, 0);
         extraheight = 0.0;
      }
   
   
   
      public String toString()
      {
         return toString_(0, null);
      }
   
   
   
   // Translate indexes to ids for graph.
      public String toString(Graph graph)
      {
         return toString_(0, graph);
      }
   
   
   
   
      private String toString_(int indent, Graph graph)
      {
         String string = new String();
      
         int i;
         for(i = 0; i < indent; i++)
            string += "   ";
      
         if(graph != null)
            string += clan.toString(graph);
         else
            string += clan.toString();
         if(firstChild != null)
         {
            string += "\n";
         
            for(i = 0; i < indent + 1; i++)
               string += "   ";
            string += "(\n";
         
            ClanTree tmpclan;
            for(tmpclan = firstChild; tmpclan != null;
            tmpclan = tmpclan.nextSibling)
            {
               string += tmpclan.toString_(indent + 1, graph);
               string += "\n";
            }
            for(i = 0; i < indent + 1; i++)
               string += "   ";
            string += ")";
         }
         return string;
      }
   
   
   
      public void fixLinear(Set node_subset, Set child_relation[],
      Set parent_relation[])
      {
         ClanTree child;
      
         for(child = firstChild; child != null; child = child.nextSibling)
            child.fixLinear(node_subset, child_relation, parent_relation);
      
         if(clan.clanType != Clan.PRIMITIVE)
            return;
      
         boolean is_linear = true;
      
         ClanTree last = firstChild;
         ClanTree cur;
      
         for(cur = last.nextSibling; is_linear && cur != null;
         cur = last.nextSibling)
         {
         	// If the current source is not a child
         	// of the last sink, or the last sink
         	// is not a parent of the current source,
         	// then this is not a linear clan.
         
            Set last_children = new Set();
            last_children.union(child_relation[last.clan.sinks.first()]);
            last_children.intersect(node_subset);
            Set cur_parents = new Set();
            cur_parents.union(parent_relation[cur.clan.sources.first()]);
            cur_parents.intersect(node_subset);
         
            if(!(cur.clan.nodes.isSubset(last_children)) ||
            !(last.clan.nodes.isSubset(cur_parents)))
            {
               is_linear = false;
               break;
            }
            last = cur;
         }
         if(is_linear)
            clan.clanType = Clan.LINEAR;
      }
   
   
   
   
      public int numberOfChildren()
      {
         int num = 0;
         ClanTree tmpnode;
         for(tmpnode = firstChild; tmpnode != null;
         tmpnode = tmpnode.nextSibling)
            num++;
         return num;
      }
   
   
   }
