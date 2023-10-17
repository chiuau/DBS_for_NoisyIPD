package ipdlx.strategy;

import ipdlx.Strategy;


/**
 * @author Tomek Kaczanowski
 */
public class NEG extends Strategy {
    private final static String _abbrName = "NEG";
    private final static String _name = "Negation";
    private final static String _description = "If opponent plays COOPERATION, in next move NEG will play DEFECTION; if opponent plays DEFECTION NEG will play COOPERATION. The first move is random.";
    
    /**
     * This constructor creates NEG strategy object with probability of mistake = 0
     */
    public NEG() {
        super(_abbrName, _name, _description);
    }

    public double getMove() {
        return (opponentMove == COOPERATE) ? DEFECT : COOPERATE;
	
        /*
         *         which is equivalent to:
         *          if (opponentMove == COOPERATE) {
         *               return super.getFinalMove(DEFECT);
         *          }
         *          else {
         *               return super.getFinalMove(COOPERATE);
         *           }
         */
    }

    public void reset() {
        opponentMove = Math.random() > 0.5 ? COOPERATE : DEFECT;
    }
}
