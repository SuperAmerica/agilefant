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
  
    <h3>Additional views</h3>
  
    <table class="settings-table">
    <tr>
      <td><label for="dailyWorkEnabled">Enable Daily Work</label></td>
      <td><ww:checkbox disabled="true" name="dailyWorkEnabled" fieldValue="true" value="%{dailyWorkEnabled}"></ww:checkbox></td>
      <td ><a href="#" style="font-size: 80%; color: #1e5eee; text-decoration: underline;">What is daily work?</a></td>
    </tr>
    <tr>
      <td><label for="devPortfolioEnabled">Enable Dev Portfolio</label></td>
      <td><ww:checkbox disabled="true" name="devPortfolioEnabled" fieldValue="true" value="%{devPortfolioEnabled}"></ww:checkbox></td>
      <td ><a href="#" style="font-size: 80%; color: #1e5eee; text-decoration: underline;">What is Dev Portfolio?</a></td>
    </tr>
    <tr>
      <td><label for="hourReportingEnabled">Enable Timesheets</label></td>
      <td><ww:checkbox name="hourReportingEnabled" fieldValue="true" value="%{hourReportingEnabled}"></ww:checkbox></td>
      <td ><a href="#" style="font-size: 80%; color: #1e5eee; text-decoration: underline;">What are Timesheets?</a></td>
    </tr>
    
    </table>
    
    
    
    <c:if test="${dailyWorkEnabled}">
    
    
    
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
  
    </c:if>
  
    <ww:submit value="Save"></ww:submit>
    
    </ww:form>
  </div>

</div>
</div>

</jsp:body>
</struct:htmlWrapper>