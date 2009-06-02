<%@ include file="../inc/_taglibs.jsp"%>

<script type="text/javascript">

$(document).ready(function() {
    $('#themeChooserLink-createtask').themeChooser({
        taskId: ${taskId},
        backlogId: '#createtaskBacklogId',
        themeListContainer: '#themeListContainer-createtask'
    });
    $('#userChooserLink-createtask').userChooser({
        taskId: ${taskId},
        legacyMode: false,
        backlogIdField: '#createtaskBacklogId',
        userListContainer: '#userListContainer-createtask'
    });
 
});

</script>

<div class="validateWrapper validateNewtask">
<ww:form action="storeNewtask" method="post">
	<ww:hidden name="fromTodoId" />
	<table class="formTable">
		<tr>
			<td>Name</td>
			<td>*</td>
			<td colspan="2"><ww:textfield size="60" name="task.name" /></td>
		</tr>

		<tr>
            <td></td>
            <td></td>
            <td><ww:submit value="Create" id="createButton" /></td>
            <td class="deleteButton"><ww:reset value="Cancel"
                cssClass="closeDialogButton" /></td>
        </tr>
	</table>

</ww:form>
</div>