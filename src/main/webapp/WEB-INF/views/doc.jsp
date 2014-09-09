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
        <a href="#" id="title_area" data-type="text" data-placement="right" data-emptytext="Untitled document" data-title="Enter document title"
           class="editable editable-click">${doc.title}</a>
    </h1>
</div>

<div class="container">
    <div class="row">


        <div class="col-md-8">
            <div id="text_container">
                <div id="fake_area"><span></span></div>
                <div id="caret"></div>
                <textarea id="text_area" class="textarea" placeholder="Please enter text here..." rows="28">${doc.contents}</textarea>
            </div>
        </div>
        <div class="col-md-4">
            <h4>Users</h4>
            <ul id="user_area" class="user-badge-list">
                <li class="user-badge-item">
                    <span class="badge" style="background: black;">${principal.name} (You)</span>
                </li>
            </ul>
        </div>

    </div>
</div>

<script src="${root}/resources/js/operations/CaretUpdate.js"></script>
<script src="${root}/resources/js/operations/ClientMessage.js"></script>
<script src="${root}/resources/js/operations/ContentManager.js"></script>
<script src="${root}/resources/js/operations/DocumentChangeNotification.js"></script>
<script src="${root}/resources/js/operations/DocumentChangeRequest.js"></script>
<script src="${root}/resources/js/operations/OperationBatch.js"></script>
<script src="${root}/resources/js/operations/OperationBatchBuffer.js"></script>
<script src="${root}/resources/js/operations/OperationContainer.js"></script>
<script src="${root}/resources/js/operations/OperationTransformer.js"></script>
<script src="${root}/resources/js/operations/Pair.js"></script>
<script src="${root}/resources/js/operations/TitleUpdate.js"></script>
<script src="${root}/resources/js/operations/Utils.js"></script>
<script src="${root}/resources/js/controllers/MessageBroker.js"></script>
<script src="${root}/resources/js/controllers/CollaborationController.js"></script>
<script src="${root}/resources/js/controllers/UIElementController.js"></script>
<script src="${root}/resources/js/sockjs-0.3.4.js"></script>
<script src="${root}/resources/js/stomp.js"></script>
<script src="${root}/resources/js/uuid.js"></script>
<script src="${root}/resources/js/queue.js"></script>
<script src="${root}/resources/js/colors.js"></script>
<script src="${root}/resources/js/bootstrap.min.js"></script>
<script src="${root}/resources/js/bootstrap-editable.min.js"></script>
<script src="${root}/resources/js/detector.js"></script>
<script src="${root}/resources/js/md5.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		$('#title_area').editable();
		var clientId = hex_md5(pstfgrpnt());
		var messageBroker = new MessageBroker("<c:url value='/ws'/>");
		var elementController = new UIElementController($("#text_container"), $("#text_area"), $("#fake_area"), $("#title_area"), $("#user_area"));
		var collaborationController = new CollaborationController(clientId, messageBroker, ${doc.id}, ${doc.version}, elementController);
		var remoteNotify = collaborationController.remoteNotify.bind(collaborationController);
		var remoteTitleUpdate = collaborationController.remoteTitleUpdate.bind(collaborationController);
		var remoteCaretUpdate = collaborationController.remoteCaretUpdate.bind(collaborationController);
		var remoteDisconnect = collaborationController.remoteDisconnect.bind(collaborationController);		
		var notifyClose = collaborationController.notifyClose.bind(collaborationController);		
		messageBroker.connect(${doc.id}, remoteNotify, remoteTitleUpdate, remoteCaretUpdate, remoteDisconnect);
		<c:forEach items="${clients}" var="client">
		if (clientId !== '${client.clientId}')
		{
			elementController.showRemoteCaret('${client.clientId}', '${client.username}', ${client.caretPosition});
		}
		</c:forEach>
		
		window.onbeforeunload = function() {
			notifyClose();
		};
	});
</script>

