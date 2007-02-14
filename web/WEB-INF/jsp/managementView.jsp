<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:menu navi="${contextName}" /> 

<p>
        <img src="drawGantChart.action"/>
</p>

<p>Portfolio hierarchy:</p>

<aef:productList/>
<c:if test="${empty productList}">
No products
</c:if>
<ul>
<c:forEach items="${productList}" var="product">
<!-- 
	<ww:url id="productLink" action="managementView" includeParams="none"/>
	<li>
		<ww:a href="%{productLink}">${ product.name }</ww:a>
	</li>
	-->
			<li>
	${ product.name }
			</li>
	<ul>
		<c:forEach items="${product.deliverables}" var="deliverable">
<!-- 
			<ww:url id="deliverableLink" action="managementView" includeParams="none"/>
			<li>
				<ww:a href="%{deliverableLink}">${ deliverable.name }</ww:a>
			</li>
	 -->
			<li>
	 ${ deliverable.name }
			</li>
	 		<ul>
				<c:forEach items="${deliverable.iterations}" var="iteration">
					<ww:url id="iterationLink" action="viewIteration" includeParams="none">
						<ww:param name="iterationId" value="${iteration.id}"/>												
					</ww:url>
					<li>
						<ww:a href="%{iterationLink}">${ iteration.name }</ww:a>
				 		<ul>
							<c:forEach items="${iteration.iterationGoals}" var="goal">
								<li>
									${ goal.name } - <i>${ goal.description } </i>
			
								</li>
							</c:forEach>
						</ul>



					</li>
				</c:forEach>
			</ul>
		</c:forEach>

	</ul>
</c:forEach>
</ul>


<%@ include file="./inc/_footer.jsp" %>
