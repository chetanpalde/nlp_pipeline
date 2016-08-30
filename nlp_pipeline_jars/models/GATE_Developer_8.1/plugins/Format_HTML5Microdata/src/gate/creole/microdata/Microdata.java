/*
 * Microdata
 * 
 * Copyright (c) 2011-2014, The University of Sheffield.
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 3, June 2007
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 * 
 * Mark A. Greenwood, 11/06/2011
 */

package gate.creole.microdata;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class Microdata {

  private List<ItemScope> itemscopes = new ArrayList<ItemScope>();

  /**
   * Get the list (note order is important) of the ItemScope instances that
   * describe how GATE annotations are converted into HTML Microdata
   * 
   * @return an ordered list of ItemScope instances
   */
  public List<ItemScope> getItemscopes() {
    return itemscopes;
  }

  /**
   * Create an instance of this class from an XML configuration file.
   * 
   * @param url
   *          the location of the configuration file
   * @return a Microdata instance describing the annotations to embed
   * @throws IOException
   *           if a problem occurs loading the configuration
   */
  protected static Microdata load(URL url) throws IOException {
    // just use XStream to re-create an instance of this class from the XML file
    return (Microdata)xstream.fromXML(url.openStream());
  }

  private static XStream xstream = null;

  static {
    xstream = new XStream();
    xstream.setClassLoader(Microdata.class.getClassLoader());
    xstream.alias("itemscope", ItemScope.class);
    xstream.alias("microdata", Microdata.class);
    xstream.addImplicitCollection(Microdata.class, "itemscopes");
    xstream.registerConverter(new AbstractSingleValueConverter() {
      public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
        return clazz.equals(URI.class);
      }

      public Object fromString(String str) {
        try {
          return new URI(str);
        } catch(Exception e) {
          throw new RuntimeException(e);
        }
      }
    });

    xstream.registerConverter(new Converter() {
      @SuppressWarnings("rawtypes")
      public boolean canConvert(Class type) {
        return type.equals(HashMap.class);
      }

      public void marshal(Object source, HierarchicalStreamWriter writer,
              MarshallingContext context) {
        throw new RuntimeException(
                "Writing config files is not currently supported!");
      }

      @SuppressWarnings({"rawtypes", "unchecked"})
      public Object unmarshal(HierarchicalStreamReader reader,
              UnmarshallingContext context) {
        HashMap map = new HashMap();
        while(reader.hasMoreChildren()) {

          try {
            if(reader.getNodeName().equals("restrictions")) {
              // Elements in this map look like
              // <feature name="locType">city</feature>

              String feature = reader.getAttribute("name");
              reader.moveDown();
              String value = reader.getValue();
              reader.moveUp();
              map.put(feature, value);
            } else if(reader.getNodeName().equals("metadata")) {
              // Elements in this map look like
              // <content itemprop="gender">gender</word>

              String itemprop = reader.getAttribute("itemprop");
              reader.moveDown();
              String feature = reader.getValue().toLowerCase();
              reader.moveUp();
              map.put(feature, itemprop);
            }
          } catch(Exception e) {
            e.printStackTrace();
          }
        }
        return map;
      }
    });
  }
}
