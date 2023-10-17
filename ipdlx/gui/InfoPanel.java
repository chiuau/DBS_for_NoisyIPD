package ipdlx.gui;

import ipdlx.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import javax.swing.filechooser.FileFilter;


/**
 *
 * @author Jan Humble
 */
public class InfoPanel extends JPanel implements GameListener,
						 TournamentListener,
						 ActionListener {

    private JTextArea textArea;
    private Tournament tournament;
    private static final String INTRO = 
	"WELCOME TO IPDLX TOURNAMENT\n" + 
	"Written by Jan Humble <jan.humble@nottingham.ac.uk>"; 
    
    private File chosenDirectory;
    protected boolean autoClear = true;
    private JCheckBox autoClearCB;

    public InfoPanel(Tournament tournament) {
	super(new BorderLayout());
	this.tournament = tournament;
	this.textArea = new JTextArea(INTRO);
	//this.textArea.setTabSize(20);
	this.textArea.setEditable(false);
	add(new JScrollPane(textArea));
	JPanel p = new JPanel();
	
	autoClearCB = new JCheckBox("Auto clear results", autoClear);
	autoClearCB.addActionListener(this);
	p.add(autoClearCB);
	
	JButton clearB = new JButton("Clear");
	clearB.addActionListener(this);
	p.add(clearB);
	
	JButton infoB = new JButton("Print Game Info");
	infoB.addActionListener(this);
	p.add(infoB);

	JButton b = new JButton("Save to file ...");
	b.addActionListener(this);
	p.add(b);

	add(BorderLayout.SOUTH, p);
	tournament.getGame().addGameListener(this);
	tournament.addTournamentListener(this);
    }
    
    public void appendText(String text) {
	textArea.append(text);
    }
    
    public void setText(String text) {
	textArea.setText(text);
    }
    
    public void tournamentResultPosted(TournamentResult result) {
	appendText("\n" + result.toString());
    }
    
    public void gameResultPosted(GameResult result) {
	appendText("\n" + result.toString());
    }
    
     public void gameRoundResultPosted(GameRoundResult result) {
	 appendText("\n" + result.toString());
    }

    public void gameStarted(Game game) {
    }
    
    public void tournamentStarted(Tournament t) {
	if (autoClear) {
	    textArea.setText(INTRO + "\n");
	}
	appendText("\nTournament Started ...");
    }

    private File chooseSaveFile(String defaultName) {
	if (chosenDirectory == null) {
	    chosenDirectory = new File(".");
	}
	JFileChooser chooser = new JFileChooser(chosenDirectory);
	TextFileFilter filter = new TextFileFilter();
	chooser.setFileFilter(filter);
	chooser.setSelectedFile(new File(defaultName));
	int returnVal = chooser.showSaveDialog(this);
	if(returnVal == JFileChooser.APPROVE_OPTION) {
	    System.out.println("You chose to save to this file: " +
			       chooser.getSelectedFile().getName());
	    chosenDirectory = chooser.getCurrentDirectory();
	    return chooser.getSelectedFile();
	}
	return null;
    }

    public void actionPerformed(ActionEvent ae) {
	String command = ae.getActionCommand();
	if (command.equals("Clear")) {
	    textArea.setText(INTRO + "\n");
	} else if (command.equals("Print Game Info")) {
	    textArea.append(tournament.toString());
	}  else if (command.equals("Save to file ...")) {
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
	    String date = dateFormat.format(java.util.Calendar.getInstance().getTime());
	    
	    File file = chooseSaveFile("pd_output_" + date + ".txt");
	    try {
		String text = textArea.getText();
		FileWriter fw = new FileWriter(file);
		fw.write(text, 0, text.length());
		fw.close();
	    } catch (IOException ioe) {
		ioe.printStackTrace();
	    }
	} else if (command.equals("Auto clear results")) {
	    autoClear = autoClearCB.isSelected();
	}
    }
}


class TextFileFilter extends FileFilter {
    
    public boolean accept(File f) {
	if (!f.canRead()) {
	    return false;
	}
	
	if (f.isFile()) {
	    String filename = f.getName();
	    if (filename.endsWith(".txt")) {
		return true;
	    }
	} else if (f.isDirectory()) {
	    return true;
	}
	return false;
    }
    
    
    public String getDescription() {
	return "Text Output File";
    }
}
