import ipdlx.Strategy;

/*
 * My name is Tsz-Chiu Au.  I am a graduate student in
 * Department of Computer Science, at the University of Maryland, College Park.
 * Email: chiu@cs.umd.edu
 * Tel: 301-405-2716
 */
/**
 * @author Tsz-Chiu Au
 */
public class TFTF extends Strategy {
    private final static String _abbrName = "TFTF";
    private final static String _name = "TFT with forgiveness";
    private final static String _description = "TFT with forgiveness" ;

    private static double forgiveChance = 0.75 ;
    double myMove,yourMove  ;

    public TFTF() {
        super(_abbrName, _name, _description);
        reset() ;
    }

    private double forgive() {
      if (Math.random() < forgiveChance) {
        forgiveChance *= 0.25 ;
        return COOPERATE ;
      } else {
        return DEFECT ;
      }
    }

    public double getMove() {
      yourMove = opponentMove ;

      if (yourMove==DEFECT && myMove==DEFECT) {
        myMove = forgive() ;
      } else {
        myMove = yourMove ;
      }

      return myMove ;
    }

    public void reset() {
      opponentMove = COOPERATE ;
      yourMove = COOPERATE ;
      myMove = COOPERATE ;
    }
}

