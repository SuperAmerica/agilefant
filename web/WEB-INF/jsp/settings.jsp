<%@ include file="./inc/_taglibs.jsp"%>
<%@ include file="./inc/_header.jsp"%>

<aef:menu navi="settings" pageHierarchy="${pageHierarchy}" />
<aef:existingObjects />

<h2>Settings</h2>


<ww:form action="storeSettings.action">
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
	<ww:checkbox name="value" fieldValue="true" value="${hourReport}"></ww:checkbox>		
</td>
</tr>
</tbody>
</table>

<p>To restore default threshold, just leave the field empty.</p>

<table id="row">
<thead>
<tr>
<th>Load</th>
<th>Threshold</th>
</thead>
<tbody>
<tr class="odd">
<td>Minimum</td>
<td colspan="2"><ww:textfield name="rangeLowValue" id="minimumField" value="${settingBusiness.rangeLow}" size="4" /> %</td>
</tr>
<tr class="even">
<td>Optimal Low</td>
<td><input type="text" name="optimalLowValue" id="optimalLowField" value="${settingBusiness.optimalLow}" size="4" /> %</td>
</tr>
<tr class="odd">
<td>Optimal High</td>
<td><input type="text" name="optimalHighValue" id="optimalHighField" value="${settingBusiness.optimalHigh}" size="4" /> %</td>
</tr>
<tr class="even">
<td>Critical</td>
<td><input type="text" name="criticalLowValue" id="criticalLowField" value="${settingBusiness.criticalLow}" size="4" /> %</td>
</tr>
<tr class="odd">
<td>Maximum</td>
<td><input type="text" name="rangeHighValue" id="maximumField" value="${settingBusiness.rangeHigh}" size="4" /> %</td>
</tr>
</tr>
</tbody>
</table>

<ww:submit value="Save"></ww:submit>
</ww:form>

<%@ include file="./inc/_footer.jsp"%>