<%@ include file="../inc/_taglibs.jsp"%>
<form>
    <div class="shareIterationForm">
      <div><img src="static/img/agilefant-logo-80px.png" alt="Share Iteration" style="float: left;" />
        <div style="margin-left: 90px">
          <p>The link below provides read only access to this iteration.</p>
          <p>Use the link to keep non-Agilefant users up to date without granting them full access to your product!</p>
          <p><input type="text" name="tokenUrl" value="${ReadonlyToken}" readonly="readonly" />
        </div>
      </div>
    </div>
</form>