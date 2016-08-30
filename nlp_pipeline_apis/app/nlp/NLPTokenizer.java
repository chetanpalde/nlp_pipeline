package nlp;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Class to create tokens of a sentence. 
 * @author synerzip
 *
 */
class NLPTokenizer {
	/**
	 * An empty constructor.
	 */
	NLPTokenizer() {
	}

	/**
	 * Function to create tokens of a sentence and part of NLP pipeline.
	 * @param sentence - A sentence to apply tokens on
	 * @return - It returns list of tokens
	 */
	ArrayList<Map<String, Object>> getTokens(CoreMap sentence) {
		ArrayList<Map<String, Object>> arrTokens = new ArrayList<Map<String, Object>>();
		List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
		for (CoreLabel token : tokens) {
			Map<String, Object> kvToken = new HashMap<String, Object>();
			kvToken.put("token", token.originalText());
			kvToken.put("begin_position", token.beginPosition());
			kvToken.put("end_position", token.endPosition());
			arrTokens.add(kvToken);
		}
		return arrTokens;
	}
}
