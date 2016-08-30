/*
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Valentin Tablan 20 Feb 2003
 *
 *  $Id: MenuLayoutTest.java 17656 2014-03-14 08:55:23Z markagreenwood $
 */

package gate.swing;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

@SuppressWarnings("serial")
public class MenuLayoutTest extends JFrame {

  public MenuLayoutTest() {
    super("Displaying Long Menus");
    JMenuBar menuBar = new JMenuBar();
    this.setJMenuBar(menuBar);
    JMenu bigMenu = new JMenu("bigMenu");
    menuBar.add(bigMenu);

    // specify a layout manager for the menu
    MenuLayout vflayout = new MenuLayout();
    bigMenu.getPopupMenu().setLayout(vflayout);
    for (int i = 1; i < 200; i++) {
      JMenuItem bigMenuItem = new JMenuItem("bigMenu " + i);
      //uncomment below for crazy sizes
//      bigMenuItem.setFont(bigMenuItem.getFont().deriveFont(
//          12 + (float)Math.random() * 10));
      if(i > 100){
        bigMenuItem.setFont(bigMenuItem.getFont().deriveFont((float)20));
      }

      bigMenu.add(bigMenuItem);
    }
  }

  public static void main(String[] args) {
    MenuLayoutTest frame = new MenuLayoutTest();
    frame.setSize(250, 200);
    frame.setLocation(200, 300);
    frame.setVisible(true);
  }
}