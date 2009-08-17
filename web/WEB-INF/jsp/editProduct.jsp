<%@ include file="./inc/_taglibs.jsp"%>
<%@ include file="./inc/_header.jsp"%>

<c:set var="divId" value="1336" scope="page" />
<aef:currentBacklog backlogId="${product.id}"/>

<aef:menu navi="backlog" title="${product.name}" menuContextId="${product.id}"/>

<ww:actionerror />
<ww:actionmessage />

<script type="text/javascript">

var productId = ${product.id};

function editProduct() {
    toggleDiv('editProductForm'); toggleDiv('descriptionDiv'); showWysiwyg('productDescription'); return false;
}

/* Initialize the SimileAjax object */
/*
var SimileAjax = {
    loaded:                 false,
    loadingScriptsCount:    0,
    error:                  null,
    params:                 { bundle:"true" }
};
SimileAjax.Platform = new Object();*/
</script>
<%--<script type="text/javascript" src="static/js/timeline/simile-ajax-bundle.js?<ww:text name="struts.agilefantReleaseId" />"></script>

<!-- Include timeline -->
<script type="text/javascript" src="static/js/timeline/timeline-load.js?<ww:text name="struts.agilefantReleaseId" />"></script>
<script type="text/javascript" src="static/js/timeline/timeline-bundle.js?<ww:text name="struts.agilefantReleaseId" />"></script>
<script type="text/javascript" src="static/js/timeline/timeline-custom.js?<ww:text name="struts.agilefantReleaseId" />"></script>
--%>
<h2><c:out value="${product.name}" /></h2>
<table>
	<tbody>
		<tr>
			<td>
			<div class="subItems" style="margin-top: 0" id="subItems_editProductDetails">
			<div class="subItemHeader">
			<table cellspacing="0" cellpadding="0">
				<tr>
				    <td class="iconsbefore">
                    </td>
					<td class="header">Details</td>
					<td class="icons">
					<table cellspacing="0" cellpadding="0">
                            <tr>
                            <td>
					   <a href="#" onclick="editProduct(); return false;"
					       class="editLink" title="Edit product details" />
					   </td>
					   </tr>
					   </table>
                    </td>
				</tr>
			</table>
			</div>
			<div class="subItemContent">
			<div id="descriptionDiv" class="descriptionDiv"
				style="display: block;">
			<table class="infoTable" cellpadding="0" cellspacing="0">
				<tr>
					<td class="info1"><ww:text name="general.uniqueId"/></td>
					<td class="info3"><aef:quickReference item="${product}" /></td>
					<td class="info4" rowspan="2">&nbsp;</td>
				</tr>
				<tr>
					<td colspan="2" class="description"><div class="backlogDescription">${product.description}</div></td>
					<td></td>
				</tr>
			</table>
			</div>

			<div id="editProductForm" style="display: none;">
			<div class="validateWrapper validateExistingProduct">
			<form id="productEditForm" action="ajax/storeProduct.action" method="post">
				<ww:hidden name="productId" value="%{product.id}" />

				<table class="formTable">
					<tr>
						<td>Name</td>
						<td>*</td>
						<td colspan="2"><ww:textfield size="60" name="product.name" /></td>
					</tr>
					<tr>
						<td>Description</td>
						<td></td>
						<td colspan="2"><ww:textarea name="product.description"
							id="productDescription" cols="70" rows="10">${aef:nl2br(product.description)}</ww:textarea></td>
					</tr>
					<tr>
						<td></td>
						<td></td>
						<c:choose>
							<c:when test="${productId == 0}">
								<td><ww:submit value="Create"
								 disabled="disabled" cssClass="undisableMe" /></td>
							</c:when>
							<c:otherwise>
								<td><ww:submit value="Save" />
								<td class="deleteButton"><ww:submit
									action="deleteProduct"
									disabled="disabled" cssClass="undisableMe"
									value="Delete" /></td>
							</c:otherwise>
						</c:choose>
					</tr>
				</table>
			</form></div></div>
			</div>
			</td>
		</tr>
	</tbody>
</table>

<table>
<%--
    <!-- The timeline -->
    <tr>
    <td>
    <div class="subItems" id="subItems_editProductRoadmap">
    <div class="subItemHeader">
    <table cellspacing="0" cellpadding="0">
                            <tr>
                                <td class="header">Product roadmap</td>
                                <td style="width: 100px;">
                                	<select id="productTimelinePeriod" onchange="updateTimelinePeriod(this);">
                                		<option value="1">display quartal</option>
                                		<option value="2">display 6 months</option>
                                		<option value="3" selected="selected">display one year</option>
                                		<option value="4">display three years</option>
                                	</select>
                                </td>
                            </tr>
                        </table>
    </div>
    
       
    <div id="productTimeline"></div>
    
    <div id="timelineLegend" style="width:100%; text-align:center; margin-bottom: 10px;">
    <table style="margin: auto; border: 1px solid #ccc;" cellpadding="2" cellspacing="2">
        <tr>
            <td><div class="timeline-band-project-green" style="display:block;width:50px;height:5px;margin:2px;">&nbsp;</div>
            <div class="timeline-band-project-yellow" style="display:block;width:50px;height:5px;margin:2px;">&nbsp;</div>
            <div class="timeline-band-project-red" style="display:block;width:50px;height:5px;margin:2px;">&nbsp;</div>
            <div class="timeline-band-project-grey" style="display:block;width:50px;height:5px;margin:2px;">&nbsp;</div>
            <div class="timeline-band-project-black" style="display:block;width:50px;height:5px;margin:2px;">&nbsp;</div></td>
            <td>Project</td>
            <td><div class="timeline-band-iteration" style="display:block;width:50px;height:5px;margin:2px;">&nbsp;</div></td>
            <td>Iteration</td>
            <td><div class="timeline-band-theme" style="display:block;width:50px;height:5px !important;margin:2px;">&nbsp;</div></td>
            <td>Theme</td>  
        </tr>
    </table>
    </div>
    
    </div>
    </td>
    </tr>
--%>
	<tr>
		<td>
			<div class="subItems" id="subItems_editProductProjectList">
				<div class="subItemHeader">
				<table cellspacing="0" cellpadding="0">
	                <tr>
	                    <td class="header">
	                    Projects
	                    </td>
	                    <td class="icons">
	                    <table cellpadding="0" cellspacing="0">
	                    <tr>
	                    <td>
	                    <ww:url id="createLink" namespace="ajax" action="createProject" includeParams="none">
          						  <ww:param name="productId" value="product.id" />
					           	</ww:url>
						<ww:a href="%{createLink}" title="Create a new project"
						  cssClass="openCreateDialog openProjectDialog" onclick="return false;">
						</ww:a>
						</td>
						</tr>
						</table>
					</td>
					</tr>
				</table>
				</div>

				<c:if test="${!empty product.children}">
				<div class="subItemContent">
				<display:table class="listTable" name="product.children"
					id="row" requestURI="editProduct.action">
							
					<display:column sortable="false" title="St." class="statusColumn">
						<%@ include file="./inc/_projectStatusIcon.jsp"%>
						<div id="projectTabContainer-${row.id}" class="tabContainer" style="overflow:visible; white-space: nowrap; width: 15px;"></div>
					</display:column>		
														
					<display:column sortable="true" sortProperty="name" title="Name">					
						<ww:url id="editLink" action="editProject" includeParams="none">
							<ww:param name="projectId" value="#attr.row.id" />
						</ww:url>
						<ww:a
							href="%{editLink}">
							${aef:html(row.name)}
						</ww:a>
					</display:column>					
					
					<display:column sortable="true" sortProperty="projectType.name"
						title="Project type">
						<div>
						<c:choose>
						<c:when test="${(!empty row.projectType)}">
							${aef:html(row.projectType.name)}
						</c:when>
						<c:otherwise>
							undefined
						</c:otherwise>
						</c:choose>
						</div>
					</display:column>
          
          <%--
					<display:column sortable="false" title="Iter. info">
						<c:out value="${row.metrics.numberOfOngoingIterations}" /> / 
						<c:out value="${row.metrics.numberOfAllIterations}" />
					</display:column>
          
					
					<display:column sortable="false" title="Assignees">
						<c:out value="${row.metrics.assignees}" />
					</display:column>
          --%>					
															
					<display:column sortable="true" title="Start date">
						<ww:date name="#attr.row.startDate" />
					</display:column>

					<display:column sortable="true" title="End date">
						<ww:date name="#attr.row.endDate" />
					</display:column>
												
					<display:column sortable="false" title="Actions">
						<img src="static/img/edit.png" alt="Edit" title="Edit project" style="cursor: pointer;" onclick="handleTabEvent('projectTabContainer-${row.id}','project',${row.id},0); return false;" />						
						<ww:url id="deleteLink" action="deleteProject" includeParams="none">
							<ww:param name="productId" value="product.id" />
							<ww:param name="projectId">${row.id}</ww:param>
              <%--
              <ww:param name="contextName">product</ww:param>
              <ww:param name="contextObjectId">${product.id}</ww:param>
              --%>
						</ww:url>
						<ww:a href="%{deleteLink}" onclick="return confirmDelete();">
							<img src="static/img/delete_18.png" alt="Delete project" title="Delete project" style="cursor: pointer;"/>
						</ww:a>
					</display:column>
				</display:table>
				</div>
				</c:if>
			</div>
		</td>
	</tr>
</table>


<ww:url id="createStoryLink" action="createStoryForm" namespace="ajax" includeParams="none">
  <ww:param name="backlogId">${product.id}</ww:param>
</ww:url>

<table>
  <tr>
    <td>
      <div class="subItems" id="subItems_editProductStoryList">
        <div class="subItemHeader">
          <table cellspacing="0" cellpadding="0">
            <tr>
              <td class="header">Stories</td>
              <td class="icons">
                <table cellpadding="0" cellspacing="0">
                  <tr>
                    <td><ww:a cssClass="openCreateDialog openStoryDialog"
                          href="%{createStoryLink}" onclick="return false;"
                          title="Create a new story">
                        </ww:a></td>
                  </tr>
                </table>     
              </td>
            </tr>
          </table>
        </div>
        <c:if test="${!empty stories}">
          <div class="subItemContent">
            <%@ include file="./inc/_storyList.jsp"%>
          </div>
        </c:if>
      </div>
    </td>
  </tr>
</table>



<%@ include file="./inc/_footer.jsp"%>