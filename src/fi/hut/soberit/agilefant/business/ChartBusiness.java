package fi.hut.soberit.agilefant.business;

public interface ChartBusiness {

    /**
     * Create an iteration burndown chart as a byte array that is interpreted as
     * a .png file
     * 
     * @param iterationId
     *                Id of the iteration of which the burndown is generated
     * @return Byte array representing a png image file
     */
    public abstract byte[] getIterationBurndown(int iterationId);

    /**
     * Create a small iteration burndown chart as a byte array that is
     * interpreted as a .png file
     * 
     * @param iterationId
     *                Id of the iteration of which the burndown is generated
     * @return Byte array representing a png image file
     */
    public abstract byte[] getSmallIterationBurndown(int iterationId);

}