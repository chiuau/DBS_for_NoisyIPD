package ipdlx;

import ipdlx.PDValues;
import java.util.Vector;

/**
 * Results of the game how many points each player won
 *
 * @author Jan Humble
 */
public class StandardGameResult extends GameResult {
  
    /**
     * constructor
     */
    public StandardGameResult(int nrRounds) {
        super(nrRounds);
    }

    /**
     * constructor
     */
    public StandardGameResult(Player playerA, Player playerB, int nrRounds) {
        super(new Player[] {playerA, playerB}, nrRounds);
    }
    
    public final Player getPlayerA() {
	return this.players[0];
    }

    public final Player getPlayerB() {
	return this.players[1];
    }
    
    public void addPayoff(double playerAPayoff, double playerBPayoff) {
        payoffs[0] += playerAPayoff;
	payoffs[1] += playerBPayoff;
    }

}
