<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<c:set var="add_action">
	<c:url value="/users/create.html" />
</c:set>
<c:if test="${empty root}">
	<c:set var="root" value="${pageContext.request.contextPath}" />
</c:if>

<h2>Users</h2>
<c:set var="u" value="${users}"/>

<div class="list-group">
<c:forEach var="user" items="${u}" varStatus="status">
	<c:url var="users_action" value="/users/edit.html">
		<c:param name="user_id" value="${user.id}"></c:param>
	</c:url>
  <a href="${users_action}" class="list-group-item">
    ${user.username}
  </a>
    </c:forEach>
</div>
<p>
	<a class="btn btn-primary btn-lg" role="button" href="${add_action}">Create user</a>
</p>