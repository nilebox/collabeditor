<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="page-header">
	<h1>Document "${doc.title}"</h1>
</div>

<div class="container">
	<div style="margin-top:40px">
		<p>
			<button type="button" class="btn btn-success">Save</button>
			<button type="button" class="btn btn-danger">Delete</button>
		</p>
		<textarea class="textarea" placeholder="Enter text ..." style="width: 810px; height: 400px;" value="${doc.contents}"></textarea>
	</div>
</div>


