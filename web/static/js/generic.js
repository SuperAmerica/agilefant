
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
	if (confirm("Really delete task?")) {
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

function confirmDeleteTeam() {
	if (confirm("Are you sure to delete the team?")) {
		return true;
	} else {
		return false;
	}
}

function confirmReset() {
	if (confirm("Are you sure you want to reset the original estimate for this backlog item?")) {
		return true;
	} else {
		return false;
	}
}

function disableIfEmpty(value, elements) {
	if(value == "") {
		alert("Invalid selection. Please select a valid object.");
		for(i = 0; i < elements.length; i++){
			document.getElementById(elements[i]).disabled = true;
		}
	} else {
		for(i = 0; i < elements.length; i++){
		document.getElementById(elements[i]).disabled = false;
		}
	}			
}