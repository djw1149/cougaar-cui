/* 
 * <copyright> 
 *  Copyright 1997-2001 Clark Software Engineering (CSE)
 *  under sponsorship of the Defense Advanced Research Projects 
 *  Agency (DARPA). 
 *  
 *  This program is free software; you can redistribute it and/or modify 
 *  it under the terms of the Cougaar Open Source License as published by 
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).  
 *  
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS  
 *  PROVIDED "AS IS" WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR  
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF  
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT  
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT  
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL  
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,  
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR  
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.  
 *  
 * </copyright> 
 */

package org.cougaar.lib.uiframework.ui.components.desktop;

import java.awt.*;
import java.awt.event.*;

import java.io.Serializable;

import javax.swing.*;

import java.util.Vector;

import org.cougaar.lib.uiframework.ui.components.desktop.CougaarDesktopUI;
import org.cougaar.lib.uiframework.ui.components.desktop.CDesktopFrame;

/***********************************************************************************************************************
<b>Description</b>: This class is a test Cougaar Desktop component for menu proxy capabilities in the Cougaar Desktop
                    application.  This component provides diferent types of menu items (check box, radio button, etc.)
                    that can be enabled, disabled and changed to show how the Cougaar Desktop application can switch
                    (using the option under the Window menu) between displaying the menu bar of a desktop application
                    in the application frame's window or on the Cougaar Desktop application's menu bar.

@author Eric B. Martin, &copy;2001 Clark Software Engineering, Ltd. & Defense Advanced Research Projects Agency (DARPA)
@version 1.0
***********************************************************************************************************************/
public class MenuTestGUI extends org.cougaar.lib.uiframework.ui.components.desktop.ComponentFactory implements org.cougaar.lib.uiframework.ui.components.desktop.CougaarDesktopUI
{
  private JMenuBar menuBar = null;

  private JMenu addMenu = null;
  private JMenu fileMenu = null;

  private JMenu sameOptionsMenu = null;

  private JButton label = null;

  private int count = 0;

  public void install(CDesktopFrame f)
  {
	  JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(7, 1));

    JButton button = null;





    label = new JButton("Click to Enable/Disable Add Menu: 0");
    label.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          addMenu.setEnabled(!addMenu.isEnabled());
        }
      });
		panel.add(label);




    
    button = new JButton("Click to Enable/Disable File menu");
    button.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          fileMenu.setEnabled(!fileMenu.isEnabled());
        }
      });
		panel.add(button);




    button = new JButton("Click to Enable/Disable File: Same Options menu");
    button.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          sameOptionsMenu.setEnabled(!sameOptionsMenu.isEnabled());
        }
      });
		panel.add(button);



    button = new JButton("Click to Enable/Disable File: Multiply By 10 menu item");
    button.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          fileMenu.getItem(0).setEnabled(!fileMenu.getItem(0).isEnabled());
        }
      });
		panel.add(button);





    button = new JButton("Click to Add a menu");
    button.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          JMenu menu = null;
          JMenuItem menuItem = null;
          menu = new JMenu("New Menu " + count);
      		menuItem = new JMenuItem("some menu item");
      		menu.add(menuItem);
          menuBar.add(menu);
          
          menuBar.revalidate();
          menuBar.repaint();
        }
      });
		panel.add(button);





    button = new JButton("Click to Add a menu item to the add menu");
    button.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
      		JMenuItem menuItem = new JMenuItem("some menu item " + count);
      		addMenu.add(menuItem);
        }
      });
		panel.add(button);





    button = new JButton("Click to Add a menu item to the file menu");
    button.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
      		JMenuItem menuItem = new JMenuItem("some menu item " + count);
      		fileMenu.add(menuItem);
        }
      });
		panel.add(button);





    menuBar = new JMenuBar();
    setupMenus();

    f.setJMenuBar(menuBar);
    
    f.getContentPane().add(panel);
  }

  private void setupMenus()
  {
    JMenu menu = null;
    JMenuItem menuItem = null;







    menu = new JMenu("Add Menu");

		menuItem = new JMenuItem("Add 1");
		menuItem.setMnemonic(KeyEvent.VK_1);
		menuItem.addActionListener(new ActionListener()
  		{
  		  public void actionPerformed(ActionEvent e)
  		  {
  		    label.setText("Click to Enable/Disable Add Menu: " + ++count);
  		  }
  		});
		menu.add(menuItem);

		menuItem = new JMenuItem("Add 2");
		menuItem.setMnemonic(KeyEvent.VK_2);
		menuItem.addActionListener(new ActionListener()
  		{
  		  public void actionPerformed(ActionEvent e)
  		  {
  		    count += 2;
  		    label.setText("Click to Enable/Disable Add Menu: " + count);
  		  }
  		});
		menu.add(menuItem);

    menuBar.add(menu);

    addMenu = menu;






    menu = new JMenu("File");

		menuItem = new JMenuItem("Multiply by 10");
		menuItem.setMnemonic(KeyEvent.VK_M);
		menuItem.addActionListener(new ActionListener()
  		{
  		  public void actionPerformed(ActionEvent e)
  		  {
  		    count *= 10;
  		    label.setText("Click to Enable/Disable Add Menu: " + count);
  		  }
  		});
		menu.add(menuItem);

		menuItem = new JMenuItem("Divide by 10");
		menuItem.setMnemonic(KeyEvent.VK_U);
		menuItem.addActionListener(new ActionListener()
  		{
  		  public void actionPerformed(ActionEvent e)
  		  {
  		    count /= 10;
  		    label.setText("Click to Enable/Disable Add Menu: " + count);
  		  }
  		});
		menu.add(menuItem);

    sameOptionsMenu = new JMenu("Same Options");

		menuItem = new JMenuItem("Multiply by 10");
		menuItem.setMnemonic(KeyEvent.VK_M);
		menuItem.addActionListener(new ActionListener()
  		{
  		  public void actionPerformed(ActionEvent e)
  		  {
  		    count *= 10;
  		    label.setText("Click to Enable/Disable Add Menu: " + count);
  		  }
  		});
		sameOptionsMenu.add(menuItem);

		menuItem = new JMenuItem("Divide by 10");
		menuItem.setMnemonic(KeyEvent.VK_U);
		menuItem.addActionListener(new ActionListener()
  		{
  		  public void actionPerformed(ActionEvent e)
  		  {
  		    count /= 10;
  		    label.setText("Click to Enable/Disable Add Menu: " + count);
  		  }
  		});
		sameOptionsMenu.add(menuItem);

		menu.add(sameOptionsMenu);






    menuBar.add(menu);

    fileMenu = menu;
  }

	public String getToolDisplayName()
	{
	  return("MenuTestGUI");
	}

	public CougaarDesktopUI create()
	{
	  return(this);
	}

  public boolean supportsPlaf()
  {
    return(true);
  }

  public void install(JFrame f)
  {
    throw(new RuntimeException("install(JFrame f) not supported"));
  }

  public void install(JInternalFrame f)
  {
    throw(new RuntimeException("install(JInternalFrame f) not supported"));
  }

  public boolean isPersistable()
  {
    return(false);
  }

  public Serializable getPersistedData()
  {
    return(null);
  }

  public void setPersistedData(Serializable data)
  {
  }

  public String getTitle()
  {
    return("MenuTestGUI");
  }

  public Dimension getPreferredSize()
  {
    return(new Dimension(400, 200));
  }

  public boolean isResizable()
  {
    return(true);
  }
}
