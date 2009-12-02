<%@ include file="../inc/_taglibs.jsp"%>

<c:choose>
	<c:when test="${empty task.hourEntries}">
		Delete task?
	</c:when>
	<c:otherwise>
		<form>
			<div class="deleteForm">
				<p>This task contains spent effort entries.</p>
				<ul>
					<li>
						<input type="radio" name="hourEntryHandlingChoice" value="MOVE" checked="checked" />
						Move the spent effort entries to the
						<c:choose>
							<c:when test="${task.story == null}">iteration '${task.iteration.name}'</c:when>
							<c:otherwise>story '${task.story.name}'</c:otherwise>
						</c:choose>
					</li>
					<li>
						<input type="radio" name="hourEntryHandlingChoice" value="DELETE" />
						Delete the spent effort entries.
					</li>
				</ul>
			</div>
		</form>
	</c:otherwise>
</c:choose>