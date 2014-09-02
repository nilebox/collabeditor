<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:if test="${empty root}">
	<c:set var="root" value="${pageContext.request.contextPath}" />
</c:if>

<div class="page-header">
	<h1>
		<c:set var="doctitle" value="${doc.title}" />
		<c:if test="${empty doctitle}">
			<c:set var="doctitle" value="Untitled document" />
		</c:if>
		<a href="#" id="doctitle" data-type="text" data-placement="right" data-title="Enter document title" class="editable editable-click" data-original-title="" title="">${doctitle}</a>
	</h1>
</div>

<div class="container">
	<div style="margin-top:40px">
		<p>
			<button type="button" class="btn btn-success">Save</button>
			<button type="button" class="btn btn-danger">Delete</button>
		</p>
		<textarea id="textarea" class="textarea" placeholder="Please enter text here..." style="width: 810px; height: 400px;">${doc.contents}</textarea>
	</div>
</div>

<script src="${root}/resources/js/sockjs-0.3.4.js"></script>
<script src="${root}/resources/js/stomp.js"></script>
<script src="${root}/resources/js/collab.js"></script>
<script type="text/javascript">
	var stompClient = stompConnect(${doc.id});
	var oldVal = $("#textarea").val();

	$("#textarea").on('change keyup paste', function() {
		var currentVal = $(this).val();
		if(currentVal === oldVal) {
			return; //check to prevent multiple simultaneous triggers
		}

		oldVal = currentVal;
		//action to be performed on textarea changed
		alert("changed: " + currentVal);
		stompSend(stompClient, ${doc.id}, null, currentVal);
	});
	
</script>

