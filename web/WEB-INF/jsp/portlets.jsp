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
}
.widget {
  background: whiteSmoke;

  display: block;
  min-width: 350px;
  max-height: 350px;
  
  margin: 10px 0 0;
  padding: 0;
  
  border: 1px solid #ccc;
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
  
  background: white;
  
  vertical-align: middle;
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
  border: 1px solid white;
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
</style>

<script type="text/javascript">

$(document).ready(function() {

  $('.widgetList').sortable({
    connectWith: '.widgetList',
    dropOnEmpty: true,
    placeholder: 'widget-placeholder',
    handle: '.widgetHeader'
  });

  $('.closeWidget').live('click',function() {
    $(this).parents('.widget').remove();
  });

  $('.minimizeWidget').live('click',function() {
    $(this).siblings('.maximizeWidget').show();
    $(this).parents('.widget').find('.widgetContent').hide('blind');
    $(this).hide();
  });

  $('.maximizeWidget').live('click',function() {
    $(this).siblings('.minimizeWidget').show();
    $(this).parents('.widget').find('.widgetContent').show('blind');
    $(this).hide();
  });

  <c:forEach items="${contents.widgets}" var="widget">
  $('#widget_${widget.id}').load('${widget.url}');
  </c:forEach>

});

</script>



<h2>Widgets of ${contents.name}</h2>

<div style="margin-top: 2em; min-width: 750px; background: #def;">
  <div class="widgetContainer">
    <ul class="widgetList">
      <c:forEach items="${contents.widgets}" var="widget">
        <li class="widget" id="widget_${widget.id}"><img src="static/img/pleasewait.gif" /></li>
      </c:forEach>
    </ul>
  </div>
  <div class="widgetContainer">
    <ul class="widgetList">
    
    </ul>
  </div>
</div>



</struct:htmlWrapper>