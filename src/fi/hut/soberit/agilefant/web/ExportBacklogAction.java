package fi.hut.soberit.agilefant.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.opensymphony.xwork.Action;

import fi.hut.soberit.agilefant.business.ChartBusiness;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.BacklogItemHourEntry;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.IterationGoal;
import fi.hut.soberit.agilefant.util.BacklogItemResponsibleContainer;


public class ExportBacklogAction extends BacklogContentsAction {

    private static final long serialVersionUID = 2494955391506953433L;
    
    private CreationHelper helper;
    
    private ByteArrayOutputStream result;
    
    private CellStyle dateStyle;
    
    private ChartBusiness chartBusiness;
    
    private CellStyle bgStyle;
    
    private CellStyle boldStyle;
    

    public String exportBacklog() {
     
        
       Workbook wb = new HSSFWorkbook();
       bgStyle = wb.createCellStyle();
       bgStyle.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
       result = new ByteArrayOutputStream();
       
       helper = wb.getCreationHelper();
       dateStyle = wb.createCellStyle();
       dateStyle.setDataFormat(helper.createDataFormat().getFormat("m.d.yy h:mm"));
       boldStyle = wb.createCellStyle();
       Font bfont = wb.createFont();
       bfont.setBoldweight(Font.BOLDWEIGHT_BOLD);
       boldStyle.setFont(bfont);
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
       
       if(this.getBacklog() instanceof Iteration) {
           byte[] burndownData = chartBusiness.getIterationBurndown(backlogId);
           int pictureIdx = wb.addPicture(burndownData, Workbook.PICTURE_TYPE_PNG);
           Sheet burndown = wb.createSheet("Burndown");
           Drawing drawing = burndown.createDrawingPatriarch();
           ClientAnchor anchor = helper.createClientAnchor();
           anchor.setCol1(3);
           anchor.setRow1(2);
           Picture pict = drawing.createPicture(anchor, pictureIdx);
           pict.resize();    
           
           Sheet goals = wb.createSheet("Iteration goals");
           renderIterationGoals(goals);
           goals.autoSizeColumn(0);
           goals.autoSizeColumn(1);
           goals.autoSizeColumn(2);
           goals.autoSizeColumn(3);
           
           Sheet effort = wb.createSheet("Spent effort");
           renderIterationGoalsEffort(effort);
           effort.autoSizeColumn(0);
           effort.autoSizeColumn(1);
           effort.autoSizeColumn(2);
           effort.autoSizeColumn(3);
       }
       
       
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
            setRowStyle(row, currentRow);
            currentRow++;
        }
    }
    
    private void renderIterationGoals(Sheet sheet) {
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("Name");
        row.createCell(1).setCellValue("Effort left (hours)");
        row.createCell(2).setCellValue("Original estimate (hours)");
        row.createCell(3).setCellValue("Effort spent (hours)");
        int rowNum = 1;
        Collection<IterationGoal> goals = ((Iteration) backlog)
                .getIterationGoals();
        for (IterationGoal goal : goals) {
            Row cRow = sheet.createRow(rowNum);
            Cell cName = cRow.createCell(0);
            cName.setCellValue(goal.getName());
            cName.setCellStyle(boldStyle);
            cRow.createCell(1).setCellValue("??");
            cRow.createCell(2).setCellValue("??");
            cRow.createCell(3).setCellValue("??");
            setRowStyle(cRow,rowNum);
            rowNum++;
            for (BacklogItem item : goal.getBacklogItems()) {
                Row iRow = sheet.createRow(rowNum);
                iRow.createCell(0).setCellValue(item.getName());
                effortToCell(iRow.createCell(1), item.getEffortLeft());
                effortToCell(iRow.createCell(2), item.getOriginalEstimate());
                effortToCell(iRow.createCell(3), item.getEffortSpent());
                setRowStyle(iRow, rowNum);
                rowNum++;
            }
        }
    }
    
    private void renderIterationGoalsEffort(Sheet sheet) {
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("Name");
        row.createCell(1).setCellValue("User");
        row.createCell(2).setCellValue("Date");
        row.createCell(3).setCellValue("Effort spent (hours)");
        int rowNum = 1;
        Collection<IterationGoal> goals = ((Iteration) backlog)
                .getIterationGoals();
        for (IterationGoal goal : goals) {
            Row cRow = sheet.createRow(rowNum);
            Cell cName = cRow.createCell(0);
            cName.setCellValue(goal.getName());
            cName.setCellStyle(boldStyle);
            cRow.createCell(1).setCellValue("");
            cRow.createCell(2).setCellValue("");
            cRow.createCell(3).setCellValue("??");
            setRowStyle(cRow,rowNum);
            rowNum++;
            for (BacklogItem item : goal.getBacklogItems()) {
                for (BacklogItemHourEntry entry : item.getHourEntries()) {
                    Row hRow = sheet.createRow(rowNum);
                    if(entry.getDescription() != null) {
                        hRow.createCell(0).setCellValue(item.getName() + " : " + entry.getDescription());
                    } else {
                        hRow.createCell(0).setCellValue(item.getName());
                    }
                    if (entry.getUser() != null) {
                        hRow.createCell(1).setCellValue(
                                entry.getUser().getFullName());
                    } else {
                        hRow.createCell(1).setCellValue("");
                    }
                    Cell d = hRow.createCell(2);
                    d.setCellValue(entry.getDate());
                    d.setCellStyle(dateStyle);
                    effortToCell(hRow.createCell(3), entry.getTimeSpent());
                    setRowStyle(hRow, rowNum);
                    rowNum++;
                }
            }
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
        Cell originalEstimate = row.createCell(6);
        Cell effortSpent = row.createCell(7);
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
        effortToCell(effortLeft, dataItem.getEffortLeft());
        effortToCell(originalEstimate, dataItem.getOriginalEstimate());
        effortToCell(effortSpent, dataItem.getEffortSpent());
    }
    private void setRowStyle(Row row, int num) {
        /*if((num-1)%2 == 1) {
            Iterator<Cell> iter = row.cellIterator();
            while(iter.hasNext()) {
                Cell cur = iter.next();
                cur.setCellStyle(bgStyle);
            }
        }*/
    }
    
    private void effortToCell(Cell cell, AFTime eff) {
        if(eff != null) {
            cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
            cell.setCellValue(eff.toDouble());
        } else {
            cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
            cell.setCellValue(0);
        }
    }
    
    public InputStream getSheetData() {
        return new ByteArrayInputStream(result.toByteArray());
    }
    public void setChartBusiness(ChartBusiness chartBusiness) {
        this.chartBusiness = chartBusiness;
    }
}
