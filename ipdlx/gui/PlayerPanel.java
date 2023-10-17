package ipdlx.gui;

import ipdlx.*;

import java.util.Observer;
import java.util.Observable;
import java.util.Vector;
import java.util.Enumeration;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.table.AbstractTableModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import ipdlx.tools.TournamentTools;

/**
 *
 * @author Jan Humble
 */
public class PlayerPanel extends JPanel implements ActionListener, Observer {
    
    private final PlayerTable playerTable;
    private File chosenFile, chosenDirectory = new File(".");
    private Tournament tournament;
    private JLabel nrPlayersLabel;
    
    public PlayerPanel(Tournament t) {
	super(new BorderLayout());
	this.tournament = t;
	TournamentPanel.monitor.addObserver(this);
	add(BorderLayout.CENTER, 
	    new JScrollPane(playerTable = new PlayerTable(t)));
	
	JPanel p = new JPanel();
	
	nrPlayersLabel = new JLabel(String.valueOf(tournament.getNrPlayers()) + " players loaded");
	nrPlayersLabel.setForeground(Color.blue);
	JPanel pP = new JPanel();
	pP.add(nrPlayersLabel);
	p.add(pP);
	
	JButton b = new JButton("Load from file ...");
	b.addActionListener(this);
	p.add(b);
	
	b = new JButton("Reload Players");
	b.addActionListener(this);
	p.add(b);
	b = new JButton("Select All");
	b.addActionListener(this);
	p.add(b);
	b = new JButton("Remove");
	b.addActionListener(this);
	p.add(b);
	add(BorderLayout.NORTH, p);
    }

    public void actionPerformed(ActionEvent ae) {
	String command = ae.getActionCommand();
	if (command.equals("Reload Players")) {
	    update();
	} else if (command.equals("Select All")) {
	    playerTable.selectAll();
	} else if (command.equals("Remove")) {
	    int[] rows = playerTable.getSelectedRows();
	    if (rows != null) {
		playerTable.removePlayersAt(rows);
	    }
	    update();
	} else if (command.equals("Load from file ...")) {
	    chosenFile = chooseFile();
	    if (chosenFile != null) {
		Vector players = TournamentTools.createHumanPlayers(chosenFile);
		if (players != null) {
		    tournament.addPlayers(players);
		    update();
		}
	    }
	}
    }

    public void update(Observable o, Object arg) {
	//System.out.println("Revalidate");
	playerTable.revalidate();
	nrPlayersLabel.setText(String.valueOf(tournament.getNrPlayers()) + " players loaded");
    }
    
    public void update() {
	TournamentPanel.monitor.fireChange();
	
    }

    private File chooseFile() {
	JFileChooser chooser = new JFileChooser(chosenDirectory);
	PDTournamentFileFilter filter = new PDTournamentFileFilter();
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

class PlayerTable extends JTable {
    public PlayerTable(Tournament t) {
	super(new PlayerTableModel(t));
    }

    public void removePlayersAt(int[] rows) {
	((PlayerTableModel) getModel()).removePlayersAt(rows);
	revalidate();
    }
}

class PDTournamentFileFilter extends FileFilter {
    
    public boolean accept(File f) {
	
	if (!f.canRead()) {
	    return false;
	}
	
	if (f.isFile()) {
	    String filename = f.getName();
	    if (filename.endsWith(".xml")) {
		return true;
	    }
	} else if (f.isDirectory()) {
	    return true;
	}
	
	return false;
    }
    
    
    public String getDescription() {
	return "IPDX Tournament files (.xml)";
    }
}

class PlayerTableModel extends AbstractTableModel {
    
    private static String[] headers = {"Competition", 
				       "Player",
				       "Strategy",
				       "Email"};
    private Tournament t;

    PlayerTableModel(Tournament t) {
	this.t = t;
    }

    public int getColumnCount() {
	return headers.length;
    }

    public int getRowCount() {
	if (t != null && t.getPlayers() != null) {
	    int nrPlayers = t.getPlayers().size();
	    return t.getPlayers().size();
	} 
	return 0;
    }
    
    public void removePlayersAt(int[] rows) {
	Vector players = t.getPlayers();
	for (int i = 0; i < rows.length; i++) {
	    if (rows[i] < players.size()) {
		players.removeElementAt(rows[i]);
	    }
	    if (i + 1 < rows.length) {
		rows[i+1]-= (i+1);
	    }
	}
	
    }

    
    public String getColumnName(int column) {
	return headers[column];
    }
   
    /*
     * JTable uses this method to determine the default renderer/
     * editor for each cell.  If we didn't implement this method,
     * then the last column would contain text ("true"/"false"),
     * rather than a check box.
     */
    public Class getColumnClass(int c) {
	return getValueAt(0, c).getClass();
    }

    public Object getValueAt(int row, int column) {
	Vector players = t.getPlayers();
	if (players == null) {
	    return null;
	}
	Player p = (Player) players.elementAt(row);
	String email = "";
	String competition = "";
	if (p instanceof HumanPlayer) {
	    java.util.Properties props = 
		((HumanPlayer) p).getPlayerProperties(); 
	    email = props.getProperty("email");
	    competition = props.getProperty("competition");
	}
	switch(column) {
	case 0:
	    return (competition != null) ? competition : String.valueOf(1);
	case 1:
	    return p.getName();
	case 2:
	    return p.getStrategy().getFullName();
	case 3:
	    return email;
	default:
	    return "";
	}
    }

    public void setValueAt(Object value, int row, int column) {
	switch(column) {
	case 0:
	    boolean prevVal = 
		((Boolean)getValueAt(row, column)).booleanValue();
	    super.setValueAt(new Boolean(!prevVal), row, column);
	    break;
	}
	
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
	//Note that the data/cell address is constant,
	//no matter where the cell appears onscreen.
        return(col < 1);
    }
}