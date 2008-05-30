
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

function confirmDeleteHour() {
	if (confirm("Really delete hour entry?")) {
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
		alert("Invalid selection. Select a valid backlog.");
		for(i = 0; i < elements.length; i++){
			document.getElementById(elements[i]).disabled = true;
		}
	} else {
		for(i = 0; i < elements.length; i++){
		document.getElementById(elements[i]).disabled = false;
		}
	}			
}
function checkEstimateFormat(field) {
	var ret = false;
	var fields = document.getElementsByName(field);
	var value = fields[0].value;
	var regex = new RegExp("^[ ]*([0-9]+[.,]?[0-9]*h?)?([ ]*[0-5]?[0-9]min)?[ ]*$");
	if(value.length > 0) {
		ret = regex.test(value);
	}
	if(!ret) {
		alert("Invalid effort format!");
		return false;
	}
	return true;
}