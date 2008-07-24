<%@ include file="../jsp/inc/_taglibs.jsp" %>
<%@tag description = "Datepicker" %>
<%@ attribute name="name" %>
<%@ attribute name="value" %>
<%@ attribute name="format" %>
<%@ attribute name="id" %>

<ww:textfield id="${id}" value="${value}" name="${name}" size="14" cssClass="datePickerField" cssStyle="float: left;"/>

<script type="text/javascript">
<!--
$(function()
{
	$('#${id}').datePicker({displayClose: true, createButton: true, clickInput: false});
});
//-->
</script>

