<%@ include file="../jsp/inc/_taglibs.jsp"%>
<%@tag description="Create unique object id text and link"%>
<%@attribute name="item" required="true" type="java.lang.Object"%>
<aef:resolveObjectNamespace item="${item}" var="item_unique">
${item_unique} ( ${item_unique_url} )
</aef:resolveObjectNamespace>