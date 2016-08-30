package nlp;

import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.Tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to do stanford tree parsing/ shallow parsing.
 * @author synerzip
 *
 */
class NLPShallowParsing {


	/**
	 * An empty constructor.
	 */
	NLPShallowParsing() {
	}

	/**
	 * Function to create tree parse of a sentence and part of NLP pipeline.
	 * @param lexicalizedParser - Lexical parser used to do parsing.
	 * @param sentence - Sentence in String.
	 * @return It returns parsers on a phrases/words
	 */
	ArrayList<Map<String, Object>> getShallowParse(LexicalizedParser lexicalizedParser, String sentence) {
		ArrayList<Map<String, Object>> arrShallowParse = new ArrayList<Map<String, Object>>();

		lexicalizedParser.setOptionFlags(new String[] { "-maxLength", "80", "-retainTmpSubcategories" });
		Tree tree = lexicalizedParser.parse(sentence);
		for (Tree subtree : tree) {
			Map<String, Object> kvShallowParse = new HashMap<String, Object>();
			String typeLabel = subtree.label().value();
			if (typeLabel.equals("NP") || typeLabel.equals("VP") || typeLabel.equals("PP")) {
				kvShallowParse.put("label", subtree.label().toString());
				String phrase = "";
				for (Tree subTree : subtree.getLeaves()) {
					phrase += " " + subTree.toString();
				}
				phrase = phrase.trim();
				kvShallowParse.put("phrase", phrase);
				arrShallowParse.add(kvShallowParse);
			}
		}
		return arrShallowParse;
	}
}
