package nlp;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import gate.util.GateException;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Class defining the seperate APIs
 *
 */
public class NLPApi extends Controller{
    Models nlpModels;
    ArrayList<Object> stanfordModels;
    ArrayList<Object> openNlpModels;
    StanfordCoreNLP pipeline;
    LexicalizedParser lexicalizedParser;
    ArrayList<NameFinderME> openNlpNerModels;
    SentenceDetectorME openNlpSentenceModel;

    public NLPApi(){
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
     * Function acts as plug and play into any other module for stanford dependency parsing.
     * @return It returns actual sentence along with their dependency parsing.
     */
    public Result getDeepParsingModule() {
        String text = extractInput();
        String finalOutput;
        if(text!=null) {
            ArrayList<Map<String, Object>> responseDeepParse = new ArrayList<Map<String, Object>>();
            NLPSentenceSplitter cSentenceSplitter = new NLPSentenceSplitter();
            List<CoreMap> cmSentences = cSentenceSplitter.getSentences(pipeline, openNlpSentenceModel, text);
            NLPDeepParsing cDeepParsing = new NLPDeepParsing();
            for (CoreMap cmSentence : cmSentences) {
                Map<String, Object> responseDp = new HashMap<String, Object>();
                responseDp.put("sentence", cmSentence.toString());
                responseDp.put("deep_parse", cDeepParsing.getDeepParsing(lexicalizedParser, cmSentence.toString()));
                responseDeepParse.add(responseDp);
            }
            Map<String,Object> output = new HashMap<String, Object>() ;
            output.put("deep_parsing",responseDeepParse);
            finalOutput = new Gson().toJson(output);
        }else{
            finalOutput = "WRONG INPUT PARAMETER. USE 'sentence' AS INPUT PARAMETER. NO OUTPUT GENERATED\n";
        }
        return ok(finalOutput);
    }
    /**
     * Function acts as plug and play into any other module for lemmatization.
     * @return It returns sentence with list of tokens.
     */
    public Result getLemmasModule() {
        String text = extractInput();
        String finalOutput;
        if(text!=null) {
            ArrayList<Map<String, Object>> responseLemmas = new ArrayList<Map<String, Object>>();
            NLPSentenceSplitter cSentenceSplitter = new NLPSentenceSplitter();
            List<CoreMap> cmSentences = cSentenceSplitter.getSentences(pipeline, openNlpSentenceModel, text);
            NLPLemmatizer cLemmatizer = new NLPLemmatizer();
            for (CoreMap cmSentence : cmSentences) {
                List<CoreLabel> tokens = cmSentence.get(CoreAnnotations.TokensAnnotation.class);
                Map<String, Object> responseLemma = new HashMap<String, Object>();
                responseLemma.put("sentence", cmSentence.toString());
                responseLemma.put("lemmas", cLemmatizer.getLemmas(tokens));
                responseLemmas.add(responseLemma);
            }
            Map<String,Object> output = new HashMap<String, Object>() ;
            output.put("lemmatizer",responseLemmas);
            finalOutput = new Gson().toJson(output);
        }else{
            finalOutput = "WRONG INPUT PARAMETER. USE 'sentence' AS INPUT PARAMETER. NO OUTPUT GENERATED\n";
        }
        return ok(finalOutput);
    }
    /**
     * Function acts as plug and play into any other module for named entity detection using GATE ANNIE.
     * @return It returns sentence with named entities.
     */
    public Result getNamedEntityGateAnnieModule() {
        String text = extractInput();
        String finalOutput;
        if(text!=null) {
            ArrayList<Map<String, Object>> responseNers = new ArrayList<Map<String, Object>>();
            NLPSentenceSplitter cSentenceSplitter = new NLPSentenceSplitter();
            List<CoreMap> cmSentences = cSentenceSplitter.getSentences(pipeline, openNlpSentenceModel, text);
            NLPNamedEntityGateAnnie cNamedEntityGateAnnie = new NLPNamedEntityGateAnnie();
            for (CoreMap cmSentence : cmSentences) {
                Map<String, Object> responseNer = new HashMap<String, Object>();
                responseNer.put("sentence", cmSentence.toString());
                try {
                    responseNer.put("ner", cNamedEntityGateAnnie.getNamedEntityGateAnnie(cmSentence));
                    responseNers.add(responseNer);
                } catch (GateException | IOException e) {
                    e.printStackTrace();
                }
            }
            Map<String,Object> output = new HashMap<String, Object>() ;
            output.put("gate_annie_ner",responseNers);
            finalOutput = new Gson().toJson(output);
        }else{
            finalOutput = "WRONG INPUT PARAMETER. USE 'sentence' AS INPUT PARAMETER. NO OUTPUT GENERATED\n";
        }
        return ok(finalOutput);
    }

    /**
     * Function acts as plug and play into any other module for named entity detection.
     * @return It returns sentence with named entities.
     */
    public Result getNamedEntityStanfordModule() {
        String text = extractInput();
        String finalOutput;
        if(text!=null) {
            ArrayList<Map<String, Object>> responseNers = new ArrayList<Map<String, Object>>();
            NLPSentenceSplitter cSentenceSplitter = new NLPSentenceSplitter();
            List<CoreMap> cmSentences = cSentenceSplitter.getSentences(pipeline, openNlpSentenceModel, text);
            NLPNamedEntityStanford cNamedEntityRecognizer = new NLPNamedEntityStanford();
            for (CoreMap cmSentence : cmSentences) {
                Map<String, Object> responseNer = new HashMap<String, Object>();
                responseNer.put("sentence", cmSentence.toString());
                responseNer.put("ner", cNamedEntityRecognizer.getNamedEntityRecognizer(cmSentence));
                responseNers.add(responseNer);
            }
            Map<String,Object> output = new HashMap<String, Object>() ;
            output.put("stanford_ner",responseNers);
            finalOutput = new Gson().toJson(output);
        }else{
            finalOutput = "WRONG INPUT PARAMETER. USE 'sentence' AS INPUT PARAMETER. NO OUTPUT GENERATED\n";
        }
        return ok(finalOutput);
    }
    /**
     * Function acts as plug and play into any other module for part of speech.
     * @return It returns sentence and part of speech tags on each word of that particular sentence..
     */
    public Result getPOSModule() {
        String text = extractInput();
        String finalOutput;
        if(text!=null) {
            ArrayList<Map<String, Object>> responsePoss = new ArrayList<Map<String, Object>>();
            NLPSentenceSplitter cSentenceSplitter = new NLPSentenceSplitter();
            List<CoreMap> cmSentences = cSentenceSplitter.getSentences(pipeline, openNlpSentenceModel, text);
            NLPPartOfSpeech cPartOfSpeech = new NLPPartOfSpeech();
            for (CoreMap cmSentence : cmSentences) {
                List<CoreLabel> tokens = cmSentence.get(CoreAnnotations.TokensAnnotation.class);
                Map<String, Object> responsePos = new HashMap<String, Object>();
                responsePos.put("sentence", cmSentence.toString());
                responsePos.put("pos", cPartOfSpeech.getPartOfSpeech(tokens));
                responsePoss.add(responsePos);
            }
            Map<String,Object> output = new HashMap<String, Object>() ;
            output.put("part_of_speech",responsePoss);
            finalOutput = new Gson().toJson(output);
        }else{
            finalOutput = "WRONG INPUT PARAMETER. USE 'sentence' AS INPUT PARAMETER. NO OUTPUT GENERATED\n";
        }
        return ok(finalOutput);

    }
    /**
     * Function acts as plug and play into any other module for sentence splitting.
      * @return It returns sentence with list of sentences.
     */
    public Result getSentencesModule() {
        String text = extractInput();
        String finalOutput;
        if(text!=null) {
        NLPSentenceSplitter cSentenceSplitter = new NLPSentenceSplitter();
        List<CoreMap> cmSentences = cSentenceSplitter.getSentences(pipeline, openNlpSentenceModel, text);

        ArrayList<Map<String,String>> output = new ArrayList<Map<String,String>>();
        for (CoreMap cmSentence : cmSentences) {
            Map<String,String> sentences = new HashMap<String,String>();
            sentences.put("sentence",cmSentence.toString());
            output.add(sentences);
        }
            Map<String,Object> sent = new HashMap<String, Object>() ;
            sent.put("sentences",output);
            finalOutput = new Gson().toJson(sent);
        }else{
            finalOutput = "WRONG INPUT PARAMETER. USE 'sentence' AS INPUT PARAMETER. NO OUTPUT GENERATED\n";
        }
        return ok(finalOutput);
    }
    /**
     * Function acts as plug and play into any other module for stanford tree parsing.
     * @return It returns actual sentence along with their tree parsing/shallow parsing..
     */
    public Result getShallowParseModule() {
        String text = extractInput();
        String finalOutput = null;
        if(text!=null) {
            ArrayList<Map<String, Object>> responseShallowParse = new ArrayList<Map<String, Object>>();
            NLPSentenceSplitter cSentenceSplitter = new NLPSentenceSplitter();
            List<CoreMap> cmSentences = cSentenceSplitter.getSentences(pipeline, openNlpSentenceModel, text);
            NLPShallowParsing cShallowParsing = new NLPShallowParsing();
            for (CoreMap cmSentence : cmSentences) {
                Map<String, Object> responseSp = new HashMap<String, Object>();
                responseSp.put("sentence", cmSentence.toString());
                responseSp.put("shallow_parse", cShallowParsing.getShallowParse(lexicalizedParser, cmSentence.toString()));
                responseShallowParse.add(responseSp);
            }
            Map<String,Object> output = new HashMap<String, Object>() ;
            output.put("shallow_parsing",responseShallowParse);
            finalOutput = new Gson().toJson(output);
        }else{
            finalOutput = "WRONG INPUT PARAMETER. USE 'sentence' AS INPUT PARAMETER. NO OUTPUT GENERATED\n";
        }
        return ok(finalOutput);
    }
    /**
     * Function acts as plug and play into any other module for tokenization.
     * @return It returns sentence with list of tokens.
     */
    public Result getTokensModule() {
        String text = extractInput();
        String finalOutput;
        if(text!=null) {
            ArrayList<Map<String, Object>> responseTokens = new ArrayList<Map<String, Object>>();
            NLPSentenceSplitter cSentenceSplitter = new NLPSentenceSplitter();
            List<CoreMap> cmSentences = cSentenceSplitter.getSentences(pipeline, openNlpSentenceModel, text);
            NLPTokenizer cTokenizer = new NLPTokenizer();
            for (CoreMap cmSentence : cmSentences) {
                Map<String, Object> responseToken = new HashMap<String, Object>();
                responseToken.put("sentence", cmSentence.toString());
                responseToken.put("tokens", cTokenizer.getTokens(cmSentence));
                responseTokens.add(responseToken);
            }
            Map<String, Object> output = new HashMap<String, Object>();
            output.put("tokenizer", responseTokens);
            finalOutput = new Gson().toJson(output);
        }else{
            finalOutput = "WRONG INPUT PARAMETER. USE 'sentence' AS INPUT PARAMETER. NO OUTPUT GENERATED\n";
        }
        return ok(finalOutput);
    }
    /**
     * Function acts as plug and play into any other module for named entity detection using Apache OpenNLP.
     * @return It returns sentence with named entities.
     */
    public Result getNamedEntityOpenNLPModule() {
        String text = extractInput();
        String finalOutput = null;
        if(text!=null) {
            ArrayList<Map<String, Object>> responseNers = new ArrayList<Map<String, Object>>();
            NLPNamedEntityOpenNLP nlpNamedEntityOpenNLP = new NLPNamedEntityOpenNLP();
            NLPSentenceSplitter cSentenceSplitter = new NLPSentenceSplitter();
            List<CoreMap> cmSentences = cSentenceSplitter.getSentences(pipeline, openNlpSentenceModel, text);
            for (CoreMap cmSentence : cmSentences) {
                List<CoreLabel> tokens = cmSentence.get(CoreAnnotations.TokensAnnotation.class);
                Map<String, Object> responseNer = new HashMap<String, Object>();
                responseNer.put("sentence", cmSentence.toString());
                responseNer.put("ner", nlpNamedEntityOpenNLP.getNamedEntityOpenNLP(openNlpNerModels, tokens));
                responseNers.add(responseNer);
            }
            Map<String, Object> output = new HashMap<String, Object>();
            output.put("opennlp_ner", responseNers);
            finalOutput = new Gson().toJson(output);
        }else{
            finalOutput = "WRONG INPUT PARAMETER. USE 'sentence' AS INPUT PARAMETER. NO OUTPUT GENERATED\n";
        }
        return ok(finalOutput);
    }
    /**
     * Method used to extract text and text parameter and returns the text data
     * @return text
     */
    private String extractInput(){
        String text = null;
        JsonNode json = request().body().asJson();
        String inputParameter = json.fieldNames().next();
        if(inputParameter.equals("sentence")){
            text = json.get(inputParameter).textValue();
        }else{
            System.out.println("WRONG INPUT PARAMETER. USE 'sentence' AS INPUT PARAMETER. NO OUTPUT GENERATED\n");
        }
        return text;
    }
}
