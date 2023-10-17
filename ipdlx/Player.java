package ipdlx;

/**
 * base class for all players
 * @author Tomasz Kaczanowski, Jan Humble
 */
public class Player {
    /** sum of points won by player */
    private double result;
    
    /** player's strategy */
    protected Strategy strategy;
    
    /** player's name*/
    protected String name;
    
    public int gamesPlayed, gamesWon, gamesLost, gamesTied;
    
    /**
     * @param strategy
     */
    public Player(Strategy strategy) {
        this(strategy, "");
    }

    /**
     * @param strategy
     * @param name
     */
    public Player(Strategy strategy, String name) {
        this.strategy = strategy;
	this.name = name;
    }

    /**
     * Returns the player name.
     */
    public String getName() {
	return name;
    }

    /**
     * increases result by a given number
     * also increases player's strategy result by the same number
     *
     * @param number number of points to be added to result
     */
    void increaseResult(double number) {
        result += number;
    }

    /**
     * returns result
     *
     * @return number of points gathered by this player during game(s)
     */
    public double getResult() {
        return result;
    }

    public void clearResult() {
	this.result = 0.0;
	gamesPlayed = 0;
	gamesWon = 0;
	gamesLost = 0;
	gamesTied = 0;
    }
    
    /**
     * clears the memory of the player - sets his strategy to the initial state
     */
    public void clearMemory() {
        strategy.reset();
    }

    /**
     * returns move
     * @return move of this player
     */
    public double getMove() {
        return strategy.getMove();
    }

    /** setter for opponentMove field
     * @param opponentMove last move of the opponent
     */
    public void setOpponentMove(double opponentMove) {
        strategy.opponentMove = opponentMove;
    }

    /** setter for opponentMove field
     * @param opponentsMoves last moves of the opponents
     */
    public void setOpponentsMoves(double[] opponentsMoves) {
        strategy.opponentsMoves = opponentsMoves;
    }

    public final void setNumberOfOpponents(int nrOfOpponents) {
	strategy.setNumberOfOpponents(nrOfOpponents);
    }
   
    public void setLastResult(double lastResult) {
        strategy.lastResult = lastResult;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
    public String toString() {
        return "Player: " + super.toString() + "\n\tstrategy - " +
        strategy.getAbbrName() + "\tpoints - " + getResult();
    }
}
