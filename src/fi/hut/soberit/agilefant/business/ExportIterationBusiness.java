package fi.hut.soberit.agilefant.business;

import org.apache.poi.ss.usermodel.Workbook;

public interface ExportIterationBusiness {
    public Workbook exportIteration(int iterationId);
}
