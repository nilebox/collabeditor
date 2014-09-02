<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="page-header">
	<h1>
		<a href="#" id="doctitle" data-type="text" data-placement="right" data-title="Enter document title" class="editable editable-click" data-original-title="" title="">${doc.title}</a>
	</h1>
</div>

<div class="container">
	<div style="margin-top:40px">
		<p>
			<button type="button" class="btn btn-success">Save</button>
			<button type="button" class="btn btn-danger">Delete</button>
		</p>
		<textarea class="textarea" placeholder="Please enter text here..." style="width: 810px; height: 400px;">${doc.contents}</textarea>
	</div>
</div>


