<%@ include file="../jsp/inc/_taglibs.jsp"%>

<%@tag description="menu"%>

<%@attribute name="navi"%>

<script type="text/javascript">
function changeTab(select_navi) {
	navi = select_navi;
}
</script>

<div id="outer_wrapper">
<div id="wrapper">

<!-- Tabs -->

<!-- Basic -->
<c:choose>
    <c:when test="${navi == 'basic'}">
        <li class="selected">
    </c:when>
    <c:otherwise>
        <li>
    </c:otherwise>
</c:choose>
	<a href="">
    <img src="static/img/dailyWork.png" alt="Basic" />      
    Basic
    </a>
</li>
<!-- /Basic -->

<!-- BLIs -->
<c:choose>
    <c:when test="${navi == 'blis'}">
        <li class="selected">
    </c:when>
    <c:otherwise>
        <li>
    </c:otherwise>
</c:choose>
	<a href="">
    <img src="static/img/backlog.png" alt="BLIs" />    
    BLIs
    </a>
</li>
<!-- /BLIs -->

</ul>
<!-- /Tabs -->

<!-- The main page begins -->
<div id="editMain">
