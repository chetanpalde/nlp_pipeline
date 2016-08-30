package nlp;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A Class to split pargraph into sentences using Apache OpenNLP
 * @author synerzip
 *
 */
class NLPSentenceSplitter

	{

	SentenceModel model = null;
	Mediator mediator;

	/**
	 * Constructor of load Open NLP sentence splitter model.
	 */
	NLPSentenceSplitter() {
		mediator = new Mediator();
	}

	/**
	 * Function to do sentence splitting and it is part of NLP pipeline.
	 * @param pipeline - Stanford pipeline consists of modules.
	 * @param openNlpSentenceModel - OpenNLP SentenceDetectorME paramter.
	 * @param text - Text in string.
	 * @return It returns list of sentences in CoreMap Class format.
	 */
	List<CoreMap> getSentences(StanfordCoreNLP pipeline, SentenceDetectorME openNlpSentenceModel, String text) {
		Annotation sentenceAnnotation;
		Annotation coremapSentence;
		List<CoreMap> coremapSentences = new ArrayList<>();
		String[] sentences = openNlpSentenceModel.sentDetect(text);
		int index = 0;
		int sentenceLength = sentences.length;
		for (String sentence : sentences) {

			// Apply annotation to String sentence
			sentenceAnnotation = new Annotation(sentence);
			pipeline.annotate(sentenceAnnotation);

			// Calling custom Annotation to create coreMap sentences
			coremapSentence = mediator.customAnnotate(sentenceAnnotation, sentenceLength, index++);

			// add the sentence to the list
			coremapSentences.add(coremapSentence);
		}
		return coremapSentences;
	}
}