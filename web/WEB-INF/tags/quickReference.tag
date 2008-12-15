<%@ include file="../jsp/inc/_taglibs.jsp"%>
<%@tag description="Create unique object id text and link"%>
<%@attribute name="item" required="true" type="java.lang.Object"%>
<aef:resolveObjectNamespace item="${item}" var="item_unique">
<a href="qr.action?id=${item_unique}" style="cursor: default;" onclick="return false;">${item_unique}</a> 
</aef:resolveObjectNamespace>