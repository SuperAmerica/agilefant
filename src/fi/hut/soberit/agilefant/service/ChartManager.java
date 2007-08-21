package fi.hut.soberit.agilefant.service;

public interface ChartManager {

	/**
	 * Create an iteration burndown chart as a byte array that is
	 * interpreted as a .png file
	 * 
	 * @param iterationId Id of the iteration of which the burndown is generated
	 * @return Byte array representing a png image file
	 */
	public abstract byte[] getIterationBurndown(int iterationId);

}