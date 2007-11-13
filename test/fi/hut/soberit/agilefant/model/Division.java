package fi.hut.soberit.agilefant.model;

import fit.ColumnFixture;

public class Division extends ColumnFixture {
	public double numerator, denominator;

	public double quotient() {
		return numerator / denominator;
	}
}
