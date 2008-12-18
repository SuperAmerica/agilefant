// (c) 2roX 2007
// @author Marjukka Kokkonen
//
// Thanks to the author of "DOM Scripting : Web design with JavaScript and the Document Object Model"
// for leading the way.

var debug = false;  // for debug alerts

// Add onchange property to the work logging fields
function prepareFields(){
  var elems = getInputElementsToSumUp();
  for(var i=0; i<elems.length; i++){
    elems[i].onchange = function(){ return updateSumField(); }
  }
  updateSumField(); // initialize the text showing the sum
}

// If javascript enabled:
// First time called, inserts into the document a text field
//   where to insert the changing sum. (With description text.)
// Next calls update the sum field.
//
function updateSumField(){
  var idForSumField = "sumField";

  // Create a new node for displaying the sum and the description text
  var sum = countSum();
  var sumLine = document.createElement("p");
  sumLine.setAttribute("id", idForSumField);
  var text = document.createTextNode("Sum of work to log: "+sum);
  sumLine.appendChild(text);

  // Try to reach the existing sum field element
  // If exsting, replace it with the new one, else create it 
  //   below(/above) the table where the numbers reside.
  //
  var oldField = document.getElementById(idForSumField);
  if(oldField){
    oldField.parentNode.replaceChild(sumLine,oldField);
  }else{
    var theTable = getTheTable();
    //theTable.parentNode.insertBefore(sumLine, theTable);           // inserting before
    theTable.parentNode.insertBefore(sumLine, theTable.nextSibling); // inserting after
  }
}

// Counts the sum of inputted efforts.
function countSum(){
  var sum = 0;
  var elems = getInputElementsToSumUp(); // get the input fields from the document
  for(var i=0; i<elems.length; i++){
    //var numb = parseInt(elems[i].getAttribute("value"));
    var numb = parseInt(elems[i].value);
    if(!isNaN(numb)) sum += numb;
    if(debug) alert("sum="+sum +" ("+elems[i]+": "+numb+")");
  }
  return(sum);
}

// Get the table where the input fields reside.
// Needed by the sum field inserting function 
//    -> separated from getInputElementsToSumUp() function.
//
function getTheTable(){
  var tableId = "row";

  var theTable = document.getElementById(tableId);
  if(!theTable)  {
    if(debug) alert("There is no element with id '+tableId+'. Stopping.");
    return false;
  }
  return theTable;
}

// Returns the input fields whose sum is to be counted.
//
function getInputElementsToSumUp(){

  // The number of the column counting from left, where the elements to be summed reside.
  var columnNo = 2;

  // Check that there are elements to sum up
  var theTable = getTheTable();

  var tbodyElems = theTable.getElementsByTagName("tbody");
  if(tbodyElems.length!=1)  {    if(debug) alert("There are none or more than one tbody elements to consider. Stopping.");    return false;  }

  // get the 'tr' elements of the table
  var tRows = tbodyElems[0].getElementsByTagName("tr");
  if(tRows.length<1)  {    if(debug) alert("There are no 'tr' elements to consider. Stopping.");    return false;  }

  // the 'td' elements inside the table rows
  var theTds = Array();
  for(var i=0; i<tRows.length; i++){
    var rowElems = tRows[i].getElementsByTagName("td");
    theTds[theTds.length] = rowElems[columnNo-1];
    if(tRows.length<1)  {    if(debug) alert("There are no 'td' elements to consider. Stopping.");    return false;  }
  }

  // At last, get the input fields from the <td> elements.
  // Supposing here that there is nothing more in the <td>
  // element but the input field.
  //
  var theFields = Array();
  for(var i=0; i<theTds.length; i++){
    var formnode = theTds[i];
    theFields[theFields.length] = formnode.childNodes[3]; //.getElementById("myTasksPerformWork_event_effort");
    //if(debug) alert(theFields[theFields.length-1].firstChild);
  }

  return theFields;
}

// Add the functionality after the page has been loaded
//
window.onload = function(){

  // Check that the needed functions exist
  if( !document.getElementsByTagName || 
      !document.childNodes ||
      !document.firstChild ||
      !document.createElement ||
      !document.getElementById )
  {
    if(debug) alert("Browser does not have support for one or more of the needed functions.");
    return false;
  }

  // Add the functionality
  prepareFields();
}