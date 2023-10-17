/*
 * Learning of opponent strategy with forgiveness (version 1.0) (Mar 1, 2005)
 * Copyright (C) 2005-2006 by Tsz-Chiu Au
 * chiu@cs.umd.edu
 * http://www.cs.umd.edu/~chiu
 *
 * Hi. My name is Tsz-Chiu Au, the author of this program. I am currently a graduate
 * student in Department of Computer Science, at the University of Maryland, College Park.
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
 * Learning of opponent strategy with forgiveness
 * @author Tsz-Chiu Au
 */
public class LSF extends Strategy {
    private final static String _abbrName = "LSF";
    private final static String _name = "Learning of opponent strategy with forgiveness";
    private final static String _description = "This strategy calculates the best next moves by the expected opponent's decision rules that are learnt from discounted frequencies of the opponent's previous decisions.  At the beginning, the opponent's decision rules is assumed to be TFT.  The degree of forgiveness is large in the first retaliation, but it is exponentially decreasing." ;

    private final static double discountRate = 0.75 ;

    double forgiveChance = 0.75 ;

    int isFirstMove ;
    int yourMove ;
    int myMove ;
    int yourLastMove ;
    int myLastMove ;

    double[][][] H ;

    /* constructor */

    public LSF() {
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

    /* compute the expected oponent's strategy according to the discounted frequencies */

    private double[][][] calcExpectedStrategy(double[][][] H) {
      double[][][] EP = new double[2][2][2] ;

      EP[0][0][0] = H[0][0][0]/(H[0][0][0]+H[0][0][1]) ;
      EP[0][0][1] = H[0][0][1]/(H[0][0][0]+H[0][0][1]) ;
      EP[0][1][0] = H[0][1][0]/(H[0][1][0]+H[0][1][1]) ;
      EP[0][1][1] = H[0][1][1]/(H[0][1][0]+H[0][1][1]) ;
      EP[1][0][0] = H[1][0][0]/(H[1][0][0]+H[1][0][1]) ;
      EP[1][0][1] = H[1][0][1]/(H[1][0][0]+H[1][0][1]) ;
      EP[1][1][0] = H[1][1][0]/(H[1][1][0]+H[1][1][1]) ;
      EP[1][1][1] = H[1][1][1]/(H[1][1][0]+H[1][1][1]) ;

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

    /* forgive */

    private int forgive() {
      if (Math.random() < forgiveChance) {
        forgiveChance *= 0.25 ;
        return 1 ;
      } else {
        return 0 ;
      }
    }

    /* strategy's main procedures */

    public double getMove() {
      if (isFirstMove==1) {
        isFirstMove = 0 ;
        myMove = 1 ;
      } else {
        yourMove = (opponentMove == COOPERATE)?1:0 ;
        historyDiscount() ;
        yourLastMove = yourMove ; myLastMove = myMove ;  /* proceed to next state */
        myMove = bestMove(calcExpectedStrategy(H), yourLastMove, myLastMove);
        if (myMove==0 && yourMove==0) { myMove = forgive() ; }
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

      forgiveChance = 0.75 ;
   
      H = new double[2][2][2];
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

