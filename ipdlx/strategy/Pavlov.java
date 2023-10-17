/*
 * IPDL - Iterated Prisoner's Dilemma Library
 * Copyright (C) 2003-2004 by Tomasz Kaczanowski
 * giaur @ qs dot pl
 * http://www.giaur.qs.pl/ipdl
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package ipdlx.strategy;

import ipdlx.Strategy;


/**
 * Pavlov strategy.
 * @author Tomek Kaczanowski, Jan Humble
 */
public class Pavlov extends Strategy {
    private final static String _abbrName = "Pavlov";
    private final static String _name = "Pavlov";
    private final static String _description =
        "Pavlov plays according to very simple rule: 'if my move in last round brought me a lot of points, then I will repeat it; otherwise I'll change my behaviour.'" +
        "\nPavlov divides game results into two groups: SUCCESS (T or R) and DEFEAT (P or S). If his last result belongs to SUCCESS group he plays the same move, otherwise he plays the other move.";

    /*
            "\nThere is an example of Pavlov vs. TFT game. Let's say that first Pavlov move (randomly chosen) is DEFECTION." +
            "\n1. TFT COOPERATES, Pavlov plays DEFECTION" +
            "\n  the result was T so Pavlov plays the same move" +
            "\n2. TFT plays DEFECTION, Pavlov plays DEFECTION" +
            "\n  the result was P so Pavlov changes his move" +
            "\n3. TFT plays DEFECTION, Pavlov COOPERATES" +
            "\n  the result was S so Pavlov changes his move" +
            "\n4. TFT plays COOPERATES, Pavlov plays DEFECTION" +
            "\n  the result was T so Pavlov plays the same move" +
            "\n...and so on...";
    */

    /** Pavlov remembers his last move */
    double lastMove;

    /** what value does Pavlov consider as succes in previous round */
    double success;

    public Pavlov() {
	this(0.5);
    }

    public Pavlov(double success) {
        super(_abbrName, _name, _description);
        this.success = success;
    }
    
    protected Pavlov(String abbrName, String name, String description,
        double success) {
        super(abbrName, name, description);
        this.success = success;
    }

    /**
     * @return move according to Pavlov strategy
     */
    public double getMove() {
        if (lastResult < success) {
            lastMove = (lastMove == DEFECT) ? COOPERATE : DEFECT;
        }

        return lastMove;
    }

    public void reset() {
        lastResult += ((Math.random() > 0.5) ? (-1) : 1);
        lastMove = (Math.random() + 0.5);
    }

    /**
     * @return success - value which Pavlov considers as success
     */
    public double getSuccess() {
        return success;
    }
}
