<%@ include file="/WEB-INF/jsp/inc/_taglibs.jsp" %>

<struct:htmlWrapper navi="widgets">

<style>
.widgetContainer {
  width: 50%;
  margin: 0;
  padding: 0;
  float: left;
}
.widgetList {
  margin: 0;
  padding: 0;
  width: 100%;
  min-height: 200px;
  list-style-type: none;
  position: relative;
}
.widget {
  background: white;

  display: block;
  min-width: 350px;
  max-height: 350px;
  
  margin: 10px 0 0;
  padding: 0;
  
  border: 1px solid #A6C9E2;
  -webkit-border-radius: 5px;
  -moz-border-radius: 5px;
  border-radius: 5px;
  
  font-size: 80%;
  
  width: 95%;
  
  overflow: hidden;
}
.widget-placeholder {
  min-width: 350px;
  width: 95%;
  height: 100px;
  
  margin: 10px 0 0;
  padding: 0;
  
  border: 1px dashed #ccc;
  background: whiteSmoke;
}
.widgetHeader {
  border: 1px solid #ccc;
  -webkit-border-radius: 5px;
  -moz-border-radius: 5px;
  border-radius: 5px;
  
  padding: 2px 5px;
  margin: 2px;
  
  background: whiteSmoke;
  
  vertical-align: middle;
}
.realWidget .widgetHeader {
  cursor: move;
}
.widgetHeader ul {
  float: right;
  margin: 0;
  padding: 0;
}
.widgetHeader ul li {
  font-weight: bold;
  display: inline-block;
  float: right;
  width: 13px;
  height: 13px;
  margin: 0 2px;
  padding: 0;
  cursor: pointer;
  text-align: center;
  border: 1px solid transparent;
  -moz-border-radius: 3px;
  -webkit-border-radius: 3px;
  border-radius: 3px;
}
.widgetHeader ul li:hover {
  color: #666;
  background: whiteSmoke;
  border-color: #ccc;
}
.widgetContent {
  margin: 2px 5px;
  max-height: 320px;
  overflow: auto;
}
.createNewWidget button, .createNewWidget td {
  font-size: 100% !important;
}
</style>

<script type="text/javascript">

$(document).ready(function() {

  $('.widgetList').sortable({
    connectWith: '.widgetList',
    dropOnEmpty: true,
    placeholder: 'widget-placeholder',
    handle: '.widgetHeader',
    items: '> :not(.createNewWidget)',
    delay: 300,
    stop: function(event, ui) {
      var pos = ui.item.parent('ul').children('li').index(ui.item);
      $.ajax({
        type: 'POST',
        url:  'ajax/widgets/moveWidget.action',
        data: { widgetId: ui.item.attr('widgetId'), listNumber: ui.item.parent('ul').attr('listNumber'), position: pos }
      });
    }
  });

  $('.closeWidget').live('click',function() {
    var widget = $(this).parents('.widget');
    
    $.ajax({
      type: 'POST',
      dataType: 'text',
      url: 'ajax/widgets/deleteWidget.action',
      data: { widgetId: widget.attr('widgetId') },
      success: function(data, status) {
        MessageDisplay.Ok('Widget removed');
        widget.remove();
      }
    });
    
  });

  $('.minimizeWidget').live('click',function() {
    var me = $(this);
    var parent = me.parents('.widget');
    me.siblings('.maximizeWidget').show();
    parent.find('.widgetContent').hide('blind');
    me.hide();

    jQuery.cookie('agilefant_widgetcollection_${contents.id}_' + parent.attr('widgetId'), 'closed', { expires: 60 });
  });

  $('.maximizeWidget').live('click',function() {
    var me = $(this);
    var parent = me.parents('.widget');
    me.siblings('.minimizeWidget').show();
    parent.find('.widgetContent').show('blind');
    me.hide();

    jQuery.cookie('agilefant_widgetcollection_${contents.id}_' + parent.attr('widgetId'), 'open', { expires: 60 });
  });

  /*
   * New widget creation
   */
  
  $('.newWidgetLink').click(function() {
    var clone = $('#templates > #newWidget').clone();
    clone.removeAttr('id');

    clone.find('.cancelNewWidget').click(function() {
      clone.remove();
    });

    var idField = clone.find('.objectId');
    var typeField = clone.find('.objectType');

     
    idField.agilefantQuickSearch({
      source: "ajax/search.action",
      minLength: 3,
      select: function(event, ui) {
        idField.data('selectedId',ui.item.originalObject.id);
      }
    });

    clone.find('.saveNewWidget').click(function() {
      $.ajax({
        type: 'POST',
        dataType: 'html',
        url: 'ajax/widgets/createWidget.action',
        data: { type: typeField.val(), objectId: idField.data('selectedId'), collectionId: ${contents.id}, position: 0, listNumber: 0 },
        success: function(data, status) {
          MessageDisplay.Ok('Widget added');
          clone.replaceWith($('<li class="widget realWidget"/>').html(data));
        }
      });
    });
    
    clone.prependTo($('.widgetList:eq(0)'));
    return false;
  });

  /*
   * Load the widget contents
   */
  
  <c:forEach items="${contents.widgets}" var="widget">
  $('#widget_${widget.id}').attr('widgetId',${widget.id}).load('ajax/widgets/${widget.type}.action?objectId=${widget.objectId}');
  </c:forEach>

});

</script>


<h2>Widgets of ${contents.name}</h2>

<a href="#" class="newWidgetLink">Add widget</a>

<div style="margin-top: 2em; min-width: 750px; background: #def;">
  <c:set var="listCount" value="0"/>
  <c:forEach items="${widgetGrid}" var="widgetList">
    <div class="widgetContainer">
      <ul class="widgetList" listNumber="${listCount}">
        <c:forEach items="${widgetList}" var="widget">
          <li class="widget realWidget" id="widget_${widget.id}"><div style="text-align:center;"><img src="static/img/pleasewait.gif" style="display:inline-block;vertical-align:middle;"/><span style="font-size:100%;color:#666;vertical-align: middle;">Please wait...</span></div></li>
        </c:forEach>
      </ul>
    </div>
    <c:set var="listCount" value="${listCount + 1}"/>
  </c:forEach>
</div>


<!-- Hidden templates -->
<ul id="templates" style="display: none;">
  <li class="widget createNewWidget" id="newWidget" style="position:relative;">
    <div class="widgetHeader"><span>Create a new widget</span></div>
    <div class="widgetContent">
      <table>
        <tr>
          <td>Type</td>
          <td><ww:select name="type" list="#{'iterationMetrics':'Iteration Metrics','burndown':'Burndown','text':'Text'}" cssClass="objectType"/></td>
        </tr>
        <tr>
          <td>Object</td>
          <td><input name="object" class="objectId"/></td>
        </tr>
      </table>
      <div style="clear: left; float: right;">
        <button class="dynamics-button saveNewWidget">Save</button>
        <button class="dynamics-button cancelNewWidget">Cancel</button>
      </div>
    </div>
  </li>
</ul>
<!-- /Hidden templates -->


</struct:htmlWrapper>