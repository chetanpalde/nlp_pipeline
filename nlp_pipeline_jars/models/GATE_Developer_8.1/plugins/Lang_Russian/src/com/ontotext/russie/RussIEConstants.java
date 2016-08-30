package com.ontotext.russie;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * RussIEConstants.java
 * This interface contains a collection of RussIE constants.
 * <p>Title: RussIE</p>
 * <p>Description: Russian Information Extraction based on GATE</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Ontotext Lab.</p>
 * @author borislav popov
 * @version 1.0
 */
public interface RussIEConstants {

public final static String RUSSIE_VERSION = "0.1.07.26";

/**Morphology file extension*/
final static String EXT_MORPH_FILE = ".pl";

/**Inflectional Gazetteer Resources extension*/
final static String EXT_INFL_GAZ_FILE = ".infl";


final static String TYPE_LOCATION = "Location";
final static String TYPE_MSD = "MSD";
final static String TYPE_TOKEN = "Token";
final static String TYPE_LOOKUP = "Lookup";
final static String TYPE_PERSON = "Person";
final static String TYPE_DATE = "Date";
final static String TYPE_NUMBER = "Number";
final static String TYPE_ORGANIZATION = "Organization";




/*FEATURES*/

final static String MAJOR_TYPE = "majorType";
final static String FEATURE_OCCURANCE = "occurance";

/**MSD annotation type feature*/
final static String FEATURE_MSD_TYPE = "type";

/** POS annotation type feature in Token */
final static String FEATURE_POS_TYPE = "category";

/**MSD annotation lemma-main form feature*/
final static String FEATURE_LEMMA = "lemma";

/*MAjor Types*/
final static String MAJOR_TYPE_PERSON_FIRST = "person_first";
final static String MAJOR_TYPE_PERSON_FULL = "person_full";
final static String MAJOR_TYPE_PERSON_SURNAME = "surname";
final static String MAJOR_TYPE_LOC = "location";


/*Syllables */

/* Vowels */
final static String [] arrVowels = {
  "\u0430","\u0410",
  "\u0435","\u0415",
  "\u0438","\u0418",
  "\u0439","\u0419",
  "\u043E","\u041E",
  "\u0443","\u0423",
  "\u044A","\u042A",
  "\u044C","\u042C",
  "\u044B","\u042B",
  "\u0451","\u0401",
  "\u044D","\u042D",
  "\u044E","\u042E",
  "\u044F","\u042F"
};

static final Set<String> SET_OF_VOWELS = new HashSet<String>(Arrays.asList(arrVowels));

/*Consonant Suffixes*/
final static String [] arrConsonantSuffixes = {
  // om
  "\u043E\u043C","\u041E\u041C",
  // em
  "\u0435\u043C","\u0415\u041C",
  // e with 2 points- m
  "\u0451\u043C","\u0401\u041C",
  // ~ih
  "\u044B\u0445","\u042B\u0425",
  // ~im
  "\u044B\u043C","\u042B\u041C",
  // ov
  "\u043E\u0432","\u041E\u0412",
  // am
  "\u0430\u043C","\u0410\u041C",
  // ah
  "\u0430\u0445","\u0410\u0425",
  // iam
  "\u044F\u043C","\u042F\u041C",
  // iah
  "\u044F\u0445","\u042F\u0425",
  };

static Set<String> SET_OF_CONSONANT_SUFFIXES = new HashSet<String>(Arrays.asList(arrConsonantSuffixes));



/*Stemming Limitations*/
static int minWordLength = 2;
static int maxTruncatedVowels = 2;





} // class RussIEConstants

