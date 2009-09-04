<%@ include file="../inc/_taglibs.jsp"%>

<struct:htmlWrapper navi="settings">

<jsp:attribute name="menuContent">
  <struct:settingsMenu />
</jsp:attribute>

<jsp:body>
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
<td colspan="2"><ww:textfield name="rangeLow" id="minimumField" size="4" /> %</td>
</tr>
<tr class="even">
<td>Optimal Low</td>
<td><ww:textfield name="optimalLow" id="optimalLowField" size="4" /> %</td>
</tr>
<tr class="odd">
<td>Optimal High</td>
<td><ww:textfield name="optimalHigh" id="optimalHighField" size="4" /> %</td>
</tr>
<tr class="even">
<td>Critical</td>
<td><ww:textfield name="criticalLow" id="criticalLowField" size="4" /> %</td>
</tr>
<tr class="odd">
<td>Maximum</td>
<td><ww:textfield name="rangeHigh" id="maximumField" size="4" /> %</td>
</tr>
</tbody>
</table>


<ww:submit value="Save"></ww:submit>
</ww:form>

</jsp:body>
</struct:htmlWrapper>