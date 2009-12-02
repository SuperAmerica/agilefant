<%@ include file="../inc/_taglibs.jsp"%>

<c:choose>
	<c:when test="${empty story.hourEntries && empty story.tasks}">
		Delete story?
	</c:when>
	<c:otherwise>
		<form>
			<div class="deleteForm">
				<c:if test="${!empty story.tasks}">
					<p>This story contains tasks.</p>
					<ul>
						<li>
							<input type="radio" name="taskHandlingChoice" value="MOVE" checked="checked" />
							Move the tasks to the iteration.
						</li>
						<li>
							<input type="radio" name="taskHandlingChoice" value="DELETE" />
							Delete the tasks.
						</li>
						<li class="taskHourEntryHandling" style="display: none">
							<p>Some tasks contain spent effort entries.</p>
							<ul>
								<li>
									<input type="radio" name="taskHourEntryHandlingChoice" value="MOVE" checked="checked" />
									Move the spent effort entries to the iteration.
								</li>
								<li>
									<input type="radio" name="taskHourEntryHandlingChoice" value="DELETE" />
									Delete the spent effort entries.
								</li>
							</ul>
						</li>
					</ul>
				</c:if>
				<c:if test="${!empty story.hourEntries}">
					<p>This story contains spent effort entries.</p>
					<ul>
						<li>
							<input type="radio" name="storyHourEntryHandlingChoice" value="MOVE" checked="checked" />
							Move the spent effort entries to the iteration.
						</li>
						<li>
							<input type="radio" name="storyHourEntryHandlingChoice" value="DELETE" />
							Delete the spent effort entries.
						</li>
					</ul>
				</c:if>
			</div>
		</form>
	</c:otherwise>
</c:choose>