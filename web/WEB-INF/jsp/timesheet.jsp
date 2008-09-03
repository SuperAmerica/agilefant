<%@ include file="./inc/_taglibs.jsp"%>
<%@ include file="./inc/_header.jsp"%>

<aef:menu navi="timesheet" pageHierarchy="${pageHierarchy}" />
<aef:existingObjects />

<aef:productList />
<aef:userList />
<aef:teamList />
<aef:currentUser />




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
									<td>
										<select multiple="multiple" name="backlogIds">
										<%-- Resolve if product is selected or is in 'path' and set variable 'class' accordingly--%>
												<c:forEach items="${productList}" var="product">
												<c:if test="${aef:listContains(selected, product.id)}">
													<option value="${product.id}" selected="selected">${aef:out(product.name)}</option>
												</c:if>	
												<c:if test="${!aef:listContains(selected, product.id)}">
													<option value="${product.id}">${aef:out(product.name)}</option>
												</c:if>
												<c:forEach items="${product.projects}" var="project">
													<c:if test="${aef:listContains(selected, project.id)}">						
														<option value="${project.id}" selected="selected">&nbsp;&nbsp;&nbsp;&nbsp;${aef:out(project.name)}</option>
													</c:if>
													<c:if test="${!aef:listContains(selected, project.id)}">						
														<option value="${project.id}">&nbsp;&nbsp;&nbsp;&nbsp;${aef:out(project.name)}</option>
													</c:if>
									                	<c:forEach items="${project.iterations}" var="it">
									                		<c:if test="${aef:listContains(selected, it.id)}">
	                    										<option value="${it.id}" selected="selected">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${aef:out(it.name)}</option>
	                										</c:if>
	                										<c:if test="${!aef:listContains(selected, it.id)}">
	                    										<option value="${it.id}">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${aef:out(it.name)}</option>
	                										</c:if>
	                									</c:forEach>
	                							</c:forEach>
	            							</c:forEach>
			
										</select>
									</td>
								</tr>
								<!-- Interval selection -->			
								<tr>
									<td>Interval</td>
					
									<td colspan="2">
										<script type="text/javascript">
											function addZero($string) {
												var str = '0'+$string;
												//return last two chars
												return str.substr(str.length-2);
											}
			
											function change_selected_interval(value) {
												var startDate = document.getElementById('effStartDate');
												var endDate = document.getElementById('effEndDate');
												// Current timestamp
												var now = new Date();
												
												// Yesterday's timestamp
												var yesterday = new Date(now.getTime() - 86400000);
												
												// This monday
												var daysfrommonday = 0;
												if(now.getDay() == 0) {
													daysfrommonday = 6;
												} else {
													daysfrommonday = now.getDay() - 1;
												}
												var thismonday = new Date(now.getTime() - (86400000 * daysfrommonday));
												
												// Last week's monday
												var lastmonday = new Date(thismonday.getTime() - (86400000 * 7));
												
												// Last month
												var lastmonth = new Date();
												if(now.getMonth() == 0) {
													lastmonth.setMonth(11)
													lastmonth.setFullYear(now.getFullYear() - 1);
												} else {
													lastmonth.setMonth(now.getMonth() - 1)
												}
												
												
												if (value == 'TODAY') {
													startDate.value = now.getFullYear() + '-' + addZero(now.getMonth() + 1) + '-' + addZero(now.getDate()) + ' 00:00';
													endDate.value   = now.getFullYear() + '-' + addZero(now.getMonth() + 1) + '-' + addZero(now.getDate()) + ' ' + addZero(now.getHours()) + ':' + addZero(now.getMinutes());
												} else if (value == 'YESTERDAY') {
													startDate.value = yesterday.getFullYear() + '-' + addZero(yesterday.getMonth() + 1) + '-' + addZero(yesterday.getDate()) + ' 00:00';
													endDate.value   = now.getFullYear() + '-' + addZero(now.getMonth() + 1) + '-' + addZero(now.getDate()) + ' 00:00';
												} else if (value == 'THIS_WEEK') {
													startDate.value = thismonday.getFullYear() + '-' + addZero(thismonday.getMonth() + 1) + '-' + addZero(thismonday.getDate()) + ' 00:00';
													endDate.value   = now.getFullYear() + '-' + addZero(now.getMonth() + 1) + '-' + addZero(now.getDate()) + ' ' + addZero(now.getHours()) + ':' + addZero(now.getMinutes());
												} else if (value == 'LAST_WEEK') {
													startDate.value = lastmonday.getFullYear() + '-' + addZero(lastmonday.getMonth() + 1) + '-' + addZero(lastmonday.getDate()) + ' 00:00';
													endDate.value   = thismonday.getFullYear() + '-' + addZero(thismonday.getMonth() + 1) + '-' + addZero(thismonday.getDate()) + ' 00:00';
												} else if (value == 'THIS_MONTH') {
													startDate.value = now.getFullYear() + '-' + addZero(now.getMonth() + 1) + '-01 00:00';
													endDate.value   = now.getFullYear() + '-' + addZero(now.getMonth() + 1) + '-' + addZero(now.getDate()) + ' ' + addZero(now.getHours()) + ':' + addZero(now.getMinutes());
												} else if (value == 'LAST_MONTH') {
													startDate.value = lastmonth.getFullYear() + '-' + addZero(lastmonth.getMonth() + 1) + '-01 00:00';
													endDate.value   = now.getFullYear() + '-' + addZero(now.getMonth() + 1) + '-01 00:00';
												} else if (value == 'THIS_YEAR') {
													startDate.value = now.getFullYear() + '-01-01 00:00';
													endDate.value   = now.getFullYear() + '-' + addZero(now.getMonth() + 1) + '-' + addZero(now.getDate()) + ' ' + addZero(now.getHours()) + ':' + addZero(now.getMinutes());
												} else if (value == 'LAST_YEAR') {
													startDate.value = (now.getFullYear() - 1) + '-01-01 00:00';
													endDate.value   = now.getFullYear() + '-01-01 00:00';
												} else if (value == 'NO_INTERVAL') {
													startDate.value = '';
													endDate.value   = '';
												}
											}
											$(document).ready( function() {
												var interval = document.getElementById('interval');
												<ww:set name="currently" value="#attr.interval" />
												var current = "${currently}";
												if (current) {
													change_selected_interval(current);
													$("#interval").find("[value='"+current+"']").attr("selected","selected");
												} 
												/*
												else {
													change_selected_interval('TODAY');
												}
												*/	
											});
										</script> 
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
								<tr>				
									<td>Start date</td>
									<td>
										<!--<ww:datepicker size="15" showstime="false"
	                       					format="%{getText('webwork.datepicker.format')}" id="effStartDate" name="startDate" value=""/>-->
	                       				<aef:datepicker value="${startDate}" id="effStartDate" name="startDate" format="%{getText('webwork.shortDateTime.format')}" />
									</td>
								</tr>
								<!--  End date -->
								<tr>				
									<td>End date</td>
									<td>
	               						<!--<ww:datepicker size="15" showstime="false"
	                       					format="%{getText('webwork.datepicker.format')}" id="effEndDate" name="endDate" value=""/>-->
	                       				<aef:datepicker value="${endDate}" id="effEndDate" name="endDate" format="%{getText('webwork.shortDateTime.format')}" />
									</td>
								</tr>
								<!--  User selection -->				
								<tr>
									<td>Users</td>
					
				
									<td><c:set var="divId" value="1" scope="request" />
	
	                    				<div id="assigneesLink">
	                    					<a href="javascript:toggleDiv(${divId});">
	                    						<img src="static/img/users.png"/>
	                        					<c:set var="listSize" value="${fn:length(selUser)}" scope="page" />
												<c:choose>
													<c:when test="${listSize > 0}">
														<c:set var="count" value="0" scope="page" />
														<c:set var="comma" value="," scope="page" />
														<c:forEach items="${selUser}" var="responsible">
															<c:if test="${count == listSize - 1}" >
																<c:set var="comma" value="" scope="page" />
															</c:if>
																<span><c:out value="${responsible.initials}" /></span><c:out value="${comma}" />
															<c:set var="count" value="${count + 1}" scope="page" />
														</c:forEach>
													</c:when>
													<c:otherwise>
														<c:out value="choose" />
													</c:otherwise>
												</c:choose>
	                    					</a>
	                    				</div>
	                        
	                    				<div id="${divId}" class="userSelector" style="display: none;">
	                 
	                    					<div id="userselect" class="userSelector">
	                    						<div class="left">
	                    							<label>Active users</label>
	                    							<ul class="users_0"></ul>
	                    		
	                    							<label>Disabled users</label>
	                    							<ul class="users_1"></ul>
	                    							
	                    						</div>
	                        					<div class="right">
	                            					<label>Teams</label>
	                            					<ul class="groups" />
	                    						</div>
	                    						<script type="text/javascript">
	                        						$(document).ready( function() {
	                            						<aef:teamList />
	                            						<aef:enabledUserList />
	                            						<aef:userList />
	                            						<ww:set name="teamList" value="#attr.teamList" />
	                            						<ww:set name="enabledUserList" value="#attr.enabledUserList" />
	                            						<ww:set name="userList" value="#attr.userList" />
	                            						<ww:set name="selectedID" value="#attr.selUId" />
	                            						var teams = [<aef:teamJson items="${teamList}" />];
	                            						var preferred = [<aef:userJson items="${enabledUserList}" />];
	                           						 	var other = [<aef:userJson items="${aef:listSubstract(userList, enabledUserList)}" />];
	                           						 	var selected = [<aef:idJson items="${selUser}" />];
	                           	 						$('#userselect').multiuserselect({users: [preferred, other], groups: teams, root: $('#userselect')}).selectusers(selected);
	                        						});
	                        					</script>
	                    					</div>
	                    				</div>
	                    			</td>
								</tr>
								<!-- Submit button -->
								<tr>
									<td></td>
									<td>
										<ww:submit value="Calculate" />
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
							<table>
								<tbody>
									<tr>
										<td class="header">Entries</td>
										<td class="icons">
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