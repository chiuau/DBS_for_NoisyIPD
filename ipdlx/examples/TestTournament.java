package ipdlx.examples;

import ipdlx.*;
import ipdlx.strategy.*;

import java.util.Vector;

/**
 *
 * @author Jan Humble
 */
public class TestTournament extends Tournament {
    
    public TestTournament() {
	super(new StandardGame(200, new GameMatrix()));
	addPlayers(createPlayers());
    }

    public Vector createPlayers() {
	// create players
	Vector players = new Vector();
	players.addAll(Tournament.createPlayers(new RAND(), 20));
	players.addAll(Tournament.createPlayers(new TFT(), 20));
	players.addAll(Tournament.createPlayers(new GRIM(), 20));
	players.addAll(Tournament.createPlayers(new STFT(), 20));
	players.addAll(Tournament.createPlayers(new NEG(), 20));
	return players;
    }
    
}
