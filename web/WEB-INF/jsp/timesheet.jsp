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
				<ww:form action="generateTree">
					<div id="subItems" style="margin-top: 0pt;">
						<div id="subItemHeader">				
							<table cellpadding="0" cellspacing="0">
								<tbody><tr>
									<td class="header">Query</td>
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
												} else {
													change_selected_interval('TODAY');
												}
												if(current) {
													$("#interval").find("[value='"+current+"']").attr("selected","selected");
												}	
											});
										</script> 
										<select name="interval" id="interval" onchange="change_selected_interval(this.value);">
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
										<ww:datepicker size="15" showstime="false"
	                       					format="%{getText('webwork.datepicker.format')}" id="effStartDate" name="startDate" value=""/>
									</td>
								</tr>
								<!--  End date -->
								<tr>				
									<td>End date</td>
									<td>
	               						<ww:datepicker size="15" showstime="false"
	                       					format="%{getText('webwork.datepicker.format')}" id="effEndDate" name="endDate" value=""/>
									</td>
								</tr>
								<!--  User selection -->				
								<tr>
									<td>Users</td>
					
				
									<td><c:set var="divId" value="1" scope="page" />
	
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
					<div id="subItems" style="margin-top: 0pt;">
						<div id="subItemHeader">
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
						<div id="subItemContent">
						<div id="listProjectHours" style="">
						<table class="reportTable" cellpadding="0" cellspacing="0">
							<tbody>
								<tr>
									<th>Name / Comment</th>
									<th>&nbsp;</th>
									<th>&nbsp;</th>
									<th style="width:120px;">Effort spent</th>
								</tr>
								
								<c:forEach items="${products}" var="prod">
									<c:set var="divId" value="${divId + 1}" scope="page" />
									<tr>
										<c:if test="${!(empty prod.hourEntries && empty prod.childBacklogItems)}">
										<th class="product first" colspan="3"><a onclick="javascript:if($('.backlogitem.prod${divId}').is(':visible')) { $('.prod${divId}').hide(); } else { $('.backlogitem.prod${divId}').show(); }"><c:out value="${prod.backlog.name}"/></a></th>
										</c:if>
										<c:if test="${empty prod.hourEntries && empty prod.childBacklogItems}">
										<th class="product first" colspan="3"><c:out value="${prod.backlog.name}"/></th>
										</c:if>
										<th class="product"><c:out value="${prod.hourTotal}"/></th> 
									</tr>
									<c:if test="${!empty prod.hourEntries}">
										<c:set var="heDivId" value="${heDivId + 1}" scope="page" />
										<tr class="backlogitem prod${divId} special hourentryhead leftborder toggleall">
											<th class="backlogitem first" colspan="3"><a onclick="javascript:$('.hour${heDivId}').toggle();">Logged effort</a></th>
											<th class="backlogitem fourth"><c:out value="${prod.spentHours}"/></th>
										</tr>
										<c:forEach items="${prod.hourEntries}" var="he">
											<tr class="hourentries hour${heDivId} prod${divId} leftborder toggleall">
												<td class="hourentry first"><c:out value="${he.description}"/></td>
												<td class="hourentry second"><ww:date name="#attr.he.date" format="dd.MM.yyyy HH:mm" /></td>
												<td class="hourentry third">${aef:html(he.user.fullName)}</td>
												<td class="hourentry fourth">${aef:html(he.timeSpent)}</td>
											</tr>
										</c:forEach>
									</c:if>
									<c:if test="${!empty prod.childBacklogItems}">
										<tr class="backlogitem prod${divId} special backlogitemshead leftborder toggleall">
											<th class="backlogitem first" colspan="3">Backlog items</th>
											<th class="backlogitem fourth">${aef:html(prod.hoursForChildBacklogItems)}</th>
										</tr>
										<c:forEach items="${prod.childBacklogItems}" var="bli">
											<c:set var="heDivId" value="${heDivId + 1}" scope="page" />
											<tr class="backlogitem prod${divId} leftborder toggleall">
												<c:if test="${!empty bli.hourEntries}">
													<th class="backlogitem first" colspan="3">&nbsp; &nbsp; &raquo; <a onclick="javascript:$('.hour${heDivId}').toggle();"><c:out value="${bli.backlogItem.name}"/></a></th>
												</c:if>
												<c:if test="${empty bli.hourEntries}">
													<th class="backlogitem first" colspan="3">&nbsp; &nbsp; &raquo; <c:out value="${bli.backlogItem.name}"/></th>
												</c:if>
												<th class="backlogitem"><c:out value="${bli.hourTotal}"/></th>
											</tr>
											<c:forEach items="${bli.hourEntries}" var="he">
												<tr class="hourentries hour${heDivId} prod${divId} leftborder toggleall">
													<td class="hourentry first"><c:out value="${he.description}"/></td>
													<td class="hourentry second"><ww:date name="#attr.he.date" format="dd.MM.yyyy HH:mm" /></td>
													<td class="hourentry third">${aef:html(he.user.fullName)}</td>
													<td class="hourentry fourth">${aef:html(he.timeSpent)}</td>
												</tr>
											</c:forEach>
										</c:forEach>										
									</c:if>
									<c:if test="${!empty prod.childBacklogs}">
										<c:forEach items="${prod.childBacklogs}" var="proj">
											<c:set var="divId" value="${divId + 1}" scope="page" />
											<tr>
												<c:if test="${!(empty proj.hourEntries && empty proj.childBacklogItems)}">
												<th class="project first" colspan="3">&raquo; <a onclick="javascript:if($('.backlogitem.proj${divId}').is(':visible')) { $('.proj${divId}').hide(); } else { $('.backlogitem.proj${divId}').show(); }"><c:out value="${proj.backlog.name}"/></a></th>
												</c:if>
												<c:if test="${empty proj.hourEntries && empty proj.childBacklogItems}">
												<th class="project first" colspan="3">&raquo; <c:out value="${proj.backlog.name}"/></th>
												</c:if>
												<th class="project"><c:out value="${proj.hourTotal}"/></th>
											</tr>
											<c:if test="${!empty proj.hourEntries}">
												<c:set var="heDivId" value="${heDivId + 1}" scope="page" />
												<tr class="backlogitem proj${divId} special hourentryhead leftborder toggleall">
														<th class="backlogitem first" colspan="3"><a onclick="javascript:$('.hour${heDivId}').toggle();">Logged effort</a></th>
														<th class="backlogitem fourth"><c:out value="${proj.spentHours}"/></th>
												</tr>
												<c:forEach items="${proj.hourEntries}" var="he">
													<tr class="hourentries hour${heDivId} proj${divId} leftborder toggleall">
														<td class="hourentry first"><c:out value="${he.description}"/></td>
														<td class="hourentry second"><ww:date name="#attr.he.date" format="dd.MM.yyyy HH:mm" /></td>
														<td class="hourentry third">${aef:html(he.user.fullName)}</td>
														<td class="hourentry fourth">${aef:html(he.timeSpent)}</td>
													</tr>
												</c:forEach>
											</c:if>
											<c:if test="${!empty proj.childBacklogItems}">
												<tr class="backlogitem proj${divId} special backlogitemshead leftborder toggleall">
													<th class="backlogitem first" colspan="3">Backlog items</th>
													<th class="backlogitem fourth">${aef:html(proj.hoursForChildBacklogItems)}</th>
												</tr>
												<c:forEach items="${proj.childBacklogItems}" var="bli">
													<c:set var="heDivId" value="${heDivId + 1}" scope="page" />
													<tr class="backlogitem proj${divId} leftborder toggleall">
														<c:if test="${!empty bli.hourEntries}">
															<th class="backlogitem first" colspan="3">&nbsp; &nbsp; &raquo; <a onclick="javascript:$('.hour${heDivId}').toggle();"><c:out value="${bli.backlogItem.name}"/></a></th>
														</c:if>
														<c:if test="${empty bli.hourEntries}">
															<th class="backlogitem first" colspan="3">&nbsp; &nbsp; &raquo; <c:out value="${bli.backlogItem.name}"/></th>
														</c:if>
														<th class="backlogitem"><c:out value="${bli.hourTotal}"/></th>
													</tr>
													<c:forEach items="${bli.hourEntries}" var="he">
														<tr class="hourentries hour${heDivId} proj${divId} leftborder toggleall">
															<td class="hourentry first"><c:out value="${he.description}"/></td>
															<td class="hourentry second"><ww:date name="#attr.he.date" format="dd.MM.yyyy HH:mm" /></td>
															<td class="hourentry third">${aef:html(he.user.fullName)}</td>
															<td class="hourentry fourth">${aef:html(he.timeSpent)}</td>
														</tr>
													</c:forEach>
												</c:forEach>										
											</c:if>
											<c:if test="${!empty proj.childBacklogs}">
												<c:forEach items="${proj.childBacklogs}" var="iter">
													<c:set var="divId" value="${divId + 1}" scope="page" />
													<tr>
														<c:if test="${!(empty iter.hourEntries && empty iter.childBacklogItems)}">
														<th class="iteration first" colspan="3">&nbsp;&nbsp;&raquo; <a onclick="javascript:if($('.backlogitem.iter${divId}').is(':visible')) { $('.iter${divId}').hide(); } else { $('.backlogitem.iter${divId}').show(); }"><c:out value="${iter.backlog.name}"/></a></th>
														</c:if>
														<c:if test="${empty iter.hourEntries && empty iter.childBacklogItems}">
														<th class="iteration first" colspan="3">&nbsp;&nbsp;&raquo; <c:out value="${iter.backlog.name}"/></th>
														</c:if>
														<th class="iteration"><c:out value="${iter.hourTotal}"/></th>
													</tr>
													<c:if test="${!empty iter.hourEntries}">
														<c:set var="heDivId" value="${heDivId + 1}" scope="page" />
														<tr class="backlogitem proj${divId} special hourentryhead leftborder toggleall">
																<th class="backlogitem first" colspan="3"><a onclick="javascript:$('.hour${heDivId}').toggle();">Logged effort</a></th>
																<th class="backlogitem fourth"><c:out value="${iter.spentHours}"/></th>
														</tr>
														<c:forEach items="${iter.hourEntries}" var="he">
															<tr class="hourentries hour${heDivId} proj${divId} leftborder toggleall">
																<td class="hourentry first"><c:out value="${he.description}"/></td>
																<td class="hourentry second"><ww:date name="#attr.he.date" format="dd.MM.yyyy HH:mm" /></td>
																<td class="hourentry third">${aef:html(he.user.fullName)}</td>
																<td class="hourentry fourth">${aef:html(he.timeSpent)}</td>
															</tr>
														</c:forEach>
													</c:if>
													<c:if test="${!empty iter.childBacklogItems}">
														<c:forEach items="${iter.childBacklogItems}" var="bli">
															<c:set var="heDivId" value="${heDivId + 1}" scope="page" />
															<tr class="backlogitem iter${divId} toggleall">
																<c:if test="${!empty bli.hourEntries}">
																<th class="backlogitem first" colspan="3">&nbsp; &nbsp; &raquo; <a onclick="javascript:$('.hour${heDivId}').toggle();"><c:out value="${bli.backlogItem.name}"/></a></th>
																</c:if>
																<c:if test="${empty bli.hourEntries}">
																<th class="backlogitem first" colspan="3">&nbsp; &nbsp; &raquo; <c:out value="${bli.backlogItem.name}"/></th>
																</c:if>
																<th class="backlogitem"><c:out value="${bli.hourTotal}"/></th>
															</tr>
															<c:forEach items="${bli.hourEntries}" var="he">
																<tr class="hourentries hour${heDivId} iter${divId} toggleall">
																	<td class="hourentry first"><c:out value="${he.description}"/></td>
																	<td class="hourentry second"><ww:date name="#attr.he.date" format="dd.MM.yyyy HH:mm" /></td>
																	<td class="hourentry third">${aef:html(he.user.fullName)}</td>
																	<td class="hourentry fourth">${aef:html(he.timeSpent)}</td>
																</tr>
															</c:forEach>
														</c:forEach>										
													</c:if>													
												</c:forEach>
											</c:if>
										</c:forEach>
									</c:if>
								</c:forEach>
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