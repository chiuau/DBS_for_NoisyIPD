package ipdlx;

import java.util.Vector;
import java.util.Enumeration;
import java.util.Iterator;


/**
 * A multiplayer game.
 * @author Jan Humble
 */
public class MultiPlayerGame extends Game {
    
    /**
     * Constructs a new game with the same game parameters as the given one.
     * Also, all game listeners are transferred.
     *
     * @param game a template game to copy parameters from
     * 
     */
    public MultiPlayerGame(Game game) {
	super(game);
    }

    /**
     * constructor
     *
     * @param nrOfRounds number of iterations
     * @param gameMatrix matrix of the prisoner's dilemma game
     */
    public MultiPlayerGame(int nrOfRounds, GameMatrix gameMatrix) {
	this(nrOfRounds, gameMatrix, 0.0f);
    }
    
    /**
     * constructor
     *
     * @param nrOfRounds number of iterations
     * @param gameMatrix matrix of the prisoner's dilemma game
     * @param noise probability of mistake
     */
    public MultiPlayerGame(int nrOfRounds, GameMatrix gameMatrix, float noise) {
	super(nrOfRounds, gameMatrix, noise);
    }

    /**
     * plays the game and return the results of it
     * @return result of the game
     */
    public GameResult play() {
        // clears strategy "memory" - 
	// important for strategies which takes actions
        // for example according to last opponent's move
	Strategy[] strategies = new Strategy[players.size()];
	int si = 0;
	for (Enumeration e = players.elements(); e.hasMoreElements(); si++) {
	    Player p = (Player) e.nextElement();
	    p.clearMemory();
	    strategies[si] = p.getStrategy(); 
	    // this is a special case of only 1 game played
	    p.gamesPlayed++;
	}
	
	final int nrOfRounds = getNumberOfRounds(true);
	gameResult = new GameResult(players, nrOfRounds);
	double[] moves = new double[players.size()];
	
        for (int i = 0; i < nrOfRounds; i++) {
	    // Calculate all moves
	    int playerIndex = 0;
	    for (Enumeration e = players.elements(); e.hasMoreElements(); playerIndex++) {
		Strategy strategy = ((Player) e.nextElement()).getStrategy();
		moves[playerIndex] =
		    filterMove(getStrategyMove(strategy));
		gameResult.writeMove(i, playerIndex, moves[playerIndex]);
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
	    
	    if (broadcastRoundResults) {
		broadcastRoundResults(new MultiplayerGameRoundResult(i, strategies, moves, payoffs));
	    }
	}
	
	// Find out maxResult
	double maxResult = 0; 
	for (Iterator i = players.iterator(); i.hasNext();) {
	    double result = ((Player) i.next()).getResult();
	    if (result > maxResult) {
		maxResult = result;
	    }
	}
	
	for (Iterator i = players.iterator(); i.hasNext();) {
	    Player p = (Player) i.next();
	    if (p.getResult() == maxResult) {
		p.gamesWon++;
	    } else {
		p.gamesLost++;
	    }
	}
	
	
	
	if (gameListeners != null) {
	    broadcastResults(gameResult);
	}
        return gameResult;
    }
}
