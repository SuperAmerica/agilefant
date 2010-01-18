<%@ include file="../inc/_taglibs.jsp"%>

<struct:htmlWrapper navi="settings">

<jsp:body>

<h2>System-wide settings</h2>

<div style="width: 30em; border: 1px solid rgb(240, 192, 0); background: rgb(255, 255, 206); padding: 1em; margin: 0.3em;"> 
  <strong>Note!</strong> Modifying these settings will affect all users.
</div>


<ww:form action="storeSettings.action" method="post">

<ww:checkbox name="hourReportingEnabled" fieldValue="true" value="%{hourReportingEnabled}"></ww:checkbox> Enable timesheets 

<h3>Load meter settings</h3>

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