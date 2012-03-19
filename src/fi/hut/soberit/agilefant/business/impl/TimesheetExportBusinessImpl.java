package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opensymphony.xwork2.TextProvider;

import fi.hut.soberit.agilefant.business.TimesheetExportBusiness;
import fi.hut.soberit.agilefant.db.HourEntryDAO;
import fi.hut.soberit.agilefant.model.BacklogHourEntry;
import fi.hut.soberit.agilefant.model.StoryHourEntry;
import fi.hut.soberit.agilefant.model.TaskHourEntry;
import fi.hut.soberit.agilefant.util.TimesheetExportRowData;
import fi.hut.soberit.agilefant.util.TimesheetExportRowDataComparator;

@Service("timesheetExportBusiness")
@Transactional
public class TimesheetExportBusinessImpl implements TimesheetExportBusiness {

    private CellStyle dateColumnStyle;
    private CellStyle decimalFormat; 
    private CellStyle headerStyle;
    @Autowired
    private HourEntryDAO hourEntryDAO;

    public Workbook generateTimesheet(TextProvider textProvider,
            Set<Integer> backlogIds, DateTime startDate, DateTime endDate,
            Set<Integer> userIds) {
        Workbook workbook = new HSSFWorkbook();
        List<TimesheetExportRowData> reportData = this.getTimesheetRows(
                backlogIds, startDate, endDate, userIds);
        Sheet plainReport = workbook.createSheet("Agilefant Timesheet");
        this.initializeColumnStyles(workbook);
        this.renderHeader(plainReport, textProvider);
        this.renderSheetData(plainReport, reportData);
        this.sizeColumns(plainReport);
        return workbook;
    }

    public List<TimesheetExportRowData> getTimesheetRows(
            Set<Integer> backlogIds, DateTime startDate, DateTime endDate,
            Set<Integer> userIds) {
        List<BacklogHourEntry> backlogEntries = this.hourEntryDAO
                .getBacklogHourEntriesByFilter(backlogIds, startDate, endDate,
                        userIds);
        List<StoryHourEntry> storyEntries = this.hourEntryDAO
                .getStoryHourEntriesByFilter(backlogIds, startDate, endDate,
                        userIds);
        List<TaskHourEntry> taskEntries = this.hourEntryDAO
                .getTaskHourEntriesByFilter(backlogIds, startDate, endDate,
                        userIds);
        List<TimesheetExportRowData> timesheetData = new ArrayList<TimesheetExportRowData>();

        for (BacklogHourEntry entry : backlogEntries) {
            timesheetData.add(new TimesheetExportRowData(entry));
        }

        for (StoryHourEntry entry : storyEntries) {
            timesheetData.add(new TimesheetExportRowData(entry));
        }

        for (TaskHourEntry entry : taskEntries) {
            timesheetData.add(new TimesheetExportRowData(entry));
        }
        Collections.sort(timesheetData, new TimesheetExportRowDataComparator());
        return timesheetData;
    }

    protected void renderSheetData(Sheet sheet,
            List<TimesheetExportRowData> dataRows) {
        for (TimesheetExportRowData rowData : dataRows) {
            Row currentRow = sheet.createRow(sheet.getLastRowNum() + 1);
            this.setRowValues(currentRow, rowData);
        }
    }

    protected Cell createCellWithValue(Row row, String value, int cellNo) {
        Cell cell = row.createCell(cellNo);
        cell.setCellValue(value);
        return cell;
    }

    protected void renderHeader(Sheet sheet, TextProvider textProvider) {
        Row headerRow = sheet.createRow(0);
        for (int columnNumber = 0; columnNumber < COLUMN_NAMES.length; columnNumber++) {
            String columnHeader = textProvider
                    .getText(COLUMN_NAMES[columnNumber]);
            Cell currentCell = createCellWithValue(headerRow, columnHeader, columnNumber);
            currentCell.setCellStyle(this.headerStyle);
        }
    }

    protected void initializeColumnStyles(Workbook wb) {
        this.dateColumnStyle = wb.createCellStyle();
        CreationHelper cHelper = wb.getCreationHelper();
        this.dateColumnStyle.setDataFormat(cHelper
                .createDataFormat().getFormat(DATE_FORMAT));
        this.decimalFormat = wb.createCellStyle();
        this.decimalFormat.setDataFormat(cHelper.
                createDataFormat().getFormat(EFFORT_FORMAT));
        this.headerStyle = wb.createCellStyle();
        Font headerFont = wb.createFont();
        headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        this.headerStyle.setFont(headerFont);
    }

    protected void sizeColumns(Sheet sheet) {
        for (int columnNum = 0; columnNum < MAX_COLUMN_SIZE.length; columnNum++) {
            sheet.autoSizeColumn(columnNum);
            int columnWidth = sheet.getColumnWidth(columnNum);
            // if column has max size and column width exceeds that, resize the
            // column
            if (MAX_COLUMN_SIZE[columnNum] > 0
                    && columnWidth > MAX_COLUMN_SIZE[columnNum]) {
                sheet.setColumnWidth(columnNum, MAX_COLUMN_SIZE[columnNum]);
            }
        }
    }

    protected void setRowValues(Row row, TimesheetExportRowData rowData) {
        String productName = "";
        if (rowData.getProduct() != null)
            productName = rowData.getProduct().getName();
        
        createCellWithValue(row, productName,
                PRODUCT_COLUMN_NUM);
        if (rowData.getProject() != null) {
            createCellWithValue(row, rowData.getProject().getName(),
                    PROJECT_COLUMN_NUM);
        }
        if (rowData.getIteration() != null) {
            createCellWithValue(row, rowData.getIteration().getName(),
                    ITERATION_COLUMN_NUM);
        }
        if (rowData.getStory() != null) {
            createCellWithValue(row, rowData.getStory().getName(),
                    STORY_COLUMN_NUM);
        }
        if (rowData.getTask() != null) {
            createCellWithValue(row, rowData.getTask().getName(),
                    TASK_COLUMN_NUM);
        }
        createCellWithValue(row, rowData.getDescription(),
                DESCRIPTION_COLUMN_NUM);
        createCellWithValue(row, rowData.getUser().getFullName(),
                USER_COLUMN_NUM);
        Cell dateColumn = row.createCell(DATE_COLUMN_NUM);
        dateColumn.setCellStyle(this.dateColumnStyle);
        dateColumn.setCellValue(rowData.getDate().toDate());
        Cell effortColumn = row.createCell(EFFORT_COLUMN_NUM);
        effortColumn.setCellType(Cell.CELL_TYPE_NUMERIC);
        effortColumn.setCellValue((double)rowData.getEffort()/60.0);
        effortColumn.setCellStyle(this.decimalFormat);
    }

    public void setHourEntryDAO(HourEntryDAO hourEntryDAO) {
        this.hourEntryDAO = hourEntryDAO;
    }

}
