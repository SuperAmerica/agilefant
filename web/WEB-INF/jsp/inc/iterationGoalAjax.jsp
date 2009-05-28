<%@ include file="_taglibs.jsp"%>

<aef:hourReporting id="hourReport"/>

<c:if test="${hourReport}">
	<c:set var="totalSum" value="${null}" />
</c:if>

<aef:productList />

<ww:actionerror />
<ww:actionmessage />

<div class="ajaxWindowTabsDiv">
<ul class="ajaxWindowTabs">
	<li><a href="#iterationGoalEditTab-${iterationGoalId}"><span><img src="static/img/edit.png" alt="Edit" /> Edit iteration goal</span></a></li>
	<li><a href="#iterationGoalBliTab-${iterationGoalId}"><span><img src="static/img/bli2.png" alt="Backlog items" /> Backlog items</span></a></li>
</ul>

<div id="iterationGoalEditTab-${iterationGoalId}" class="iterationNaviTab">

<script type="text/javascript">
$(document).ready(function() {
    var editForm = $('#iterationGoalEditForm_${iterationGoal.id}');
    editForm.validate(agilefantValidationRules.iterationGoal);
    editForm.submit(function() { return $(this).valid(); });
});
</script>

<table>
<tbody>
	<tr>
	<td>
	<div class="subItems" style="margin-top: 0px; width: 720px;">
	<div id="editIterationForm" class="validateWrapper validateIterationGoal">
<ww:form id="iterationGoalEditForm_${iterationGoal.id}" action="ajaxStoreIterationGoal"  method="post">
	<ww:hidden name="iterationGoalId" value="${iterationGoal.id}" />
	<table class="formTable">
		<tr>
			<td><ww:text name="general.uniqueId"/></td>
			<td></td>
			<td colspan="2"><aef:quickReference item="${iterationGoal}" /></td>
		</tr>
		<tr>
			<td>Name</td>
			<td>*</td>
			<td colspan="2"><ww:textfield size="60"
				name="iterationGoal.name" /></td>
		</tr>
		<tr>
			<td>Description</td>
			<td></td>
			<td colspan="2"><ww:textarea cols="70" rows="10" cssClass="useWysiwyg" 
				name="iterationGoal.description" /></td>
		</tr>
		<tr>
			<td>Iteration</td>
			<td></td>
			<td colspan="2">
			<c:choose>
				<c:when test="${iterationGoalId == 0}">
					<select name="iterationId">
				</c:when>
				<c:otherwise>
					<select name="iterationId">
				</c:otherwise>
			</c:choose>
				<option value="" class="inactive">(select iteration)</option>
				<c:forEach items="${productList}" var="product">
					<option value="" class="inactive productOption">${product.name}</option>
					<c:forEach items="${product.projects}" var="project">
						<option value="" class="inactive projectOption">${project.name}</option>
						<c:forEach items="${project.iterations}" var="iter">
							<c:choose>
								<c:when test="${iter.id == currentIterationId}">
									<option selected="selected" value="${iter.id}" class="iterationOption">${iter.name}</option>
								</c:when>
								<c:otherwise>
									<option value="${iter.id}" class="iterationOption">${iter.name}</option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</c:forEach>
				</c:forEach>
			</select></td>
		</tr>
		<tr>
			<td></td>
			<td></td>
			<td>
                <ww:submit value="Save" id="saveButton"/>
                <ww:submit name="SaveClose" value="Save & Close" id="saveClose"  />
			</td>
            <td class="deleteButton">
                <ww:submit action="deleteIterationGoal" value="Delete" />
                <ww:reset value="Cancel"/>
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

<div id="iterationGoalBliTab-${iterationGoalId}" class="iterationNaviTab">

<ww:url id="createBLILink" action="ajaxCreateBacklogItem" includeParams="none">
    <ww:param name="backlogId">${iterationGoal.iteration.id}</ww:param>
    <ww:param name="iterationGoalId">${iterationGoalId}</ww:param>
</ww:url>

<ww:a href="%{createBLILink}" cssClass="openCreateDialog openBacklogItemDialog" onclick="return false">
    Create new &raquo;
</ww:a>

<c:choose>
<c:when test="${!(empty iterationGoal.backlogItems)}" >
<display:table class="listTable"
	name="iterationGoal.backlogItems" id="row" requestURI="editIterationGoal.action" defaultsort="4"
	defaultorder="descending">					

	<display:column title="Name" sortable="false" sortProperty="name" class="shortNameColumn">												
		<c:forEach items="${bliThemeCache[row]}" var="businessTheme">
            		<c:choose>
            			<c:when test="${businessTheme.global}">
		            		<span class="businessTheme globalThemeColors" title="${businessTheme.description}"><c:out value="${businessTheme.name}"/></span>            			
            			</c:when>
            			<c:otherwise>
		            		<span class="businessTheme" title="${businessTheme.description}"><c:out value="${businessTheme.name}"/></span>    				
            			</c:otherwise>
            		</c:choose>
            </c:forEach>
		<%-- Link to go to the bli in the iteration page. --%>												
		<ww:a href="qr.action?id=BLI:${row.id}">
			${aef:html(row.name)}
		</ww:a>						
	</display:column>

	<display:column title="Responsibles" sortable="false" class="responsibleColumn">
		<div><aef:responsibleColumn backlogItemId="${row.id}" /></div>
	</display:column>

	<display:column title="Priority" sortable="false" defaultorder="descending">
		<ww:text name="backlogItem.priority.${row.priority}" />
	</display:column>

	<display:column title="Progress" sortable="false" class="todoColumn">
		<aef:backlogItemProgressBar backlogItem="${row}" bliListContext="${bliListContext}" dialogContext="${dialogContext}" hasLink="${false}"/>		
	</display:column>

	<display:column title="Effort Left<br/>" sortable="false" sortProperty="effortLeft"
		defaultorder="descending" >
		<span style="white-space: nowrap">
		<c:choose>
			<c:when test="${row.effortLeft == null}">&mdash;</c:when>
		<c:otherwise>${row.effortLeft}</c:otherwise>
		</c:choose>
		</span>
	</display:column>

	<display:column title="Original Estimate<br/>" sortable="false" sortProperty="originalEstimate"
		defaultorder="descending">
		<span style="white-space: nowrap">
		<c:choose>
		<c:when test="${row.originalEstimate == null}">&mdash;</c:when>
		<c:otherwise>${row.originalEstimate}</c:otherwise>
		</c:choose> </span>
	</display:column>
					
	<c:choose>
		<c:when test="${hourReport}">
			<display:column title="Effort Spent" sortable="false" sortProperty="effortSpent" defaultorder="descending">
				<span style="white-space: nowrap">									
				<c:choose>
					<c:when test="${row.effortSpent == null}">&mdash;</c:when>
					<c:otherwise>
						<c:out value="${row.effortSpent}" />
						<c:set var="totalSum" value="${aef:calculateAFTimeSum(totalSum, row.effortSpent)}" />
					</c:otherwise>
				</c:choose>
				</span>
			</display:column>
		</c:when>
		<c:otherwise>			
		</c:otherwise>
	</c:choose>
	
	<display:footer>
	<tr>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>						
						
		<%-- Effort left --%>
		<td><c:out value="${effortLeftSum}" /></td>
		<%-- Original estimate --%>
		<td><c:out value="${originalEstimateSum}" /></td>
		<c:if test="${hourReport}">
			<td>
				<c:choose>
					<c:when test="${totalSum != null}">
						<c:out value="${totalSum}" />
					</c:when>
					<c:otherwise>
						0h
					</c:otherwise>
				</c:choose>
			</td>
		</c:if>
	</tr>
	</display:footer>
</display:table>

</c:when>
<c:otherwise>
No backlog items.
</c:otherwise>
</c:choose>

</div>

</div>