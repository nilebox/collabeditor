<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:if test="${empty root}">
    <c:set var="root" value="${pageContext.request.contextPath}"/>
</c:if>

<div class="page-header">
    <h1>
        <c:set var="doctitle" value="${doc.title}"/>
        <c:if test="${empty doctitle}">
            <c:set var="doctitle" value="Untitled document"/>
        </c:if>
        <a href="#" id="doctitle" data-type="text" data-placement="right" data-emptytext="Untitled document" data-title="Enter document title"
           class="editable editable-click">${doc.title}</a>
    </h1>
</div>

<div class="container">
    <div class="row">


        <div class="col-md-8">
            <div id="textarea_container">
                <div id="fake_area"><span></span></div>
                <div id="caret"></div>
                <textarea id="collab_textarea" class="textarea" placeholder="Please enter text here..." rows="28">${doc.contents}</textarea>
            </div>
        </div>
        <div class="col-md-4">
            <h4>Users</h4>
            <ul id="userlist" class="user-badge-list">
                <li class="user-badge-item">
                    <span class="badge" style="background: black;">${principal.name} (You)</span>
                </li>
            </ul>
        </div>

    </div>
</div>

<script src="${root}/resources/js/ot/ContentManager.js"></script>
<script src="${root}/resources/js/ot/DocumentChangeNotification.js"></script>
<script src="${root}/resources/js/ot/DocumentChangeRequest.js"></script>
<script src="${root}/resources/js/ot/OperationBatch.js"></script>
<script src="${root}/resources/js/ot/OperationBatchBuffer.js"></script>
<script src="${root}/resources/js/ot/OperationContainer.js"></script>
<script src="${root}/resources/js/ot/OperationTransformer.js"></script>
<script src="${root}/resources/js/ot/Pair.js"></script>
<script src="${root}/resources/js/ot/Utils.js"></script>
<script src="${root}/resources/js/sockjs-0.3.4.js"></script>
<script src="${root}/resources/js/stomp.js"></script>
<script src="${root}/resources/js/uuid.js"></script>
<script src="${root}/resources/js/queue.js"></script>
<script src="${root}/resources/js/colors.js"></script>
<script src="${root}/resources/js/collab.js"></script>
<script src="${root}/resources/js/collab-textarea.js"></script>
<script src="${root}/resources/js/bootstrap.min.js"></script>
<script src="${root}/resources/js/bootstrap-editable.min.js"></script>
<script src="${root}/resources/js/detector.js"></script>
<script src="${root}/resources/js/md5.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		$('#doctitle').editable();
		var clientId = hex_md5(pstfgrpnt());
		var stompClient = stompConnect("<c:url value='/ws'/>", ${doc.id}, remoteNotify, remoteTitleUpdate);
		attachTextArea(clientId, stompClient, ${doc.id}, ${doc.version}, $("#textarea_container"), $("#collab_textarea"), $("#fake_area"), $("#doctitle"), $("#userlist"));
		<c:forEach items="${clients}" var="client">
		if (clientId !== '${client.clientId}')
		{
			addUserBadge('${client.clientId}', '${client.username}');
			setCaretPosition('${client.clientId}', ${client.caretPosition});
		}
		</c:forEach>
	});
</script>

