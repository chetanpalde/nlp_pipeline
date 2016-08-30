package nlp;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Class to get part of speech of a sentence. 
 * @author synerzip
 *
 */
class NLPPartOfSpeech {



	/**
	 * An empty constructor.
	 */
	NLPPartOfSpeech() {
	}

	/**
	 * Function to get part of speech of a sentence and part of NLP pipeline.
	 * @param tokens - list of tokens of a sentence.
	 * @return It returns part of speech tag on each word.
	 */
	ArrayList<Map<String, Object>> getPartOfSpeech(List<CoreLabel> tokens) {
		ArrayList<Map<String, Object>> arrPos = new ArrayList<Map<String, Object>>();
		for (CoreLabel token : tokens) {
			String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
			Map<String, Object> kvPos = new HashMap<String, Object>();
			kvPos.put("token", token.originalText());
			kvPos.put("begin_position", token.beginPosition());
			kvPos.put("end_position", token.endPosition());
			kvPos.put("pos", pos);
			arrPos.add(kvPos);
		}
		return arrPos;
	}
}
