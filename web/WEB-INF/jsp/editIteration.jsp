<%@ include file="./inc/_taglibs.jsp"%>
<%@ include file="./inc/_header.jsp"%>

<aef:bct iterationId="${iterationId}" />

<aef:openDialogs context="iterationGoal" id="openIterationGoalTabs" />

<c:set var="divId" value="1336" scope="page" />
<aef:menu navi="backlog" pageHierarchy="${pageHierarchy}" title="${iteration.name}"/>
<aef:productList />

<ww:actionerror />
<ww:actionmessage />
<aef:hourReporting id="hourReporting" />
<script type="text/javascript">
    var agilefantTimesheetsEnabled = ${hourReporting};
</script>

<h2><c:out value="${iteration.name}" /></h2>
<table>
	<table>
		<tbody>
			<tr>
				<td>
				<div class="subItems" style="margin-top: 0" id="iterationDetailsDiv_editIteration">
				<div class="subItemHeader"><script type="text/javascript">
                function editIteration() {
                	toggleDiv('editIterationForm'); toggleDiv('descriptionDiv'); showWysiwyg('iterationDescription'); return false;
                }
                </script>

				<table cellspacing="0" cellpadding="0">
					<tr>
					    <td class="iconsbefore">
                            <div class="expand" title="Expand" onclick="toggleExpand(this, '#descriptionDiv');">
                            </div>
                        </td>
						<td class="header">Details</td>
						<td class="icons">
						<table cellspacing="0" cellpadding="0">
                            <tr>
                            <td>
						  <a href="#" title="Edit iteration details"
						  onclick="editIteration(); return false;"
						      class="editLink" />
						    </td>
						    </tr>
						    </table>      
						</td>
					</tr>
				</table>
				</div>
				<div class="subItemContent">
				<div id="descriptionDiv" class="descriptionDiv"
					style="display: block;">
				<table class="infoTable" cellpadding="0" cellspacing="0">
					<tr>
						<th class="info1"><ww:text name="general.uniqueId"/></th>
						<td class="info3"><aef:quickReference item="${iteration}" /></td>
						
						<td class="info4" rowspan="5">
                        <div class="smallBurndown"><a href="#bigChart"><img id="smallChart" 
                            src="drawSmallChart.action?iterationId=${iteration.id}" /></a></div>
                        <div id="iterationMetrics">
                          <%@ include file="./inc/iterationMetrics.jsp"%>
                        </div>
                        </td>					
					</tr>
					<tr>	
						<th class="info1">Planned iteration size</th>
						<td class="info3" ondblclick="return editIteration();">
							<c:choose>
							<c:when test="${(!empty iteration.backlogSize)}">
								<c:out value="${iteration.backlogSize}"/>h
							</c:when>
							<c:otherwise>
								-
							</c:otherwise>
							</c:choose>
						</td>
					    <td></td>			
					</tr>
					<tr>
						<th class="info1">Timeframe</th>
						<td class="info3" ondblclick="return editIteration();"><c:out
							value="${iteration.startDate.date}.${iteration.startDate.month + 1}.${iteration.startDate.year + 1900}" />
						- <c:out
							value="${iteration.endDate.date}.${iteration.endDate.month + 1}.${iteration.endDate.year + 1900}" /></td>
						
					</tr>

					<tr>
						<td colspan="2" class="description">${iteration.description}</td>
						<td></td>
					</tr>

				</table>
				</div>
				<div id="editIterationForm" class="validateWrapper validateIteration" style="display: none;">
				<ww:form
					method="post" id="iterationEditForm" action="storeIteration">
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
							<td colspan="2"><ww:textfield size="60"
								name="iteration.name" /></td>
						</tr>
						<tr>
							<td>Description</td>
							<td></td>
							<td colspan="2"><ww:textarea cols="70" rows="10"
								id="iterationDescription" name="iteration.description"
								value="${aef:nl2br(iteration.description)}" /></td>
						</tr>
						<tr>
							<td>Project</td>
							<td>*</td>
							<td colspan="2"><select name="projectId">
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
							</select></td>
						</tr>

                        <tr>
							<td>Planned iteration size</td>
							<td></td>
							<td colspan="2"><ww:textfield size="10" id="iteration.backlogSize" name="iteration.backlogSize" /> (total man hours)
							</td>
						</tr>
						<tr>
							<td>Start date</td>
							<td>*</td>
							<td colspan="2"><%--<ww:datepicker value="%{#start}" size="15"
										showstime="true"
										format="%{getText('webwork.datepicker.format')}"
										name="startDate" />--%> <aef:datepicker id="start_date"
								name="startDate"
								format="%{getText('webwork.shortDateTime.format')}"
								value="%{#start}" /></td>
						</tr>
						<tr>
							<td>End date</td>
							<td>*</td>
							<td colspan="2"><%--<ww:datepicker value="%{#end}" size="15"
										showstime="true"
										format="%{getText('webwork.datepicker.format')}"
										name="endDate" />--%> <aef:datepicker id="end_date"
								name="endDate"
								format="%{getText('webwork.shortDateTime.format')}"
								value="%{#end}" /></td>
						</tr>
						<tr>
							<td></td>
							<td></td>
							<c:choose>
								<c:when test="${iterationId == 0}">
									<td><ww:submit value="Create" disabled="disabled" cssClass="undisableMe"/></td>
								</c:when>
								<c:otherwise>
									<td><ww:submit value="Save" id="saveButton" /></td>
									<td class="deleteButton"><ww:submit disabled="disabled" cssClass="undisableMe"
										action="deleteIteration" value="Delete" /></td>
								</c:otherwise>
							</c:choose>
						</tr>
					</table>
				</ww:form></div>
				</div>
				</div>
				</td>
			</tr>
		</tbody>
	</table>


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
	
	$('#businessThemeTable').inlineTableEdit({add: '#addIterationBusinessTheme', 
											  submit: '#backlogThemeSave',
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
				<div class="subItems" id="subItems_editIterationThemesList">
					<div class="subItemHeader">
					    <table cellspacing="0" cellpadding="0">
            			    <tr>
            			    	<td class="header">Themes</td>
            			    	<td class="icons">
		                        <table cellspacing="0" cellpadding="0">
		                            <tr>
		                            <td>
			                          <a href="#" id="addIterationBusinessTheme" title="Attach theme"
			                          onclick="return false;" class="attachLink" />
		                            </td>
		                            </tr>
		                            </table>      
		                        </td>
							</tr>
						</table>
					</div>
					<div class="subItemContent">
					<div class="validateWrapper validateEmpty">
					<ww:form action="storeBacklogThemebinding" id="iterationBusinessThemesForm" method="post">
					<ww:hidden name="backlogId" value="${iteration.id}"/>
					<input type="hidden" name="contextViewName" value="iteration" />
					<c:choose>
					<c:when test="${!empty iteration.businessThemeBindings}">
						<display:table htmlId="businessThemeTable" class="listTable" name="iteration.businessThemeBindings" id="row" requestURI="editIteration.action">

							<display:column sortable="true" title="Name" sortProperty="businessTheme.name">
								<span style="display: none;">${row.businessTheme.id}</span>
								<a style="cursor: pointer; color: #0055AA;" class="table_edit_edit">
									<c:out value="${row.businessTheme.name}"/>
								</a>
							</display:column>
							
							<display:column sortable="true" sortProperty="boundEffort" title="Planned spending">
								<c:choose>
									<c:when test="${row.relativeBinding == true}">
										<span style="display:none;">${row.percentage}%</span>
										<c:out value="${row.boundEffort}"/>
										(<c:out value="${row.percentage}"/>%)
									</c:when>
									<c:otherwise><c:out value="${row.fixedSize}"/></c:otherwise>
								</c:choose>
							</display:column>
							<display:column sortable="true" sortProperty="businessTheme.metrics.donePercentage" title="Progress">
								${row.businessTheme.metrics.donePercentage}% (${row.businessTheme.metrics.numberOfDoneBlis} / ${row.businessTheme.metrics.numberOfBlis})
							</display:column>
							<display:column sortable="false" title="Actions">
								<span class="uniqueId" style="display: none;">${row.id}</span>
								<img style="cursor: pointer;" class="table_edit_edit" src="static/img/edit.png" title="Edit" />
								<img style="cursor: pointer;" class="table_edit_delete" src="static/img/delete_18.png" title="Delete" />
							</display:column>
						</display:table>
						</c:when>
						<c:otherwise>
							<table id="businessThemeTable" style="display:none;" class="listTable">
								<tr><th class="sortable">Name</th><th class="sortable">Planned spending</th><th>Progress</th><th>Actions</th></tr>
							</table>
						</c:otherwise>
						</c:choose>
						<div id="backlogThemeSave" style="display: none;">
						<label id="themeLabel" style="padding: 3px; margin: 3px; display:block; border: 1px solid #ccc;">
						    * = the theme has been attached to this project.<br/>
						    Planned spending may be entered as time (e.g. 2h 30min) or a percentage
                            (e.g. 40%).</label>
						<input id="backlogThemeSave" type="submit" value="Save"/>
						</div>
						</ww:form>				
						</div>
						</div>
				</div>
			</c:if>
		</td>
	</tr>
</table>
<script type="text/javascript" src="static/js/dynamics/utils.js?<ww:text name="webwork.agilefantReleaseId" />"></script>
<script type="text/javascript" src="static/js/dynamics/model.js?<ww:text name="webwork.agilefantReleaseId" />"></script>
<script type="text/javascript" src="static/js/dynamics/controller.js?<ww:text name="webwork.agilefantReleaseId" />"></script>
<script type="text/javascript" src="static/js/dynamics/dynamicTable.js?<ww:text name="webwork.agilefantReleaseId" />"></script>
<script type="text/javascript" src="static/js/dynamics/commonView.js?<ww:text name="webwork.agilefantReleaseId" />"></script>

<div id="iterationGoals" style="min-width: 800px;">&nbsp;</div>
<script type="text/javascript">
$(document).ready(function() {
  new iterationController(${iterationId}, $("#iterationGoals"));
  $(document.body).bind("metricsUpdated", function() {
	  var bigChart = $("#bigChart");
	  bigChart.attr("src",bigChart.attr("src")+"#");
    var smallChart = $("#smallChart");
    smallChart.attr("src",smallChart.attr("src")+"#");
    $("#iterationMetrics").load("iterationMetrics.action",{iterationId: ${iterationId}});
	});
});
</script>




<p><img src="drawChart.action?iterationId=${iteration.id}"
	id="bigChart" width="780" height="600" /></p>

	<%@ include file="./inc/_footer.jsp"%>