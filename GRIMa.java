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
public class GRIMa extends Strategy {
    private final static String _abbrName = "GRIMa";
    private final static String _name = "GRIMa";
    private final static String _description = "GRIM with only one chance" ;

    int defectednum ;

    double myMove,yourMove  ;

    public GRIMa() {
        super(_abbrName, _name, _description);
        reset() ;
    }

    public double getMove() {
      if (defectednum>=1) { return DEFECT ; }

      if (opponentMove == DEFECT) {
        defectednum++ ;
        return DEFECT;
      } else {
        return COOPERATE;
      }
    }

    public void reset() {
      defectednum = 0 ;
      opponentMove = COOPERATE ;
    }
}

