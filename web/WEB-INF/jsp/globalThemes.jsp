<%@ include file="./inc/_taglibs.jsp"%>
<%@ include file="./inc/_header.jsp"%>

<aef:menu navi="portfolio" subnavi="businessThemes" title="Business Themes"/>
<ww:actionerror />
<ww:actionmessage />


<c:set var="productId" value="-1" />

<h2>Business Themes</h2>

<table> 
    <tr>
        <td>
            <div class="subItems" id="subItems_editProductThemesList">
                <div class="subItemHeader">
                    <table cellspacing="0" cellpadding="0">
                        <tr>
                             <td class="header">
                             Business themes
                             </td>
                             <td class="icons">
                             <table cellpadding="0" cellspacing="0">
		                    <tr>
		                    <td>
                             <ww:url id="createThemeLink" action="ajaxCreateBusinessTheme" includeParams="none">
                                 <ww:param name="productId" value="${productId}"></ww:param>
                             </ww:url>
                             <ww:a href="%{createThemeLink}" cssClass="openCreateDialog openThemeDialog" onclick="return false;"
                                 title="Create a new theme">
                                 </ww:a>
                             
                             </td>
                            </tr>
                             </table>
                             </td>
                        </tr>
                    </table>
                </div>
                
                
                
                <c:if test="${!empty activeBusinessThemes}">
                <div class="subItemContent">
                <display:table class="themeEditTable" name="activeBusinessThemes"
                    id="row" defaultsort="1" requestURI="editProduct.action">
                    <display:column title="Name" class="themeEditNameColumn" sortable="true" sortProperty="name">
                        <a class="nameLink" onclick="handleTabEvent('businessThemeTabContainer-${row.id}','businessTheme',${row.id},0); return false;">
                            <c:out value="${row.name}"/>
                        </a>                                                            
                        <div id="businessThemeTabContainer-${row.id}" class="tabContainer" style="overflow:visible; white-space: nowrap; width: 115px;"></div>
                    </display:column>
                    <display:column title="Description" class="themeDescriptionColumn" sortable="true">
                       ${fn:substring(aef:stripHTML(row.description), 0, 50)}
                    </display:column>           
                    <display:column title="Actions">
                        <img src="static/img/edit.png" alt="Edit" title="Edit theme" style="cursor: pointer;" onclick="handleTabEvent('businessThemeTabContainer-${row.id}','businessTheme',${row.id},0); return false;" />
                        <img src="static/img/disable.png" alt="Disable" title="Disable theme" style="cursor: pointer;" onclick="setThemeActivityStatus(${row.id},false); return false;" />
                        <img src="static/img/delete_18.png" alt="Delete" title="Delete theme" style="cursor: pointer;" onclick="deleteTheme(${row.id}); return false;" />
                    </display:column>
                </display:table>                    
                    
                    
                </div>
                </c:if>
                
            </div>
        </td>
    </tr>
</table>

<c:if test="${!empty nonActiveBusinessThemes}">
<table> 
    <tr>
        <td>
            <div class="subItems" id="subItems_editProductNonActiveThemesList">
                <div class="subItemHeader">
                    <table cellspacing="0" cellpadding="0">
                        <tr>
                             <td class="header">
                             Deactivated business themes
                             </td>
                        </tr>
                    </table>
                </div>
                                
                <div class="subItemContent">
                <display:table class="themeEditTable" name="nonActiveBusinessThemes"
                    id="row" defaultsort="1" requestURI="editProduct.action">
                    <display:column title="Name" class="themeEditNameColumn" sortable="true" sortProperty="name">
                        <a class="nameLink" onclick="handleTabEvent('businessThemeTabContainer-${row.id}','businessTheme',${row.id},0); return false;">
                            <c:out value="${row.name}"/>
                        </a>                    
                        <div id="businessThemeTabContainer-${row.id}" class="tabContainer" style="overflow:visible; white-space: nowrap; width: 115px;"></div>
                    </display:column>
                    <display:column title="Description" class="themeDescriptionColumn" sortable="true">
                       ${fn:substring(aef:stripHTML(row.description), 0, 50)}
                    </display:column>                           
                    <display:column title="Actions">
                        <img src="static/img/edit.png" alt="Edit" title="Edit theme" style="cursor: pointer;" onclick="handleTabEvent('businessThemeTabContainer-${row.id}','businessTheme',${row.id},0); return false;" />
                        <img src="static/img/enable.png" alt="Enable" title="Enable theme" style="cursor: pointer;" onclick="setThemeActivityStatus(${row.id},true); return false;" />
                        <img src="static/img/delete_18.png" alt="Delete" title="Delete theme" style="cursor: pointer;" onclick="deleteTheme(${row.id}); return false;" />
                    </display:column>
                </display:table>                                                            
                </div>                              
            </div>
        </td>
    </tr>
</table>
</c:if>

<%@ include file="./inc/_footer.jsp"%>