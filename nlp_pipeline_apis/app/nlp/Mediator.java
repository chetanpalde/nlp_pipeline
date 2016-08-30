package nlp;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.ChunkAnnotationUtils;
import edu.stanford.nlp.util.CoreMap;

import java.util.List;

/**
 * Class to convert String sentence into CoreMap Class sentence.
 * 
 * @author synerzip
 *
 */
class Mediator {
	/**
	 * Function to convert sentence into CoreMap sentence
	 * @param annotation
	 * @param sentenceCount - Number of sentences.
	 * @param sentenceIndex - Index position of sentence into list of sentences.
	 * @return It returns CoreMap (Annotation) Type sentence.
	 */
	Annotation customAnnotate(Annotation annotation, int sentenceCount, int sentenceIndex) {
		final boolean countLineNumbers = false;
		if (!annotation.containsKey(CoreAnnotations.TokensAnnotation.class)) {
			throw new IllegalArgumentException(
					"WordsToSentencesAnnotator: unable to find words/tokens in: " + annotation);
		}
		// get text and tokens from the document
		String text = annotation.get(CoreAnnotations.TextAnnotation.class);
		List<CoreLabel> sentenceTokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
		String docID = annotation.get(CoreAnnotations.DocIDAnnotation.class);

		// assemble the sentence annotations
		int tokenOffset = 0;
		int lineNumber = sentenceIndex;
		// section annotations to mark sentences with
		CoreMap sectionAnnotations = null;
		if (sentenceTokens.isEmpty()) {
			if (!countLineNumbers) {
				throw new IllegalStateException("unexpected empty sentence: " + sentenceTokens);
			}
		}
		// get the sentence text from the first and last character offsets
		int begin = sentenceTokens.get(0).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class);
		int last = sentenceTokens.size() - 1;
		int end = sentenceTokens.get(last).get(CoreAnnotations.CharacterOffsetEndAnnotation.class);
		String sentenceText = text.substring(begin, end);

		// create a sentence annotation with text and token offsets
		Annotation sentence = new Annotation(sentenceText);
		sentence.set(CoreAnnotations.CharacterOffsetBeginAnnotation.class, begin);
		sentence.set(CoreAnnotations.CharacterOffsetEndAnnotation.class, end);
		sentence.set(CoreAnnotations.TokensAnnotation.class, sentenceTokens);
		sentence.set(CoreAnnotations.TokenBeginAnnotation.class, tokenOffset);
		tokenOffset += sentenceTokens.size();
		sentence.set(CoreAnnotations.TokenEndAnnotation.class, tokenOffset);
		sentence.set(CoreAnnotations.SentenceIndexAnnotation.class, sentenceIndex);

		if (countLineNumbers) {
			sentence.set(CoreAnnotations.LineNumberAnnotation.class, ++lineNumber);
		}
		// Annotate sentence with section information.
		// Assume section start and end appear as first and last tokens of
		// sentence
		CoreLabel sentenceStartToken = sentenceTokens.get(0);
		CoreLabel sentenceEndToken = sentenceTokens.get(sentenceTokens.size() - 1);

		CoreMap sectionStart = sentenceStartToken.get(CoreAnnotations.SectionStartAnnotation.class);
		if (sectionStart != null) {
			// Section is started
			sectionAnnotations = sectionStart;
		}
		if (sectionAnnotations != null) {
			// transfer annotations over to sentence
			ChunkAnnotationUtils.copyUnsetAnnotations(sectionAnnotations, sentence);
		}
		String sectionEnd = sentenceEndToken.get(CoreAnnotations.SectionEndAnnotation.class);
		if (sectionEnd != null) {
			sectionAnnotations = null;
		}

		if (docID != null) {
			sentence.set(CoreAnnotations.DocIDAnnotation.class, docID);
		}

		int index = 1;
		for (CoreLabel token : sentenceTokens) {
			token.setIndex(index++);
			token.setSentIndex(sentenceIndex);
			if (docID != null) {
				token.setDocID(docID);
			}
		}
		return sentence;
	}
}
