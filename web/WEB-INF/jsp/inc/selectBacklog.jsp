<%@ include file="_taglibs.jsp"%>
<table id="backlogSelector">
	<tr>
		<td>Select product</td>
		<td></td>
	</tr>
	<tr>
		<td>Select project</td>
		<td></td>
	</tr>
	<tr>
		<td>Select iteration</td>
		<td></td>
	</tr>
</table>
<script type="text/javascript">
$(document).ready(function {
	var rows = $("#backlogSelector").children("tr");
	var product = $(rows.get(0)).children("td:eq(1)");
	var project = $(rows.get(1));
	var iteration $(rows.get(2));
});
</script>