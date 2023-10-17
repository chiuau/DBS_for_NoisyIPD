package ipdlx.examples;

import ipdlx.*;
import ipdlx.strategy.*;


/**
 * This example shows how to create a game, how to play it,
 * and how to get information about the result
 * @author Tomek Kaczanowski
 */
public class GameResultExample implements GameListener {
    public final static String HR = "\n*********************\n";
    GameMatrix gameMatrix;
    StandardGame game;
    int nbOfRounds;
    Player playerA;
    Player playerB;
    
    /**
     * @param args ignored.
     */
    public static void main(String[] args) {
	GameResultExample testGame1 = new GameResultExample();
        testGame1.doTheTest();
    }

    public void gameResultPosted(GameResult result) {
	System.out.println(result);
    }

    public void gameRoundResultPosted(GameRoundResult result) {
	System.out.println(result);
    }

    public void gameStarted(Game game) {
	
    }
    
    public void doTheTest() {
        // first create a game matrix
        gameMatrix = new GameMatrix();
	
        System.out.println(gameMatrix);
	
        System.out.println(HR);

        // set number of iterations in game
        nbOfRounds = 20;

        // create game
        game = new StandardGame(nbOfRounds, gameMatrix);
	game.addGameListener(this);
        // create players
        playerA = new Player(new RAND());
        playerB = new Player(new TFT());

        // players enter into game
        game.setPlayerA(playerA);
        game.setPlayerB(playerB);

        // let's begin
        game.play();
        
        /* if you want to know more you can use Player like this:
	   System.out.println("PlayerA played " + playerA.getStrategyName() +
	   " and won " + playerA.getResult());
	   System.out.println("PlayerB played " + playerB.getStrategyName() +
	   " and won " + playerB.getResult());
        */
                
    }
}
