package ipdlx;

import ipdlx.PDValues;
import java.util.Vector;

import java.text.MessageFormat;

/**
 * Results of the game how many points each player won
 *
 * @author Jan Humble
 */
public class GameRoundResult {
    
    protected final int roundNr;
    private final Strategy strategyA;
    private final Strategy strategyB;
    private final double moveA, moveB, payoffA, payoffB;

    public GameRoundResult(int roundNr, 
			   Strategy strategyA, 
			   double moveA, 
			   double payoffA, 
			   Strategy strategyB, 
			   double moveB,
			   double payoffB) {
	this.roundNr = roundNr;
	this.strategyA = strategyA;
	this.strategyB = strategyB;
	this.moveA = moveA;
	this.moveB = moveB;
	this.payoffA = payoffA;
	this.payoffB = payoffB;
    }
    
    public String toString() {
	return "Round " + roundNr + " " + strategyA.getAbbrName() + " plays: " + moveA + " and gets: " + payoffA + "   " + strategyB.getAbbrName() + " plays: " + moveB + " and gets " + payoffB;
    }
    
}
