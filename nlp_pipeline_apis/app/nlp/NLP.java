package nlp;

import com.google.gson.Gson;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import gate.util.GateException;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.sentdetect.SentenceDetectorME;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Class from where all modules are loaded and NLP pipeline starts.
 * 
 * @author synerzip
 *
 */
class NLP {
	private Models nlpModels;
	private ArrayList<Object> stanfordModels;
	private ArrayList<Object> openNlpModels;
	private StanfordCoreNLP pipeline;
	private LexicalizedParser lexicalizedParser;
	private ArrayList<NameFinderME> openNlpNerModels;
	private SentenceDetectorME openNlpSentenceModel;
	private static ArrayList<Map<String, Object>> output;

	/**
	 * NLP Constructor that make a call to stanford and opennlp to load their
	 * models.
	 */
	NLP() {
		nlpModels = new Models();
		stanfordModels = nlpModels.loadStanfordModels();
		pipeline = (StanfordCoreNLP) stanfordModels.get(0);
		lexicalizedParser = (LexicalizedParser) stanfordModels.get(1);
		openNlpModels = nlpModels.loadOpenNlpModels();
		openNlpSentenceModel = (SentenceDetectorME) openNlpModels.get(0);
		openNlpNerModels = (ArrayList<NameFinderME>) openNlpModels.get(1);
		nlpModels.loadGateAnnieNerModel();
	}

	/**
	 * Start NLP Pipeline.
	 */
	String parseInput(String text) {
		output = new ArrayList<>();
		Annotation annotation = new Annotation(text);
		pipeline.annotate(annotation);
		System.out.println("\n");
		// Sentence Segmentation
		NLPSentenceSplitter cSentenceSplitter = new NLPSentenceSplitter();
		List<CoreMap> sentences = cSentenceSplitter.getSentences(pipeline, openNlpSentenceModel, text);
		if (sentences != null && sentences.size() > 0) {
			for (CoreMap sentence : sentences) {
				Map<String, Object> eachSentenceOutput = new LinkedHashMap<>();
				eachSentenceOutput.put("sentence", sentence.toString());
				List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);

				// Tokenizer
				NLPTokenizer ctokenizer = new NLPTokenizer();
				ArrayList<Map<String, Object>> opTokens = ctokenizer.getTokens(sentence);
				eachSentenceOutput.put("tokenizer", opTokens);

				// Lemmatizer
				NLPLemmatizer cLemmatizer = new NLPLemmatizer();
				ArrayList<Map<String, Object>> opLemmas = cLemmatizer.getLemmas(tokens);
				eachSentenceOutput.put("lemmatizer", opLemmas);

				// Part of Speech
				NLPPartOfSpeech cPartOfSpeech = new NLPPartOfSpeech();
				ArrayList<Map<String, Object>> opPos = cPartOfSpeech.getPartOfSpeech(tokens);
				eachSentenceOutput.put("part_of_speech", opPos);

				// Shallow Parsing
				NLPShallowParsing cShallowParsing = new NLPShallowParsing();
				ArrayList<Map<String, Object>> opShallowParsing = cShallowParsing.getShallowParse(lexicalizedParser,
						sentence.toString());
				eachSentenceOutput.put("shallow_parsing", opShallowParsing);

				// Deependency Parsing
				NLPDeepParsing cDeepParsing = new NLPDeepParsing();
				ArrayList<Map<String, Object>> opDeepParsing = cDeepParsing.getDeepParsing(lexicalizedParser,
						sentence.toString());
				eachSentenceOutput.put("deep_parsing", opDeepParsing);

				// Named-Entity Recognization GATE ANNIE
				NLPNamedEntityGateAnnie cNamedEntityGateAnnie = new NLPNamedEntityGateAnnie();
				ArrayList<Map<String, Object>> opGateAnnieNer;
				try {
					opGateAnnieNer = cNamedEntityGateAnnie.getNamedEntityGateAnnie(sentence);
					eachSentenceOutput.put("gate_annie_ner", opGateAnnieNer);
				} catch (GateException | IOException e) {
					e.printStackTrace();
				}

				// Named-Entity Recognization Apache OpenNLP
				NLPNamedEntityOpenNLP cNamedEntityOpenNLP = new NLPNamedEntityOpenNLP();
				ArrayList<Map<String, Object>> opOpenNlpNer = cNamedEntityOpenNLP.getNamedEntityOpenNLP(openNlpNerModels, tokens);
				eachSentenceOutput.put("opennlp_ner", opOpenNlpNer);

				// Named-Entity Recognization Stanford
				NLPNamedEntityStanford cNamedEntityRecognizer = new NLPNamedEntityStanford();
				ArrayList<Map<String, Object>> opNer = cNamedEntityRecognizer.getNamedEntityRecognizer(sentence);
				eachSentenceOutput.put("stanford_ner", opNer);

				output.add(eachSentenceOutput);
			}
		}
		return new Gson().toJson(output);
	}
}