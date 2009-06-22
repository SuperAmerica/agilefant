package fi.hut.soberit.agilefant.business;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.opensymphony.xwork.TextProvider;

import fi.hut.soberit.agilefant.business.impl.TimesheetExportBusinessImpl;
import fi.hut.soberit.agilefant.db.HourEntryDAO;
import fi.hut.soberit.agilefant.model.BacklogHourEntry;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryHourEntry;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.TaskHourEntry;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.TimesheetExportRowData;
import static org.easymock.EasyMock.*;

public class TimesheetExportBusinessTest extends TimesheetExportBusinessImpl {

    private Product product;
    private Project project;
    private Iteration iteration;
    private Story story;
    private Task storyTask;
    private Task iterationTask;

    private User user;

    private BacklogHourEntry productEntry;
    private TaskHourEntry taskEntry;
    private StoryHourEntry storyEntry;

    private static DateTime date = new DateTime(2009, 6, 10, 12, 30, 0, 0);

    @Before
    public void setUp() {
        product = new Product();
        project = new Project();
        iteration = new Iteration();
        story = new Story();
        iterationTask = new Task();
        storyTask = new Task();

        product.setName("product 1");
        project.setName("project 1");
        iteration.setName("iteration 1");
        story.setName("story 1");
        storyTask.setName("task 1");
        iterationTask.setName("task 2");

        project.setParent(product);
        iteration.setParent(project);
        story.setBacklog(iteration);
        iterationTask.setIteration(iteration);
        storyTask.setStory(story);

        user = new User();
        user.setFullName("User 1");

        productEntry = new BacklogHourEntry();
        productEntry.setBacklog(product);
        productEntry.setDescription("foofaa");
        productEntry.setUser(user);
        productEntry.setMinutesSpent(60);
        productEntry.setDate(date.toDateTime());

        taskEntry = new TaskHourEntry();
        taskEntry.setTask(storyTask);
        taskEntry.setDescription("foo");
        taskEntry.setUser(user);
        taskEntry.setDate(date.toDateTime());
        taskEntry.setMinutesSpent(60);

        storyEntry = new StoryHourEntry();
        storyEntry.setStory(story);
    }

    @Test
    public void testGetTimesheetRows_noData() {
        HourEntryDAO heDAO = createMock(HourEntryDAO.class);
        List<BacklogHourEntry> backlogEntries = Collections.emptyList();
        List<StoryHourEntry> storyEntries = Collections.emptyList();
        List<TaskHourEntry> taskEntries = Collections.emptyList();
        expect(heDAO.getBacklogHourEntriesByFilter(null, null, null, null))
                .andReturn(backlogEntries);
        expect(heDAO.getStoryHourEntriesByFilter(null, null, null, null))
                .andReturn(storyEntries);
        expect(heDAO.getTaskHourEntriesByFilter(null, null, null, null))
                .andReturn(taskEntries);
        this.setHourEntryDAO(heDAO);
        replay(heDAO);
        List<TimesheetExportRowData> actual = super.getTimesheetRows(null,
                null, null, null);
        assertEquals(0, actual.size());
        verify(heDAO);
    }

    @Test
    public void testGetTimesheetRows() {
        Set<Integer> backlogIds = new HashSet<Integer>(Arrays.asList(1, 2, 3));
        Set<Integer> userIds = new HashSet<Integer>(Arrays.asList(1, 2));
        DateTime startTime = new DateTime(2009, 1, 1, 0, 0, 0, 0);
        DateTime endtTime = new DateTime(2009, 6, 1, 0, 0, 0, 0);
        HourEntryDAO heDAO = createMock(HourEntryDAO.class);

        List<BacklogHourEntry> backlogEntries = Arrays.asList(productEntry);
        List<StoryHourEntry> storyEntries = Arrays.asList(storyEntry);
        List<TaskHourEntry> taskEntries = Arrays.asList(taskEntry);

        expect(
                heDAO.getBacklogHourEntriesByFilter(backlogIds, startTime,
                        endtTime, userIds)).andReturn(backlogEntries);
        expect(
                heDAO.getStoryHourEntriesByFilter(backlogIds, startTime,
                        endtTime, userIds)).andReturn(storyEntries);
        expect(
                heDAO.getTaskHourEntriesByFilter(backlogIds, startTime,
                        endtTime, userIds)).andReturn(taskEntries);

        this.setHourEntryDAO(heDAO);

        replay(heDAO);
        List<TimesheetExportRowData> actual = super.getTimesheetRows(
                backlogIds, startTime, endtTime, userIds);
        assertEquals(3, actual.size());
        verify(heDAO);

    }

    private TextProvider mockTextProvider() {
        TextProvider textProvider = createMock(TextProvider.class);
        expect(textProvider.getText(TimesheetExportBusiness.COLUMN_NAMES[0]))
                .andReturn("PROD COL");
        expect(textProvider.getText(TimesheetExportBusiness.COLUMN_NAMES[1]))
                .andReturn("PROJ COL");
        expect(textProvider.getText(TimesheetExportBusiness.COLUMN_NAMES[2]))
                .andReturn("ITER COL");
        expect(textProvider.getText(TimesheetExportBusiness.COLUMN_NAMES[3]))
                .andReturn("STORY COL");
        expect(textProvider.getText(TimesheetExportBusiness.COLUMN_NAMES[4]))
                .andReturn("TASK COL");
        expect(textProvider.getText(TimesheetExportBusiness.COLUMN_NAMES[5]))
                .andReturn("DESC COL");
        expect(textProvider.getText(TimesheetExportBusiness.COLUMN_NAMES[6]))
                .andReturn("USER COL");
        expect(textProvider.getText(TimesheetExportBusiness.COLUMN_NAMES[7]))
                .andReturn("DATE COL");
        expect(textProvider.getText(TimesheetExportBusiness.COLUMN_NAMES[8]))
                .andReturn("EFF COL");
        return textProvider;
    }

    @Test
    public void testRenderHeader() {
        TextProvider textProvider = mockTextProvider();
        Sheet sheet = createMock(Sheet.class);
        Row row = createMock(Row.class);

        Cell productCell = createMock(Cell.class);
        productCell.setCellValue("PROD COL");
        productCell.setCellStyle(null);
        Cell projectCell = createMock(Cell.class);
        projectCell.setCellValue("PROJ COL");
        projectCell.setCellStyle(null);
        Cell iterationCell = createMock(Cell.class);
        iterationCell.setCellValue("ITER COL");
        iterationCell.setCellStyle(null);
        Cell storyCell = createMock(Cell.class);
        storyCell.setCellValue("STORY COL");
        storyCell.setCellStyle(null);
        Cell taskCell = createMock(Cell.class);
        taskCell.setCellValue("TASK COL");
        taskCell.setCellStyle(null);
        Cell descriptionCell = createMock(Cell.class);
        descriptionCell.setCellValue("DESC COL");
        descriptionCell.setCellStyle(null);
        Cell userCell = createMock(Cell.class);
        userCell.setCellValue("USER COL");
        userCell.setCellStyle(null);
        Cell dateCell = createMock(Cell.class);
        dateCell.setCellValue("DATE COL");
        dateCell.setCellStyle(null);
        Cell effortCell = createMock(Cell.class);
        effortCell.setCellValue("EFF COL");
        effortCell.setCellStyle(null);

        expect(sheet.createRow(0)).andReturn(row);
        expect(row.createCell(0)).andReturn(productCell);
        expect(row.createCell(1)).andReturn(projectCell);
        expect(row.createCell(2)).andReturn(iterationCell);
        expect(row.createCell(3)).andReturn(storyCell);
        expect(row.createCell(4)).andReturn(taskCell);
        expect(row.createCell(5)).andReturn(descriptionCell);
        expect(row.createCell(6)).andReturn(userCell);
        expect(row.createCell(7)).andReturn(dateCell);
        expect(row.createCell(8)).andReturn(effortCell);

        replay(textProvider, sheet, row, productCell, projectCell,
                iterationCell, storyCell, taskCell, descriptionCell, userCell,
                dateCell, effortCell);
        super.renderHeader(sheet, textProvider);
        verify(textProvider, sheet, row, productCell, projectCell,
                iterationCell, storyCell, taskCell, descriptionCell, userCell,
                dateCell, effortCell);

    }

    @Test
    public void testInitializeColumnStyles() {
        Workbook wb = createMock(Workbook.class);
        CreationHelper helper = createMock(CreationHelper.class);
        expect(wb.getCreationHelper()).andReturn(helper);

        //date format
        CellStyle style = createMock(CellStyle.class);
        expect(wb.createCellStyle()).andReturn(style);

        DataFormat format = createMock(DataFormat.class);
        expect(format.getFormat(DATE_FORMAT)).andReturn((Short) (short) 0);
        expect(helper.createDataFormat()).andReturn(format);
        style.setDataFormat((Short) (short) 0);

        //effort format
        CellStyle numStyle = createMock(CellStyle.class);
        expect(wb.createCellStyle()).andReturn(numStyle);
        DataFormat numFormat = createMock(DataFormat.class);
        expect(numFormat.getFormat(EFFORT_FORMAT)).andReturn((Short)(short)1);
        expect(helper.createDataFormat()).andReturn(numFormat);
        numStyle.setDataFormat((Short)(short)1);
        
        //header style
        CellStyle headerStyle = createMock(CellStyle.class);
        expect(wb.createCellStyle()).andReturn(headerStyle);
        Font headerFont = createMock(Font.class);
        expect(wb.createFont()).andReturn(headerFont);
        headerFont.setBoldweight(HEADER_FONT_WEIGHT);
        headerStyle.setFont(headerFont);
        
        replay(wb, style, helper, format, numStyle, numFormat, headerStyle, headerFont);
        super.initializeColumnStyles(wb);

        verify(wb, style, helper, format, numStyle, numFormat, headerStyle, headerFont);
    }

    @Test
    public void testSizeColumns() {
        Sheet sheet = createMock(Sheet.class);
        // auto size calls
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);
        sheet.autoSizeColumn(7);
        sheet.autoSizeColumn(8);
        // get size after auto size
        expect(sheet.getColumnWidth(0)).andReturn(10);
        expect(sheet.getColumnWidth(1)).andReturn(60 * 256); // no limit
        expect(sheet.getColumnWidth(2)).andReturn(10);
        expect(sheet.getColumnWidth(3)).andReturn(70 * 256); // too wide
        expect(sheet.getColumnWidth(4)).andReturn(10);
        expect(sheet.getColumnWidth(5)).andReturn(10);
        expect(sheet.getColumnWidth(6)).andReturn(10);
        expect(sheet.getColumnWidth(7)).andReturn(10);
        expect(sheet.getColumnWidth(8)).andReturn(10);

        sheet.setColumnWidth(3, 256 * 55);
        replay(sheet);
        super.sizeColumns(sheet);
        verify(sheet);

    }

    /*
     * Test setRowValues with minimum fields (effort logged directly to a
     * project)
     */
    @Test
    public void testSetRowValue_productEntry() {
        Row row = createMock(Row.class);
        TimesheetExportRowData data = new TimesheetExportRowData(productEntry);

        Cell productCell = createMock(Cell.class);
        expect(row.createCell(PRODUCT_COLUMN_NUM)).andReturn(productCell);
        productCell.setCellValue(product.getName());

        Cell descCell = createMock(Cell.class);
        expect(row.createCell(DESCRIPTION_COLUMN_NUM)).andReturn(descCell);
        descCell.setCellValue(productEntry.getDescription());

        Cell userCell = createMock(Cell.class);
        expect(row.createCell(USER_COLUMN_NUM)).andReturn(userCell);
        userCell.setCellValue(user.getFullName());

        Cell dateCell = createMock(Cell.class);
        expect(row.createCell(DATE_COLUMN_NUM)).andReturn(dateCell);
        dateCell.setCellStyle(null);
        dateCell.setCellValue(date.toDate());

        Cell effortCell = createMock(Cell.class); 
        expect(row.createCell(EFFORT_COLUMN_NUM)).andReturn(effortCell);
        effortCell.setCellValue(1.0);
        effortCell.setCellType(Cell.CELL_TYPE_NUMERIC);
        effortCell.setCellStyle(null);

        replay(productCell, descCell, userCell, dateCell, effortCell, row);
        super.setRowValues(row, data);
        verify(productCell, descCell, userCell, dateCell, effortCell, row);

    }

    /*
     * Test setRowValues with all possible fields (effort logged to a task
     * located in an iteration-level story.
     */
    @Test
    public void testSetRowValue_storyTaskEntry() {
        Row row = createMock(Row.class);
        TimesheetExportRowData data = new TimesheetExportRowData(taskEntry);

        Cell productCell = createMock(Cell.class);
        expect(row.createCell(PRODUCT_COLUMN_NUM)).andReturn(productCell);
        productCell.setCellValue(product.getName());

        Cell projectCell = createMock(Cell.class);
        expect(row.createCell(PROJECT_COLUMN_NUM)).andReturn(projectCell);
        projectCell.setCellValue(project.getName());

        Cell iterationCell = createMock(Cell.class);
        expect(row.createCell(ITERATION_COLUMN_NUM)).andReturn(iterationCell);
        iterationCell.setCellValue(iteration.getName());

        Cell storyCell = createMock(Cell.class);
        expect(row.createCell(STORY_COLUMN_NUM)).andReturn(storyCell);
        storyCell.setCellValue(story.getName());

        Cell taskCell = createMock(Cell.class);
        expect(row.createCell(TASK_COLUMN_NUM)).andReturn(taskCell);
        taskCell.setCellValue(storyTask.getName());

        Cell descCell = createMock(Cell.class);
        expect(row.createCell(DESCRIPTION_COLUMN_NUM)).andReturn(descCell);
        descCell.setCellValue(taskEntry.getDescription());

        Cell userCell = createMock(Cell.class);
        expect(row.createCell(USER_COLUMN_NUM)).andReturn(userCell);
        userCell.setCellValue(user.getFullName());

        Cell dateCell = createMock(Cell.class);
        expect(row.createCell(DATE_COLUMN_NUM)).andReturn(dateCell);
        dateCell.setCellStyle(null);
        dateCell.setCellValue(date.toDate());

        Cell effortCell = createMock(Cell.class);
        expect(row.createCell(EFFORT_COLUMN_NUM)).andReturn(effortCell);
        effortCell.setCellValue(1.0);
        effortCell.setCellType(Cell.CELL_TYPE_NUMERIC);
        effortCell.setCellStyle(null);

        replay(productCell, projectCell, iterationCell, storyCell, taskCell,
                descCell, userCell, dateCell, effortCell, row);
        super.setRowValues(row, data);
        verify(productCell, projectCell, iterationCell, storyCell, taskCell,
                descCell, userCell, dateCell, effortCell, row);
    }

}
