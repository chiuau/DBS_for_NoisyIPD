package ipdlx.gui;

import ipdlx.Player;
import ipdlx.Strategy;

import java.util.Properties;

/**
 * base class for all human players
 * @author Jan Humble
 */
public class HumanPlayer extends Player {
    
    private Properties props;

    /**
     * @param strategy
     */
    public HumanPlayer(Properties props, Strategy strategy) {
        super(strategy, props.getProperty("firstname") + " " + props.getProperty("lastname"));
	this.props = props;
    }
    
    
    public final Properties getPlayerProperties() {
	return this.props;
    }
    
}
