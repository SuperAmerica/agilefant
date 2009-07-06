<%@ include file="../jsp/inc/_taglibs.jsp" %>
<%@tag description = "Datepicker" %>
<%@ attribute name="name" %>
<%@ attribute name="value" %>
<%@ attribute name="format" %>
<%@ attribute name="id" %>

<input type="text" id="${id}" value="${value}" name="${name}" size="14" class="datePickerField" style="float: left;" />

<script type="text/javascript">
<!--
$(function()
{
	$('#${id}').datePicker({displayClose: true, createButton: true, clickInput: false});
});
//-->
</script>

