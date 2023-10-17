package ipdlx;

import java.util.Vector;
import java.util.Enumeration;

import ipdlx.tools.RandomGenerator;

/**
 * Base class for all games
 * @author Jan Humble
 */
public abstract class Game implements PDValues {
    
    protected long moveTimeLimit = 3000;  // 3 secs

    /** how many rounds (iterations) */
    private int nrOfRounds;

    /** how many rounds (iterations) */
    protected float stDev = 0.0f;
    
    /** all players */
    protected Vector players;

    /** info about the result of this game */
    protected GameResult gameResult;

    /** payoff matrix of the game */
    protected GameMatrix gameMatrix;

    /** The percentage of mistakes the strategy makes.
     * Mistake is a situation like this for example:
     * strategy wants to play COOPERATE but for some
     * unknown reasons it plays DEFECT.
     */
    protected float probabilityOfMistake;

    protected Vector gameListeners;

    protected boolean broadcastResult = true;
    protected boolean broadcastRoundResults = false;
    /**
     * Constructs a new game with the same game parameters as the given one.
     * Also, all game listeners are transferred.
     *
     * @param game a template game to copy parameters from
     * 
     */
    public Game(Game game) {
	this(game.nrOfRounds, game.gameMatrix, game.probabilityOfMistake);
	this.stDev = game.getNrRoundsStDev();
	this.gameListeners = game.gameListeners;
    }
    
    /**
     * constructor
     *
     * @param nrOfRounds number of iterations
     * @param gameMatrix matrix of the prisoner's dilemma game
     * 
     */
    public Game(int nrOfRounds, GameMatrix gameMatrix) {
	this(nrOfRounds, gameMatrix, 0.0f);
    }

    /**
     * constructor
     *
     * @param nrOfRounds number of iterations
     * @param gameMatrix matrix of the prisoner's dilemma game
     * @param noise probability of mistake
     */
    public Game(int nrOfRounds, GameMatrix gameMatrix, float noise) {
        this.nrOfRounds = nrOfRounds;
        this.gameMatrix = gameMatrix;
	this.probabilityOfMistake = noise;
    }

    public String toString() {
	return ("Game (nrRounds= " + nrOfRounds + " stdev= " + stDev +
		" noise= " + probabilityOfMistake + ")\n" +
		gameMatrix.toString());
    }

    public final void setNumberOfRounds(int nrOfRounds) {
	this.nrOfRounds = nrOfRounds;
    }

    public final int getNumberOfRoundsStDev() {
	int nrRounds = (int) RandomGenerator.gaussianRandom(nrOfRounds, stDev);
	if (nrRounds < 0) {
	    nrRounds = 0;
	}
	return nrRounds;
    }

    public final int getNumberOfRounds(boolean useStDev) {
	return useStDev ? getNumberOfRoundsStDev() : this.nrOfRounds;
    }

    public final int getNumberOfRounds() {
	return getNumberOfRounds(false);
    }

    public final GameMatrix getGameMatrix() {
	return this.gameMatrix;
    }

    public void addPlayer(Player player) {
	if (players == null) {
	    players = new Vector();
	}
	players.add(player);
    }

    public void addPlayers(Vector players) {
	if (this.players == null) {
	    this.players = players;
	} else {
	    players.addAll(players);
	} 
    }

    public final void setNrRoundsStDev(float stDev) {
	this.stDev = stDev;
    }

    public final float getNrRoundsStDev() {
	return stDev;
    }

    public void setPlayers(Vector players) {
	this.players = players;
    }

    public void setGameMatrix(GameMatrix gameMatrix) {
	this.gameMatrix = gameMatrix;
    }

    public void setMoveTimeLimit(long timeLimitMillis) {
	this.moveTimeLimit = timeLimitMillis;
    }
    
    protected double getStrategyMove(Strategy strategy) {
	return getStrategyMove(strategy, true);
    }

    protected double getStrategyMove(Strategy strategy, boolean checkTimeLimit) {
	long timeBefore = System.currentTimeMillis();
	double move = strategy.getMove();
	long timeAfter = System.currentTimeMillis();
	long total = timeAfter - timeBefore; 
	//System.out.println("Strategy '" + strategy + "' taking " + total + " ms");   
	
	if (checkTimeLimit && total > moveTimeLimit) {
	    System.out.println("WARNING: Strategy '" + strategy + "' overruning allowed move time (" + total + " ms > " + moveTimeLimit + " ms !");   
	}
	return move;
    }
    
    public double filterMove(double move) {
	
	if (Math.random() < probabilityOfMistake) {
            //	 mistake - change the move;
            return (move == COOPERATE) ? DEFECT : COOPERATE;
        }
	
        // there was no mistake - return the original move
        return move;
    }
    
    /**
     * @return probabilityOfMistake
     */
    public final float getProbabilityOfMistake() {
        return probabilityOfMistake;
    }

    public final void setProbabilityOfMistake(float probabilityOfMistake) {
        this.probabilityOfMistake = probabilityOfMistake;
    }
    
    public final void removeGameListener(GameListener gameListener) {
	if (gameListeners != null) {
	    gameListeners.remove(gameListener);
	}
    }
    
    public final Vector getGameListeners() {
	return this.gameListeners;
    }

    public void setBroadcastResult(boolean broadcast) {
	this.broadcastResult = broadcast;
    }

    public void setBroadcastRoundResults(boolean broadcast) {
	this.broadcastRoundResults = broadcast;
    }

    public boolean getBroadcastResult() {
	return this.broadcastResult;
    }

    public boolean getBroadcastRoundResults() {
	return this.broadcastRoundResults;
    }

    public final void addGameListener(GameListener gameListener) {
	if (gameListeners == null) {
	    gameListeners = new Vector();
	}
	gameListeners.add(gameListener);
    }

    protected final void broadcastResults(GameResult result) {
	if (broadcastResult) {
	    for (Enumeration enum = gameListeners.elements(); enum.hasMoreElements();) {
		((GameListener) enum.nextElement()).gameResultPosted(result);
	    }
	}
    }

    protected final void broadcastRoundResults(GameRoundResult result) {
	if (broadcastResult) {
	    for (Enumeration enum = gameListeners.elements(); enum.hasMoreElements();) {
		((GameListener) enum.nextElement()).gameRoundResultPosted(result);
	    }
	}
    }


    
    /**
     * plays the game and return the results of it
     * @return result of the game
     */
    public GameResult play() {
	// clears strategy "memory" - 
	// important for strategies which takes actions
        // for example according to last opponent's move
	for (Enumeration e = players.elements(); e.hasMoreElements();) {
	    ((Player) e.nextElement()).clearMemory();
	}
	final int nrOfRounds = getNumberOfRounds(true);
	gameResult = new GameResult(players, nrOfRounds);
	double[] moves = new double[players.size()];
        for (int i = 0; i < nrOfRounds; i++) {
	    
	    // Calculate all moves
	    int playerIndex = 0;
	    for (Enumeration e = players.elements(); e.hasMoreElements(); playerIndex++) {
		moves[playerIndex] =
		    filterMove(((Player) e.nextElement()).getMove());
	    }
	    
	    // Calculate results
	    final int nrOfPlayers = players.size();
	    double[] payoffs = new double[players.size()];
	    for (int currentIndex = 0; currentIndex < nrOfPlayers; currentIndex++) {
		Player current = (Player) players.elementAt(currentIndex);
		double[] opponentsMoves = new double[players.size() - 1];
		int opponentMoveIndex = 0;
		for (int opponentIndex = currentIndex + 1; 
		     opponentIndex < nrOfPlayers; opponentIndex++) {
		    /* Check whether the same opponent.
		     * Note that a player might play against itself, 
		     * hence we should not match Player instances.
		     */
		    double currentMove = moves[currentIndex];
		    double opponentMove = moves[opponentIndex];
		    double[] payoff = gameMatrix.getPayoff(currentMove, 
							   opponentMove);
		    payoffs[currentIndex] += payoff[0];
		    payoffs[opponentIndex] += payoff[1];
		    opponentsMoves[opponentMoveIndex++] = opponentMove;
		}
		
		// Current player has now played against all others
		current.setLastResult(payoffs[currentIndex]);
		current.setOpponentsMoves(opponentsMoves);
		gameResult.addPayoff(currentIndex, payoffs[currentIndex]);
		current.increaseResult(payoffs[currentIndex]);
	    }
	}
	
	if (gameListeners != null) {
	    broadcastResults(gameResult);
	}
        return gameResult;
    }
}
