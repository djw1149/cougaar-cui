package org.cougaar.lib.uiframework.ui.ohv.util;

import org.apache.xerces.parsers.DOMParser;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import org.xml.sax.InputSource;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.net.URL;
import java.io.InputStream;
import java.io.IOException;



/**
  Provides DOM utility methods.
**/
public class DomUtil {

   static boolean debug=
     //true;
      false;


    public static Document getDocument(String urlName ){
        try{
                    InputStream fr = (new URL(urlName)).openStream();
                    InputSource is = new InputSource(fr);

                    DOMParser domp = new DOMParser();
                    domp.setErrorHandler(new ErrorHandler(){
                          public void error(SAXParseException exception) {
                             System.err.println("[ErrorHandler.error]: " + exception);
                           }
                           public void fatalError(SAXParseException exception) {
                                 System.err.println("[ErrorHandler.fatalError]: " + exception);
                           }
                           public void warning(SAXParseException exception) {
                                 System.err.println("[ErrorHandler.warning]: " + exception);
                            }
                        }
                    );

                    domp.parse(is);
                    Document doc = domp.getDocument();
                   return doc;
        } catch (Exception ex ) {
           ex.printStackTrace();
        }
        return null;
  }

    int indentlevel=0;
    public static String printValues(Node cel, String tag) {
      return printValues(cel, tag, 0);
    }
    public static String printValues(Node cel, String tag, int indentlevel) {
      String retstr=null;
      NodeList children;
      Node child;
      try {
      indentlevel++;

      System.out.println("into getValues with indentlevel: "+indentlevel);

      if (cel.hasChildNodes()) {
        children = cel.getChildNodes();
        if ( children != null ) {
          int len = children.getLength();
          System.out.println("children.length: "+len);
          for ( int idx = 0; idx < len; idx++ ) {
            child=children.item(idx);
            if (child.getNodeType()==Node.ELEMENT_NODE
              && ((Element)child).getTagName().equals(tag)
            ){
              System.out.println("TAG-MATCH");
            }
            child.normalize();
            String nodeval=child.getNodeValue();
            System.out.println("child: nodeType="+child.getNodeType()
              +" nodeval=["+nodeval+"]");
            if (nodeval!=null&&nodeval.equals(tag)) {
              System.out.println("TAG-MATCH");
            }
            printValues(child, tag, indentlevel);
            //retstr=child.getNodeValue();
            //break;
          //}
        }
        }
      }



      } finally {
            System.out.println("out of getValues with indentlevel: "+indentlevel);
            indentlevel--;

      }
               return"";
  }
  public static String getTextNodeValue(Element cel) {
    String retstr=null;
      cel.normalize();
      NodeList children = cel.getChildNodes();
      Node child;
      int len=children.getLength();
      if (debug) {
        System.out.println("tnv:"+len);
      }

      if ( children != null ) {
        for ( int idx = 0; idx < len; idx++ ) {
          child=children.item(idx);
          if (child.getNodeType()==Node.TEXT_NODE){
              retstr=child.getNodeValue().trim();
              break;
          }
        }
      }
    return retstr;
  }

  public static String getSingleElementValue(Element cel, String tag) {
      String retstr=null;
      NodeList children = cel.getChildNodes();
      Node child;
      if (debug) {
       System.out.println(children.getLength());
      }

       int len=children.getLength();
     if ( children != null ) {
        for ( int idx = 0; idx < len; idx++ ) {
          child=children.item(idx);
          if (child.getNodeType()==Node.ELEMENT_NODE
              && ((Element)child).getTagName().equals(tag)
              ){
              retstr=getTextNodeValue((Element)child);
              if (debug) {
                System.out.println("child: nodeType="+child.getNodeType()
                  +" nodeval=["+child.getNodeValue()+"]");
                System.out.println("retstr="+retstr);
              }
            break;
          }
        }
      }
            /*
      NodeList nodes=cel.getElementsByTagName(tag);
      System.out.println(nodes.getLength());
      if (nodes.getLength()>0) {
        String retstr2=((Element)nodes.item(0)).getNodeValue();
        System.out.println("retstr2="+retstr2);
      }     */
      return retstr;
    }



//    public static void main(String[] args) {
//      test(args);
//    }

//    public static void test(String[] args) {
//      String xmlFile;
//      OrgHierRelationship ohr;
//      xmlFile = "file:/c:/alp_workspace/xerces-1_2_0/data/personal.xml";
//      xmlFile = "file:/c:/dev/ui/kr/sfp/defTest.xml";
//      xmlFile = "file:/c:/JBuilder3/myprojects/org.cougaar.lib.uiframework.ui.ohv/data/deftestb.xml";
//      xmlFile = "file:/c:/JBuilder3/myprojects/org.cougaar.lib.uiframework.ui.ohv/data/defTestTime.xml";
//     xmlFile = "file:/c:/JBuilder3/myprojects/org.cougaar.lib.uiframework.ui.ohv/data/dbjconfadm.xml";
//     OrgHierParser orgHierParser = new OrgHierParser(xmlFile);

//     Vector v = orgHierParser.parse();

//     System.out.println("Finished parsing.");

//     System.out.println();
//     System.out.println("OrgHierModel output: ");
//     OrgHierModel ohm=new OrgHierModel(v);
//     System.out.println("Start time: "+ohm.getStartTime());
//     System.out.println("Transition times: "+ohm.getTransitionTimes());
//     System.out.println("End time: "+ohm.getEndTime());
//     System.out.println("Finished OrgHierModel output.");

//     System.out.println();
//     System.out.println("OrgHierModelViewer output: ");
//     OrgHierModelViewer ohmv=new TextOrgHierModelViewer(ohm, System.out);
//     ohmv.show();
//     System.out.println("Finished OrgHierModelViewer output.");

//     OrgHierTextTree ohtt=new OrgHierTextTree(ohm);
//     ohtt.show();
//   }




}
