package ipdlx.strategy;

import ipdlx.Strategy;

/**
 * Random strategy
 * @author Tomek Kaczanowski, Jan Humble
 */
public final class RAND extends Strategy {
    
    private final static String _abbrName = "RAND";
    private final static String _name = "Random";
    private final static String _description = "Makes random moves - DEFECTS or COOPERATES with 1/2 probability.";
    
    public RAND() {
        super(_abbrName, _name, _description);
    }
    
    /**
     * doesn't need to call super.getFinalMove - it's random already
     * @return move - COOPERATE or DEFECT with 0.5 probability
     */
    public double getMove() {
        return (Math.random() > 0.5) ? COOPERATE : DEFECT;
    }
}
