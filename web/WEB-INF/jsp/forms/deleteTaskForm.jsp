<%@ include file="../inc/_taglibs.jsp"%>

<c:choose>
	<c:when test="${empty task.hourEntries}">
		Delete task?
	</c:when>
	<c:otherwise>
		<form>
			<div class="deleteTaskWithHourEntryForm">
			<table>
				<tr>
					<td>This task contains spent effort entries.</td>
				</tr>
				<tr>
					<td><input type="radio" name="hourEntryHandlingChoice" value="MOVE">Move the spent effort entries to the story / iteration.</td>
				</tr>
				<tr>
					<td><input type="radio" name="hourEntryHandlingChoice" value="DELETE">Delete the spent effort entries.</td>
				</tr>
			</table>
			</div>
		</form>
	</c:otherwise>
</c:choose>