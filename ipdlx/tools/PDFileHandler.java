/*
 * PDFileHandler, $RCSfile: DatabaseInterface.java,v $
 *
 * $Revision: 1.4 $
 * $Date: 2003/06/29 13:34:19 $
 *
 * $Author: humble $
 * Original Author: Jan Humble
 */

package ipdlx.tools;

import java.util.jar.*;
import java.util.Enumeration;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.io.File;
import java.io.IOException;

import ipdlx.*;
import ipdlx.strategy.*;

public class PDFileHandler extends URLClassLoader {
    
    public final static String STRATEGY_ENTRY_KEY = "Main-Strategy";
    
    public PDFileHandler(URL url) {
	this(new URL[] {url}, Strategy.class.getClassLoader());
    }

    public PDFileHandler(URL url, ClassLoader parent) {
	this(new URL[] {url}, parent);
    }
    
    public PDFileHandler(URL[] urls) {
	this(urls, Strategy.class.getClassLoader());
    }

    public PDFileHandler(URL[] urls, ClassLoader parent) {
	super(urls, parent);
    }

    public Strategy getStrategy(String className) {
	try {
	    if (className.endsWith(".class")) {
		className = className.substring(0, className.indexOf(".class"));
	    }
	    System.out.print("Loading class '" + className + "' ... ");
	    Class c = findClass(className);
	    System.out.print("Verifying '" + c.getName() + "' ... ");
	    if (verify(c)) {
		System.out.println("ok!");
		return instantiateFromClass(c);
	    } else {
		System.out.println("fault!");
	    }
	} catch(ClassNotFoundException cnfe) {
	    cnfe.printStackTrace();
	    return null;
	} catch(NoClassDefFoundError ncde) {
	    ncde.printStackTrace();
	    return null;
	} catch(ClassFormatError cfe) {
	    cfe.printStackTrace();
	    return null;
	}
	return null;
    }

    
    public Strategy getStrategyFromJarFile(File jarFile) {
	try {
	    return getStrategyFromJarFile(new JarFile(jarFile));
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	    return null;
	}
    }

    public Strategy getStrategyFromJarFile(JarFile jarFile) {
	Class c = getStrategyClassFromJarFile(jarFile);
	if (c != null) {
	    Strategy s = instantiateFromClass(c);
	    return s;
	}
	return null;
    }

    public Class getStrategyClassFromJarFile(JarFile jarFile) {
	Class c = getStrategyClassFromManifest(jarFile);
	if (c == null) {
	    c = getStrategyClassSingleEntry(jarFile);
	}
	return c;
    }

    private Strategy instantiateFromClass(Class c) {
	try {
	    Strategy strategy = (Strategy) c.newInstance();
	    return strategy;
	} catch (InstantiationException ie) {
	    ie.printStackTrace();
	    return null;
	} catch (IllegalAccessException iae) {
	    iae.printStackTrace();
	    return null;
	}
    }
    
    public Class getStrategyClassSingleEntry(JarFile jarFile) {
	for (Enumeration e = jarFile.entries(); e.hasMoreElements();) {
	    String entryName = ((JarEntry) e.nextElement()).getName();
	    if (entryName.endsWith(".class")) {
		entryName = entryName.substring(0, entryName.indexOf(".class"));
		if (entryName.startsWith("classes/")) {
		    entryName = 
			entryName.substring(entryName.indexOf("classes/") + 8);
		    /*
		      try {
		      addURL(new URL("classes/"));
		      } catch (Exception exc) {
		      exc.printStackTrace();
		      }
		    */
		}
		System.out.print("Loading class single entry '" + entryName + "' ... ");
		try {
		    Class c = findClass(entryName);
		    System.out.print("Verifying '" + c.getName() + "' ... ");
		    if (verify(c)) {
			System.out.println("ok!");
			return c;
		    } else {
			System.out.println("fault!");
		    }
		} catch (ClassNotFoundException cnfe) {
		    // System.out.println("Not a strategy");
		    cnfe.printStackTrace();
		} catch (NoClassDefFoundError ncdfe) {
		    ncdfe.printStackTrace();
		}
	    }
	}
	return null;
    }
    
    public boolean verify(Class c) {
	
	return Strategy.class.isAssignableFrom(c) && c != Strategy.class;
    }

    public static URL fileToJarURL(File file, String suffix) throws MalformedURLException {
	String url = "jar:" + file.toURL().toString() + "!/" + suffix;
	//System.out.println("CREATING= " + url);
	return new URL(url);
    }

    public Class getStrategyClassFromManifest(JarFile jarFile) {
	try {
	    Manifest manifest = jarFile.getManifest();
	    if (manifest != null) {
		Attributes atts = manifest.getMainAttributes();
		String strategyClassName = atts.getValue(STRATEGY_ENTRY_KEY);
		if (strategyClassName != null) {
		    try {
			
			if (strategyClassName.endsWith(".class")) {
			    strategyClassName = strategyClassName.substring(0, strategyClassName.indexOf(".class"));
			}
			
			if (strategyClassName.startsWith("classes/")) {
			    strategyClassName = 
				strategyClassName.substring(strategyClassName.indexOf("classes/") + 8);
			}
			System.out.print("Loading class '" + strategyClassName + "' ... ");
			Class c = findClass(strategyClassName);
			System.out.print("Verifying '" + c.getName() + "' ... ");
			if (verify(c)) {
			    System.out.println("ok!");
			    return c;
			} else {
			    System.out.println("ok!");
			}
		    } catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		    } catch (NoClassDefFoundError ncdfe) {
			ncdfe.printStackTrace();
		    }
		}
	    }
	} catch(IOException ioe) {
	    ioe.printStackTrace();
	}
	return null;
	
    }
    
    /*
      public boolean verify(Object obj) {
      if (obj instanceof Strategy) {
      return true;
      }
      return false;
      }
    */

    
}