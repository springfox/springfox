<%@ page import="org.springframework.context.ApplicationContext" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="springfox.documentation.swagger.ui.ApiResourceLocator" %>
<%@ page import="springfox.documentation.swagger.ui.SwaggerApi" %>
<ol>
  <%
    ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(request.getServletContext());
    ApiResourceLocator apiResourceLocator = (ApiResourceLocator) context.getBean("apiResourceLocator");
    for (SwaggerApi swaggerApi : apiResourceLocator.resources()) {
  %>
  <li>
    <%=swaggerApi.getTitle() + "-" + swaggerApi.getUri()%>
  </li>

  <%
    }
  %>
</ol>