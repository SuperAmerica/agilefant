<%@ include file="../inc/_taglibs.jsp"%>

<struct:htmlWrapper navi="settings">

<jsp:body>

<h2>System settings</h2>

<script type="text/javascript">
<!--
$(document).ready(function() {
  var dwc = $('#dailyWorkCheckbox');

  if (dwc.attr('checked')) {
    $('#thresholdDiv').show();
  }

  dwc.change(function() {
    $('#thresholdDiv').toggle();
  });
  
});
//-->
</script>

<div class="structure-main-block">
<div class="dynamictable ui-widget-content ui-corner-all">

  <div class="dynamictable-caption dynamictable-caption-block ui-widget-header ui-corner-all">
    System wide settings
  </div>

  <div style="margin: 1em 0.5em 0.5em;">
    
    <div class="warning-note"> 
      <strong>Note!</strong> Modifying these settings will affect all users.
    </div>
  
    <ww:form action="storeSettings.action" method="post">
  
    <h3>Additional views</h3>
  
    <table class="settings-table">
    <tr>
      <td><label for="dailyWorkEnabled">Enable Daily Work</label></td>
      <td><ww:checkbox id="dailyWorkCheckbox" name="dailyWorkEnabled" fieldValue="true" value="%{dailyWorkEnabled}"></ww:checkbox></td>
      <td><a href="#" class="quickHelpLink" onclick="HelpUtils.openHelpPopup(this,'Daily Work','static/html/help/dailyWorkPopup.html'); return false;">What is Daily Work?</a></td>
    </tr>
    <tr>
      <td><label for="devPortfolioEnabled">Enable Portfolio</label></td>
      <td><ww:checkbox name="devPortfolioEnabled" fieldValue="true" value="%{devPortfolioEnabled}"></ww:checkbox></td>
      <td><a href="#" class="quickHelpLink" onclick="HelpUtils.openHelpPopup(this,'Portfolio','static/html/help/devPortfolioPopup.html'); return false;">What is Portfolio?</a></td>
    </tr>
    <tr>
      <td><label for="hourReportingEnabled">Enable Timesheets</label></td>
      <td><ww:checkbox  name="hourReportingEnabled" fieldValue="true" value="%{hourReportingEnabled}"></ww:checkbox></td>
      <td><a href="#" class="quickHelpLink" onclick="HelpUtils.openHelpPopup(this,'Timesheets','static/html/help/timesheetsPopup.html'); return false;">What are Timesheets?</a></td>
    </tr>
    
    </table>
    
    
    <div style="margin: 0; padding: 0; display: none;" id="thresholdDiv">
    
    <h3>Load thresholds</h3>
    
    <p>Load tresholds are used in displaying the workload in the Daily Work view. To restore default threshold, just leave the field empty.</p>
    
    <table class="settings-table">

    <tr>
      <td>Maximum</td>
      <td><ww:textfield name="rangeHigh" id="maximumField" size="4" /> %</td>
      <td style="background: rgba(150, 8, 8, 0.7);">&nbsp;</td>
    </tr>
    <tr>
      <td>Critical</td>
      <td><ww:textfield name="criticalLow" id="criticalLowField" size="4" /> %</td>
      <td style="background: rgba(224, 17, 2, 0.7);">&nbsp;</td>
    </tr>
    <tr>
      <td>Optimal High</td>
      <td><ww:textfield name="optimalHigh" id="optimalHighField" size="4" /> %</td>
      <td style="background: rgba(245, 221, 57, 0.7);">&nbsp;</td>
    </tr>
    <tr>
      <td>Optimal Low</td>
      <td><ww:textfield name="optimalLow" id="optimalLowField" size="4" /> %</td>
      <td style="background: rgba(9, 144, 14, 0.7)">&nbsp;</td>
    </tr>
    <tr>
      <td>Minimum</td>
      <td><ww:textfield name="rangeLow" id="minimumField" size="4" /> %</td>
      <td style="background: rgba(130, 180, 244, 0.7);">&nbsp;</td>
    </tr>
    </table>
    
    </div>
  
  
    <ww:submit value="Save"></ww:submit>
    
    </ww:form>
  </div>

</div>
</div>

</jsp:body>
</struct:htmlWrapper>