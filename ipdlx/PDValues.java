package ipdlx;

/**
 * Common values for PD game
 * @author Tomasz Kaczanowski, Jan Humble
 * @version 1.0.1
 */
public interface PDValues {
    /** names of available moves - cooperation, defection and exit 
     * the third move - exit - will be used in games during which players
     * can decide when to stop playing */
    final static String[] moves = { "COOPERATION", "DEFECTION", "EXIT" };

    // constant values for moves' names 

    /** constant value for COOPERATE move */
    final static double COOPERATE = 1.0;

    /** constant value for DEFECT move */
    final static double DEFECT = 0.0;

    /** constant value for EXIT move 
     * not used yet, but if you want to give your strategies an 'exit' option
     * you can use this value to indicate this */
    final static double EXIT = 2;

    // constant values for players

    /** constant value for PLAYER A */
    final static int PLAYER_A = 0;
    
    /** constant value for PLAYER B */
    final static int PLAYER_B = 1;
    
    // constant values for default game matrix values
    
    /** default value for sucker's payoff */
    final static double DEFAULT_SUCKER = 0.0;
    
    /** default value for punishment */
    final static double DEFAULT_PUNISHMENT = 2.0;
    
    /** default value for reward */
    final static double DEFAULT_REWARD = 4.0;
    
    /** default value for temptation to defect */
    final static double DEFAULT_TEMPTATION = 5.0;
    
    /** default value for probability of mistake level */
    float DEFAULT_MISTAKE_LEVEL = 0.0f;
}
