<%@ include file="../inc/_taglibs.jsp"%>

<aef:currentUser />
<aef:enabledUserList/>
<%-- 
<aef:userEffortSum user="${currentUser}" timeInterval="Today" id="todayEffortSum" />
<aef:userEffortSum user="${currentUser}" timeInterval="Yesterday" id="yesterdayEffortSum" />
<aef:userEffortSum user="${currentUser}" timeInterval="This week" id="weekEffortSum" />
<aef:userEffortSum user="${currentUser}" timeInterval="This month" id="monthEffortSum" />
 --%>
<script type="text/javascript">
function updatePastEffort(immediate) {
    var data = new Object();
    if(immediate) { 
        data["startDate"] = $("#effStartDate").val();
        data["endDate"] = $("#effEndDate").val();
        if(data["startDate"].length < 3) {
            alert("From date -field is empty!"); 
            return;
        }
        if(data["endDate"].length < 3) {
            alert("To date -field is empty!");
            return;
        }
        $("#hourDisplay").load("getHourSum.action",data);
    } else {
        if($("#pastEffortInterval").is(":checked")) {
            $("#pastEffortChoosers").show();
        } else {
            $("#pastEffortChoosers").hide();
        }
    }
}

$(document).ready(function() {
    $('#userChooserLink-createHourEntry').userChooser({
        storyId: ${storyId},
        backlogId: ${backlogId},
        userListContainer: '#userListContainer-createHourEntry',
        selectThese: [${currentUser.id}],
        renderFor: 'hourEntry',
        validation: {
            selectAtLeast: 1,
            aftime: false
        },
        legacyMode: false
    });
});
</script>

<div class="validateWrapper validateNewHourEntry"><ww:form
	action="storeHourEntry">
	<ww:hidden name="storyId" />
	<ww:hidden name="backlogId" />

	<table class="formTable">

		<tr>
			<td>Effort spent</td>
			<td></td>
			<td colspan="2"><ww:textfield name="hourEntry.minutesSpent" />(e.g.
			"2h 30min" or "2.5")</td>
		</tr>
		<tr>
			<td>When</td>
			<td></td>
			<td><joda:format value="${hourEntry.date}" style="S-" /> <aef:datepicker
				id="he_date" name="date"
				format="%{getText('webwork.shortDateTime.format')}" value="%{#date}" />
			</td>
		</tr>

		<tr>

			<td>By whom</td>
			<td></td>
			<td colspan="2">
			<div><a id="userChooserLink-createHourEntry" href="#"
				class="assigneeLink"> <img src="static/img/users.png" /> <span
				id="userListContainer-createHourEntry"> <c:out
				value="${currentUser.initials}" /> <input type="hidden"
				name="userIds" value="${currentUser.id}" /> </span> </a></div>
			</td>
		</tr>
		<tr>
			<td>Comment</td>
			<td></td>
			<td colspan="2"><ww:textfield size="60"
				name="hourEntry.description" /></td>
		</tr>
		<tr>
			<td></td>
			<td></td>
			<td><ww:submit value="Save" id="createButton" /></td>
			<td class="deleteButton"><ww:reset value="Cancel"
				cssClass="closeDialogButton" /></td>
		</tr>
		<tr>
			<td colspan="4">My logged effort
			<div id="pastEffortShower" style="border: 1px solid #A0A0A0;">

			<table>
				<tr>
					<td colspan="2">
					<table>
						<tr>
							<td>Today:</td>
							<td>${todayEffortSum}</td>
							<td style="width: 50px;"></td>
							<td>This week:</td>
							<td>${weekEffortSum}</td>

						</tr>
						<tr>
							<td>Yesterday:</td>
							<td>${yesterdayEffortSum}</td>
							<td></td>
							<td>This month:</td>
							<td>${monthEffortSum}</td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td><input type="checkbox" name="pastEffortInterval"
						id="pastEffortInterval" onclick="javascript:updatePastEffort();" />
					Custom interval
					<div id="pastEffortChoosers" style="display: none;">
					<table>
						<tr>
							<td><aef:datepicker id="effStartDate" name="effStartDate"
								format="%{getText('webwork.shortDateTime.format')}" value="" />
							</td>
							<td style="width: 30px; text-align: center;">-</td>
							<td><aef:datepicker id="effEndDate" name="effEndDate"
								format="%{getText('webwork.shortDateTime.format')}" value="" />
							</td>
							<td><input type="button" value="Update"
								onclick="javascript:updatePastEffort(true);" /></td>
							<td></td>
							<td>
							<div id="hourDisplay"></div>
							</td>
						</tr>
					</table>
					</div>
					</td>
				</tr>
			</table>
			</div>
			<br />
			<br />
			</td>
		</tr>
	</table>
</ww:form>
</div>