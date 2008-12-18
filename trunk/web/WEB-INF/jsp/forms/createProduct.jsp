<%@ include file="../inc/_taglibs.jsp"%>

<div class="validateWrapper validateNewProduct">
<ww:form method="post"
	action="storeNewProduct">
	<ww:hidden name="productId" value="${product.id}" />
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
				cssClass="useWysiwyg" cols="70" rows="10" /></td>
		</tr>
		<tr>
			<td></td>
			<td></td>
			<td><ww:submit value="Create" id="createButton" /></td>
			<td class="deleteButton"><ww:reset value="Cancel"
				cssClass="closeDialogButton" /></td>
		</tr>
	</table>
</ww:form>
</div>