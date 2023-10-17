package ipdlx.examples;

import ipdlx.*;
import ipdlx.strategy.*;

import java.util.Vector;

/**
 *
 * @author Jan Humble
 */
public class TournamentExample implements TournamentListener,
					  GameListener {
    
    Game game;
    Tournament tournament;
    Vector players;

    /**
     * @param args ignored.
     */
    public static void main(String[] args) {
	TournamentExample testGame1 = new TournamentExample();
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

    public void tournamentResultPosted(TournamentResult result) {
	System.out.println(result);
    }

    public void tournamentStarted(Tournament t) {
    }

    public void createPlayers() {
	// create players
	players = new Vector();
	players.addAll(Tournament.createPlayers(new RAND(), 20));
	players.addAll(Tournament.createPlayers(new TFT(), 20));
	players.addAll(Tournament.createPlayers(new GRIM(), 20));
	players.addAll(Tournament.createPlayers(new STFT(), 20));
	players.addAll(Tournament.createPlayers(new NEG(), 20));
	
    }
    
    public void createTournament() {
	// first create a game matrix
        GameMatrix gameMatrix = new GameMatrix();
	
        System.out.println(gameMatrix);
	
	// set number of iterations in game
        int nbOfRounds = 200;
	
        // create game
        game = new StandardGame(nbOfRounds, gameMatrix);
       	tournament = new Tournament(game);
	createPlayers();
	tournament.addPlayers(players);
	
    }
    
    public void doTheTest() {
	createTournament();
        game.addGameListener(this);	
	tournament.addTournamentListener(this);
	// let's begin
	tournament.play();
    }
}
