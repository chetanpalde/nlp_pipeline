package nlp;

import edu.stanford.nlp.ling.CoreLabel;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.util.Span;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Class to find Named Entity using Apache OpenNLP.
 * @author synerzip
 *
 */
class NLPNamedEntityOpenNLP {

	/**
	 * An Empty Constructor.
	 */
	NLPNamedEntityOpenNLP() {
	}

	/**
	 * Function to find NER using Apache OpenNLP
	 * @param opennlp_ner_models - Loaded Apache OpenNLP models
	 * @param tokens - words of a sentence.
	 * @return - It returns array list of phrase and their respective label/class
	 */
	ArrayList<Map<String, Object>> getNamedEntityOpenNLP(ArrayList<NameFinderME> opennlp_ner_models, List<CoreLabel> tokens) {
		ArrayList<Map<String, Object>> arrNer = new ArrayList<Map<String, Object>>();
		Map<String, Object> kvNer;
		String[] token = new String[tokens.size()];
		for (CoreLabel t : tokens) {
			token[t.index() - 1] = t.originalText().toString();
		}
		for (NameFinderME name_finder : opennlp_ner_models) {
			Span nameSpans[] = name_finder.find(token);
			for (Span span : nameSpans) {
				kvNer = new HashMap<String, Object>();
				String phrase = "";
				for (int counter = span.getStart(); counter < span.getEnd(); counter++) {
					phrase += token[counter] + " ";
				}
				kvNer.put("phrase", phrase.trim());
				kvNer.put("label", span.getType());
				arrNer.add(kvNer);
			}
		}
		return arrNer;
	}
}
