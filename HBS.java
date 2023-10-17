/*
 * Hierarchical Belief Strategy (version 1.0) (Mar 2, 2005)
 * Copyright (C) 2005-2006 by Tsz-Chiu Au
 * chiu@cs.umd.edu
 * http://www.cs.umd.edu/~chiu
 *
 * Hi. My name is Tsz-Chiu Au, the author of this program and the inventor of the
 * Hierarchical Belief Strategy.  I am currently a graduate student in
 * Department of Computer Science, at the University of Maryland, College Park.
 * My email address is chiu@cs.umd.edu, and my telephone no. is 301-405-2716.
 *
 * This program is submitted to the Iterated Prisoner's Dilemma Competition, that will hold
 * at the IEEE Symposium on Computational Intelligence and Games on April 4-6, 2005.
 * The date of submission is March 2, 2005.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 */

import ipdlx.Strategy;

/**
 * Hierarchical Belief Strategy
 * @author Tsz-Chiu Au
 */
public class HBS extends Strategy {
    private final static String _abbrName = "HBS";
    private final static String _name = "Hierarchical Belief Strategy" ;
    private final static String _description = "This strategy copes with noise by keeping track of the change of hypothetical strategy of the opponent (belief strategy) instead of random forgiveness.  We consider each opponent choice that contradicts to the current belief strategy a change of strategy. The new strategy would then be evolved based on the compatibility of the previous belief strategy and the recent moves. The a priori belief strategy is TFT.  In the calculation of the next best moves, uncertain rules in the belief strategy are replaced with probabilistic rules that are derived from discounted frequencies of the opponent's previous decisions." ;

    private final static int    exactHistoryLen = 7 ;
    private final static double Hf_discountRate = 0.75 ;

    private final static int violationThreshold = 2 ;
    private final static int promotionThreshold = 3 ;

    private final static int maxLastBS = 2 ;
    private final static double exactViolationRatioThreshold = 0.3 ;
    private final static double minBSage = 5 ;

    int isFirstMove ;
    int yourMove ;
    int myMove ;
    int yourLastMove ;
    int myLastMove ;

    int[][]    BS ;
    int[][]    BSv ;
    int[][]    BSe ;
    int[][]    BSc ;
    int        BSage ;

    int[][][] lastBS ;

    int[] HexactYLmove ;
    int[] HexactMLmove ;
    int[] HexactYNmove ;
    double[][][] Hf ;
    int[][][]    Hc ;
    int[][]      Hm ;

    /* constructor */

    public HBS() {
        super(_abbrName, _name, _description);
        reset() ;
    }

    /* ************************************************* */
    /* HISTORY: record the history of the previous games */
    /* ************************************************* */

    /* The exact move history */

    private void updateExactHistory() {
      int i ;
      for(i=exactHistoryLen-1; i>=1; i--) {
        HexactYLmove[i] = HexactYLmove[i-1] ;
        HexactMLmove[i] = HexactMLmove[i-1] ;
        HexactYNmove[i] = HexactYNmove[i-1] ;
      }
      HexactYLmove[0] = yourLastMove ;
      HexactMLmove[0] = myLastMove ;
      HexactYNmove[0] = yourMove ;
    }

    private double ExactHistoryMatchRatio(int age, int m00, int m01, int m10, int m11) {
      int i ;
      double len = 0.0 ;
      double match = 0.0 ;
      int traceBackLen = (age<=exactHistoryLen)?age:exactHistoryLen ;

      for(i=0; i<traceBackLen; i++) {
        if (HexactYNmove[i]>=0) {
          len += 1.0 ;
          if (HexactYLmove[i]==0 && HexactMLmove[i]==0 && HexactYNmove[i]==m00) { match += 1.0 ; } ;
          if (HexactYLmove[i]==0 && HexactMLmove[i]==1 && HexactYNmove[i]==m01) { match += 1.0 ; } ;
          if (HexactYLmove[i]==1 && HexactMLmove[i]==0 && HexactYNmove[i]==m10) { match += 1.0 ; } ;
          if (HexactYLmove[i]==1 && HexactMLmove[i]==1 && HexactYNmove[i]==m11) { match += 1.0 ; } ;
        }
      }
      return match / len ;
    }

    /* The discounted frequency-based move history */

    private void updateDiscountHistory() {
      Hf[0][0][0] *= Hf_discountRate ;
      Hf[0][0][1] *= Hf_discountRate ;
      Hf[0][1][0] *= Hf_discountRate ;
      Hf[0][1][1] *= Hf_discountRate ;
      Hf[1][0][0] *= Hf_discountRate ;
      Hf[1][0][1] *= Hf_discountRate ;
      Hf[1][1][0] *= Hf_discountRate ;
      Hf[1][1][1] *= Hf_discountRate ;

      Hf[yourLastMove][myLastMove][yourMove] += 1.0 ;
    }

    private double discountedHistoryBasedProb(int y1, int m1) {
      return Hf[y1][m1][1]/(Hf[y1][m1][0]+Hf[y1][m1][1]) ;
    }

    /* The recent count-based move history */

    private void updateCountBasedHistory() {
      if (Hm[yourLastMove][myLastMove] == yourMove) {
        Hc[yourLastMove][myLastMove][yourMove]++ ;
      } else {
        Hm[yourLastMove][myLastMove] = yourMove ;
        Hc[yourLastMove][myLastMove][yourMove] = 1 ;
      }
    }

    private double countBasedProb(int y1, int m1) {
      return ((double)Hc[y1][m1][1])/((double)(Hc[y1][m1][0]+Hc[y1][m1][1])) ;
    }



    /* ******************************************************************** */
    /* UPDATE BS: Update the belief strategy according to the previous game */
    /* ******************************************************************** */

    private void collapseLastBSs() {
      int i ;
      int[][] combinedBS = new int[2][2] ;

      for(i=maxLastBS-1; i>=1; i--) {
        lastBS[i][0][0] = lastBS[i-1][0][0] ;
        lastBS[i][0][1] = lastBS[i-1][0][1] ;
        lastBS[i][1][0] = lastBS[i-1][1][0] ;
        lastBS[i][1][1] = lastBS[i-1][1][1] ;
      }

      combineEverylastBS(combinedBS) ;

      lastBS[0][0][0] = combinedBS[0][0] ;
      lastBS[0][0][1] = combinedBS[0][1] ;
      lastBS[0][1][0] = combinedBS[1][0] ;
      lastBS[0][1][1] = combinedBS[1][1] ;
    }

    private void resetBSandAuxInfo() {
      int v ;

      BS[0][0] = -1 ;
      BS[0][1] = -1 ;
      BS[1][0] = -1 ;
      BS[1][1] = -1 ;

      v = BSv[yourLastMove][myLastMove] ;

      BSv[0][0] = 0 ; BSe[0][0] = -1 ; BSc[0][0] = 0 ;
      BSv[0][1] = 0 ; BSe[0][1] = -1 ; BSc[0][1] = 0 ;
      BSv[1][0] = 0 ; BSe[1][0] = -1 ; BSc[1][0] = 0 ;
      BSv[1][1] = 0 ; BSe[1][1] = -1 ; BSc[1][1] = 0 ;

      BSe[yourLastMove][myLastMove] = yourMove ;
      BSc[yourLastMove][myLastMove] = ((v-1)>=2)?2:(v-1) ;

      BSage=1 ;
    }

    private void updateBeliefStrategy() {
      int i ;
      int[][] combinedBS = new int[2][2] ;

      BSage++ ;

      if (BS[yourLastMove][myLastMove] >= 0) {
        if (BS[yourLastMove][myLastMove] == yourMove) {
          BSv[yourLastMove][myLastMove] = 0 ;
        } else {
          BSv[yourLastMove][myLastMove]++ ;
          if (BSv[yourLastMove][myLastMove] >= violationThreshold) {
            collapseLastBSs() ;    // change belief strategy
            resetBSandAuxInfo() ;
          }
        }
      } else {
        if (BSe[yourLastMove][myLastMove] == yourMove) {
          if (BSc[yourLastMove][myLastMove] >= promotionThreshold) {
            BS[yourLastMove][myLastMove] = yourMove ;
            BSv[yourLastMove][myLastMove] = 0 ;
          } else {
            BSc[yourLastMove][myLastMove]++ ;
          }
        } else { // BSe[][] == -1 or != yourMove
          BSe[yourLastMove][myLastMove] = yourMove ;
          BSc[yourLastMove][myLastMove] = 1 ;
        }
      }

//      if (BSage>=minBSage && ExactHistoryMatchRatio(BSage,BS[0][0],BS[0][1],BS[1][0],BS[1][1]) > exactViolationRatioThreshold) {
//        collapseLastBSs() ;    // change belief strategy
//        resetBSandAuxInfo() ;
//
//        BSc[yourLastMove][myLastMove] = 1 ;
//      }
    }


    /* **************************************************************************** */
    /* combine BS: compute the expected oponent's strategy by the belief strategies */
    /* **************************************************************************** */

    private int isBScompatibleWithLastBS(int[][] bs, int i) {
      if (bs[0][0]==1 && lastBS[i][0][0]==0) { return 0 ; }
      if (bs[0][0]==0 && lastBS[i][0][0]==1) { return 0 ; }
      if (bs[0][1]==1 && lastBS[i][0][1]==0) { return 0 ; }
      if (bs[0][1]==0 && lastBS[i][0][1]==1) { return 0 ; }
      if (bs[1][0]==1 && lastBS[i][1][0]==0) { return 0 ; }
      if (bs[1][0]==0 && lastBS[i][1][0]==1) { return 0 ; }
      if (bs[1][1]==1 && lastBS[i][1][1]==0) { return 0 ; }
      if (bs[1][1]==0 && lastBS[i][1][1]==1) { return 0 ; }

      return 1 ;
    }

    private void combineBSwithLastBS(int[][] combinedBS, int[][] bs, int i) {
      combinedBS[0][0] = (bs[0][0]<0)?(lastBS[i][0][0]):(bs[0][0]) ;
      combinedBS[0][1] = (bs[0][1]<0)?(lastBS[i][0][1]):(bs[0][1]) ;
      combinedBS[1][0] = (bs[1][0]<0)?(lastBS[i][1][0]):(bs[1][0]) ;
      combinedBS[1][1] = (bs[1][1]<0)?(lastBS[i][1][1]):(bs[1][1]) ;
    }

    private double calcBSpWithDiscountedHistory(int[][] bs, int y2, int m2) {
      if (bs[y2][m2]>=0) {
        return (double)bs[y2][m2] ;
      } else {
        return discountedHistoryBasedProb(y2,m2) ;
      }
    }

    private void combineEverylastBS(int[][] combinedBS) {
      int i ;
      combinedBS[0][0] = BS[0][0] ;
      combinedBS[0][1] = BS[0][1] ;
      combinedBS[1][0] = BS[1][0] ;
      combinedBS[1][1] = BS[1][1] ;

      for(i=0; i<maxLastBS; i++) {
        if (isBScompatibleWithLastBS(combinedBS,i)==1) {
          if (BSage<minBSage) {
            combineBSwithLastBS(combinedBS, combinedBS, i) ;
          } else {
            if (ExactHistoryMatchRatio(BSage,lastBS[i][0][0], lastBS[i][0][1],lastBS[i][1][0],lastBS[i][1][1]) <= exactViolationRatioThreshold) {
              combineBSwithLastBS(combinedBS, combinedBS, i) ;
            }
          }
        }
      }
    }

    private void calcExpectedStrategy(double[][][] EP) {
      int[][] combinedBS = new int[2][2] ;

      combineEverylastBS(combinedBS) ;

      EP[0][0][1] = calcBSpWithDiscountedHistory(combinedBS,0,0) ;
      EP[0][1][1] = calcBSpWithDiscountedHistory(combinedBS,0,1) ;
      EP[1][0][1] = calcBSpWithDiscountedHistory(combinedBS,1,0) ;
      EP[1][1][1] = calcBSpWithDiscountedHistory(combinedBS,1,1) ;

      EP[0][0][0] = 1.0 - EP[0][0][1] ;
      EP[0][1][0] = 1.0 - EP[0][1][1] ;
      EP[1][0][0] = 1.0 - EP[1][0][1] ;
      EP[1][1][0] = 1.0 - EP[1][1][1] ;
    }

    /* ********************************************************* */
    /* BEST MOVE: calculate the best move by dynamic programming */
    /* ********************************************************* */

    private void calcExpectedScore_init(double[][] S) {
      S[0][0] = 1 ;
      S[0][1] = 0 ;
      S[1][0] = 5 ;
      S[1][1] = 3 ;
    }

    private void calcExpectedScore_calc(double[][] S, double[][] S2, double[][][] EP, int y1,int m1){
      double score0 = EP[y1][m1][0]*(1+S2[0][0]) + EP[y1][m1][1]*(5+S2[1][0]) ;
      double score1 = EP[y1][m1][0]*(0+S2[0][1]) + EP[y1][m1][1]*(3+S2[1][1]) ;
      S[y1][m1] = (score0 >= score1)?score0:score1 ;
    }

    private void calcExpectedScore_iter(double[][] S, double[][] S2, double[][][] EP) {
      calcExpectedScore_calc(S,S2,EP,0,0) ;
      calcExpectedScore_calc(S,S2,EP,0,1) ;
      calcExpectedScore_calc(S,S2,EP,1,0) ;
      calcExpectedScore_calc(S,S2,EP,1,1) ;
    }

    private double[][] calcExpectedScore(double[][][] EP) {
      double[][] escore1 = new double[2][2];
      double[][] escore2 = new double[2][2];

      calcExpectedScore_init(escore1) ;
      for(int i=0; i<30;  i++) {
        calcExpectedScore_iter(escore2, escore1, EP) ;
        calcExpectedScore_iter(escore1, escore2, EP) ;
      }
      return escore1 ;
    }

    private int bestMove(double[][][] EP, int y1, int m1) {
      double[][] S2 = calcExpectedScore(EP) ;
      double score0 = EP[y1][m1][0]*(1+S2[0][0]) + EP[y1][m1][1]*(5+S2[1][0]) ;
      double score1 = EP[y1][m1][0]*(0+S2[0][1]) + EP[y1][m1][1]*(3+S2[1][1]) ;
      return (score0>score1)?0:1 ;
    }

    /* ******************************** */
    /* MAIN: strategy's main procedures */
    /* ******************************** */

    public double getMove() {
      double[][][] EP = new double[2][2][2] ;
      int m ;

      if (isFirstMove==1) {
        isFirstMove = 0 ;
        myMove = 1 ;
      } else {
        yourMove = (opponentMove == COOPERATE)?1:0 ;

        updateExactHistory() ;
        updateDiscountHistory() ;
        // updateCountBasedHistory() ;

        updateBeliefStrategy() ;
        calcExpectedStrategy(EP) ;
        m = bestMove(EP, yourMove, myMove);
        yourLastMove = yourMove ; myLastMove = myMove ;  /* proceed to next state */
        myMove = m ;
      }
      return (myMove==0)?DEFECT:COOPERATE ;
    }

    public void reset() {
      int i ;

      opponentMove = COOPERATE ;
      yourMove = 1 ;
      myMove = 1 ;
      yourLastMove = 1 ;
      myLastMove = 1 ;
      isFirstMove = 1 ;

      // for current BS

      BS = new int[2][2] ;
      BS[0][0] = -1 ;
      BS[0][1] = -1 ;
      BS[1][0] = -1 ;
      BS[1][1] = -1 ;

      BSv = new int[2][2] ; BSe = new int[2][2] ;  BSc = new int[2][2] ;
      BSv[0][0] = 0 ; BSe[0][0] = -1 ; BSc[0][0] = 0 ;
      BSv[0][1] = 0 ; BSe[0][1] = -1 ; BSc[0][1] = 0 ;
      BSv[1][0] = 0 ; BSe[1][0] = -1 ; BSc[1][0] = 0 ;
      BSv[1][1] = 0 ; BSe[1][1] = -1 ; BSc[1][1] = 0 ;

      BSage = 1 ;

      // for Last BS

      lastBS = new int[maxLastBS][2][2] ;

      for(i=0; i<maxLastBS; i++) {  // a prior TFT
        lastBS[i][0][0] = 0 ;
        lastBS[i][0][1] = 1 ;
        lastBS[i][1][0] = 0 ;
        lastBS[i][1][1] = 1 ;
//        lastBSviolateCount[i] = 0 ;
      }

//      if (maxLastBS>=2) {   // beware of GRIM
//        lastBS[maxLastBS-1][0][0] = 0 ;
//        lastBS[maxLastBS-1][0][1] = 0 ;
//        lastBS[maxLastBS-1][1][0] = 0 ;
//        lastBS[maxLastBS-1][1][1] = 0 ;
//      }

      // move history

      HexactYLmove = new int[exactHistoryLen] ;
      HexactMLmove = new int[exactHistoryLen] ;
      HexactYNmove = new int[exactHistoryLen] ;

      for(i=0; i<exactHistoryLen; i++) {
        HexactYLmove[i] = -1 ;
        HexactMLmove[i] = -1 ;
        HexactYNmove[i] = -1 ;
      }
      HexactYLmove[0] = 1 ;
      HexactMLmove[0] = 1 ;
      HexactYNmove[0] = 1 ;

      Hc = new int[2][2][2] ;
      Hc[0][0][0] = 1 ;
      Hc[0][0][1] = 0 ;
      Hc[0][1][0] = 0 ;
      Hc[0][1][1] = 1 ;
      Hc[1][0][0] = 1 ;
      Hc[1][0][1] = 0 ;
      Hc[1][1][0] = 0 ;
      Hc[1][1][1] = 1 ;

      Hm = new int[2][2] ;
      Hm[0][0] = 0 ;
      Hm[0][1] = 1 ;
      Hm[1][0] = 0 ;
      Hm[1][1] = 1 ;

      Hf = new double[2][2][2] ;
      Hf[0][0][0] = 1 ;
      Hf[0][0][1] = 0 ;
      Hf[0][1][0] = 0 ;
      Hf[0][1][1] = 1 ;
      Hf[1][0][0] = 1 ;
      Hf[1][0][1] = 0 ;
      Hf[1][1][0] = 0 ;
      Hf[1][1][1] = 1 ;
    }
}

