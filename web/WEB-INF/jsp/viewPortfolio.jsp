<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:menu navi="${contextName}" /> 

	<ww:form action="viewPortfolio">
		<ww:hidden name="deliverableId" value="${deliverable.id}"/>
		<ww:hidden name="productId"/>

<ww:date name="%{new java.util.Date()}" id="start"/>
<ww:date name="%{new java.util.Date()}" id="end"/>
		

<c:if test="${!empty startDate}">
<ww:date name="${startDate}" id="start"/>
</c:if>

<c:if test="${!empty endDate}">
<ww:date name="${startDate}" id="end"/>
</c:if>

Startdate: 
<ww:datepicker value="%{#start}" size="10" showstime="%{true}"  format="%{getText('webwork.datepicker.format')}" name="startDate" /> 
- Enddate: 
<ww:datepicker value="%{#end}" size="10" showstime="%{true}"  format="%{getText('webwork.datepicker.format')}" name="endDate" /> 

<%-- 
			<ww:submit value="Select timescale"/>
			
--%>			
		</ww:form>


<p>
        <img src="drawGantChart.action?startDate=${startDate}&endDate=${endDate}"/>
</p>

<%@ include file="./inc/_footer.jsp" %>
