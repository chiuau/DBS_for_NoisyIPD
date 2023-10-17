package ipdlx.strategy;

import ipdlx.Strategy;

/**
 * Random strategy
 * @author Tomek Kaczanowski, Jan Humble
 */
public final class MRAND extends Strategy {
    
    private final static String _abbrName = "MRAND";
    private final static String _name = "Multi-choice Random";
    private final static String _description = "Makes random moves, in the range [0, 1]";
    
    public MRAND() {
        super(_abbrName, _name, _description);
    }
    
    /**
     * doesn't need to call super.getFinalMove - it's random already
     * @return move - COOPERATE or DEFECT with 0.5 probability
     */
    public double getMove() {
        return (Math.random());
    }
}
