package fi.hut.soberit.agilefant.remote;

import java.util.List;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sun.jersey.spi.inject.Inject;

import fi.hut.soberit.agilefant.business.TimesheetBusiness;
import fi.hut.soberit.agilefant.transfer.BacklogTimesheetNode;
import fi.hut.soberit.agilefant.transfer.TimesheetReportTO;

@Component
@Scope("prototype")
@RolesAllowed("agilefantremote")
@Path("/timesheets")
public class TimesheetResource {
    @Autowired
    private TimesheetBusiness timesheetBusiness;

    @Inject
    UriInfo uriInfo;

    @GET
    @Produces("application/xml")
    public JAXBElement<TimesheetReportTO> get(
            @QueryParam("userIds") Set<Integer> userIds,
            @QueryParam("backlogIds") Set<Integer> backlogIds,
            @QueryParam("startDate") String startDateStr,
            @QueryParam("endDate") String endDateStr) {
        DateTime endDate = null, startDate = null;
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
        if (endDateStr != null) {
            endDate = fmt.parseDateTime(endDateStr);
        }
        if (startDateStr != null) {
            startDate = fmt.parseDateTime(startDateStr);
        }
        List<BacklogTimesheetNode> rootNodes = this.timesheetBusiness
                .getRootNodes(backlogIds, startDate, endDate, userIds);
        long effortSum = this.timesheetBusiness.getRootNodeSum(rootNodes);
        TimesheetReportTO report = new TimesheetReportTO();
        report.setProductNodes(rootNodes);
        report.setTotalEffortSum(effortSum);
        return new JAXBElement<TimesheetReportTO>(new QName("timesheetReport"),
                TimesheetReportTO.class, report);
    }
}
