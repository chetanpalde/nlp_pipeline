/*
 * ItemScope
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

import gate.Annotation;
import gate.AnnotationSet;
import gate.Factory;
import gate.FeatureMap;
import gate.Utils;
import gate.util.InvalidOffsetException;

import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ItemScope {

  private String annotation;

  private Map<String, String> restrictions;

  private URI itemtype = null;

  private String itemprop = null;

  private String itemid = null;

  private Map<String, String> metadata;

  public ItemScope(String annotationType, URI itemType) {
    this.annotation = annotationType;
    this.itemtype = itemType;
  }

  public String getAnnotationType() {
    return annotation;
  }

  public void setAnnotationType(String annotationType) {
    this.annotation = annotationType;
  }

  public URI getItemType() {
    return itemtype;
  }

  public void setItemType(URI itemType) {
    this.itemtype = itemType;
  }

  public String getItemProp() {
    return itemprop;
  }

  public void setItemProp(String itemProp) {
    this.itemprop = itemProp;
  }

  public AnnotationSet getMatchingAnnotations(AnnotationSet annotations) {
    FeatureMap params = Factory.newFeatureMap();
    if(restrictions != null) params.putAll(restrictions);
    return annotations.get(annotation, params);
  }

  public Set<Integer> addMicrodata(Annotation annotation, Boolean nameAsMeta,
          AnnotationSet outputAS) throws InvalidOffsetException {
    
    Set<Integer> created = new HashSet<Integer>();
    
    FeatureMap params = new SortedFeatureMap("itemscope", "itemtype", "itemid");
    params.put("itemscope", "itemscope");
    params.put("itemtype", itemtype.toString());
    if(itemid != null && annotation.getFeatures().containsKey(itemid)) {
      params.put("itemid", annotation.getFeatures().get(itemid).toString());
    }
    
    created.add(outputAS.add(annotation.getStartNode().getOffset(), annotation.getEndNode()
            .getOffset(), "span", params));

    if(metadata != null) {
      for(Map.Entry<String, String> entry : metadata.entrySet()) {
        if(annotation.getFeatures().containsKey(entry.getKey())) {
          params = Factory.newFeatureMap();
          params.put("itemprop", entry.getValue());
          params.put("content",
                  "" + annotation.getFeatures().get(entry.getKey()));
          created.add(outputAS.add(annotation.getStartNode().getOffset(), annotation
                  .getStartNode().getOffset(), "meta", params));
        }
      }
    }

    if(itemprop != null) {
      params = Factory.newFeatureMap();
      params.put("itemprop", itemprop);
      if(!nameAsMeta) {
        created.add(outputAS.add(annotation.getStartNode().getOffset(), annotation
                .getEndNode().getOffset(), "span", params));
      } else {
        params.put("content",
                Utils.contentFor(outputAS.getDocument(), annotation).toString());
        created.add(outputAS.add(annotation.getStartNode().getOffset(), annotation
                .getStartNode().getOffset(), "meta", params));
      }
    }
    
    return created;
  }
}
