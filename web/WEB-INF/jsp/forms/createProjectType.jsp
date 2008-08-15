<%@ include file="../inc/_taglibs.jsp"%>

<ww:form action="storeProjectType" method="post">
    <ww:hidden name="projectTypeId" value="${projectType.id}" />
    <table class="formTable">
        <tr>
            <td>Name</td>
            <td>*</td>
            <td colspan="2"><ww:textfield size="60" name="projectType.name" /></td>
        </tr>
        <tr>
            <td>Description</td>
            <td></td>
            <td colspan="2"><ww:textarea cols="70" rows="10"
                name="projectType.description" cssClass="useWysiwyg" /></td>
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
