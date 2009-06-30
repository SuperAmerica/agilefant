package fi.hut.soberit.agilefant.business;

import java.util.Set;

import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.joda.time.DateTime;

import com.opensymphony.xwork2.TextProvider;


public interface TimesheetExportBusiness {
    public static final String[] COLUMN_NAMES = { "timesheet.product",
            "timesheet.project", "timesheet.iteration", "timesheet.story",
            "timesheet.task", "timesheet.description", "timesheet.user",
            "timesheet.date", "timesheet.effort" };
    public static final int[] MAX_COLUMN_SIZE = { 0, 0, 0, 256 * 55, 256 * 55,
            256 * 55, 0, 256 * 15, 0 };
    public static final int PRODUCT_COLUMN_NUM = 0;
    public static final int PROJECT_COLUMN_NUM = 1;
    public static final int ITERATION_COLUMN_NUM = 2;
    public static final int STORY_COLUMN_NUM = 3;
    public static final int TASK_COLUMN_NUM = 4;
    public static final int DESCRIPTION_COLUMN_NUM = 5;
    public static final int USER_COLUMN_NUM = 6;
    public static final int DATE_COLUMN_NUM = 7;
    public static final int EFFORT_COLUMN_NUM = 8;
    public static final String DATE_FORMAT = "dd.mm.yyyy hh:mm";
    public static final String EFFORT_FORMAT = "#,##0.00";
    public static final short HEADER_FONT_WEIGHT = Font.BOLDWEIGHT_BOLD;
    

    public Workbook generateTimesheet(TextProvider textProvider,
            Set<Integer> backlogIds, DateTime startDate, DateTime endDate,
            Set<Integer> userIds);

}
