package ipdlx.gui;

import ipdlx.*;

import java.util.Vector;
import java.util.Enumeration;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 *
 * @author Jan Humble
 */
public class GameSettingsPanel extends JPanel implements ActionListener {

    private final Tournament tournament;
    private JFormattedTextField roundsTF, noiseTF, stDevTF;
    private JComboBox typeChoice;
    private JButton applyB;

    private static final String[] GAME_TYPES = 
	new String[] {Tournament.gameTypeToString(Tournament.STANDARD_GAME),
		      Tournament.gameTypeToString(Tournament.MULTIPLAYER_GAME)};
    
    public GameSettingsPanel(Tournament tournament) {
	super(new BorderLayout());
	this.tournament = tournament;
	add(BorderLayout.NORTH, new GameMatrixPanel(tournament));
	
	JPanel controls = new JPanel(new BorderLayout());
	JPanel settingsP = new JPanel(new GridLayout(4,1));
	controls.setBorder(new EmptyBorder(5, 0, 5, 0));
	JPanel p = new JPanel(new GridLayout(1, 2));
	p.add(new JLabel("Rounds per game: "));
	roundsTF = 
	    new JFormattedTextField(new Integer(tournament.getGame().getNumberOfRounds()));
	
	roundsTF.addActionListener(this);
	p.add(roundsTF);
	settingsP.add(p);
	p = new JPanel(new GridLayout(1, 2));
	stDevTF = new JFormattedTextField(new Float(tournament.getGame().getNrRoundsStDev()));
	p.add(new JLabel("St Dev:"));
	p.add(stDevTF);
	stDevTF.addActionListener(this);
	settingsP.add(p);

	p = new JPanel(new GridLayout(1, 2));
	p.add(new JLabel("Noise: "));
	noiseTF = new JFormattedTextField(new Float(tournament.getGame().getProbabilityOfMistake()));
	noiseTF.addActionListener(this);
	p.add(noiseTF);
	settingsP.add(p);

	p = new JPanel(new GridLayout(1, 2));
	p.add(new JLabel("Game Type: "));
	p.add(typeChoice = new JComboBox(GAME_TYPES));
	typeChoice.addActionListener(this);
	settingsP.add(p);

	controls.add(BorderLayout.NORTH, settingsP);
	JPanel applyP = new JPanel();
	applyB = new JButton("Edit");
	applyB.addActionListener(this);
	applyP.add(applyB);
	controls.add(BorderLayout.SOUTH, applyP);
	
	JPanel resultsP = new JPanel(new GridLayout(2, 1));
	JCheckBox outputCB = new JCheckBox("Show game results", 
					   tournament.getGame().getBroadcastResult());
	outputCB.addActionListener(this);
	resultsP.add(outputCB);
	outputCB = new JCheckBox("Show round results", 
				 tournament.getGame().getBroadcastRoundResults());
	outputCB.addActionListener(this);
	resultsP.add(outputCB);
	
	p = new JPanel(new BorderLayout());
	p.add(BorderLayout.NORTH, controls);
	p.add(BorderLayout.SOUTH, resultsP);
	add(BorderLayout.SOUTH, p);
	setEnabled(false);
    }

    public void actionPerformed(ActionEvent ae) {
	int rounds = Integer.parseInt(roundsTF.getText());
	float noise = Float.parseFloat(noiseTF.getText());
	float stDev = Float.parseFloat(stDevTF.getText());
	Object obj = ae.getSource();
	String command = ae.getActionCommand();
	if (command.equals("Show game results")) {
	    tournament.getGame().setBroadcastResult(((JCheckBox) obj).isSelected());
	} else if (command.equals("Show round results")) {
	    tournament.getGame().setBroadcastRoundResults(((JCheckBox) obj).isSelected());
	} else if (command.equals("Apply")) {
	    tournament.getGame().setNumberOfRounds(rounds);
	    tournament.getGame().setProbabilityOfMistake(noise);
	    tournament.getGame().setNrRoundsStDev(stDev);
	    String gameType = (String) typeChoice.getSelectedItem();
	    setGameType(gameType);
	    setEnabled(false);
	    applyB.setText("Edit");
	} else if (command.equals("Edit")) {
	    setEnabled(true);
	    applyB.setText("Apply");
	}
    }
    
    public void setEnabled(boolean enable) {
	roundsTF.setEnabled(enable);
	noiseTF.setEnabled(enable);
	stDevTF.setEnabled(enable);
	typeChoice.setEnabled(enable);
	super.setEnabled(enable);
    }

    private void setGameType(String type) {
	Vector listeners = tournament.getGame().getGameListeners();
	Game newGame = tournament.getGame();
	if (type.equals(GAME_TYPES[0])) {
	    newGame = new StandardGame(newGame);
	} else if (type.equals(GAME_TYPES[1])) {
	    newGame = new MultiPlayerGame(newGame);
	}
	tournament.setGame(newGame);
	System.out.print("Setting new game parameters ...\n" + newGame.toString());
    }
    
}