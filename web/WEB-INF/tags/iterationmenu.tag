<%@ include file="../jsp/inc/_taglibs.jsp" %>

   <%@tag description = "Iteration list" %>

   <%@attribute name="iterationId"%>

		<ww:form action="contextView">
		<ww:hidden name="contextName" value="iteration"/> 
			<p>
			<aef:iterationList id="iterationList"/> 
			
				<select name="contextObjectId">
							<c:forEach items="${iterationList}" var="iter">
								<c:choose>
									<c:when test="${iterationId == iter.id}">
										<option selected="selected" value="${iter.id}" title="iter.name">${aef:out(iter.name)}</option>
									</c:when>
									<c:otherwise>
										<option value="${iter.id}" title="iter.name">${aef:out(iter.name)}</option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
				</select>
			<ww:submit value="Select iteration"/>
			</p>			
		</ww:form>
				

