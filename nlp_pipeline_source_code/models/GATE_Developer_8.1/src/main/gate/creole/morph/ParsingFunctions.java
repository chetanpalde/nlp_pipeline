package gate.creole.morph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * <p>
 * Title: ParsingFunctions.java
 * </p>
 * <p>
 * Description: This class implements all static methods, which can be used for
 * various purposes, like converting rules defined by users into the regular
 * expressions, finding varilable type from its value type etc.
 * </p>
 */
public class ParsingFunctions {


	/**
	 * This method takes the value of the variable and tells the user what type
	 * of value is from CharacterRange, CharacterSet, StringSet
	 * 
	 * @param varValue
	 *            value for which to find the variable type
	 * @return ERROR_CODE = -4, STRING_SET_CODE = 0, CHARACTER_RANGE_CODE = 1,
	 *         CHARACTER_SET_CODE = 2;
	 */
	public static int findVariableType(String varValue) {
		// if the value starts with " it is string set
		// if the value starts with "[-" it is a character range
		// if the value starts with "[" it is a character set
		// otherwise error
		if (varValue == null) {
			return Codes.ERROR_CODE;
		}

		if (varValue.length() >= 3 && varValue.charAt(0) == '\"'
				&& (varValue.lastIndexOf('\"') == (varValue.length() - 1))) {
			// for string set it should be greater than 3 because
			// it requires at least one character to make the string
			// first and the last character should be "
			return Codes.STRING_SET_CODE;

		} else if (varValue.length() >= 6
				&& (((varValue.length() - 3) % 3) == 0)
				&& varValue.substring(0, 2).equals("[-")
				&& varValue.charAt(varValue.length() - 1) == ']') {
			// for the character range it should be greater than 6 because
			// three characters as "[-" and "]"
			// and finally to define the range character-character
			return Codes.CHARACTER_RANGE_CODE;

		} else if (varValue.length() >= 3 && varValue.charAt(0) == '['
				&& varValue.charAt(varValue.length() - 1) == ']') {
			// for the character set it should be greater than 3 characters
			// because
			// it requires at least one character
			// first and the last character should be [ and ] respectively
			if (varValue.charAt(1) == '-') {
				return Codes.ERROR_CODE;
			} else {
				return Codes.CHARACTER_SET_CODE;
			}

		} else {
			// there are some errors
			return Codes.ERROR_CODE;
		}

	}

	/**
	 * This method checks for the string if it is a valid integer value
	 * 
	 * @param value
	 *            value to be checked for its type to be integer
	 * @return if value is an integer returns true, false otherwise
	 */
	public static boolean isInteger(String value) {
		try {
			Integer.parseInt(value);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	/**
	 * This method checks for the string if it is a valid integer value
	 * 
	 * @param value
	 *            value to be checked for its type to be integer
	 * @return if value is an integer returns true, false otherwise
	 */
	public static boolean isBoolean(String value) {
		if (value.equals("false") || value.equals("true")) {
			return true;
		} else {
			return false;
		}
	}

	// [abcd]
	public static final int OR = 0;

	// (abcd)
	public static final int AND = 1;

	// [abcd]+
	public static final int OR_PLUS = 2;

	// (abcd)+
	public static final int AND_PLUS = 3;

	// [abcd]*
	public static final int OR_STAR = 4;

	// (abcd)*
	public static final int AND_STAR = 5;

	
	public static String[] normlizePattern(String line) {
		List<String> patterns = PatternParser.parsePattern(line);
		/*String[] pats = new String[patterns.size()];
		for(int i=0;i<patterns.size();i++) {
			pats[i] = patterns.get(i);
		}
		return pats;*/
		return patterns.toArray(new String[patterns.size()]);
	}
	
	
	public static PatternPart[] getPatternParts(String line) {
		// the first thing we do is replace all variables with their respective
		// values
		List<PatternPart> patterns = new ArrayList<PatternPart>();
		line = line.replaceAll("[\\(]+","(");
		line = line.replaceAll("[\\)]+",")");
		line = line.replaceAll("[\\[]+","[");
		line = line.replaceAll("[\\]]+","]");
		line = line.replaceAll("(\\()[\\[]+","[");
		line = line.replaceAll("(\\])[\\)]+","]");

		while(true) {
			if(line.trim().length() == 0)
				break;

			if(line.charAt(0)!= '(' && line.charAt(0) != '[') {
				int index = line.indexOf("(");
				int index1 = line.indexOf("[");
				if(index < 0 && index1 > 0)
					index = index1;
				else if(index > 0 && index1 < 0) {
					// no need to anything
				} else if(index > index1)
					index = index1;
				
				if(index < 0) {
					line = "(" + line + ")";
				} else {
					line = '(' + line.substring(0,index) + ")" + line.substring(index, line.length());
				}
			}
			
			boolean rdBracket = false;
			boolean rcBracket = false;
			
			int index = line.indexOf('(');
			if(index >= 0)
				rdBracket = true;
			
			int rcindex = line.indexOf('[');
			if(rcindex >= 0) {
				// check which one appears first
				if(rdBracket) {
					if(index < rcindex)
						rcBracket = false;
					else {
						rcBracket = true;
						rdBracket = false;
						index = rcindex;
					}
				} else {
					index = rcindex;
					rcBracket = true;
				}
			}
			
			// no bracket found
			if(!rdBracket && !rcBracket)
				break;
			
			int index1 = -1;
			if(rdBracket) {
				index1 = line.indexOf(')');
				if(index1 < 0)
					break;
			}
			
			if(rcBracket) {
				index1 = line.indexOf(']');
				if(index1 < 0)
					break;
			}

			boolean isPlus = false;
			boolean isStar = false;
			
			if(index1+1 < line.length()) {
				isPlus = line.charAt(index1+1) == '+';
				if(!isPlus)
					isStar = line.charAt(index1+1) == '*';
			}
			
			// we check if the character after closing bracket is
			String string = line.substring(index+1, index1);
			// by now there shouldn't be any bracket
			string = string.replaceAll("\\(","");
			string = string.replaceAll("\\)","");
			
			if(!isPlus && !isStar && rcBracket) {
				// Style 1
				// [ABCD]
				PatternPart pp = new PatternPart(string, OR);
				patterns.add(pp);
			} else if(!isPlus && !isStar && rdBracket) { 
				// Style 2
				// (ABC)
				PatternPart pp = new PatternPart(string, AND);
				patterns.add(pp);
			} else if(isPlus && rdBracket) {
				// Style 3
				// (ABC)+
				PatternPart pp = new PatternPart(string, AND_PLUS);
				patterns.add(pp);
			} else if(isPlus && rcBracket){
				// Style 4
				// [ABCD]+
				PatternPart pp = new PatternPart(string, OR_PLUS);
				patterns.add(pp);
			} else if(isStar && rcBracket){
				// Style 4
				// [ABCD]*
				PatternPart pp = new PatternPart(string, OR_STAR);
				patterns.add(pp);
			} else {
				// Style 4
				// (ABCD)*
				PatternPart pp = new PatternPart(string, AND_STAR);
				patterns.add(pp);
			}
			
			if(isPlus || isStar)
				index1++;
			
			if(index1+1 < line.length())
				line = line.substring(index1+1, line.length());
			else
				line = "";
		}

		PatternPart[] parts = new PatternPart[patterns.size()];
		for(int i=0;i<patterns.size();i++) {
			parts[i] = patterns.get(i);
		}
		return parts;
	}

	/**
	 * This method is used to find the method definition But it can recognize
	 * only String, boolean and int types for Example: stem(2,"ed","d") ==>
	 * stem(int,java.lang.String,java.lang.String);
	 * 
	 * @param method
	 * @return the definition of the method
	 */
	public static String getMethodName(String method) {
		// find the first index of '('
		int index = method.indexOf('(');
		String methodName = method.substring(0, index) + "(";

		// now get the parameter types
		String[] parameters = method.substring(index + 1, method.length() - 1)
				.split(",");

		// find the approapriate type
		for (int i = 0; i < parameters.length; i++) {
			if (parameters[i].startsWith("\"") && parameters[i].endsWith("\"")) {
				methodName = methodName + "java.lang.String";
			} else if (ParsingFunctions.isBoolean(parameters[i])) {
				methodName = methodName + "boolean";
			} else if (ParsingFunctions.isInteger(parameters[i])) {
				methodName = methodName + "int";
			}
			if ((i + 1) < parameters.length) {
				methodName = methodName + ",";
			}
		}
		methodName = methodName + ")";
		return methodName;
	}

	public static final short IRREG_STEM = 0;
	public static final short NULL_STEM = 1;
	public static final short SEMIREG_STEM = 2;
	public static final short STEM = 3;
	
	/**
	 * This method is used to find the method definition But it can recognize
	 * only String, boolean and int types for Example: stem(2,"ed","d") ==>
	 * stem(int,java.lang.String,java.lang.String);
	 * 
	 * @param method
	 * @return the definition of the method
	 */
	public static short getMethodIndex(String method) {
		// find the first index of '('
		int index = method.indexOf('(');
		String methodName = method.substring(0, index) + "(";
		
		// now get the parameter types
		String[] parameters = method.substring(index + 1, method.length() - 1)
				.split(",");

		// find the approapriate type
		for (int i = 0; i < parameters.length; i++) {
			if (parameters[i].startsWith("\"") && parameters[i].endsWith("\"")) {
				methodName = methodName + "java.lang.String";
			} else if (ParsingFunctions.isBoolean(parameters[i])) {
				methodName = methodName + "boolean";
			} else if (ParsingFunctions.isInteger(parameters[i])) {
				methodName = methodName + "int";
			}
			if ((i + 1) < parameters.length) {
				methodName = methodName + ",";
			}
		}
		methodName = methodName + ")";
		if (methodName.startsWith("irreg_stem")) {
			return IRREG_STEM;
		} else if (methodName.startsWith("null_stem")) {
			return NULL_STEM;
		} else if(methodName.startsWith("semi_reg_stem")) {
			return SEMIREG_STEM;
		} else if(methodName.startsWith("stem")) {
			return STEM;
		}
		return -1;
	}

	
	/**
	 * This method finds the actual parameter values
	 * 
	 * @param method
	 *            from which parameters are required to be found
	 * @return parameter values
	 */
	public static String[] getParameterValues(String method) {
		// now first find the name of the method
		// their parameters and their types
		int index = method.indexOf("(");

		// now get the parameters
		String[] parameters = method.substring(index + 1, method.length() - 1)
				.split(",");

		// process each parameter
		for (int i = 0; i < parameters.length; i++) {
			// we need to remove " from String
			if (parameters[i].startsWith("\"") && parameters[i].endsWith("\"")) {
				parameters[i] = parameters[i].substring(1, parameters[i]
						.length() - 1).intern();
				continue;
			}
		}
		return parameters;
	}
	
	public static Set<Set<FSMState>> createFSMs(String string, int type, Set<Set<FSMState>> initStates, Interpret owner) {
		Set<Set<FSMState>> result = new HashSet<Set<FSMState>>();
		// we create different groups for states 
		Iterator<Set<FSMState>> iter = initStates.iterator();
		while(iter.hasNext()) {
			Set<FSMState> states = iter.next();
			switch (type) {
			case OR:
				result.addAll(orFSMs(string, states, owner));
				break;
			case OR_PLUS:
				result.addAll(orPlusFSMs(string, states,owner));
				break;
			case AND_PLUS:
				result.addAll(andPlusFSMs(string, states,owner));
				break;
			case OR_STAR:
				result.addAll(orStarFSMs(string, states,owner));
				break;
			case AND_STAR:
				result.addAll(andStarFSMs(string, states,owner));
				break;
			default:
			  if(string.length() > 0)
				  result.addAll(andFSMs(string, states,owner));
				break;
			}
		}
		return result;
	}

	
	@SuppressWarnings("unused")
  private static FSMState next(char ch, Set<FSMState> states) {
		Iterator<FSMState> iter = states.iterator();
		while(iter.hasNext()) {
			FSMState state = iter.next();
			FSMState nextState = state.next(ch, FSMState.CHILD_STATE);
			if(nextState != null)
				return nextState;
		}
		return null;
	}
	
	private static int getIndex(Set<FSMState> states) {
		Iterator<FSMState> iter = states.iterator();
		while(iter.hasNext()) {
			FSMState state = iter.next();
			return state.getIndex();
		}
		return -1;
	}

	
	/**
	 * (abc) -> a -> b -> c ->
	 */
	public static List<Set<FSMState>> andFSMs(String line, Set<FSMState> initStates, Interpret owner) {
		// for the first inital State
		// we need to find out if any of the parent contains referece to it
		char ch = line.charAt(0);

		int nextIndex = getIndex(initStates);
		FSMState currentState = owner.getState(ch, nextIndex + 1);
		if(currentState == null) {
			currentState = new FSMState(nextIndex+1);
			//System.out.println(ch + " \t "+(nextIndex+1));
			owner.addState(ch, currentState, nextIndex+1);
		}
		
		// currentState contains the first state
		// this should be added as a child state to all initStates
		Iterator<FSMState> iter = initStates.iterator();
		while(iter.hasNext()) {
			FSMState state = iter.next();
			state.put(ch, currentState, FSMState.CHILD_STATE);
		}
		
		// and from current state
		// do the linking of rest of the characters
		
		FSMState nextState = currentState;
		for (int i = 1; i < line.length(); i++) {
			ch = line.charAt(i);
			nextState = currentState.next(ch,  FSMState.CHILD_STATE);
		    if(nextState == null){
		    	nextState = owner.getState(ch, currentState.getIndex()+1);
		    	if(nextState == null) {
		    		nextState = new FSMState(currentState.getIndex()+1);
		    		//System.out.println(ch + " \t "+(currentState.getIndex()+1));
		    		owner.addState(ch, nextState, currentState.getIndex()+1);
		    	}
				currentState.put(ch, nextState,  FSMState.CHILD_STATE);
		    }			
			currentState = nextState;
		}
		List<Set<FSMState>> nextStates = new ArrayList<Set<FSMState>>();
		Set<FSMState> newSet = new HashSet<FSMState>();
		newSet.add(nextState);
		nextStates.add(newSet);
		return nextStates;
	}

	/**
	 * [abc] -> a, 
	 * 		 -> b, 
	 * 		 -> c
	 */ 
	public static List<Set<FSMState>> orFSMs(String line, Set<FSMState> initStates, Interpret owner) {
		// for each character in the line
		// we need to find out if any of the initStates contain reference to it
		// if so that should be assigned to all initStates
		Set<FSMState> nextStates = new HashSet<FSMState>();
		for(int i=0;i<line.length();i++) {
			// for the current character
			// we need to find out if any of the parent contains referece to it
			char ch = line.charAt(i);
			int nextIndex = getIndex(initStates);
			FSMState currentState = owner.getState(ch, nextIndex + 1);
			if(currentState == null) {
				currentState = new FSMState(nextIndex+1);
				//System.out.println(ch + " \t "+(nextIndex+1));
				owner.addState(ch, currentState, nextIndex+1);
			}
			
			
			// currentState should be added as a nextStates
			nextStates.add(currentState);
			
			// currentState contains refenrece for the current character
			// this should be added as a child state to all initStates
			Iterator<FSMState> iter = initStates.iterator();
			while(iter.hasNext()) {
				FSMState state = iter.next();
				state.put(ch, currentState, FSMState.CHILD_STATE);
			}
			
		}
		List<Set<FSMState>> newList = new ArrayList<Set<FSMState>>();
		newList.add(nextStates);
		return newList;
	}

	/**
	 * [abc]+ 
	 * each element can travel to itself and can travel to next one
	 */
	public static List<Set<FSMState>> orPlusFSMs(String line, Set<FSMState> initStates, Interpret owner) {
		// for each character in the line
		// we need to find out if any of the initStates contain reference to it
		// if so that should be assigned to all initStates
		List<FSMState> nextStates = new ArrayList<FSMState>();
		for(int i=0;i<line.length();i++) {
			// for the current character
			// we need to find out if any of the parent contains referece to it
			char ch = line.charAt(i);
			int nextIndex = getIndex(initStates);
			FSMState currentState = owner.getState(ch, nextIndex + 1);
			if(currentState == null) {
				currentState = new FSMState(nextIndex+1);
				//System.out.println(ch + " \t "+(nextIndex+1));

				owner.addState(ch, currentState, nextIndex+1);
			}
			
			// currentState should be added as a nextStates
			nextStates.add(currentState);
			
			// currentState contains refenrece for the current character
			// this should be added as a child state to all initStates
			Iterator<FSMState> iter = initStates.iterator();
			while(iter.hasNext()) {
				FSMState state = iter.next();
				state.put(ch, currentState, FSMState.CHILD_STATE);
			}
		}

		for(int i=0;i<nextStates.size();i++) {
			FSMState from = nextStates.get(i);
			for(int j=0;j<nextStates.size();j++) {
				FSMState to = nextStates.get(j);
				char ch = line.charAt(j);
				from.put(ch, to, FSMState.ADJ_STATE);
			}
		}

		Set<FSMState> newSet = new HashSet<FSMState>();
		newSet.addAll(nextStates);
		List<Set<FSMState>> newList = new ArrayList<Set<FSMState>>();
		newList.add(newSet);
		return newList;
	}

	/**
	 * (abc)+ 
	 * -> a -> b -> c -> null 
	 * -> a -> b -> c -> a
	 */
	public static List<Set<FSMState>> andPlusFSMs(String line, Set<FSMState> initStates, Interpret owner) {
		// for the first inital State
		// we need to find out if any of the parent contains referece to it
		char ch = line.charAt(0);
		int nextIndex = getIndex(initStates);
		FSMState currentState = owner.getState(ch, nextIndex + 1);
		if(currentState == null) {
			currentState = new FSMState(nextIndex+1);
			//System.out.println(ch + " \t "+(nextIndex+1));

			owner.addState(ch, currentState, nextIndex+1);
		}

		FSMState firstState = currentState;
		
		// currentState contains the first state
		// this should be added as a child state to all initStates
		Iterator<FSMState> iter = initStates.iterator();
		while(iter.hasNext()) {
			FSMState state = iter.next();
			state.put(ch, currentState, FSMState.CHILD_STATE);
		}
		
		// and from current state
		// do the linking of rest of the characters
		FSMState nextState = currentState;
		for (int i = 1; i < line.length(); i++) {
			ch = line.charAt(i);
			nextState = currentState.next(ch,  FSMState.CHILD_STATE);
		    if(nextState == null){
		    	nextState = owner.getState(ch, currentState.getIndex()+1);
		    	if(nextState == null) {
		    		nextState = new FSMState(currentState.getIndex()+1);
		    		//System.out.println(ch + " \t "+(currentState.getIndex()+1));
		    		owner.addState(ch, nextState, currentState.getIndex()+1);
		    	}
				currentState.put(ch, nextState,  FSMState.CHILD_STATE);
		    }			
			currentState = nextState;
		}
			
		nextState.put(line.charAt(0), firstState,  FSMState.ADJ_STATE);
		List<Set<FSMState>> nextStates = new ArrayList<Set<FSMState>>();
		Set<FSMState> newSet = new HashSet<FSMState>();
		newSet.add(nextState);
		nextStates.add(newSet);
		return nextStates;
	}

	/**
	 * [abc]* 
	 * each element can have reference to adjecent ones and to itself
	 */
	public static List<Set<FSMState>> orStarFSMs(String line, Set<FSMState> initStates, Interpret owner) {
		// for each character in the line
		// we need to find out if any of the initStates contain reference to it
		// if so that should be assigned to all initStates
		List<FSMState> nextStates = new ArrayList<FSMState>();
		for(int i=0;i<line.length();i++) {
			// for the current character
			// we need to find out if any of the parent contains referece to it
			char ch = line.charAt(i);
			int nextIndex = getIndex(initStates);
			FSMState currentState = owner.getState(ch, nextIndex + 1);
			if(currentState == null) {
				currentState = new FSMState(nextIndex+1);
				//System.out.println(ch + " \t "+(nextIndex+1));

				owner.addState(ch, currentState, nextIndex+1);
			}
			
			// currentState should be added as a nextStates
			nextStates.add(currentState);
			
			// currentState contains refenrece for the current character
			// this should be added as a child state to all initStates
			Iterator<FSMState> iter = initStates.iterator();
			while(iter.hasNext()) {
				FSMState state = iter.next();
				state.put(ch, currentState, FSMState.CHILD_STATE);
			}
		}

		for(int i=0;i<nextStates.size();i++) {
			FSMState from = nextStates.get(i);
			for(int j=0;j<nextStates.size();j++) {
				FSMState to = nextStates.get(j);
				char ch = line.charAt(j);
				from.put(ch, to, FSMState.ADJ_STATE);
			}
		}

		Set<FSMState> newSet = new HashSet<FSMState>();
		newSet.addAll(nextStates);
		
		List<Set<FSMState>> newList = new ArrayList<Set<FSMState>>();
		newList.add(newSet);
		newList.add(initStates);
		return newList;
	}

	/**
	 * (abc)*
	 */
	public static List<Set<FSMState>> andStarFSMs(String line, Set<FSMState> initStates, Interpret owner) {
		// for the first inital State
		// we need to find out if any of the parent contains referece to it
		char ch = line.charAt(0);
		int nextIndex = getIndex(initStates);
		FSMState currentState = owner.getState(ch, nextIndex + 1);
		if(currentState == null) {
			currentState = new FSMState(nextIndex+1);
			//System.out.println(ch + " \t "+(nextIndex+1));

			owner.addState(ch, currentState, nextIndex+1);
		}

		FSMState firstState = currentState;
		
		// currentState contains the first state
		// this should be added as a child state to all initStates
		Iterator<FSMState> iter = initStates.iterator();
		while(iter.hasNext()) {
			FSMState state = iter.next();
			state.put(ch, currentState, FSMState.CHILD_STATE);
		}
		
		// and from current state
		// do the linking of rest of the characters
		FSMState nextState = currentState;
		for (int i = 1; i < line.length(); i++) {
			ch = line.charAt(i);
			nextState = currentState.next(ch,  FSMState.CHILD_STATE);
		    if(nextState == null){
		    	nextState = owner.getState(ch, currentState.getIndex()+1);
		    	if(nextState == null) {
		    		nextState = new FSMState(currentState.getIndex()+1);
		    		//System.out.println(ch + " \t "+(currentState.getIndex()+1));
		    		owner.addState(ch, nextState, currentState.getIndex()+1);
		    	}
				currentState.put(ch, nextState,  FSMState.CHILD_STATE);
		    }			
			currentState = nextState;
		}
		
		
		nextState.put(line.charAt(0), firstState,  FSMState.ADJ_STATE);
		
		List<Set<FSMState>> nextStates = new ArrayList<Set<FSMState>>();
		Set<FSMState> newSet = new HashSet<FSMState>();
		newSet.add(nextState);
		nextStates.add(newSet);
		nextStates.add(initStates);
		return nextStates;
	}

	
	/**
	 * This method convert the expression which has been entered by the user in
	 * the .rul file (i.e. rules defined by the user), into the expression which
	 * are recognized by the regular expression Patterns
	 * 
	 * @param line
	 *            rule defined by the user
	 * @param storage
	 *            this method internally requires values of the used variables
	 *            to replace the them with their values in the expression
	 * @return newly generated regular expression
	 */
	public static String convertToRegExp(String line, Storage storage) {
		// replace all OR with |
		line = line.replaceAll("( OR )", "|");
		line = line.replaceAll("(\\[\\-)", "[");

		// we will use the stack concept here
		// for every occurence of '{', or '(' we will add that into the stack
		// and for every occurence of '}' or ')' we will remove that element
		// from
		// the stack
		// if the value found between the bracket is an integer value
		// we won't replace those brackets
		StringBuffer newExpr = new StringBuffer(line);
		Stack<String> stack = new Stack<String>();
		Stack<Integer> bracketIndexes = new Stack<Integer>();

		for (int i = 0; i < newExpr.length(); i++) {
			if (newExpr.charAt(i) == '{') {
				// add it to the stack
				stack.add("{");
				bracketIndexes.add(new Integer(i));

			} else if (newExpr.charAt(i) == '(') {
				// add it to the stack
				stack.add("(");
				bracketIndexes.add(new Integer(i));

			} else if (newExpr.charAt(i) == '[') {
				// add it to the stack
				stack.add("[");
				bracketIndexes.add(new Integer(i));

			} else if (newExpr.charAt(i) == '\"') {
				// before adding it to the stack, check if this is the closing
				// one
				if (stack.isEmpty()
						|| !((stack.get(stack.size() - 1))
								.equals("\""))) {
					// yes this is the opening one
					// add it to the stack
					stack.add("\"");
					bracketIndexes.add(new Integer(i));
				} else {
					// this is the closing one
					stack.pop();
					int index = (bracketIndexes.pop()).intValue();
					newExpr.setCharAt(index, '(');
					newExpr.setCharAt(i, ')');
				}
			} else if (newExpr.charAt(i) == '}') {
				// remove the element from the stack
				// it must be '{', otherwise generate the error
				String bracket = (stack.pop());
				int index = (bracketIndexes.pop()).intValue();
				if (!bracket.equals("{")) {
					return null;
				}

				// now check if the value between these brackets is integer,
				// that means
				// we don't need to change the brackets, otherwise change them
				// to
				// '(' and ')'
				if (isInteger(newExpr.substring(index + 1, i))) {
					// yes it is an integer
					// continue
					continue;
				} else {
					// no it is string
					newExpr.setCharAt(index, '(');
					newExpr.setCharAt(i, ')');
				}

			} else if (newExpr.charAt(i) == ')') {
				// remove the element from the stack
				// it must be ')', otherwise generate the error
				String bracket = (stack.pop());
				bracketIndexes.pop();
				if (!bracket.equals("(")) {
					return null;
				}
				continue;
			} else if (newExpr.charAt(i) == ']') {
				// remove the element from the stack
				// it must be '[', otherwise generate the error
				String bracket = (stack.pop());
				bracketIndexes.pop();
				if (!bracket.equals("[")) {
					return null;
				}
			}
		}
		// check if all the stacks are empty then and only then the written
		// expression is true, otherwise it is incorrect
		if (!stack.empty() || !bracketIndexes.empty()) {
			return null;
		}
		// System.out.println(line+" "+newExpr);
		// now we need to replace the variables with their values
		// but how would we know which is the variable
		// so get the variable list and check if it is available in the
		// expression
		String[] varNames = storage.getVarNames();
		for (int i = 0; i < varNames.length; i++) {
			// check for the occurance of each varName in the expression
			int index = -1;
			String myString = "{[()]} ";
			while ((index = newExpr.indexOf(varNames[i], index + 1)) != -1) {
				// System.out.println(index + " "+newExpr.length());
				// now check for the left and right characters
				if (index > 0) {
					if (myString.indexOf(newExpr.charAt(index - 1)) == -1) {
						index = index + varNames[i].length() - 1;
						// this is not the varilable
						continue;
					}
				}
				if ((varNames[i].length() + index) < newExpr.length()) {
					if (myString.indexOf(newExpr.charAt(varNames[i].length()
							+ index)) == -1) {
						index = index + varNames[i].length() - 1;
						// this is not the variable
						continue;
					}
				}

				// yes it is a variable
				String replaceWith = "(" + (storage.get(varNames[i]))
						+ ")";
				newExpr.replace(index, (varNames[i].length() + index),
						replaceWith);
				index = index + replaceWith.length();
			}
		}
		return new String(newExpr);
	}
}
