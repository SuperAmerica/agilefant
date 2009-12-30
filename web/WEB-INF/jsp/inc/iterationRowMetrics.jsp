<%@ include file="./_taglibs.jsp"%>

<div style="margin: 2em">
	<table style="width: 100%">
		<tr valign="top">
			<td>
				<h4>Story states by count</h4>
				<table style="margin: 0 1em" cellpadding="5">
					<c:forEach var="entry" items="${iterationRowMetrics.stateDistribution}">
						<tr>
							<td>${entry.key}</td>
							<td><div class="storyState${entry.key}" style="-moz-border-radius: 5px; border: 1px solid #777; height: 20px; width: ${entry.value * 300 / iterationRowMetrics.storyCount}px"></div></td>
							<td>${entry.value}</td>
						</tr>
					</c:forEach>
					<tr style="font-weight: bold">
						<td>Total</td>
						<td></td>
						<td>${iterationRowMetrics.storyCount} stories</td>
					</tr>
				</table>
			</td>
			<td>
				<h4>Time left in iteration</h4>
				<table style="width: 300px; border-collapse: collapse">
					<tr style="height: 20px">
						<td style="background-color: #00ff00; border: 1px solid #777">
						</td>
						<td style="background-color: #ff0000; border: 1px solid #777; width: ${iterationRowMetrics.daysLeft * 100 / iterationRowMetrics.totalDays}%">
						</td>
					</tr>
					<tr>
						<td colspan="2">
							${iterationRowMetrics.daysLeft} of ${iterationRowMetrics.totalDays} days
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</div>