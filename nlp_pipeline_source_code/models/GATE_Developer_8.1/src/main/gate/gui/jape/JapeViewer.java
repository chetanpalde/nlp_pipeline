package gate.gui.jape;

import gate.LanguageAnalyser;
import gate.Resource;
import gate.creole.ANNIEConstants;
import gate.creole.AbstractProcessingResource;
import gate.creole.AbstractVisualResource;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.GuiType;
import gate.event.ProgressListener;
import gate.jape.parser.ParseCpslConstants;
import gate.jape.parser.ParseCpslTokenManager;
import gate.jape.parser.SimpleCharStream;
import gate.jape.parser.Token;
import gate.util.BomStrippingInputStreamReader;
import gate.util.GateRuntimeException;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

/**
 * A JAPE viewer that allows access to all phases of the grammar and
 * provides syntax highlighting. Future versions may allow editing and
 * reload of JAPE files.
 *
 * @author Mark A. Greenwood
 */
@CreoleResource(name="Jape Viewer", comment="A JAPE grammar file viewer", helpURL="http://gate.ac.uk/userguide/chap:jape", guiType=GuiType.LARGE, mainViewer=true, resourceDisplayed="gate.creole.Transducer")
public class JapeViewer extends AbstractVisualResource implements
                                                      ANNIEConstants,
                                                      ProgressListener {

  private static final long serialVersionUID = -6026605466406110590L;

  /**
   * The text area where the JAPE source will be displayed
   */
  private JTextPane textArea;

  /**
   * The tree in which the phases of the grammar will be shown
   */
  private JTree treePhases;

  private JScrollPane treeScroll;

  /**
   * A flag so we can know if we are currently reading a highlighting a
   * JAPE source file
   */
  private boolean updating = false;

  /**
   * The JAPE transducer for which we need to show the JAPE source
   */
  private LanguageAnalyser transducer;

  /**
   * A map that associates the syntactic elements of JAPE files with a
   * colour for performing syntax highlighting
   */
  private Map<Integer, Style> colorMap = new HashMap<Integer, Style>();

  /**
   * The default style used by the text area. This is used so that we
   * can ensure that normal text is displayed normally. This fixes a
   * problem where sometime the highlighting goes screwy and shows
   * everything as a comment.
   */
  private Style defaultStyle;

  @Override
  public Resource init() {
    initGuiComponents();
    return this;
  }

  private void initGuiComponents() {
    setLayout(new BorderLayout());
    textArea = new JTextPane();
    textArea.setEditable(false);
    JScrollPane textScroll = new JScrollPane(textArea,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    add(textScroll, BorderLayout.CENTER);

    treePhases = new JTree();
    treeScroll = new JScrollPane(treePhases,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    add(treeScroll, BorderLayout.WEST);
    treePhases.getSelectionModel().setSelectionMode(
            TreeSelectionModel.SINGLE_TREE_SELECTION);
    treePhases.addTreeSelectionListener(new TreeSelectionListener() {
      @Override
      public void valueChanged(TreeSelectionEvent e) {
        if(updating) return;
        if(e.getPath().getLastPathComponent() == null) return;

        try {
          readJAPEFileContents(new URL((URL)transducer.getParameterValue("grammarURL"), e.getPath()
                  .getLastPathComponent()
                  + ".jape"));
        }
        catch(MalformedURLException mue) {
          mue.printStackTrace();
        } catch(ResourceInstantiationException rie) {
          rie.printStackTrace();
        }
      }
    });

    // if we want to set the jape to be monospaced (like most code
    // editors) then
    // do this...
    /*
     * MutableAttributeSet attrs = textArea.getInputAttributes();
     * StyleConstants.setFontFamily(attrs, "monospaced"); StyledDocument
     * doc = textArea.getStyledDocument(); doc.setCharacterAttributes(0,
     * doc.getLength() + 1, attrs, false);
     */
    defaultStyle = textArea.addStyle("default", null);

    Style style = textArea.addStyle("brackets", null);
    StyleConstants.setForeground(style, Color.red);
    colorMap.put(ParseCpslConstants.leftBrace, style);
    colorMap.put(ParseCpslConstants.rightBrace, style);
    colorMap.put(ParseCpslConstants.leftBracket, style);
    colorMap.put(ParseCpslConstants.rightBracket, style);
    colorMap.put(ParseCpslConstants.leftSquare, style);
    colorMap.put(ParseCpslConstants.rightSquare, style);

    style = textArea.addStyle("keywords", null);
    StyleConstants.setForeground(style, Color.blue);
    colorMap.put(ParseCpslConstants.rule, style);
    colorMap.put(ParseCpslConstants.priority, style);
    colorMap.put(ParseCpslConstants.macro, style);
    colorMap.put(ParseCpslConstants.bool, style);
    colorMap.put(ParseCpslConstants.phase, style);
    colorMap.put(ParseCpslConstants.input, style);
    colorMap.put(ParseCpslConstants.option, style);
    colorMap.put(ParseCpslConstants.multiphase, style);
    colorMap.put(ParseCpslConstants.phases, style);

    style = textArea.addStyle("strings", null);
    StyleConstants.setForeground(style, new Color(0, 128, 128));
    colorMap.put(ParseCpslConstants.string, style);

    style = textArea.addStyle("comments", null);
    StyleConstants.setForeground(style, new Color(0, 128, 0));
    colorMap.put(ParseCpslConstants.singleLineCStyleComment, style);
    colorMap.put(ParseCpslConstants.singleLineCpslStyleComment, style);
    colorMap.put(ParseCpslConstants.commentStart, style);
    colorMap.put(ParseCpslConstants.commentChars, style);
    colorMap.put(ParseCpslConstants.commentEnd, style);
    colorMap.put(ParseCpslConstants.phasesSingleLineCStyleComment, style);
    colorMap.put(ParseCpslConstants.phasesSingleLineCpslStyleComment, style);
    colorMap.put(ParseCpslConstants.phasesCommentStart, style);
    colorMap.put(ParseCpslConstants.phasesCommentChars, style);
    colorMap.put(ParseCpslConstants.phasesCommentEnd, style);
  }

  @Override
  public void setTarget(final Object target) {
    if(target == null) {
     throw new NullPointerException("JAPE viewer received a null target");
    }
    // check that the target is one we can work with - it needs to be a
    // LanguageAnalyser that has grammarURL and encoding parameters.
    boolean targetOK = true;
    if(target instanceof LanguageAnalyser) {
      try {
        ((LanguageAnalyser)target).getParameterValue("grammarURL");
        ((LanguageAnalyser)target).getParameterValue("encoding");
      } catch(ResourceInstantiationException rie) {
        targetOK = false;
      }
    } else {
      targetOK = false;
    }
     
    if(!targetOK) {
      throw new IllegalArgumentException(
              "The GATE jape viewer can only be used with a GATE jape transducer!\n"
                      + target.getClass().toString()
                      + " is not a GATE Jape Transducer!");
    }
    
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {

        if(transducer != null && transducer instanceof AbstractProcessingResource) {
          ((AbstractProcessingResource)transducer).removeProgressListener(JapeViewer.this);
        }
    
        transducer = (LanguageAnalyser)target;
        URL japeFileURL = null;
        
        try {
          japeFileURL = (URL)transducer.getParameterValue("grammarURL");
        }
        catch (ResourceInstantiationException rie) {
          //ignore this for now and let the null catch take over
          rie.printStackTrace();
        }


        if(japeFileURL == null) {
          textArea.setText("The source for this JAPE grammar is not available!");
          remove(treeScroll);
          return;
        }

        String japePhaseName = japeFileURL.getFile();
        japePhaseName = japePhaseName.substring(japePhaseName.lastIndexOf("/") + 1,
                japePhaseName.length() - 5);
        treePhases.setModel(new DefaultTreeModel(new DefaultMutableTreeNode(
                japePhaseName)));
        treePhases.setSelectionRow(0);

        readJAPEFileContents(japeFileURL);
        if(transducer instanceof AbstractProcessingResource) {
          ((AbstractProcessingResource)transducer).addProgressListener(JapeViewer.this);
        }
      }
      
    });
    
  }

  private void readJAPEFileContents(URL url) {
    if(treePhases.getLastSelectedPathComponent() == null) return;
    updating = true;

    try {
      Reader japeReader = null;
      String encoding = (String)transducer.getParameterValue("encoding");
      if(encoding == null) {
        japeReader = new BomStrippingInputStreamReader(url.openStream());
      }
      else {
        japeReader = new BomStrippingInputStreamReader(url.openStream(), encoding);
      }
      BufferedReader br = new BufferedReader(japeReader);
      String content = br.readLine();
      StringBuilder japeFileContents = new StringBuilder();
      List<Integer> lineOffsets = new ArrayList<Integer>();

      while(content != null) {
        lineOffsets.add(japeFileContents.length());

        // replace tabs with spaces otherwise the highlighting fails
        // TODO work out why this is needed and fix it properly
        japeFileContents.append(content.replaceAll("\t", "   ")).append("\n");
        content = br.readLine();
      }

      textArea.setText(japeFileContents.toString());
      textArea.updateUI();
      br.close();

      ParseCpslTokenManager tokenManager = new ParseCpslTokenManager(
              new SimpleCharStream(
                      new StringReader(japeFileContents.toString())));

      StyledDocument doc = textArea.getStyledDocument();

      doc.setCharacterAttributes(0, japeFileContents.length(), defaultStyle,
              true);

      ((DefaultMutableTreeNode)treePhases.getSelectionPath()
              .getLastPathComponent()).removeAllChildren();

      Token t;
      while((t = tokenManager.getNextToken()).kind != 0) {

        Token special = t.specialToken;
        while(special != null) {
          Style style = colorMap.get(special.kind);
          if(style != null) {
            int start = lineOffsets.get(special.beginLine - 1)
                    + special.beginColumn - 1;
            int end = lineOffsets.get(special.endLine - 1) + special.endColumn
                    - 1;
            doc.setCharacterAttributes(start, end - start + 1, style, true);
          }

          special = special.specialToken;
        }

        Style style = colorMap.get(t.kind);

        if(style != null) {
          int start = lineOffsets.get(t.beginLine - 1) + t.beginColumn - 1;
          int end = lineOffsets.get(t.endLine - 1) + t.endColumn - 1;
          doc.setCharacterAttributes(start, end - start + 1, style, true);
        }

        if(t.kind == ParseCpslConstants.path) {
          ((DefaultMutableTreeNode)treePhases.getSelectionPath()
                  .getLastPathComponent()).add(new DefaultMutableTreeNode(t
                  .toString()));
        }
      }
    }
    catch(IOException ioe) {
      throw new GateRuntimeException(ioe);
    } catch(ResourceInstantiationException rie) {
      throw new GateRuntimeException(rie);
    }

    if(treePhases.getSelectionRows() != null
            && treePhases.getSelectionRows().length > 0)
      treePhases.expandRow(treePhases.getSelectionRows()[0]);

    updating = false;
  }

  @Override
  public void processFinished() {
    setTarget(transducer);
  }

  @Override
  public void progressChanged(int progress) {

  }
}
