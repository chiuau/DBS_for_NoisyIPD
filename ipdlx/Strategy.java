package ipdlx;

/**
 * Base abstract class for all strategies.
 *
 * @author Tomasz Kaczanowski, Jan Humble
 */
public abstract class Strategy implements PDValues, Cloneable {
    /** strategy abbreviated name - eg. TFT
     * @todo */
    private String abbrName;
    
    /** strategy full name - eg. Tit-For-Tat */
    private String name;

    /** strategy description - how does this particular strategy works */
    private String description;

    /** info about last opponent's move - did he defect or cooperate.     *not every strategy uses this variable */
    protected double opponentMove;

    /** info about last opponent's move - did he defect or cooperate.     *not every strategy uses this variable */
    protected double[] opponentsMoves;

    /** info about last game's result.
     * not every strategy uses this variable */
    protected double lastResult;
    
    /** sum of points gathered by this strategy.
     * useful if many players use one strategy
     * and you want to know how many points did the
     * strategy  win in total */
    private double result;
    
    protected int nrOfOpponents = 1;
    
    /** Constructor for "dirty" strategies. Sets:
     * result to 0
     * also sets strategy names and description
     * @param abbrName abbreviated name of strategy, eg. "TFT"
     * @param name name of strategy, eg. "Tit-For-Tat"
     * @param description description of strategy, how does it works, when does it coopearate and defect
     * */
    public Strategy(String abbrName, String name, String description) {
        this.abbrName = abbrName;
        this.name = name;
        this.description = description;
	reset();
    }
    
    /**
     * returns move
     * @return move of this strategy
     */
    public abstract double getMove();
    
    /** adds i point to result
     * @param i number of points to add to result */
    final void increaseResult(double i) {
        result += i;
    }
    
    public void setLastResult(double lastResult) {
        this.lastResult = lastResult;
    }

    public final int getNumberOfOpponents() {
	return this.nrOfOpponents;
    }

    public final void setNumberOfOpponents(int nrOfOpponents) {
	this.nrOfOpponents = nrOfOpponents;
    }

    /** returns strategy result
     * @return strategy's result (the number of points gathered during game(s)
     * */
    protected final double getResult() {
        return result;
    }

    /** returns strategy description
     * @return strategy's description */
    public String getDescription() {
        return description;
    }

    /** returns strategy name
     * @return strategy's name */
    public String getName() {
        return name;
    }

    /** returns strategy abbreviated name
     * @return strategy's abbreviated name*/
    public String getAbbrName() {
        return abbrName;
    }

    /** returns full name of the strategy
     * @return full name of the strategy "abbrName (FullName)"
     */

    protected Object clone() throws CloneNotSupportedException {
	try {
	    Strategy newStrategy = (Strategy) getClass().newInstance();
	    return newStrategy;
	} catch (InstantiationException ie) {
	    ie.printStackTrace();
	    return null;
	} catch (IllegalAccessException iae) {
	    iae.printStackTrace();
	    return null;
	}
    }

    public String getFullName() {
        StringBuffer buf = new StringBuffer(abbrName);
        buf.append(" (");
        buf.append(name);
        buf.append(")");
	
        return buf.toString();
    }
    
    /** setter for opponentMove field.
     * if your strategy doesn't use last opponent move in its
     * next move calculations, setting of this field won't hurt it
     * @param opponentMove last move of the opponent
     * */
    public final void setOpponentMove(double opponentMove) {
        this.opponentMove = opponentMove;
	setOpponentsMoves(new double[] {opponentMove});
    }
    
    /** setter for opponentsMoves field.
     * if your strategy doesn't use last opponent move in its
     * next move calculations, setting of this field won't hurt it
     * @param opponentsMoves last move of all the opponents
     * */
    public final void setOpponentsMoves(double[] opponentsMoves) {
        this.opponentsMoves = opponentsMoves;
    }
    
    public final double[] getOpponentsMoves() {
        return this.opponentsMoves;
    }

    public final double getOpponentMove() {
        return this.opponentMove;
    }
    
    /**
     * @deprecated This used to implement noise, but is now implemeneted
     * in the Game class.
     */
    public final double getFinalMove(double move) {
	return move;
    }

    
    /**
     * sets the initial state of this strategy
     */
    public void reset() {
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(getFullName());
	return buf.toString();
    }
}
