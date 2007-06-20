<%@ include file="../jsp/inc/_taglibs.jsp" %>

   <%@tag description = "Deliverable list" %>

   <%@attribute name="deliverableId"%>

		<ww:form action="contextView">
		<ww:hidden name="contextName" value="project"/> 
			<p>
			<aef:deliverableList id="deliverableList"/> 
			
				<select name="contextObjectId">
							<c:forEach items="${deliverableList}" var="deliverable">
								<c:choose>
									<c:when test="${deliverableId == deliverable.id}">
										<option selected="selected" value="${deliverable.id}" title="${deliverable.name}">${aef:out(deliverable.name)}</option>
									</c:when>
									<c:otherwise>
										<option value="${deliverable.id}" title="${deliverable.name}">${aef:out(deliverable.name)}</option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
				</select>
			<ww:submit value="Select project"/>
			</p>			
		</ww:form>
				