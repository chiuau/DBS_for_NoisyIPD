/*
 * Derivative Belief Strategy (version 1.2) (Mar 2, 2005)
 * Copyright (C) 2005-2006 by Tsz-Chiu Au
 * chiu@cs.umd.edu
 * http://www.cs.umd.edu/~chiu
 *
 * Hi. My name is Tsz-Chiu Au, the author of this program and the inventor of the
 * Derivative Belief Strategy.  I am currently a graduate student in
 * Department of Computer Science, at the University of Maryland, College Park.
 * My email address is chiu@cs.umd.edu, and my telephone no. is 301-405-2716.
 *
 * This program is submitted to the Iterated Prisoner's Dilemma Competition, that will hold
 * at the IEEE Symposium on Computational Intelligence and Games on April 4-6, 2005.
 * The date of submission is March 3, 2005.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 */

import ipdlx.Strategy;

/**
 * Derivative Belief Strategy (more trick!)
 * @author Tsz-Chiu Au
 */
public class DBSx extends Strategy {
    private final static String _abbrName = "DBSx";
    private final static String _name = "DBS (version x)" ;
    private final static String _description = "This strategy copes with noise by keeping track of the change of hypothetical strategy of the opponent (belief strategy) instead of random forgiveness.  We consider each opponent choice that contradicts to the current belief strategy a change of strategy. The new strategy would then be evolved based on the compatibility of the previous belief strategy and the recent moves. The a priori belief strategy is TFT.  In the calculation of the next best moves, uncertain rules in the belief strategy are replaced with probabilistic rules that are derived from discounted frequencies of the opponent's previous decisions." ;

    private final static int violationThreshold = 4 ;
    private final static int promotionThreshold = 3 ;

    private final static int maxLastBS = 1 ;
    private final static int reuseViolationThreshold = 8 ;

    private final static double discountRate = 0.75 ;


    int isFirstMove ;
    int yourMove ;
    int myMove ;
    int yourLastMove ;
    int myLastMove ;

    int[][]    BS ;
    int[][]    BSv ;
    int[][]    BSe ;
    int[][]    BSc ;

    int[][][] lastBS ;
    int[]     lastBSviolateCount ;

    double[][][] H ;
    int[][][]  Hc ;
    int[][]    Hm ;

    /* constructor */

    public DBSx() {
        super(_abbrName, _name, _description);
        reset() ;
    }

    /* Update the count-based history */

    private void updateDiscountHistory() {
      H[0][0][0] *= discountRate ;
      H[0][0][1] *= discountRate ;
      H[0][1][0] *= discountRate ;
      H[0][1][1] *= discountRate ;
      H[1][0][0] *= discountRate ;
      H[1][0][1] *= discountRate ;
      H[1][1][0] *= discountRate ;
      H[1][1][1] *= discountRate ;

      H[yourLastMove][myLastMove][yourMove] += 1.0 ;
    }

    private double discountedHistoryBasedProb(int y1, int m1) {
      return H[y1][m1][1]/(H[y1][m1][0]+H[y1][m1][1]) ;
    }

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

    /* Update the belief strategy according to the previous game */

    private void updateBeliefStrategy() {
      int i ;
      int v ;
      int[][] combinedBS = new int[2][2] ;

      if (BS[yourLastMove][myLastMove] >= 0) {
        if (BS[yourLastMove][myLastMove] == yourMove) {
          BSv[yourLastMove][myLastMove] = 0 ;
        } else {
          BSv[yourLastMove][myLastMove]++ ;
          if (BSv[yourLastMove][myLastMove] >= violationThreshold) {
            // change belief strategy

            for(i=maxLastBS-1; i>=1; i--) {
              lastBS[i][0][0] = lastBS[i-1][0][0] ;
              lastBS[i][0][1] = lastBS[i-1][0][1] ;
              lastBS[i][1][0] = lastBS[i-1][1][0] ;
              lastBS[i][1][1] = lastBS[i-1][1][1] ;
            }

            if (isBScompatible(0)==1) {
              combineBS(combinedBS, 0) ;
              lastBS[0][0][0] = combinedBS[0][0] ;
              lastBS[0][0][1] = combinedBS[0][1] ;
              lastBS[0][1][0] = combinedBS[1][0] ;
              lastBS[0][1][1] = combinedBS[1][1] ;
            } else {
              lastBS[0][0][0] = BS[0][0] ;
              lastBS[0][0][1] = BS[0][1] ;
              lastBS[0][1][0] = BS[1][0] ;
              lastBS[0][1][1] = BS[1][1] ;
            }

            BS[0][0] = -1 ;
            BS[0][1] = -1 ;
            BS[1][0] = -1 ;
            BS[1][1] = -1 ;

            for(i=0; i<maxLastBS; i++) { 
              lastBSviolateCount[i] = (lastBS[i][yourLastMove][myLastMove]!=yourMove)?1:0 ;
            }

            v = BSv[yourLastMove][myLastMove] ;

            BSv[0][0] = 0 ; BSe[0][0] = -1 ; BSc[0][0] = 0 ;
            BSv[0][1] = 0 ; BSe[0][1] = -1 ; BSc[0][1] = 0 ;
            BSv[1][0] = 0 ; BSe[1][0] = -1 ; BSc[1][0] = 0 ;
            BSv[1][1] = 0 ; BSe[1][1] = -1 ; BSc[1][1] = 0 ;

            BSe[yourLastMove][myLastMove] = yourMove ;
            BSc[yourLastMove][myLastMove] = ((v-1)>=2)?2:(v-1) ;
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

      for(i=0; i<maxLastBS; i++) {
        if (lastBS[i][yourLastMove][myLastMove]>=0  &&
            lastBS[i][yourLastMove][myLastMove] != yourMove) {
          lastBSviolateCount[i]++ ;
        }
      }
    }

    /* compute the expected oponent's strategy by the belief strategies */

    private double calcBSpWithHistory(int bs[][], int y2, int m2) {
      if (bs[y2][m2]>=0) {
        return (double)bs[y2][m2] ;
      } else {
        return discountedHistoryBasedProb(y2,m2) ;
      }
    }

    private int isBScompatible(int i) {
      if (BS[0][0]==1 && lastBS[i][0][0]==0) { return 0 ; }
      if (BS[0][0]==0 && lastBS[i][0][0]==1) { return 0 ; }
      if (BS[0][1]==1 && lastBS[i][0][1]==0) { return 0 ; }
      if (BS[0][1]==0 && lastBS[i][0][1]==1) { return 0 ; }
      if (BS[1][0]==1 && lastBS[i][1][0]==0) { return 0 ; }
      if (BS[1][0]==0 && lastBS[i][1][0]==1) { return 0 ; }
      if (BS[1][1]==1 && lastBS[i][1][1]==0) { return 0 ; }
      if (BS[1][1]==0 && lastBS[i][1][1]==1) { return 0 ; }

      return 1 ;
    }

    private double degreeOfCompatibility(int i) {
      double degree = 0.0 ;

      if (BS[0][0]==lastBS[i][0][0]) { degree += (BS[0][0]>=0)?(1.0):(0.5) ; }
      if (BS[0][1]==lastBS[i][0][1]) { degree += (BS[0][1]>=0)?(1.0):(0.5) ; }
      if (BS[1][0]==lastBS[i][1][0]) { degree += (BS[1][0]>=0)?(1.0):(0.5) ; }
      if (BS[1][1]==lastBS[i][1][1]) { degree += (BS[1][1]>=0)?(1.0):(0.5) ; }

      return degree ;
    }

    private void combineBS(int[][] combinedBS, int i) {
      combinedBS[0][0] = (BS[0][0]<0)?(lastBS[i][0][0]):(BS[0][0]) ;
      combinedBS[0][1] = (BS[0][1]<0)?(lastBS[i][0][1]):(BS[0][1]) ;
      combinedBS[1][0] = (BS[1][0]<0)?(lastBS[i][1][0]):(BS[1][0]) ;
      combinedBS[1][1] = (BS[1][1]<0)?(lastBS[i][1][1]):(BS[1][1]) ;
    }

    private void calcExpectedStrategy(double[][][] EP) {
      int i, imax ;
      double degree, maxdegree ;
      int[][] combinedBS = new int[2][2] ;

      imax=-1 ; maxdegree = -1.0 ;
      for(i=0; i<maxLastBS; i++) {
        if (lastBSviolateCount[i]<=reuseViolationThreshold && isBScompatible(i)==1) {
          degree = degreeOfCompatibility(i) ;
          if (degree>=maxdegree) {
            imax=i ;
            maxdegree=degree ;
          }
        }
      }

      if (imax>=0) {
        combineBS(combinedBS, imax) ;
      } else {
        combinedBS[0][0] = BS[0][0] ;
        combinedBS[0][1] = BS[0][1] ;
        combinedBS[1][0] = BS[1][0] ;
        combinedBS[1][1] = BS[1][1] ;
      }

      EP[0][0][1] = calcBSpWithHistory(combinedBS,0,0) ;
      EP[0][1][1] = calcBSpWithHistory(combinedBS,0,1) ;
      EP[1][0][1] = calcBSpWithHistory(combinedBS,1,0) ;
      EP[1][1][1] = calcBSpWithHistory(combinedBS,1,1) ;

      EP[0][0][0] = 1.0 - EP[0][0][1] ;
      EP[0][1][0] = 1.0 - EP[0][1][1] ;
      EP[1][0][0] = 1.0 - EP[1][0][1] ;
      EP[1][1][0] = 1.0 - EP[1][1][1] ;
    }

    /* calculate the best move by dynamic programming */

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

    /* strategy's main procedures */

    public double getMove() {
      double[][][] EP = new double[2][2][2] ;
      int m ;

      if (isFirstMove==1) {
        isFirstMove = 0 ;
        myMove = 1 ;
      } else {
        yourMove = (opponentMove == COOPERATE)?1:0 ;
        updateCountBasedHistory() ;
        updateDiscountHistory() ;
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

      // Last Belief strategy

      lastBS = new int[maxLastBS][2][2] ;
      lastBSviolateCount = new int[maxLastBS] ;

      for(i=0; i<maxLastBS; i++) {
        lastBS[i][0][0] = 0 ;
        lastBS[i][0][1] = 1 ;
        lastBS[i][1][0] = 0 ;
        lastBS[i][1][1] = 1 ;
        lastBSviolateCount[i] = 0 ;
      }

      // History

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

      H = new double[2][2][2] ;
      H[0][0][0] = 1 ;
      H[0][0][1] = 0 ;
      H[0][1][0] = 0 ;
      H[0][1][1] = 1 ;
      H[1][0][0] = 1 ;
      H[1][0][1] = 0 ;
      H[1][1][0] = 0 ;
      H[1][1][1] = 1 ;
    }
}

