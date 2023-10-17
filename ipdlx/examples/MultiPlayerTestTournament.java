package ipdlx.examples;

import ipdlx.*;
import ipdlx.strategy.*;

import java.util.Vector;
/**
 *
 * @author Jan Humble
 */
public class MultiPlayerTestTournament extends Tournament {
    
    public MultiPlayerTestTournament() {
	super(new MultiPlayerGame(200, new GameMatrix()));
	addPlayers(createPlayers());
    }

    public Vector createPlayers() {
	// create players
	Vector players = new Vector();
	players.addAll(Tournament.createPlayers(new RAND(), 10));
	players.addAll(Tournament.createPlayers(new TFT(), 10));
	players.addAll(Tournament.createPlayers(new GRIM(), 10));
	players.addAll(Tournament.createPlayers(new STFT(), 10));
	players.addAll(Tournament.createPlayers(new NEG(), 10));
	return players;
    }
    
}
