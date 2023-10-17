package ipdlx.gui;

import ipdlx.*;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.BoxLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


/**
 *
 * @author Jan Humble
 */
public class TournamentPanel extends JPanel {
    
    protected Tournament tournament;

    static TournamentMonitor monitor;

    public TournamentPanel() {
	this(null);
    }
    
    public TournamentPanel(Tournament tournament) {
	super(new BorderLayout());
	this.tournament = tournament;
	monitor = new TournamentMonitor(tournament);
	JTabbedPane pane = new JTabbedPane();
	pane.add("Players", new PlayerPanel(tournament));
	pane.add("Results", new ResultsPanel(tournament));
	pane.add("Strategies", new StrategyPanel(tournament));
	pane.add("Info", new InfoPanel(tournament));
	
	add(BorderLayout.CENTER, pane);
	JPanel leftPanel = new JPanel();
	leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
	BorderedPanel p = new BorderedPanel("Game Settings");
	p.add(new GameSettingsPanel(tournament));
	
	leftPanel.add(p);
	p = new BorderedPanel("Controls");
	p.add(new TournamentRunPanel(tournament));
	leftPanel.add(p);
	JPanel lp = new JPanel();
	lp.add(leftPanel);
	add(BorderLayout.WEST, lp);
    }
    
    
}