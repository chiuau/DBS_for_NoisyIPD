package ipdlx;

/**
 * Log of the game.
 * A exhaustive description of a game -
 * what moves did players do, what was the result of every round.
 * Usefull for strategy-testing purposes.
 *
 * @author Tomasz Kaczanowski, Jan Humble
 */
public class GameResultLog extends StandardGameResult implements PDValues {

    /** number of rounds where both players played COOPERATE */
    private final static int COCO = 0;

    /** number of rounds where both players played different moves (C & D)*/
    private final static int CODE = 1;

    /** number of rounds where both players played DEFECT */
    private final static int DEDE = 2;

    /** number of rounds */
    private int nbOfRounds;

    /** game log - what moves were chosen in every round*/
    private double[][] log;

    /** game statistics - number of COCO, CODE and DEDE rounds */
    private int[] stats;

    /** names of strategies */
    private String[] strategies;

    /**
     * constructor.
         * @param nbOfRounds number of rounds in this game
         * @param strategies - names of strategies for both players
         */
    public GameResultLog(int nbOfRounds, String[] strategies) {
        super(nbOfRounds);
        this.nbOfRounds = nbOfRounds;
        this.strategies = strategies;
        log = new double[2][nbOfRounds];
        stats = new int[3];
    }

    /**
     * write type of move made by players to game log
     * @param nbOfRound - which round is this
     * @param player - which player A or B
     * @param move - type of move of player - COOPERATION or DEFECTION
     */
    public void writeMove(int nbOfRound, int player, double move) {
        log[player][nbOfRound] = move;
    }

    /**
     * returns game log (for both players)
     * @return game log
     */
    public double[][] getLog() {
        return log;
    }

    /**
     * returns game log for one player
     * @param playerNb player A or B
     * @return game log for one player
     */
    public double[] getLogPlayer(int playerNb) {
        return log[playerNb];
    }

    /**
     * creates statistics based on game log
     */
    public void createStatistics() {
        double move_A;
        double move_B;

        for (int i = 0; i < nbOfRounds; i++) {
            move_A = log[PLAYER_A][i];
            move_B = log[PLAYER_B][i];

            if (move_A == move_B) {
                if (move_A == DEFECT) {
                    stats[DEDE]++;
                } else {
                    stats[COCO]++;
                }
            } else {
                stats[CODE]++;
            }
        }
    }

    /**
    * @return percent of rounds when both players played COOPERATE
    */
    public float getCoCoPercent() {
        return (float) stats[COCO] / nbOfRounds;
    }

    /**
     * @return percent of rounds when one player played COOPERATE and the other DEFECT
     */
    public float getCoDePercent() {
        return (float) stats[CODE] / nbOfRounds;
    }

    /**
     * @return percent of rounds when both players played DEFECT
     */
    public float getDeDePercent() {
        return (float) stats[DEDE] / nbOfRounds;
    }

    /**
     * @return number of roundse when both players played COOPERATE
     */
    public int getCoCoNumber() {
        return stats[COCO];
    }

    /**
     * @return number of rounds when one player played COOPERATE and the other DEFECT
     */
    public int getCoDeNumber() {
        return stats[CODE];
    }

    /**
     * @return number of roundse when both players played DEFECT
     */
    public int getDeDeNumber() {
        return stats[DEDE];
    }

    /**
     * returns number of rounds in this game
     * @return number of rounds in this game
     */
    public int getNbOfRounds() {
        return nbOfRounds;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer("Game Log: ");
        buf.append("\n\nPlayer 1: " + strategies[PLAYER_A]);
        buf.append("\nPlayer 2: " + strategies[PLAYER_B]);
        buf.append("\nNumber of rounds: " + nbOfRounds);
        buf.append("\n\n\t\t\tPLAYER 1\t\tPLAYER 2");

        for (int i = 0; i < nbOfRounds; i++) {
            buf.append("\nROUND " + i);
            buf.append("\t\t" + log[PLAYER_A][i] + "\t\t" +
		       log[PLAYER_B][i]);
        }

        buf.append("\n\npercent of mutual COOPERATION rounds: " +
            (int) (getCoCoPercent() * 100) + "%");
        buf.append("\npercent of COOPERATION vs. DEFECTION rounds: " +
            (int) (getCoDePercent() * 100) + "%");
        buf.append("\npercent of mutual DEFECTION rounds: " +
            (int) (getDeDePercent() * 100) + "%");
        buf.append("\n\n" + super.toString());

        return buf.toString();
    }
}
