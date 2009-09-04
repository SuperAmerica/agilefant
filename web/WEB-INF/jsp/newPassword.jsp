<%@ include file="./inc/_taglibs.jsp"%>

<struct:htmlWrapper navi="" hideLogout="true" hideControl="true" hideMenu="true">

<jsp:body>
<ww:form action="generateNewPassword.action" cssClass="newPassword">
	<p>Fill in your username and e-mail below to generate a new
	password.</p>
	<table>
	<tr>
		<td>Username</td>
		<td><ww:textfield name="name" required="true" /></td>
	</tr>
	<tr>
		<td>Email</td>
		<td><ww:textfield name="email" required="true" /></td>
	</tr>
	<tr>
		<td><ww:submit /></td>
	</tr>
	</table>
</ww:form>
</jsp:body>
</struct:htmlWrapper>