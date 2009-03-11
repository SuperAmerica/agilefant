package fi.hut.soberit.agilefant.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.opensymphony.xwork.Action;

import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.util.BacklogItemResponsibleContainer;


public class ExportBacklogAction extends BacklogContentsAction {

    private static final long serialVersionUID = 2494955391506953433L;
    
    private CreationHelper helper;
    
    private ByteArrayOutputStream result;
    
    private CellStyle dateStyle;

    public String exportBacklog() {
        
        
       Workbook wb = new HSSFWorkbook();
       result = new ByteArrayOutputStream();
       
       helper = wb.getCreationHelper();
       dateStyle = wb.createCellStyle();
       dateStyle.setDataFormat(helper.createDataFormat().getFormat("h:mm"));
       this.loadContents();
       Sheet sheet = wb.createSheet("Backlog items");
       renderHeader(sheet);
       renderContents(sheet, (short)1);
       
       sheet.autoSizeColumn(0);
       sheet.autoSizeColumn(1);
       sheet.autoSizeColumn(2);
       sheet.autoSizeColumn(3);
       sheet.autoSizeColumn(4);
       sheet.autoSizeColumn(5);
       sheet.autoSizeColumn(6);
       sheet.autoSizeColumn(7);
       
       try {
            wb.write(result);
        } catch (IOException e) {
            return Action.ERROR;
        }
        return Action.SUCCESS;
    }
    
    private void renderContents(Sheet sheet, short startRow) {
        short currentRow = startRow;
        for(BacklogItem item : this.getBacklogItems()) {
            Row row = sheet.createRow(currentRow);
            renderBliRow(row, item);
            currentRow++;
        }
    }
    
    private void renderHeader(Sheet sheet) {
        Row row = sheet.createRow(0);
        Cell name = row.createCell(0);
        Cell iterationGoal = row.createCell(1);
        Cell responsibles = row.createCell(2);
        Cell priority = row.createCell(3);
        Cell status = row.createCell(4);
        Cell effortLeft = row.createCell(5);
        Cell originalEstimate = row.createCell(6);
        Cell effortSpent = row.createCell(7);
        
        name.setCellValue("name");
        iterationGoal.setCellValue("Iteration goal");
        responsibles.setCellValue("Responsibles");
        priority.setCellValue("Priority");
        status.setCellValue("Status");
        effortLeft.setCellValue("Effort left");
        originalEstimate.setCellValue("Original estimate");
        effortSpent.setCellValue("EffortSpent");
    }
    
    private void renderBliRow(Row row, BacklogItem dataItem) {
        Cell name = row.createCell(0);
        Cell iterationGoal = row.createCell(1);
        Cell responsibles = row.createCell(2);
        Cell priority = row.createCell(3);
        Cell status = row.createCell(4);
        Cell effortLeft = row.createCell(5);
        //effortLeft.setCellStyle(dateStyle);
        Cell originalEstimate = row.createCell(6);
        //originalEstimate.setCellStyle(dateStyle);
        Cell effortSpent = row.createCell(7);
        //effortSpent.setCellStyle(dateStyle);

        name.setCellValue(dataItem.getName());
        if (dataItem.getIterationGoal() != null) {
            iterationGoal.setCellValue(dataItem.getIterationGoal().getName());
        } else {
            iterationGoal.setCellValue("");
        }
        if (this.getBacklogResponsibles().get(dataItem) != null) {
            StringBuffer resp = new StringBuffer();
            int totalResp = this.getBacklogResponsibles().get(dataItem).size();
            int currentResp = 1;
            for (BacklogItemResponsibleContainer c : this
                    .getBacklogResponsibles().get(dataItem)) {
                if (c.getUser() != null) {
                    resp.append(c.getUser().getInitials());
                    if (currentResp != totalResp) {
                        resp.append(", ");
                    }
                    currentResp++;
                }
            }
            responsibles.setCellValue(resp.toString());
        } else {
            responsibles.setCellValue("");
        }

        priority.setCellValue(this.getText("backlogItem.priority."
                + dataItem.getPriority().toString()));
        status.setCellValue(this.getText("backlogItem.state."
                + dataItem.getState().toString()));
        if (dataItem.getEffortLeft() != null) {
            effortLeft.setCellValue(dataItem.getEffortLeft().toString());
        } else {
            effortLeft.setCellValue("-");
        }
        if (dataItem.getOriginalEstimate() != null) {
            originalEstimate.setCellValue(dataItem.getOriginalEstimate().toString());
        } else {
            originalEstimate.setCellValue("-");
        }
        if (dataItem.getEffortSpent() != null) {
            effortSpent.setCellValue(dataItem.getEffortSpent().toString());
        } else {
            effortSpent.setCellValue("-");
        }
    }
    
    public InputStream getSheetData() {
        return new ByteArrayInputStream(result.toByteArray());
    }
}
