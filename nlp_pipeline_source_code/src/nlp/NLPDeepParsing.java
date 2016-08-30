package nlp;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to do stanford dependency parsing.
 * @author synerzip
 *
 */
class NLPDeepParsing {

	/**
	 * An empty constructor.
	 */
	NLPDeepParsing() {
	}

	/**
	 * Function to create deep parse of a sentence and part of NLP pipeline.
	 * @param lexicalizedParser - Lexical parser used to do parsing.
	 * @param sentence - Sentence in String.
	 * @return It returns dependencies between words.
	 */
	ArrayList<Map<String, Object>> getDeepParsing(LexicalizedParser lexicalizedParser, String sentence) {
		ArrayList<Map<String, Object>> arrDependencies = new ArrayList<Map<String, Object>>();
		lexicalizedParser.setOptionFlags(new String[] { "-maxLength", "80", "-retainTmpSubcategories" });
		Tree tree = lexicalizedParser.parse(sentence);
		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
		List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
		for (TypedDependency typedDependency : tdl) {
			Map<String, Object> dictDependency = new HashMap<String, Object>();
			IndexedWord dep = typedDependency.dep();
			String dependentWord = dep.word();
			int dependentIndex = dep.index();
			IndexedWord gov = typedDependency.gov();
			String governorWord = gov.word();
			int governorIndex = gov.index();
			GrammaticalRelation relation = typedDependency.reln();
			dictDependency.put("relation", relation.toString());
			dictDependency.put("dependent_index", dependentIndex);
			dictDependency.put("dependent", dependentWord.toString());
			dictDependency.put("governor_index", governorIndex);
			dictDependency.put("governor", governorWord.toString());
			arrDependencies.add(dictDependency);
		}
		return arrDependencies;
	}
}
