package fi.hut.soberit.agilefant.business;

import fi.hut.soberit.agilefant.model.Iteration;

public interface IterationBurndownBusiness {

    /**
     * Constructs a burndown from the given iteration's history.
     * <p>
     * Transforms the <code>JFreeChart</code> object to an image byte array.
     * @return the byte array containing the burndown chart as image
     */
    public byte[] getIterationBurndown(Iteration iteration, Integer timeZoneOffset);

    public byte[] getSmallIterationBurndown (Iteration iteration, Integer timeZoneOffset);
    
    public byte[] getCustomIterationBurndown(Iteration iteration, Integer width, Integer height, Integer timeZoneOffset);
}
