package gate.creole.morph;

import gate.creole.ResourceInstantiationException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PatternParser {

	public static void main(String[] args) {
		try {

			BufferedReader in = new BufferedReader(new InputStreamReader(
					System.in));
			variableDeclarationCommand("A ==> [abcdefghijklmnopqrstuvwxyz0123456789-]");
			variableDeclarationCommand("V ==> [aeiou]");
			variableDeclarationCommand("VI ==> [aeiouy]");
			variableDeclarationCommand("C ==> [bcdfghjklmnpqrstvwxyz]");
			variableDeclarationCommand("CX ==> [bcdfghjklmnpqrstvwxz]");
			variableDeclarationCommand("CX2 ==> \"bb\" OR \"cc\" OR \"dd\" OR \"ff\" OR \"gg\" OR \"hh\" OR \"jj\" OR \"kk\" OR \"ll\" OR \"mm\" OR \"nn\" OR \"pp\" OR \"qq\" OR \"rr\" OR \"ss\" OR \"tt\" OR \"vv\" OR \"ww\" OR \"xx\" OR \"zz\"");
			variableDeclarationCommand("CX2S ==> \"ff\" OR \"ss\" OR \"zz\"");
			variableDeclarationCommand("S ==> \"s\" OR \"x\" OR \"ch\" OR \"sh\"");
			variableDeclarationCommand("PRE ==> \"be\" OR \"ex\" OR \"in\" OR \"mis\" OR \"pre\" OR \"pro\" OR \"re\"");
			variableDeclarationCommand("EDING ==> \"ed\" OR \"ing\"");
			variableDeclarationCommand("ESEDING ==> \"es\" OR \"ed\" OR \"ing\"");
			
			while (true) {
				System.out.print("Query: ");
				String line = in.readLine();
				if (line == null || line.length() < 1)
					break;

				getPattern(line);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void getPattern(String line) {
		String[] ruleParts = line.split("==>");
		// now check if the method which has been called in this rule actually
		// available in the MorphFunction Class
		//String methodCalled = ruleParts[1].trim();

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

		ruleParts[0] = ruleParts[0].substring(i + 1, ruleParts[0].length()).trim();
		String regExp = ParsingFunctions.convertToRegExp(ruleParts[0], variables);
		String[] rules = ParsingFunctions.normlizePattern(regExp);
		for (int m = 0; m < rules.length; m++) {
			PatternPart parts[] = ParsingFunctions.getPatternParts(rules[m].trim());
			// each part has a type associated with it
			for (int j = 0; j < parts.length; j++) {
				System.out.println(parts[j].getPartString() + "=>"
						+ parts[j].getType());
			}
		}
	}

	public final static Storage variables = new Storage();
	
	private static void variableDeclarationCommand(String line)
			throws ResourceInstantiationException {
		// ok so first find the variable name and the value for it
		String varName = (line.split("==>"))[0].trim();
		String varValue = (line.split("==>"))[1].trim();

		// find the type of variable it is
		int valueType = ParsingFunctions.findVariableType(varValue.trim());

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
		}

		// and finally add the variable in
		if (!variables.add(varName, varInst.getPattern())) {
		}

		varInst.resetPointer();
	}

	public static List<String> parsePattern(String q1) {

		// arraylist to return - will contain all the OR normalized queries
		List<String> patterns = new ArrayList<String>();

		// remove all extra spaces from the query
		q1 = q1.trim();

		// we add opening and closing brackets explicitly
		q1 = "( " + q1 + " )";

		// add the main Query in the arraylist
		patterns.add(q1);

		for (int index = 0; index < patterns.size(); index++) {
			// get the query to be parsed
			String query = patterns.get(index);

			// current character and the previous character
			char ch = ' ', pre = ' ';

			// if query is ORed
			// we need duplication
			// for example: {A}((B)|(C))
			// the normalized form will be
			// (A)(B)
			// (A)(C)
			// here we need (A) to be duplicated two times
			boolean duplicated = false;
			int dupliSize = 0;
			
			String data = "";

			// we need to look into one query at a time and parse it
			for (int i = 0; i < query.length(); i++) {
				pre = ch;
				ch = query.charAt(i);

				// check if it is an open bracket
				// it is if it doesn't follow the '\' escape sequence
				if (isOpenBracket(ch, pre)) {

					// so find out where it gets closed
					int brClPos = findBracketClosingPosition(i + 1, query);

					// see if there are any OR operators in it
					List<String> orTokens = findOrTokens(query.substring(i + 1,
							brClPos));

					// orTokens will have
					// for eg. {A} | ({B}{C})
					// then {A}
					// and ({B}{C})
					// so basically findOrTokens find out all the tokens around
					// | operator
					if (orTokens.size() > 1) {
						String text = "";

						// data contains all the buffered character before the
						// current positions
						// for example "ABC" ({B} | {C})
						// here "ABC" will be in data
						// and {B} and {C} in orTokens
						if (!duplicated && data.length() > 0) {
							text = data;
							data = "";
						} else {
							if (index == patterns.size() - 1) {
								// this is the case where we would select the
								// text as ""
								text = "";
							} else {
								text = patterns
										.get(patterns.size() - 1);
							}
						}

						// so we need to duplicate the text orTokens.size()
						// times
						// for example "ABC" ({B} | {C})
						// text = "ABC"
						// orTokens {B} {C}
						// so two queries will be added
						// 1. "ABC"
						// 2. "ABC"

						patterns = duplicate(patterns, text, dupliSize,
								orTokens.size());
						// and tokens will be added
						// 1. "ABC" {B}
						// 2. "ABC" {C}
						patterns = writeTokens(orTokens, patterns, dupliSize);

						// text is duplicated so make it true
						duplicated = true;

						// and how many times it was duplicated
						if (dupliSize == 0)
							dupliSize = 1;
						dupliSize *= orTokens.size();
					} else {
						// what if the there is only one element between ( and )
						// it is not an 'OR' query

						// check how many times we have duplicated the text
						if (dupliSize == 0) {
							// if zero and the text buffered is ""
							// we simply add "" as a separate Query
							// otherwise add the buffered data as a separate
							// Query
							if (data.length() == 0)
								patterns.add("");
							else
								patterns.add(data);

							// because we simply needs to add it only once
							// but still we have copied it as a separate query
							// so say duplicated = true
							duplicated = true;
							data = "";
							// and ofcourse the size of the duplication will be
							// only 1
							dupliSize = 1;
						}
						// and we need to add all the contents between two
						// brackets in the last duplicated
						// queries
						patterns = writeStringInAll("<"
								+ query.substring(i + 1, brClPos) + ">",
								dupliSize, patterns);
					}
					i = brClPos;
				} else {
					if (duplicated) {
						patterns = writeCharInAll(ch, dupliSize, patterns);
					} else {
						data += "" + ch;
					}
				}
			}

			boolean scan = scanQueryForOrOrBracket(query);
			if (scan) {
				patterns.remove(index);
				index--;
			}
		}

		List<String> queriesToReturn = new ArrayList<String>();
		for (int i = 0; i < patterns.size(); i++) {
			String q = patterns.get(i);
			if (q.trim().length() == 0) {
				continue;
			} else if (queriesToReturn.contains(q.trim())) {
				continue;
			} else {
				queriesToReturn.add(q.trim());
			}
		}

		for (int i = 0; i < queriesToReturn.size(); i++) {
			String s = queriesToReturn.get(i);
			s = s.replaceAll("<", "(");
			s = s.replaceAll(">", ")");
			s = s.substring(1, s.length() - 1);
			queriesToReturn.set(i, s.trim());
		}
		return queriesToReturn;
	}

	public static boolean scanQueryForOrOrBracket(String query) {
		int index = 0;
		int index1 = 0;
		do {
			index = query.indexOf('|', index);
			if (index == 0) {
				return true;
			} else if (index > 0) {
				// we have found it but we need to check if it is an escape
				// sequence
				if (query.charAt(index - 1) == '\\') {
					// yes it is an escape sequence
					// lets search for the next one
				} else {
					return true;
				}
			}

			// if we are here that means it was not found
			index1 = query.indexOf('(', index1);
			if (index1 == 0) {
				return true;
			} else if (index1 > 0) {
				// we have found it
				if (query.charAt(index1 - 1) == '\\') {
					// yes it is an escape sequence
					continue;
				} else {
					return true;
				}
			}

		} while (index >= 0 && index1 >= 0);

		return false;
	}

	public static List<String> writeTokens(List<String> tokens, List<String> queries,
			int dupliSize) {
		if (dupliSize == 0)
			dupliSize = 1;

		List<String> qToRemove = new ArrayList<String>();
		for (int j = 0; j < dupliSize; j++) {
			for (int i = 1; i <= tokens.size(); i++) {
				String token = tokens.get(i - 1);
				if (token.trim().equals("{__o__}")) {
					token = " ";
				}
				String s = queries.get(queries.size()
						- (j * tokens.size() + i));
				qToRemove.add(s);
				s += token;
				queries.set(queries.size() - (j * tokens.size() + i), s);
			}
		}

		// and now remove
		for (int i = 0; i < qToRemove.size(); i++) {
			queries.remove(qToRemove.get(i));
		}

		return queries;
	}

	public static List<String> duplicate(List<String> queries, String s,
			int dupliSize, int no) {
		if (s == null)
			s = "";

		List<String> strings = new ArrayList<String>();
		if (dupliSize == 0) {
			strings.add(s);
		} else {
			for (int i = 0; i < dupliSize; i++) {
				strings.add(queries.get(queries.size() - (i + 1)));
			}
		}

		for (int i = 0; i < strings.size(); i++) {
			for (int j = 0; j < no; j++) {
				queries.add(strings.get(i));
			}
		}

		return queries;
	}

	public static List<String> findOrTokens(String query) {
		int balance = 0;
		char pre = ' ';
		char ch = ' ';
		List<String> ors = new ArrayList<String>();

		String s = "";
		for (int i = 0; i < query.length(); i++) {
			pre = ch;
			ch = query.charAt(i);
			if (isOpenBracket(ch, pre)) {
				balance++;
				s += "" + ch;
				continue;
			}

			if (isClosingBracket(ch, pre) && balance > 0) {
				balance--;
				s += "" + ch;
				continue;
			}

			if (isOrSym(ch, pre)) {
				if (balance > 0) {
					s += "" + ch;
					continue;
				} else {
					ors.add(s);
					s = "";
					continue;
				}
			}

			s += "" + ch;
		}

		if (s.length() > 0)
			ors.add(s);

		return ors;
	}

	public static int findBracketClosingPosition(int startFrom, String query) {
		int balance = 0;
		char pre = ' ';
		char ch = ' ';
		for (int i = startFrom; i < query.length(); i++) {
			pre = ch;
			ch = query.charAt(i);
			if (isOpenBracket(ch, pre)) {
				balance++;
				continue;
			}

			if (isClosingBracket(ch, pre)) {
				if (balance > 0) {
					balance--;
				} else {
					return i;
				}
			}
		}
		return -1;
	}

	public static List<String> writeCharInAll(char c, int no, List<String> queries) {
		for (int i = 0; i < no; i++) {
			String s = queries.get(queries.size() - (i + 1));
			s += "" + c;
			queries.set(queries.size() - (i + 1), s);
		}
		return queries;
	}

	public static List<String> writeStringInAll(String c, int no, List<String> queries) {
		for (int i = 0; i < no; i++) {
			String s = queries.get(queries.size() - (i + 1));
			s += "" + c;
			queries.set(queries.size() - (i + 1), s);
		}
		return queries;
	}

	public static boolean isOpenBracket(char ch, char pre) {
		if (ch == '(' && pre != '\\')
			return true;
		else
			return false;
	}

	public static boolean isClosingBracket(char ch, char pre) {
		if (ch == ')' && pre != '\\')
			return true;
		else
			return false;
	}

	public static boolean isOrSym(char ch, char pre) {
		if (ch == '|' && pre != '\\')
			return true;
		else
			return false;
	}

}
