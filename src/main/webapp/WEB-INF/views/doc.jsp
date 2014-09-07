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
		<a href="#" id="doctitle" data-type="text" data-placement="right" data-emptytext="Untitled document" data-title="Enter document title" class="editable editable-click">${doc.title}</a>
	</h1>
</div>

		<div class="container">
			<div class="row">
				<form class="form-horizontal">
					<div class="span6">	
						<div id="textarea_container" style="margin-top:40px">
							<div id="fake_area"><span></span></div>
							<div id="caret"></div>		
							<textarea id="collab_textarea" class="textarea" placeholder="Please enter text here..." style="width: 810px; height: 400px;">${doc.contents}</textarea>
						</div>
					</div>
					<div class="navbar-right">
						<ul id="userlist">
							<li class="user-badge-item">
								<span class="badge" style="background: red;">nile</span>
							</li>
							<li class="user-badge-item">
								<span class="badge" style="background: green;">panbaraban</span>
							</li>							
						</ul>
					</div>
				</form>
			</div>
		</div>

<script src="${root}/resources/js/sockjs-0.3.4.js"></script>
<script src="${root}/resources/js/stomp.js"></script>
<script src="${root}/resources/js/uuid.js"></script>
<script src="${root}/resources/js/queue.js"></script>
<script src="${root}/resources/js/colors.js"></script>
<script src="${root}/resources/js/collab.js"></script>
<script src="${root}/resources/js/collab-textarea.js"></script>
<script src="${root}/resources/js/bootstrap.min.js"></script>
<script src="${root}/resources/js/bootstrap-editable.min.js"></script>	
<script type="text/javascript">
	$('#doctitle').editable();
	var stompClient = stompConnect("<c:url value='/ws'/>", ${doc.id}, remoteNotify, remoteTitleUpdate);
	attachTextArea(stompClient, ${doc.id}, ${doc.version}, $("#textarea_container"), $("#collab_textarea"), $("#fake_area"), $("#doctitle"), $("#userlist"));
</script>

