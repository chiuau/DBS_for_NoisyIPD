package ipdlx.strategy;

import ipdlx.Strategy;

import java.util.Hashtable;
import java.util.Enumeration;

/**
 * Historical strategy
 * @author Jan Humble
 */
public final class Historical extends Strategy {
    private final static String _abbrName = "Historical";
    private final static String _name = "Historical";
    private final static String _description = "Makes moves according to previous moves.";
    
    private Hashtable history;
    private int historySize;
    private StringBuffer prevOpponentMoves;
    private StringBuffer prevMoves;
    private String initialMoves;
    private String actions;
    
    public Historical(int historySize, 
		      String initialActions, 
		      String actions) throws WrongHistoricalValuesException {
	this(_abbrName, _name, _description, historySize, actions, initialActions);
    }

    public Historical(String abbrName,
		      String name, 
		      String description,
		      int historySize, 
		      String initialActions,
		      String actions) throws WrongHistoricalValuesException {
        super(abbrName, name, description);
	this.history = new Hashtable();
	this.historySize = historySize;
	this.initialMoves = initialActions;
	this.actions = actions;
	construct(initialMoves, actions);
	reset();
    }

    public void reset() {
	this.prevOpponentMoves = new StringBuffer(historySize);
	this.prevMoves = new StringBuffer(historySize);
    }

    protected void construct(String initialActions, String actions) throws WrongHistoricalValuesException {
	
	if (initialActions.length() != historySize) {
	    throw new WrongHistoricalValuesException("nr of initial actions " +historySize + " != " + initialActions.length());
	}
	
	int nrOfActions = (int) Math.pow(2, (historySize * 2));
	if (nrOfActions != actions.length()) {
	    throw new WrongHistoricalValuesException("nr of actions " + nrOfActions + " != " + actions.length());
	}

	for (int i = 0; i < nrOfActions; i++) {
	    String key = makeKeyForHistory(i, historySize);
	    String value = actions.substring(i, i+1);
	    addHistoryBranch(key, value);
	}
    }
    
    void addHistoryBranch(String branch, String move) {
	history.put(branch, move);
    }
    
    final static String moveToChar(double move) {
	return (move == COOPERATE) ? "C" : "D";
    }
    
    protected Object clone() throws CloneNotSupportedException {
	try {
	    Historical hist =  new Historical(getAbbrName(), 
					      getName(), 
					      getDescription(), 
					      historySize,
					      initialMoves,
					      actions);
	    return hist;
	} catch (WrongHistoricalValuesException whve) {
	    whve.printStackTrace();
	    return null;
	}
    }
    

    final static double charToMove(String s) {
	return s.equals("C") ? COOPERATE : DEFECT;
    }
    
    public double getMove() {
	
	double move = COOPERATE;  // default;
	
	// Insert the previous move from opponent
	// This needs to be done before, since opponentMove is set
	// after the getMove is called.
	if (prevOpponentMoves.length() == historySize) {
	    prevOpponentMoves.deleteCharAt(historySize-1);
	}
	prevOpponentMoves.insert(0, moveToChar(opponentMove));
	
	// See if to use the initial moves
	if (prevMoves.length() < historySize) {
	    move = charToMove(initialMoves.substring(prevMoves.length(), 
						     prevMoves.length() + 1));
	    //System.out.println(getName() + " length=" + prevMoves.length() + "initial move=" + initialMoves + " playing initial move " + move);
	} else { // use the move from history branch
	    String value = (String) history.get(prevMoves.toString() + prevOpponentMoves.toString());
	    if (value != null) {
		move = charToMove(value);
		//System.out.println(getName() + " prevMoves=" + prevMoves + " prevOppMoves=" + prevOpponentMoves + " playing  move " + move);
	    }
	}
	
	// Add this move to the history
	if (prevMoves.length() == historySize) {
	    prevMoves.deleteCharAt(historySize-1);
	}
	prevMoves.insert(0, moveToChar(move));
	
	return move;
    }
    
    static String makeKeyForHistory(int index, int historySize) {
	int keySize = historySize*2;
	String bin = "";
	int rest;
	while (index > 0) {
	    rest = index % 2;
	    bin = moveToChar(1 - rest).concat(bin);
	    index = (index - rest)/ 2;
	}
	String key = "";
	for (int i = 0; i < (keySize - bin.length()); i++) {
	    key += "C";
	}
	return key + bin;
    }

    public String toString() {
	StringBuffer sb = new StringBuffer(super.toString());
	sb.append("Initial moves: " + initialMoves);
	for (Enumeration e = history.keys(); e.hasMoreElements();) {
	    String branch = (String) e.nextElement();
	    sb.append("\nHistory = " + branch + " Move = " + history.get(branch));
	}
	return sb.toString();
    }
    
    public static void main(String[] args) {
	System.out.println(makeKeyForHistory(Integer.parseInt(args[0]), Integer.parseInt(args[1])));
    }


}
