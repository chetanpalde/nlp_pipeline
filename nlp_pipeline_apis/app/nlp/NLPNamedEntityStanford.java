package nlp;

import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Pair;

import java.util.*;
import java.util.Map.Entry;

/**
 * A Class to find Named Entity using Stanford NER.
 * @author synerzip
 *
 */
class NLPNamedEntityStanford {

	/**
	 * An Empty Constructor.
	 */
	NLPNamedEntityStanford() {
	}

	/**
	 * Only use the following tag set to find named entities
	 */
	private static final Set<String> validTags = new HashSet<String>(Arrays.asList(
			new String[] { "PERSON", "ORGANIZATION", "LOCATION", "MISC", "MONEY", "PERCENT", "DATE", "TIME", "O" }));

	/**
	 * Function to get class/label for a phrase.
	 * @param labels - phrase having array of words
	 * @return It returns class/label having highest frequency of its occurence of particular class for a phrase.
	 */
	static String predictNer(ArrayList<String> labels) {
		String label = "";
		HashMap<String, Integer> wordFreq = new HashMap<String, Integer>();
		Set<String> unique = new HashSet<String>(labels);
		for (String key : unique) {
			wordFreq.put(key, Collections.frequency(labels, key));
			int maxValueInMap = (Collections.max(wordFreq.values()));
			for (Entry<String, Integer> entry : wordFreq.entrySet()) {
				if (entry.getValue() == maxValueInMap) {
					label = entry.getKey();
				}
			}
		}
		return label;
	}

	/**
	 * Function to get Phrase and labels.
	 * @param start - Start position of word in a sentence
	 * @param tid - End position of word in a sentence
	 * @param tokens - List of tokens of a sentence.
	 * @return It returns phrase and array of labels/classes of each word of a phrase.
	 */
	static Map<String, ArrayList<String>> getPhraseLabel(int start, int tid, List<CoreLabel> tokens) {
		Map<String, ArrayList<String>> arrPhraseLabel = new HashMap<>();
		ArrayList<String> arrWords = new ArrayList<String>();
		ArrayList<String> labels = new ArrayList<String>();
		for (int counter = start; counter < tid; counter++) {
			arrWords.add(tokens.get(counter).originalText().toString());
			labels.add(tokens.get(counter).get(NamedEntityTagAnnotation.class));
		}
		arrPhraseLabel.put("phrase", arrWords);
		arrPhraseLabel.put("labels", labels);
		return arrPhraseLabel;
	}

	/**
	 * Function to get named entity and part of NLP pipeline.
	 * @param sentence - Sentence of a paragraph.
	 * @return - It returns array list of phrase and their respective label/class
	 */
	ArrayList<Map<String, Object>> getNamedEntityRecognizer(CoreMap sentence) {
		ArrayList<Map<String, Object>> arrNer = new ArrayList<Map<String, Object>>();
		Map<String, ArrayList<String>> arrPhraseLabel = new HashMap<>();
		List<Pair<Integer, Integer>> offsets = new ArrayList<Pair<Integer, Integer>>();
		String prevTag = "O";
		int tid = 0;
		int start = -1;
		Map<String, Object> kvNer = null;
		List<CoreLabel> tokens = sentence.get(TokensAnnotation.class);
		for (CoreLabel token : tokens) {
			kvNer = new HashMap<String, Object>();
			String tag = token.get(NamedEntityTagAnnotation.class);
			if (!validTags.contains(tag)) {
				tag = "O";
			}
			if (tag.equals(prevTag)) {

			} else {
				if (tag.equals("O")) {
					offsets.add(Pair.makePair(start, tid));
					arrPhraseLabel = getPhraseLabel(start, tid, tokens);
					kvNer.put("phrase", String.join(" ", arrPhraseLabel.get("phrase")));
					kvNer.put("label", predictNer(arrPhraseLabel.get("labels")));
					arrNer.add(kvNer);
					start = -1;
				} else {
					if (prevTag.equals("O")) {
						start = tid;
					} else {
						offsets.add(Pair.makePair(start, tid));
						arrPhraseLabel = getPhraseLabel(start, tid, tokens);
						kvNer.put("phrase", String.join(" ", arrPhraseLabel.get("phrase")));
						kvNer.put("label", predictNer(arrPhraseLabel.get("labels")));
						arrNer.add(kvNer);
						start = tid;
					}
				}
			}
			prevTag = tag;
			tid++;
		}
		if (!prevTag.equals("O")) {
			kvNer = new HashMap<String, Object>();
			offsets.add(Pair.makePair(start, tid));
			arrPhraseLabel = getPhraseLabel(start, tid, tokens);
			kvNer.put("phrase", String.join(" ", arrPhraseLabel.get("phrase")));
			kvNer.put("label", predictNer(arrPhraseLabel.get("labels")));
			arrNer.add(kvNer);
		}
		return arrNer;
	}
}
