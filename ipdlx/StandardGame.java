package ipdlx;

import java.util.Vector;
import java.util.Enumeration;


/**
 * Standard 2 player game
 * @author Tomasz Kaczanowski, Jan Humble
 */
public class StandardGame extends Game {
        
    /** first player */
    protected Player playerA;
    
    /** second player */
    protected Player playerB;

    /**
     * Constructs a new game with the same game parameters as the given one.
     * Also, all game listeners are transferred.
     *
     * @param game a template game to copy parameters from
     * 
     */
    public StandardGame(Game game) {
	super(game);
    }

    /**
     * Constructs a standard game, playing 200 rounds 
     * with the default game matrix.
     **/
    public StandardGame() {
	this(200, new GameMatrix());
    }

    /**
     * constructor
     *
     * @param nrOfRounds number of iterations
     * @param gameMatrix matrix of the prisoner's dilemma game
     */
    public StandardGame(int nrOfRounds, GameMatrix gameMatrix) {
	this(nrOfRounds, gameMatrix, 0.0f);
    }

    /**
     * constructor
     *
     * @param nrOfRounds number of iterations
     * @param gameMatrix matrix of the prisoner's dilemma game
     * @param noise probability of mistake
     */
    public StandardGame(int nrOfRounds, GameMatrix gameMatrix, float noise) {
        super(nrOfRounds, gameMatrix, noise);
    }
    
    /**
     * sets the first player
     *
     * @param playerA first player of this game
     */
    public final void setPlayerA(Player playerA) {
        this.playerA = playerA;
	addPlayer(playerA);
    }
    
    /**
     * sets the second player
     *
     * @param playerB second player of this game
     */
    public void setPlayerB(Player playerB) {
        this.playerB = playerB;
	addPlayer(playerB);
    }
    
    /**
     * plays the game and return the results of it
     * @return result of the game
     */
    public GameResult play() {
        // clears strategy "memory" - important for strategies which takes actions
        // for example according to last opponent's move
        playerA.clearMemory();
        playerB.clearMemory();

	final int nrOfRounds = getNumberOfRounds(true);
        StandardGameResult gameResult = 
	    new StandardGameResult(playerA, playerB, nrOfRounds);

	this.gameResult = gameResult;
	
	Strategy strategyA = playerA.getStrategy();
	Strategy strategyB = playerB.getStrategy();
	if (strategyA == strategyB) {
	    //System.out.println("Creating strategy clone for " + strategyB.getName());
	    try {
		strategyB = (Strategy) strategyA.clone();
	    } catch (CloneNotSupportedException cnse) {
		cnse.printStackTrace();
		return gameResult;
	    }
	}
	
	for (int i = 0; i < nrOfRounds; i++) {
	    
	    double moveA = filterMove(getStrategyMove(strategyA));
            double moveB = filterMove(getStrategyMove(strategyB));
	    double lastResultA = gameMatrix.getPayoff(moveA, moveB, PLAYER_A);
            double lastResultB = gameMatrix.getPayoff(moveA, moveB, PLAYER_B);
	    gameResult.addPayoff(lastResultA, lastResultB);
	    gameResult.writeMove(i, PLAYER_A, moveA);
	    gameResult.writeMove(i, PLAYER_B, moveB);
            strategyA.setOpponentMove(moveB);
            strategyB.setOpponentMove(moveA);
            strategyA.setLastResult(lastResultA);
            strategyB.setLastResult(lastResultB);
	    if (broadcastRoundResults) {
		broadcastRoundResults(new GameRoundResult(i, strategyA, moveA, lastResultA, strategyB, moveB, lastResultB));
	    }
        }
		
        // the game is over, so we can increase players' results
	double resultA = gameResult.getPayoffPlayer(PLAYER_A);
	double resultB = gameResult.getPayoffPlayer(PLAYER_B);
	
	playerA.gamesPlayed++;
	playerB.gamesPlayed++;

        playerA.increaseResult(resultA);
        playerB.increaseResult(resultB);

	if (resultA > resultB) {
	    playerA.gamesWon++;
	    playerB.gamesLost++;
	} else if (resultA < resultB) {
	    playerA.gamesLost++;
	    playerB.gamesWon++;
	} else if (resultA == resultB) {
	    playerA.gamesTied++;
	    playerB.gamesTied++;
	}

	if (gameListeners != null) {
	    broadcastResults(gameResult);
	}
        return gameResult;
    }
}
