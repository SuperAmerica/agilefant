<%@include file="/WEB-INF/jsp/inc/_taglibs.jsp" %>

<struct:widget name="Portfolio properties" widgetId="-1">

<script type="text/javascript">
$(document).ready(function() {
  
  var myWidget = $('#portfolioPropertiesTable').parents('.widget');
  $('.cancelProperties').click(function() {
    $(this).parents('.widget').remove();
  });

  $('.deletePortfolio').click(function() {
    var dialog = new DynamicsConfirmationDialog(
        "Really delete the portfolio?",
        "This action can't be reversed",
        function() {
          window.location.href = "deletePortfolio.action?collectionId=${collectionId}"
        }
    );
  });
  
  $('.saveProperties').click(function() {
    var postData = {
      "collection.id":   ${collectionId},
      "collection.name": $('input[name=collectionName]').val(),
      "private":         $('input[name=collectionPrivate]').is(':checked'),
      "userId":          ${currentUser.id}
    };

    $.ajax({
      type: 'POST',
      url:  'ajax/widgets/storeCollection.action',
      data: postData,
      success: function(data, status) {
        MessageDisplay.Ok('Portfolio information updated');
        myWidget.remove();
      }
    });
  });
});
</script>


<table id="portfolioPropertiesTable">
  <tr>
    <td>
      Name
    </td>
    <td>
      <input name="collectionName" value="${contents.name}"/>
    </td>
  </tr>
  <tr>
    <td>
      Private?
    </td>
    <td>
      <c:if test="${contents.user != null}">
        <c:set var="checkboxSelected" value='checked="checked"' />
      </c:if>
      <input type="checkbox" name="collectionPrivate" disabled="disabled" ${checkboxSelected}/>
    </td>
  </tr>
</table>

<div style="clear: left;">
  <button class="dynamics-button deletePortfolio" style="width: 20ex;">Delete portfolio</button>
  <button class="dynamics-button saveProperties" style="float: right;">Save</button>
  <button class="dynamics-button cancelProperties" style="float: right;">Cancel</button>
</div>

</struct:widget>