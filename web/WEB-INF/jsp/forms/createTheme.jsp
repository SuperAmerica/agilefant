<%@ include file="../inc/_taglibs.jsp"%>

<aef:productList />

<div class="validateWrapper validateNewTheme">
<ww:form action="ajaxStoreBusinessTheme" method="post">
	<ww:hidden name="businessTheme.active" value="true" />
	<table class="formTable">
		<tr>
			<td>Name</td>
			<td>*</td>
			<td colspan="2"><ww:textfield size="20"
				name="businessTheme.name" maxlength="20" /></td>
		</tr>
		<tr>
			<td>Product</td>
			<td>*</td>
			<td colspan="2"><select name="productId">
				<option class="inactive" value="">(select product)</option>
				<c:forEach items="${productList}" var="product">
					<c:choose>
						<c:when test="${product.id == productId}">
							<option selected="selected" value="${product.id}"
								title="${product.name}" class="productOption">${aef:out(product.name)}</option>
						</c:when>
						<c:otherwise>
							<option value="${product.id}" title="${product.name}"
								class="productOption">${aef:out(product.name)}</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select></td>
		</tr>
		<tr>
			<td>Description</td>
			<td></td>
			<td colspan="2"><ww:textarea cols="50" rows="7"
				name="businessTheme.description" /></td>
		</tr>
		<tr>
			<td></td>
			<td></td>
			<td><ww:submit value="Create" /></td>
			<td class="deleteButton"><ww:reset value="Cancel"
				cssClass="closeDialogButton" /></td>
            <td></td>
		</tr>
	</table>
</ww:form>
</div>