/*
 * GameMatrix, $RCSfile: DatabaseInterface.java,v $
 *
 * $Revision: 1.4 $
 * $Date: 2003/06/29 13:34:19 $
 *
 * $Author: humble $
 * Original Author: Jan Humble
 */

package ipdlx;

/**
 *
 * @author Jan Humble
 */
public class GameMatrix {
    
    protected double[][] payoffMatrix;

    public static final double[][] defaultPayoffMatrix5x5 = 
	new double[][] {{ 4.0,  3.0,  2.0,  1.0,  0.0},
			{4.25, 3.25, 2.25, 1.25, 0.25},
			{ 4.5,  3.5,  2.5,  1.5,  0.5},
			{4.75, 3.75, 2.75, 1.75, 0.75},
			{ 5.0,  4.0,  3.0,  2.0,  1.0}};
    
    public static final double[][] defaultPayoffMatrix2x2 = 
	new double[][] {{ 3.0,  0.0},
			{ 5.0,  1.0}};
    protected int nrRows, nrColumns;

    public GameMatrix() throws WrongGameMatrixValuesException {
	this(defaultPayoffMatrix2x2);
    }

    public boolean checkValues(double[][] payoffMatrix) {
	return (payoffMatrix != null && payoffMatrix[0] != null 
		&& payoffMatrix.length == payoffMatrix[0].length);
    }
    
    public synchronized void setPayoffMatrix(double[][] payoffMatrix) throws WrongGameMatrixValuesException {
	if (checkValues(payoffMatrix)) {
	    this.payoffMatrix = payoffMatrix;
	    this.nrRows = payoffMatrix.length;
	    this.nrColumns = payoffMatrix[0].length;
	} else {
	    throw(new WrongGameMatrixValuesException("Error: Wrong payoff matrix values."));
	}
    }

    public GameMatrix(double[][] payoffMatrix) throws WrongGameMatrixValuesException {
	setPayoffMatrix(payoffMatrix);
    }

    protected int[] getPayoffIndex(double moveA, double moveB) {
	int i = (int) Math.rint((nrRows-1) - (moveA * (double) (nrRows-1)));
	int j = (int) Math.rint((nrColumns-1) - (moveB * (double) (nrColumns-1)));
	return new int[] {i, j};
    }

    public double getPayoff(double moveA, double moveB, int player) {
	return getPayoff(moveA, moveB)[player];
    }

    public double[] getPayoff(double moveA, double moveB) {
	int[] index = getPayoffIndex(moveA, moveB);
	int i = index[0];
	int j = index[1];
	return new double[] {payoffMatrix[i][j], payoffMatrix[j][i]};
    }
    
    public double[][] getPayoffMatrix() {
	return payoffMatrix;
    }

    public String toString() {
	String out = "Payoff Matrix:\n";
	for (int j = 0; j < nrRows; j++) {
	    for (int i = 0; i < nrColumns; i++) {
		out += payoffMatrix[j][i] + "\t";
	    }
	    out += "\n";
	}
	return out;
    }
    
    public static void main(String[] args) {
	try {
	    GameMatrix gameMatrix = new GameMatrix();
	    System.out.println(gameMatrix.toString());
	    double[] payoff = 
		gameMatrix.getPayoff(Double.parseDouble(args[0]),
				     Double.parseDouble(args[1]));
	    System.out.println("Payoff A=" + payoff[0] + 
			       " Payoff B=" + payoff[1]);
	} catch (WrongGameMatrixValuesException wgmv) {}
	
    }
    
}