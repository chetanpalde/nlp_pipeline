/*
 *  TestConstraints
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Eric Sword, 03/09/08
 *
 *  $Id: BaseJapeTests.java 17935 2014-05-09 08:56:35Z markagreenwood $
 */
package gate.jape.functest;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.Resource;
import gate.corpora.DocumentStaxUtils;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.util.Files;
import gate.util.GateException;
import gate.util.InvalidOffsetException;
import gate.util.OffsetComparator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Tests for Constraint predicate logic
 */
public abstract class BaseJapeTests extends TestCase {
    /** JAPE Transducer under test. CHANGE THIS to test another Transducer */
    public static final TransducerType transducerType = TransducerType.CLASSIC;

    private static final Logger logger = Logger.getLogger(BaseJapeTests.class);

    protected static final String DEFAULT_DATA_FILE = "jape/InputTexts/AveShort";

    public BaseJapeTests(String name) {
	super(name);
	setUpGate(); //TODO remove when figure out why TestConstraints won't start
    }

    /**
     * Initializes GATE and sets up plug-ins. The operation is idempotent.
     */
    protected static void setUpGate() {
	if (Gate.isInitialised()) {
	    logger.warn("GATE already intialized and set up for JAPE Transducer tests.");
	    
	}
	else {
	  
	

	Properties logConfProps = new Properties();
	InputStream logConfStream = BaseJapeTests.class.getResourceAsStream("log4j-test.properties");
	assert logConfStream != null : "Cannot locate LOG4J properties file";
	try {
	    logConfProps.load(logConfStream);
	    logConfStream.close();
	} catch (IOException e2) {
	    logger.warn("Unable to load logging properties.");
	    BasicConfigurator.configure();
	}

	PropertyConfigurator.configure(logConfProps);

	if (System.getProperty("gate.home") == null)
	    System.setProperty("gate.home", ".");
	try {
	    Gate.init();
	} catch (GateException e1) {
	    String errMsg = "Unable to initialize GATE";
	    logger.fatal(errMsg);
	    throw new RuntimeException(errMsg);
	}
	logger.debug("GATE home is: " + Gate.getGateHome().getAbsolutePath());

	if (!Gate.getPluginsHome().isDirectory()) {
	    String errMsg = "GATE home and plug-in directory set up failure.";
	    logger.fatal(errMsg);
	    throw new RuntimeException(errMsg);
	}
	
	}

	/** CHANGE THIS if JAPE Transducer need more plug-ins */
	final File[] plugInsToLoad = { new File(Gate.getPluginsHome(), "Ontology") };

	for (File plugInDir : plugInsToLoad) {
	    try {
		registerCREOLE(plugInDir);
	    } catch (GateException ge) {
		logger.warn(plugInDir.getAbsoluteFile() + " was not loaded.");
	    }
	}

	try {
	    registerCREOLE((transducerType == TransducerType.CLASSIC) ? new File(Gate.getPluginsHome(), "ANNIE") : transducerType.getPlugInDir());
	} catch (Exception e) {
	    String errMsg = "JAPE Transducer plug-in cannot be loaded.";
	    logger.fatal(errMsg);
	    throw new RuntimeException(errMsg);
	}
    }

    private static void registerCREOLE(File plugInDir) throws GateException {
	if (plugInDir == null || !plugInDir.isDirectory()) {
	    throw new IllegalArgumentException(plugInDir.getAbsolutePath()
		    + " is not a valid plug-in directory.");
	}
	try {
	    File creolePath = plugInDir.getCanonicalFile();
	    Gate.getCreoleRegister().registerDirectories(creolePath.toURI().toURL());
	} catch (IOException e) {
	    throw new GateException(e);
	}
    }

    private static Resource createOntology(String ontologyURL) throws MalformedURLException, ResourceInstantiationException {
	FeatureMap params = Factory.newFeatureMap();
	params.put("rdfXmlURL", new URL(ontologyURL)); //TODO: FeatureMap keys should be defined as constants somewhere
	params.put("loadImports", true);
	final String ontologyClass = "gate.creole.ontology.impl.sesame.OWLIMOntology";
	Resource ontology = null;
	    ontology = Factory.createResource(ontologyClass, params);

	return ontology;
    }

    /**
     * Executes transducer with the provided parameters and returns transduced annotations ordered
     * 
     * @param doc a GATE document with initial annotations 
     * @param japeFile a file with the JAPE grammar
     * @param ontologyURL optional ontology URL. Might be null.
     * @return an ordered set of transduced annotations
     * 
     * @throws MalformedURLException
     * @throws ResourceInstantiationException
     * @throws ExecutionException
     */
    private Set<Annotation> runTransducer(Document doc, String japeFile, String ontologyURL)
	    throws MalformedURLException, ResourceInstantiationException, ExecutionException {

	if (doc == null || japeFile == null) {
	    throw new IllegalArgumentException("Document or JAPE file must not be null");
	}
	FeatureMap params = Factory.newFeatureMap();
	URL japeUrl = Files.getGateResource(japeFile);
	if (japeUrl == null) {
	    throw new IllegalArgumentException("Resource with relative path: " + japeFile + " is missing.");
	}

	switch (transducerType) {
	case PLUS:
	case PDAPLUS: {
	    params.put("sourceType", "JAPE"); //TODO: FeatureMap keys should be defined as constants somewhere
	    params.put("sourceURL", japeUrl);
	}
	    break;
	case CLASSIC:
	case PDA: {
	    params.put("grammarURL", japeUrl);
	}
	    break;
	default:
	    String errMsg = "Unknown JAPE transducer type";
	    logger.fatal(errMsg);
	    throw new IllegalArgumentException(errMsg);
	}

	params.put("encoding", "UTF-8");

	final String outputAsName = "Output";
	params.put("outputASName", outputAsName);

	

	AbstractLanguageAnalyser transducer = (AbstractLanguageAnalyser) Factory.createResource(
		transducerType.getFqdnClass(), params);
	
	Resource ontology = null;
	
	if (ontologyURL != null) {
    ontology = createOntology(ontologyURL);
    transducer.setParameterValue("ontology", ontology);
	}
	
	transducer.setDocument(doc);
	transducer.execute();
	
	if (ontology != null) {
	  transducer.setParameterValue("ontology", null);
	  Factory.deleteResource(ontology);	  
	}

	Set<Annotation> orderedResults = new TreeSet<Annotation>(new OffsetComparator());
	orderedResults.addAll(doc.getAnnotations(outputAsName));
	return orderedResults;
    }

    /* Test Utility Methods */
    protected Set<Annotation> doTest(String docResourcePath, String japeResourcePath, AnnotationCreator ac,
	    String ontologyURL) throws Exception {

	Document doc = Factory.newDocument(Files.getGateResourceAsString(docResourcePath));
	return doTest(doc, japeResourcePath, ac, ontologyURL);
	
    }

    protected Set<Annotation> doTest(String docResourcePath, String japeResourcePath, AnnotationCreator ac)
	    throws Exception {
	return doTest(docResourcePath, japeResourcePath, ac, null);
    }
    
    protected static int count=0;

    protected Set<Annotation> doTest(Document doc, String japeResourcePath, AnnotationCreator ac,
	    String ontologyURL) throws Exception {
	if (ac != null)
	    ac.annotate(doc);
	Set<Annotation> orderedResults = runTransducer(doc, japeResourcePath, ontologyURL);
	//DocumentStaxUtils.writeDocument(doc, new File("/home/mark/test"+(count++)+".xml"));
	return orderedResults;
    }

    /* Result Comparison */
    /**
     * Compares 2 ordered sets of annotations by comparison of the rules that created them.
     */
    protected static void compareResults(String[] expectedResults, Set<Annotation> actualResults) {
	int i = 0;

	assertEquals("Number of the expected and transduced annotations must be equal.",
		expectedResults.length, actualResults.size());

	for (Annotation annot : actualResults) {
	    String ruleName = (String) annot.getFeatures().get("rule");
	    assertEquals("Annotation must be created by rule: " + expectedResults[i], expectedResults[i],
		    ruleName);
	    i++;
	}
    }

    protected final static void compareStartOffsets(Set<Annotation> res, int... startOffsets) {
	assertEquals(startOffsets.length, res.size());
	int i = 0;
	for (Annotation annot : res) {
	    assertEquals("Annotation " + annot.getId() + " must start at position " + startOffsets[i],
		    startOffsets[i], annot.getStartNode().getOffset().intValue());
	    i++;
	}
    }

    protected final static void compareEndOffsets(Set<Annotation> res, int... endOffsets) {
	assertEquals(endOffsets.length, res.size());
	int i = 0;
	for (Annotation annot : res) {
	    assertEquals("Annotation  " + annot.getId() + " msut end at position " + endOffsets[i],
		    endOffsets[i], annot.getEndNode().getOffset().intValue());
	    i++;
	}
    }

    /* Utility Classes */

    /**
     * Callback interface used in the doTest method.
     * 
     * @version $Revision$
     * @author esword
     */
    public static interface AnnotationCreator {
	//public void setDoc(Document doc);

	public AnnotationSet createAnnots(Document doc) throws InvalidOffsetException;
	
	public void annotate(Document doc) throws InvalidOffsetException;

	//public AnnotationCreator addInc(String type);

	//public AnnotationCreator add(int start, int end, String type) throws InvalidOffsetException;

	//public AnnotationCreator add(int start, int end, String type, FeatureMap fm) throws InvalidOffsetException;

	//public AnnotationCreator add(String type);
    }

    public static abstract class BaseAnnotationCreator implements AnnotationCreator {
	protected AnnotationSet as;
	protected int curOffset = 0;
	protected int dfltAnnotLen = 2;
	protected static FeatureMap emptyFeat = Factory.newFeatureMap();

	public BaseAnnotationCreator() {}

	@Override
  public final void annotate(Document doc) throws InvalidOffsetException {
	    as = doc.getAnnotations();
	    createAnnots(doc);
	}

	/**
	 * Add an annotation of the given type over the given range. Does not
	 * increment curOffset.
	 * @throws InvalidOffsetException 
	 */
	protected AnnotationCreator add(int start, int end, String type) throws InvalidOffsetException {
	    return add(start, end, type, emptyFeat);
	}

	/**
	 * Add an annotation of the given type over the given range. Does not
	 * increment curOffset.
	 * @throws InvalidOffsetException 
	 */
	protected AnnotationCreator add(int start, int end, String type, FeatureMap fm)
		throws InvalidOffsetException {
	    as.add(new Long(start), new Long(end), type, fm);
	    return this;
	}

	/**
	 * Add an annotation of the given type at the current offset and
	 * increment the placement counter.
	 * @throws InvalidOffsetException 
	 */
	protected AnnotationCreator addInc(String type) throws InvalidOffsetException {
	    add(type);
	    curOffset += dfltAnnotLen;
	    return this;
	}

	/**
	 * Add annot at the current offset
	 * @throws InvalidOffsetException 
	 */
	protected AnnotationCreator add(String type) throws InvalidOffsetException {
	    as.add(new Long(curOffset), new Long(curOffset + dfltAnnotLen), type, emptyFeat);
	    return this;
	}
    }

}
