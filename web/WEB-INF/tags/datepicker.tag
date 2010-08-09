<%@ include file="../jsp/inc/_taglibs.jsp" %>
<%@tag description = "Datepicker" %>
<%@ attribute name="name" %>
<%@ attribute name="value" type="org.joda.time.DateTime" %>
<%@ attribute name="format" %>
<%@ attribute name="id" %>

<input type="text" id="${id}" value="<joda:format value='${value}' pattern='${format}' />" name="${name}" size="14" class="datePickerField" style="float: left;" />

<script type="text/javascript">
<!--
$(function()
{
	$('#${id}').datepicker( {
    dateFormat : 'yy-mm-dd',
    numberOfMonths : 3,
    showButtonPanel : true,
    beforeShow : function(input, inst) {
    console.log(input);
    console.log(inst);
        pattern = /(\d|[0-1][0-9]|2[0-3]):(\d|[0-5][0-9])$/;
        var index = inst.input.val().search(pattern);
        if (index === -1) {
          inst.input.data("hoursandmins", '12:00');
        }
        else {
          inst.input.data("hoursandmins", inst.input.val().substr(index, 5));
        }
    },
    onSelect : function(newValue, inst) {
        var val = newValue + " " + inst.input.data("hoursandmins");
        inst.input.val(val);
    },
    buttonImage : 'static/img/calendar.gif',
    buttonImageOnly : true,
    showOn : 'button',
    constrainInput : false
});
});
//-->
</script>

