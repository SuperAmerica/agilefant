<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:menu navi="activityTypes" pageHierarchy="${pageHierarchy}"/>

<!-- <a href="contextView.action?contextName=portfolio&resetContextView=true">Portfolio</a> | Activity types
-->

	<h2>Project types </h2>
	<p>
		<ww:url id="createActivityTypeLink" action="createActivityType"/>
		<ww:a href="%{createActivityTypeLink}">Create new &raquo;</ww:a>
	</p>

	<p>
		<c:choose>
			<c:when test="${empty activityTypes}">
				No project types were found.
			</c:when>
			<c:otherwise>
		<display:table class="listTable" name="${activityTypes}" id="row" requestURI="listActivityTypes.action">
			<display:column sortable="true" property="id"/>
			<display:column sortable="true" title="Name">
				<ww:url id="editLink" action="editActivityType">
					<ww:param name="activityTypeId" value="${row.id}"/>
				</ww:url>
				<ww:a href="%{editLink}">
					${aef:html(row.name)}
				</ww:a>
			</display:column>
			<display:column sortable="false" title="Actions">			
					<!-- <ww:url id="editLink" action="editActivityType">
						<ww:param name="activityTypeId" value="${row.id}"/>
					</ww:url>-->
					<ww:url id="deleteLink" action="deleteActivityType">
						<ww:param name="activityTypeId" value="${row.id}"/>
					</ww:url>
					<!-- <ww:a href="%{editLink}">Edit</ww:a>|-->
					<ww:a href="%{deleteLink}">Delete</ww:a>
			</display:column>
		</display:table>
				
			</c:otherwise>
		</c:choose>			
	</p>
<%@ include file="./inc/_footer.jsp" %>
