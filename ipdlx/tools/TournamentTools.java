/*
 * TournamentTools, $RCSfile: DatabaseInterface.java,v $
 *
 * $Revision: 1.4 $
 * $Date: 2003/06/29 13:34:19 $
 *
 * $Author: humble $
 * Original Author: Jan Humble
 */

package ipdlx.tools;

import ipdlx.gui.HumanPlayer;
import ipdlx.Strategy;
import ipdlx.strategy.Historical;
import ipdlx.strategy.WrongHistoricalValuesException;

import java.util.Vector;
import java.util.Properties;
import java.net.MalformedURLException;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.net.URL;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;

public abstract class TournamentTools {
    
    protected static File baseDir;

    public static Vector createHumanPlayers(String filename) {
	return createHumanPlayers(new File(filename));
    }
    
    
    public static void splitXML(String filename) {
	File file = new File(filename);
	NodeList nl = null;
	DocumentBuilder builder = null;
	Document d1, d2, d3;
	try {
	    builder = 
		DocumentBuilderFactory.newInstance().newDocumentBuilder();
	    Document doc = builder.parse(file);
	    //System.out.println(docToString(doc));
	    nl = doc.getElementsByTagName("PLAYER");
	    d1 = builder.parse(new File("template.xml"));
	    d2 = builder.parse(new File("template.xml"));
	    d3 = builder.parse(new File("template.xml"));
	} catch (Exception e) {
	    e.printStackTrace();
	    System.out.println("Could not parse xml file: " + file);
	    return;
	}
	
	for (int i = 0; i < nl.getLength(); i++) {
	    NodeList playerAtts = nl.item(i).getChildNodes();
	    int index = 0;
	    for (int j = 0; j < playerAtts.getLength(); j++) {
		Node n = playerAtts.item(j);
		if (n.getNodeType() == Node.ELEMENT_NODE) {
		    Node valueNode = playerAtts.item(j).getFirstChild();
		    String value = (valueNode != null) ? valueNode.getNodeValue().trim() : ""; 
		    if (n.getNodeName().equals("competition")) {
			if (value.equals("1")) {
			    //System.out.println("APPENDING");
			    d1.getFirstChild().appendChild(d1.importNode(nl.item(i), true));
			} else if (value.equals("2")) {
			    d2.getFirstChild().appendChild(d2.importNode(nl.item(i), true));
			} else if (value.equals("3")) {
			    d3.getFirstChild().appendChild(d3.importNode(nl.item(i), true));
			}
		    }
		}
	    }
	}
	docToFile(d1, "entries_comp_1.xml");
	docToFile(d2, "entries_comp_2.xml");
	docToFile(d3, "entries_comp_3.xml");
	//System.out.println(docToString(d1));
    }
    
    public static Vector createHumanPlayers(File file) {
	Vector players = new Vector();
	
	if (baseDir == null) {
	    baseDir = file.getParentFile();
	}
	
	NodeList nl = null;
	try {
	    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	    Document doc = builder.parse(file);
	    nl = doc.getElementsByTagName("PLAYER");
	} catch (Exception e) {
	    e.printStackTrace();
	    System.out.println("Could not parse xml file: " + file);
	    return null;
	}
	
	for (int i = 0; i < nl.getLength(); i++) {
	    NodeList playerAtts = nl.item(i).getChildNodes();
		Properties props = new Properties();
		int index = 0;
		for (int j = 0; j < playerAtts.getLength(); j++) {
		    Node n = playerAtts.item(j);
		    if (n.getNodeType() == Node.ELEMENT_NODE) {
			Node valueNode = playerAtts.item(j).getFirstChild();
			String value = (valueNode != null) ? valueNode.getNodeValue() : ""; 
			props.setProperty(n.getNodeName(), value.trim());
			// System.out.println(props.getProperty(n.getNodeName()));
		    }
		}
		HumanPlayer player = createHumanPlayer(props);
		if (player != null) {
		    players.add(player);
		}
		//System.out.println("\n");
	}
	return players;
    }
    
    public static HumanPlayer createHistoricalHumanPlayer(Properties props) {
	String abbrName = props.getProperty("acronym");
	String name = abbrName;
	String description = props.getProperty("description");
	int historySize = Integer.parseInt(props.getProperty("historylength"));
	String moves = props.getProperty("strategies");
	String initialMoves = moves.substring(0, historySize);
	String actions = moves.substring(historySize);
	try {
	    Historical historical = new Historical(abbrName, 
						   name, 
						   description,
						   historySize, 
						   initialMoves,
						   actions);
	    //System.out.println(historical);
	    return new HumanPlayer(props, historical);
	} catch (WrongHistoricalValuesException whve) {
	    whve.printStackTrace();
	    return null;
	}
    }

    public static HumanPlayer createHumanPlayer(Properties props) {
	if (props.getProperty("historylength") != null) {
	    return createHistoricalHumanPlayer(props);
	}
	
	String strategyFilename = props.getProperty("file");
	String path = baseDir.getAbsolutePath() + "/" + props.getProperty("playerid");
	Strategy strategy = null;
	
	if (strategyFilename.endsWith(".jar")) {
	    try {
		File jarFile = new File(path, strategyFilename);
		if (jarFile.exists()) {
		    fixFileName(strategyFilename, jarFile);
		}
		PDFileHandler fileHandler =
		    new PDFileHandler(new URL[] { PDFileHandler.fileToJarURL(jarFile, ""),
						  PDFileHandler.fileToJarURL(jarFile, "classes/")});
		//System.out.println("URL CREATED = " + new URL("jar:" + jarFile.toURL().toString() + "!/classes/"));
		
		strategy = fileHandler.getStrategyFromJarFile(jarFile);
	    } catch (MalformedURLException mfue) {
		mfue.printStackTrace();
	    }
	} else if (strategyFilename.endsWith(".class")) {
	    try {
		File file = new File(path, strategyFilename);
		if (file.exists()) {
		    fixFileName(strategyFilename, file);
		}
		PDFileHandler fileHandler = 
		    new PDFileHandler(file.getParentFile().toURL());
		
		strategy = fileHandler.getStrategy(strategyFilename);
	    } catch (MalformedURLException mfue) {
		mfue.printStackTrace();
	    }
	}
	if (strategy == null) {
	    System.out.println("WARNING: Could not create player, Strategy=" + strategyFilename + " failed to load.");
	    return null;
	}
	return new HumanPlayer(props, strategy);
    }

    public static boolean fixFileName(String correctFilename, File file) {
	if (file.getName().equals(correctFilename)) {
	    return false;
	}
	if (file.getName().equalsIgnoreCase(correctFilename)) {
	    file.renameTo(new File(file.getParentFile(), correctFilename));
	    System.out.println("Filename FIXED!");
	    return true;
	}
	return false;
    }

    
    public static void docToFile(Document doc, String filename) {
	try {
	    DOMSource src = new DOMSource(doc);
	    FileWriter fw = new FileWriter(filename);
	    StreamResult result = new StreamResult(fw); 
	    Transformer t = TransformerFactory.newInstance().newTransformer();
	    t.transform(src, result);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public static String docToString(Document doc) {
	try {
	    DOMSource src = new DOMSource(doc);
	    StringWriter sw = new StringWriter();
	    StreamResult result = new StreamResult(sw); 
	    Transformer t = TransformerFactory.newInstance().newTransformer();
	    t.transform(src, result);
	    return sw.toString();
	} catch (Exception e) {
	    e.printStackTrace();
	    return null;
	}
    }


    public static void main(String[] args) {
	//File file = generateTournamentXML();
	//createHumanPlayers("./entries.xml");
	splitXML(args[0]);
    }
    
}