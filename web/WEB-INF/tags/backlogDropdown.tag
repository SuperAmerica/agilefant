<%@ include file="../jsp/inc/_taglibs.jsp" %>
<%@ tag description = 
		"This tag generates dropdown list of all available backlogs" %>

<!-- The HTTP-form name to use for the selected value -->
<%@ attribute name="selectName" %>

<!-- The id of the backlog that should be pre-selected -->
<%@ attribute name="preselectedBacklogId" %>

<!-- The name of the attribute containing the collection of backlog 
item containers (e.g. product) -->
<%@ attribute type="java.util.Collection" name="backlogs" %>

<select name="${selectName}">

	<!-- List products -->
	<c:forEach items="${backlogs}" var="product">
		
		<!-- Using two ifs instead of c:choose for clarity -->
		<c:if test="${product.id != preselectedBacklogId}">
			<option	value="${product.id}"	title="${product.name}" >
				${product.name}		
			</option>
		</c:if>
		
		<c:if test="${product.id == preselectedBacklogId}">
			<option	selected="selected" 
					value="${product.id}"	title="${product.name}" >
				${product.name}		
			</option>
		</c:if>
		
		<!-- List projects -->
		<c:forEach items="${product.deliverables}" var="project">	

			<c:if test="${project.id == preselectedBacklogId}">
				<option selected="selected" 
						value="${project.id}"	title="${project.name}" >
					&nbsp;&nbsp;&nbsp;&nbsp;${project.name}		
				</option>
			</c:if>
			
			<c:if test="${project.id != preselectedBacklogId}">
				<option value="${project.id}"	title="${project.name}" >
					&nbsp;&nbsp;&nbsp;&nbsp;${project.name}		
				</option>
			</c:if>
			
			<!-- List iterations -->
			<c:forEach items="${project.iterations}" var="iteration">
				
				<c:if test="${iteration.id == preselectedBacklogId}">
					<option	selected="selected" 
							value="${iteration.id}"	title="${iteration.name}" >
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${iteration.name}		
					</option>
				</c:if>
				
				<c:if test="${iteration.id != preselectedBacklogId}">
					<option	value="${iteration.id}"	title="${iteration.name}" >
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${iteration.name}		
					</option>
				</c:if>
				
				
			</c:forEach>
		</c:forEach>
	</c:forEach>
</select>