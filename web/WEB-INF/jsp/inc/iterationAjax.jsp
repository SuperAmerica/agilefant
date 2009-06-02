<%@ include file="_taglibs.jsp"%>

<aef:productList />

<ww:actionerror />
<ww:actionmessage />

<div class="ajaxWindowTabsDiv">
<ul class="ajaxWindowTabs">
	<li><a href="#iterationInfoTab-${iterationId}"><span><img src="static/img/info.png" alt="Info" /> Info</span></a></li>
	<li><a href="#iterationEditTab-${iterationId}"><span><img src="static/img/edit.png" alt="Edit" /> Edit iteration</span></a></li>
	<li><a href="#iterationThemesTab-${iterationId}"><span><img src="static/img/theme.png" alt="Iterations" /> Themes</span></a></li>
	<li><a href="#iterationGoalsTab-${iterationId}"><span><img src="static/img/goal.png" alt="Stories" /> Stories</span></a></li>
</ul>

<div id="iterationInfoTab-${iterationId}" class="iterationNaviTab">

<table>
<tbody>
	<tr>
	<td>
	<div class="subItems" style="margin-top: 0px; width: 720px;">

		<div id="descriptionDiv" class="descriptionDiv"
			style="display: block; max-height: 1000em; overflow: visible;">
			<table class="infoTable" cellpadding="0" cellspacing="0">
				<tr>
					<th class="info1" style="background: transparent;">Timeframe</th>
					<td class="info3">
						<c:out
							value="${iteration.startDate.date}.${iteration.startDate.month + 1}.${iteration.startDate.year + 1900}" />
						- <c:out
						value="${iteration.endDate.date}.${iteration.endDate.month + 1}.${iteration.endDate.year + 1900}" /></td>
					<td class="info4" rowspan="3">
					<div class="smallBurndown"><a href="#bigChart"><img
						src="drawSmallChart.action?iterationId=${iteration.id}" /></a>
					</div>

					<table>
						<tr>
							<th style="background: transparent;">Velocity</th>
							<td><c:out value="${iterationMetrics.dailyVelocity}" /> /
							day</td>
						</tr>
						<c:if test="${iterationMetrics.backlogOngoing}">
							<tr>
								<th style="background: transparent;">Schedule variance</th>
								<td><c:choose>
									<c:when test="${iterationMetrics.scheduleVariance != null}">
										<c:choose>
											<c:when test="${iterationMetrics.scheduleVariance > 0}">
												<span class="red">+ 
											</c:when>
											<c:otherwise>
												<span>
											</c:otherwise>
										</c:choose>
										<c:out value="${iterationMetrics.scheduleVariance}" /> days
                                            </c:when>
									<c:otherwise>
                                                unknown
                                            </c:otherwise>
								</c:choose></td>
							</tr>
							<tr>
								<th style="background: transparent;">Scoping needed</th>
								<td><c:choose>
									<c:when test="${iterationMetrics.scopingNeeded != null}">
										<c:out value="${iterationMetrics.scopingNeeded}" />
									</c:when>
									<c:otherwise>
                                                unknown
                                            </c:otherwise>
								</c:choose></td>
							</tr>
						</c:if>
						<tr>
							<th style="background: transparent;">Completed</th>
							<td><c:out value="${iterationMetrics.percentDone}" />% (<c:out
								value="${iterationMetrics.completedItems}" /> / <c:out
								value="${iterationMetrics.totalItems}" />)</td>
						</tr>
					</table>

					</td>
				</tr>
				<tr>
					<td colspan="2" class="description">${iteration.description}</td>
					<td></td>
				</tr>

			</table>
		</div>

	</div>
	</td>
	</tr>
</tbody>
</table>

</div>

<div id="iterationEditTab-${iterationId}" class="iterationNaviTab">

<table>
<tbody>
	<tr>
	<td>
	<div class="subItems" style="margin-top: 0px; width: 725px;">
	<div id="editIterationForm" class="validateWrapper validateIteration">
	<ww:form method="post" id="iterationEditForm" action="storeIteration">
		<ww:hidden name="iterationId" value="${iteration.id}" />
		<ww:date name="%{iteration.getTimeOfDayDate(6)}" id="start"
			format="%{getText('webwork.shortDateTime.format')}" />
		<ww:date name="%{iteration.getTimeOfDayDate(18)}" id="end"
			format="%{getText('webwork.shortDateTime.format')}" />
		<c:if test="${iteration.id > 0}">
			<ww:date name="%{iteration.startDate}" id="start"
				format="%{getText('webwork.shortDateTime.format')}" />
			<ww:date name="%{iteration.endDate}" id="end"
				format="%{getText('webwork.shortDateTime.format')}" />
		</c:if>

		<table class="formTable">
		<tr>
			<td>Name</td>
			<td>*</td>
			<td colspan="2"><ww:textfield size="60" name="iteration.name" /></td>
		</tr>
		<tr>
			<td>Description</td>
			<td></td>
			<td colspan="2"><ww:textarea cols="70" rows="10" cssClass="useWysiwyg"
				id="iterationDescription" name="iteration.description"
				value="${aef:nl2br(iteration.description)}" /></td>
		</tr>
		<tr>
			<td>Project</td>
			<td>*</td>
			<td colspan="2">
			<select name="projectId">
				<option class="inactive" value="">(select project)</option>
				<c:forEach items="${productList}" var="product">
					<option value="" class="inactive productOption">${aef:out(product.name)}</option>
					<c:forEach items="${product.projects}" var="project">
						<c:choose>
							<c:when test="${project.id == currentProjectId}">
								<option selected="selected" value="${project.id}"
									class="projectOption" title="${project.name}">${aef:out(project.name)}</option>
							</c:when>
							<c:otherwise>
								<option value="${project.id}" title="${project.name}"
									class="projectOption">${aef:out(project.name)}</option>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</c:forEach>
			</select>
			</td>
			</tr>
			<tr>
				<td>Planned iteration size</td>
				<td></td>
				<td colspan="2">
					<ww:textfield size="10" id="iteration.backlogSize" name="iteration.backlogSize" /> (total man hours)
				</td>
			</tr>
			<tr>
				<td>Start date</td>
				<td>*</td>
				<td colspan="2">					
					<aef:datepicker id="start_date_${iteration.id}" name="startDate"
						format="%{getText('webwork.shortDateTime.format')}"
						value="%{#start}" />
				</td>
			</tr>
			<tr>
				<td>End date</td>
				<td>*</td>
				<td colspan="2">
					<aef:datepicker id="end_date_${iteration.id}" name="endDate"
						format="%{getText('webwork.shortDateTime.format')}"
						value="%{#end}" />
				</td>
			</tr>
			<tr>
				<td></td>
				<td></td>
				<td><ww:submit value="Save" id="saveButton" /></td>
				<td class="deleteButton"><ww:submit action="deleteIteration" value="Delete" />
				<ww:reset value="Cancel" />
				</td>								
			</tr>
		</table>
		</ww:form>
		</div>
</div>
</td>
</tr>
</tbody>
</table>


</div>

<div id="iterationThemesTab-${iterationId}" class="iterationNaviTab">

<script type="text/javascript">
$(document).ready( function() {
    
	var projectThemes = [];
    <c:if test="${!empty iteration.project.businessThemeBindings}">
	    projectThemes = [<c:forEach items="${iteration.project.businessThemeBindings}" var="them">${them.businessTheme.id},</c:forEach>-1];
	</c:if>
	var getThemeData = function() {
		var ret = {};
		var data = jsonDataCache.get('themesByProduct',{data: {productId: ${iteration.project.product.id}, includeGlobalThemes: true}},${iteration.project.product.id});
		jQuery.each(data,function() {
			if(this.active === true) {
				if(jQuery.inArray(this.id,projectThemes) > -1) {
					ret[this.id] = this.name+" *";
				} else {
					ret[this.id] = this.name;
				}
			}
		});
		return ret;
	};
   $("#iterationBusinessThemesForm_${iteration.id}").submit(function() {
   		var themes = getThemeData();
   		var moved = [];
   		$("#iterationBusinessThemesForm_${iteration.id}").find("select[name=businessThemeIds]").each(function() {
   			var cTheme = parseInt($(this).val());
   			var t = jQuery.inArray(cTheme,projectThemes);
   			if(t > -1) {
   				 moved.push(themes[cTheme].substring(0,themes[cTheme].length-1));
   			}
   		});
   		if(moved.length > 0) {
   			var message = "Theme(s) " + (moved.join(", ")) + " will be moved from project to this iteration.";
   			message += "\nDo your wish to continue?\n";
   			return confirm(message);   		
   		}
   		return true;
   });
	$('#businessThemeTable_${iteration.id}').inlineTableEdit({add: '#addIterationBusinessTheme_${iteration.id}', 
		submit: '#backlogThemeSave_${iteration.id}',
		submitParam: 'bindingId',
		deleteaction: 'removeThemeFromBacklog.action',
		fields: {
			businessThemeIds: {cell: 0,type: 'select', data: getThemeData},
			plannedSpendings: {cell: 1, type: 'text' },
			reset: {cell: 3, type: 'reset'}
		}
	});
											  
});

</script>



<table>
	<tr>
		<td>
			<c:if test="${iterationId != 0}">
				<div class="subItems validateWrapper validateEmpty" style="margin-top: 0; margin-left: 3px; width: 710px;">
					<a id="addIterationBusinessTheme_${iteration.id}" href="#">Attach theme &raquo;</a>
					
					<ww:form action="storeBacklogThemebinding" id="iterationBusinessThemesForm_${iteration.id}" method="post">
					<ww:hidden name="backlogId" value="${iteration.id}"/>
					<input type="hidden" name="contextViewName" value="project" />
					<p>
					<c:choose>
					<c:when test="${!empty iteration.businessThemeBindings}">
						<display:table htmlId="businessThemeTable_${iteration.id}" class="listTable" name="iteration.businessThemeBindings" id="row" requestURI="editIteration.action">

							<display:column sortable="false" title="Name" sortProperty="businessTheme.name">
								<span style="display: none;">${row.businessTheme.id}</span>
								<c:out value="${row.businessTheme.name}"/>
							</display:column>
							
							<display:column sortable="false" sortProperty="boundEffort" title="Planned spending">
								<c:choose>
									<c:when test="${row.relativeBinding == true}">
										<span style="display:none;">${row.percentage}</span>
										<c:out value="${row.boundEffort}"/>
										(<c:out value="${row.percentage}"/>%)
									</c:when>
									<c:otherwise><c:out value="${row.fixedSize}"/></c:otherwise>
								</c:choose>
							</display:column>
							<display:column sortable="false" sortProperty="businessTheme.metrics.donePercentage" title="Progress">
								${row.businessTheme.metrics.donePercentage} (${row.businessTheme.metrics.numberOfDoneBlis} / ${row.businessTheme.metrics.numberOfBlis})
							</display:column>
							<display:column sortable="false" title="Actions">
								<span class="uniqueId" style="display: none;">${row.id}</span>
								<img style="cursor: pointer;" class="table_edit_edit" src="static/img/edit.png" title="Edit" />
								<img style="cursor: pointer;" class="table_edit_delete" src="static/img/delete_18.png" title="Delete" />
							</display:column>
						</display:table>
						</c:when>
						<c:otherwise>
							<table id="businessThemeTable_${iteration.id}" style="display:none;" class="listTable">
								<tr><th class="sortable">Name</th><th class="sortable">Planned spending</th><th>Progress</th><th>Actions</th></tr>
							</table>
						</c:otherwise>
						</c:choose>
						</p>
						<input id="backlogThemeSave_${iteration.id}" style="display: none"; type="submit" value="Save" />
						</ww:form>				
						</div>
				</div>
			</c:if>
		</td>
	</tr>
</table>

</div>

<div id="iterationGoalsTab-${iterationId}" class="iterationNaviTab">





<table>
	<tr>
		<td>
		<div class="subItems" validateWrapper
			validateEmpty" style="margin-top: 0; margin-left: 3px; width: 710px;">
		<ww:url id="createIterationGoalLink" action="ajaxCreateIterationGoal"
			includeParams="none">
			<ww:param name="iterationId" value="${iteration.id}" />
		</ww:url>
		<ww:a cssClass="openCreateDialog openIterationGoalDialog" onclick="return false;"
			href="%{createIterationGoalLink}">
			Create a new story &raquo;
		</ww:a>
		<c:if test="${!empty iteration.iterationGoals}">
			<aef:hourReporting id="hourReport"></aef:hourReporting>
			<c:if test="${hourReport}">
				<aef:backlogHourEntrySums id="iterationGoalEffortSpent"
					groupBy="IterationGoal" target="${iteration}" />
			</c:if>
			<div class="subItemContent">
			<p><display:table class="listTable"
				name="iteration.iterationGoals" id="row"
				requestURI="editIteration.action">

				<display:column sortable="false" title="Name" sortProperty="name"
					class="iterationGoalNameColumn">					
					${aef:html(row.name)}
				</display:column>

				<display:column sortable="false" sortProperty="description"
					title="Description">
					${aef:html(row.description)}
				</display:column>
				
				<display:column sortable="false" title="# of tasks">
				  ${aef:html(fn:length(row.stories))}
				</display:column>

				<display:column sortable="false" title="Effort left sum">
					<c:out value="${iterationGoalEffLeftSums[row.id]}" />
				</display:column>

				<display:column sortable="false" title="Original estimate sum">
					<c:out value="${iterationGoalOrigEstSums[row.id]}" />
				</display:column>

				<c:if test="${hourReport}">
					<display:column sortable="false" title="Effort Spent">
						<c:choose>
							<c:when test="${iterationGoalEffortSpent[row.id] != null}">
								<c:out value="${iterationGoalEffortSpent[row.id]}" />
							</c:when>
							<c:otherwise>&mdash;</c:otherwise>
						</c:choose>
					</display:column>
				</c:if>

				<display:column sortable="false" title="Actions"
					class="actionColumn">
					<ww:url id="deleteLink" action="deleteIterationGoal"
						includeParams="none">
						<ww:param name="iterationGoalId" value="${row.id}" />						
					</ww:url>
					<ww:a href="%{deleteLink}&contextViewName=${currentAction}&contextObjectId=${iteration.project.id}" onclick="return confirmDelete()">
						<img src="static/img/delete_18.png" alt="Delete" title="Delete" />
					</ww:a>					
				</display:column>

			</display:table>
			</p>
			</div>
		</c:if>
		</div>
		</td>   
	</tr>
</table>
</div>

</div>