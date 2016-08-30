/*
 *
 *  Copyright (c) 1995-2011, The University of Sheffield. See the file
 *  COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 *
 *  This file is part of GATE (see http://gate.ac.uk/), and is free
 *  software, licenced under the GNU Library General Public License,
 *  Version 2, June 1991 (in the distribution as file licence.html,
 *  and also available at http://gate.ac.uk/gate/licence.html).
 *
 *  This class is based on code from the Jasper 2 JSP compiler from Jakarta
 *  Tomcat 5.5, produced by the Apache project.
 *
 *  Ian Roberts, 13/Dec/2004
 *
 *  $Id: Eclipse.java 17716 2014-03-20 14:17:24Z markagreenwood $
 */
package gate.util.compilers;

import gate.Gate;
import gate.util.Err;
import gate.util.GateClassLoader;
import gate.util.GateException;
import gate.util.Strings;
import gate.util.compilers.eclipse.jdt.core.compiler.IProblem;
import gate.util.compilers.eclipse.jdt.internal.compiler.ClassFile;
import gate.util.compilers.eclipse.jdt.internal.compiler.CompilationResult;
import gate.util.compilers.eclipse.jdt.internal.compiler.Compiler;
import gate.util.compilers.eclipse.jdt.internal.compiler.DefaultErrorHandlingPolicies;
import gate.util.compilers.eclipse.jdt.internal.compiler.ICompilerRequestor;
import gate.util.compilers.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import gate.util.compilers.eclipse.jdt.internal.compiler.IProblemFactory;
import gate.util.compilers.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import gate.util.compilers.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import gate.util.compilers.eclipse.jdt.internal.compiler.env.INameEnvironment;
import gate.util.compilers.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
import gate.util.compilers.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import gate.util.compilers.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * This class compiles a set of java sources using the JDT compiler from the
 * Eclipse project.  Unlike the Sun compiler, this compiler can load
 * dependencies directly from the GATE class loader, which (a) makes it faster,
 * (b) means the compiler will work when GATE is loaded from a classloader
 * other than the system classpath (for example within a Tomcat web
 * application), and (c) allows it to compile code that depends on classes
 * defined in CREOLE plugins, as well as in the GATE core.  This is the default
 * compiler for GATE version 3.0.
 *
 * @author Ian Roberts
 */
public class Eclipse extends gate.util.Javac {

  public static final boolean DEBUG = false;

  /**
   * Compiles a set of java sources using the Eclipse Java compiler and loads
   * the compiled classes in the gate class loader.
   * 
   * @param sources a map from fully qualified classname to java source
   * @throws GateException in case of a compilation error or warning.
   * In the case of warnings the compiled classes are loaded before the error is
   * raised.
   */
  @Override
  public void compile(Map<String,String> sources, final GateClassLoader classLoader) throws GateException {
    
    final Map<String, String> sourcesFiltered = new HashMap<String, String>(sources);
    
    // filter out classes that are already known
    Iterator<Map.Entry<String, String>> srcIter = sourcesFiltered.entrySet().iterator();
    while(srcIter.hasNext()) {
      String className = srcIter.next().getKey();
      if(classLoader.findExistingClass(className) != null) {
        // class already known
        log.warn("Cannot compile class \"" + className + 
            "\" as a version already exists in the target class loader.");
        srcIter.remove();
      }
    }
    if(sourcesFiltered.isEmpty()) return;
    
    // store any problems that occur douring compilation
    final Map<String,List<IProblem>> problems = new HashMap<String, List<IProblem>>();

    // A class representing a file to be compiled.  An instance of this class
    // is returned by the name environment when one of the classes given in the
    // sources map is requested.
    class CompilationUnit implements ICompilationUnit {
      String className;

      CompilationUnit(String className) {
        this.className = className;
      }

      @Override
      public char[] getFileName() {
        return className.toCharArray();
      }
      
      @Override
      public char[] getContents() {
        return sourcesFiltered.get(className).toCharArray();
      }
      
      /**
       * Returns the unqualified name of the class defined by this
       * compilation unit.
       */
      @Override
      public char[] getMainTypeName() {
        int dot = className.lastIndexOf('.');
        if (dot > 0) {
          return className.substring(dot + 1).toCharArray();
        }
        return className.toCharArray();
      }
      
      /**
       * Returns the package name for the class defined by this compilation
       * unit.  For example, if this unit defines java.lang.String,
       * ["java".toCharArray(), "lang".toCharArray()] would be returned.
       */
      @Override
      public char[][] getPackageName() {
        StringTokenizer izer = 
          new StringTokenizer(className, ".");
        char[][] result = new char[izer.countTokens()-1][];
        for (int i = 0; i < result.length; i++) {
          String tok = izer.nextToken();
          result[i] = tok.toCharArray();
        }
        return result;
      }
      
      @Override
      public boolean ignoreOptionalProblems() {
        return false;
      }
    }
    
    // Name enviroment - maps class names to eclipse objects.  If the class
    // name is one of those given in the sources map, the appropriate
    // CompilationUnit is created.  Otherwise, we try to load the requested
    // .class file from the GATE classloader and return a ClassFileReader for
    // that class.
    final INameEnvironment env = new INameEnvironment() {

      /**
       * Tries to find the class or source file defined by the given type
       * name.  We construct a string from the compound name (e.g. ["java",
       * "lang", "String"] becomes "java.lang.String") and search using that.
       */
      @Override
      public NameEnvironmentAnswer findType(char[][] compoundTypeName) {
        String result = "";
        String sep = "";
        for (int i = 0; i < compoundTypeName.length; i++) {
          result += sep;
          result += new String(compoundTypeName[i]);
          sep = ".";
        }
        return findType(result);
      }

      /**
       * Tries to find the class or source file defined by the given type
       * name.  We construct a string from the compound name (e.g. "String",
       * ["java", "lang"] becomes "java.lang.String") and search using that.
       */
      @Override
      public NameEnvironmentAnswer findType(char[] typeName, 
                                            char[][] packageName) {
        String result = "";
        String sep = "";
        for (int i = 0; i < packageName.length; i++) {
          result += sep;
          result += new String(packageName[i]);
          sep = ".";
        }
        result += sep;
        result += new String(typeName);
        return findType(result);
      }
      
      /**
       * Find the type referenced by the given name.
       */
      private NameEnvironmentAnswer findType(String className) {
        if(DEBUG) {
          System.err.println("NameEnvironment.findType(" + className +")");
        }
        try {
          if (sourcesFiltered.containsKey(className)) {
            if(DEBUG) {
              System.err.println("Found " + className + " as one of the "
                  + "sources, returning it as a compilation unit");
            }
            // if it's one of the sources we were given to compile,
            // return that as a CompilationUnit.
            ICompilationUnit compilationUnit = new CompilationUnit(className);
            return new NameEnvironmentAnswer(compilationUnit, null);
          }

          // otherwise, try and load the class from the GATE classloader.
          String resourceName = className.replace('.', '/') + ".class";
          
          // there is no point looking into the classloader we are compiling
          // into as it won't contain classes we aren't already compiling (and
          // even if it did they would be found eventually)
          InputStream is = Gate.getClassLoader().getResourceAsStream(resourceName);
          if (is != null) {
            if(DEBUG) {
              System.err.println("Found " + className + " in GATE classloader, "
                  + "returning it as a class file reader");
            }
            byte[] classBytes;
            byte[] buf = new byte[8192];
            ByteArrayOutputStream baos = 
              new ByteArrayOutputStream(buf.length);
            int count;
            while ((count = is.read(buf, 0, buf.length)) > 0) {
              baos.write(buf, 0, count);
            }
            baos.flush();
            classBytes = baos.toByteArray();
            char[] fileName = className.toCharArray();
            ClassFileReader classFileReader = 
              new ClassFileReader(classBytes, fileName, 
                                  true);
            return new NameEnvironmentAnswer(classFileReader, null);
          }
        }
        catch (IOException exc) {
          System.err.println("Compilation error");
          exc.printStackTrace();
        }
        catch (gate.util.compilers.eclipse.jdt.internal.compiler
                    .classfmt.ClassFormatException exc) {
          System.err.println("Compilation error");
          exc.printStackTrace();
        }
        // if no class found by that name, either as a source of in the
        // GATE classloader, return null.  This will cause a compiler
        // error.
        if(DEBUG) {
          System.err.println("Class " + className + " not found");
        }
        return null;
      }

      /**
       * Is the requested name a package?  We assume yes if it's not a class.
       */
      private boolean isPackage(String result) {
        if (sourcesFiltered.containsKey(result)) {
          return false;
        }
//        String resourceName = result.replace('.', '/') + ".class";
        Class<?> theClass = null;
        try{
          theClass = classLoader.loadClass(result);
        }catch(Throwable e){};
        return theClass == null;
      }

      /**
       * Checks whether the given name refers to a package rather than a
       * class.
       */
      @Override
      public boolean isPackage(char[][] parentPackageName, 
                               char[] packageName) {
        String result = "";
        String sep = "";
        if (parentPackageName != null) {
          for (int i = 0; i < parentPackageName.length; i++) {
            result += sep;
            String str = new String(parentPackageName[i]);
            result += str;
            sep = ".";
          }
        }
        String str = new String(packageName);
        if (Character.isUpperCase(str.charAt(0))) {
          if (!isPackage(result)) {
            return false;
          }
        }
        result += sep;
        result += str;
        return isPackage(result);
      }

      @Override
      public void cleanup() {
      }

    };

    // Error handling policy - try the best we can
    final IErrorHandlingPolicy policy = 
        DefaultErrorHandlingPolicies.proceedWithAllProblems();

    final Map<String, String> settings = new HashMap<String, String>();
    settings.put(CompilerOptions.OPTION_LineNumberAttribute,
                 CompilerOptions.GENERATE);
    settings.put(CompilerOptions.OPTION_SourceFileAttribute,
                 CompilerOptions.GENERATE);
    settings.put(CompilerOptions.OPTION_ReportDeprecation,
                 CompilerOptions.IGNORE);
    // ignore unused imports, missing serial version UIDs and unused local
    // variables - otherwise every JAPE action class would generate warnings...
    settings.put(CompilerOptions.OPTION_ReportUnusedImport,
                 CompilerOptions.IGNORE);
    settings.put(CompilerOptions.OPTION_ReportMissingSerialVersion,
                 CompilerOptions.IGNORE);
    settings.put(CompilerOptions.OPTION_ReportUnusedLocal,
                 CompilerOptions.IGNORE);
    settings.put(CompilerOptions.OPTION_ReportUncheckedTypeOperation,
                 CompilerOptions.IGNORE);
    settings.put(CompilerOptions.OPTION_ReportRawTypeReference,
                 CompilerOptions.IGNORE);
    settings.put(CompilerOptions.OPTION_ReportUnusedLabel,
        CompilerOptions.IGNORE);    

    // source and target - force 1.6 target as GATE only requires 1.6 or later.
    settings.put(CompilerOptions.OPTION_Source,
                 CompilerOptions.VERSION_1_8);
    settings.put(CompilerOptions.OPTION_TargetPlatform,
                 CompilerOptions.VERSION_1_6);

    final IProblemFactory problemFactory = 
      new DefaultProblemFactory(Locale.getDefault());

    // CompilerRequestor defines what to do with the result of a compilation.
    final ICompilerRequestor requestor = new ICompilerRequestor() {
      @Override
      public void acceptResult(CompilationResult result) {
        boolean errors = false;
        if (result.hasProblems()) {
          IProblem[] problems = result.getProblems();
          for (int i = 0; i < problems.length; i++) {
            // store all the errors and warnings from this result
            IProblem problem = problems[i];
            if (problem.isError()) {
              errors = true;
            }
            addProblem(problem);
          }
        }
        // if there were no errors (there may have been warnings), load the
        // compiled classes into the GATE classloader
        if (!errors) {
          ClassFile[] classFiles = result.getClassFiles();
          for (int i = 0; i < classFiles.length; i++) {
            ClassFile classFile = classFiles[i];
            char[][] compoundName = classFile.getCompoundName();
            String className = "";
            String sep = "";
            for (int j = 0; j < compoundName.length; j++) {
              className += sep;
              className += new String(compoundName[j]);
              sep = ".";
            }
            byte[] bytes = classFile.getBytes();
            classLoader.defineGateClass(className, bytes,
                                        0, bytes.length);
          }
        }
      }

      private void addProblem(IProblem problem) {
        String name = new String(problem.getOriginatingFileName());
        List<IProblem> problemsForName = problems.get(name);
        if(problemsForName == null) {
          problemsForName = new ArrayList<IProblem>();
          problems.put(name, problemsForName);
        }
        problemsForName.add(problem);
      }
    };

    // Define the list of things to compile
    ICompilationUnit[] compilationUnits = new ICompilationUnit[sourcesFiltered.size()];
    int i = 0;
    Iterator<String> sourcesIt = sourcesFiltered.keySet().iterator();
    while(sourcesIt.hasNext()) {
      compilationUnits[i++] =
        new CompilationUnit(sourcesIt.next());
    }

    // create the compiler
    Compiler compiler = new Compiler(env,
                                     policy,
                                     new CompilerOptions(settings),
                                     requestor,
                                     problemFactory);

    // and compile the classes
    compiler.compile(compilationUnits);

    if(!problems.isEmpty()) {
      boolean errors = false;
      Iterator<Map.Entry<String, List<IProblem>>> problemsIt = problems.entrySet().iterator();
      while(problemsIt.hasNext()) {
        Map.Entry<String, List<IProblem>> prob = problemsIt.next();
        String name = prob.getKey();
        List<IProblem> probsForName = prob.getValue();
        Iterator<IProblem> probsForNameIt = probsForName.iterator();
        while(probsForNameIt.hasNext()) {
          IProblem problem = probsForNameIt.next();
          if(problem.isError()) {
            Err.pr("Error: ");
            errors = true;
          }
          else if(problem.isWarning()) {
            Err.pr("Warning: ");
          }
          Err.prln(problem.getMessage()
                + " at line " 
                + problem.getSourceLineNumber() + " in " + name);
        }
        // print the source for this class, to help the user debug.
        Err.prln("\nThe offending input was:\n");
        Err.prln(Strings.addLineNumbers(sourcesFiltered.get(name)));
      }
      if(errors) {
        throw new GateException(
          "There were errors; see error log for details!");
      }
    }
  }
  
  private static final Logger log = Logger.getLogger(Eclipse.class);
}
