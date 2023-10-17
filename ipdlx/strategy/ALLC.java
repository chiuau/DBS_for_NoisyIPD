package ipdlx.strategy;

import ipdlx.Strategy;


/**
 * Always defect strategy
 * @author Tomasz Kaczanowski, Jan Humble
 */
public class ALLC extends Strategy {
    private final static String _abbrName = "ALLC";
    private final static String _name = "Always Cooperate";
    private final static String _description = "Always plays COOPERATION.";

    public ALLC() {
        super(_abbrName, _name, _description);
    }

    protected ALLC(String abbrName, String name, String description) {
        super(abbrName, name, description);
    }

    /**
     * always returns COOPERATE
     *
     * @return COOPERATE
     */
    public double getMove() {
        return COOPERATE;
    }
}
