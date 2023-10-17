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
 * Tit-for-Two-Tats strategy
 * @author Tomek Kaczanowski, Jan Humble
 */
public class TFTT extends Strategy {
    private final static String _abbrName = "TFTT";
    private final static String _name = "Tit-for-Two-Tats";
    private final static String _description = "Like TFT but forgives one defection. DEFECTS only if the opponent betrayed him in two last consecutive moves.";
    private double previousOpponentMove;

    public TFTT() {
        super(_abbrName, _name, _description);
    }

    protected TFTT(String abbrName, String name, String description) {
        super(abbrName, name, description);
    }

    /* (non-Javadoc)
     * @see ipdl.Strategy#getMove()
     */
    public double getMove() {
        double move = COOPERATE;
	
        if ((opponentMove == DEFECT) && (previousOpponentMove == DEFECT)) {
            move = DEFECT;
        }
	
        previousOpponentMove = opponentMove;
	
        return move;
    }

    /* (non-Javadoc)
     * @see ipdl.Strategy#reset()
     */
    public void reset() {
        previousOpponentMove = COOPERATE;
    }
}
