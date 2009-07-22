<%@ include file="./inc/_taglibs.jsp"%>
<%@ include file="./inc/_header.jsp"%>

<aef:currentBacklog backlogId="${iteration.id}"/>

<c:set var="divId" value="1336" scope="page" />
<aef:menu navi="backlog" title="${iteration.name}" menuContextId="${iteration.id}"/>
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
				<div id="descriptionDiv" class="descriptionDiv">
				<table class="infoTable" cellpadding="0" cellspacing="0">
					<%-- TODO: Add support for charts/metrics --%>
					<tr>
						<th class="info1"><ww:text name="general.uniqueId"/></th>
						<td class="info3"><aef:quickReference item="${iteration}" /></td>
						
						<td class="info4" rowspan="5">
						            
                        <div class="smallBurndown"><a href="#bigChart"><img id="smallChart" 
                            src="drawSmallIterationBurndown.action?backlogId=${iteration.id}" /></a></div>
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
						<td colspan="2" class="description">
							<div class="backlogDescription">${iteration.description}</div>
						</td>
						<td></td>
					</tr>

				</table>
				</div>
				<div id="editIterationForm" class="validateWrapper validateIteration" style="display: none;">
				<form	method="post" id="iterationEditForm" action="ajax/storeIteration.action">
					<ww:hidden name="iterationId" value="#attr.iteration.id" />
					<ww:date name="%{iteration.getTimeOfDayDate(6)}" id="start"
						format="%{getText('struts.shortDateTime.format')}" />
					<ww:date name="%{iteration.getTimeOfDayDate(18)}" id="end"
						format="%{getText('struts.shortDateTime.format')}" />
					<c:if test="${iteration.id > 0}">
						<ww:date name="%{iteration.startDate}" id="start"
							format="%{getText('struts.shortDateTime.format')}" />
						<ww:date name="%{iteration.endDate}" id="end"
							format="%{getText('struts.shortDateTime.format')}" />
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
								id="iterationDescription" name="iteration.description">${aef:nl2br(iteration.description)} </ww:textarea></td>
						</tr>
						<tr>
							<td>Project</td>
							<td>*</td>
							<td colspan="2"><select name="projectId">
								<option class="inactive" value="">(select project)</option>
								<c:forEach items="${productList}" var="product">
									<option value="" class="inactive productOption">${aef:out(product.name)}</option>
									<c:forEach items="${product.children}" var="project">
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
							<td colspan="2"> <aef:datepicker id="start_date"
								name="startDate"
								format="%{getText('struts.shortDateTime.format')}"
								value="${start}" /></td>
						</tr>
						<tr>
							<td>End date</td>
							<td>*</td>
							<td colspan="2"> <aef:datepicker id="end_date"
								name="endDate"
								format="%{getText('struts.shortDateTime.format')}"
								value="${end}" /></td>
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
									<td class="deleteButton"><input type="button"  class="undisableMe"
										onClick="javascript:deleteIteration(${iterationId},${projectId})" value="Delete" /></td>
								</c:otherwise>
							</c:choose>
						</tr>
					</table>
				</form></div>
				</div>
				</div>
				</td>
			</tr>
		</tbody>
	</table>
<script type="text/javascript" src="static/js/dynamics/view/DynamicView.js"></script>
<script type="text/javascript" src="static/js/dynamics/view/Table.js"></script>
<script type="text/javascript" src="static/js/dynamics/view/Row.js"></script>
<script type="text/javascript" src="static/js/dynamics/view/Cell.js"></script>
<script type="text/javascript" src="static/js/dynamics/view/RowActions.js"></script>
<script type="text/javascript" src="static/js/dynamics/view/TableConfiguration.js"></script>
<script type="text/javascript" src="static/js/dynamics/view/Toggle.js"></script>

<script type="text/javascript" src="static/js/dynamics/model/CommonModel.js"></script>
<script type="text/javascript" src="static/js/dynamics/model/BacklogModel.js"></script>
<script type="text/javascript" src="static/js/dynamics/model/IterationModel.js"></script>
<script type="text/javascript" src="static/js/dynamics/model/StoryModel.js"></script>
<script type="text/javascript" src="static/js/dynamics/model/TaskModel.js"></script>
<script type="text/javascript" src="static/js/dynamics/model/UserModel.js"></script>
<script type="text/javascript" src="static/js/dynamics/model/ModelFactory.js"></script>

<script type="text/javascript" src="static/js/dynamics/controller/CommonController.js"></script>
<script type="text/javascript" src="static/js/dynamics/controller/BacklogController.js"></script>
<script type="text/javascript" src="static/js/dynamics/controller/IterationController.js"></script>
<script type="text/javascript" src="static/js/dynamics/controller/StoryController.js"></script>

<script type="text/javascript" src="static/js/dynamics/Dynamics.events.js"></script>

<script type="text/javascript" src="static/js/utils/ArrayUtils.js"></script>
<script type="text/javascript" src="static/js/utils/Configuration.js"></script>


<form onsubmit="return false;"><div id="stories" style="min-width: 800px; width: 98%;">&nbsp;</div></form>
<script type="text/javascript">
$(document).ready(function() {
  new IterationController(${iteration.id}, $('#stories'));
});
</script>


<p><img src="drawIterationBurndown.action?backlogId=${iteration.id}"
	id="bigChart" width="780" height="600" /></p>

	<%@ include file="./inc/_footer.jsp"%>