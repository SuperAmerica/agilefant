<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:bct userId="${user.id}"/>

<aef:menu navi="3"  pageHierarchy="${pageHierarchy}"/> 
	<ww:actionerror/>
	<ww:actionmessage/>
	<h2>Edit User</h2>
	<ww:form method="POST" action="storeUser">
		<c:if test="${user.id > 0}">
			<p>
				To keep the old password, just leave password fields empty.
			</p>
		</c:if>
		<ww:hidden name="userId" value="${user.id}"/>
		<p>		
			Full name: <ww:textfield name="user.fullName"/>
		</p>
		<p>
			User ID (used in login): <ww:textfield name="user.loginName"/>
		</p>
		<p>
			Email: <ww:textfield name="user.email" value="${user.email}"/>
		</p>
		<p>
			Password: <ww:password name="password1"/>
		<p>
		<p>
			Password again: <ww:password name="password2"/>
		</p>
			<p>
			<ww:submit value="Store"/>
		</p>
	</ww:form>
<%@ include file="./inc/_footer.jsp" %>