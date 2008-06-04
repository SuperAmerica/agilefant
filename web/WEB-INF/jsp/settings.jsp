<%@ include file="./inc/_taglibs.jsp"%>
<%@ include file="./inc/_header.jsp"%>

<aef:menu navi="settings" pageHierarchy="${pageHierarchy}" />
<aef:existingObjects />

<h2>Settings</h2>


<ww:form action="storeHourReporting.action">
<%-- 
<ww:hidden name="name" value="HourReporting"/>
<ww:hidden name="description" value="Description"/>
--%>
<table id="row">
<thead>
<tr>
<th>Setting</th>
<th>Enabled</th>
</thead>
<tbody>
<tr class="odd">
<td>Timesheets</td>
<td>
	<aef:hourReporting id="hourReport"></aef:hourReporting>
	<ww:checkbox name="value" fieldValue="true" value="${hourReport}">
	</ww:checkbox>
</td>
</tr>
</tbody>
</table>
<ww:submit value="Save"></ww:submit>
</ww:form>

<%@ include file="./inc/_footer.jsp"%>