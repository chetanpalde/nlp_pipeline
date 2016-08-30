package nlp;

import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import gate.Gate;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.InvalidFormatException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Class to load Models of stanford coreNLP and Apache OpenNLP
 * 
 * @author synerzip
 *
 */
class Models {
	private String workingDirectory = System.getProperty("user.dir");
	
	/**
	 * An Empty Constructor.
	 */
	Models() {}

	/**
	 * Function to load stanford corenlp models.
	 * 
	 * @return It return stanford pipeline and lexical parser.
	 */
	ArrayList<Object> loadStanfordModels() {
		ArrayList<Object> stanfordModels = new ArrayList<>();
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse");
		props.put("ner.model","edu/stanford/nlp/models/ner/english.muc.7class.distsim.crf.ser.gz");

		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		LexicalizedParser lexicalizedParser = LexicalizedParser
				.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
		stanfordModels.add(pipeline);
		stanfordModels.add(lexicalizedParser);
		return stanfordModels;
	}

	/**
	 * Function to call Apache OpenNLP to load its models.
	 * 
	 * @return It return Apache OpenNLP NER and sentence splitter models.
	 */
	ArrayList<Object> loadOpenNlpModels() {
		ArrayList<Object> openNlpModels = new ArrayList<Object>();
		ArrayList<NameFinderME> openNlpNerModels = loadOpenNlpNerModel();
		SentenceDetectorME openNlpSentenceModel = null;
		try {
			openNlpSentenceModel = loadOpenNlpSentenceSplitterModel();
		} catch (IOException e) {
			e.printStackTrace();
		}
		openNlpModels.add(openNlpSentenceModel);
		openNlpModels.add(openNlpNerModels);
		return openNlpModels;
	}
	/**
	 * Function to load Apache OpenNLP sentence splitter model.
	 * @return It returns sentence detector model of openNLP.
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	SentenceDetectorME loadOpenNlpSentenceSplitterModel() throws IOException {
		String filename = "models/opennlp_models/en-sent.bin";
		String absoluteFilePath = workingDirectory + File.separator + filename;
		FileInputStream modelIn = new FileInputStream(absoluteFilePath);
		SentenceModel model = new SentenceModel(modelIn);
		SentenceDetectorME sentenceDetectorME = new SentenceDetectorME(model);
		return sentenceDetectorME;
	}
	/**
	 * Function to read OpenNLP NER models.
	 * 
	 * @param modelName
	 *            - OpenNLP NER model name
	 * @return It returns token name finder model of openNLP.
	 * @throws IOException
	 */
	TokenNameFinderModel initializeOpenNlpNerModel(String modelName) throws IOException {
		String absoluteFilePath = workingDirectory + File.separator + modelName;
		InputStream inputStream = new FileInputStream(absoluteFilePath);
		TokenNameFinderModel model = new TokenNameFinderModel(inputStream);
		inputStream.close();
		return model;
	}
	/**
	 * Function to load OpenNLP NER models.
	 * 
	 * @return It returns loaded OpenNLP NER models.
	 */
	ArrayList<NameFinderME> loadOpenNlpNerModel() {
		String[] modelNames = {
				"models/opennlp_models/en-ner-person.bin", "models/opennlp_models/en-ner-date.bin",
				"models/opennlp_models/en-ner-location.bin","models/opennlp_models/en-ner-money.bin",
				"models/opennlp_models/en-ner-organization.bin","models/opennlp_models/en-ner-percentage.bin",
				"models/opennlp_models/en-ner-time.bin"
		};
		ArrayList<NameFinderME> allNameFinder = new ArrayList<NameFinderME>();
		for (String modelName : modelNames) {
			try {
				NameFinderME nameFinderME = new NameFinderME(initializeOpenNlpNerModel(modelName));
				allNameFinder.add(nameFinderME);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Apache OpenNLP NER models loaded..");
		return allNameFinder;
	}
	/**
	 * Function to load Gate Annie models.
	 */
	void loadGateAnnieNerModel() {
		if(!Gate.isInitialised()) {
			Gate.setGateHome(new File(workingDirectory + "/models/GATE_Developer_8.1"));
			try {
				Gate.init();
			} catch (Exception e) {
				System.out.println("Gate is already set...!!!");
			}
		}
	}
}
