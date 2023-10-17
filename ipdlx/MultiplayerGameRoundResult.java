package ipdlx;

import ipdlx.PDValues;
import java.util.Vector;

import java.text.MessageFormat;

/**
 * Results of the game how many points each player won
 *
 * @author Jan Humble
 */
public class MultiplayerGameRoundResult extends GameRoundResult {
    
    private final Strategy[] strategies;
    private final double[] moves, payoffs;
    
    public MultiplayerGameRoundResult(int roundNr, 
				      Strategy[] strategies, 
				      double[] moves, 
				      double [] payoffs) {
	super(roundNr, null, -1, -1, null, -1, -1);
	this.strategies = strategies;
	this.moves = moves;
	this.payoffs = payoffs;
    }
    
    public String toString() {
	StringBuffer sb = new StringBuffer("Round " + roundNr);
	for (int i = 0; i < strategies.length; i++) {
	    sb.append("\n" + strategies[i].getAbbrName() + " plays " + moves[i] + " and gets " + payoffs[i]);
	}
	return sb.toString();
    }
    
}
