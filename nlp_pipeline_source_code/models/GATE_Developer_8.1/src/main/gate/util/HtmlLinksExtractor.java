/*
 *  HtmlLinkExtractor.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Cristian URSU,  16/Nov/2001
 *
 *  $Id: HtmlLinksExtractor.java 17640 2014-03-12 14:13:54Z markagreenwood $
 */

package gate.util;

import java.io.*;
import java.util.*;

import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.parser.ParserDelegator;

/**
 * This class extracts links from HTML files.
 * <B>It has been hacked</B> to build the contents of
 * <A HREF="http://gate.ac.uk/sitemap.html">http://gate.ac.uk/sitemap.html</A>;
 * you <B>probably don't want to use it</B> for anything else!
 * <P>
 * Implements the behaviour of the HTML reader.
 * Methods of an object of this class are called by the HTML parser when
 * events will appear.
 */
public class HtmlLinksExtractor extends ParserCallback {

  /** Debug flag */
  private static final boolean DEBUG = false;

  /** The tag currently being processed */
  private HTML.Tag currentTag = null;

  /** whether we've done a title before */
  static boolean firstTitle = true;

  /** will contain &lt;/UL&gt; after first title */
  static String endUl = "";

  /** Name of the file we're currently processing */
  static String currFile = "";

  /** Path to the file we're currently processing */
  static String currPath = "";

  /** This method is called when the HTML parser encounts the beginning
    * of a tag that means that the tag is paired by an end tag and it's
    * not an empty one.
    */
  @Override
  public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {

    currentTag = t;
    if (HTML.Tag.A == t){
      Out.pr("<LI><" + t);
      Enumeration<?> e = a.getAttributeNames();
      while(e.hasMoreElements()) {
        HTML.Attribute name = (HTML.Attribute) e.nextElement();
        String value = (String) a.getAttribute(name);

        if(name == HTML.Attribute.HREF) {
          if(
            value.startsWith("http:") || value.startsWith("HTTP:") ||
            value.startsWith("file:") || value.startsWith("FILE:") ||
            value.startsWith("mailto:") || value.startsWith("MAILTO:") ||
            value.startsWith("ftp:") || value.startsWith("FTP:")
          )
            Out.pr(" HREF=\"" + value + "\"");
          else { // if it is a relative path....
            Out.pr(" HREF=\"" + currPath + "/" + value + "\"");
          }
        }
      } // while

      Out.pr(">");
    }// End if

    if (HTML.Tag.TITLE == t){
      Out.pr(endUl + "<H3>");
      if(firstTitle) { firstTitle = false; endUl = "</UL>"; }
    }// End if

  }//handleStartTag

  private void printAttributes(MutableAttributeSet a){
    if (a == null) return;
    // Take all the attributes an put them into the feature map
    if (0 != a.getAttributeCount()){
      Enumeration<?> enumeration = a.getAttributeNames();
      while (enumeration.hasMoreElements()){
        Object attribute = enumeration.nextElement();
        Out.pr(" "+ attribute.toString() + "=\"" +
                                  a.getAttribute(attribute).toString()+"\"");
      }// End while
    }// End if
  }// printAttributes();

   /** This method is called when the HTML parser encounts the end of a tag
     * that means that the tag is paired by a beginning tag
     */
  @Override
  public void handleEndTag(HTML.Tag t, int pos){
    currentTag = null;

    if (HTML.Tag.A == t)
      Out.pr("</"+t+">\n");
    if (HTML.Tag.TITLE == t)
      Out.pr(
        "</H3></A>\n\n<P>Links in: <A HREF=\"" + currFile +
        "\">" + currFile + "</A>:\n<UL>\n"
      );

  }//handleEndTag

  /** This method is called when the HTML parser encounts an empty tag
    */
  @Override
  public void handleSimpleTag(HTML.Tag t, MutableAttributeSet a, int pos){
    if (HTML.Tag.A == t){
      Out.pr("<"+t);
      printAttributes(a);
      Out.pr("/>\n");
    }// End if

    if (HTML.Tag.TITLE == t){
      Out.pr("<"+t);
      printAttributes(a);
      Out.pr("/>\n");
    }// End if
  } // handleSimpleTag

  /** This method is called when the HTML parser encounts text (PCDATA)*/
  @Override
  public void handleText(char[] text, int pos){

    if(HTML.Tag.A == currentTag){
      //text of tag A
      String tagText = new String(text);
      Out.pr(tagText);
    }// End if

    if(HTML.Tag.TITLE == currentTag){
      //text of tag A
      String tagText = new String(text);
      Out.pr(tagText);
    }// End if

  }// end handleText();

  /**
    * This method is called when the HTML parser encounts an error
    * it depends on the programmer if he wants to deal with that error
    */
  @Override
  public void handleError(String errorMsg, int pos) {
    //Out.println ("ERROR CALLED : " + errorMsg);
  }

  /** This method is called once, when the HTML parser reaches the end
    * of its input streamin order to notify the parserCallback that there
    * is nothing more to parse.
    */
  @Override
  public void flush() throws BadLocationException{
  }// flush

  /** This method is called when the HTML parser encounts a comment
    */
  @Override
  public void handleComment(char[] text, int pos) {
  }

  /**
   * Given a certain folder it lists recursively all the files contained
   * in that folder. It returns a list of strings representing the file
   * names
   */
  private static List<String> listAllFiles(File aFile, Set<String> foldersToIgnore){
    List<String> sgmlFileNames = new ArrayList<String>();
    List<File> foldersToExplore = new ArrayList<File>();
    if (!aFile.isDirectory()){
      // add the file to the file list
      sgmlFileNames.add(aFile.getPath());
      return sgmlFileNames;
    }// End if
    listFilesRec(aFile,sgmlFileNames,foldersToExplore, foldersToIgnore);
    return sgmlFileNames;
  } // listAllFiles();

  /** Helper method for listAllFiles */
  private static void listFilesRec(File aFile,
                                  List<String> fileNames,
                                  List<File> foldersToExplore,
                                  Set<String> foldersToIgnore){

    String[] fileList = aFile.list();
    for (int i=0; i< fileList.length; i++){
      File tmpFile = new File(aFile.getPath()+"\\"+fileList[i]);
      if (tmpFile.isDirectory()){
        // If the file is not included
        if (!foldersToIgnore.contains(tmpFile.getName())) {  //fileList[i])) {
          if(DEBUG) {
            Err.prln("adding dir: " + tmpFile);
            Err.prln("  name: " + tmpFile.getName());
          }
          foldersToExplore.add(tmpFile);
        }
      }else{
        // only process .html files
        if(
          ( fileList[i].toLowerCase().endsWith(".html") ) ||
          ( fileList[i].toLowerCase().endsWith(".htm") )
        ) fileNames.add(tmpFile.getPath());
      }// End if
    }// End for

    while(!foldersToExplore.isEmpty()){
      File folder = foldersToExplore.get(0);
      foldersToExplore.remove(0);
      listFilesRec(folder,fileNames,foldersToExplore,foldersToIgnore);
    }//End while

  } // listFilesRec();

  /** Extract links from all .html files below a directory */
  public static void main(String[] args){
    HTMLEditorKit.Parser  parser = new ParserDelegator();
    // create a new Htmldocument handler
    HtmlLinksExtractor htmlDocHandler = new HtmlLinksExtractor();

    if (args.length == 0){
      Out.prln(
        "Eg: java HtmlLinksExtractor g:\\tmp\\relative javadoc img > results.txt"
      );
      return;
    }
    // Create a folder file File
    File htmlFolder = new File(args[0]);
    Set<String> foldersToIgnore = new HashSet<String>();
    for(int i = 1; i<args.length; i++)
      foldersToIgnore.add(args[i]);

    List<String> htmlFileNames = listAllFiles(htmlFolder,foldersToIgnore);
    //Collections.sort(htmlFileNames);
    while (!htmlFileNames.isEmpty()){
      try{
        String htmlFileName = htmlFileNames.get(0);
        currFile = htmlFileName;
        currPath = new File(currFile).getParent().toString();
        htmlFileNames.remove(0);

        Out.prln("\n\n<A HREF=\"file://" + htmlFileName + "\">");
        Reader reader = new FileReader(htmlFileName);
        // parse the HTML document
        parser.parse(reader, htmlDocHandler, true);
      } catch (IOException e){
        e.printStackTrace(System.out);
      }// End try
    }// End while
    System.err.println("done.");
  }// main

}//End class HtmlLinksExtractor



