<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" import="java.io.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<c:set var="exception" value="${requestScope['javax.servlet.error.exception']}"/>



<div class="panel panel-danger">
  <div class="panel-heading">
    <h3 class="panel-title">Sorry, an exception has occured!</h3>
  </div>
  <div class="panel-body">
    Exception is: <%= exception %> <br/>
    <pre>
<% 
// if there is an exception
if (exception != null) {
// print the stack trace hidden in the HTML source code for debug
exception.printStackTrace(new PrintWriter(out));
}
%>
</pre>
  </div>
</div>
