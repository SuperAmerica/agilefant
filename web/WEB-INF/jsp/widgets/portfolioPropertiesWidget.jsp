<%@include file="/WEB-INF/jsp/inc/_taglibs.jsp" %>

<struct:widget name="Portfolio properties" widgetId="-1">

<script type="text/javascript">
$(document).ready(function() {
  
  var myWidget = $('#portfolioPropertiesTable').parents('.widget');
  $('.cancelProperties').click(function() {
    $(this).parents('.widget').remove();
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

<div style="clear: left; float: right;">
  <button class="dynamics-button saveProperties">Save</button>
  <button class="dynamics-button cancelProperties">Cancel</button>
</div>

</struct:widget>