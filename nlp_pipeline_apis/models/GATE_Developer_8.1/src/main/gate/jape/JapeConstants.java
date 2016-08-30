/*
 *  JapeConstants.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Hamish Cunningham, 09/07/98
 *
 *  $Id: JapeConstants.java 15333 2012-02-07 13:18:33Z ian_roberts $
 */


package gate.jape;

import java.io.Serializable;

/**
  * Constants interface for the JAPE package.
  */
public interface JapeConstants extends Serializable
{

  /** no Kleene operator */
  public int NO_KLEENE_OP		=  0;

  /** Kleene star (*) */
  public int KLEENE_STAR		=  1;

  /** Kleene plus (+) */
  public int KLEENE_PLUS		=  2;

  /** Kleene query (?) */
  public int KLEENE_QUERY		=  3;

  /** No binding on this element */
  public int NO_BINDING			=  1;

  public int MULTI_SPAN_BINDING		=  2;

  public int SINGLE_SPAN_BINDING	=  3;

  /** Brill-style rule application */
  public int BRILL_STYLE = 1;
  /** Appelt-style rule application */
  public int APPELT_STYLE = 2;
  /** Appelt-shortest-style rule application */
  public int FIRST_STYLE = 3;
  /** The phase finishes on the first match */
  public int ONCE_STYLE = 4;
  /** The phase finishes on the first match */
  public int ALL_STYLE = 5;


  /** The default priority of a rule. */
  public int DEFAULT_PRIORITY = -1;

  /** How far to increase indent when padding toString invocations. */
  public int INDENT_PADDING = 4;

} // JapeConstants





// $Log$
// Revision 1.13  2005/06/08 16:33:20  valyt
// Support for ALL matching style
//
// Revision 1.12  2005/01/11 13:51:36  ian
// Updating copyrights to 1998-2005 in preparation for v3.0
//
// Revision 1.11  2004/07/21 17:10:08  akshay
// Changed copyright from 1998-2001 to 1998-2004
//
// Revision 1.10  2004/03/25 13:01:14  valyt
// Imports optimisation throughout the Java sources
// (to get rid of annoying warnings in Eclipse)
//
// Revision 1.9  2002/04/23 10:30:47  valyt
//
// bugfix: confusion between "once" and "first" application style
//
// Revision 1.8  2002/04/22 11:45:57  valyt
//
// ONCE mode added to Jape
//
// Revision 1.7  2001/05/17 16:02:41  valyt
//
// Jape grammars now can match using the "first" style
//
// Tokeniser only matches one character per (Space)Tokens in case of whitespace or control characters
//
// Revision 1.6  2001/05/17 11:50:41  valyt
//
// 	Factory now handles Runtime parameters as well as inittime ones.
//
// 	There is a new rule application style Appelt-shortest
//
// Revision 1.5  2001/04/06 17:09:49  hamish
// save of session state via serialisation prototyped
//
// Revision 1.4  2000/11/08 16:35:02  hamish
// formatting
//
// Revision 1.3  2000/10/26 10:45:30  oana
// Modified in the code style
//
// Revision 1.2  2000/10/10 15:36:35  oana
// Changed System.out in Out and System.err in Err;
// Added the DEBUG variable seted on false;
// Added in the header the licence;
//
// Revision 1.1  2000/02/23 13:46:06  hamish
// added
//
// Revision 1.1.1.1  1999/02/03 16:23:01  hamish
// added gate2
//
// Revision 1.5  1998/08/12 15:39:36  hamish
// added padding toString methods
//
// Revision 1.4  1998/07/31 13:12:18  mks
// done RHS stuff, not tested
//
// Revision 1.3  1998/07/30 11:05:17  mks
// more jape
//
// Revision 1.2  1998/07/29 11:06:58  hamish
// first compiling version
//
// Revision 1.1.1.1  1998/07/28 16:37:46  hamish
// gate2 lives
