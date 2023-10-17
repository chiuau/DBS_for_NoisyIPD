package ipdlx;

import java.lang.reflect.Array;

/**
 * payoff matrix for a player.
 * This class has two purposes:
 * 1) helps create game matrices
 * 2) allows to easily print out the matrix for one player
 * @author Tomek Kaczanowski
 */
public class PlayerMatrix implements PDValues {
    /**
     * 2x2 matrix of payoffs for one player
     */
    protected double[][] matrix;
    
    /**
     * constructor
     * @param reward - reward for cooperation
     * @param sucker - sucker's payoff
     * @param temptation - temptation to defect
     * @param punishment - punishment (for mutual defection)
     */
    public PlayerMatrix(double reward, double sucker, double temptation, double punishment) {
        matrix = new double[][] {
	    { reward, sucker },
	    { temptation, punishment }
	};
    }

    public PlayerMatrix(double[][] matrix) {
	this.matrix = matrix;
    }
	    
    /**
     * @return matrix with player's payoffs
     */
    public double[][] getMatrix() {
        return matrix;
    }
    
    /** Prints out player's payoffs
     * @return String with nicely formatted matrix
     */
    public String toString() {
        StringBuffer out = new StringBuffer();
	for (int j = 0; j < matrix.length; j++) {
	    for (int i = 0; i < matrix[0].length; i++) {
		out.append(matrix[j][i] + "\t");
	    }
	    out.append("\n");
	}
	
	return out.toString();
    }

    /**
     * takes PlayerMatrix and changes places of temptation and sucker.
     * usefull for creating symmetrical game matrices
         * @return PlayerMatrix with changed places of temptation and sucker
         */
    public PlayerMatrix transpose() {
        double[][] matrix = getMatrix();
	/*
	  return new PlayerMatrix(matrix[COOPERATE][COOPERATE],
	  matrix[DEFECT][COOPERATE], 
	  matrix[COOPERATE][DEFECT],
	  matrix[DEFECT][DEFECT]);
	*/
	return new PlayerMatrix(matrix);
    }
}
