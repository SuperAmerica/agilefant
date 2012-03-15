<%@ include file="../inc/_taglibs.jsp"%>
<form>
    <div class="shareIterationForm">
      <div><img src="static/img/agilefant-logo-80px.png" alt="Share Iteration" style="float: left;" />
        <div style="margin-left: 90px">
          <p>The link below provides read only access to this iteration.</p>
          <p>Use the link to keep non-Agilefant users up to date without granting them full access to your product!</p>
          
          <%
          	String url = request.getRequestURL().toString();
          	url = url.substring(0, url.indexOf("WEB-INF"));
          	url = url.concat("token/");
          %>
          
          <p><input type="text" id="tokenUrl" name="tokenUrl" value="<%=url%>${readonlyToken}" readonly="readonly" size="75" />
        </div>
      </div>
    </div>
</form>