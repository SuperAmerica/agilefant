<%@ include file="../inc/_taglibs.jsp"%>
  <div><img src="static/img/question.png" alt="Are you sure?" style="float: left;" />
  <div style="margin-left: 90px">
<c:choose>
	<c:when test="${empty story.hourEntries && empty story.tasks && empty story.children}">
		Delete story?
	</c:when>
	<c:otherwise>
		<form>
		  <div class="deleteForm">
        <c:if test="${!empty story.children}">
          <p>This story has children.</p>
          <ul>
            <li>
              <input type="radio" name="childHandlingChoice" value="MOVE" onchange="$('#deleteChildren').hide();" checked="checked" />
              <c:choose>
              <c:when test="${story.parent != null}">
                Move the children to the story's parent: <span style="color: #666;">${story.parent.name}</span>
              </c:when>
              <c:otherwise>
                Move the children to be root stories
              </c:otherwise>
              </c:choose>
            </li>
            <li>
              <input type="radio" name="childHandlingChoice" value="DELETE" onchange="$('#deleteChildren').show('blind');"/>
              Delete all the children
            </li>
            <li id="deleteChildren" style="display: none; border: 1px dashed #A6C9E2; padding: 0.5em;">
              <h4>These stories will be deleted!</h4>
              
              <p style="font-weight: normal;"><strong>Note!</strong> The children may contain tasks and hour entries which will be deleted!</p>
              
              <div class="hierarchyContainer">
                <div class="storyTreeContainer">
                  <div class="tree">
                    <ul>
                      <ai:storyTreeNode_forStory node="${story}" />
                    </ul>
                  </div>
                </div>
              </div>
            </li>
          </ul>
        </c:if>
      
				<c:if test="${!empty story.tasks}">
					<p>This story contains tasks.</p>
					<ul>
						<c:choose>
							<c:when test="${aef:isIteration(story.backlog)}">
								<li>
									<input type="radio" name="taskHandlingChoice" value="MOVE" checked="checked" />
									Move the tasks to the iteration '${story.backlog.name}'.
								</li>
								<li>
									<input type="radio" name="taskHandlingChoice" value="DELETE" />
									Delete the tasks.
								</li>
								<li class="taskHourEntryHandling" style="display: none">
									<p>Some tasks might contain spent effort entries.</p>
									<ul>
										<li>
											<input type="radio" name="taskHourEntryHandlingChoice" value="MOVE" />
											Move the spent effort entries to the iteration '${story.backlog.name}'.
										</li>
										<li>
											<input type="radio" name="taskHourEntryHandlingChoice" value="DELETE" />
											Delete the spent effort entries.
										</li>
									</ul>
								</li>
							</c:when>
							<c:otherwise>
								<li style="color: #ff0000">
									<input type="hidden" name="taskHandlingChoice" value="DELETE" checked="checked"/>
									The tasks will be permanently deleted.
								</li>
								<li class="taskHourEntryHandling">
									<p>Some tasks might contain spent effort entries.</p>
									<ul>
										<li>
											<input type="radio" name="taskHourEntryHandlingChoice" value="MOVE" checked="checked" />
											Move the spent effort entries to the <c:if test="${aef:isProject(story.backlog)}">project</c:if><c:if test="${aef:isProduct(story.backlog)}">product</c:if> '${story.backlog.name}'.
										</li>
										<li>
											<input type="radio" name="taskHourEntryHandlingChoice" value="DELETE" />
											Delete the spent effort entries.
										</li>
									</ul>
								</li>
							</c:otherwise>
						</c:choose>
					</ul>
				</c:if>
				<c:if test="${!empty story.hourEntries}">
					<p>This story contains spent effort entries.</p>
					<ul>
						<li>
							<input type="radio" name="storyHourEntryHandlingChoice" value="MOVE" checked="checked" />
							Move the spent effort entries to the <c:if test="${aef:isProject(story.backlog)}">project</c:if><c:if test="${aef:isProduct(story.backlog)}">product</c:if> '${story.backlog.name}'.
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
</div>
</div>