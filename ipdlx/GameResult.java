package ipdlx;

import ipdlx.PDValues;
import java.util.Vector;

import java.text.MessageFormat;

/**
 * Results of the game how many points each player won
 *
 * @author Jan Humble
 */
public class GameResult {
    /** number of points won by players */
    protected final double[] payoffs;
    protected final Player[] players;
    protected final int nrRounds;
    
    /** game log - what moves were chosen in every round*/
    private double[][] log;
    
    /**
     * constructor
     */
    public GameResult(int nrRounds) {
	this.payoffs = null;
	this.players = null;
	this.nrRounds = nrRounds;
    }

    /**
     * constructor
     */
    public GameResult(Vector players, int nrRounds) {
	this((Player[]) players.toArray(new Player[players.size()]), nrRounds);
    }

    /**
     * constructor
     */
    public GameResult(Player[] players, int nrRounds) {
	payoffs = new double[players.length];
	this.players = players;
	this.nrRounds = nrRounds;
	this.log = new double[players.length][nrRounds];
    }

    public final Player getPlayer(int index) {
	return this.players[index];
    }

    public void addPayoff(int index, double payoff) {
        payoffs[index] += payoff;
    }
    
    /**
     * returns number of points won by each player during this game
     * @return payoff number of points won by each player
     */
    public double[] getPayoffs() {
        return payoffs;
    }

    /**
     * returns number of points won by selected player during this game
     * @param playerNb player A or B
     * @return payoff number of points won selected player
     */
    public final double getPayoffPlayer(int playerNb) {
        return this.payoffs[playerNb];
    }

    

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
	StringBuffer buff = new StringBuffer("*** Game Results (nrRounds = " + nrRounds + "):");
	// \nPlayer\tStrategy\tPoints");
	for (int i = 0; i < players.length; i++) {
	    Object[] args = {players[i].getName(), 
			     players[i].getStrategy().getFullName(),
			     new Double(getPayoffPlayer(i))};
	    buff.append(MessageFormat.format("\n player={0} strategy={1} points={2}", args));
	    // buff.append(MessageFormat.format("\n{0}\t{1}\t{2}", args));
	}
	return buff.toString();
    }
    
    /**
     * write type of move made by players to game log
     * @param nbOfRound - which round is this
     * @param player - which player A or B
     * @param move - type of move of player - COOPERATION or DEFECTION
     */
    public void writeMove(int nbOfRound, int player, double move) {
        log[player][nbOfRound] = move;
    }

    
    /**
     * returns game log (for both players)
     * @return game log
     */
    public double[][] getLog() {
        return log;
    }

}
