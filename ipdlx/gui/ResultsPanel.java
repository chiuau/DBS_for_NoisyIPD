package ipdlx.gui;

import ipdlx.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.util.Vector;
import java.util.Enumeration;
import javax.swing.JTable;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import java.text.SimpleDateFormat;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Jan Humble
 */
public class ResultsPanel extends JPanel implements TournamentListener,
						    ActionListener {
    
    private JLabel timeTF;
    private ResultsTable resultsTable;
    private TournamentResult lastResult;
    private File chosenFile, chosenDirectory;

    public ResultsPanel(Tournament t) {
	super(new BorderLayout());
	t.addTournamentListener(this);
	JPanel top = new JPanel();
	top.add(new JLabel("Total run time:"));
	top.add(timeTF = new JLabel("0.0 secs"));
	add(BorderLayout.NORTH, top);
	add(BorderLayout.CENTER, 
	    new JScrollPane(resultsTable = new ResultsTable(t)));
	JPanel bottom = new JPanel();
	JButton b = new JButton("Create HTML");
	b.addActionListener(this);
	bottom.add(b);
	add(BorderLayout.SOUTH, bottom);
    }
    
    public void actionPerformed(ActionEvent ae) {
	if (lastResult != null) {
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
	    String date = dateFormat.format(lastResult.getTournamentDate());
	    chosenFile = chooseSaveFile("pd_tournament_result_" + date + ".html");
	    if (chosenFile != null) {
		String html = 
		    ipdlx.tools.HTMLGenerator.tournamentResultToHTML(lastResult);
		try {
		    FileWriter fw = new FileWriter(chosenFile);
		    fw.write(html, 0, html.length());
		    fw.close();
		} catch (IOException ioe) {
		    ioe.printStackTrace();
		}
	    }
	}
    }
    
    private File chooseSaveFile(String defaultName) {
	if (chosenDirectory == null) {
	    chosenDirectory = new File(".");
	}
	JFileChooser chooser = new JFileChooser(chosenDirectory);
	HTMLFileFilter filter = new HTMLFileFilter();
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

    public void tournamentResultPosted(TournamentResult result) {
	this.lastResult = result;
	result.sortPlayers();
	timeTF.setText(result.getTotalTournamentTime() / 1000.0 + " secs");
	resultsTable.setPlayers(result.getPlayers());
    }

    public void tournamentStarted(Tournament t) {
    }
}

class HTMLFileFilter extends FileFilter {
    
    public boolean accept(File f) {
	if (!f.canRead()) {
	    return false;
	}
	
	if (f.isFile()) {
	    String filename = f.getName();
	    if (filename.endsWith(".html")) {
		return true;
	    }
	} else if (f.isDirectory()) {
	    return true;
	}
	return false;
    }
    
    
    public String getDescription() {
	return "HTML File";
    }
}

class ResultsTable extends JTable {
    
    public ResultsTable(Tournament t) {
	super(new ResultTableModel(t.getPlayers()));
    }
    
    public void setPlayers(Vector players) {
	((ResultTableModel) getModel()).setPlayers(players);
	revalidate();
	repaint();
    }

}

class ResultTableModel extends DefaultTableModel {
    
    private static String[] headers = {"Ranking",
				       "Player",
				       "Strategy",
				       "Games Played",
				       "Won",
				       "Tied",
				       "Lost",
				       "Total"};
    private Vector players;
    
    ResultTableModel(Vector players) {
	super(headers, players.size());
	this.players = players;
    }

    public void setPlayers(Vector players) {
	this.players = players;
    }

    public String getColumnName(int column) {
	return headers[column];
    }
    
    public int getRowCount() {
	if (players != null) {
	    return players.size();
	} 
	return 0;
    }

    public Object getValueAt(int row, int column) {
	if (players == null) {
	    return null;
	}
	Player p = (Player) players.elementAt(row);
	switch(column) {
	case 0:
	    return new Integer(row+1);
	case 1:
	    return p.getName();
	case 2:
	    return p.getStrategy().getFullName();
	case 3:
	    return new Integer(p.gamesPlayed);
	case 4:
	    return new Integer(p.gamesWon);
	case 5:
	    return new Integer(p.gamesTied);
	case 6:
	    return new Integer(p.gamesLost);
	case 7:
	    return new Double(p.getResult());
	default:
	    return "";
	}
    }
}