package ipdlx.examples;

import ipdlx.*;
import ipdlx.strategy.*;
import ipdlx.gui.*;

import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

/**
 *
 * @author Jan Humble
 */
public class GUITournamentExample {
    
    /**
     * @param args ignored.
     */
    public static void main(String[] args) {
	JFrame frame = new JFrame("IPD Tournament");
	Tournament testT = new Tournament();
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.getContentPane().add(new TournamentPanel(testT));
	frame.pack();
	frame.setVisible(true);
    }
}
