<%@ include file="../inc/_taglibs.jsp"%>

<struct:htmlWrapper navi="settings">

<jsp:body>

<h2>System settings</h2>

<div class="structure-main-block">
<div class="dynamictable ui-widget-content ui-corner-all">

  <div class="dynamictable-caption dynamictable-caption-block ui-widget-header ui-corner-all">
    System wide settings
  </div>

  <div style="margin: 1em 0.5em 0.5em;">
    
    <div style="width: 30em; border: 1px solid rgb(240, 192, 0); background: rgb(255, 255, 206); padding: 1em; margin: 0.3em;"> 
      <strong>Note!</strong> Modifying these settings will affect all users.
    </div>
  
    <ww:form action="storeSettings.action" method="post">
  
    <h3>Timesheets settings</h3>
  
    <table class="settings-table">
    <tr>
      <td><label for="hourReportingEnabled">Enable timesheets</label></td>
      <td><ww:checkbox name="hourReportingEnabled" fieldValue="true" value="%{hourReportingEnabled}"></ww:checkbox></td>
    </tr>
    </table>
    
    
    
    
    <h3>Load thresholds</h3>
    
    <p>To restore default threshold, just leave the field empty.</p>
    
    <table class="settings-table">
    <tr>
      <td>Minimum</td>
      <td><ww:textfield name="rangeLow" id="minimumField" size="4" /> %</td>
    </tr>
    <tr>
      <td>Optimal Low</td>
      <td><ww:textfield name="optimalLow" id="optimalLowField" size="4" /> %</td>
    </tr>
    <tr>
      <td>Optimal High</td>
      <td><ww:textfield name="optimalHigh" id="optimalHighField" size="4" /> %</td>
    </tr>
    <tr>
      <td>Critical</td>
      <td><ww:textfield name="criticalLow" id="criticalLowField" size="4" /> %</td>
    </tr>
    <tr>
      <td>Maximum</td>
      <td><ww:textfield name="rangeHigh" id="maximumField" size="4" /> %</td>
    </tr>
    </table>
  
    <ww:submit value="Save"></ww:submit>
    
    </ww:form>
  </div>

</div>
</div>

</jsp:body>
</struct:htmlWrapper>