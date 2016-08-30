package nlp;

import edu.stanford.nlp.util.CoreMap;
import gate.*;
import gate.creole.ANNIEConstants;
import gate.util.GateException;
import gate.util.persistence.PersistenceManager;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * A Class to find Named Entity using GATE ANNIE.
 * @author symerzip
 *
 */
class NLPNamedEntityGateAnnie {

	/**
	 * An Empty Constructor
	 */
	NLPNamedEntityGateAnnie() {
	}

	/**
	 * @param sentence - Coremap Sentence of Stanford.
	 * @return - It returns array list of phrase and their respective label/class
	 * @throws GateException
	 * @throws IOException
	 */
	ArrayList<Map<String, Object>> getNamedEntityGateAnnie(CoreMap sentence) throws GateException, IOException {
		ArrayList<Map<String, Object>> arrNer = new ArrayList<Map<String, Object>>();
		Map<String, Object> kvNer;
		LanguageAnalyser controller = (LanguageAnalyser) PersistenceManager.loadObjectFromFile(
				new File(new File(Gate.getPluginsHome(), ANNIEConstants.PLUGIN_DIR), ANNIEConstants.DEFAULT_FILE));
		Corpus corpus = Factory.newCorpus("corpus");
		Document document = Factory.newDocument(sentence.toString());
		corpus.add(document);
		controller.setCorpus(corpus);
		controller.execute();
		AnnotationSet tags = document.getAnnotations()
				.get(new HashSet<>(Arrays.asList("Person", "Organization", "Location", "Money", "Date", "Time", "Percentage")));
		for (Annotation tag : tags) {
			kvNer = new HashMap<String, Object>();
			kvNer.put("phrase", Utils.stringFor(document, tag).toString());
			kvNer.put("label", tag.getType().toString());
			arrNer.add(kvNer);
		}
		Factory.deleteResource(document);
		Factory.deleteResource(corpus);
		Factory.deleteResource(controller);
		return arrNer;
	}
}