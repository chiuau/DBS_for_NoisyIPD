/*
 * TIT-FOR-TAT Improved (version 1.0) (Mar 7, 2005)
 * Copyright (C) 2005-2006 by Tsz-Chiu Au
 * chiu@cs.umd.edu
 * http://www.cs.umd.edu/~chiu
 *
 * Hi. My name is Tsz-Chiu Au, the author of this program and the inventor of the
 * TIT-FOR-TAT Improved Strategy.  I am currently a graduate student in
 * Department of Computer Science, at the University of Maryland, College Park.
 * My email address is chiu@cs.umd.edu, and my telephone no. is 301-405-2716.
 *
 * This program is submitted to the Iterated Prisoner's Dilemma Competition, that will hold
 * at the IEEE Symposium on Computational Intelligence and Games on April 4-6, 2005.
 * The date of submission is March 7, 2005.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 */

import ipdlx.Strategy;

/**
 * TIT-FOR-TAT improved
 * @author Tsz-Chiu Au
 */
public class TFTI extends Strategy {
    private final static String _abbrName = "TFTI";
    private final static String _name = "TFT improved" ;
    private final static String _description = "This strategy is an improvement of TFT. It improves two weaknesses of TFT: (1) a continuous echo of defects occurs when someone makes a mistake, and (2) TFT cannot deal with RAND effectively.  We strengthen the first weakness by prescribing treatment plans when echo occurs.  By measuring the effectiveness of cooperation between two consecutive games, we decide when to switch TFT to ALLD in order to deal with RAND-like strategy." ;

    private final static int exactHistoryLen = 40 ;
    private final static double treatmentEffectiveness = 0.66 ;
    private final static int treatmentEffectivenessHistory = 6 ;
    private final static int treatmentEffectivenessMaxHistory = 20 ;

    private final static int isCheckRAND = 0 ;
    private final static double isRANDratio = 0.55 ;
    private final static int traceRANDhistory = 15 ;

    int isFirstMove ;
    int yourMove ;
    int myMove ;
    int yourLastMove ;
    int myLastMove ;

    int[] HexactYLmove ;
    int[] HexactMLmove ;
    int[] HexactYNmove ;
    int[] HexactMNmove ;

    /* constructor */

    public TFTI() {
        super(_abbrName, _name, _description);
        reset() ;
    }

    /* Update the exact history */

    private void updateExactHistory() {
      int i ;
      for(i=exactHistoryLen-1; i>=1; i--) {
        HexactYLmove[i] = HexactYLmove[i-1] ;
        HexactMLmove[i] = HexactMLmove[i-1] ;
        HexactYNmove[i] = HexactYNmove[i-1] ;
        HexactMNmove[i] = HexactMNmove[i-1] ;
      }
      HexactYLmove[0] = yourLastMove ;
      HexactMLmove[0] = myLastMove ;
      HexactYNmove[0] = yourMove ;
      HexactMNmove[0] = myMove ;
    }

//  private void printExactHistory() {
//    int i ;
//    for(i=exactHistoryLen-1; i>=0; i--){ System.out.printf("%c",(HexactYLmove[i]==1)?'-':'D'); }
//    System.out.printf("\n") ;
//    for(i=exactHistoryLen-1; i>=0; i--){ System.out.printf("%c",(HexactMLmove[i]==1)?'-':'D'); }
//    System.out.printf("\n") ;
//    for(i=exactHistoryLen-1; i>=0; i--){ System.out.printf("%c",(HexactYNmove[i]==1)?'-':'D'); }
//    System.out.printf("\n") ;
//    for(i=exactHistoryLen-1; i>=0; i--){ System.out.printf("%c",(HexactMNmove[i]==1)?'-':'D'); }
//    System.out.printf("\n") ;
//  }

    /* Treatment plans */

    private int isType1Problem() {
      if ( HexactYNmove[0] == 0 && HexactMNmove[0] == 1 &&
           HexactYNmove[1] == 1 && HexactMNmove[1] == 0 &&
           HexactYNmove[2] == 0 && HexactMNmove[2] == 1)
      {
        return 1 ;
      } else {
        return 0 ;
      }
    }

    private int isType2Problem() {
      if ( HexactYNmove[0] == 0 && HexactMNmove[0] == 0 &&
           HexactYNmove[1] == 0 && HexactMNmove[1] == 0 &&
           HexactYNmove[2] == 0 && HexactMNmove[2] == 0)
      {
        return 1 ;
      } else {
        return 0 ;
      }
    }

    private int isTreatmentEffective() {
      int i ;
      int c1,c2 ;

      c1 = 0 ; c2 = 0 ;
      for(i=0; i<treatmentEffectivenessMaxHistory; i++) {
        if (HexactMLmove[i]==1) {
          if (HexactYNmove[i]==1) { c1++ ; }
          c2++ ;
          if (c2>=treatmentEffectivenessHistory) { break ; }
        }
      }

      if (c2==0) {
        return 0 ;
      } else {
        if (((double)c1 / (double)c2) > treatmentEffectiveness ) {
          return 1 ;
        } else {
          return 0 ;
        }
      }
    }

    /* counter RAND */

    private int isRANDlike() {
      int i ;
      int c1,c2 ;
      double r ;

      if (isCheckRAND==1) {
        c1 = 0 ; c2 = 0 ;
        for(i=0; i<exactHistoryLen; i++) {
          if (HexactMLmove[i]==1) {
            if (HexactYNmove[i]==0) { c1++ ; }
            c2++ ;
          }
        }

        r = (double)c1 / (double)c2 ;

        if (c2<=traceRANDhistory) {
          return 0 ;
        } else {
          if (r < isRANDratio && (1-r) < isRANDratio ) {
            return 1 ;
          } else {
            return 0 ;
          }
        }
      } else {
        return 0 ;
      }
    }

    /* Strategy's main procedures */

    int isOpponentRAND ;

    int isInTreatmentPlan ;
    int gameID ;

    public double getMove() {
      int m ;

      gameID++ ;

      if (isFirstMove==1) {
        isFirstMove = 0 ;
        myMove = 1 ;  // to be nice
      } else if (isOpponentRAND==1) {
        myMove = 0 ;
      } else {
        yourMove = (opponentMove == COOPERATE)?1:0 ;

        updateExactHistory() ;

        if (isRANDlike()==1) {
          isOpponentRAND = 1 ;
          m = 0 ;
        } else {
          if (isInTreatmentPlan==1) {
            m = 1 ;
            isInTreatmentPlan = 0 ;
          } else {
            if (isTreatmentEffective()==1) {
              if (isType1Problem()==1) {
                m = 1 ;
              } else if (isType2Problem()==1) {
                m = 1 ;
                isInTreatmentPlan = 1 ;
              } else {
                m = yourMove ;
              }
            } else {
              m = yourMove ;
            }
          }
        }

        yourLastMove = yourMove ; myLastMove = myMove ;   /* proceed to next state */
        myMove = m ;
      }
      return (myMove==0)?DEFECT:COOPERATE ;
    }

    public void reset() {
      int i ;

      yourMove = 1 ;
      myMove = 1 ;
      yourLastMove = 1 ;
      myLastMove = 1 ;
      isFirstMove = 1 ;

      gameID = 0 ; 

      isInTreatmentPlan = 0 ;
      isOpponentRAND = 0 ;

      HexactYLmove = new int[exactHistoryLen] ;
      HexactMLmove = new int[exactHistoryLen] ;
      HexactYNmove = new int[exactHistoryLen] ;
      HexactMNmove = new int[exactHistoryLen] ;

      for(i=0; i<exactHistoryLen; i++) {
        HexactYLmove[i] = -1 ;
        HexactMLmove[i] = -1 ;
        HexactYNmove[i] = -1 ;
        HexactMNmove[i] = -1 ;
      }
    }
}

