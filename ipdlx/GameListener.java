package ipdlx;

import java.util.EventListener;

/**
 *
 * @author Jan Humble
 */
public interface GameListener extends EventListener {

    public abstract void gameStarted(Game game);
    public abstract void gameResultPosted(GameResult gameResult);
    public abstract void gameRoundResultPosted(GameRoundResult gameRoundResult);
}