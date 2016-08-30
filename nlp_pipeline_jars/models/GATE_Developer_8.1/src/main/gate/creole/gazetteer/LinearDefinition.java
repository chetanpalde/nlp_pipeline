/*
 *  LinearDefinition.java
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
 *  $Id: LinearDefinition.java 17594 2014-03-08 12:07:09Z markagreenwood $
 */
package gate.creole.gazetteer;

import gate.creole.ResourceInstantiationException;
import gate.util.BomStrippingInputStreamReader;
import gate.util.Files;
import gate.util.GateRuntimeException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;

/**
 * Represents a Linear Definition [lists.def] file <br>
 * The normal usage of the class will be * construct it * setURL * load
 * * change * store
 */
public class LinearDefinition extends gate.creole.AbstractLanguageResource
                                                                          implements
                                                                          List<LinearNode> {

  private static final long serialVersionUID = 4050479036709221175L;

  /** the default encoding of the definition */
  private final static String ENCODING = "UTF-8";

  /** the list of nodes */
  private List<LinearNode> nodes = new ArrayList<LinearNode>();

  /** the URL of the definition */
  private URL url;

  /** set of lists as strings */
  private List<String> lists = new ArrayList<String>();

  /** the encoding of the list */
  private String encoding = "UTF-8";

  /** a mapping between a list and a node */
  private Map<String, LinearNode> nodesByList =
          new HashMap<String, LinearNode>();

  /** a map of gazetteer lists by nodes. this is loaded on loadLists */
  private Map<LinearNode, GazetteerList> gazListsByNode =
          new HashMap<LinearNode, GazetteerList>();

  /** flag whether the definition has been modified after loading */
  private boolean isModified = false;

  /**
   * the separator used to delimit feature name-value pairs in gazetteer
   * lists
   */
  private String separator;

  public LinearDefinition() {
  }

  /**
   * Sets the encoding of the linear def
   * 
   * @param encod the encoding to be set
   */
  public void setEncoding(String encod) {
    encoding = encod;
  }

  /**
   * Gets the encoding of the linear def
   * 
   * @return the encoding of the list
   */
  public String getEncoding() {
    return encoding;
  }

  /**
   * Loads the gazetteer lists and maps them to the nodes
   * 
   * @return a map of nodes vs GazetteerLists
   * @throws ResourceInstantiationException when the resource cannot be
   *           created
   */
  public Map<LinearNode, GazetteerList> loadLists()
          throws ResourceInstantiationException {
    return loadLists(false);
  }

  /**
   * Loads the gazetteer lists and maps them to the nodes
   * 
   * @return a map of nodes vs GazetteerLists
   * @param isOrdered true if the feature maps used should be ordered
   * @throws ResourceInstantiationException when the resource cannot be
   *           created
   */
  public Map<LinearNode, GazetteerList> loadLists(boolean isOrdered)
          throws ResourceInstantiationException {
    try {
      gazListsByNode = new HashMap<LinearNode, GazetteerList>();
      Iterator<LinearNode> inodes = nodes.iterator();
      while(inodes.hasNext()) {
        LinearNode node = inodes.next();

        GazetteerList list = new GazetteerList();
        list.setSeparator(separator);
        URL lurl = new URL(url, node.getList());
        list.setURL(lurl);
        list.setEncoding(encoding);
        list.load(isOrdered);

        gazListsByNode.put(node, list);
      } // while inodes
    } catch(Exception ex) {
      throw new ResourceInstantiationException(ex);
    }
    return gazListsByNode;
  } // loadLists()

  /**
   * Loads a single gazetteer list given a name
   * 
   * @param listName the name of the list to be loaded
   * @return the loaded gazetteer list
   * @throws ResourceInstantiationException
   */
  public GazetteerList loadSingleList(String listName)
          throws ResourceInstantiationException {
    return loadSingleList(listName, false);
  }

  /**
   * Loads a single gazetteer list given a name
   * 
   * @param listName the name of the list to be loaded
   * @param isOrdered true if the feature maps used should be ordered
   * @return the loaded gazetteer list
   * @throws ResourceInstantiationException
   */
  public GazetteerList loadSingleList(String listName, boolean isOrdered)
          throws ResourceInstantiationException {
    GazetteerList list = new GazetteerList();
    list.setSeparator(separator);
    try {

      try {
        URL lurl = new URL(url, listName);
        list.setURL(lurl);
        list.load(isOrdered);
      } catch(Exception x) {
        String path = url.getPath();
        int slash = path.lastIndexOf("/");
        if(-1 != slash) {
          path = path.substring(0, slash + 1);
        }

        File f = new File(path + listName);

        if(!f.exists()) f.createNewFile();

        URL lurl = new URL(url, listName);
        list.setURL(lurl);
        list.load(isOrdered);

      }

    } catch(MalformedURLException murle) {
      throw new ResourceInstantiationException(murle);
    } catch(IOException ioex) {
      throw new ResourceInstantiationException(ioex);
    }
    return list;
  } // loadSingleList

  /**
   * Gets the lists by node map
   * 
   * @return a map of nodes vs lists
   */
  public Map<LinearNode, GazetteerList> getListsByNode() {
    return gazListsByNode;
  }

  /**
   * Gets a map of lists names vs nodes
   * 
   * @return a map of lists names vs nodes
   */
  public Map<String, LinearNode> getNodesByListNames() {
    return nodesByList;
  }

  /**
   * Gets the value of the isModified flag.
   * 
   * @return true if the definition has been modified
   */
  @Override
  public boolean isModified() {
    return isModified;
  }

  public void setModified(boolean modified) {
    isModified = modified;
  }

  /**
   * Gets the url of this linear definition
   * 
   * @return the url of this linear definition
   */
  public URL getURL() {
    return url;
  }

  /**
   * Sets the url of this linear definition
   * 
   * @param aUrl the url of this linear definition
   */
  public void setURL(URL aUrl) {
    url = aUrl;
  }

  /**
   * Loads linear definition if url is set
   */
  public void load() throws ResourceInstantiationException {
    if(null == url) {
      throw new ResourceInstantiationException("URL not set (null).");
    }
    BufferedReader defReader = null;
    try {
      if("file".equals(url.getProtocol())) {
        File definitionFile = Files.fileFromURL(url);
        // create an new definition file only if not existing
        definitionFile.createNewFile();
      }
      defReader =
              new BomStrippingInputStreamReader((url).openStream(), ENCODING);

      String line;
      LinearNode node;
      while(null != (line = defReader.readLine())) {
        node = new LinearNode(line);

        this.add(node);

      } // while
      isModified = false;
    } catch(Exception x) {
      throw new ResourceInstantiationException(x);
    } finally {
      IOUtils.closeQuietly(defReader);
    }
  } // load();

  /**
   * Stores this to a definition file.
   */
  public void store() throws ResourceInstantiationException {
    if(null == url) {
      throw new ResourceInstantiationException("URL not set.(null)");
    }
    try {

      File fileo = Files.fileFromURL(url);
      fileo.delete();
      BufferedWriter defWriter = new BufferedWriter(new FileWriter(fileo));
      Iterator<LinearNode> inodes = nodes.iterator();
      while(inodes.hasNext()) {
        defWriter.write(inodes.next().toString());
        defWriter.newLine();
      }
      defWriter.close();
      isModified = false;
    } catch(Exception x) {
      throw new ResourceInstantiationException(x);
    }

  } // store();

  /**
   * Gets gazetteer lists of this definition. note that a new list is
   * created so the adding and removing of lists will not affect the
   * internal members. Also there is no setLists method since the
   * leading member of the class is nodes, and lists cannot be added
   * individually without being associated with a node.
   * 
   * @return a list of the gazetteer lists names
   */
  public List<String> getLists() {
    return new ArrayList<String>(lists);
  }

  /**
   * get the nodes of the definition as a list
   * 
   * @return the list of nodes
   */
  public List<LinearNode> getNodes() {
    return new ArrayList<LinearNode>(nodes);
  }

  /**
   * Gets the set of all major types in this definition
   * 
   * @return the set of all major types present in this definition
   */
  public Set<String> getMajors() {
    Set<String> result = new HashSet<String>();
    for(int i = 0; i < nodes.size(); i++) {
      String maj = nodes.get(i).getMajorType();
      if(null != maj) result.add(maj);
    }
    return result;
  } // getMajors

  /**
   * Gets the set of all minor types in this definition
   * 
   * @return the set of all minor types present in this definition
   */
  public Set<String> getMinors() {
    Set<String> result = new HashSet<String>();
    for(int i = 0; i < nodes.size(); i++) {
      String min = nodes.get(i).getMinorType();
      if(null != min) result.add(min);
    }
    result.add("");
    return result;
  } // getMinors()

  /**
   * Gets the set of all languages in this definition
   * 
   * @return the set of all languages present in this definition
   */
  public Set<String> getLanguages() {
    Set<String> result = new HashSet<String>();
    for(int i = 0; i < nodes.size(); i++) {
      String lang = nodes.get(i).getLanguage();
      if(null != lang) result.add(lang);
    }
    result.add("");
    return result;
  } // getMinors()

  /*---implementation of interface java.util.List---*/
  @Override
  public boolean addAll(int index, Collection<? extends LinearNode> c) {
    int size = nodes.size();
    Iterator<? extends LinearNode> iter = c.iterator();
    LinearNode o;
    while(iter.hasNext()) {
      o = iter.next();
      add(index, o);
      // instance of linearnode
    } // while

    boolean result = (size != nodes.size());
    isModified |= result;
    return result;
  }

  @Override
  public LinearNode get(int index) {
    return nodes.get(index);
  }

  @Override
  public LinearNode set(int index, LinearNode element) {
    throw new UnsupportedOperationException(
            "this method has not been implemented");
  }

  /**
   * Add a node to this LinearDefinition.
   * <p>
   * NOTE: this will throw a GateRuntimeException if anything goes wrong
   * when reading the list.
   * 
   * @param index
   * @param ln
   */
  @Override
  public void add(int index, LinearNode ln) {
    String list = ln.getList();
    if(!nodesByList.containsKey(list)) {
      try {
        GazetteerList gl = loadSingleList(list);
        gazListsByNode.put(ln, gl);
        nodes.add(index, ln);
        nodesByList.put(list, ln);
        lists.add(list);
        isModified = true;
      } catch(ResourceInstantiationException x) {
        throw new GateRuntimeException("Error loading list: " + list + ": "
                + x.getMessage(), x);
      }
    } // if unique
  }

  @Override
  public LinearNode remove(int index) {
    LinearNode result = null;
    int size = nodes.size();
    result = nodes.remove(index);
    if(null != result) {
      String list = result.getList();
      lists.remove(list);
      nodesByList.remove(list);
      gazListsByNode.remove(result);
      isModified |= (size != nodes.size());
    }
    return result;
  }

  @Override
  public int indexOf(Object o) {
    return nodes.indexOf(o);
  }

  @Override
  public int lastIndexOf(Object o) {
    return nodes.lastIndexOf(o);
  }

  @Override
  public ListIterator<LinearNode> listIterator() {
    throw new UnsupportedOperationException("this method is not implemented");
  }

  @Override
  public ListIterator<LinearNode> listIterator(int index) {
    throw new UnsupportedOperationException("this method is not implemented");
  }

  @Override
  public List<LinearNode> subList(int fromIndex, int toIndex) {
    return nodes.subList(fromIndex, toIndex);
  } // class SafeIterator

  @Override
  public int size() {
    return nodes.size();
  }

  @Override
  public boolean isEmpty() {
    return 0 == nodes.size();
  }

  @Override
  public boolean contains(Object o) {
    return nodes.contains(o);
  }

  @Override
  public Iterator<LinearNode> iterator() {
    return new SafeIterator();
  }

  @Override
  public Object[] toArray() {
    return nodes.toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return nodes.toArray(a);
  }

  /**
   * Adds a new node, only if its list is new and uniquely mapped to
   * this node.
   * <p>
   * NOTE: this will throw a GateRuntimeException if anything goes wrong
   * reading the list.
   * 
   * @param o a node
   * @return true if the list of node is not already mapped with another
   *         node.
   */
  @Override
  public boolean add(LinearNode o) {
    boolean result = false;
    String list = o.getList();
    if(!nodesByList.containsKey(list)) {
      try {
        GazetteerList gl = loadSingleList(list);
        gazListsByNode.put(o, gl);
        result = nodes.add(o);
        nodesByList.put(list, o);
        lists.add(list);
        isModified = true;
      } catch(ResourceInstantiationException x) {
        throw new GateRuntimeException("Error loading list: " + list + ": "
                + x.getMessage(), x);
        // result = false;
      }
    } // if unique
    return result;
  } // add()

  @Override
  public boolean remove(Object o) {
    boolean result = false;
    int size = nodes.size();
    if(o instanceof LinearNode) {
      result = nodes.remove(o);
      String list = ((LinearNode)o).getList();
      lists.remove(list);
      nodesByList.remove(list);
      gazListsByNode.remove(o);
      isModified |= (size != nodes.size());
    } // if linear node
    return result;
  }// remove

  @Override
  public boolean containsAll(Collection<?> c) {
    return nodes.containsAll(c);
  }

  @Override
  public boolean addAll(Collection<? extends LinearNode> c) {
    boolean result = false;
    Iterator<? extends LinearNode> iter = c.iterator();
    LinearNode o;
    while(iter.hasNext()) {
      o = iter.next();
      result |= add(o);
    } // while
    return result;
  } // addAll()

  @Override
  public boolean removeAll(Collection<?> c) {
    boolean result = false;
    Iterator<?> iter = c.iterator();
    Object o;
    while(iter.hasNext()) {
      o = iter.next();
      result |= remove(o);
    }
    return result;
  }// removeAll()

  @Override
  public boolean retainAll(Collection<?> c) {
    int aprioriSize = nodes.size();
    List<LinearNode> scrap = new ArrayList<LinearNode>();

    LinearNode node;
    Iterator<LinearNode> inodes = nodes.iterator();
    while(inodes.hasNext()) {
      node = inodes.next();
      if(c.contains(node)) {
        scrap.add(node);
      }
    } // for

    removeAll(scrap);
    isModified |= (aprioriSize != nodes.size());
    return (aprioriSize != nodes.size());
  }

  @Override
  public void clear() {
    nodes.clear();
    lists.clear();
    nodesByList.clear();
    gazListsByNode.clear();
    isModified = true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((lists == null) ? 0 : lists.hashCode());
    result = prime * result + ((nodes == null) ? 0 : nodes.hashCode());
    result =
            prime * result
                    + ((nodesByList == null) ? 0 : nodesByList.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if(this == obj) return true;
    if(obj == null) return false;
    if(getClass() != obj.getClass()) return false;
    LinearDefinition other = (LinearDefinition)obj;
    if(lists == null) {
      if(other.lists != null) return false;
    } else if(!lists.equals(other.lists)) return false;
    if(nodes == null) {
      if(other.nodes != null) return false;
    } else if(!nodes.equals(other.nodes)) return false;
    if(nodesByList == null) {
      if(other.nodesByList != null) return false;
    } else if(!nodesByList.equals(other.nodesByList)) return false;
    return true;
  }

  /*---end of implementation of interface java.util.List---*/

  /*-----------internal classes -------------*/

  /**
   * SafeIterator class provides an iterator which is safe to be
   * iterated and objects removed from it
   */
  private class SafeIterator implements Iterator<LinearNode> {
    private Iterator<LinearNode> iter = LinearDefinition.this.nodes.iterator();

    private boolean removeCalled = false;

    private LinearNode last = null;

    @Override
    public boolean hasNext() {
      return iter.hasNext();
    }

    @Override
    public LinearNode next() {
      removeCalled = false;
      last = iter.next();
      return last;
    }

    @Override
    public void remove() {
      if(!removeCalled && null != last) {
        LinearDefinition.this.remove(last);
      }// if possible remove
      removeCalled = true;
    } // remove

  } // class SafeIterator

  /**
   * @return the separator
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

} // class LinearDefinition
