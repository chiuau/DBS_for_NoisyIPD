package ipdlx;

import java.util.EventListener;

/**
 *
 * @author Jan Humble
 */
public interface TournamentListener extends EventListener {
    
    public abstract void tournamentStarted(Tournament tournament);
    public abstract void tournamentResultPosted(TournamentResult tournamentResult);
}