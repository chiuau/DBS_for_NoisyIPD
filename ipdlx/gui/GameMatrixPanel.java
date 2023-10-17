package ipdlx.gui;

import ipdlx.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 *
 * @author Jan Humble
 */

public class GameMatrixPanel extends JPanel implements ActionListener {
    
    public static String[] matrixChoices = 
	new String[] {"2 x 2", "5 x 5"};

    private JTextField[][] matrixFields;
    private JComboBox choices;
    private JPanel matrixPanel;
    private Tournament tournament;

    public GameMatrixPanel(Tournament tournament) {
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	this.tournament = tournament;
	matrixPanel = new JPanel();
	//matrixPanel.setPreferredSize(new Dimension(280, 190));
	//matrixPanel.setLayout(new BoxLayout(matrixPanel, BoxLayout.Y_AXIS));
	matrixPanel.setBorder(new EtchedBorder());
	matrixPanel.add(createPanelFromMatrix(tournament.getGame().getGameMatrix().getPayoffMatrix()));
	JPanel p = new JPanel();
	p.add(new JLabel("1 = Cooperation   0 = Defection"));
	add(p);
			
	choices = new JComboBox(matrixChoices);
	choices.addActionListener(this);
	p = new JPanel();
	p.add(new JLabel("Payoff Matrix"));
	p.add(choices);
	add(p);
	add(matrixPanel);
    }
	
    String fractionToString(int numerator, int denominator) {
	if (numerator % denominator > 0) {
	    int gcd = gcd(numerator, denominator);
	    return (numerator/gcd) + "/" + (denominator/gcd);
	} else {
	    return String.valueOf(numerator/denominator);
	}
    }
    
    static int gcd(int a, int b) {
	int r = a % b;
	if (r > 0) {
	    return gcd(b, r);
	} else {
	    return b;
	}
    }

    public JPanel createPanelFromMatrix(double[][] matrix) {
	int nrRows = matrix.length;
	int nrColumns = matrix[0].length;
	matrixFields = new JTextField[nrRows][nrColumns];
	JPanel panel = new JPanel(new GridLayout(nrRows+1, nrColumns+1, 5, 5));
	for (int i = 0; i < nrRows+1; i++) {
	    for (int j = 0; j < nrColumns + 1; j++) {
		if (i == 0) {
		    if (j > 0) {
			panel.add(new JLabel(fractionToString(nrColumns-j, 
							      nrColumns-1)));
		    } else if (j == 0) {
			panel.add(new JLabel("Coop"));
		    }
		} else {
		    if (j == 0) {
			if (i > 0) {
			    panel.add(new JLabel(fractionToString(nrRows-i, 
								  nrRows-1)));
			}
		    } else {
			
			JTextField tf = 
			    new JFormattedTextField(new Double(matrix[i-1][j-1]));
			matrixFields[i-1][j-1] = tf;
			tf.setColumns(3);
			panel.add(tf);
		    }
		    
		}
	    }
	}
   	panel.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.RAISED),
					   new EmptyBorder(5, 5, 5, 5)));
	panel.setAlignmentY(panel.CENTER_ALIGNMENT);
	return panel;
    }

    public void setMatrixValues(double[][] matrix) {
	int nrRows = matrix.length;
	int nrColumns = matrix[0].length;
	for (int i = 0; i < nrRows; i++) {
	    for (int j = 0; j < nrColumns; j++) {
		matrixFields[i][j].setText(String.valueOf(matrix[i][j]));
	    }
	}
    }
    
    public double[][] getMatrixFromPanel() {
	int nrRows = matrixFields.length;
	int nrColumns = matrixFields[0].length;
	double[][] matrix = new double[nrRows][nrColumns];
	for (int i = 0; i < nrRows; i++) {
	    for (int j = 0; j < nrColumns; j++) {
		matrix[i][j] = Double.parseDouble(matrixFields[i][j].getText());
	    }
	}
	return matrix;
    }
	
    public void actionPerformed(ActionEvent ae) {
	JComboBox cb = (JComboBox) ae.getSource();
        String command = (String) cb.getSelectedItem();
	matrixPanel.removeAll();
	Game game = tournament.getGame();
	if (command.equals(matrixChoices[0])) {
	    matrixPanel.add(createPanelFromMatrix(GameMatrix.defaultPayoffMatrix2x2));
	    game.setGameMatrix(new GameMatrix(GameMatrix.defaultPayoffMatrix2x2));
	    revalidate();
	    repaint();
	} else if (command.equals(matrixChoices[1])) {
	    matrixPanel.add(createPanelFromMatrix(GameMatrix.defaultPayoffMatrix5x5));
	    game.setGameMatrix(new GameMatrix(GameMatrix.defaultPayoffMatrix5x5));
	    revalidate();
	    repaint();
	}
    }
    
}