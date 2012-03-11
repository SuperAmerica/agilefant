<%@tag description="Logout div" %>

<%@taglib uri="../../tlds/aef_structure.tld" prefix="struct" %>
<%@taglib uri="../../tlds/aef.tld" prefix="aef" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="/struts-tags" prefix="ww" %>

<div id="logoutDiv">
  <a href="#" id="createNewMenuLink" onclick="return false;">Create new</a> 
  |
  <a href="editUser.action">${currentUser.fullName}</a>
  <c:if test="${currentUser.admin}">(Administrator)</c:if>
  |
  <a href="help.action">Help</a>
  |
  <a href="http://tinyurl.com/agilefant-registration-2011">Register</a>
  |
  <a href="j_spring_security_logout?exit=Logout">Logout</a>

<struct:createNewMenu />
</div>




