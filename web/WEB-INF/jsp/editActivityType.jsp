<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:menu /> 
	<ww:actionerror/>
	<ww:actionmessage/>
	<c:choose>
		<c:when test="${activityType.id == 0}">
			<h2>Create new activity type</h2>
		</c:when>
		<c:otherwise>
			<h2>Edit activity type: ${activityType.name}</h2>
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
		<td><ww:textfield name="activityType.name"/></td>	
		</tr>
		<tr>
		<td>Description</td>
		<td></td>
		<td><ww:textarea cols="40" rows="6" name="activityType.description" /></td>	
		</tr>
		<tr>
		<td></td>
		<td></td>
		<td><ww:submit value="Store"/>
			<ww:submit name="action:listActivityTypes" value="Cancel"/>
			</td>	
		</tr>
		</table>

	</ww:form>
	
	
	<c:if test="${activityType.id > 0}">
	
		<div id="subItems">
		<div id="subItemHeader">
			Subitems
		</div>
		<div id="subItemContent">
	
		<p>Work types 
			<ww:url id="createLink" action="createWorkType" includeParams="none">
				<ww:param name="activityTypeId" value="${activityType.id}"/>
			</ww:url>
			<ww:a href="%{createLink}">Create new &raquo;</ww:a>		
		</p>
		
		<display:table class="listTable" name="${activityType.workTypes}" id="row" requestURI="editActivityType.action?activityTypeId=${activityType.id}">
			<display:column sortable="true" property="id"/>
			<display:column sortable="true" property="name"/>
			<display:column sortable="false" title="Actions">			
				<ww:url id="editLink" action="editWorkType" includeParams="none">
					<ww:param name="workTypeId" value="${row.id}"/>
					<ww:param name="activityTypeId" value="${activityType.id}"/>
				</ww:url>
				<ww:url id="deleteLink" action="deleteWorkType" includeParams="none">
					<ww:param name="workTypeId" value="${row.id}"/>
					<ww:param name="activityTypeId" value="${activityType.id}"/>
				</ww:url>
				<ww:a href="%{editLink}">Edit</ww:a>|<ww:a href="%{deleteLink}">Delete</ww:a>
			</display:column>
		</display:table>
</div>
</div>

	</c:if>
<%@ include file="./inc/_footer.jsp" %>