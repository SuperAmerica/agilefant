<%@ include file="./inc/_taglibs.jsp"%>
<%@ include file="./inc/_header.jsp"%>

<%@page import="fi.hut.soberit.agilefant.model.Product"%>

<c:if test="${product.id > 0}">
	<aef:bct productId="${productId}" />
</c:if>

<c:set var="divId" value="1336" scope="page" />
<aef:menu navi="backlog" pageHierarchy="${pageHierarchy}" />
<ww:actionerror />
<ww:actionmessage />

<aef:openDialogs context="themes" id="openThemes" />

<c:forEach items="${openThemes}" var="openTheme">

</c:forEach>

<script type="text/javascript">
var themeFormSettings = {
	rules: {
	    "businessTheme.name": {
	       required: true,
	       minlength: 1
	    }
	},
	messages: {
        "businessTheme.name": {
            required: "Please enter a name",
            minlength: "Please enter a name"
        }
	}
};

function submitThemeForm() {
    if($(this).valid()) {
        $.post($(this).attr("action"), $(this).serializeArray(),
            function(data, status) {
                reloadPage();                
            });
    }
    
    return false;
}

function openEditThemeTabs(target,id) {
	var target = $("#"+target);
	if(target.attr("tab-data-loaded")) {
		if (target.is(":visible")) {
            ajaxCloseDialog("themes", id);
		}
		else {
		  ajaxOpenDialog("themes", id);
		}
		target.toggle();
	} else {
        ajaxOpenDialog("themes", id);
		target.load("businessThemeTabs.action",{businessThemeId: id},function(data, status) {
			var t = target.find(".tabs-nav").length;
			target.find(".businessThemeTabs").tabs();
			var form = target.find("form");
			form.validate(themeFormSettings);
			form.submit(submitThemeForm);
		});
		target.attr("tab-data-loaded","1");
	}
	return false;
}

function setThemeActivityStatus(themeId,status) {
	var url = "";
	if(status == true) {
		url = "ajaxActivateBusinessTheme.action";
	} else {
		url = "ajaxDeactivateBusinessTheme.action";
	}
	$.post(url,{businessThemeId: themeId},function(data,status) {
		reloadPage();
	});
}

function deleteTheme(themeId) {
	var confirm = confirmDelete();
	var url = "ajaxDeleteBusinessTheme.action";			
	
	if (confirm) {
		$.post(url,{businessThemeId: themeId},function(data) {
			reloadPage();
		});
	}
}

$(document).ready(function() {
    <c:forEach items="${openThemes}" var="openTheme">
        openEditThemeTabs("businessThemeTabContainer-${openTheme}", ${openTheme});
    </c:forEach>
    
    var createThemeForm = $("#createThemeDiv").find("form");
    createThemeForm.validate(themeFormSettings);
    createThemeForm.submit(submitThemeForm);
    
});

/* Initialize the SimileAjax object */
var SimileAjax = {
    loaded:                 false,
    loadingScriptsCount:    0,
    error:                  null,
    params:                 { bundle:"true" }
};
SimileAjax.Platform = new Object();
</script>
<script type="text/javascript" src="static/js/timeline/simile-ajax-bundle.js"></script>

<!-- Include timeline -->
<script type="text/javascript">
var productId = ${product.id};
</script>
<script type="text/javascript" src="static/js/timeline/timeline-load.js"></script>
<script type="text/javascript" src="static/js/timeline/timeline-bundle.js"></script>
<script type="text/javascript" src="static/js/timeline/timeline-custom.js"></script>

<c:choose>
    <c:when test="${productId == 0}">
        <c:set var="new" value="New" scope="page" />
    </c:when>
    <c:otherwise>
        <c:set var="new" value="" scope="page" />
    </c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${productId == 0}">
		<h2>Create product</h2>
		
		<div id="editProductForm">
			<ww:form method="post" action="store${new}Product">
	        <ww:hidden name="productId" value="${product.id}" />
	            <table class="formTable">
		            <tr>
		                <td>Name</td>
		                <td>*</td>
		                <td colspan="2"><ww:textfield size="60"
		                    name="product.name" /></td>
		            </tr>
		            <tr>
		                <td>Description</td>
		                <td></td>
		                <td colspan="2"><ww:textarea name="product.description" cssClass="useWysiwyg" 
		                    cols="70" rows="10" /></td>
		            </tr>
		            <tr>
		                <td></td>
		                <td></td>
		                <c:choose>
		                    <c:when test="${productId == 0}">
		                        <td><ww:submit value="Create" /></td>
		                    </c:when>
		                    <c:otherwise>
		                        <td><ww:submit value="Save" />
		                        <td class="deleteButton"><ww:submit
		                            onclick="return confirmDelete()" action="deleteProduct"
		                            value="Delete" /></td>
		                    </c:otherwise>
		                </c:choose>
		            </tr>
		        </table>
			</ww:form>
		</div>
	</c:when>
	
	<c:otherwise>
	<h2><c:out value="${product.name}" /></h2>		
    <table>
  		<tbody>
 	    	<tr>
    	 		<td>
            		<div class="subItems" style="margin-top: 0">
                		<div class="subItemHeader">
	                		<script type="text/javascript">
			                function expandDescription() {
			                    document.getElementById('descriptionDiv').style.maxHeight = "1000em";
			                    document.getElementById('descriptionDiv').style.overflow = "visible";
			                }
			                function collapseDescription() {
			                    document.getElementById('descriptionDiv').style.maxHeight = "12em";
			                    document.getElementById('descriptionDiv').style.overflow = "hidden";
			                }
			                function editProduct() {
			                	toggleDiv('editProductForm'); toggleDiv('descriptionDiv'); showWysiwyg('productDescription'); return false;
			                }
			                </script>


	                        <table cellspacing="0" cellpadding="0">
	                            <tr>
	                                <td class="header">Details <a href=""
	                                    onclick="return editProduct();">Edit
	                                &raquo;</a></td>
	                                <td class="icons">
	                                <a href=""
	                                    onclick="expandDescription(); return false;"> <img
	                                    src="static/img/plus.png" width="18" height="18" alt="Expand"
	                                    title="Expand" /> </a> <a href=""
	                                    onclick="collapseDescription(); return false;"> <img
	                                    src="static/img/minus.png" width="18" height="18"
	                                    alt="Collapse" title="Collapse" /> </a>
	                                </td>
	                            </tr>
	                        </table>
                      	</div>
                     	<div class="subItemContent">
							<div id="descriptionDiv" class="descriptionDiv"
								style="display: block;">
								<table class="infoTable" cellpadding="0" cellspacing="0">
								
									<tr>
										<td colspan="2" class="description">${product.description}</td>
										<td class="info4">&nbsp;</td>
									</tr>
								</table>
							</div>
						
							<div id="editProductForm" style="display: none;">
								<ww:form action="store${new}Product" method="post">
									<ww:hidden name="productId" value="${product.id}" />
	
									<table class="formTable">
										<tr>
											<td>Name</td>
											<td>*</td>
											<td colspan="2"><ww:textfield size="60"
												name="product.name" /></td>
										</tr>
										<tr>
											<td>Description</td>
											<td></td>
											<td colspan="2"><ww:textarea name="product.description" id="productDescription" 
												cols="70" rows="10" value="${aef:nl2br(product.description)}" /></td>
										</tr>
										<tr>
											<td></td>
											<td></td>
											<c:choose>
												<c:when test="${productId == 0}">
													<td><ww:submit value="Create" /></td>
												</c:when>
												<c:otherwise>
													<td><ww:submit value="Save" />
													<td class="deleteButton"><ww:submit
														onclick="return confirmDelete()" action="deleteProduct"
														value="Delete" /></td>
												</c:otherwise>
											</c:choose>
										</tr>
									</table>
								</ww:form>
					  		</div>
					  	</div>
					</td>
          		</tr>
        	</tbody>
		</table>           
	</c:otherwise>
</c:choose>

<c:if test="${product.id > 0}">
<table>
    <!-- The timeline -->
    <tr>
    <td>
    <div id="subItems">
    <div id="subItemHeader">
    <table cellspacing="0" cellpadding="0">
                            <tr>
                                <td class="header">Product roadmap</td>
                            </tr>
                        </table>
    </div>
    
       
    <div id="productTimeline"></div>
    
    <div id="timelineLegend" style="width:100%; text-align:center; margin-bottom: 10px;">
    <table style="margin: auto; border: 1px solid #ccc;" cellpadding="2" cellspacing="2">
        <tr>
            <td><div class="timeline-band-project" style="display:block;width:50px;height:5px;">&nbsp;</div></td>
            <td>Project</td>
            <td><div class="timeline-band-iteration" style="display:block;width:50px;height:5px;">&nbsp;</div></td>
            <td>Iteration</td>
            
        </tr>
    </table>
    </div>
    
    </div>
    </td>
    </tr>

	<tr>
		<td>
			<div class="subItems">
				<div class="subItemHeader">
				<table cellspacing="0" cellpadding="0">
	                <tr>
	                    <td class="header">
	                    Projects <ww:url id="createLink" action="createProject" includeParams="none">
						<ww:param name="productId" value="${product.id}" /></ww:url>
						<ww:a
					href="%{createLink}&contextViewName=editProduct&contextObjectId=${product.id}">Create new &raquo;</ww:a>
					</td>
					</tr>
				</table>
				</div>

				<c:if test="${!empty product.projects}">
				<div class="subItemContent">
				<p><display:table class="listTable" name="product.projects"
					id="row" requestURI="editProduct.action">
					<display:column sortable="true" sortProperty="name" title="Name">
						<ww:url id="editLink" action="editProject" includeParams="none">
							<ww:param name="productId" value="${product.id}" />
							<ww:param name="projectId" value="${row.id}" />
						</ww:url>

						<ww:a
							href="%{editLink}&contextViewName=editProduct&contextObjectId=${product.id}">
							${aef:html(row.name)}
						</ww:a>
					</display:column>

					<display:column sortable="true" sortProperty="projectType.name"
						title="Project type" property="projectType.name" />

					<display:column sortable="true" title="Iterations">
						${fn:length(row.iterations)}
					</display:column>

					<display:column sortable="true" title="Items">
						${fn:length(row.backlogItems)}
					</display:column>

					<display:column sortable="true" title="Effort left"
						sortProperty="totalEffortLeftSum.time"
						defaultorder="descending">
						<c:out value="${effLeftSums[row]}" />
					</display:column>

					<display:column sortable="true" title="Original estimate"
						sortProperty="totalOriginalEstimateSum.time"
						defaultorder="descending">
						<c:out value="${origEstSums[row]}" />
					</display:column>

					<display:column sortable="true" title="Start date">
						<ww:date name="#attr.row.startDate" />
					</display:column>

					<display:column sortable="true" title="End date">
						<ww:date name="#attr.row.endDate" />
					</display:column>
												
					<display:column sortable="false" title="Actions">
						<ww:url id="deleteLink" action="deleteProject"
							includeParams="none">
							<ww:param name="productId" value="${product.id}" />
							<ww:param name="projectId" value="${row.id}" />
						</ww:url>
						<ww:a href="%{deleteLink}&contextViewName=editProduct&contextObjectId=${product.id}"
							onclick="return confirmDelete()">Delete</ww:a>
					</display:column>
				</display:table></p>
				</div>
				</c:if>
			</div>
		</td>
	</tr>
</table>

<table>	
	<tr>
		<td>
			<div class="subItems">
				<div class="subItemHeader">
					<table cellspacing="0" cellpadding="0">
						<tr>
							 <td class="header">
							 Themes
							<ww:a href="#" onclick="toggleDiv('createThemeDiv'); return false;">Create new &raquo;</ww:a>
							 </td>
						</tr>
					</table>
				</div>
				
				<div style="display: none;" id="createThemeDiv">
				<ww:form action="ajaxStoreBusinessTheme" method="post">
				    <ww:hidden name="productId" value="${product.id}" />
				    <ww:hidden name="businessTheme.active" value="true" />
				    <table class="formTable">
				        <tr>
				            <td>Name</td>
				            <td>*</td>
				            <td colspan="2"><ww:textfield size="20" name="businessTheme.name" maxlength="20" /></td>
				        </tr>       
				        <tr>
				            <td>Description</td>
				            <td></td>
				            <td colspan="2"><ww:textarea cols="50" rows="7"
				                name="businessTheme.description"/></td>
				        </tr>
				        <tr>
				            <td></td>
				            <td></td>
		                    <td><ww:submit value="Create" /></td>
				            <td class="deleteButton"><ww:reset value="Cancel" onclick="toggleDiv('createThemeDiv');"/></td>
				        </tr>
				    </table>
				</ww:form>
				</div>
				
				<c:if test="${!empty activeBusinessThemes}">
				<div id="subItemContent">
				<display:table class="themeEditTable" name="activeBusinessThemes"
					id="row">
				<display:column title="Name" class="themeEditNameColumn">
					<c:out value="${row.name}" />					
					<div id="businessThemeTabContainer-${row.id}" style="overflow:visible; white-space: nowrap; width: 115px;"></div>
				</display:column>
				<display:column title="Completed BLIs">
					<c:out value="${businessThemeMetrics[row].donePercentage}" />% 
					(<c:out value="${businessThemeMetrics[row].numberOfDoneBlis}" /> /
					<c:out value="${businessThemeMetrics[row].numberOfBlis}" />)					
				</display:column>				
				<display:column title="Actions">
				<img src="static/img/edit.png" alt="Edit" title="Edit theme" style="cursor: pointer;" onclick="openEditThemeTabs('businessThemeTabContainer-${row.id}',${row.id});" />
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

<!-- Show non-active themes table only if there are any. -->
<c:if test="${!empty nonActiveBusinessThemes}">
<table>	
	<tr>
		<td>
			<div id="subItems">
				<div id="subItemHeader">
					<table cellspacing="0" cellpadding="0">
						<tr>
							 <td class="header">
							 Deactivated themes
							 </td>
						</tr>
					</table>
				</div>
								
				<div id="subItemContent">
				<display:table class="themeEditTable" name="nonActiveBusinessThemes"
					id="row">
				<display:column title="Name" class="themeEditNameColumn">
					<c:out value="${row.name}" />					
					<div id="businessThemeTabContainer-${row.id}" style="overflow:visible; white-space: nowrap; width: 115px;"></div>
				</display:column>
				<display:column title="Completed BLIs">
					<c:out value="${businessThemeMetrics[row].donePercentage}" />% 
					(<c:out value="${businessThemeMetrics[row].numberOfDoneBlis}" /> /
					<c:out value="${businessThemeMetrics[row].numberOfBlis}" />)					
				</display:column>								
				<display:column title="Actions">
				<img src="static/img/edit.png" alt="Edit" title="Edit theme" style="cursor: pointer;" onclick="openEditThemeTabs('businessThemeTabContainer-${row.id}',${row.id});" />
				<img src="static/img/enable.png" alt="Enable" title="Enable theme" style="cursor: pointer;" onclick="setThemeActivityStatus(${row.id},true); return false;return false;" />
				<img src="static/img/delete_18.png" alt="Delete" title="Delete theme" style="cursor: pointer;" onclick="deleteTheme(${row.id}); return false;" />
				</display:column>
				</display:table>															
				</div>								
			</div>
		</td>
	</tr>
</table>
</c:if>

<table>	
	<tr>
		<td>
			<div class="subItems">
			<div class="subItemHeader">
				<table cellspacing="0" cellpadding="0">
	                <tr>
	                    <td class="header">
	                    Backlog items <ww:url
					id="createBacklogItemLink" action="createBacklogItem"
					includeParams="none">
					<ww:param name="backlogId" value="${product.id}" />
				</ww:url> <ww:a
					href="%{createBacklogItemLink}&contextViewName=editProduct&contextObjectId=${product.id}">Create new &raquo;</ww:a>
					</td>
					</tr>
				</table>
			</div>			

			<c:if test="${!empty product.backlogItems}">
			<div class="subItemContent">
				<p><%@ include file="./inc/_backlogList.jsp"%>
				</p>
			</div>
			</c:if>
			</div>
		</td>
	</tr>
</table>

</c:if>
<%@ include file="./inc/_footer.jsp"%>