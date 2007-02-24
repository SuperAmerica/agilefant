
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
	if (confirm("Deleting the task will cause all of its events, including logged work, to be deleted. Continue?")) {
		return true;
	} else {
		return false;
	}
}
