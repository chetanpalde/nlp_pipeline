/*
 *  DumpingPR.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Kalina Bontcheva, 19/10/2001
 *
 *  $Id: DumpingPR.java 17589 2014-03-08 08:13:46Z markagreenwood $
 */

package gate.creole.dumpingPR;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Gate;
import gate.ProcessingResource;
import gate.Resource;
import gate.corpora.DocumentImpl;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.util.Files;
import gate.util.GateRuntimeException;
import gate.util.InvalidOffsetException;
import gate.util.Out;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class implements a DumpingPR which exports a given set of annotation
 * types + the original markup, back into the document's native format.
 * The export might also include the GATE features of those annotations or
 * not (the default). One can also control whether the export files have a
 * new suffix (useSuffixForDumpFiles) and what this suffix is
 * (suffixForDumpFiles). By default, a suffix is used and it is .gate.
 */
public class DumpingPR extends AbstractLanguageAnalyser
  implements ProcessingResource {

  private static final long serialVersionUID = -5279930527247392922L;

  public static final String
    DPR_DOCUMENT_PARAMETER_NAME = "document";

  public static final String
    DPR_ANN_SET_PARAMETER_NAME = "annotationSetName";

  public static final String
    DPR_ANN_TYPES_PARAMETER_NAME = "annotationTypes";

  public static final String
    DPR_DUMP_TYPES_PARAMETER_NAME = "dumpTypes";

  public static final String
    DPR_OUTPUT_URL_PARAMETER_NAME = "outputDirectoryUrl";

  public static final String
    DPR_INCLUDE_FEAT_PARAMETER_NAME = "includeFeatures";

  public static final String
    DPR_USE_SUFFIX_PARAMETER_NAME = "useSuffixForDumpFiles";

  public static final String
    DPR_FILE_SUFFIX_PARAMETER_NAME = "suffixForDumpFiles";

  private static final boolean DEBUG = true;

  /**
   * A list of annotation types, which are to be dumped into the output file
   */
  protected List<String> annotationTypes;

  /**
   * A list of strings specifying new names to be used instead of the original
   * annotation types given in the annotationTypes parameter. For example, if
   * annotationTypes was set to [Location, Date], then if dumpTypes is set to
   * [Place, Date-expr], then the labels <Place> and <Date-expr> will be inserted
   * instead of <Location> and <Date>.
   */
  protected List<String> dumpTypes;

  /**the name of the annotation set
   * from which to take the annotations for dumping
   */
  protected String annotationSetName;

  /**
   * Whether or not to include the annotation features during export
   */
  protected boolean includeFeatures = false;

  /**
   * Whether or not to include the annotation features during export
   */
  protected boolean useStandOffXML = false;

  /**
   * What suffix to use for the dump files. .gate by default, but can be
   * changed via the set method.
   */
  protected String suffixForDumpFiles = ".gate";

  /**
   * Whether or not to use the special suffix fo the dump files. True by
   * default.
   */
  protected boolean useSuffixForDumpFiles = true;

  protected java.net.URL outputDirectoryUrl;

  private static final String DUMPING_PR_SET = "DumpingPRTempSet";

  /** Initialise this resource, and return it. */
  @Override
  public Resource init() throws ResourceInstantiationException
  {
    return super.init();
  } // init()

  /**
  * Reinitialises the processing resource. After calling this method the
  * resource should be in the state it is after calling init.
  * If the resource depends on external resources (such as rules files) then
  * the resource will re-read those resources. If the data used to create
  * the resource has changed since the resource has been created then the
  * resource will change too after calling reInit().
  */
  @Override
  public void reInit() throws ResourceInstantiationException
  {
    init();
  } // reInit()

  /** Run the resource. */
  @Override
  public void execute() throws ExecutionException {
    if(document == null)
      throw new GateRuntimeException("No document to process!");

    //if we're saving into standOffXML, then the rest of the settings do
    //not matter because that toXML method saves everything
    if (this.useStandOffXML) {
      write2File();
      return;
    }

    AnnotationSet allAnnots;
    // get the annotations from document
    if ((annotationSetName == null)|| (annotationSetName.equals("")))
      allAnnots = document.getAnnotations();
    else
      allAnnots = document.getAnnotations(annotationSetName);

    //if none found, print warning and exit
    if ((allAnnots == null) || allAnnots.isEmpty()) {
      Out.prln("DumpingPR Warning: No annotations found for export. "
               + "Including only those from the Original markups set.");
      write2File(null);
      return;
    }

    //if we're saving into standOffXML, then the rest of the settings do
    //not matter because that toXML method saves everything
    if (this.useStandOffXML) {
      write2File();
      return;
    }

    //first transfer the annotation types from a list to a set
    //don't I just hate this!
    Set<String> types2Export = new HashSet<String>(annotationTypes);

    //then get the annotations for export
    AnnotationSet annots2Export = allAnnots.get(types2Export);

    //check whether we want the annotations to be renamed before
    //export (that's what dumpTypes is for)
    if (dumpTypes != null && !dumpTypes.isEmpty()) {
      Map<String,String> renameMap = new HashMap<String,String>();
      for(int i=0; i<dumpTypes.size() && i<annotationTypes.size(); i++) {
        //check if we have a corresponding annotationType and if yes,
        //then add to the hash map for renaming
        renameMap.put(annotationTypes.get(i), dumpTypes.get(i));
      }//for
      //if we have to rename annotations, then do so
      if(!renameMap.isEmpty() && annots2Export != null)
        annots2Export = renameAnnotations(annots2Export, renameMap);
    }//if

    write2File(annots2Export);
    document.removeAnnotationSet(DumpingPR.DUMPING_PR_SET);

  } // execute()

  protected void write2File(AnnotationSet exportSet) {
      File outputFile;
      String fileName = null;
      if(document.getSourceUrl() == null)
        fileName = document.getName() + "_" + Gate.genSym();
      else 
        fileName = getFileName(document.getSourceUrl());
      
      fileName = getNewFileName(outputDirectoryUrl, fileName);
      StringBuffer tempBuff = new StringBuffer(fileName);
      //now append the special suffix if we want to use it
      if (useSuffixForDumpFiles)
        tempBuff.append(this.suffixForDumpFiles);

      String outputPath = tempBuff.toString();

      if (DEBUG)
        Out.prln(outputPath);
      outputFile = new File(outputPath);

    try {
      // Prepare to write into the xmlFile using the doc's encoding if there
      OutputStreamWriter writer;
      if (document instanceof DocumentImpl) {
        String encoding = ((DocumentImpl) document).getEncoding();
        if (encoding == null || "".equals(encoding))
          writer = new OutputStreamWriter(new FileOutputStream(outputFile));
        else
          writer = new OutputStreamWriter(
                            new FileOutputStream(outputFile), encoding);
      } else
          writer = new OutputStreamWriter(
                            new FileOutputStream(outputFile));

      // Write (test the toXml() method)
      // This Action is added only when a gate.Document is created.
      // So, is for sure that the resource is a gate.Document
      writer.write(document.toXml(exportSet, includeFeatures));
      writer.flush();
      writer.close();
    } catch (IOException ex) {
      throw new GateRuntimeException("Dumping PR: Error writing document "
                                     + document.getName() + ": "
                                     + ex.getMessage());
    }


  }//write2File

  protected void write2File() {
      File outputFile;
      String fileName = null;
      if(document.getSourceUrl() == null)
        fileName = document.getName() + "_" + Gate.genSym();
      else 
        fileName = getFileName(document.getSourceUrl());

      fileName = getNewFileName(outputDirectoryUrl, fileName);
      StringBuffer tempBuff = new StringBuffer(fileName);
      //now append the special suffix if we want to use it
      if (useSuffixForDumpFiles)
        tempBuff.append(this.suffixForDumpFiles);
      String outputPath = tempBuff.toString();
      if (DEBUG)
        Out.prln(outputPath);
      outputFile = new File(outputPath);

    try {
      // Prepare to write into the xmlFile using the doc's encoding if there
      OutputStreamWriter writer;
      if (document instanceof DocumentImpl) {
        String encoding = ((DocumentImpl) document).getEncoding();
        if (encoding == null || "".equals(encoding))
          writer = new OutputStreamWriter(new FileOutputStream(outputFile));
        else
          writer = new OutputStreamWriter(
                            new FileOutputStream(outputFile), encoding);
      } else
          writer = new OutputStreamWriter(
                            new FileOutputStream(outputFile));

      // Write (test the toXml() method)
      // This Action is added only when a gate.Document is created.
      // So, is for sure that the resource is a gate.Document
      writer.write(document.toXml());
      writer.flush();
      writer.close();
    } catch (IOException ex) {
      throw new GateRuntimeException("Dumping PR: Error writing document "
                                     + document.getName() + ": "
                                     + ex.getMessage());
    }


  }//write2File


  protected String getFileName(URL url) {
    String fileName = url.getFile();
    int index = fileName.lastIndexOf("/");
    if(index == -1) index = fileName.lastIndexOf("\\");
    if(index == -1)
      return fileName;
    else {
      if(index + 1 == fileName.length()) {
        fileName = fileName.substring(0, fileName.length()-1);
        index = fileName.lastIndexOf("/");
        if(index == -1) index = fileName.lastIndexOf("\\");
        if(index == -1) return fileName;
      }
      fileName = fileName.substring(index+1, fileName.length());
    }
    return fileName;
  }

  protected String getNewFileName(URL dir, String file) {
    return new File((dir == null) ?
      new File(System.getProperty("java.io.tmpdir")) : Files.fileFromURL(dir),
      file).getAbsolutePath();
  }

  protected AnnotationSet renameAnnotations(AnnotationSet annots2Export,
                                   Map<String,String> renameMap){
    Iterator<Annotation> iter = annots2Export.iterator();
    AnnotationSet as = document.getAnnotations(DUMPING_PR_SET);
    if (!as.isEmpty())
      as.clear();
    while(iter.hasNext()) {
      Annotation annot = iter.next();
      //first check whether this type needs to be renamed
      //if not, continue
      if (!renameMap.containsKey(annot.getType()))
        renameMap.put(annot.getType(), annot.getType());
      try{
        as.add(annot.getId(),
            annot.getStartNode().getOffset(),
            annot.getEndNode().getOffset(),
            renameMap.get(annot.getType()),
            annot.getFeatures());
      } catch (InvalidOffsetException ex) {
        throw new GateRuntimeException("DumpingPR: " + ex.getMessage());
      }
    }//while
    return as;
  }//renameAnnotations


  /**get the name of the annotation set*/
  public String getAnnotationSetName() {
    return annotationSetName;
  }//getAnnotationSetName

  /** set the annotation set name*/
  public void setAnnotationSetName(String newAnnotationSetName) {
    this.annotationSetName = newAnnotationSetName;
  }//setAnnotationSetName

  public List<String> getAnnotationTypes() {
    return this.annotationTypes;
  }

  public void setAnnotationTypes(List<String> newTypes) {
    this.annotationTypes = newTypes;
  }

  public List<String> getDumpTypes() {
    return this.dumpTypes;
  }

  public void setDumpTypes(List<String> newTypes) {
    dumpTypes = newTypes;
  }

  public URL getOutputDirectoryUrl() {
    return this.outputDirectoryUrl;
  }

  public void setOutputDirectoryUrl(URL file) {
    this.outputDirectoryUrl = file;
  }

  public void setIncludeFeatures(Boolean inclFeatures) {
    if (inclFeatures != null)
      includeFeatures = inclFeatures.booleanValue();
  }

  public Boolean getIncludeFeatures() {
    return new Boolean(includeFeatures);
  }

  public void setUseStandOffXML(Boolean newValue) {
    if (newValue != null)
      useStandOffXML = newValue.booleanValue();
  }

  public Boolean getUseStandOffXML() {
    return new Boolean(useStandOffXML);
  }

  public String getSuffixForDumpFiles() {
    return suffixForDumpFiles;
  }

  public void setSuffixForDumpFiles(String newSuffix) {
    this.suffixForDumpFiles = newSuffix;
  }

  public Boolean getUseSuffixForDumpFiles() {
    return new Boolean(this.useSuffixForDumpFiles);
  }

  public void setUseSuffixForDumpFiles(Boolean useOrNot) {
    if (useOrNot != null)
      this.useSuffixForDumpFiles = useOrNot.booleanValue();
  }

} // class DumpingPR
