/*
 *  SourceInfo.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Mark A. Greenwood, 08/12/2010
 *
 */

package gate.jape;

import gate.util.Strings;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple class to store and use the mapping between Java and Jape
 * source code for error reporting.
 */
public class SourceInfo {

  private List<BlockInfo> blocks = new ArrayList<BlockInfo>();

  private String className = null;

  private String phaseName = null;

  private String sectionName = null;

  public SourceInfo(String className, String phaseName, String sectionName) {
    this.className = className;
    this.phaseName = phaseName;
    this.sectionName = sectionName;
  }

  public String addBlock(String previousCode, String codeBlock) {
    if(!codeBlock.startsWith("  // JAPE Source:")) return codeBlock;

    String info = codeBlock.substring(18, codeBlock.indexOf("\n")).trim();
    String code = codeBlock.substring(codeBlock.indexOf("\n") + 1);

    String japeURL = info.substring(0, info.lastIndexOf(":"));
    int lineNumber = Integer
            .parseInt(info.substring(info.lastIndexOf(":") + 1));

    int startLine = previousCode.split("\n").length + 1;
    int endLine = startLine + code.split("\n").length;

    int startOffset = previousCode.length();
    int endOffset = previousCode.length() + code.length();

    blocks.add(new BlockInfo(japeURL, lineNumber, startLine, endLine,
            startOffset, endOffset));

    return code;
  }

  public String getSource(String source, int javaLineNumber) {
    for(BlockInfo info : blocks) {   
      if(info.contains(javaLineNumber)) {
        return info.getSource(source, info.getJapeLineNumber(javaLineNumber));
      }
    }

    return "";
  }

  public StackTraceElement getStackTraceElement(int javaLineNumber) {
    for(BlockInfo info : blocks) {
      StackTraceElement japeSTE = info.getStackTraceElement(javaLineNumber);

      if(japeSTE != null) return japeSTE;
    }

    return null;
  }

  /**
   * Enhances a Throwable by replacing mentions of Java code inside a
   * Jape RhsAction with a reference to the original Jape source where
   * available.
   * 
   * @param t the Throwable to enhance with Jape source information
   */
  public void enhanceTheThrowable(Throwable t) {
    if(t.getCause() != null) {
      enhanceTheThrowable(t.getCause());
    }
    
    List<StackTraceElement> stack = new ArrayList<StackTraceElement>();

    for(StackTraceElement ste : t.getStackTrace()) {
      if(ste.getClassName().equals(className)) {

        StackTraceElement japeSTE = null;

        if(ste.getLineNumber() >= 0) {
          for(BlockInfo info : blocks) {
            japeSTE = info.getStackTraceElement(ste.getLineNumber());

            if(japeSTE != null) break;
          }
        }
        else {
          // this will happen if we are running from a
          // serialised jape grammar as we don't keep the
          // source info
          japeSTE = new StackTraceElement(phaseName, sectionName, null, -1);
        }

        stack.add(japeSTE != null ? japeSTE : ste);
      }
      else {
        stack.add(ste);
      }
    }

    t.setStackTrace(stack.toArray(new StackTraceElement[stack.size()]));
  }

  private class BlockInfo {
    String japeURL;

    int japeLine;

    int startLine, endLine;

    int startOffset, endOffset;

    BlockInfo(String japeURL, int japeLine, int startLine, int endLine,
            int startOffset, int endOffset) {

      this.japeURL = japeURL;
      this.japeLine = japeLine;
      this.startLine = startLine;
      this.endLine = endLine;
      this.startOffset = startOffset;
      this.endOffset = endOffset;
    }

    public boolean contains(int lineNumber) {
      return (startLine <= lineNumber && lineNumber <= endLine);
    }

    @SuppressWarnings("unused")
    public String getNumberedSource(String source) {
      return Strings.addLineNumbers(getSource(source), japeLine);
    }

    public String getSource(String source, int line) {
      String[] lines = getSource(source).split("\n");

      return lines[line - japeLine];
    }

    public int getJapeLineNumber(int javaLineNumber) {
      if(!contains(javaLineNumber)) return -1;

      return japeLine + (javaLineNumber - startLine);
    }

    public StackTraceElement getStackTraceElement(int javaLineNumber) {
      int japeLineNumber = getJapeLineNumber(javaLineNumber);

      if(japeLineNumber == -1) return null;

      return new StackTraceElement(phaseName, sectionName, japeURL,
              japeLineNumber);
    }

    public String getSource(String source) {
      return source.substring(startOffset, endOffset);
    }
  }
}
