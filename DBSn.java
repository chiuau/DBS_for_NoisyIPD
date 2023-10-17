/*
 * Derivative Belief Strategy strategy  (version 1.1) (Mar 1, 2005)
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
 * The date of submission is March 1, 2005.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 */

import ipdlx.Strategy;

/**
 * Derivative Belief Strategy
 * @author Tsz-Chiu Au
 */
public class DBSn extends Strategy {
    private final static String _abbrName = "DBSn";
    private final static String _name = "Derivative Belief Strategy (version n)" ;
    private final static String _description = "This strategy copes with noise by keeping track of the change of hypothetical strategy of the opponent (belief strategy) instead of random forgiveness.  We consider each opponent choice that contradicts to the current belief strategy is either a noise or a change of strategy. The strategy would determine which case after the next few moves. If it is the latter case, a new strategy is derived based on the previous belief strategies and the recent moves. The a priori belief strategy is TFT.  In the calculation of the next best moves, uncertain rules in the belief strategy are replaced with probabilistic rules that are derived from discounted frequencies of the opponent's previous decisions." ;

    private final static double discountRate = 0.75 ;
    private final static int violationTolerance = 9 ;
    private final static int promotionThreshold = 3 ;

    int isFirstMove ;
    int yourMove ;
    int myMove ;
    int yourLastMove ;
    int myLastMove ;

    double[][][] H ;
    int[][] lastBS ;
    int[][] BS;
    int[][] BSe ;
    int[][] BSc ;

    int lastBSviolationCount ;

    /* constructor */

    public DBSn() {
        super(_abbrName, _name, _description);
        reset() ;
    }

    /* the discounted frequency of the opponent's decision rules */

    private void historyDiscount() {
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

    /* Update the belief strategy according to the previous game */

    private void updateBeliefStrategy() {
      int[][] combinedBS = new int[2][2] ;

      if (BS[yourLastMove][myLastMove]>=0) {
        if (BS[yourLastMove][myLastMove] != yourMove) {  // violate the current belief strategy

          if (isBScompatible(combinedBS)==1) {
            lastBS[0][0] = combinedBS[0][0] ;
            lastBS[0][1] = combinedBS[0][1] ;
            lastBS[1][0] = combinedBS[1][0] ;
            lastBS[1][1] = combinedBS[1][1] ;
          } else {
            lastBS[0][0] = BS[0][0] ;
            lastBS[0][1] = BS[0][1] ;
            lastBS[1][0] = BS[1][0] ;
            lastBS[1][1] = BS[1][1] ;
          }
          BS[0][0] = -1 ;
          BS[0][1] = -1 ;
          BS[1][0] = -1 ;
          BS[1][1] = -1 ;

          BSe[0][0] = -1 ; BSc[0][0] = 0 ;
          BSe[0][1] = -1 ; BSc[0][1] = 0 ;
          BSe[1][0] = -1 ; BSc[1][0] = 0 ;
          BSe[1][1] = -1 ; BSc[1][1] = 0 ;

          BSe[yourLastMove][myLastMove] = yourMove ;
          BSc[yourLastMove][myLastMove] = 1 ;

          lastBSviolationCount=1 ;
        }
      } else {
        if (BSe[yourLastMove][myLastMove] == yourMove) {
          if (BSc[yourLastMove][myLastMove] >= promotionThreshold) {
            BS[yourLastMove][myLastMove] = yourMove ;  // promote it to the current belief strategy
          } else {
            BSc[yourLastMove][myLastMove]++ ;
          }
        } else {
          BSe[yourLastMove][myLastMove] = yourMove ;
          BSc[yourLastMove][myLastMove] = 1 ;
        }
      }

      if (lastBS[yourLastMove][myLastMove] != yourMove) {  // even if lastBS[][] < 0
        lastBSviolationCount++ ;
      }
    }

    /* compute the expected oponent's strategy by the belief strategies */

    private int isBScompatible(int[][] combinedBS) {
      if (BS[0][0]==1 && lastBS[0][0]==0) { return 0 ; }
      if (BS[0][0]==0 && lastBS[0][0]==1) { return 0 ; }
      if (BS[0][1]==1 && lastBS[0][1]==0) { return 0 ; }
      if (BS[0][1]==0 && lastBS[0][1]==1) { return 0 ; }
      if (BS[1][0]==1 && lastBS[1][0]==0) { return 0 ; }
      if (BS[1][0]==0 && lastBS[1][0]==1) { return 0 ; }
      if (BS[1][1]==1 && lastBS[1][1]==0) { return 0 ; }
      if (BS[1][1]==0 && lastBS[1][1]==1) { return 0 ; }

      combinedBS[0][0] = (BS[0][0]<0)?(lastBS[0][0]):(BS[0][0]) ;
      combinedBS[0][1] = (BS[0][1]<0)?(lastBS[0][1]):(BS[0][1]) ;
      combinedBS[1][0] = (BS[1][0]<0)?(lastBS[1][0]):(BS[1][0]) ;
      combinedBS[1][1] = (BS[1][1]<0)?(lastBS[1][1]):(BS[1][1]) ;

      return 1 ;
    }

    private void calcExpectedStrategy_from_BS(double[][][] EP, int[][] BS1) {
      if (BS1[0][0]==0) { EP[0][0][0]=1.0; EP[0][0][1]=0.0; }
      if (BS1[0][0]==1) { EP[0][0][0]=0.0; EP[0][0][1]=1.0; }
      if (BS1[0][0]<0)  {
        EP[0][0][0] = H[0][0][0]/(H[0][0][0]+H[0][0][1]) ;
        EP[0][0][1] = H[0][0][1]/(H[0][0][0]+H[0][0][1]) ;
      }
      if (BS1[0][1]==0) { EP[0][1][0]=1.0; EP[0][1][1]=0.0; }
      if (BS1[0][1]==1) { EP[0][1][0]=0.0; EP[0][1][1]=1.0; }
      if (BS1[0][1]<0)  {
        EP[0][1][0] = H[0][1][0]/(H[0][1][0]+H[0][1][1]) ;
        EP[0][1][1] = H[0][1][1]/(H[0][1][0]+H[0][1][1]) ;
      }
      if (BS1[1][0]==0) { EP[1][0][0]=1.0; EP[1][0][1]=0.0; }
      if (BS1[1][0]==1) { EP[1][0][0]=0.0; EP[1][0][1]=1.0; }
      if (BS1[1][0]<0)  {
        EP[1][0][0] = H[1][0][0]/(H[1][0][0]+H[1][0][1]) ;
        EP[1][0][1] = H[1][0][1]/(H[1][0][0]+H[1][0][1]) ;
      }
      if (BS1[1][1]==0) { EP[1][1][0]=1.0; EP[1][1][1]=0.0; }
      if (BS1[1][1]==1) { EP[1][1][0]=0.0; EP[1][1][1]=1.0; }
      if (BS1[1][1]<0)  {
        EP[1][1][0] = H[1][1][0]/(H[1][1][0]+H[1][1][1]) ;
        EP[1][1][1] = H[1][1][1]/(H[1][1][0]+H[1][1][1]) ;
      }
    }

    private double[][][] calcExpectedStrategy() {
      double[][][] EP = new double[2][2][2] ;
      int[][] combinedBS = new int[2][2] ;

      if (lastBSviolationCount<=violationTolerance && isBScompatible(combinedBS)==1) {
        calcExpectedStrategy_from_BS(EP, combinedBS) ;
      } else {
        calcExpectedStrategy_from_BS(EP, BS) ;
      }

      return EP ;
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
      if (isFirstMove==1) {
        isFirstMove = 0 ;
        myMove = 1 ;
      } else {
        yourMove = (opponentMove == COOPERATE)?1:0 ;
        historyDiscount() ;
        updateBeliefStrategy() ;
        yourLastMove = yourMove ; myLastMove = myMove ;  /* proceed to next state */
        myMove = bestMove(calcExpectedStrategy(), yourLastMove, myLastMove);
      }
      return (myMove==0)?DEFECT:COOPERATE ;
    }

    public void reset() {
      opponentMove = COOPERATE ;
      yourMove = 1 ;
      myMove = 1 ;
      yourLastMove = 1 ;
      myLastMove = 1 ;
      isFirstMove = 1 ;

      H = new double[2][2][2] ;
      H[0][0][0] = 1 ;
      H[0][0][1] = 0 ;
      H[0][1][0] = 0 ;
      H[0][1][1] = 1 ;
      H[1][0][0] = 1 ;
      H[1][0][1] = 0 ;
      H[1][1][0] = 0 ;
      H[1][1][1] = 1 ;

      lastBS = new int[2][2] ; BS = new int[2][2] ;
      lastBS[0][0] = 0 ; BS[0][0] = -1 ;
      lastBS[0][1] = 1 ; BS[0][1] = -1 ;
      lastBS[1][0] = 0 ; BS[1][0] = -1 ;
      lastBS[1][1] = 1 ; BS[1][1] = -1 ;

      BSe = new int[2][2] ;  BSc = new int[2][2] ;
      BSe[0][0] = -1 ; BSc[0][0] = 0 ;
      BSe[0][1] = -1 ; BSc[0][1] = 0 ;
      BSe[1][0] = -1 ; BSc[1][0] = 0 ;
      BSe[1][1] = -1 ; BSc[1][1] = 0 ;

      lastBSviolationCount = 0 ;
    }
}

