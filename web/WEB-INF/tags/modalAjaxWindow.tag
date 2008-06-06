<%@ include file="../jsp/inc/_taglibs.jsp"%>

<%@tag description="Set up ajax listeners and containers for modal window"%>
<%@attribute name="closeOnSubmit"%>

<div id="modalWindowContainer" class="jqmWindow" style="display: none;"><b>Please wait, content loading...</b></div>
<script type="text/javascript">

	$(document).ready(function() {
		$(".openModalWindow").unbind("click").click(function() {
			var pos = $(this).offset();
			var win = $("#modalWindowContainer");
			win.html("<b>Please wait, content loading...</b>");
			win.show();
			var target = $(this).attr("href");
			<%-- Create the background overlay --%>
			var bg = $('<div style="background: #000; opacity: 0.3; z-index: 9; position: absolute; top: 0px; left: 0px; filter:alpha(opacity=30);-moz-opacity:.30;">&nbsp;</div>');
			win.css("top",pos.top-200).css("z-index","11");
			<%-- Make sure that the background div covers the whole screen by catching window resize and scroll 
				 events --%>
			$(window).resize(function() {
				bg.css("height",$(document).height()).css("width",$(document).width());

			});
			$(window).scroll(function() {
				bg.css("height",$(document).height()).css("width",$(document).width());
			});
			$(window).resize();
			bg.appendTo(document.body);
			var comp = function(data,status) {
				if(status != "success") {
					return false;
				}
				<%-- direct user to the login screen if session has timed out --%>
				
				if(data.indexOf("AJAX-MODAL") == -1) {
					alert("Your session has timed out!");
					window.location.reload();
					return false;
				}
				win.html(data);
				<c:if test="${closeOnSubmit == 'true'}">
					win.find("form").submit(function() {
						var data = $(this).serializeArray();
						jQuery.post($(this).attr("action"),data);
						win.find(".jqmClose").click();
						return false;
					});
				</c:if>
				<%-- register close button to hide the window and remove overlay --%>
				win.find(".jqmClose").click(function() {
					win.hide();
					bg.remove();
					jQuery.post("popContext.action");
					$(".calendar").remove(); //fix this (the stupid dhtml calendar)
					return false;
				});
				return false;
			};
			$.ajax({cache: false, type: "POST", success: comp, url: target, dataType: "html"});
			return false;
		});
	});
</script>
