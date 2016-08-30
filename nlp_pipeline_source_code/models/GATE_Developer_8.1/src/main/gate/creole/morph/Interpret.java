package gate.creole.morph;

import gate.creole.ResourceInstantiationException;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * <p>
 * Title: Interpret.java
 * </p>
 * <p>
 * Description: This is the main class which which should be invoked to load the
 * rule file in the system and then to execute the program to find the root word
 * and the affix to it.
 * </p>
 */
public class Interpret {

	/**
	 * instance of the ReadFile class which reads the file and stores each line
	 * of the given program in the arraylist which can be read using different
	 * methods of the ReadFile class
	 */
	private ReadFile file;

	/** Boolean variables to keep track on which section is being read */
	private boolean isDefineVarSession, isDefineRulesSession;

	/** Instance of Storage class, which is used store all the variables details */
	private Storage variables;

	/** This variables keeps the record of available methods for the morphing */
	private Method[] methods;

	/** This variables holds the affix */
	private String affix;

	private Pattern vPat = Pattern.compile("((VB)[DGNPZ]?)|(MD)");

	private Pattern nPat = Pattern.compile("(NN)(S)*");

	MorphFunctions morphInst;

	List<Pattern> patterns = new ArrayList<Pattern>();
	List<List<CharClass>> fsms = new ArrayList<List<CharClass>>();
	
	/**
	 * The initial state of the FSM that backs this morpher
	 */
	protected FSMState initialState;

	//protected Set lastStates;

	/**
	 * It starts the actual program
	 */
	public void init(URL ruleFileURL) throws ResourceInstantiationException {
		variables = new Storage();
		prepareListOfMorphMethods();
		file = new ReadFile(ruleFileURL);
		affix = null;
		isDefineRulesSession = false;
		isDefineVarSession = false;
		morphInst = new MorphFunctions();

		readProgram();
		initialState = new FSMState(-1);
		
		//lastStates = new HashSet();
		interpretProgram();

		variables = null;
		file = null;
		//lastStates = null;
	}
	
	/**
	 * Initialize this Interpret by copying pointers to the sharable state
	 * of an existing Interpret instance.
	 */
	public void init(Interpret existingInterpret) {
    affix = null;
    isDefineRulesSession = false;
    isDefineVarSession = false;
    morphInst = new MorphFunctions();
    
    // copy shared state
    fsms = existingInterpret.fsms;
    patterns = existingInterpret.patterns;
    initialState = existingInterpret.initialState;
	}

	class CharClass {
		char ch;
		FSMState st;
	}
	
	public void addState(char ch, FSMState fsm, int index) {
		if(index == fsms.size()) {
			fsms.add(new ArrayList<CharClass>());
		}
		
		List<CharClass> fs = fsms.get(index);
		for(int i=0;i<fs.size();i++) {
			CharClass cc = fs.get(i);
			if(cc.ch == ch)
				return;
		}
		
		CharClass cc = new CharClass();
		cc.ch = ch;
		cc.st = fsm;
		fs.add(cc);
	}

	
	public FSMState getState(char ch, int index) {
		if(index >= fsms.size()) return null;
		List<CharClass> fs = fsms.get(index);
		for(int i=0;i<fs.size();i++) {
			CharClass cc = fs.get(i);
			if(cc.ch == ch)
				return cc.st;
		}
		return null;
	}
	
	private Set<FSMState> getStates(char ch, Set<FSMState> states) {
		Set<FSMState> newStates = new HashSet<FSMState>();
		Iterator<FSMState> iter = states.iterator();
		while (iter.hasNext()) {
			FSMState st = iter.next();
			FSMState chState = st.next(ch, FSMState.CHILD_STATE);
			if (chState != null) {
				newStates.add(chState);
			}

			FSMState adState = st.next(ch, FSMState.ADJ_STATE);
			if (adState != null) {
				newStates.add(adState);
			}
		}
		return newStates;
	}

	private boolean validCategory(String category) {
		if (category.equals("*")) {
			return true;
		} else if (vPat.matcher(category).matches()) {
			return true;
		} else if (nPat.matcher(category).matches()) {
			return true;
		}
		return false;
	}
	
	/**
	 * @return set of the Lookups associated with the parameter
	 */
	public String runMorpher(String word, String category) {
		affix = null;
		if(!validCategory(category)) {
			return word;
		}
		
		foundRule = false;
		Set<FSMState> states = new HashSet<FSMState>();
		states.add(initialState);
		for (int i = 0; i < word.length(); i++) {
			char ch = word.charAt(i);
			states = getStates(ch, states);
			if (states.isEmpty()) {
				return word;
			}

		}

		// we have all states here
		// we obtain all RHSes
		SortedSet<RHS> rhses = new TreeSet<RHS>(new Comparator<RHS>() {
      @Override
      public int compare(RHS r1, RHS r2) {
        return r1.getPatternIndex() - r2.getPatternIndex();
      }
    });
    
		Iterator<FSMState> iter = states.iterator();
		while (iter.hasNext()) {
			FSMState st = iter.next();
			rhses.addAll(st.getRHSes());
		}

		if (rhses.isEmpty()) {
			return word;
		}

		return executeRHSes(rhses, word, category);
	}

	protected int patternIndex = -1;
	public int getPatternIndex() {
	  return patternIndex;
	}
	
	protected String executeRHSes(SortedSet<RHS> rhses, String word, String category) {
    foundRule = false;
    // rhses are in sorted order
    // we need to check if the word is compatible with pattern
    Iterator<RHS> rhsiter = rhses.iterator();
    while (rhsiter.hasNext()){
      RHS r1 = rhsiter.next();
      String answer = executeRHS(word, category, r1);
      
      if (foundRule) {
        patternIndex = r1.getPatternIndex();
        return answer;
      }
    }
    return word;
	}
	
	protected boolean foundRule = false;

	protected String executeRHS(String word, String category, RHS rhs) {
		if (category.equals("*")) {
			return executeRule(word, rhs);
		} else if (rhs.isVerb() && vPat.matcher(category).matches()) {
			return executeRule(word, rhs);
		} else if (rhs.isNoun() && nPat.matcher(category).matches()) {
			return executeRule(word, rhs);
		}
		return word;
	}

	private String executeRule(String word, RHS rhs) {
		Pattern p = patterns.get(rhs.getPatternIndex());

		short methodIndex = rhs.getMethodIndex();
		if (!p.matcher(word).matches()) {
			foundRule = false;
			return word;
		}

		// call the appropriate function
		String[] parameters = rhs.getParameters();

		// set the given word in that morph program
		morphInst.setInput(word);
		String answer = null;
		switch (methodIndex) {
		case ParsingFunctions.IRREG_STEM:
			answer = morphInst.irreg_stem(parameters[0], parameters[1]);
			break;
		case ParsingFunctions.NULL_STEM:
			answer = morphInst.null_stem();
			break;
		case ParsingFunctions.SEMIREG_STEM:
			answer = morphInst.semi_reg_stem(Integer.parseInt(parameters[0]),
					parameters[1]);
			break;
		case ParsingFunctions.STEM:
			answer = morphInst.stem(Integer.parseInt(parameters[0]),
					parameters[1], parameters[2]);
			break;
		default:
			answer = null;
			break;
		}
		
		if(answer != null) {
			this.affix = morphInst.getAffix();
			foundRule = true;
			return answer;
		} else {
			foundRule = false;
			return word;
		}
	}

	/**
	 * This method prepares the list of available methods in the MorphFunctions
	 * class
	 */
	private void prepareListOfMorphMethods()
			throws ResourceInstantiationException {
		methods = MorphFunctions.class.getDeclaredMethods();
	}

	/**
	 * read the program file
	 */
	private void readProgram() throws ResourceInstantiationException {
		// read the program file
		boolean readStatus = file.read();

		// check if read was success
		if (!readStatus) {
			// not it wasn't so simply display the message and ask user to check
			// it
			generateError("Some errors reading program file.. please check the"
					+ "program and try again");
		}
	}

	/**
	 * This method reads each line of the program and interpret them
	 */
	private void interpretProgram() throws ResourceInstantiationException {
		// read each line and parse it
		while (file.hasNext()) {
			String currentLine = file.getNext();

			if (currentLine == null || currentLine.trim().length() == 0) {
				continue;
			}

			// remove all the leading spaces
			currentLine = currentLine.trim();

			/*
			 * if commandType is 0 ==> defineVars command if commandType is 1
			 * ==> defineRules command if commandType is 2 ==> variable
			 * declaration if commandType is 3 ==> rule declaration otherwise //
			 * unknown generate error
			 */
			int commandType = findCommandType(currentLine);
			switch (commandType) {
			case -1:
				// comment command
				continue;
			case 0:
				// defineVars command
				defineVarsCommand();
				break;
			case 1:
				// defineRules command
				defineRulesCommand();
				break;
			case 2:
				// variable declaration
				variableDeclarationCommand(currentLine);
				break;
			case 3:
				// rule declaration
				ruleDeclarationCommand(currentLine);
				break;
			default:
				generateError("Syntax Error at line " + file.getPointer()
						+ " : " + currentLine);
				break;
			}
		} // end while
	}

	/**
	 * This method interprets the line and finds out the type of command and
	 * returns the integer indicating the type of the command
	 * 
	 * @param line
	 *            The program command to be interpreted
	 * @return and <tt>int</tt> value
	 */
	private int findCommandType(String line) {

		// check for the comment command
		if (line.substring(0, 2).equals("//") || line.charAt(0) == '#') {
			return -1;
		} else if (line.equals("defineVars")) {
			return 0;
		} else if (line.equals("defineRules")) {
			return 1;
		} else if (isDefineVarSession && line.split("==>").length == 2) {
			return 2;
		} else if (isDefineRulesSession &&
		/*
		 * (line.charAt(0) == '{' || line.charAt(0) == '[' || line.charAt(0) ==
		 * '(' || line.charAt(0) == '\"')
		 */(line.charAt(0) == '<') && line.split("==>").length == 2) {
			return 3;
		} else {
			return Codes.ERROR_CODE;
		}
	}

	/**
	 * This method processes the command to define the variable section
	 */
	private void defineVarsCommand() throws ResourceInstantiationException {

		// variable section can only be defined once
		if (isDefineVarSession) {
			generateError("Variable Section already defined - " + "see line "
					+ file.getPointer());
		} else if (isDefineRulesSession) {
			generateError("Variable section must be declared before the Rule "
					+ "Section - see line " + file.getPointer());
		} else {
			isDefineVarSession = true;
		}
	}

	/**
	 * This method processes the command to define the rule section
	 */
	private void defineRulesCommand() throws ResourceInstantiationException {
		if (isDefineRulesSession) {
			generateError("Rule Section already defined - see " + "line "
					+ file.getPointer());
		} else {
			isDefineVarSession = false;
			isDefineRulesSession = true;
		}
	}

	/**
	 * This method processes the command to declare the variable
	 * 
	 * @param line
	 */
	private void variableDeclarationCommand(String line)
			throws ResourceInstantiationException {
		// ok so first find the variable name and the value for it
		String varName = (line.split("==>"))[0].trim();
		String varValue = (line.split("==>"))[1].trim();

		// find the type of variable it is
		int valueType = ParsingFunctions.findVariableType(varValue
				.trim());
		if (valueType == Codes.ERROR_CODE) {
			generateError(varName + " - Variable Syntax Error - see " + "line"
					+ file.getPointer() + " : " + line);
		}

		// based on the variable type create the instance
		Variable varInst = null;
		switch (valueType) {
		case Codes.CHARACTER_RANGE_CODE:
			varInst = new CharacterRange();
			break;
		case Codes.CHARACTER_SET_CODE:
			varInst = new CharacterSet();
			break;
		case Codes.STRING_SET_CODE:
			varInst = new StringSet();
			break;
		}

		// set the values in the variable
		if (!varInst.set(varName, varValue)) {
			generateError(varName
					+ " - Syntax Error while assigning value to the "
					+ "variable - see line" + file.getPointer() + " : " + line);
		}

		// and finally add the variable in
		if (!variables.add(varName, varInst.getPattern())) {
			generateError(varName.trim() + " - Variable already defined - see "
					+ "line " + file.getPointer() + " : " + line);
		}

		varInst.resetPointer();
	}

	/**
	 * This method processes the command to declare the rule
	 * 
	 * @param line
	 */
	private void ruleDeclarationCommand(String line)
			throws ResourceInstantiationException {
		// lets divide the rule into two parts
		// LHS and RHS.
		// LHS is a part which requires to be parsed and
		// RHS should be checked for the legal function name and valid arguments
		// we process RHS first and then the LHS
		String[] ruleParts = line.split("==>");
		if (ruleParts.length != 2) {
			generateError("Error in declaring rule at line : "
					+ file.getPointer() + " : " + line);
		}

		// now check if the method which has been called in this rule actually
		// available in the MorphFunction Class
		String methodCalled = ruleParts[1].trim();
		if (!isMethodAvailable(methodCalled)) {

			// no method is not available so print the syntax error
			generateError("Syntax error - method does not exists - see "
					+ "line " + file.getPointer() + " : " + line);
		}

		// so RHS part is Ok
		// now we need to check if LHS is written properly
		// and convert it to the pattern that is recognized by the java
		String category = "";
		// we need to find out the category
		int i = 1;
		for (; i < ruleParts[0].length(); i++) {
			if (ruleParts[0].charAt(i) == '>')
				break;
			category = category + ruleParts[0].charAt(i);
		}

		if (i >= ruleParts[0].length()) {
			generateError("Syntax error - pattern not written properly - see "
					+ "line " + file.getPointer() + " : " + line);
		}

		RHS rhs = new RHS(ruleParts[1], category, (short)patterns.size());
		ruleParts[0] = ruleParts[0].substring(i + 1, ruleParts[0].length())
				.trim();
		String regExp = ParsingFunctions.convertToRegExp(
				ruleParts[0], variables);
		patterns.add(Pattern.compile(regExp));
		String[] rules = ParsingFunctions.normlizePattern(regExp);
		for (int m = 0; m < rules.length; m++) {
			Set<Set<FSMState>> lss = new HashSet<Set<FSMState>>();
			lss.clear();
			Set<FSMState> newSet = new HashSet<FSMState>();
			newSet.add(initialState);
			lss.add(newSet);
			PatternPart parts[] = ParsingFunctions
					.getPatternParts(rules[m].trim());
			for (int j = 0; j < parts.length; j++) {
				lss = ParsingFunctions.createFSMs(parts[j].getPartString(), parts[j].getType(), lss, this);
			}
			Iterator<Set<FSMState>> iter = lss.iterator();
			while (iter.hasNext()) {
				Set<FSMState> set = iter.next();
				Iterator<FSMState> subIter = set.iterator();
				while (subIter.hasNext()) {
					FSMState st = subIter.next();
					st.addRHS(rhs);
				}
			}
		}
		//drawFSM();
	}

	@SuppressWarnings("unused")
  private Set<FSMState> intersect(Set<FSMState> a, Set<FSMState> b) {
		Set<FSMState> result = new HashSet<FSMState>();
		Iterator<FSMState> iter = a.iterator();
		while (iter.hasNext()) {
			FSMState st = iter.next();
			if (b.contains(st)) {
				result.add(st);
			}
		}
		return result;
	}

	@SuppressWarnings("unused")
  private void drawFSM() {
		// we start with initialState
		System.out.println("Initial:");
		String space = "";
		drawFSM(initialState, space);
	}

	private void drawFSM(FSMState st, String space) {
		CharMap map = st.getTransitionFunction();
		char[] keys = map.getItemsKeys();
		if (keys != null) {
			System.out.println(space + "Child:");
			for (int i = 0; i < keys.length; i++) {
				System.out.println(space + "'" + keys[i] + "':");
				drawFSM(map.get(keys[i], FSMState.CHILD_STATE), space + "  ");
			}
		}
		keys = map.getAdjitemsKeys();
		if (keys != null) {
			System.out.println("ADJ:");
			for (int i = 0; i < keys.length; i++) {
				System.out.println(space + "'" + keys[i] + "' :");
				// drawFSM(map.get(keys[i], FSMState.ADJ_STATE), space+" ");
			}
		}
	}

	/**
	 * This method takes a method signature and searches if the method
	 * 
	 * @param method
	 * @return a <tt>boolean</tt> value.
	 */
	private boolean isMethodAvailable(String method) {
		// now first find the name of the method
		// their parameters and their types
		int index = method.indexOf("(");
		if (index == -1 || index == 0
				|| method.charAt(method.length() - 1) != ')') {
			return false;
		}

		String methodName = method.substring(0, index);
		// now get the parameters

		String[] parameters;
		int[] userMethodParams;

		String arguments = method.substring(index + 1, method.length() - 1);
		if (arguments == null || arguments.trim().length() == 0) {
			parameters = null;
			userMethodParams = null;
		} else {
			parameters = method.substring(index + 1, method.length() - 1)
					.split(",");
			userMethodParams = new int[parameters.length];
		}

		// find the parameter types
		// here we define only three types of arguments
		// String, boolean and int
		if (parameters != null) {
			for (int i = 0; i < parameters.length; i++) {
				if (parameters[i].startsWith("\"")
						&& parameters[i].endsWith("\"")) {
					userMethodParams[i] = 7;
					parameters[i] = "java.lang.String";
					continue;
				} else if (ParsingFunctions.isBoolean(parameters[i])) {
					userMethodParams[i] = 6;
					parameters[i] = "boolean";
				} else if (ParsingFunctions.isInteger(parameters[i])) {
					userMethodParams[i] = 2;
					parameters[i] = "int";
				} else {
					// type cannot be recognized so generate error
					return false;
				}
			}
		}

		// now parameters have been found, so check them with the available
		// methods
		// in the morph function
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().equals(methodName)) {
				// yes method has found now check for the parameters
				// compatibility
				Class<?>[] methodParams = methods[i].getParameterTypes();
				// first check for the number of parameters
				if (methods[i].getName().equals("null_stem")) {
					return true;
				}
				if (methodParams.length == parameters.length) {
					// yes arity has matched
					// now set the precedence
					int[] paramPrecedence = new int[methodParams.length];

					// assign precedence
					for (int j = 0; j < methodParams.length; j++) {
						if (methodParams[j].getName()
								.equals("java.lang.String"))
							paramPrecedence[j] = 7;
						else if (methodParams[j].getName().equals("boolean"))
							paramPrecedence[j] = 6;
						else if (methodParams[j].getName().equals("int"))
							paramPrecedence[j] = 2;
						else
							return false;
					}

					// if we are here that means all the type matched
					// so valid method declaration
					return true;
				}
			}
		}
		// if we are here that means method doesnot found
		return false;
	}

	/**
	 * Generates the error and stop the execution
	 * 
	 * @param mess -
	 *            message to be displayed as an error on the standard output
	 */
	private void generateError(String mess)
			throws ResourceInstantiationException {
		System.out.println("\n\n" + mess);
		System.out.println("Program terminated...");
		throw new ResourceInstantiationException("\n\n" + mess);
	}

	/**
	 * Main method
	 * 
	 * @param args
	 */
	public static void main(String[] args)
			throws ResourceInstantiationException {
		if (args == null || args.length < 3) {
			System.out
					.println("Usage : Interpret <Rules fileName> <word> <POS>");
			System.exit(-1);
		}
		Interpret interpret = new Interpret();
		try {
			interpret.init(new URL(args[0]));
		} catch (MalformedURLException mue) {
			throw new RuntimeException(mue);
		}
		String rootWord = interpret.runMorpher(args[1], args[2]);
		String affix = interpret.getAffix();
		System.out.println("Root : " + rootWord);
		System.out.println("affix : " + affix);
	}

	/**
	 * This method tells what was the affix to the provided word
	 * 
	 * @return affix
	 */
	public String getAffix() {
		return this.affix;
	}

	public FSMState getInitialState() {
		return initialState;
	}
}