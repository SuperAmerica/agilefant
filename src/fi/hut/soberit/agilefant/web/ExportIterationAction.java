package fi.hut.soberit.agilefant.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Workbook;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.business.ExportIterationBusiness;

@Scope("prototype")
@Component("exportIterationAction")
public class ExportIterationAction implements Action {
    private ByteArrayOutputStream iterationData;
    private String exportFileName;
    private int iterationId;
    @Autowired
    private ExportIterationBusiness exportIterationBusiness;

    public String execute() {
        DateTime now = new DateTime();
        exportFileName = "Agilefant_Iteration_" + iterationId + "_"
                + now.toString("yyyyMMDDHHmm");
        iterationData = new ByteArrayOutputStream();
        Workbook exportable = exportIterationBusiness
                .exportIteration(iterationId);
        try {
            exportable.write(iterationData);
        } catch (IOException e) {
            return Action.ERROR;
        }
        return Action.SUCCESS;
    }

    public int getIterationId() {
        return iterationId;
    }

    public void setIterationId(int iterationId) {
        this.iterationId = iterationId;
    }

    public InputStream getIterationData() {
        return new ByteArrayInputStream(iterationData.toByteArray());
    }

    public String getExportFileName() {
        return exportFileName;
    }

}
