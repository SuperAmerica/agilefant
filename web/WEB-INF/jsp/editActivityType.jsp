<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:menu navi="activityTypes" pageHierarchy="${pageHierarchy}"/>
	<ww:actionerror/>
	<ww:actionmessage/>
	<c:choose>
		<c:when test="${activityType.id == 0}">
			<h2>Create project type</h2>
		</c:when>
		<c:otherwise>
			<h2>Edit project type</h2>
		</c:otherwise>
	</c:choose>
	<ww:form action="storeActivityType">
		<ww:hidden name="activityTypeId" value="${activityType.id}"/>
		<table class="formTable">
		<tr>
		<td></td>
		<td></td>
		<td></td>	
		</tr>
		<tr>
		<td>Name</td>
		<td>*</td>
		<td><ww:textfield size="60" name="activityType.name"/></td>	
		</tr>
		<tr>
		<td>Description</td>
		<td></td>
		<td><ww:textarea cols="70" rows="10" name="activityType.description" /></td>	
		</tr>
		<tr>
		<td></td>
		<td></td>
		<td>
			<c:choose>
				<c:when test="${activityType.id == 0}">
					<ww:submit value="Create"/>
				</c:when>
				<c:otherwise>
				  <ww:submit value="Save"/>
 					<span class="deleteButton">
 						<ww:submit action="deleteActivityType" value="Delete"/>
 					</span>
				</c:otherwise>
			</c:choose>
		</td>	
		</tr>
		</table>

	</ww:form>
	
	
	<c:if test="${activityType.id > 0}">
	
	
<table><tr><td>
	
		<div id="subItems">
		<div id="subItemHeader">
			Work types
			<ww:url id="createLink" action="createWorkType" includeParams="none">
				<ww:param name="activityTypeId" value="${activityType.id}"/>
			</ww:url>
			<ww:a href="%{createLink}">Create new &raquo;</ww:a>		
		</div>
		<c:if test="${!empty activityType.workTypes}">
		<div id="subItemContent">
	
		<display:table class="listTable" name="${activityType.workTypes}" id="row" requestURI="editActivityType.action?activityTypeId=${activityType.id}">
			<display:column sortable="true" property="id"/>
			<display:column sortable="true" title="Name">
				<ww:url id="editLink" action="editWorkType" includeParams="none">
					<ww:param name="workTypeId" value="${row.id}"/>
					<ww:param name="activityTypeId" value="${activityType.id}"/>
				</ww:url>
				<ww:a href="%{editLink}">
					${aef:html(row.name)}
				</ww:a>
			</display:column>
			<display:column sortable="false" title="Actions">			
				<!-- <ww:url id="editLink" action="editWorkType" includeParams="none">
					<ww:param name="workTypeId" value="${row.id}"/>
					<ww:param name="activityTypeId" value="${activityType.id}"/>
				</ww:url> -->
				<ww:url id="deleteLink" action="deleteWorkType" includeParams="none">
					<ww:param name="workTypeId" value="${row.id}"/>
					<ww:param name="activityTypeId" value="${activityType.id}"/>
				</ww:url>
				<!--<ww:a href="%{editLink}">Edit</ww:a>|-->
				<ww:a href="%{deleteLink}">Delete</ww:a>
			</display:column>
		</display:table>
</div>
</c:if>
</div>
</td></tr></table>

	</c:if>
<%@ include file="./inc/_footer.jsp" %>