package ipdlx.strategy;

import ipdlx.Strategy;

public class WrongHistoricalValuesException extends Exception {
 
    public WrongHistoricalValuesException() {
	super();
    }
	
    public WrongHistoricalValuesException(String message) {
	super(message);
    }
   
}