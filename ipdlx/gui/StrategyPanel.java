package ipdlx.gui;

import ipdlx.*;
import ipdlx.tools.PDFileHandler;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import java.util.Vector;
import java.util.Enumeration;
import java.util.Hashtable;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.io.*;
import java.net.*;
import java.util.jar.*;
import javax.swing.filechooser.FileFilter;


public class StrategyPanel extends JPanel implements ActionListener {
    
    private Tournament tournament;
    private JTextField[] nrPlayersTF;
    private Hashtable players;
    private JTextField jarFileTF, playerNameTF;
    private JTextField nrPlayersCreateTF;
    private File chosenFile, chosenDirectory;
    private Strategy  chosenStrategy;
    
    public final static String[] DEFAULT_STRATEGIES = 
	new String[] {"ipdlx.strategy.RAND",
		      "ipdlx.strategy.MRAND",
		      "ipdlx.strategy.NEG",
		      "ipdlx.strategy.ALLC", 
		      "ipdlx.strategy.ALLD", 
		      "ipdlx.strategy.TFT",
		      "ipdlx.strategy.STFT",
		      "ipdlx.strategy.TFTT",
		      "ipdlx.strategy.GRIM",
		      "ipdlx.strategy.Pavlov"};
    
    public final static String[] MULTICHOICE_DEFAULT_STRATEGIES =
	new String[] {"ipdlx.strategy.MRAND"};
    
    public StrategyPanel(Tournament tournament) {
	this.tournament = tournament;
	this.nrPlayersTF = new JTextField[DEFAULT_STRATEGIES.length];
	BorderedPanel p = new BorderedPanel("Strategies");
	for (int i = 0; i < DEFAULT_STRATEGIES.length; i++) {
	    JPanel stratPanel = 
		createStrategyChoicePanel(i, DEFAULT_STRATEGIES[i]);
	    if (stratPanel != null) {
		p.add(stratPanel);
	    }
	}
	add(p);
	
	players = new Hashtable();
	add(createLoadPanel());
    }
    
    

    JPanel createLoadPanel() {
	BorderedPanel p = new BorderedPanel("Create Player");
	
	JPanel panel = new JPanel(new FlowLayout());
	panel.add(new JLabel("Strategy File:"));
	panel.add(jarFileTF = new JTextField(20));
	
	JButton browseB = new JButton("Browse");
	browseB.addActionListener(this);
	panel.add(browseB);
	p.add(panel);
	panel = new JPanel();
	panel.add(new JLabel("Player Name:"));
	playerNameTF = new JTextField("Player");
	playerNameTF.setColumns(10);
	panel.add(playerNameTF);
	this.nrPlayersCreateTF = new JFormattedTextField(new Integer(1));
	nrPlayersCreateTF.setColumns(4);
	
	panel.add(new JLabel("Nr Players:"));
	panel.add(nrPlayersCreateTF);
	JButton submitB = new JButton("Create");
	submitB.addActionListener(this);
	panel.add(submitB);
	p.add(panel);
	return p;
    }

    JPanel createStrategyChoicePanel(final int index, String strategyName) {
	JPanel p = new JPanel(new BorderLayout());
	// Verify if it exists
	final Class strategyClass = 
	    Tournament.getStrategyClass(strategyName);
	if (strategyClass == null) {
	    return null;
	}
	final JCheckBox cb = new JCheckBox(strategyClass.getName());
	nrPlayersTF[index] = new JFormattedTextField(new Integer(1));
	nrPlayersTF[index].setColumns(4);
	cb.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
		    String command = ae.getActionCommand();
		    if (cb.isSelected()) {
			int size = 
			    Integer.parseInt(nrPlayersTF[index].getText());
			Vector newPlayers = 
			    Tournament.createPlayers(null, strategyClass, size);
			players.put(command, newPlayers);
			tournament.addPlayers(newPlayers);
			TournamentPanel.monitor.fireChange();
		    } else {
			Vector deleteP = (Vector) players.get(command);
			Vector tPlayers = tournament.getPlayers();
			for (Enumeration e = deleteP.elements();
			     e.hasMoreElements();) {
			    tPlayers.remove(e.nextElement());
			}
			players.remove(command);
			TournamentPanel.monitor.fireChange();
		    }
		}
	    });
	p.add(BorderLayout.WEST, cb);
	JPanel pp = new JPanel();
	pp.add(new JLabel("Nr Players:"));
	
	pp.add(nrPlayersTF[index]);
	p.add(BorderLayout.EAST, pp);
	return p;
    }

    public void actionPerformed(ActionEvent ae) {
	String command = ae.getActionCommand();	  
	if (command.equals("Browse")) {
	    chosenFile = chooseFile();
	    if (chosenFile != null) {
		try {
		    if (chosenFile.getName().endsWith(".jar")) {
			PDFileHandler fileHandler =
			    new PDFileHandler(new URL[] { PDFileHandler.fileToJarURL(chosenFile, ""),
							  PDFileHandler.fileToJarURL(chosenFile, "classes/")});
			chosenStrategy = 
			    fileHandler.getStrategyFromJarFile(chosenFile);
		    } else if (chosenFile.getName().endsWith(".class")) {
			PDFileHandler fileHandler = 
			    new PDFileHandler(chosenDirectory.toURL());
			chosenStrategy = 
			    fileHandler.getStrategy(chosenFile.getName());
		    }
		    jarFileTF.setText(chosenStrategy.getName());
		} catch (MalformedURLException mue) {
		    mue.printStackTrace();
		}
	    } else {
		System.out.println("Error: Chosen file not legal");
	    }
	} else if (command.equals("Create")) { // bbq
	    String name = playerNameTF.getText().trim();
	    int nrPlayers = Integer.parseInt(nrPlayersCreateTF.getText());
	    Vector players = Tournament.createPlayers(name, chosenStrategy, nrPlayers);
	    tournament.addPlayers(players);
	    TournamentPanel.monitor.fireChange();
	}
    }
    
    private File chooseFile() {
	JFileChooser chooser = new JFileChooser(chosenDirectory);
	PDJARFileFilter filter = new PDJARFileFilter();
	chooser.setFileFilter(filter);
	int returnVal = chooser.showOpenDialog(this);
	if(returnVal == JFileChooser.APPROVE_OPTION) {
	    System.out.println("You chose to open this file: " +
			       chooser.getSelectedFile().getName());

	    chosenDirectory = chooser.getCurrentDirectory();
	    return chooser.getSelectedFile();
	}
	return null;
    }
}

class PDJARFileFilter extends FileFilter {
    
    public boolean accept(File f) {
	
	if (!f.canRead()) {
	    return false;
	}
	
	if (f.isFile()) {
	    String filename = f.getName();
	    if (filename.endsWith(".jar") || filename.endsWith(".class")) {
		return true;
	    }
	} else if (f.isDirectory()) {
	    return true;
	}
	
	return false;
    }
    
    
    public String getDescription() {
	return "IPDLX Strategy files (.class, .jar)";
    }
}
