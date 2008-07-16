<%@ include file="_taglibs.jsp"%>
<div class="businessThemeTabsDiv">
<ul class="businessThemeTabs">

	<li><a href="#businessThemeEditTab-${businessThemeId}"><span><img src="static/img/edit.png" alt="Edit" /> Edit theme</span></a></li>
	<li><a href="#businessThemeBLITab-${businessThemeId}"><span><img src="static/img/backlog.png" alt="Backlog items" /> Backlog items</span></a></li>
</ul>
<div id="businessThemeEditTab-${businessThemeId}" class="businessThemeNaviTab">
<ww:form action="ajaxStoreBusinessTheme" method="post">
	<ww:hidden name="businessThemeId" value="${businessTheme.id}" />
	<ww:hidden name="productId" value="${businessTheme.product.id}" />
	<ww:hidden name="businessTheme.active" value="${businessTheme.active}" />
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
			<td><ww:submit value="Save" /></td>
			<td class="deleteButton"><ww:reset value="Cancel" onclick="openEditThemeTabs('businessThemeTabContainer-${businessThemeId}', ${businessThemeId});"/></td>
		</tr>
	</table>
</ww:form>
</div>

<div id="businessThemeBLITab-${businessThemeId}" class="businessThemeNaviTab">
<p>Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Etiam egestas, arcu ut sodales egestas, lacus diam molestie massa, quis dictum urna quam sed erat. Maecenas venenatis. Etiam in nunc eu est placerat interdum. Nullam facilisis hendrerit risus. Nunc vulputate, urna vitae auctor convallis, ante nisl fermentum dui, vel vestibulum leo metus quis orci. Quisque viverra ullamcorper arcu. Quisque cursus rhoncus nisi. Sed pellentesque orci nec purus. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec massa lorem, euismod vel, commodo ac, sollicitudin non, leo. Ut vehicula, leo sed pulvinar molestie, felis felis fringilla enim, non laoreet tellus enim et leo.

</p><p>Vivamus varius justo ut tellus. Praesent imperdiet eros semper lorem. Pellentesque luctus placerat leo. Vestibulum venenatis. Mauris ut nulla. Nullam ornare libero sed quam. Sed aliquet. Ut convallis elementum nunc. Praesent ac lorem. Nullam orci sapien, placerat gravida, fringilla eu, blandit sed, nisl. Donec consectetuer volutpat felis. Suspendisse potenti.

</p><p>Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Sed vitae risus quis quam lacinia sodales. Etiam enim. Etiam semper fringilla nunc. Ut vulputate. Pellentesque id nisl quis metus vehicula dictum. Pellentesque suscipit dictum nisi. Aliquam non purus. Pellentesque auctor sodales enim. Suspendisse rhoncus elit eu nisi. Aliquam erat volutpat. Praesent nulla nulla, molestie quis, mollis dignissim, hendrerit quis, urna. Nam quis felis ac ligula pharetra vehicula. Nam in libero ac justo pharetra rutrum. Ut risus.

</p><p>Donec malesuada leo eu mi. Nam pellentesque odio a mauris. Nam malesuada, tellus nec sagittis imperdiet, mi odio tristique sapien, nec vulputate augue ligula non dolor. Aliquam non pede sit amet arcu aliquam gravida. Vivamus iaculis mollis nisl. Vestibulum pretium. Lorem ipsum dolor sit amet, consectetuer adipiscing elit. In in neque. Nunc egestas, felis nec tempus euismod, purus sapien pharetra tellus, et venenatis magna lectus at est. Proin vitae sapien id ligula tincidunt viverra. Vivamus condimentum, urna ut posuere venenatis, diam neque pharetra dui, vitae semper odio sapien non lorem. In non quam. Cras id odio.

</p><p>Suspendisse vel mauris. Pellentesque tempus sollicitudin quam. Sed sagittis erat et massa. Integer eu sem. Sed pretium quam eu metus. Nunc facilisis massa in lectus cursus malesuada. Etiam et nisi at libero gravida aliquam. Suspendisse ac ipsum non sapien tincidunt tincidunt. Morbi nec est vel dolor ullamcorper dapibus. Etiam sagittis.
</p> 
</div>
</div>
