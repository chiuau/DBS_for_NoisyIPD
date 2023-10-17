package ipdlx.gui;

import ipdlx.Tournament;

import java.util.Observable;
import java.util.Observer;

public class TournamentMonitor extends Observable {

    private final Tournament tournament;

    TournamentMonitor(Tournament t) {
	this.tournament = t;
    }

    public void fireChange() {
	setChanged();
	notifyObservers(tournament);
    }
}