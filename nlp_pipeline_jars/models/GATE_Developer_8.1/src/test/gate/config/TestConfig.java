/*
 *  TestConfig.java
 *
 *  Copyright (c) 1995-2012, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  Hamish Cunningham, 9/Nov/00
 *
 *  $Id: TestConfig.java 17656 2014-03-14 08:55:23Z markagreenwood $
 */

package gate.config;

import gate.CreoleRegister;
import gate.Gate;
import gate.GateConstants;
import gate.corpora.TestDocument;
import gate.util.Files;
import gate.util.GateException;
import gate.util.OptionsMap;
import gate.util.Out;
import gate.util.Strings;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/** CREOLE test class
  */
public class TestConfig extends TestCase
{
  /** Debug flag */
  private static final boolean DEBUG = false;

  /** Construction */
  public TestConfig(String name) throws GateException { super(name); }

  /** Fixture set up */
  @Override
  public void setUp() throws Exception {
    CreoleRegister register = Gate.getCreoleRegister();
    register.registerDirectories(new URL(TestDocument.getTestServerName()+"tests"));
  } // setUp

  /** Put things back as they should be after running tests
    * (reinitialise the CREOLE register).
    */
  @Override
  public void tearDown() throws Exception {
    CreoleRegister register = Gate.getCreoleRegister();
    register.clear();
    Gate.init();
  } // tearDown

  /**
   * Helper method that processes a config file.
   */
  private void readConfig(URL configUrl) throws Exception {
    ConfigDataProcessor configProcessor = new ConfigDataProcessor();

    // open a stream to the builtin config data file (tests version)
    InputStream configStream = null;
    try {
      configStream = configUrl.openStream();
    } catch(IOException e) {
      throw new GateException(
        "Couldn't open config data test file: " + configUrl + " " + e
      );
    }
    if (DEBUG)
      Out.prln(
        "Parsing config file ... " + configStream + "from URL" + configUrl
      );
    configProcessor.parseConfigFile(configStream, configUrl);
  } // readConfig

  /** Test config loading */
  // currently disabled as the files have moved on gate.ac.uk due to a change in svn structure
  // which means the test can't read the files it needs and fails.
  /*public void testConfigReading() throws Exception {
    System.out.println("Reading GATE config from : " + new URL(TestDocument.getTestServerName()+"tests/gate.xml"));
    readConfig(new URL(TestDocument.getTestServerName()+"tests/gate.xml"));

    // check that we got the CREOLE dir entry; then remove it
    // so it doesn't get accessed in other tests
    CreoleRegister reg = Gate.getCreoleRegister();
    Set dirs = reg.getDirectories();
    assertTrue(
      "CREOLE register doesn't contain URL from test gate.xml",
      dirs != null && ! dirs.isEmpty() &&
      dirs.contains(new URL("http://gate.ac.uk/tests/"))
    );

    // we should have a GATECONFIG entry on Gate
    String fullSizeKeyName = "FULLSIZE";
    String fullSizeValueName = "yes";
    Map gateConfig = Gate.getUserConfig();
    assertNotNull("no gate config map", gateConfig);
    String fullSizeValue = (String) gateConfig.get(fullSizeKeyName);
    assertNotNull("no full size value", fullSizeValue);
    assertEquals(
      "incorrect config data from tests/gate.xml",
      fullSizeValueName, fullSizeValue
    );

    // clear the gate config for subsequent tests
    gateConfig.clear();


// the code below is removed after serial controller stopped
// being a PR. the XML config scripting of runnable systems isn't
// working anyhow. when/if it does work, appropriate tests should be
// re-added here
//    // get a test system
//    ResourceData controllerResData =
//      (ResourceData) reg.get("gate.creole.SerialController");
//    assertNotNull("no resdata for serial controller", controllerResData);
//    ProcessingResource controller =
//      (ProcessingResource) controllerResData.getInstantiations().pop();
//    assertNotNull("no controller instance", controller);
//
//    // try running the system
//    controller.execute();
  } // testConfigReading()
  */

  /** Test config updating */
  public void testConfigUpdating() throws Exception {
    // clear the gate config so we don't write values from the
    // system initialisation into the test file
    OptionsMap configMap = Gate.getUserConfig();
    configMap.clear();

    // if user config file exists, save it and remember the name
    //String configName = Gate.getUserConfigFileName();
    //File userConfigFile = new File(configName);
    File userConfigFile = Gate.getUserConfigFile();
    String configName = userConfigFile.getAbsolutePath();
    File savedConfigFile = null;
    if(userConfigFile.exists()) {
      if(DEBUG) {
        Out.prln(userConfigFile);
        Out.prln("can write: " + userConfigFile.canWrite());
      }
      String userConfigDirectory = userConfigFile.getParent();
      if(userConfigDirectory == null)
        userConfigDirectory = "";
      savedConfigFile = new File(
        userConfigDirectory + Strings.getFileSep() +
        "__saved_gate.xml__for_TestConfig__" + System.currentTimeMillis()
      );
      if(DEBUG) Out.prln(savedConfigFile);
      boolean renamed = userConfigFile.renameTo(savedConfigFile);
      assertTrue("rename failed", renamed);
    }
    assertTrue("user config file still there", ! userConfigFile.exists());

    // call Gate.writeConfig - check it creates an empty config file
    //this is no longer a valid test as the written user config will at least
    //contain the values for the known and autload plugin paths.
    Gate.writeUserConfig();
    @SuppressWarnings("unused")
    String writtenConfig = Files.getString(new File(configName));
    @SuppressWarnings("unused")
    String empty = Gate.getEmptyConfigFile();
//    assertEquals("written config doesn't match", writtenConfig, empty);

    // set some config attributes via Gate.getConfigData
    configMap.put("A", "1");
    configMap.put("B", "2");

    // call Gate.writeConfig, delete the config data from Gate's map,
    // read the config file and check that the new data is present
    Gate.writeUserConfig();
    configMap.clear();
    readConfig(userConfigFile.toURI().toURL());

    // reinstante saved user config file if not null
    userConfigFile.delete();
    if(savedConfigFile != null) {
      savedConfigFile.renameTo(userConfigFile);
    }

  } // testConfigUpdating

  /** Test session state file naming */
  public void testSessionStateFileNaming() throws Exception {
    String fileSep = Strings.getFileSep();
    if(DEBUG) {
      Out.prln("file sep is: " + fileSep);
    }

    if(Gate.runningOnUnix()) {
      assertTrue(fileSep.equals("/"));
      assertTrue(
        Gate.getUserSessionFile().toString().endsWith("."+GateConstants.GATE_DOT_SER)
      );
    } else {
      assertTrue(! fileSep.equals("/"));
      assertTrue(
        ! Gate.getUserSessionFile().toString().endsWith("."+GateConstants.GATE_DOT_SER)
      );
    }

  } // testSessionStateFileNaming

  /** Test config file naming */
  public void testConfigFileNaming() throws Exception {
    String fileSep = Strings.getFileSep();
    if(DEBUG) {
      Out.prln("file sep is: " + fileSep);
    }

    if(Gate.runningOnUnix()) {
      assertTrue(fileSep.equals("/"));
      assertTrue(
        Gate.getDefaultUserConfigFileName().endsWith("."+GateConstants.GATE_DOT_XML)
      );
    } else {
      assertTrue(! fileSep.equals("/"));
      assertTrue(
        ! Gate.getDefaultUserConfigFileName().endsWith("."+GateConstants.GATE_DOT_XML)
      );
    }

  } // testConfigFileNaming

  /** Test suite routine for the test runner */
  public static Test suite() {
    return new TestSuite(TestConfig.class);
  } // suite

} // class TestConfig
