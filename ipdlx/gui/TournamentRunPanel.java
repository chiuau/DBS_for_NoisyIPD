package ipdlx.gui;

import ipdlx.*;

import java.util.Enumeration;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.BoxLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Observer;
import java.util.Observable;

/**
 *
 * @author Jan Humble
 */
public class TournamentRunPanel extends JPanel implements ActionListener, 
							  GameListener, 
							  TournamentListener,
							  Observer {
    
    private Tournament tournament;
    private JProgressBar progressBar;
    private JButton startButton, stopButton, clearButton;
    private int totalGames, gamesRun;

    public TournamentRunPanel(Tournament tournament) {
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	this.progressBar = new JProgressBar();
	this.progressBar.setStringPainted(true);
	this.progressBar.setMinimum(0);
	this.progressBar.setForeground(java.awt.Color.green);
	this.startButton = new JButton("Start");
	this.startButton.addActionListener(this);
	this.stopButton = new JButton("Stop");
	this.stopButton.addActionListener(this);
	this.clearButton = new JButton("Clear");
	this.clearButton.addActionListener(this);
	
	JPanel controls = new JPanel();
	controls.add(startButton);
	controls.add(stopButton);
	controls.add(clearButton);
	add(BorderLayout.NORTH, controls);
	add(BorderLayout.SOUTH, progressBar);
	
	setTournament(tournament);
    }
    
    public void setTournament(Tournament tournament) {
	this.tournament = tournament;
	TournamentPanel.monitor.addObserver(this);
	this.tournament.addTournamentListener(this);
	this.tournament.getGame().addGameListener(this);
	clear();
    }
    
    public void update(Observable o, Object arg) {
	setValues();
    }

    void setValues() {
	totalGames = tournament.getNrOfGames();
	progressBar.setMaximum(totalGames);
	progressBar.setString("Games to run: " + totalGames);
    }
    
    public void clear() {
	setValues();
	gamesRun = 0;
	progressBar.setValue(0);
	stopButton.setEnabled(false);
	startButton.setEnabled(true);
	clearButton.setEnabled(true);
	tournament.clearAllResults();
    }
    
    public void stop() {
	tournament.stop();
	stopButton.setEnabled(false);
	startButton.setEnabled(true);
	clearButton.setEnabled(true);
    }

    public void start() {
	setValues();
	stopButton.setEnabled(true);
	startButton.setEnabled(false);
	clearButton.setEnabled(false);
	
	System.out.println("Starting tournament on " + java.util.Calendar.getInstance().getTime() + " ...\n" + tournament.getGame().toString());
	tournament.play();
    }

    public void tournamentResultPosted(TournamentResult result) {
	stop();
    }
    
    public void tournamentStarted(Tournament t) {
    }
    

    public void gameStarted(Game game) {
    }
    
    public void gameResultPosted(GameResult result) {
	progressBar.setValue(++gamesRun);
	progressBar.setString(gamesRun + "/" + totalGames);
    }
    
    public void gameRoundResultPosted(GameRoundResult result) {
    }

    public void actionPerformed(ActionEvent ae) {
	String command = ae.getActionCommand();
	if (command.equals("Start")) {
	    start();
	} else if (command.equals("Stop")) {
	    stop();
	} else if (command.equals("Clear")) {
	    clear();
	}
    }

}