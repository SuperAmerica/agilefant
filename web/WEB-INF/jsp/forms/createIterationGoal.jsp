<%@ include file="../inc/_taglibs.jsp"%>

<aef:productList />

<ww:form action="storeNewIterationGoal"  method="post">
    <ww:hidden name="iterationGoalId" value="${iterationGoal.id}" />
    <table class="formTable">
        <tr>
            <td>Name</td>
            <td>*</td>
            <td colspan="2"><ww:textfield size="60"
                name="iterationGoal.name" /></td>
        </tr>
        <tr>
            <td>Description</td>
            <td></td>
            <td colspan="2"><ww:textarea cols="70" rows="10" cssClass="useWysiwyg" 
                name="iterationGoal.description" /></td>
        </tr>
        <tr>
            <td>Iteration</td>
            <td></td>
            <td colspan="2">
                <select name="iterationId">
                <option value="" class="inactive">(select iteration)</option>
                <c:forEach items="${productList}" var="product">
                    <option value="" class="inactive productOption">${product.name}</option>
                    <c:forEach items="${product.projects}" var="project">
                        <option value="" class="inactive projectOption">${project.name}</option>
                        <c:forEach items="${project.iterations}" var="iter">
                            <c:choose>
                                <c:when test="${iter.id == currentIterationId}">
                                    <option selected="selected" value="${iter.id}" class="iterationOption">${iter.name}</option>
                                </c:when>
                                <c:otherwise>
                                    <option value="${iter.id}" class="iterationOption">${iter.name}</option>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </c:forEach>
                </c:forEach>
            </select></td>
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
