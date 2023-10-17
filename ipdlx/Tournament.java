package ipdlx;

import java.util.Vector;
import java.util.Collection;
import java.util.Calendar;import java.util.Vector;
import java.util.Enumeration;

/**
 *  Main Tournament implementation. 
 *
 * @author Jan Humble
 */
public class Tournament {
    
    public static final int STANDARD_GAME = 0;
    public static final int MULTIPLAYER_GAME = 1;
    protected int gameType;
    
    private Vector players;
    private Vector tournamentListeners;
    private int nrOfGames;
    private TournamentRun tournamentRun;
    
    private Game game;

    public Tournament() {
	this(new StandardGame());
    }
    
    public Tournament(Game game) {
	this.players = new Vector();
	setGame(game);
    }
    
    public void setGame(Game game) {
	this.game = game;
	if (game instanceof StandardGame) {
	    setGameType(STANDARD_GAME);
	} else if (game instanceof MultiPlayerGame) {
	    setGameType(MULTIPLAYER_GAME);
	}
    }

    public void addPlayer(Player player) {
	this.players.add(player);
    }

    public void addPlayers(Vector players) {
	this.players.addAll(players);
    }
    
    public Vector getPlayers() {
	return players;
    }

    public int getNrPlayers() {
	return players.size();
    }
    
    public void clearAllResults() {
	for (Enumeration e = players.elements(); e.hasMoreElements();) {
	    ((Player) e.nextElement()).clearResult();
	}
    }

    public int getGameType() {
	return this.gameType;
    }

    public void setGameType(int type) {
	this.gameType = type;
    }
    
    public int getNrOfGames() {
	switch(gameType) {
	case STANDARD_GAME:
	    int nr = players.size();
	    for (int i = nr-1; i > 0; i--) {
		nr += i;
	    }
	    return nr;
	case MULTIPLAYER_GAME:
	    return 1;
	}
	return -1;
    }

    public Game getGame() {
	return game;
    }

    public void addTournamentListener(TournamentListener tl) {
	if (tournamentListeners == null) {
	    tournamentListeners = new Vector();
	}
	tournamentListeners.add(tl);
    }
    
    public static final Class getStrategyClass(String strategyName) {
	try {
	    return Class.forName(strategyName);
	} catch(ClassNotFoundException cnfe) {
	    cnfe.printStackTrace();
	    return null;
	}
    }
    
    public static final Strategy copyStrategy(Strategy strategy) {
	try {
	    return (Strategy) strategy.getClass().newInstance();
	} catch(InstantiationException ie) {
	    ie.printStackTrace();
	    return null;
	} catch(IllegalAccessException iae) {
	    iae.printStackTrace();
	    return null;
	}
    }
    
    public static final Strategy loadStrategy(Class strategyClass) {
	try {
	    return (Strategy) strategyClass.newInstance();
	} catch(InstantiationException ie) {
	    ie.printStackTrace();
	    return null;
	} catch(IllegalAccessException iae) {
	    iae.printStackTrace();
	    return null;
	}
    }

    public static final Strategy loadStrategy(String strategyName) {
	return loadStrategy(getStrategyClass(strategyName));
    }
    
    /**
     * Utility method for creating players
     */
    public static Vector createPlayers(Class strategyClass, int size) {
	return createPlayers(strategyClass.getName(), strategyClass, size);
    }

    /**
     * Utility method for creating players
     */
    public static Vector createPlayers(Strategy strategy, int size) {
	return createPlayers(strategy.getName(), strategy, size);
    }
    
    /**
     * Utility method for creating players
     */
    public static Vector createPlayers(String commonName, 
				       Strategy templateStrategy, int size) {
	return createPlayers(commonName, templateStrategy.getClass(), size);
    }

    /**
     * Utility method for creating players
     */
    public static Vector createPlayers(String commonName, 
				       Class strategyClass, int size) {
	Vector players = new Vector(size);
	for (int i = 0; i < size; i++) {
	    Strategy copy = loadStrategy(strategyClass);
	    if (copy != null) {
		if (commonName == null) {
		    commonName = copy.toString();
		}
		players.add(new Player(copy, commonName + "_" + (i+1)));
	    }
	}
	return players;
    }
    
    public void play() {
	switch(gameType) {
	case STANDARD_GAME:
	    tournamentRun = new StandardTournamentRun((StandardGame) game, 
						      players, 
						      tournamentListeners);
	    break;
	case MULTIPLAYER_GAME:
	    tournamentRun = 
		new MultiPlayerTournamentRun((MultiPlayerGame) game, 
					     players, 
					     tournamentListeners);
	    break;
	}
	broadcastStart();
	tournamentRun.start();
    }
    
    protected final void broadcastStart() {
	for (Enumeration enum = tournamentListeners.elements(); enum.hasMoreElements();) {
	    ((TournamentListener) enum.nextElement()).tournamentStarted(this);
	}
    }

    public void stop() {
	if (tournamentRun != null) {
	    tournamentRun.stopRun();
	    tournamentRun = null;
	}
    }

    public static String gameTypeToString(int gameType) {
	switch(gameType) {
	case STANDARD_GAME:
	    return "Standard";
	case MULTIPLAYER_GAME:
	    return "Multi-player";
	default:
	    return "Unknown";
	}
    }
    
    public String toString() {
	return "Tournament (nrPlayers= " + players.size() + " type= " 
	    + gameTypeToString(gameType) + ")\n" + game.toString();
    }
}

abstract class TournamentRun extends Thread {
    
    protected final Game game;
    protected final Vector players;
    protected final Vector tournamentListeners;
    protected boolean playing;
    
    TournamentRun(Game game, Vector players, Vector listeners) {
	this.game = game;
	this.players = players;
	this.tournamentListeners = listeners;
    }
    
    public boolean inPlay() {
	return playing;
    }
    
    public final void run() {
	// Inform every Strategy of the number of opponents
	int nrOfOpponents = players.size();
	for (Enumeration e = players.elements(); e.hasMoreElements();) {
	    ((Player) e.nextElement()).setNumberOfOpponents(nrOfOpponents);
	}
	playing = true;
	long timeBefore = System.currentTimeMillis();
	TournamentResult tournamentResult = play();
	long timeAfter = System.currentTimeMillis();
	tournamentResult.setTotalTournamentTime(timeAfter - timeBefore);
	tournamentResult.setTournamentDate(Calendar.getInstance().getTime());
	if (tournamentListeners != null) {
	    broadcastResults(tournamentResult);
	}
	playing = false;
    }

    public abstract TournamentResult play();
    
    public void stopRun() {
	playing = false;
    }

    protected final void broadcastResults(TournamentResult result) {
	for (Enumeration enum = tournamentListeners.elements(); enum.hasMoreElements();) {
	    ((TournamentListener) enum.nextElement()).tournamentResultPosted(result);
	}
    }

    
}




class StandardTournamentRun extends TournamentRun {
    
    StandardTournamentRun(StandardGame game, Vector players, Vector listeners) {
	super(game, players, listeners);
    }
    
    public TournamentResult play() {
	TournamentResult tournamentResult = new TournamentResult(players, Tournament.STANDARD_GAME);
	// Play each player against all others, including self
	for (int a = 0; a < players.size() && playing; a++) {
	    Player playerA = (Player) players.elementAt(a);
	    ((StandardGame) game).setPlayerA(playerA);
	    for (int b = a; b < players.size() && playing; b++) {
		Player playerB = (Player) players.elementAt(b);
		((StandardGame) game).setPlayerB(playerB);
		((StandardGame) game).play();
	    }
	}
	return tournamentResult;
    }
}

class MultiPlayerTournamentRun extends TournamentRun {
    
    MultiPlayerTournamentRun(MultiPlayerGame game, Vector players, Vector listeners) {
	super(game, players, listeners);
    }
    
    public TournamentResult play() {
	TournamentResult tournamentResult = 
	    new TournamentResult(players, 
				 Tournament.MULTIPLAYER_GAME);
	
	// Play where at every round all players compete against each other.
	((MultiPlayerGame) game).setPlayers(players);
	playing = true;
	((MultiPlayerGame) game).play();
  	playing = false;
	return tournamentResult;
    }
}
