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
				<ww:form action="timesheet">
					<div id="subItems" style="margin-top: 0pt;">
						<div id="subItemHeader">				
							<table cellpadding="0" cellspacing="0">
								<tbody><tr>
									<td class="header">Generate Timesheets Report</td>
								</tr>
								</tbody>
							</table>
						</div>
						<table class="formTable">
							<tbody>
	
								<tr>
									<td>
										Select Backlog
									</td>
									<td>
										<select multiple="multiple" name="${selectName}" disabled>
										<%-- Resolve if product is selected or is in 'path' and set variable 'class' accordingly--%>
											<c:forEach items="${productList}" var="product">
									
											<%-- Print Product-link--%>
												<option>${aef:out(product.name)}</option>
					
					
												<%-- Resolve if project is selected or is in 'path' and set variable 'class' accordingly--%>
												<c:forEach items="${product.projects}" var="project">
						
													<option>&nbsp;&nbsp;&nbsp;&nbsp;${aef:out(project.name)}</option>
						
									                	<c:forEach items="${project.iterations}" var="it">
	                    									<option>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${aef:out(it.name)}</option>
	                									</c:forEach>
	                							</c:forEach>
	            							</c:forEach>
			
										</select>
									</td>
								</tr>
								<!-- Interval selection -->			
								<tr>
									<td>Select Interval</td>
					
									<td colspan="2">
										<script type="text/javascript">
											function change_selected_interval(value) {
	
												var startDate = document.getElementById('effStartDate');
												var endDate = document.getElementById('effEndDate');
												
												var now = new Date();
												if (value == 'TODAY') {
													var year = now.getFullYear();
													var month = now.getMonth() + 1;
													var day = now.getDate();
													startDate.value = ('' + year + '-' + month + '-' + day +' 00:00');
													endDate.value   = ('' + year + '-' + month + '-' + day +' 23:59');
												} else if (value == 'THIS_WEEK') {
													var day = now.getDate();
													var temp = now.getDay();
													now.setDate((day - temp) + 1);
													var year = now.getFullYear();
													var month = now.getMonth() + 1;
													day = now.getDate();
													startDate.value = ('' + year + '-' + month + '-' + day +' 00:00');
													now.setDate(day + 6);
													day = now.getDate();
													month = now.getMonth() + 1;
													year = now.getFullYear();
													endDate.value   = ('' + year + '-' + month + '-' + day +' 23:59');
												} else if (value == 'LAST_WEEK') {
													var day = now.getDate();
													var temp = now.getDay();
													now.setDate((day - temp) - 6);
													var year = now.getFullYear();
													var month = now.getMonth() + 1;
													day = now.getDate();
													startDate.value = ('' + year + '-' + month + '-' + day +' 00:00');
													now.setDate(day + 6);
													day = now.getDate();
													month = now.getMonth() + 1;
													year = now.getFullYear();
													endDate.value   = ('' + year + '-' + month + '-' + day +' 23:59');
												} else if (value == 'THIS_MONTH') {
													var year = now.getFullYear();
													var month = now.getMonth() + 1;
													startDate.value = ('' + year + '-' + month + '-' + '1 00:00');
													now.setMonth(month);
													now.setDate(0);
													var day = now.getDate();
													endDate.value   = ('' + year + '-' + month + '-' + day +' 23:59');
												} else if (value == 'LAST_MONTH') {
													var year = now.getFullYear();
													var month = now.getMonth();
													startDate.value = ('' + year + '-' + month + '-' + '1 00:00');
													now.setDate(0);
													var day = now.getDate();
													endDate.value   = ('' + year + '-' + month + '-' + day +' 23:59');
												} else if (value == 'THIS_YEAR') {
													var year = now.getFullYear();
													startDate.value = ('' + year + '-' + '1-1 00:00');
													endDate.value   = ('' + year + '-' + '12-31 23:59');
												} else if (value == 'NO_INTERVAL') {
													startDate.value = '';
													endDate.value   = '';
												}
											}
										</script> 
										<select name="interval" id="interval" onchange="change_selected_interval(this.value);" disabled>
	
											<option>(Select interval)</option>
											<option value="NO_INTERVAL">(No interval - display all hours)</option>
	    									<option value="TODAY">Today</option>
	    									<option value="THIS_WEEK">This week</option>
					    					<option value="LAST_WEEK">Last week</option>
	    									<option value="THIS_MONTH">This month</option>
	
	    									<option value="LAST_MONTH">Last month</option>
	    									<option value="THIS_YEAR">This year</option>
										</select>
									</td>
								</tr>
								<!--  Start date -->
								<tr>				
									<td>Start date</td>
									<td>
										<ww:datepicker size="15" showstime="false"
	                       					format="%{getText('webwork.datepicker.format')}" id="effStartDate" name="startDate" value="" disabled="true"/>
									</td>
								</tr>
								<!--  End date -->
								<tr>				
									<td>End date</td>
									<td>
	               						<ww:datepicker size="15" showstime="false"
	                       					format="%{getText('webwork.datepicker.format')}" id="effEndDate" name="endDate" value="" disabled="true"/>
									</td>
								</tr>
								<!--  User selection -->				
								<tr>
									<td>Select Users</td>
					
				
									<td><c:set var="divId" value="1" scope="page" />
	
	                    				<div id="assigneesLink">
	                    					<%--
	                    					<a href="javascript:toggleDiv(${divId});">
											--%>
												<a>
	                        					<img src="static/img/users.png" />
	                        					select
	                    					</a>
	                    				</div>
	                        
	                    				<div id="${divId}" class="userSelector" style="display: none;">
	                 
	                    					<div id="userselect" class="userSelector">
	                    						<div class="left">
	                    							<label>Enabled users</label>
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
	                            						var teams = [<aef:teamJson items="${teamList}" />];
	                            						var preferred = [<aef:userJson items="${enabledUserList}" />];
	                           						 	var other = [<aef:userJson items="${aef:listSubstract(userList, enabledUserList)}" />];
	                            
	                           	 						$('#userselect').multiuserselect({users: [preferred, other], groups: teams, root: $('#userselect')});
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
										<%--
										<ww:submit value="Generate Timesheets Report" />
										--%>
										<input type="submit" value="Generate Timesheets Report" disabled />
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
					
				
			
					

<!--
<img src="http://www.vayrynen.com/pics/navi_picture.jpg" alt="Paavo rulaa!" /><br>
<h2>Kansalaisyhteiskunta</h2>
Arvoisa äänestäjä: Huolestuttaako Teitä kansakuntamme tulevaisuus?<br><br>
Askarruttaako Teitä kolmikantaneuvotteluiden tulokset sekä viimeaikainen keskustelu
työmarkkinajärjestöjen roolista?<br><br>
Minulla on juuri sinulle <a href="http://www.vayrynen.com/">asiaa</a>.
Nyt on koittanut työreformin aika!<br>

<img src="http://irestidelcarlino.files.wordpress.com/2006/12/david-hasselhoff-07.jpg" alt="Yeah baby! Want to hard ride tonight?"/>
--!>

<%@ include file="./inc/_footer.jsp"%>