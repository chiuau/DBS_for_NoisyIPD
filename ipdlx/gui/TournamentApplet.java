package ipdlx.gui;

import ipdlx.*;

import javax.swing.JApplet;

public class TournamentApplet extends JApplet {
    
    public TournamentApplet() {
	Tournament testT = new Tournament();
	getContentPane().add(new TournamentPanel(testT));
    }
    
}