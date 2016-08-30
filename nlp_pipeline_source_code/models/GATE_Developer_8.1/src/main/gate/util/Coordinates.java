/*
 *  Coordinates.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 * 
 *  Kalina Bontcheva, 20/09/2000
 *
 *  $Id: Coordinates.java 17600 2014-03-08 18:47:11Z markagreenwood $
 */
package gate.util;

public class Coordinates {

  int x1, x2, y1, y2;

  public Coordinates(int x1, int y1, int x2, int y2) {
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
  }

  public int getX1() {
    return x1;
  }

  public int getY1() {
    return y1;
  }

  public int getX2() {
    return x2;
  }

  public int getY2() {
    return y2;
  }

  public void setX1( int x) {
  	x1 = x;
  }

  public void setX2( int x) {
  	x2 = x;
  }

  public void setY1( int y) {
  	y1 = y;
  }

  public void setY2( int y) {
  	y2 = y;
  }


  @Override
  public String toString() {
    return "x1=" + x1 + ";y1=" + y1 + ";x2=" + x2 + ";y2=" + y2;
  }
  
} // class Coordinates
