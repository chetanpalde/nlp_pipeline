package gate.creole.gazetteer;

import gate.Factory;
import gate.Resource;
import gate.creole.CustomDuplication;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;

/**
 * Provides a way to efficiently multi-thread a DefaultGazetteer.
 * 
 * The reccommended way to multithread a gate pipeline is to have a
 * seperate instance per thread (or a resource pool). This is not ideal
 * when using large Gazetteers as these can take a long time to
 * initialise and take up a lot of memory. This class provides a way to
 * bootstrap a new gazetteer instance off of an existing gazetteer
 * instance while still maintaining all thread level variables.
 * {@link DefaultGazetteer} implements {@link CustomDuplication} using
 * this class, so the easiest way to build multiple copies of a
 * {@link DefaultGazetteer} PR that share a single FSM is to create one
 * in the usual way and then use {@link Factory#duplicate(Resource)} to
 * copy it.
 * 
 * NOTE: It is (probably) impossible to use this class from within
 * either the Gaze user interface or from a .gapp application file. You
 * should only use this PR when embedding GATE within another
 * application and initialise it specifically. There is no reason a
 * DefaultGazetteer loaded via a .gapp file cannot be used to bootstrap
 * this PR however.
 * 
 * @author Matt Nathan
 */
@CreoleResource(isPrivate = true, name = "Sharable Gazettee")
public class SharedDefaultGazetteer extends DefaultGazetteer {

  private static final long serialVersionUID = 5298177260117975299L;

  public static final String SDEF_GAZ_BOOTSTRAP_GAZETTEER_PROPERTY_NAME =
          "bootstrapGazetteer";

  /**
   * The existing DefaultGazetteer instance whose FSM we will share.
   */
  protected DefaultGazetteer bootstrapGazetteer;

  /**
   * Copy the references to the shareable state (i.e. the FSM) from the
   * existing gazetteer. Note that this method <i>deliberately</i> does
   * not call <code>super.init()</code> as to do so would cause the
   * lists to be reloaded.
   */
  @Override
  public Resource init() throws ResourceInstantiationException {
    if(bootstrapGazetteer == null) {
      throw new ResourceInstantiationException(
              "No gazetteer provided to bootstrap this gazetteer creation!");
    }
    this.annotationSetName = bootstrapGazetteer.annotationSetName;
    this.caseSensitive = bootstrapGazetteer.caseSensitive;
    this.definition = bootstrapGazetteer.definition;
    this.encoding = bootstrapGazetteer.encoding;
    this.fsmStates = bootstrapGazetteer.fsmStates;
    this.gazetteerFeatureSeparator =
            bootstrapGazetteer.gazetteerFeatureSeparator;
    this.initialState = bootstrapGazetteer.initialState;
    this.listsByNode = bootstrapGazetteer.listsByNode;
    this.listsURL = bootstrapGazetteer.listsURL;
    this.longestMatchOnly = bootstrapGazetteer.longestMatchOnly;
    this.mappingDefinition = bootstrapGazetteer.mappingDefinition;
    this.wholeWordsOnly = bootstrapGazetteer.wholeWordsOnly;

    this.getFeatures().putAll(bootstrapGazetteer.getFeatures());
    return this;
  }

  public DefaultGazetteer getBootstrapGazetteer() {
    return bootstrapGazetteer;
  }

  @CreoleParameter(comment = "The DefaultGazetteer that is to be used to bootstrap this shared instance")
  public void setBootstrapGazetteer(DefaultGazetteer bootstrapGazetteer) {
    this.bootstrapGazetteer = bootstrapGazetteer;
  }
}
