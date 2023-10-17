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
package ipdlx;


/**
 * An exception thrown if the values provided for a payoff
 * matrix don't meet the specific conditions
 * (for example T > R > P > S and 2 * R > T + S)
 * @author Tomek Kaczanowski, Jan Humble
 */
public class WrongGameMatrixValuesException extends RuntimeException {
    public WrongGameMatrixValuesException(String s) {
        super(s);
    }
}
