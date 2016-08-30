package nlp;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreLabel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Class to do stemming on a words. 
 * @author synerzip
 *
 */
class NLPLemmatizer {


	/**
	 * Constructor to load WordNet Module by using JWNL model of wordnet.
	 *
	 */
	NLPLemmatizer() {}

	/**
	 * Function to do stem on a word and part of NLP pipeline.
	 * @param tokens - List of tokens of a sentence.
	 * @return It returns array of stem words.
	 */
	ArrayList<Map<String, Object>> getLemmas(List<CoreLabel> tokens) {
		ArrayList<Map<String, Object>> arrLemmas = new ArrayList<Map<String, Object>>();
		for (CoreLabel token : tokens) {
			String lemma = token.get(LemmaAnnotation.class); /* Stanford Lemmatizer */
			Map<String, Object> kvLemma = new HashMap<String, Object>();
			kvLemma.put("word", token.originalText());
			kvLemma.put("begin_position", token.beginPosition());
			kvLemma.put("end_position", token.endPosition());
			kvLemma.put("lemma", lemma);
			arrLemmas.add(kvLemma);
		}
		return arrLemmas;
	}
}
