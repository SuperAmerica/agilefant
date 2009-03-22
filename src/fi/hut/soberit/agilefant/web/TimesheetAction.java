package fi.hut.soberit.agilefant.web;



import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.providers.rememberme.RememberMeAuthenticationToken;

import com.opensymphony.webwork.interceptor.PrincipalAware;
import com.opensymphony.webwork.interceptor.PrincipalProxy;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.TimesheetBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.AgilefantUserDetails;
import fi.hut.soberit.agilefant.util.BacklogItemTimesheetNode;
import fi.hut.soberit.agilefant.util.BacklogTimesheetNode;
import flexjson.JSONSerializer;

/**
 * 
 * @author Vesa Pirila / Spider
 * @author Pasi Pekkanen
 *
 */
public class TimesheetAction extends ActionSupport implements PrincipalAware {

    private static final long serialVersionUID = -8988740967426943267L;
    
    private TimesheetBusiness timesheetBusiness;
    
    private UserBusiness userBusiness;

    private Set<Integer> productIds = new HashSet<Integer>();
    
    private Set<Integer> projectIds = new HashSet<Integer>();
    
    private Set<Integer> iterationIds = new HashSet<Integer>();
    
    private List<BacklogTimesheetNode> products;
    
    private List<Integer> selected = new ArrayList<Integer>();
    
    private List<User> selUser = new ArrayList<User>();
    
    private UserDAO userDAO;

    private int[] backlogIds;

    private String startDate;

    private String endDate;
    
    private String interval;
    
    private Set<Integer> userIds = new HashSet<Integer>();
    
    private int backlogSelectionType = 0;
    
    private boolean onlyOngoing = false;
    
    private AFTime totalSpentTime;

    private int currentUserId = 0;
    
    private ByteArrayOutputStream excelData;
        
    private CellStyle dateStyle;
    
    public int[] getBacklogIds() {
        return backlogIds;
    }

    public void setBacklogIds(int[] backlogIds) {
        this.backlogIds = backlogIds;
    }
    
    /**
     * Needed for xwork's execAndWait as action is executed in a different
     * thread than the wait page. Thus no static threadLocal based principals
     * (such as those in SecurityUtil) can be used.
     */
    public void setPrincipalProxy(PrincipalProxy principalProxy) {
        Principal principal = principalProxy.getUserPrincipal();
        AgilefantUserDetails ud;
        if (principal instanceof RememberMeAuthenticationToken) {
            ud = (AgilefantUserDetails) ((RememberMeAuthenticationToken) principal)
                    .getPrincipal();
        } else {
            ud = (AgilefantUserDetails) ((UsernamePasswordAuthenticationToken) principal)
                    .getPrincipal();
        }
        currentUserId = ud.getUserId();
        
    }
    
    private List<Integer> selectedBacklogs() {
        List<Integer> ret = new ArrayList<Integer>();
        if(this.projectIds.contains(-1)) {
            if(this.onlyOngoing) {
                ret.addAll(this.projectIds);
            } else {
                ret.addAll(this.productIds);
            }
        } else if(this.iterationIds.contains(-1)) {
             if(this.onlyOngoing) {
                ret.addAll(this.iterationIds);
            } else {
                ret.addAll(this.projectIds);
            }
        } else {
            if(this.projectIds.size() == 0) {
                ret.addAll(this.productIds);
            } else if(this.iterationIds.size() == 0) {
                ret.addAll(this.projectIds);
            } else {
                ret.addAll(this.iterationIds);
            }
        }
        return ret;
    }
    public String initialize() {
        this.interval = "TODAY";
        this.onlyOngoing = true;
        this.userIds.add(this.currentUserId);
        return Action.SUCCESS;
    }
    public String generateTree(){
        List<Integer> ids = null;
        Set<Integer> users = new HashSet<Integer>();
        if(backlogSelectionType == 0) {
            ids = this.selectedBacklogs();
            users.addAll(userIds);
        } else {
            Date start = null;
            Date end = null;
            try {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                start = df.parse(this.startDate);
                end = df.parse(this.endDate);
            } catch(Exception e) {
                start = null;
                end = null;
            }
            Collection<Backlog> tmp = userBusiness.getOngoingBacklogsByUserAndInterval(currentUserId, start, end);
            ids = new ArrayList<Integer>();
            for(Backlog bl : tmp) {
                ids.add(bl.getId());
            }
            //only for current user:
            users.add(currentUserId);
        }
        if(ids == null || ids.size() == 0) {
            addActionError("No backlogs selected.");
            return Action.ERROR;
        }
        try{
            products = timesheetBusiness.generateTree(ids, startDate, endDate, users);
            totalSpentTime = timesheetBusiness.calculateRootSum(products);
        }catch(IllegalArgumentException e){
            addActionError(e.getMessage());
            return Action.ERROR;
        }
        return Action.SUCCESS;
    }

    public String generateExcel() {
        
        Workbook wb = new HSSFWorkbook();
        Sheet effort = wb.createSheet("Agilefant timesheet");
        if(generateTree().equals(Action.ERROR)) {
            return Action.ERROR;
        }
        CellStyle boldStyle = wb.createCellStyle();
        Font boldFont = wb.createFont();
        boldStyle.setFont(boldFont);
        boldFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        dateStyle = wb.createCellStyle();
        dateStyle.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("m.d.yy h:mm"));
        Row head = effort.createRow(0);
        Cell tmp;
        tmp = head.createCell(0);
        tmp.setCellValue("Product");
        tmp.setCellStyle(boldStyle);
        tmp = head.createCell(1); 
        tmp.setCellValue("Project");
        tmp.setCellStyle(boldStyle);
        tmp = head.createCell(2);
        tmp.setCellValue("Iteration");
        tmp.setCellStyle(boldStyle);
        tmp = head.createCell(3);
        tmp.setCellValue("Iteration goal");
        tmp.setCellStyle(boldStyle);
        tmp = head.createCell(4);
        tmp.setCellValue("Backlog item");
        tmp.setCellStyle(boldStyle);
        tmp = head.createCell(5);
        tmp.setCellValue("Comment");
        tmp.setCellStyle(boldStyle);
        tmp = head.createCell(6);
        tmp.setCellValue("User");
        tmp.setCellStyle(boldStyle);
        tmp = head.createCell(7);
        tmp.setCellValue("Date");
        tmp.setCellStyle(boldStyle);
        tmp = head.createCell(8);
        tmp.setCellValue("Spent effort (hours)");
        tmp.setCellStyle(boldStyle);
        
        //effort.createFreezePane(0, 1, 0, 1);
        
        generateExcelNode(products, effort);
        
        effort.autoSizeColumn(0);
        effort.autoSizeColumn(1);
        effort.autoSizeColumn(2);
        //try to autosize, but limit size to 55 characters
        effort.autoSizeColumn(3);
        if(effort.getColumnWidth(3) > 55*256) {
            effort.setColumnWidth(3, 55*256);
        }
        effort.autoSizeColumn(4);
        if(effort.getColumnWidth(4) > 55*256) {
            effort.setColumnWidth(4, 55*256);
        }
        effort.autoSizeColumn(5);
        if(effort.getColumnWidth(5) > 55*256) {
            effort.setColumnWidth(5, 55*256);
        }
        effort.autoSizeColumn(6);
        effort.autoSizeColumn(8);
        effort.autoSizeColumn(9);
        
        try {
            excelData = new ByteArrayOutputStream();
            wb.write(excelData);
        } catch (IOException e) {
            return Action.ERROR;
        }
        return Action.SUCCESS;
    }
    
    private void generateExcelNode(List<BacklogTimesheetNode> bls, Sheet effSheet) {
        for(BacklogTimesheetNode rnode : bls) {
            if(rnode.getChildBacklogs() != null) {
                generateExcelNode(rnode.getChildBacklogs(), effSheet);
            }
            if(rnode.getHourEntries() != null) {
                for(HourEntry entry : rnode.getHourEntries()) {
                    Row row = effSheet.createRow(effSheet.getLastRowNum()+1);
                    addExcelRow(row, entry, rnode.getBacklog(), null);
                    
                }
            }
            if(rnode.getChildBacklogItems() != null) {
                for(BacklogItemTimesheetNode bnode : rnode.getChildBacklogItems()) {
                    for(HourEntry bentry : bnode.getHourEntries()) {
                        Row row = effSheet.createRow(effSheet.getLastRowNum()+1);
                        addExcelRow(row, bentry, rnode.getBacklog(), bnode.getBacklogItem());
                    }
                }
            }
        }
        
    }
    
    private void addExcelRow(Row row, HourEntry entry, Backlog bl, BacklogItem bli) {
        Cell prod = row.createCell(0);
        Cell proj = row.createCell(1);
        Cell iter = row.createCell(2);
        Cell goal = row.createCell(3);
        Cell blic = row.createCell(4);
        Cell desc = row.createCell(5);
        Cell user = row.createCell(6);
        Cell time = row.createCell(7);
        Cell eff = row.createCell(8);
        
        if(bl instanceof Iteration) {
            Iteration ite = (Iteration)bl;
            prod.setCellValue(ite.getProject().getProduct().getName());
            proj.setCellValue(ite.getProject().getName());
            iter.setCellValue(ite.getName());
        } else if(bl instanceof Project) {
            Project prj = (Project)bl;
            proj.setCellValue(prj.getName());
            prod.setCellValue(prj.getProduct().getName());
        } else {
            prod.setCellValue(bl.getName());
        }
        if(bli != null) {
            blic.setCellValue(bli.getName());
            if(bli.getIterationGoal() != null) {
                goal.setCellValue(bli.getIterationGoal().getName());
            }
        }
        if(entry.getUser() != null) {
            user.setCellValue(entry.getUser().getFullName());
        }
        if(entry.getDate() != null) {
            time.setCellValue(entry.getDate());
            time.setCellStyle(dateStyle);
        }
        if(entry.getDescription() != null) {
            desc.setCellValue(entry.getDescription());
        }
        if(entry.getTimeSpent() != null) {
            eff.setCellValue(((double)Math.round(entry.getTimeSpent().toDouble()*100))/100);
            eff.setCellType(Cell.CELL_TYPE_NUMERIC);
        }
    }
    public TimesheetBusiness getTimesheetBusiness() {
        return timesheetBusiness;
    }

    public List<BacklogTimesheetNode> getProducts() {
        return products;
    }

    /**
     * This should not be used anywhere
     * @param products
     */
    public void setProducts(List<BacklogTimesheetNode> products) {
        this.products = products;
    }

    public void setTimesheetBusiness(TimesheetBusiness timesheetBusiness) {
        this.timesheetBusiness = timesheetBusiness;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public List<Integer> getSelected() {
        this.selected.clear();
        for(int sel : backlogIds) {
            this.selected.add(sel);
        }
        return selected;
    }

    public List<User> getSelUser() {
        this.selUser.clear();
        for(int sel: userIds) {
            this.selUser.add(userDAO.get(sel));
        }
        return selUser;
    }

    public AFTime getTotalSpentTime() {
        return totalSpentTime;
    }

    public void setTotalSpentTime(AFTime totalSpentTime) {
        this.totalSpentTime = totalSpentTime;
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public Set<Integer> getProductIds() {
        return productIds;
    }

    public void setProductIds(Set<Integer> productIds) {
        this.productIds = productIds;
    }

    public Set<Integer> getProjectIds() {
        return projectIds;
    }

    public void setProjectIds(Set<Integer> projectIds) {
        this.projectIds = projectIds;
    }

    public Set<Integer> getIterationIds() {
        return iterationIds;
    }

    public void setIterationIds(Set<Integer> iterationIds) {
        this.iterationIds = iterationIds;
    }
    
    public String getJSONProducts() {
        return new JSONSerializer().serialize(this.productIds);
    }
    public String getJSONProjects() {
        return new JSONSerializer().serialize(this.projectIds);
    }
    public String getJSONIterations() {
        return new JSONSerializer().serialize(this.iterationIds);
    }

    public void setUserBusiness(UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }

    public int getBacklogSelectionType() {
        return backlogSelectionType;
    }

    public void setBacklogSelectionType(int backlogSelectionType) {
        this.backlogSelectionType = backlogSelectionType;
    }

    public boolean isOnlyOngoing() {
        return onlyOngoing;
    }

    public void setOnlyOngoing(boolean onlyOngoing) {
        this.onlyOngoing = onlyOngoing;
    }

    public Set<Integer> getUserIds() {
        return userIds;
    }

    public void setUserIds(Set<Integer> userIds) {
        this.userIds = userIds;
    }
    
    public InputStream getSheetData() {
        return new ByteArrayInputStream(excelData.toByteArray());
    }
    
}
