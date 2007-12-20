
function toggleDiv(id)
{
	var div = document.getElementById(id);

	if (div.style.display == "none")
	{
		div.style.display = "";
	}
	else
	{
		div.style.display = "none";
	}
}

function confirmDeleteTask() {
	if (confirm("Deleting the task will cause all of its events, including logged work, to be deleted.")) {
		return true;
	} else {
		return false;
	}
}

function confirmDeleteBli() {
	if (confirm("Deleting the backlog item will cause all of its tasks to be deleted.")) {
		return true;
	} else {
		return false;
	}
}

function confirmDelete() {
	if (confirm("Are you sure?")) {
		return true;
	} else {
		return false;
	}
}