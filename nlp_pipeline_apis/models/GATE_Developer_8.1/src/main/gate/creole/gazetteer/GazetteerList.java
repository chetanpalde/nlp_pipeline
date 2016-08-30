/*
 *  GazetteerList.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  borislav popov 02/2002
 *
 *  $Id: GazetteerList.java 17648 2014-03-13 11:16:47Z markagreenwood $
 */

package gate.creole.gazetteer;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import gate.creole.ResourceInstantiationException;
import gate.util.BomStrippingInputStreamReader;
import gate.util.Files;
import gate.util.GateRuntimeException;

/**
 * Gazetteer List provides the means for uploading, managing and storing
 * the data in the gazetteer list files.
 */
public class GazetteerList extends gate.creole.AbstractLanguageResource
                                                                       implements
                                                                       List<GazetteerNode> {

  private static final long serialVersionUID = -812795422822719315L;

  /** indicates list representation of the gazetteer list */
  public final static int LIST_MODE = 0;

  /** indicates representation of the gaz list as a single string */
  public final static int STRING_MODE = 1;

  /** the url of this list */
  private URL url;

  /** the encoding of the list */
  private String encoding = "UTF-8";

  /**
   * indicates the current mode of the gazetteer list(e.g.
   * STRING_MODE,LIST_MODE)
   */
  private int mode = 0;

  /**
   * flag indicating whether the list has been modified after
   * loading/storing
   */
  private boolean isModified = false;

  /** the entries of this list */
  private List<GazetteerNode> entries = new ArrayList<GazetteerNode>();

  /** the content of this list */
  private String content = null;

  /**
   * the separator used to delimit feature name-value pairs in gazetteer
   * lists
   */
  private String separator;

  /** create a new gazetteer list */
  public GazetteerList() {
  }

  /** @return true if the list has been modified after load/store */
  @Override
  public boolean isModified() {
    return isModified;
  }

  /**
   * Sets the modified status of the current list
   * 
   * @param modified is modified flag
   */
  public void setModified(boolean modified) {
    isModified = modified;
  }

  /**
   * Retrieves the current mode of the gaz list
   * 
   * @return the current mode
   */
  public int getMode() {
    return mode;
  }

  /**
   * Sets mode of the gazetteer list
   * 
   * @param m the mode to be set
   */
  public void setMode(int m) {
    if(m != mode) {
      switch(m) {
        case LIST_MODE: {
          mode = m;
          updateContent(content);
          break;
        } // LIST_MODE
        case STRING_MODE: {
          content = this.toString();
          mode = m;
          break;
        } // STRING_MODE
        default: {
          throw new gate.util.GateRuntimeException("Invalid Mode =" + mode
                  + "\nValid modes are:\nLIST_MODE = " + LIST_MODE
                  + "\nSTRING_MODE = " + STRING_MODE);
        } // default
      } // switch
    } // only if different from the current
  } // setMode(int)

  /**
   * Sets the encoding of the list
   * 
   * @param encod the encoding to be set
   */
  public void setEncoding(String encod) {
    encoding = encod;
  }

  /**
   * Gets the encoding of the list
   * 
   * @return the encoding of the list
   */
  public String getEncoding() {
    return encoding;
  }

  /**
   * Loads a gazetteer list
   * 
   * @throws ResourceInstantiationException when the resource cannot be
   *           created
   */
  public void load() throws ResourceInstantiationException {
    load(false);
  }

  /**
   * Loads a gazetteer list
   * 
   * @param isOrdered true if the feature maps used should be ordered
   * @throws ResourceInstantiationException when the resource cannot be
   *           created
   */
  @SuppressWarnings("resource")
  public void load(boolean isOrdered) throws ResourceInstantiationException {
    BufferedReader listReader = null;
    
    try {
      if(null == url) {
        throw new ResourceInstantiationException("URL not specified (null).");
      }

      listReader =
              new BomStrippingInputStreamReader((url).openStream(), encoding);
      String line;
      int linenr = 0;
      Pattern emptyPattern = Pattern.compile("\\s*");
      while(null != (line = listReader.readLine())) {
        linenr++;
        if(emptyPattern.matcher(line).matches()) {
          // skip empty line
          continue;
        }
        GazetteerNode node = null;
        try {
          node = new GazetteerNode(line, separator, isOrdered);
        } catch(Exception ex) {
          throw new GateRuntimeException("Could not read gazetteer entry "
                  + linenr + " from URL " + getURL() + ": " + ex.getMessage(),
                  ex);
        }
        
        entries.add(node);
      } // while

      listReader.close();
    } catch(Exception x) {
      throw new ResourceInstantiationException(x.getClass() + ":"
              + x.getMessage(),x);
    }
    finally {
      IOUtils.closeQuietly(listReader);
    }
    isModified = false;
  } // load ()

  /**
   * Stores the list to the specified url
   * 
   * @throws ResourceInstantiationException
   */
  public void store() throws ResourceInstantiationException {
    try {
      if(null == url) {
        throw new ResourceInstantiationException("URL not specified (null)");
      }

      File fileo = Files.fileFromURL(url);

      fileo.delete();
      OutputStreamWriter listWriter =
              new OutputStreamWriter(new FileOutputStream(fileo), encoding);
      // BufferedWriter listWriter = new BufferedWriter(new
      // FileWriter(fileo));
      Iterator<GazetteerNode> iter = entries.iterator();
      while(iter.hasNext()) {
        listWriter.write(iter.next().toString());
        listWriter.write(13);
        listWriter.write(10);
      }
      listWriter.close();
    } catch(Exception x) {
      throw new ResourceInstantiationException(x.getClass() + ":"
              + x.getMessage());
    }
    isModified = false;
  } // store()

  /**
   * Sets the URL of the list
   * 
   * @param theUrl the URL of the List
   */
  public void setURL(URL theUrl) {
    url = theUrl;
    isModified = true;
  }

  /**
   * Gets the URL of the list
   * 
   * @return the URL of the list
   */
  public URL getURL() {
    return url;
  }

  /**
   * @return the seperator
   */
  public String getSeparator() {
    return separator;
  }

  /**
   * @param separator the separator to set
   */
  public void setSeparator(String separator) {
    this.separator = separator;
  }

  /*--------------implementation of java.util.List--------------------*/
  @Override
  public int size() {
    return entries.size();
  }

  @Override
  public boolean isEmpty() {
    return (0 == entries.size());
  }

  @Override
  public boolean contains(Object o) {
    return entries.contains(o);
  } // contains()

  /**
   * Gets an iterator over the list. It is not dangerous if the iterator
   * is modified since there are no dependencies of entries to other
   * members
   */
  @Override
  public Iterator<GazetteerNode> iterator() {
    return entries.iterator();
  }

  @Override
  public Object[] toArray() {
    return entries.toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return entries.toArray(a);
  }

  @Override
  public boolean add(GazetteerNode o) {
    boolean result = entries.add(o);
    isModified |= result;
    return result;
  } // add()

  @Override
  public boolean remove(Object o) {
    boolean result = entries.remove(o);
    isModified |= result;
    return result;
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return entries.containsAll(c);
  }

  /**
   * Adds entire collection
   * 
   * @param c a collection to be addded
   * @return true if all the elements where Strings and all are
   *         sucessfully added
   */
  @Override
  public boolean addAll(Collection<? extends GazetteerNode> c) {
    Iterator<? extends GazetteerNode> iter = c.iterator();
    GazetteerNode o;
    boolean result = false;

    while(iter.hasNext()) {
      o = iter.next();
      result |= entries.add(o);
    } // while
    isModified |= result;

    return result;
  } // addAll(Collection)

  @Override
  public boolean addAll(int index, Collection<? extends GazetteerNode> c) {
    boolean result = entries.addAll(index, c);
    isModified |= result;
    return result;
  } // addAll(int,Collection)

  @Override
  public boolean removeAll(Collection<?> c) {
    boolean result = entries.removeAll(c);
    isModified |= result;
    return result;
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    boolean result = entries.retainAll(c);
    isModified |= result;
    return result;
  }

  @Override
  public void clear() {
    if(0 < entries.size()) isModified = true;
    entries.clear();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((entries == null) ? 0 : entries.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if(this == obj) return true;
    if(obj == null) return false;
    if(getClass() != obj.getClass()) return false;
    GazetteerList other = (GazetteerList)obj;
    if(entries == null) {
      if(other.entries != null) return false;
    } else if(!entries.equals(other.entries)) return false;
    return true;
  }

  @Override
  public GazetteerNode get(int index) {
    return entries.get(index);
  }

  @Override
  public GazetteerNode set(int index, GazetteerNode element) {
    isModified = true;
    return entries.set(index, element);
  }

  @Override
  public void add(int index, GazetteerNode element) {
    isModified = true;
    entries.add(index, element);
  }

  @Override
  public GazetteerNode remove(int index) {
    int size = entries.size();
    GazetteerNode result = entries.remove(index);
    isModified |= (size != entries.size());
    return result;
  }

  @Override
  public int indexOf(Object o) {
    return entries.indexOf(o);
  }

  @Override
  public int lastIndexOf(Object o) {
    return entries.lastIndexOf(o);
  }

  @Override
  public ListIterator<GazetteerNode> listIterator() {
    return entries.listIterator();
  }

  @Override
  public ListIterator<GazetteerNode> listIterator(int index) {
    return entries.listIterator(index);
  }

  @Override
  public List<GazetteerNode> subList(int fromIndex, int toIndex) {
    return entries.subList(fromIndex, toIndex);
  }

  /**
   * Retrieves the string representation of the gaz list according to
   * its mode. If {@link #LIST_MODE} then all the entries are dumped
   * sequentially to a string. If {@link #STRING_MODE} then the content
   * (a string) of the gaz list is retrieved.
   * 
   * @return the string representation of the gaz list
   */
  @Override
  public String toString() {
    String stres = null;
    switch(mode) {
      case LIST_MODE: {
        StringBuffer result = new StringBuffer();
        String entry = null;
        for(int i = 0; i < entries.size(); i++) {
          GazetteerNode node = entries.get(i);
          entry = node.getEntry().trim();
          if(entry.length() > 0) {
            result.append(entry);
            Map<String,Object> featureMap = node.getFeatureMap();
            if(featureMap != null && (featureMap.size() > 0)) {
              result.append(node.featureMapToString(featureMap));
            }
            result.append("\n");
          }// if
        }// for
        stres = result.toString();
        break;
      }
      case STRING_MODE: {
        stres = content;
        break;
      }
      default: {
        throw new gate.util.GateRuntimeException("Invalid Mode =" + mode
                + "\nValid modes are:\nLIST_MODE = " + LIST_MODE
                + "\nSTRING_MODE = " + STRING_MODE);
      }
    } // switch
    return stres;
  }// toString()

  /**
   * Updates the content of the gaz list with the given parameter.
   * Depends on the mode of the gaz list. In the case of
   * {@link #LIST_MODE} the new content is parsed and loaded as single
   * nodes through the {@link java.util.List} interface. In the case of
   * {@link #STRING_MODE} the new content is stored as a String and is
   * not parsed.
   * 
   * @param newContent the new content of the gazetteer list
   */
  public void updateContent(String newContent) {
    switch(mode) {
      case STRING_MODE: {
        content = newContent;
        break;
      }
      case LIST_MODE: {
        BufferedReader listReader;
        listReader = new BufferedReader(new StringReader(newContent));
        String line;
        List<GazetteerNode> tempEntries = new ArrayList<GazetteerNode>();
        try {
          while(null != (line = listReader.readLine())) {
            tempEntries.add(new GazetteerNode(line, separator));
          } // while
          listReader.close();
        } catch(IOException x) {
          /** should never be thrown */
          throw new gate.util.LuckyException("IOException :" + x.getMessage());
        }

        isModified = !tempEntries.equals(entries);
        clear();
        entries = tempEntries;
        break;
      } // LIST_MODE
      default: {
        throw new gate.util.GateRuntimeException("Invalid Mode =" + mode
                + "\nValid modes are:\nLIST_MODE = " + LIST_MODE
                + "\nSTRING_MODE = " + STRING_MODE);
      }// default
    } // switch mode
  } // updateContent(String)

} // Class GazetteerList
