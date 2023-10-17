package ipdlx;

import ipdlx.PDValues;

import java.util.Vector;
import java.util.Enumeration;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Results of the game how many points each player won
 *
 * @author Jan Humble
 */
public class TournamentResult implements PDValues {
   
    private Vector players;
    private long totalTournamentTime;
    private Date time;
    private int gameType;

    public TournamentResult(Vector players, int gameType) {
	this.players = players;
	this.gameType = gameType;
    }
    
    public void setTotalTournamentTime(long time) {
	this.totalTournamentTime = time;
    }

    public long getTotalTournamentTime() {
	return this.totalTournamentTime;
    }

    public int getTournamentGameType() {
	return this.gameType;
    }

    public void setTournamentDate(Date time) {
	this.time = time;
    }

    public Date getTournamentDate() {
	return this.time;
    }

    public Vector getPlayers() {
	return players;
    }
    
    public void sortPlayers() {
	Collections.sort(players, new ResultComparator());
    }

    public final static String makeTextColumn(String text, int size) {
	StringBuffer sb = new StringBuffer(text);
	if (text.length() > size) {
	    return text.substring(0, size);
	}
	for (int i = text.length(); i <= size; ++i) {
	    sb.append(" ");
	}
	return sb.toString();
    }

    public String toString() {
	sortPlayers();
	StringBuffer buff = new StringBuffer("*** Tournament Results ***");
	buff.append("\nDate:" + time);
	buff.append("\nTotal run time:" + ((double) totalTournamentTime / 1000.0) + " secs");
	Object[] headers = {makeTextColumn("Player", 20),
			    makeTextColumn("Strategy", 20),
			    makeTextColumn("Won", 10),
			    makeTextColumn("Tied", 10),
			    makeTextColumn("Lost", 10),
			    makeTextColumn("Total Points", 12)};
	buff.append(java.text.MessageFormat.format("\n{0}\t{1}\t{2}\t{3}\t{4}\t{5}", headers));
	int i = 0;
	for (Enumeration e = players.elements(); e.hasMoreElements();) {
	    Player p = (Player) e.nextElement();
	    Object[] args = {makeTextColumn(p.getName(), 20),
			     makeTextColumn(p.getStrategy().getFullName(), 20),
			     makeTextColumn(String.valueOf(p.gamesWon), 10),
			     makeTextColumn(String.valueOf(p.gamesTied), 10),
			     makeTextColumn(String.valueOf(p.gamesLost), 10),
			     makeTextColumn(String.valueOf(p.getResult()), 10)}; 
	    buff.append(java.text.MessageFormat.format("\n{0}\t{1}\t{2}\t{3}\t{4}\t{5}", args));
	}
	return buff.toString();
    }

}

class ResultComparator implements Comparator {
    
    public int compare(Object o1, Object o2) {
	double p1 = ((Player) o1).getResult();
        double p2 = ((Player) o2).getResult();
	if (p1 > p2) {
	    return -1;
	} else if (p1 < p2) {
	    return 1;
	} else {
	    return 0;
	}
	
    }
    
    public boolean equals(Object obj) {
	return (this == obj);
    }

}