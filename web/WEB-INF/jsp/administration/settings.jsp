<%@ include file="../inc/_taglibs.jsp"%>
<%@ include file="../inc/_header.jsp"%>

<aef:menu navi="administration" subnavi="settings" title="Settings"/>
<aef:existingObjects />

<h2>Timesheet settings</h2>

<ww:form action="storeSettings.action" method="post">
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
	<ww:checkbox name="hourReportingEnabled" fieldValue="true" value="%{hourReportingEnabled}"></ww:checkbox>		
</td>
</tr>
</tbody>
</table>

<h2>Load meter settings</h2>

<p>To restore default threshold, just leave the field empty.</p>

<table id="row">
<thead>
<tr>
<th>Load</th>
<th>Threshold</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td>Minimum</td>
<td colspan="2"><ww:textfield name="rangeLow" id="minimumField" value="%{rangeLow}" size="4" /> %</td>
</tr>
<tr class="even">
<td>Optimal Low</td>
<td><input type="text" name="optimalLow" id="optimalLowField" value="%{optimalLow}" size="4" /> %</td>
</tr>
<tr class="odd">
<td>Optimal High</td>
<td><input type="text" name="optimalHigh" id="optimalHighField" value="%{optimalHigh}" size="4" /> %</td>
</tr>
<tr class="even">
<td>Critical</td>
<td><input type="text" name="criticalLow" id="criticalLowField" value="%{criticalLow}" size="4" /> %</td>
</tr>
<tr class="odd">
<td>Maximum</td>
<td><input type="text" name="rangeHigh" id="maximumField" value="%{rangeHigh}" size="4" /> %</td>
</tr>
</tbody>
</table>


<ww:submit value="Save"></ww:submit>
</ww:form>

<%@ include file="../inc/_footer.jsp"%>