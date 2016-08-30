/*
 * Copyright (c) 2009-2013, The University of Sheffield.
 * 
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * Licensed under the GNU Library General Public License, Version 3, June 2007
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 */

package gate.alchemyAPI;

import gate.creole.ExecutionException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.RunTime;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alchemyapi.api.AlchemyAPI_NamedEntityParams;

/**
 * @author Niraj Aswani
 * @author Mark A. Greenwood
 */
@CreoleResource(name = "AlchemyAPI: Entity Extraction", comment = "Runs the AlchemyAPI Entity Extraction service on a GATE document", icon = "Alchemy")
public class EntityExtraction extends KeywordExtraction {

  private static final long serialVersionUID = -8443994795704361590L;

  /** debug */
  private boolean DEBUG = false;

  @RunTime
  @CreoleParameter(defaultValue = "Mention")
  public void setAnnotationType(String annotationType) {
    this.annotationType = annotationType;
  }

  /**
   * Using URLConnection to connect to alchemy API
   * 
   * @param text
   * @return
   * @throws ExecutionException
   */
  @Override
  protected List<TextSpan> process(String text) throws ExecutionException {
    
    if(DEBUG) System.out.println("About to process: " + text);

    // the list of results we will evenutally return
    List<TextSpan> toReturn = new ArrayList<TextSpan>();

    // the params to configure the service
    AlchemyAPI_NamedEntityParams params = new AlchemyAPI_NamedEntityParams();

    // get the XML result doc back from the service
    Document doc = null;
    try {
      doc = alchemy.TextGetRankedNamedEntities(text, params);
    } catch(Exception e) {
      e.printStackTrace();
      throw new ExecutionException(e);
    }

    // convert the XML into TextSpan instances
    NodeList entitiesList = doc.getElementsByTagName("entity");
    for(int i = 0; i < entitiesList.getLength(); i++) {
      Node n = entitiesList.item(i);
      NodeList children = n.getChildNodes();
      TextSpan r = new TextSpan();

      for(int j = 0; j < children.getLength(); j++) {
        Node cn = children.item(j);
        if(cn.getNodeName().equals("type")) {
          r.featureMap.put("type", cn.getTextContent());
        } else if(cn.getNodeName().equals("relevance")) {
          r.featureMap.put("relevance", cn.getTextContent());
        } else if(cn.getNodeName().equals("text")) {
          r.text = cn.getTextContent();
        } else if(cn.getNodeName().equals("disambiguated")) {
          NodeList disambChildren = cn.getChildNodes();
          for(int k = 0; k < disambChildren.getLength(); k++) {
            Node dcn = disambChildren.item(k);
            if(!dcn.getNodeName().equals("#text"))
              r.featureMap.put(dcn.getNodeName(), dcn.getTextContent());
          }
        }
      }
      
      if(DEBUG) System.out.println(r);
      
      toReturn.add(r);
    }
    return toReturn;
  }
}