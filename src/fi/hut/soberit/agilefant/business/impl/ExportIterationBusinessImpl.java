package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.stereotype.Service;

import fi.hut.soberit.agilefant.business.ExportIterationBusiness;
import fi.hut.soberit.agilefant.business.IterationBurndownBusiness;
import fi.hut.soberit.agilefant.business.IterationBusiness;
import fi.hut.soberit.agilefant.business.SettingBusiness;
import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.Label;
import fi.hut.soberit.agilefant.model.StoryState;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.TaskState;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;
import fi.hut.soberit.agilefant.transfer.IterationMetrics;
import fi.hut.soberit.agilefant.transfer.IterationTO;
import fi.hut.soberit.agilefant.transfer.StoryTO;
import fi.hut.soberit.agilefant.util.StoryMetrics;

@Service("exportIterationBusiness")
public class ExportIterationBusinessImpl implements ExportIterationBusiness {

    @Autowired
    private IterationBusiness iterationBusiness;
    @Autowired
    private SettingBusiness settingBusiness;
    @Autowired
    private IterationBurndownBusiness iterationBurndownBusiness;

    private static final String STORY_POINT_COL = "B";
    private static final String EL_COL = "E";
    private static final String OE_COL = "F";
    private static final String ES_COL = "G";

    private class SheetStyles {
        public CellStyle boxedBold;
        public CellStyle boxed;
        public CellStyle whiteRow;
        public CellStyle greyRow;
    }

    public Workbook exportIteration(int iterationId) {
        Workbook wb = new HSSFWorkbook();
        IterationTO iteration = iterationBusiness
                .getIterationContents(iterationId);
        SheetStyles styles = createStyles(wb);
        createSummary(wb, styles, iteration);
        createStories(wb, styles, iteration);
        createTasks(wb, styles, iteration);
        return wb;
    }

    private SheetStyles createStyles(Workbook wb) {
        SheetStyles styles = new SheetStyles();

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

        CellStyle storyRowStyle = wb.createCellStyle();
        storyRowStyle.cloneStyleFrom(boxedStyle);
        storyRowStyle.setBorderBottom(CellStyle.BORDER_NONE);
        storyRowStyle.setBorderTop(CellStyle.BORDER_NONE);

        CellStyle storyRowStoryGrey = wb.createCellStyle();
        storyRowStoryGrey.cloneStyleFrom(storyRowStyle);
        storyRowStoryGrey.setFillForegroundColor(IndexedColors.GREY_25_PERCENT
                .getIndex());
        storyRowStoryGrey.setFillPattern(CellStyle.SOLID_FOREGROUND);

        styles.boxed = boxedStyle;
        styles.boxedBold = boxedHeader;
        styles.whiteRow = storyRowStyle;
        styles.greyRow = storyRowStoryGrey;
        return styles;
    }

    @SuppressWarnings("unchecked")
    private Sheet createStories(Workbook wb, SheetStyles styles,
            IterationTO iter) {
        Sheet storiesSheet = wb.createSheet("Stories");
        Row headerRow = storiesSheet.createRow(0);

        formatCell(headerRow.createCell(0), styles.boxedBold, "ID");
        formatCell(headerRow.createCell(1), styles.boxedBold, "Name");
        formatCell(headerRow.createCell(2), styles.boxedBold, "Points");
        formatCell(headerRow.createCell(3), styles.boxedBold, "State");
        formatCell(headerRow.createCell(4), styles.boxedBold, "Assignees");
        formatCell(headerRow.createCell(5), styles.boxedBold, "Labels");
        formatCell(headerRow.createCell(6), styles.boxedBold, "Description");
        List<StoryTO> stories = iter.getRankedStories();
        Collections.sort(stories, new PropertyComparator("rank", true, true));

        int curentRow = 1;
        for (StoryTO story : stories) {
            Row row = storiesSheet.createRow(curentRow++);
            formatCell(row.createCell(0), styles.whiteRow, story.getId());
            formatCell(row.createCell(1), styles.whiteRow, story.getName());
            formatCell(row.createCell(2), styles.whiteRow,
                    story.getStoryPoints());
            formatCell(row.createCell(3), styles.whiteRow, story.getState());
            formatCellUsers(row.createCell(4), styles.whiteRow,
                    story.getResponsibles());
            formatCellLabels(row.createCell(5), styles.whiteRow,
                    story.getLabels());
            formatCell(row.createCell(6), styles.whiteRow,
                    story.getDescription());
        }

        for (int i = 0; i < 7; i++) {
            storiesSheet.autoSizeColumn(i);
        }

        return storiesSheet;
    }

    @SuppressWarnings("unchecked")
    private Sheet createTasks(Workbook wb, SheetStyles styles, IterationTO iter) {
        Sheet tasksSheet = wb.createSheet("Tasks");
        List<Task> tasks = new ArrayList<Task>();

        // build task list in rank order
        List<StoryTO> stories = iter.getRankedStories();
        Collections.sort(stories, new PropertyComparator("rank", true, true));
        Comparator<Task> taskComparator = new PropertyComparator("rank", true,
                true);
        for (StoryTO story : stories) {
            List<Task> storyTasks = new ArrayList<Task>(story.getTasks());
            Collections.sort(storyTasks, taskComparator);
            tasks.addAll(storyTasks);
        }
        List<Task> tasksWoStory = new ArrayList<Task>(iter.getTasks());
        Collections.sort(tasksWoStory, taskComparator);
        tasks.addAll(tasksWoStory);

        Row headerRow = tasksSheet.createRow(0);

        formatCell(headerRow.createCell(0), styles.boxedBold, "ID");
        formatCell(headerRow.createCell(1), styles.boxedBold, "Name");
        formatCell(headerRow.createCell(2), styles.boxedBold, "Story");
        formatCell(headerRow.createCell(3), styles.boxedBold, "Story ID");
        formatCell(headerRow.createCell(4), styles.boxedBold, "State");
        formatCell(headerRow.createCell(5), styles.boxedBold, "Assignees");
        formatCell(headerRow.createCell(6), styles.boxedBold, "Effort Left");
        formatCell(headerRow.createCell(7), styles.boxedBold,
                "Original Estimate");
        formatCell(headerRow.createCell(8), styles.boxedBold, "Description");

        int currentRow = 1;
        for (Task task : tasks) {
            Row row = tasksSheet.createRow(currentRow++);
            formatCell(row.createCell(0), styles.whiteRow, task.getId());
            formatCell(row.createCell(1), styles.whiteRow, task.getName());
            if (task.getStory() != null) {
                formatCell(row.createCell(2), styles.whiteRow, task.getStory()
                        .getName());
                formatCell(row.createCell(3), styles.whiteRow, task.getStory()
                        .getId());
            }
            formatCell(row.createCell(4), styles.whiteRow, task.getState());
            formatCellUsers(row.createCell(5), styles.whiteRow,
                    task.getResponsibles());
            formatCell(row.createCell(6), styles.whiteRow, task.getEffortLeft());
            formatCell(row.createCell(7), styles.whiteRow,
                    task.getOriginalEstimate());
            formatCell(row.createCell(8), styles.whiteRow,
                    task.getDescription());
        }

        for (int i = 0; i < 9; i++) {
            tasksSheet.autoSizeColumn(i);
        }

        return tasksSheet;
    }

    private Sheet createSummary(Workbook wb, SheetStyles styles,
            IterationTO iter) {
        Sheet info = wb.createSheet("Summary");

        renderIterationInfo(styles, iter, info);
        Row storyHeaders = info.createRow(9);

        formatCell(storyHeaders.createCell(0), styles.boxedBold, "Story name");
        formatCell(storyHeaders.createCell(1), styles.boxedBold, "Points");
        formatCell(storyHeaders.createCell(2), styles.boxedBold, "State");
        formatCell(storyHeaders.createCell(3), styles.boxedBold, "Assignees");
        formatCell(storyHeaders.createCell(4), styles.boxedBold,
                "Effort left (h)");
        formatCell(storyHeaders.createCell(5), styles.boxedBold,
                "Original estimate (h)");
        if (settingBusiness.isHourReportingEnabled()) {
            formatCell(storyHeaders.createCell(6), styles.boxedBold,
                    "Effort Spent (h)");
        }

        CellStyle[] rowStyles = { styles.whiteRow, styles.greyRow };

        int currentRowNum = 10;
        for (StoryTO story : iter.getRankedStories()) {
            CellStyle currentStyle = rowStyles[currentRowNum % 2];
            Row currentRow = info.createRow(currentRowNum++);
            formatCell(currentRow.createCell(0), currentStyle, story.getName());
            formatCell(currentRow.createCell(1), currentStyle,
                    story.getStoryPoints());
            formatCell(currentRow.createCell(2), currentStyle, story.getState());
            formatCellUsers(currentRow.createCell(3), currentStyle,
                    story.getResponsibles());
            StoryMetrics storymetrics = story.getMetrics();
            if (storymetrics == null) {
                storymetrics = new StoryMetrics();
            }
            formatCell(currentRow.createCell(4), currentStyle,
                    storymetrics.getEffortLeft());
            formatCell(currentRow.createCell(5), currentStyle,
                    storymetrics.getOriginalEstimate());
            if (settingBusiness.isHourReportingEnabled()) {
                formatCell(currentRow.createCell(6), currentStyle,
                        storymetrics.getEffortSpent());
            }
        }

        int firstTableRow = 11;
        int lasTableRow = currentRowNum;
        Row storySums = info.createRow(currentRowNum);
        renderStorySums(styles, firstTableRow, lasTableRow, storySums);

        Row totalSums = info.createRow(lasTableRow + 2);
        renderInfoTotals(styles, iter, totalSums);

        Row tasksWoStorySums = info.createRow(lasTableRow + 1);

        int totalsRowNum = lasTableRow + 3;
        int storySumRows = lasTableRow + 1;
        renderTasksWOStory(styles, tasksWoStorySums, totalsRowNum, storySumRows);

        info.setColumnWidth(0, 256 * 50);
        for (int cell = 1; cell < 10; cell++) {
            info.autoSizeColumn(cell);
        }

        addIterationBurndown(wb, iter, info, currentRowNum);

        return info;
    }

    private void renderTasksWOStory(SheetStyles styles, Row tasksWoStorySums,
            int totalsRowNum, int storySumRows) {
        Cell tmp;
        formatCell(tasksWoStorySums.createCell(0), styles.boxedBold,
                "Tasks without story");
        formatCell(tasksWoStorySums.createCell(1), styles.boxedBold, "");
        formatCell(tasksWoStorySums.createCell(2), styles.boxedBold, "");
        formatCell(tasksWoStorySums.createCell(3), styles.boxedBold, "");
        tmp = tasksWoStorySums.createCell(4);
        tmp.setCellType(Cell.CELL_TYPE_FORMULA);
        tmp.setCellFormula(EL_COL + totalsRowNum + "- " + EL_COL + storySumRows
                + "");
        tmp.setCellStyle(styles.boxedBold);
        tmp = tasksWoStorySums.createCell(5);
        tmp.setCellType(Cell.CELL_TYPE_FORMULA);
        tmp.setCellFormula(OE_COL + totalsRowNum + "- " + OE_COL + storySumRows
                + "");
        tmp.setCellStyle(styles.boxedBold);
        if (settingBusiness.isHourReportingEnabled()) {
            tmp = tasksWoStorySums.createCell(6);
            tmp.setCellType(Cell.CELL_TYPE_FORMULA);
            tmp.setCellFormula(ES_COL + totalsRowNum + "- " + ES_COL
                    + storySumRows + "");
            tmp.setCellStyle(styles.boxedBold);
        }
    }

    private void renderInfoTotals(SheetStyles styles, IterationTO iter,
            Row totalSums) {
        formatCell(totalSums.createCell(0), styles.boxedBold, "Total");
        formatCell(totalSums.createCell(1), styles.boxedBold, "");
        formatCell(totalSums.createCell(2), styles.boxedBold, "");
        formatCell(totalSums.createCell(3), styles.boxedBold, "");

        IterationMetrics metrics = iterationBusiness.getIterationMetrics(iter);
        formatCell(totalSums.createCell(4), styles.boxedBold,
                metrics.getEffortLeft());
        formatCell(totalSums.createCell(5), styles.boxedBold,
                metrics.getOriginalEstimate());
        if (settingBusiness.isHourReportingEnabled()) {
            formatCell(totalSums.createCell(6), styles.boxedBold,
                    metrics.getSpentEffort());
        }
    }

    private void renderStorySums(SheetStyles styles, int firstTableRow,
            int lasTableRow, Row sums) {
        Cell tmp;
        formatCell(sums.createCell(0), styles.boxedBold, "Story Totals");

        tmp = sums.createCell(1);
        tmp.setCellType(Cell.CELL_TYPE_FORMULA);
        tmp.setCellFormula("SUM(" + STORY_POINT_COL + firstTableRow + ":"
                + STORY_POINT_COL + lasTableRow + ")");
        tmp.setCellStyle(styles.boxedBold);

        formatCell(sums.createCell(2), styles.boxedBold, "");
        formatCell(sums.createCell(3), styles.boxedBold, "");

        tmp = sums.createCell(4);
        tmp.setCellType(Cell.CELL_TYPE_FORMULA);
        tmp.setCellFormula("SUM(" + EL_COL + firstTableRow + ":" + EL_COL
                + lasTableRow + ")");
        tmp.setCellStyle(styles.boxedBold);

        tmp = sums.createCell(5);
        tmp.setCellType(Cell.CELL_TYPE_FORMULA);
        tmp.setCellFormula("SUM(" + OE_COL + firstTableRow + ":" + OE_COL
                + lasTableRow + ")");
        tmp.setCellStyle(styles.boxedBold);

        if (settingBusiness.isHourReportingEnabled()) {
            tmp = sums.createCell(6);
            tmp.setCellType(Cell.CELL_TYPE_FORMULA);
            tmp.setCellFormula("SUM(" + ES_COL + firstTableRow + ":" + ES_COL
                    + lasTableRow + ")");
            tmp.setCellStyle(styles.boxedBold);
        }
    }

    private void addIterationBurndown(Workbook wb, IterationTO iter,
            Sheet info, int currentRowNum) {
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
    }

    private void renderIterationInfo(SheetStyles styles, IterationTO iter,
            Sheet info) {
        Cell metadata = info.createRow(0).createCell(0);
        metadata.setCellValue("Exported at "
                + new DateTime().toString("yyyy.MM.dd HH:mm") + " by "
                + SecurityUtil.getLoggedUser().getFullName());
        Row iterationNameRow = info.createRow(2);
        Row iterationTimeframeRow = info.createRow(3);
        Row iterationAssigneesRow = info.createRow(4);
        Row iterationDescriptionRow = info.createRow(5);

        formatCell(iterationNameRow.createCell(0), styles.boxedBold, "Name");
        formatCell(iterationNameRow.createCell(1), styles.boxed, iter.getName());

        formatCell(iterationTimeframeRow.createCell(0), styles.boxedBold,
                "Timeframe");
        formatCell(iterationTimeframeRow.createCell(1), styles.boxed, iter
                .getStartDate().toString("yyyy.MM.dd HH:mm")
                + " - "
                + iter.getEndDate().toString("yyyy.MM.dd HH:mm"));

        formatCell(iterationAssigneesRow.createCell(0), styles.boxedBold,
                "Assignees");
        formatCellUsers(iterationAssigneesRow.createCell(1), styles.boxed,
                iter.getAssignees());

        formatCell(iterationDescriptionRow.createCell(0), styles.boxedBold,
                "Description");
        formatCell(iterationDescriptionRow.createCell(1), styles.boxed,
                iter.getDescription());

        // format the merged cells
        info.createRow(6).createCell(1).setCellStyle(styles.boxed);
        for (int row = 2; row < 7; row++) {
            for (int cell = 2; cell < 7; cell++) {
                info.getRow(row).createCell(cell).setCellStyle(styles.boxed);
            }
        }

        // merge cells
        info.addMergedRegion(new CellRangeAddress(2, 2, 1, 6));
        info.addMergedRegion(new CellRangeAddress(3, 3, 1, 6));
        info.addMergedRegion(new CellRangeAddress(4, 4, 1, 6));
        info.addMergedRegion(new CellRangeAddress(5, 6, 1, 6));
    }

    private static void formatCell(Cell cell, CellStyle style, StoryState value) {
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellStyle(style);
        cell.setCellValue(value.getName());
    }

    private static void formatCell(Cell cell, CellStyle style, TaskState value) {
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellStyle(style);
        cell.setCellValue(value.getName());
    }

    private static void formatCell(Cell cell, CellStyle style,
            ExactEstimate value) {
        if (value != null) {
            formatCell(cell, style, value.getMinorUnits());
        }
    }

    private static void formatCell(Cell cell, CellStyle style, Integer value) {
        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
        cell.setCellStyle(style);
        if (value != null) {
            cell.setCellValue(value);
        }
    }

    private static void formatCell(Cell cell, CellStyle style, Long value) {
        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
        cell.setCellStyle(style);
        if (value == null) {
            value = 0l;
        }
        cell.setCellValue(value / 60.0);
    }

    private static void formatCell(Cell cell, CellStyle style, String value) {
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellStyle(style);
        cell.setCellValue(value);
    }

    private static void formatCellUsers(Cell cell, CellStyle style,
            Set<User> users) {
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

    private static void formatCellLabels(Cell cell, CellStyle style,
            Set<Label> labels) {
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellStyle(style);
        if (labels != null && !labels.isEmpty()) {
            StringBuilder strb = new StringBuilder();
            for (Label label : labels) {
                strb.append(label.getDisplayName());
                strb.append(", ");
            }
            String str = strb.substring(0, strb.length() - 2).toString();
            cell.setCellValue(str);
        }
    }

}
