<%@ include file="./inc/_taglibs.jsp"%>
<%@ include file="./inc/_header.jsp"%>

<aef:menu navi="settings" pageHierarchy="${pageHierarchy}" />
<aef:existingObjects />

<ww:form action="storeHourReporting.action">
<%--
<ww:hidden name="name" value="HourReporting"/>
<ww:hidden name="description" value="Description"/>
--%>
<h2>Settings</h2>
<table frame="border" rules="all">
<tr>
<th>Name</th><th>Value</th><th>Description</th>
</tr>
<tr>
	<td>Time sheet functionality</td>

	<td>
		<aef:hourReporting id="hourReport"></aef:hourReporting>
		<ww:checkbox name="value" fieldValue="true" value="${hourReport}">
		</ww:checkbox>
	</td>

	<td>Hour reporting enabled</td>

</tr>
</table>
<ww:submit value="Save"></ww:submit>
</ww:form>

<%@ include file="./inc/_footer.jsp"%>