// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3)
// Source File Name: POSMapper.java

package com.ontotext.russie.morph;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Resource;
import gate.annotation.AnnotationSetImpl;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;
import gate.util.OffsetComparator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import com.ontotext.russie.RussIEConstants;

@CreoleResource(name = "POS Mapper",
    comment = "Map complex Russian morphology tags into simpler POS categories",
    helpURL = "http://gate.ac.uk/userguide/sec:misc-creole:language-plugins:russian")
public class POSMapper extends AbstractLanguageAnalyser implements
  RussIEConstants {

  private static final long serialVersionUID = 748287388332117288L;

  public Resource init() throws ResourceInstantiationException {
    fireStatusChanged("Init POS Mapper structures ...");
    initMap();
    fireProcessFinished();
    return this;
  }

  private void initMap() {
    categoriesMap = new HashMap<String, String>();
    String adjTypes =
      "Au,Aupfpaa,Aupfpai,Aupfpd,Aupfpg,Aupfpi,Aupfpl,Aupfpn,Aupfsfa,Aupfsfd,Aupfsfg,Aupfsfi,Aupfsfl,Aupfsfn,Aupfsmaa,Aupfsmai,Aupfsmd,Aupfsmg,Aupfsmi,Aupfsml,Aupfsmn,Aupfsna,Aupfsnd,Aupfsng,Aupfsni,Aupfsnl,Aupfsnn,Aupsp,Aupssf,Aupssm,Aupssn";
    String type;
    for(StringTokenizer tok = new StringTokenizer(adjTypes, ","); tok
      .hasMoreElements(); categoriesMap.put(type, "JJ"))
      type = tok.nextToken();

    fireStatusChanged("Categories in map count: " + categoriesMap.size());
    categoriesMap.put("Auc", "JJR");
    categoriesMap.put("C", "CC");
    categoriesMap.put("D", "RB");
    categoriesMap.put("T", "RP");
    categoriesMap.put("P", "PP");
    categoriesMap.put("R", "IN");
    fireStatusChanged("Categories in map count: " + categoriesMap.size());
    String nnTypes =
      "Nfa,Nfi,Nma,Nmi,Nmi2g,Nmi2l,Nmi2lg,Nmi2lgs2g,Nmi2lgs2l,Nmi2ls2g,Nna,Nni,Nfasa,Nfasd,Nfasg,Nfasi,Nfasl,Nfasn,Nfisa,Nfisd,Nfisg,Nfisi,Nfisl,Nfisn,Nmasa,Nmasd,Nmasg,Nmasi,Nmasl,Nmasn,Nmi2gsa,Nmi2gsd,Nmi2gsg,Nmi2gsi,Nmi2gsl,Nmi2gsn,Nmi2lgsa,Nmi2lgsd,Nmi2lgsg,Nmi2lgsi,Nmi2lgsl,Nmi2lgsn,Nmi2lsa,Nmi2lsd,Nmi2lsg,Nmi2lsi,Nmi2lsl,Nmi2lsn,Nmisa,Nmisd,Nmisg,Nmisi,Nmisl,Nmisn,Nnasa,Nnasd,Nnasg,Nnasi,Nnasl,Nnasn,Nnisa,Nnisd,Nnisg,Nnisi,Nnisl,Nnisn";
    for(StringTokenizer tok = new StringTokenizer(nnTypes, ","); tok
      .hasMoreElements(); categoriesMap.put(type, "NN"))
      type = tok.nextToken();

    fireStatusChanged("Categories in map count: " + categoriesMap.size());
    String nnsTypes =
      "Nfapa,Nfapd,Nfapg,Nfapi,Nfapl,Nfapn,Nfipa,Nfipd,Nfipg,Nfipi,Nfipl,Nfipn,Nmapa,Nmapd,Nmapg,Nmapi,Nmapl,Nmapn,Nmi2gpa,Nmi2gpd,Nmi2gpg,Nmi2gpi,Nmi2gpl,Nmi2gpn,Nmi2lgpa,Nmi2lgpd,Nmi2lgpg,Nmi2lgpi,Nmi2lgpl,Nmi2lgpn,Nmi2lpa,Nmi2lpd,Nmi2lpg,Nmi2lpi,Nmi2lpl,Nmi2lpn,Nmipa,Nmipd,Nmipg,Nmipi,Nmipl,Nmipn,Nnapa,Nnapd,Nnapg,Nnapi,Nnapl,Nnapn,Nnipa,Nnipd,Nnipg,Nnipi,Nnipl,Nnipn,Np,Nppa,Nppd,Nppg,Nppi,Nppl,Nppn";
    for(StringTokenizer tok = new StringTokenizer(nnsTypes, ","); tok
      .hasMoreElements(); categoriesMap.put(type, "NNS"))
      type = tok.nextToken();

    String nnpTypes =
      "Npfa,Npfi,Npma,Npmi,Npmi2g,Npmi2l,Npmi2lg,Npmi2lgs2g,Npmi2lgs2l,Npmi2ls2g,Npna,Npni,Npfasa,Npfasd,Npfasg,Npfasi,Npfasl,Npfasn,Npfisa,Npfisd,Npfisg,Npfisi,Npfisl,Npfisn,Npmasa,Npmasd,Npmasg,Npmasi,Npmasl,Npmasn,Npmi2gsa,Npmi2gsd,Npmi2gsg,Npmi2gsi,Npmi2gsl,Npmi2gsn,Npmi2lgsa,Npmi2lgsd,Npmi2lgsg,Npmi2lgsi,Npmi2lgsl,Npmi2lgsn,Npmi2lsa,Npmi2lsd,Npmi2lsg,Npmi2lsi,Npmi2lsl,Npmi2lsn,Npmisa,Npmisd,Npmisg,Npmisi,Npmisl,Npmisn,Npnasa,Npnasd,Npnasg,Npnasi,Npnasl,Npnasn,Npnisa,Npnisd,Npnisg,Npnisi,Npnisl,Npnisn";
    for(StringTokenizer tok = new StringTokenizer(nnpTypes, ","); tok
      .hasMoreElements(); categoriesMap.put(type, "NNP"))
      type = tok.nextToken();

    String nnpsTypes =
      "Npfapa,Npfapd,Npfapg,Npfapi,Npfapl,Npfapn,Npfipa,Npfipd,Npfipg,Npfipi,Npfipl,Npfipn,Npmapa,Npmapd,Npmapg,Npmapi,Npmapl,Npmapn,Npmi2gpa,Npmi2gpd,Npmi2gpg,Npmi2gpi,Npmi2gpl,Npmi2gpn,Npmi2lgpa,Npmi2lgpd,Npmi2lgpg,Npmi2lgpi,Npmi2lgpl,Npmi2lgpn,Npmi2lpa,Npmi2lpd,Npmi2lpg,Npmi2lpi,Npmi2lpl,Npmi2lpn,Npmipa,Npmipd,Npmipg,Npmipi,Npmipl,Npmipn,Npnapa,Npnapd,Npnapg,Npnapi,Npnapl,Npnapn,Npnipa,Npnipd,Npnipg,Npnipi,Npnipl,Npnipn,Npp,Npppa,Npppd,Npppg,Npppi,Npppl,Npppn";
    for(StringTokenizer tok = new StringTokenizer(nnpsTypes, ","); tok
      .hasMoreElements(); categoriesMap.put(type, "NNPS"))
      type = tok.nextToken();

    fireStatusChanged("Categories in map count: " + categoriesMap.size());
    showMessage("Categories in map count: " + categoriesMap.size());
  }

  public void execute() throws ExecutionException {
    if(super.document == null)
      throw new ExecutionException("No document to process!");
    AnnotationSet annotationSet;
    if(inputASName == null || inputASName.equals(""))
      annotationSet = super.document.getAnnotations();
    else annotationSet = super.document.getAnnotations(inputASName);
    if(annotationSet == null) {
      showMessage("No annotation set!");
      return;
    } else {
      fireStatusChanged("Mapping of morphology information...");
      mapCategories(annotationSet);
      fireProcessFinished();
      fireStatusChanged("POS Mapper processing finished!");
      return;
    }
  }

  private void mapCategories(AnnotationSet annSet) {
    AnnotationSet msdSet = annSet.get("MSD");
    if(msdSet == null) {
      showMessage("No annotations from type MSD");
      return;
    }
    AnnotationSetImpl tokenSet = (AnnotationSetImpl)annSet.get("Token");
    if(tokenSet == null) {
      showMessage("No annotations from type Token");
      return;
    }
    HashSet<String> mappedTypes = new HashSet<String>();
    Annotation list[] = msdSet.toArray(new Annotation[mappedTypes.size()]);
    Arrays.sort(list, new OffsetComparator());
    for(int index = 0; index < list.length;) {
      Annotation msdAnn;
      String tokType;
      do {
        msdAnn = list[index];
        String msdType = (String)msdAnn.getFeatures().get("type");
        if(msdType != null) {
          tokType = categoriesMap.get(msdType);
          if(tokType != null) mappedTypes.add(tokType);
        }
      } while(++index < list.length && msdAnn.compareTo(list[index]) == 0);
      tokType = "";
      for(Iterator<String> it = mappedTypes.iterator(); it.hasNext();)
        tokType = tokType + it.next() + " ";

      tokType = tokType.trim();
      AnnotationSet singleToken =
        tokenSet.getStrict(msdAnn.getStartNode().getOffset(), msdAnn
          .getEndNode().getOffset());
      if(singleToken.size() > 0 && tokType.length() > 0) {
        Annotation tokAnn = singleToken.iterator().next();
        tokAnn.getFeatures().put("category", tokType);
      } else if(singleToken.size() == 0)
        showMessage("No token annotation for MSD annotation: " + msdAnn);
      else showMessage("No recognized category for MSD annotation: " + msdAnn);
      mappedTypes.clear();
    }

  }

  @Optional
  @RunTime
  @CreoleParameter(comment = "The name of the annotation set containing "
    + "MSD and Token annotations")
  public void setInputASName(String newInputASName) {
    inputASName = newInputASName;
  }

  public String getInputASName() {
    return inputASName;
  }

  private void showMessage(String s) {
  }

  protected static final boolean DEBUG = false;

  protected static final boolean DETAILED_DEBUG = false;

  protected String inputASName;

  protected Map<String, String> categoriesMap;
}
