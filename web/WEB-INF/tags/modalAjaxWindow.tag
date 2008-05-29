<%@ include file="../jsp/inc/_taglibs.jsp"%>

<%@tag description="Set up ajax listeners and containers for modal window"%>

<div id="modalWindowContainer" class="jqmWindow" style="display: none;"><b>Please wait, content loading...</b></div>
<script type="text/javascript">
	function hideModalWindow(hash) {
		hash.w.html("<b>Please wait, content loading...</b>");
		hash.o.remove();
		hash.w.hide();
	}
	function openModalWindow(hash) {
		var trig = $(hash.t);
		var pos = trig.offset();
		$(hash.w).css("top",pos.top-200).show();
	}
	
	$(document).ready(function() {
		jQuery("#modalWindowContainer").jqm({modal: true, ajax: "@href", overlay: 30, trigger: ".openModalWindow", closeClass: "jqmClose", onHide: hideModalWindow, onShow: openModalWindow});
	
	});
	
</script>