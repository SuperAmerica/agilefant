package fi.hut.soberit.agilefant.business.impl;

import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.hut.soberit.agilefant.business.ExportIterationBusiness;
import fi.hut.soberit.agilefant.business.IterationBurndownBusiness;
import fi.hut.soberit.agilefant.business.IterationBusiness;
import fi.hut.soberit.agilefant.business.SettingBusiness;
import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.StoryState;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.IterationMetrics;
import fi.hut.soberit.agilefant.transfer.IterationTO;
import fi.hut.soberit.agilefant.transfer.StoryTO;

@Service("exportIterationBusiness")
public class ExportIterationBusinessImpl implements ExportIterationBusiness {

    @Autowired
    private IterationBusiness iterationBusiness;
    @Autowired
    private SettingBusiness settingBusiness;
    @Autowired
    private IterationBurndownBusiness iterationBurndownBusiness;

    public Workbook exportIteration(int iterationId) {
        Workbook wb = new HSSFWorkbook();
        IterationTO iteration = iterationBusiness
                .getIterationContents(iterationId);
        createSummary(wb, iteration);
        return wb;
    }

    public Sheet createSummary(Workbook wb, IterationTO iter) {
        Sheet info = wb.createSheet("Summary");
        CellStyle boxedStyle = wb.createCellStyle();

        boxedStyle.setBorderBottom(CellStyle.BORDER_THIN);
        boxedStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());

        boxedStyle.setTopBorderColor(boxedStyle.getBottomBorderColor());
        boxedStyle.setBorderTop(boxedStyle.getBorderBottom());

        boxedStyle.setLeftBorderColor(boxedStyle.getBottomBorderColor());
        boxedStyle.setBorderLeft(boxedStyle.getBorderBottom());

        boxedStyle.setRightBorderColor(boxedStyle.getBottomBorderColor());
        boxedStyle.setBorderRight(boxedStyle.getBorderBottom());

        CellStyle boxedHeader = wb.createCellStyle();
        boxedHeader.cloneStyleFrom(boxedStyle);
        Font boldFont = wb.createFont();
        boldFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        boxedHeader.setFont(boldFont);

        Row iterationNameRow = info.createRow(2);
        Row iterationTimeframeRow = info.createRow(3);
        Row iterationAssigneesRow = info.createRow(4);
        Row iterationDescriptionRow = info.createRow(5);
        Row storyTableHeaderRow = info.createRow(9);

        formatCell(iterationNameRow.createCell(0), boxedHeader, "Name");
        formatCell(iterationNameRow.createCell(1), boxedStyle, iter.getName());

        formatCell(iterationTimeframeRow.createCell(0), boxedHeader,
                "Timeframe");
        formatCell(iterationTimeframeRow.createCell(1), boxedStyle, iter
                .getStartDate().toString("yyyy.MM.dd HH:mm")
                + " - "
                + iter.getEndDate().toString("yyyy.MM.dd HH:mm"));

        formatCell(iterationAssigneesRow.createCell(0), boxedHeader,
                "Assignees");
        formatCell(iterationAssigneesRow.createCell(1), boxedStyle,
                iter.getAssignees());

        formatCell(iterationDescriptionRow.createCell(0), boxedHeader,
                "Description");
        formatCell(iterationDescriptionRow.createCell(1), boxedStyle,
                iter.getDescription());

        // format the merged cells
        info.createRow(6).createCell(1).setCellStyle(boxedStyle);
        for (int row = 2; row < 7; row++) {
            for (int cell = 2; cell < 7; cell++) {
                info.getRow(row).createCell(cell).setCellStyle(boxedStyle);
            }
        }

        // merge cells
        info.addMergedRegion(new CellRangeAddress(2, 2, 1, 6));
        info.addMergedRegion(new CellRangeAddress(3, 3, 1, 6));
        info.addMergedRegion(new CellRangeAddress(4, 4, 1, 6));
        info.addMergedRegion(new CellRangeAddress(5, 6, 1, 6));

        formatCell(storyTableHeaderRow.createCell(0), boxedHeader, "Story name");
        formatCell(storyTableHeaderRow.createCell(1), boxedHeader, "Points");
        formatCell(storyTableHeaderRow.createCell(2), boxedHeader, "State");
        formatCell(storyTableHeaderRow.createCell(3), boxedHeader, "Assignees");
        formatCell(storyTableHeaderRow.createCell(4), boxedHeader,
                "Effort left (h)");
        formatCell(storyTableHeaderRow.createCell(5), boxedHeader,
                "Original estimate (h)");
        if (settingBusiness.isHourReportingEnabled()) {
            formatCell(storyTableHeaderRow.createCell(6), boxedHeader,
                    "Effort Spent (h)");
        }

        CellStyle storyRowStyle = wb.createCellStyle();
        storyRowStyle.cloneStyleFrom(boxedStyle);
        storyRowStyle.setBorderBottom(CellStyle.BORDER_NONE);
        storyRowStyle.setBorderTop(CellStyle.BORDER_NONE);

        CellStyle storyRowStoryGrey = wb.createCellStyle();
        storyRowStoryGrey.cloneStyleFrom(storyRowStyle);
        storyRowStoryGrey.setFillForegroundColor(IndexedColors.GREY_25_PERCENT
                .getIndex());
        storyRowStoryGrey.setFillPattern(CellStyle.SOLID_FOREGROUND);

        CellStyle[] rowStyles = { storyRowStyle, storyRowStoryGrey };

        int currentRowNum = 10;
        for (StoryTO story : iter.getRankedStories()) {
            CellStyle currentStyle = rowStyles[currentRowNum % 2];
            Row currentRow = info.createRow(currentRowNum++);
            formatCell(currentRow.createCell(0), currentStyle, story.getName());
            formatCell(currentRow.createCell(1), currentStyle,
                    story.getStoryPoints());
            formatCell(currentRow.createCell(2), currentStyle, story.getState());
            formatCell(currentRow.createCell(3), currentStyle,
                    story.getResponsibles());
            formatCell(currentRow.createCell(4), currentStyle, story
                    .getMetrics().getEffortLeft());
            formatCell(currentRow.createCell(5), currentStyle, story
                    .getMetrics().getOriginalEstimate());
            if (settingBusiness.isHourReportingEnabled()) {
                formatCell(currentRow.createCell(6), currentStyle, story
                        .getMetrics().getEffortSpent());
            }
        }

        Row sums = info.createRow(currentRowNum);
        IterationMetrics metrics = iterationBusiness.getIterationMetrics(iter);
        formatCell(sums.createCell(0), boxedHeader, "Totals");
        formatCell(sums.createCell(1), boxedHeader, metrics.getStoryPoints());
        formatCell(sums.createCell(2), boxedHeader, "");
        formatCell(sums.createCell(3), boxedHeader, "");
        formatCell(sums.createCell(4), boxedHeader, metrics.getEffortLeft());
        formatCell(sums.createCell(5), boxedHeader,
                metrics.getOriginalEstimate());
        if (settingBusiness.isHourReportingEnabled()) {
            formatCell(sums.createCell(6), boxedHeader,
                    metrics.getSpentEffort());
        }

        info.setColumnWidth(0, 256 * 50);
        for (int cell = 1; cell < 10; cell++) {
            info.autoSizeColumn(cell);
        }

        int burndownPicId = wb.addPicture(
                iterationBurndownBusiness.getIterationBurndown(iter),
                Workbook.PICTURE_TYPE_PNG);

        Drawing dwr = info.createDrawingPatriarch();
        ClientAnchor cAnch = wb.getCreationHelper().createClientAnchor();
        cAnch.setRow1(currentRowNum + 4);
        cAnch.setCol1(0);
        cAnch.setCol2(6);
        Picture pict = dwr.createPicture(cAnch, burndownPicId);
        pict.resize();

        return info;
    }

    private void formatCell(Cell cell, CellStyle style, StoryState value) {
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellStyle(style);
        cell.setCellValue(value.getName());
    }

    private void formatCell(Cell cell, CellStyle style, ExactEstimate value) {
        if (value != null) {
            formatCell(cell, style, value.getMinorUnits());
        }
    }

    private void formatCell(Cell cell, CellStyle style, Integer value) {
        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
        cell.setCellStyle(style);
        if (value != null) {
            cell.setCellValue(value);
        }
    }

    private void formatCell(Cell cell, CellStyle style, long value) {
        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
        cell.setCellStyle(style);
        cell.setCellValue(value / 60.0);
    }

    private void formatCell(Cell cell, CellStyle style, String value) {
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellStyle(style);
        cell.setCellValue(value);
    }

    private void formatCell(Cell cell, CellStyle style, Set<User> users) {
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellStyle(style);
        if (users == null || users.isEmpty()) {

        } else {
            StringBuilder strb = new StringBuilder();
            for (User user : users) {
                strb.append(user.getFullName());
                strb.append(", ");
            }
            String str = strb.substring(0, strb.length() - 2).toString();
            cell.setCellValue(str);
        }
    }

}
