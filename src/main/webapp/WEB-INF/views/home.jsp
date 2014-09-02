<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set value="${fn:length(docs)}" var="size" />

<div class="page-header">
	<h1>Documents <small>(${size})</small></h1>
</div>

<div class="row">
	<div class="col-md-12">
		<p>
			<c:url var="doc_create" value="/docs/create.html"></c:url>
			<a href="${doc_create}" class="btn btn-success" role="button">Create New Document</a>
		</p>
		<table class="table table-striped">
            <thead>
				<tr>
					<th>Title</th>
					<th>Last modified</th>
					<th>Modified by</th>					
					<th>Created</th>
					<th>Created by</th>
				</tr>
            </thead>
            <tbody>
				<c:forEach items="${docs}" var="doc">
					<c:url var="doc_action" value="/docs/edit.html">
						<c:param name="id" value="${doc.id}"></c:param>
					</c:url>
					<c:set var="doctitle" value="${doc.title}" />
					<c:if test="${empty doctitle}">
						<c:set var="doctitle" value="Untitled document" />
					</c:if>
					<tr>
						<td><a href="${doc_action}">${doctitle}</a></td>
						<td><date><fmt:formatDate pattern="dd.MM.yyyy HH:mm" value="${doc.modified}" /></date></td>
						<td>${doc.modifiedBy}</td>
						<td><date><fmt:formatDate pattern="dd.MM.yyyy HH:mm" value="${doc.created}" /></date></td>
						<td>${doc.createdBy}</td>
					</tr>
				</c:forEach>
            </tbody>
		</table>
	</div>
</div>


