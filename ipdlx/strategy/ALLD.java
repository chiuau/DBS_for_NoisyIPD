package ipdlx.strategy;

import ipdlx.Strategy;


/**
 * Always defect strategy
 * @author Tomasz Kaczanowski, Jan Humble
 */
public class ALLD extends Strategy {
    private final static String _abbrName = "ALLD";
    private final static String _name = "Always Defect";
    private final static String _description = "Always plays DEFECTION.";

    public ALLD() {
        super(_abbrName, _name, _description);
    }

    protected ALLD(String abbrName, String name, String description) {
        super(abbrName, name, description);
    }

    /**
     * always returns DEFECT
     *
     * @return DEFECT
     */
    public double getMove() {
        return DEFECT;
    }
}
