package gate.gui;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.*;
import javax.swing.text.Document;

import gate.event.StatusListener;
import gate.swing.XJEditorPane;

/**
 * A frame used by Gate to display Help information.
 * It is a basic HTML browser.
 */
@SuppressWarnings("serial")
public class HelpFrame extends JFrame implements StatusListener {

  public HelpFrame(){
    super();
    initLocalData();
    initGuiComponents();
    initListeners();
  }

  protected void initLocalData(){
  }

  protected void initGuiComponents(){
    getContentPane().setLayout(new BorderLayout());
    textPane = new XJEditorPane();
    textPane.setEditable(false);
    getContentPane().add(new JScrollPane(textPane), BorderLayout.CENTER);

    toolBar = new JToolBar();
    toolBar.add(textPane.getBackAction());
    toolBar.add(textPane.getForwardAction());

    getContentPane().add(toolBar, BorderLayout.NORTH);

    Box southBox = Box.createHorizontalBox();
    southBox.add(new JLabel(" "));
    status = new JLabel();
    southBox.add(status);
    getContentPane().add(southBox, BorderLayout.SOUTH);

  }

  protected void initListeners(){
    textPane.addPropertyChangeListener(new PropertyChangeListener(){
      @Override
      public void propertyChange(PropertyChangeEvent e) {
        if(e.getPropertyName().equals("document")){
          String title = (String)textPane.getDocument().
                                          getProperty("title");
          setTitle((title == null) ?
                   "GATE help browser" :
                   title + " - GATE help browser");
        }
      }
    });

    textPane.addStatusListener(this);
  }

  public void setPage(URL newPage) throws IOException{
    textPane.setPage(newPage);
    String title = (String)textPane.getDocument().
                                    getProperty(Document.TitleProperty);
    setTitle((title == null) ?
             "GATE help browser" :
             title + " - GATE help browser");
  }

  XJEditorPane textPane;
  JToolBar toolBar;
  JLabel status;
  @Override
  public void statusChanged(String e) {
    status.setText(e);
  }
}