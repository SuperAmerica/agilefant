package fi.hut.soberit.agilefant.fixtures;

import fit.ColumnFixture;

public class Division extends ColumnFixture {
	  public double numerator, denominator;
	  public double quotient() {
	    return numerator/denominator;
	  }
}
