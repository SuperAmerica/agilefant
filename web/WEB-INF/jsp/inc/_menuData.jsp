<%@ include file="./_taglibs.jsp"%>

<c:choose>

<%-- Administration menu --%>
<c:when test="${navi == 'administration'}">
[

    {
        <ww:url id="editLink" namespace="/" action="settings" includeParams="none"></ww:url>
        
        "text": '<a href="${editLink}">General settings</a>',
        "hasChildren": false,
        
        <c:if test="${subnavi == 'settings'}">
        "classes": "selected path",
        </c:if>
    },
    {
        <ww:url id="editLink" namespace="/" action="listTeams" includeParams="none"></ww:url>
        
        "text": '<a href="${editLink}">Teams</a>',
        "hasChildren": false,
        
        <c:if test="${subnavi == 'teams'}">
        "classes": "selected path",
        </c:if>
    },    
    {
        <ww:url id="editLink" namespace="/" action="listUsers" includeParams="none"></ww:url>
        
        "text": '<a href="${editLink}">Users</a>',
        "hasChildren": false,
        
        <c:if test="${subnavi == 'users'}">
        "classes": "selected path",
        </c:if>
    }
]
</c:when>


<%-- Portfolio menu --%>
<c:when test="${navi == 'portfolio'}">
[
    {
        <ww:url id="editLink" namespace="/" action="projectPortfolio" includeParams="none"></ww:url>
        
        "text": '<a href="${editLink}">Current projects</a>',
        "hasChildren": false,
        
        <c:if test="${subnavi == 'portfolioProjects'}">
        "classes": "selected path",
        </c:if>
    },
    {
        <ww:url id="editLink" namespace="/" action="globalThemes" includeParams="none"></ww:url>
        
        "text": '<a href="${editLink}">Business themes</a>',
        "hasChildren": false,
        
        <c:if test="${subnavi == 'businessThemes'}">
        "classes": "selected path",
        </c:if>
    },
    {
        <ww:url id="editLink" namespace="/" action="listProjectTypes" includeParams="none"></ww:url>
        
        "text": '<a href="${editLink}">Project types</a>',
        "hasChildren": false,
        
        <c:if test="${subnavi == 'projectTypes'}">
        "classes": "selected path",
        </c:if>
    }
]
</c:when>

<%-- Backlog hierarchy --%>
<c:otherwise>
<aef:currentBacklog backlogId="${contextObjectId}"/>
<c:set var="count" value="0" />
<c:set var="amount" value="${fn:length(menuData.menuItems)}" />
[
<c:forEach items="${menuData.menuItems}" var="item">
<c:set var="count" value="${count + 1}" />


    {
        <%-- Create the link url --%>
        <ww:url id="editLink" namespace="/" action="backlogAction" includeParams="none">
            <ww:param name="backlogId">${item.id}</ww:param>
        </ww:url>
    
        <%-- Create the text --%>
        "text": '<a href="${editLink}"><c:out value="${item.name}" /></a>',
        
        <%-- Check for children --%>
        <c:choose>
            <c:when test="${aef:listContains(openBacklogs, item)}">
                <c:set var="subMenuData" value="${openDatas[item]}" />
                "expanded": true,
                "children": 
                [
                    <c:set var="subCount" value="0" />
                    <c:set var="subAmount" value="${fn:length(subMenuData.menuItems)}" />
	                <c:forEach items="${subMenuData.menuItems}" var="subItem">
	                   <c:set var="subCount" value="${subCount + 1}" />
	                   { 
                            <ww:url id="subEditLink" namespace="/" action="backlogAction" includeParams="none">
                                <ww:param name="backlogId">${subItem.id}</ww:param>
                            </ww:url>

                            "text": '<a href="${subEditLink}"><c:out value="${subItem.name}" /></a>',
                            
                            <c:choose>
                            <c:when test="${aef:listContains(openBacklogs, subItem)}">
                            <c:set var="subsubMenuData" value="${openDatas[subItem]}" />
                            "expanded": true,
                            "children":
                            [
                                <%-- And once again, iterate the children  --%>
                                <c:set var="subsubCount" value="0" />
                                <c:set var="subsubAmount" value="${fn:length(subsubMenuData.menuItems)}" />
                                <c:forEach items="${subsubMenuData.menuItems}" var="subsubItem">
                                    <c:set var="subsubCount" value="${subsubCount + 1}"/>
                                    {
                                        <ww:url id="subsubEditLink" namespace="/" action="backlogAction" includeParams="none">
                                            <ww:param name="backlogId">${subsubItem.id}</ww:param>
                                        </ww:url>            
                                    
                                        "text": '<a href="${subsubEditLink}"><c:out value="${subsubItem.name}" /></a>',
                                        
                                        <c:choose>
                                        <c:when test="${subsubItem.id == currentIterationId}">
                                            "classes": "path selected",
                                        </c:when>
                                        <c:otherwise>
                                            <c:if test="${aef:isBeforeThisDay(subsubItem.endDate)}">
                                                "classes": "past",
                                            </c:if>
                                            <c:if test="${!aef:isBeforeThisDay(subsubItem.startDate)}">
                                                "classes": "upcoming",
                                            </c:if>
                                        </c:otherwise>
                                        </c:choose>
                                    }<c:if test="${subsubAmount != subsubCount}">,</c:if>
                                </c:forEach>
                            ],
                            </c:when>
                            <c:otherwise>
                                <c:if test="${subMenuData.hasChildren[subItem]}">
                                    "hasChildren": true,
                                </c:if>
                            </c:otherwise>
                            </c:choose>
                            
                            <%-- Classes --%>
                            <c:choose>
                            <c:when test="${aef:isProject(subItem)}">
                            <c:choose>
                            <c:when test="${subItem.id == currentProjectId}">
                                <c:choose>
                                <c:when test="${!empty currentIterationId}">
                                    "classes": "path",
                                </c:when>
                                <c:otherwise>
                                    "classes": "selected path",
                                </c:otherwise>
                                </c:choose>
                            </c:when>
                            <%-- Check, if is past or upcoming --%>
                            <c:otherwise>
                                <c:if test="${aef:isBeforeThisDay(subItem.endDate)}">
                                    "classes": "past",
                                </c:if>
                                <c:if test="${!aef:isBeforeThisDay(subItem.startDate)}">
                                    "classes": "upcoming",
                                </c:if>
                            </c:otherwise>
                            </c:choose>
                            </c:when>
                            <c:when test="${aef:isIteration(subItem)}">
                                <c:choose>
                                <c:when test="${subItem.id == currentIterationId}">
                                    "classes": "selected path",
                                </c:when>
                                <c:otherwise>
                                    <c:if test="${aef:isBeforeThisDay(subItem.endDate)}">
                                        "classes": "past",
                                    </c:if>
                                    <c:if test="${!aef:isBeforeThisDay(subItem.startDate)}">
                                        "classes": "upcoming",
                                    </c:if>
                                </c:otherwise>
                                </c:choose>
                            </c:when>
                            </c:choose>
                        
                            
                            "id": "menubacklog_<c:out value="${subItem.id}" />"
                            
	                   }<c:if test="${subAmount != subCount}">,</c:if>
	                 </c:forEach>
                ],
            </c:when>
            <c:otherwise>
                <c:if test="${menuData.hasChildren[item]}">
                "hasChildren": true,
                </c:if>
            </c:otherwise>
        </c:choose>

        <%-- Classes --%>
        <c:choose>
        <c:when test="${aef:isProduct(item)}">
            <c:if test="${item.id == currentProductId}">
            <c:choose>
                <c:when test="${!empty currentProjectId}">
                    "classes": "path",
                </c:when>
                <c:otherwise>
                    "classes": "selected path",
                </c:otherwise>
            </c:choose>
            </c:if>
        </c:when>
        <c:when test="${aef:isProject(item)}">
            <c:choose>
            <c:when test="${item.id == currentProjectId}">
            <c:choose>
                <c:when test="${!empty currentIterationId}">
                    "classes": "path",
                </c:when>
                <c:otherwise>
                    "classes": "selected path",
                </c:otherwise>
            </c:choose>
            </c:when>
            <%-- Check, if is past or upcoming --%>
            <c:otherwise>
                
                <c:if test="${aef:isBeforeThisDay(item.endDate)}">
                    "classes": "past",
                </c:if>
                <c:if test="${!aef:isBeforeThisDay(item.startDate)}">
                    "classes": "upcoming",
                </c:if>
                
             </c:otherwise>
            </c:choose>
        </c:when>
        <c:when test="${aef:isIteration(item)}">
            <c:choose>
            <c:when test="${item.id == currentIterationId}">
                "classes": "selected path",
            </c:when>
            <c:otherwise>
                <c:if test="${aef:isBeforeThisDay(item.endDate)}">
                    "classes": "past",
                </c:if>
                <c:if test="${!aef:isBeforeThisDay(item.startDate)}">
                    "classes": "upcoming",
                </c:if>
            </c:otherwise>
            </c:choose>
        </c:when>
        </c:choose>

        <%-- The id --%>
        "id": "menubacklog_<c:out value="${item.id}" />"
    }<c:if test="${amount != count}">,</c:if>
</c:forEach>
]
</c:otherwise>
</c:choose>