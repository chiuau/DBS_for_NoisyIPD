import java.lang.* ;
import ipdlx.*;
import ipdlx.strategy.*;
import ipdlx.gui.*;

public class TMain {

  private static double noise = 0.1 ;

  private static int p1_co_num, p2_co_num ;
  private static int p1_score, p2_score ;

  private static void printMove(Strategy p, double m) {
    if (m == PDValues.COOPERATE) {
      System.out.println(p.getAbbrName() + " cooperated.") ;
    } else {
      System.out.println(p.getAbbrName() + " defected.") ;
    }
  }

  private static void play(Strategy p1, Strategy p2, int round_num) {
    double p1move, p2move ;
    String p1_move_history = "" ;
    String p2_move_history = "" ;
    int is_p1_noise ;
    int is_p2_noise ;

    p1_co_num = 0 ; p2_co_num = 0 ;
    p1_score = 0; p2_score = 0 ;

    p1.reset();
    p2.reset();

    for(int i=0; i<round_num; i++) {
      p1move = p1.getMove() ;
      p2move = p2.getMove() ;

      if (Math.random() < noise) {
        p1move = (p1move == PDValues.COOPERATE)?(PDValues.DEFECT):(PDValues.COOPERATE) ;
        is_p1_noise = 1 ;
      } else {
        is_p1_noise = 0 ;
      }
      if (Math.random() < noise) {
        p2move = (p2move == PDValues.COOPERATE)?(PDValues.DEFECT):(PDValues.COOPERATE) ;
        is_p2_noise = 1 ;
      } else {
        is_p2_noise = 0 ;
      }

      if (p1move == PDValues.COOPERATE) {
        p1_co_num++ ;
        if (is_p1_noise==0) {
          p1_move_history = p1_move_history + "-" ;
        } else {
          p1_move_history = p1_move_history + "[1m[31m-[0m" ;
        }
      } else {
        if (is_p1_noise==0) {
          p1_move_history = p1_move_history + "D" ;
        } else {
          p1_move_history = p1_move_history + "[1m[31mD[0m" ;
        }
      }
      if (p2move == PDValues.COOPERATE) {
        p2_co_num++ ;
        if (is_p2_noise==0) {
          p2_move_history = p2_move_history + "-" ;
        } else {
          p2_move_history = p2_move_history + "[1m[31m-[0m" ;
        }
      } else {
        if (is_p2_noise==0) {
          p2_move_history = p2_move_history + "D" ;
        } else {
          p2_move_history = p2_move_history + "[1m[31mD[0m" ;
        }
      }

      if (p1move == PDValues.COOPERATE && p2move == PDValues.COOPERATE) {
        p1_score += 3; p2_score += 3;
      } else if (p1move == PDValues.COOPERATE) {
        p1_score += 0; p2_score += 5;
      } else if (p2move == PDValues.COOPERATE) {
        p1_score += 5; p2_score += 0;
      } else {
        p1_score += 1; p2_score += 1;
      }

      p1.setOpponentMove(p2move) ;
      p2.setOpponentMove(p1move) ;
    }

    System.out.printf("       %4s vs %4s", p1.getAbbrName(), p2.getAbbrName()) ;
    System.out.printf("  co:(%.2f,%.2f)", p1_co_num/(float)round_num, p2_co_num/(float)round_num) ;
    System.out.printf(" score:(%d,%d)", p1_score, p2_score) ;
    System.out.printf(",(%.2f,%.2f)", p1_score/(3*(float)round_num), p2_score/(3*(float)round_num)) ;
    if (p1_score>p2_score) {
      System.out.printf(" %4s win", p1.getAbbrName()) ;
    } else if (p1_score<p2_score) {
      System.out.printf(" %4s lose", p1.getAbbrName()) ;
    } else {
      System.out.printf(" tie" ) ;
    }
    System.out.printf("\n") ;

    System.out.printf("%5s: %s\n", p1.getAbbrName(), p1_move_history) ;
    System.out.printf("%5s: %s\n", p2.getAbbrName(), p2_move_history) ;
    System.out.printf("\n") ;
  }

  public static void main(String[] args) {

    int round_num = 90 ;
    int N = 20 ;
    Strategy p[] = new Strategy[20];

    // int id = Integer.parseInt(args[0]) ;

    p[0] = new TFT() ;
    p[1] = new RAND() ;
    p[2] = new TFTT() ;
    p[3] = new STFT() ;
    p[4] = new Pavlov() ;
    p[5] = new NEG() ;
    p[6] = new ALLC() ;
    p[7] = new ALLD() ;
    p[8] = new GRIM() ;
    p[9] = new GRIMa() ;
    p[10] = new TFTF() ;
    p[11] = new TFTTa() ;
    p[12] = new DBSm() ;
    p[13] = new HBS() ;
    p[14] = new HBSbad() ;
    p[15] = new DBSpl() ;
    p[16] = new LSF() ;
    p[17] = new DBStft() ;
    p[18] = new TFTI() ;
    p[19] = new DBSz() ;

//    int id = 18 ;
    int id = N-1 ;

    System.out.printf("============ %d rounds ============\n\n", round_num) ;

    for(int i=0; i<N; i++) {
      play(p[id],p[i],round_num) ;
    }
  }
}
