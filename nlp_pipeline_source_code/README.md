#Nlp-Pipeline.jar
	The NLP-Pipeline project provides variours APIs related to NLP. We have provided following APIs to our users:
	1.Sentence Splitter
	2.Tokenizer
	3.Lemmatizer
	4.Part of speech
	5.Shallow Parser
	6.Deep Parser
	7.Named Entity Recognizers 
		A. Gate Annie Named Entity Recognizer 
		B. Apache OpenNLP Named Entity Recognizer 
		C. Stanford Named Entity Recognizer


#Set Up:
		1. Download the nlp_pipeline_source_code project.
		2. Make a Jar file from the project as mentioned in the fields below.
		3. After the creation of the jar file, copy the models folder present in the nlp_pipeline_source_code folder and 
		paste it in the directory depending on your use as mentioned in 'How to use it'.
		If you do not keep the models folder on required location, it wont compile.
		3. To run nlp_pipeline.jar, you need Oracle Java 8 to be installed.
		After you follow these instructions, you are ready to go furthur.

#How to make nlp_pipeline.jar:
		1.Download the nlp_pipeline_source_code.
		2.Open Eclipse -->File --> Open Projects from File System --> Directory --> Give the location of downloaded nlp_pipeline_source_code --> De-select the 'Search for nested projects' --> You will see the NLP-pipeline Project be selected --> Finish.
		3.Run the project once so that you will be sure about the setup is correct or not.
		4.Now, to make .jar file, go to File --> Export --> Java --> Runnable Jar File --> Next --> 
		In Launch configurations, select Main-NLP_pipeline --> Select export destination --> Select Extract Required Libraries into generated JAR --> Finish --> Ok 
		Ignore if some warning occours.
		5. Now, your Jar file is generated on the defined location.
#How to use it:
	#1.Using nlp_pipeline.jar into your project:
		1. Place the models folder in your project directory where the src and bin folders are. 
		2. Now go to your IDE and Right click on your project --> Build Path --> Configure Build Path --> 
		Libraries --> Add External Jars --> Browse the jar and press ok. It will load the jar in your project.
		3. To use the methods of this jar, you need to set up an NlpApi Object as follows:
				NLPApi nlpApi = new NLPApi();
	Now you can access the following methods using the nlpApi object.

		1. Sentence Splitter			String getSentences(String input){}
		2. Tokenizer					String getTokens(String input){}
		3. Lemmatizer					String getLemmas(String input){}
		4. Part of speech				String getPartsOfSpeech(String input){}
		5. Shallow Parser				String shallowParse(String input){}
		6. Deep Parser					String deepParse(String input){}
		7. Named Entity Recognizers(NER) 
			a.Gate Annie NER			String getGateAnnieNer(String input){}
			b.Apache OpenNLP NER		String getOpenNlpNer(String input){}
			c.Stanford NER				String getStanfordNer(String input){}
			
			Note that the return type of methods is given as String which is in Json format mentioned in detail in Breif description section.

	#2. Using nlp_pipeline.jar through command line: 
		Make sure that you have your models folder in the same directory in which jar file is kept.
		You can also run the jar from the command line. There are two scenearios:
		a. You provide a text file as input:
			Run the following command:
				java -jar nlp_pipeline.jar <your file path>
			eg: java -jar nlp_pipeline.jar /home/input.txt
				
		b. Console based application:
			Run the jar by using following command:
				java -jar nlp_pipeline.jar
			You will get : 'Enter the sentence: ' as output. Provide a sentence and press Enter.
				
		The output through the command line argument would be the merged version of all the above mentioned functions in the Json format.
			

#Brief Description
The following are the APIs provided which you can use independently: 

#1. Sentence Splitter 
	The sentence splitter splits the input string into number of sentences. The splitting is done with respect to the fullstop. The method accepts an input string and it returns the output sentences in following JSON format. Here, for sentence splitting, we have used Apache OpenNlp library.
		
	Method:
		String getSentences(String input){
		
		}
	
	Input String: Everyone loves NLP because its cool. Everyone should learn it. You will enjoy it.
	
	Output: 
	
		Sentence1:Everyone loves NLP because its cool.
		Sentence2:Everyone should learn it.
		Sentence3:You will enjoy it.
	
	The output would be in the following JSON format:
	{"sentences":[{"sentence":""},{"sentence":""}]}

#2. Tokenizer
	The tokenizer splits the sentences in atomic entities known as tokens. The following method accepts a String of data and return the tokens in the following JSON format.
	You can easily use this json output and retrieve the required data. The begin_position and end_position states the begin position and the end position of the words in that sentence respectively. Here, the input string is splitted into two sentences with respect to the fullstop by the use of sentence splitter. Here, for tokenization, we have used Stanford CoreNLP library.
	Method:
		String getTokens(String input){
		
		}
		
		
	Input String: “Barak Obama is the President of the United States of America”
	
	Output: 
	
	Token1:Barak
	Token2:Obama
	Token3:is
	Token4:the
	Token5:President
	Token6:of
	Token7:the
	Token8:United
	Token9:States
	Token10:of
	Token11:America
	
	The output would be in the following JSON format:
	{"tokenizer":[{"sentence":"<Your sentence>","tokens":[{"begin_position":,"end_position":,"token":""},{"begin_position":,"end_position":,"token":""}]}]}



#3. Lemmatizer
	Lemmatization in linguistics is the process of grouping together the different inflected forms of a word so they can be analysed as a single item. Lets consider an example of the words compares, comparing and  compare. All the three words may look dissimilar but have a comman entity i.e. compare. This comman entity i.e. compare , is known as Lemma. And the process of retrieving  such lemmas from data is known as lemmatization.  Here, for lemmatization, we have used Stanford CoreNLP library.
	
	Method:	
		String getLemmas(String input){
		
		}
		
		
	Input String: Everyone loves NLP because its cool
	
	Output:
	
	Word1:Everyone	lemma1:everyone
	Word2:loves		lemma2:love
	Word3:NLP		lemma3:nlp
	Word4:because	lemma4:because
	Word5:its		lemma5:its
	Word6:cool		lemma6:cool
	
	The output would be in the following JSON format:
	{"lemmatizer":[{"sentence":"","lemmas"[{"lemma":"","begin_position":,"word":"","end_position":}]}]}

#4. Part of speech 
	Part of speech module analyses the input string and returns the JSON format of the part of speech entities. The input string is splitted into sentences and for each sentence, part of speech is analysed for every word.  Here, for part of speech, we have used Stanford CoreNLP library.There are approximately 36 different parts of speech given below.
	
	POS Labels: 
	 
	CC Coordinating conjunction 
	CD Cardinal number 
	DT Determiner 
	EX Existential there 
	FW Foreign word 
	IN Preposition or subordinating conjunction 
	JJ Adjective 
	JJR Adjective, comparative 
	JJS Adjective, superlative 
	LS List item marker 
	MD Modal 
	NN Noun, singular or mass 
	NNS Noun, plural 
	NNP Proper noun, singular 
	NNPS Proper noun, plural 
	PDT Predeterminer 
	POS Possessive ending 
	PRP Personal pronoun 
	PRP$ Possessive pronoun 
	RB Adverb 
	RBR Adverb, comparative 
	RBS Adverb, superlative 
	RP Particle 
	SYM Symbol 
	TO to 
	UH Interjection 
	VB Verb, base form 
	VBD Verb, past tense 
	VBG Verb, gerund or present participle 
	VBN Verb, past participle 
	VBP Verb, non­3rd person singular present 
	VBZ Verb, 3rd person singular present 
	WDT Wh­determiner 
	WP Wh­pronoun 
	WP$ Possessive wh­pronoun 
	WRB Wh­adverb
	
	Method:
		String getPartsOfSpeech(String input){
		
		}
				
	Input String: Barak Obama is the President of the United States of America
	
	Output:
	 
	Token1:Barak		pos1:NNP
	Token2:Obama		pos2:NNP
	Token3:is			pos3:VBZ
	Token4:the			pos4:DT
	Token5:President	pos5:NNP
	Token6:of			pos6:IN
	Token7:the			pos7:DT
	Token8:United		pos8:NNP
	Token9:States		pos9:NNPS
	Token10:of			pos10:IN
	Token11:America		pos11:NNP
	
	The output would be in the following JSON format:
	{"part_of_speech":[{"sentence":"<your sentence>","pos":[{"pos":"","begin_position":,"end_position":,"token":""},{"pos":"","begin_position":,"end_position":,"token":""}]}]}


#5. Shallow Parser 

		 Shallow parsing refers to chunking of data. This data is divided into the following chunks: 
	
			1. Noun Phrase(NP):  a word or group of words containing a noun and functioning in a sentence as subject, object, or prepositional object.
			2. Verb Phrase(VP):   a verb with another word or words indicating tense, mood, or person.
			3. Preposition Phase(PP): Every prepositional phrase is a series of words made up of a preposition and its object. The object may be a noun, pronoun, gerund or c clause. A prepositional 						phrase functions as an adjective or adverb.
	
	Here, for shallow parsing, we have used Stanford CoreNLP library. The method takes an input string as parameter and returns the output in the following JSON format.
	
	Method:
		String shallowParse(String input){

		}
		
		
	Input text: Barak Obama is the President of the United States of America
	
	Output:
	
	Phrase1:Barak Obama											label1:NP
	Phrase2:is the President of the United States of America	label2:VP
	Phrase3:the President of the United States of America		label3:NP
	Phrase4:the President										label4:NP
	Phrase5:of the United States								label5:PP
	Phrase6:the United States									label6:NP
	Phrase7:of America											label7:PP
	Phrase8:America												label8:NP
	
	The output would be in the following JSON format:
	{"shallow_parsing":[{"sentence":"","shallow_parse":[{"phrase":"","label":""}]}]}
	
#6. Deep Parser 

	Deep parsing is the process of retrieving meaningful gramatical relations between the governor and the dependent. 	Deep parsing Api accepts an input string and returns the result in following JSON format.  Here, for deep parsing, we have used Stanford CoreNLP library. It retrieves the following entities per sentence:

	1. governor	
		Also known as a regent or a head. The gramatical relation is between governor and the dependent.
	2. governor_index
		The governor_index refers to the indexed position of the governor in the sentence.
	It starts from 1.

	3. dependent_index
		The dependent_index refers to the indexed position of the dependent in the sentence. It starts from 1.

	4. dependent
		Dependent will be used when a more precise relation in the hierarchy does not exist 
	or cannot be retrieved by the system.

	5. relation
		There are approximately 50 gramatical  relations possible between the governor and the dependent. The following is the list of relations with heirarchy:

	root - root 
	dep - dependent 
		aux - auxiliary 
			auxpass - passive auxiliary 
			cop - copula 
		arg - argument 
			agent - agent 
			comp - complement 
				acomp - adjectival complement 
				ccomp - clausal complement with internal subject 
				xcomp - clausal complement with external subject 
				obj - object 
					dobj - direct object 
					iobj - indirect object 
					pobj - object of preposition 
			subj - subject 
				nsubj - nominal subject 
					nsubjpass - passive nominal subject 
			csubj - clausal subject 
				csubjpass - passive clausal subject 
		cc - coordination 
		conj - conjunct 
		expl - expletive (expletive “there”) 
		mod - modifier 
			amod - adjectival modifier 
			appos - appositional modifier 
			advcl - adverbial clause modifier 
			det - determiner 
			predet – predeterminer
			preconj - preconjunct 
			vmod - reduced, non-finite verbal modifier 
			mwe - multi-word expression modifier 
				mark - marker (word introducing an advcl or ccomp 
			advmod - adverbial modifier 
				neg - negation modifier 
			rcmod - relative clause modifier 
			quantmod - quantifier modifier 
			nn - noun compound modifier 
			npadvmod - noun phrase adverbial modifier 
				tmod - temporal modifier 
			num - numeric modifier 
			number - element of compound number 
			prep - prepositional modifier 
			poss - possession modifier 
			possessive - possessive modifier (’s) 
			prt - phrasal verb particle 
		parataxis - parataxis 
		goeswith - goes with 
		punct - punctuation 
		ref - referent 
		sdep - semantic dependent 
			xsubj - controlling subject

	
	
		String deepParse(String input){
		
		}
	
	
	Input text: Barak Obama is the President of the United States of America
	
	Output:
	
	governor1:Obama			dependent1:Barak		relation1:nn
	governor2:President		dependent2:Obama		relation2:nsubj
	governor3:President		dependent3:is			relation3:cop
	governor4:President		dependent4:the			relation4:det
	governor5:ROOT			dependent5:President	relation5:root
	governor6:States		dependent6:the			relation6:det
	governor7:States		dependent7:United		relation7:nn
	governor8:President		dependent8:States		relation8:prep_of
	governor9:President		dependent9:America		relation9:prep_of
	
	
	The output would be in the following JSON format:
	{"deep_parsing":[{"sentence":","deep_parse":[{"governor":"","governor_index":,"dependent_index":,"dependent":"","relation":""}]}]}

#7. Named Entity Recognizer(NER)
	Named-entity recognition (NER) is a subtask of information extraction that seeks to locate and classify named entities in text into pre-defined categories such as the names of persons, organizations, locations, expressions of times, quantities, monetary values, percentages, etc.
	Here we are providing three different NERs with different capabilities:

#(A) Gate Annie Named Entity Recognizer 
	The Gate Annie Named Entity Recognizer module is capable of recognizing entities such as:
		1. Person
		2. Location
		3. Organization
		4. Date
		5. Money
	For Named Entity Recognization, here we have used Gate Annie NER library.	
	It takes string as an input and returns the output of the mentioned entities in the  following JSON format.

	Method:
		String getGateAnnieNer(String input){
		
		}
		
		
	Input text: Sachin Tendulkar is regarded as the god of cricket. Mr.Barak Obama is the President of United States. Its Narendra Modi who is holding the title of Prime minister of India. Delhi is capital of 		    India. Indias independence day is on 15th August 1947. India donated Pakistan $60 crore for rehabitation. Huge companies such as Intel, Google, etc. are setting their foots in India. I got 		    85% in 10th grade
	
	Output:
	
	Phrase1:Mr.Barak			label1:Person
	Phrase2:Obama				label2:Person
	Phrase3:United States		label3:Location
	Phrase4:India				label4:Location
	Phrase5:Delhi				label5:Location
	Phrase6:India				label6:Location
	Phrase7:15th August 1947	label7:Date
	Phrase8:$60					label8:Money
	Phrase9:India				label9:Location
	Phrase10:Pakistan			label10:Location
	Phrase11:Google				label11:Organization
	Phrase12:India				label12:Location
	Phrase13:Intel				label13:Organization
	
	The output would be in the following JSON format:
	{"gate_annie_ner":[{"sentence":"","ner":[{"phrase":"","label":""},{"phrase":"","label":""}]}
	
#(B) Named Entity Open NLP 
	The Named Entity Recognizer by Apache's Open NLP detects the named entities and numbers in the text. The following are the entities recognized by this method: 
		1.Location
		2.Person
		3.Organization
		4.Money 
		5.Percentage
		6.Date
		7.Time 
	The method takes string as input and returns the output in the following JSON format.
	
	Method:
		String getOpenNlpNer(String input){
		
		}
	
	Input text: Sachin Tendulkar is regarded as the god of cricket. Mr.Barak Obama is the President of United States. Its Narendra Modi who is holding the title of Prime minister of India. Delhi is capital of India. Indias independence day is on 15th August 1947. India donated Pakistan $60 crore for rehabitation. Huge companies such as Intel, Google, etc. are setting their foots in India. I got 85% in 10th grade.
	
	Output: 
	
	
	Phrase1:Mr.Barak Obama	label1:person
	Phrase2:United States	label2:location
	Phrase3:India			label3:location
	Phrase4:India			label4:location
	Phrase5:August 1947		label5:date
	Phrase6:India			label6:location
	Phrase7:Pakistan		label7:location
	Phrase8:$ 60			label8:money
	Phrase9:India			label9:location
	Phrase10:Intel			label10:organization
	Phrase11:85 %			label11:percentage
	
	The output would be in the following JSON format:
	{"opennlp_ner":[{"sentence":"","ner":[{"phrase":"","label":""},{"phrase":"","label":""}]}]}
	
	
#(C) Stanford Named Entity Recognizer Module
	Here, for Named Entity Recognization, we have used the Stanford CoreNlp library of class 7.
	The Stanford Named Entity Recognizer Module of class 7 detects the following entities in the sentence:
		1.Location
		2.Person
		3.Organization
		4.Money 
		5.Percent
		6.Date
		7.Time 
	The method takes an input string and returns the output in the following JSON format. 
	
	Method:	
		String getStanfordNer(String input){
		
		}
	Input text: Sachin Tendulkar is regarded as the god of cricket. Mr.Barak Obama is the President of United States. Its Narendra Modi who is holding the title of Prime minister of India. Delhi is capital of India. Indias independence day is on 15th August 1947. India donated Pakistan $60 crore for rehabitation. Huge companies such as Intel, Google, etc. are setting their foots in India. I got 85% in 10th grade. I knew about it at 1:30 PM.
	
	Output: 
	
	Phrase1:Sachin Tendulkar	label1:LOCATION
	Phrase2:Obama				label2:PERSON
	Phrase3:United States		label3:LOCATION
	Phrase4:Narendra Modi		label4:PERSON
	Phrase5:India				label5:LOCATION
	Phrase6:India				label6:LOCATION
	Phrase7:15th August 1947	label7:DATE
	Phrase8:India				label8:LOCATION
	Phrase9:Pakistan			label9:LOCATION
	Phrase10:$ 60				label10:MONEY
	Phrase11:Intel				label11:ORGANIZATION
	Phrase12:Google				label12:ORGANIZATION
	Phrase13:India				label13:LOCATION
	Phrase14:85 %				label14:PERCENT
	Phrase15:1:30 PM			label15:TIME
	
	The output would be in the following JSON format: 
	{"stanford_ner":[{"sentence":"","ner":[{"phrase":"","label":""},{"phrase":"","label":""}]}]}

