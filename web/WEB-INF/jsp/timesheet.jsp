<%@ include file="./inc/_taglibs.jsp"%>
<%@ include file="./inc/_header.jsp"%>

<aef:menu navi="timesheet" pageHierarchy="${pageHierarchy}" title="Timesheets"/>
<aef:existingObjects />

<script type="text/javascript">
$(document).ready(function() {
    var chooserDiv = $("#selectBacklogs");
    var userSel = $("#userSelect");
    
    $("#showOnlyOngoingBacklogs").change(function() {
        if($(this).is(":checked")) {
            chooserDiv.backlogChooser("setDateLimit");
        } else {
            chooserDiv.backlogChooser("unsetDateLimit");
        }
    });
    chooserDiv.backlogChooser({
        useDateLimit: $("#showOnlyOngoingBacklogs").is(":checked"),
        selectedProducts: ${JSONProducts},
        selectedProjects: ${JSONProjects},
        selectedIterations: ${JSONIterations}
    });
    $('#userChooserLink-createStory').userChooser({
        legacyMode: false,
        renderFor: 'allUsers',
        storyId: 0,
        userListContainer: '#userListContainer-createStory',
        emptySelectionText: "(all)"
    });
});

function addZero($string) {
    var str = '0'+$string;
    //return last two chars
    return str.substr(str.length-2);
}

function change_selected_interval(value) {
    var startDate = document.getElementById('effStartDate');
    var endDate = document.getElementById('effEndDate');

	var today = new Date();
	today.zeroTime();
	var yesterday = new Date();
	yesterday.zeroTime();
	yesterday.addDays(-1);
	var thisweek = new Date();
	thisweek.zeroTime();
	thisweek.addDays(1 - thisweek.getDay());
	var lastweek = new Date();
	lastweek.zeroTime();
	lastweek.addDays(1 - lastweek.getDay());
	lastweek.addDays(-7);
	var thismonth = new Date();
	thismonth.zeroTime();
	thismonth.setDate(1);
	var lastmonth = new Date();
	lastmonth.zeroTime();
	lastmonth.addMonths(-1);
	lastmonth.setDate(1);
	var thisyear = new Date();
	thisyear.zeroTime();
	thisyear.setMonth(0);
	thisyear.setDate(1);
	var lastyear = new Date();
	lastyear.zeroTime();
	lastyear.setDate(1);
	lastyear.setMonth(0);
	lastyear.addYears(-1);
    $(".dateSelectRow").hide();
    if (value == 'TODAY') {
        startDate.value = today.asString();
        today.setHours(23);
        today.setMinutes(59);
        endDate.value   = today.asString();
    } else if (value == 'YESTERDAY') {
        startDate.value = yesterday.asString();
        yesterday.setHours(23);
        yesterday.setMinutes(59);
        endDate.value   = yesterday.asString();
    } else if (value == 'THIS_WEEK') {
        startDate.value = thisweek.asString();
        thisweek.addDays(6);
        thisweek.setHours(23);
        thisweek.setMinutes(59);
        endDate.value   = thisweek.asString();
    } else if (value == 'LAST_WEEK') {
        startDate.value = lastweek.asString();
        lastweek.addDays(6);
        lastweek.setHours(23);
        lastweek.setMinutes(59);
        endDate.value   = lastweek.asString();
    } else if (value == 'THIS_MONTH') {
        startDate.value = thismonth.asString();
        thismonth.addMonths(1);
        thismonth.addMinutes(-1);
        endDate.value   = thismonth.asString();
    } else if (value == 'LAST_MONTH') {
        startDate.value = lastmonth.asString();
        lastmonth.addMonths(1);
        lastmonth.addMinutes(-1);
        endDate.value   = lastmonth.asString();
    } else if (value == 'THIS_YEAR') {
        startDate.value = thisyear.asString();
        thisyear.addYears(1);
        thisyear.addMinutes(-1);
        endDate.value   = thisyear.asString();
    } else if (value == 'LAST_YEAR') {
        startDate.value = lastyear.asString();
        lastyear.addYears(1);
        lastyear.addMinutes(-1);
        endDate.value   = lastyear.asString();
    } else if (value == 'NO_INTERVAL') {
        startDate.value = '';
        endDate.value   = '';
    } else {
		$(".dateSelectRow").show();
    }
}
$(document).ready( function() {
    var interval = document.getElementById('interval');
    <ww:set name="currently" value="#attr.interval" />
    var current = "${interval}";
    if (current) {
        change_selected_interval(current);
        $("#interval").find("[value='"+current+"']").attr("selected","selected");
    } 
});
</script>

<h2>Timesheets</h2>

<table>
	<tbody>
		<tr>
			<td>
				<ww:form action="generateTree" method="post">
					<div id="subItems" style="margin-top: 0pt;" id="subItems_timesheetGenerateReport">
						<div id="subItemHeader">				
							<table cellpadding="0" cellspacing="0">
								<tbody><tr>
									<td class="header">Generate a report</td>
								</tr>
								</tbody>
							</table>
						</div>
						<table class="formTable">
							<tbody>
	
								<tr>
									<td>
										Backlogs 
									</td>
<%-- 									
									<td>
										<c:choose>
											<c:when test="${backlogSelectionType == 1}">
												<input type="radio" name="backlogSelectionType" value="0" />Advanced
												<input type="radio" name="backlogSelectionType" value="1" checked="checked"/>My effort from ongoing projects I've been assigned to.										
											</c:when>
											<c:otherwise>
												<input type="radio" name="backlogSelectionType" value="0" checked="checked"/>Advanced
												<input type="radio" name="backlogSelectionType" value="1" />My effort from ongoing projects I've been assigned to.
											</c:otherwise>
										</c:choose>
									</td>
								</tr>
								<tr>
									<td></td>
									<td>
										<c:choose>
											<c:when test="${backlogSelectionType == 1}">
												<div id="advancedBacklogs" style="display: none;">
											</c:when>
											<c:otherwise>
												<div id="advancedBacklogs">
											</c:otherwise>
										</c:choose>
--%>
							 		<td>
							 			<input type="hidden" name="backlogSelectionType" value="0" />
							 			<div id="advancedBacklogs">
										<c:choose>
											<c:when test="${onlyOngoing}">
												<input id="showOnlyOngoingBacklogs" name="onlyOngoing" type="checkbox" value="true" checked="checked"/>Hide past projects and iterations.<br />								
											</c:when>
											<c:otherwise>
												<input id="showOnlyOngoingBacklogs" name="onlyOngoing" type="checkbox" value="true" />Hide past projects and iterations.<br />						
											</c:otherwise>
										</c:choose>
										<div id="selectBacklogs"></div>
									</div>
									</td>
								</tr>
								<!-- Interval selection -->			
								<tr>
									<td>Interval</td>
					
									<td colspan="2">
										 
										<select name="interval" id="interval" onchange="change_selected_interval(this.value);">
												<option value="">Custom</option>
												<option value="TODAY">Today</option>
												<option value="YESTERDAY">Yesterday</option>
												<option value="THIS_WEEK">This week</option>
												<option value="LAST_WEEK">Last week</option>
												<option value="THIS_MONTH">This month</option>
												<option value="LAST_MONTH">Last month</option>
												<option value="THIS_YEAR">This year</option>
												<option value="LAST_YEAR">Last year</option>
												<option value="NO_INTERVAL">(All past entries)</option>
										</select>
									</td>
								</tr>
								<!--  Start date -->
								<tr class="dateSelectRow" style="display: none;">				
									<td>Start date</td>
									<td>
	                       				<aef:datepicker value="${startDate}" id="effStartDate" name="startDate" format="%{getText('webwork.shortDateTime.format')}" />
									</td>
								</tr>
								<!--  End date -->
								<tr class="dateSelectRow" style="display: none;">				
									<td>End date</td>
									<td>
	                       				<aef:datepicker value="${endDate}" id="effEndDate" name="endDate" format="%{getText('webwork.shortDateTime.format')}" />
									</td>
								</tr>
								<!--  User selection -->				
								<tr id="userSelect">
									<td>Users</td>
									<td>                        
	                    				<div>
							                <a id="userChooserLink-createStory" href="#" class="assigneeLink">
							                    <img src="static/img/users.png"/>
							                    <span id="userListContainer-createStory">
							                    <c:set var="userCount" value="${fn:length(selUser)}" />
							                    <c:set var="curUserNo" value="0" />
                                                <c:if test="${userCount == 0}">
                                                (all)
                                                </c:if>
							                    <c:forEach items="${selUser}" var="selu">
                                                    <input type="hidden" name="userIds" value="${selu.id}" />
                                                    <c:set var="curUserNo" value="${curUserNo + 1}" />
                                                    ${selu.initials}<c:if test="${curUserNo !=  userCount}">,</c:if>									                
                                                </c:forEach>
							                    </span>
							                </a>
							            </div>
	                    			</td>
								</tr>
								<!-- Submit button -->
								<tr>
									<td></td>
									<td>
										<ww:submit value="Calculate" />
										<ww:submit value="Export to Excel" action="generateExcel" />
									</td>
								</tr>
							</tbody>
						</table>
					</ww:form>
				</div>
			</td>
		</tr>
	</tbody>
</table>

<%--show the tree--%>
<c:if test="${!empty products}">
	<table style="margin-top:20px;">
		<tbody>
			<tr>
				<td>
					<div class="subItems" style="margin-top: 0pt;" id="subItems_timesheetEntries">
						<div class="subItemHeader">
							<table cellpadding="0" cellspacing="0">
								<tbody>
									<tr>
										<td class="header">Entries</td>
										<td class="icons_old" style="padding: 2px 3px 0 3px;">
											<a onclick="javascript:$('.toggleall').show();" style="cursor:pointer;"><img src="static/img/plus.png" alt="Expand" title="Expand" height="18" width="18"></a>
											<a onclick="javascript:$('.toggleall').hide();" style="cursor:pointer;"><img src="static/img/minus.png" alt="Collapse" title="Collapse" height="18" width="18"></a>
										</td>
									</tr>
								</tbody>
							</table>
						</div>
						<div class="subItemContent">
						<div id="listProjectHours" style="">
						<table class="reportTable" cellpadding="0" cellspacing="0">
							<tbody>
								<tr>
									<th>Name / Comment</th>
									<th style="width:115px;">&nbsp;</th>
									<th style="width:125px;">&nbsp;</th>
									<th style="width:120px;">Effort spent</th>
								</tr>
								<aef:timesheetItem nodes="${products}" />
								<tr class="total">
									<th class="total">Query total</th>
									<th class="total">&nbsp;</th>
									<th class="total">&nbsp;</th>
									<th class="total"><c:out value="${totalSpentTime}"/></th>
								</tr>
							</tbody>
						</table>
					</div>
				</td>
			</tr>
		</tbody>
	</table>
</c:if>
<%@ include file="./inc/_footer.jsp"%>