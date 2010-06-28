<%@ include file="/WEB-INF/jsp/inc/_taglibs.jsp" %>

<struct:htmlWrapper navi="portlets">

<style>
.portletContainer {
  width: 50%;
  margin: 0;
  padding: 0;
  float: left;
}
.portletList {
  margin: 0;
  padding: 0;
  width: 100%;
  min-height: 200px;
  list-style-type: none;
}
.portlet {
  background: whiteSmoke;

  display: block;
  min-width: 350px;
  max-height: 350px;
  
  margin: 10px 0 0;
  padding: 0;
  
  border: 1px solid #ccc;
  -webkit-border-radius: 5px;
  -moz-border-radius: 5px;
  
  font-size: 80%;
  
  width: 95%;
  
  overflow: hidden;
}
.portlet-placeholder {
  min-width: 350px;
  width: 95%;
  height: 100px;
  
  margin: 10px 0 0;
  padding: 0;
  
  border: 1px dashed #ccc;
  background: whiteSmoke;
}
.portletHeader {
  border: 1px solid #ccc;
  -webkit-border-radius: 5px;
  -moz-border-radius: 5px;
  
  padding: 2px 5px;
  margin: 2px;
  
  background: white;
  
  vertical-align: middle;
  cursor: move;
}
.portletHeader ul {
  float: right;
  margin: 0;
  padding: 0;
}
.portletHeader ul li {
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
}
.portletHeader ul li:hover {
  color: #666;
  background: whiteSmoke;
  border-color: #ccc;
}
.portletContent {
  margin: 2px 5px;
  max-height: 320px;
  overflow: auto;
}
</style>

<script type="text/javascript">
var counter = 0;
var addTextPortlet = function addTextPortlet(num) {
  var ul = $('ul.portletList:eq(' + num + ')');
  $('.textPortlet:eq(0)').clone().appendTo(ul);
};
var addBurndownPortlet = function addBurndownPortlet(num) {
  var ul = $('ul.portletList:eq(' + num + ')');
  $('.burndownPortlet:eq(0)').clone().appendTo(ul);
};

$(document).ready(function() {
  addTextPortlet(0);
  addBurndownPortlet(0);
  addTextPortlet(0);
  addBurndownPortlet(1);
  addTextPortlet(1);

  $('.portletList').sortable({
    connectWith: '.portletList',
    dropOnEmpty: true,
    placeholder: 'portlet-placeholder',
    handle: '.portletHeader'
  });

  $('.closeWidget').live('click',function() {
    $(this).parents('.portlet').remove();
  });

  $('.minimizeWidget').live('click',function() {
    $(this).siblings('.maximizeWidget').show();
    $(this).parents('.portlet').find('.portletContent').hide('blind');
    $(this).hide();
  });

  $('.maximizeWidget').live('click',function() {
    $(this).siblings('.minimizeWidget').show();
    $(this).parents('.portlet').find('.portletContent').show('blind');
    $(this).hide();
  });

  $('#addPortlet').click(function() {
    if ($('#portletSelect').val() === 'burndown') {
      addBurndownPortlet(0);
    }
    else {
      addTextPortlet(0);
    }
  });
});

</script>

<h2>Portlets of <ww:select list="#{'foo':'foo','bar':'bar','baz':'baz'}" /></h2>

<p>Add portlet <ww:select id="portletSelect" list="#{'burndown':'Burndown','text':'Text'}" /><button id="addPortlet" class="dynamics-button">Add</button></p>

<div style="margin-top: 2em; min-width: 750px; background: #def;">
  <div class="portletContainer">
    <ul class="portletList">
      <li class="portlet textPortlet">
        <div class="portletHeader">
          <span>Otsikko</span>
          <ul>
            <li class="closeWidget">X</li>
            <li class="maximizeWidget" style="display: none;">+</li>
            <li class="minimizeWidget">-</li>
          </ul>
        </div>
        <div class="portletContent">
        Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla semper venenatis enim sit amet vulputate. Quisque sed mauris tortor, quis ultricies est. Morbi ultrices semper dolor imperdiet venenatis. Vivamus ante est, ornare sit amet iaculis id, faucibus eget turpis. Duis ut purus in neque aliquam sollicitudin. Vivamus nisl nisl, pulvinar at ullamcorper quis, rutrum nec nunc. Morbi id enim ut velit vestibulum suscipit. Aliquam ornare nunc a magna sollicitudin eu volutpat mi condimentum. Fusce sodales auctor tellus, vel congue ligula rutrum in. Sed auctor, dui sed posuere mollis, massa tellus malesuada metus, nec feugiat enim nulla tincidunt eros. Nam luctus urna vitae justo adipiscing et posuere ligula hendrerit. Donec id felis tortor. Morbi lorem magna, varius in vestibulum non, sodales sed metus. Aenean gravida, urna venenatis lacinia eleifend, leo neque commodo eros, sed sollicitudin tortor velit vitae nisl. Sed ornare molestie arcu, non convallis felis elementum non.

        Curabitur enim nibh, pharetra in tempor ac, dignissim porttitor nisi. Suspendisse at quam eros, ac rhoncus neque. Suspendisse placerat leo eu quam accumsan posuere. Vivamus in leo risus. Morbi in aliquam erat. Cras ornare metus ac nisi molestie bibendum. Sed a diam orci, bibendum pulvinar risus. Nulla a elit arcu, id blandit lacus. Morbi facilisis, tellus eget sagittis pellentesque, augue urna sagittis neque, sit amet tincidunt ligula felis nec ligula. In aliquam aliquet diam et porta. Maecenas ut metus eget velit laoreet interdum in ac elit. Nullam ut quam id tortor commodo varius accumsan sit amet mauris.
        </div>
      </li>
      <li class="portlet burndownPortlet">
        <div class="portletHeader">
          <span>Burndown</span>
          <ul>
            <li class="closeWidget">X</li>
            <li class="maximizeWidget" style="display: none;">+</li>
            <li class="minimizeWidget">-</li>
          </ul>
        </div>
        <div class="portletContent" style="text-align: center;">
          <img src="http://localhost:8080/agilefant/drawIterationBurndown.action?backlogId=134" width="300" style="display: inline-block;"/>
        </div>
      </li>
    </ul>
  </div>
  <div class="portletContainer">
    <ul class="portletList"></ul>
  </div>
</div>


</struct:htmlWrapper>