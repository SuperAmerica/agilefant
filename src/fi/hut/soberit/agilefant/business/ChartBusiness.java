package fi.hut.soberit.agilefant.business;

import fi.hut.soberit.agilefant.model.AFTime;

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
    
    /**
     * Create a project burndown chart as a byte array that is interpreted as
     * a .png file
     * 
     * @param projectId
     *                Id of the project of which the burndown is generated
     * @return Byte array representing a png image file
     */
    public abstract byte[] getProjectBurndown(int projectId);
    
    /**
     * Create a small project burndown chart as a byte array that is
     * interpreted as a .png file
     * 
     * @param projectId
     *                Id of the project of which the burndown is generated
     * @return Byte array representing a png image file
     */
    public abstract byte[] getSmallProjectBurndown(int projectId);
    
    /**
     * Create a load meter as a byte array that is interpreted as a .png file
     * @param userId
     * @return
     */
    public abstract byte[] getLoadMeter(int userId);

}