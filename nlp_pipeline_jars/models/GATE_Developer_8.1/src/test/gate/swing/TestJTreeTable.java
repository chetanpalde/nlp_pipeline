/*
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Valentin Tablan, 16 Jan 2008
 *
 *  $Id: TestJTreeTable.java 17656 2014-03-14 08:55:23Z markagreenwood $
 */
package gate.swing;

import gate.gui.MainFrame;

import java.io.File;
import java.util.Date;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * This class is used to demonstrate the functionality of {@link JTreeTable}.
 */
public class TestJTreeTable {
  
  private JFrame mainFrame;
  
  private JTreeTable treeTable;
  
  private TreeTableModel treeTableModel;
  
  private class FileTTModel extends AbstractTreeTableModel{
    
    private String[] columnNames = {"NAME", "SIZE", "DATE"};
    private static final int NAME_COLUMN = 0;
    private static final int SIZE_COLUMN = 1;
    private static final int DATE_COLUMN = 2;
    
    public FileTTModel(File root){
      super(root);
      this.root = root;
    }
    
    @Override
    public Object getChild(Object parent, int index) {
      if(parent instanceof File){
        File parentFile = (File)parent;
        if(parentFile.isDirectory()){
          File[] children = parentFile.listFiles();
          if(children != null && children.length > index){
            return children[index];
          }else{
            return null;
          }
        }else{
          throw new RuntimeException("Not a directory!");
        }
      }else{
        throw new RuntimeException("Not a file!");
      }
    }

    /* (non-Javadoc)
     * @see gate.swing.AbstractTreeTableModel#getChildCount(java.lang.Object)
     */
    @Override
    public int getChildCount(Object parent) {
      if(parent instanceof File){
        File parentFile = (File)parent;
        if(parentFile.isDirectory()){
          File[] children = parentFile.listFiles();
          return children == null ? 0 : children.length;
        }else{
          return 0;
        }
      }else{
        throw new RuntimeException("Not a file!");
      }
    }

    @Override
    public Class<?> getColumnClass(int column) {
      return String.class;
    }

    @Override
    public int getColumnCount() {
      return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
      return columnNames[column];
    }

    @Override
    public Object getValueAt(Object node, int column) {
      if(node instanceof File){
        File nodeFile = (File)node;
        switch(column) {
          case NAME_COLUMN: return nodeFile.getName();
          case SIZE_COLUMN: return nodeFile.length();
          case DATE_COLUMN: return new Date(nodeFile.lastModified()).toString();
          default: return "";
        }
      }else{
        throw new RuntimeException("Not a file!");
      }
    }

    @Override
    public boolean isCellEditable(Object node, int column) {
      return false;
    }
    
  }
  
  public TestJTreeTable(){
    initGui();
  }
  
  private void initGui(){
    //Unfortunately this will affect ALL trees
    //There seems to be no way of setting this up on a tree by tree basis.
    UIManager.put("Tree.collapsedIcon", MainFrame.getIcon("closed"));
    UIManager.put("Tree.expandedIcon", MainFrame.getIcon("expanded"));
    
    mainFrame = new JFrame(JTreeTable.class.getName());
    mainFrame.setSize(800, 600);
    mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    
    treeTableModel = new FileTTModel(new File("/"));
    treeTable = new JTreeTable(treeTableModel);
    
    DefaultTreeCellRenderer treeRenderer = new DefaultTreeCellRenderer();
    treeRenderer.setOpenIcon(MainFrame.getIcon("open-file"));
    treeRenderer.setClosedIcon(MainFrame.getIcon("open-file"));
    treeRenderer.setLeafIcon(MainFrame.getIcon("document"));
    
    treeTable.getTree().setCellRenderer(treeRenderer);
//    treeTable.getTree().setShowsRootHandles(false);
//    treeTable.getTree().setRootVisible(false);
    //according to the Swing tutorial at 
    //http://java.sun.com/docs/books/tutorial/uiswing/components/tree.html#display,
    //this will only work on the Java LookAndFeel. 
    treeTable.getTree().putClientProperty("JTree.lineStyle", "None");
    mainFrame.getContentPane().add(new JScrollPane(treeTable));
    
  }
  
  public static void main(String[] args) {
    TestJTreeTable tester = new TestJTreeTable();
    tester.mainFrame.setVisible(true);
  }

}
