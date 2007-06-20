<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:menu navi="${contextName}" /> 

Portfolio | <a href="listActivityTypes.action">Activity types</a>
<p>

	<ww:form action="viewPortfolio">
		<ww:hidden name="deliverableId" value="${deliverable.id}"/>
		<ww:hidden name="productId"/>

<ww:date name="%{new java.util.Date()}" id="start"/>
<ww:date name="%{new java.util.Date()}" id="end"/>
		

<c:if test="${empty startDate}">
<ww:date name="${startDate}" id="start"/>
</c:if>

<c:if test="${empty endDate}">
<ww:date name="${endDate}" id="end"/>
</c:if>
Startdate: 
<ww:datepicker value="%{startDate}" size="10" showstime="%{true}"  format="%{getText('webwork.datepicker.format')}" name="startDate" /> 
- Enddate: 
<ww:datepicker value="%{endDate}" size="10" showstime="%{true}"  format="%{getText('webwork.datepicker.format')}" name="endDate" /> 

			<ww:submit value="Select timescale"/>
			
		</ww:form>
</p>


				<ww:url id="imgUrl" action="drawGantChart" includeParams="none">
					<ww:param name="startDate" >
						<ww:date name="startDate" />					
					</ww:param>
					<ww:param name="endDate" >
						<ww:date name="endDate" />					
					</ww:param>
				</ww:url>
				


<p>
        <img src="${imgUrl}"/>
</p>

<%@ include file="./inc/_footer.jsp" %>
