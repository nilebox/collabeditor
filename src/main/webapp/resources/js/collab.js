function stompConnect(url, docid, notifyReceive, notifyTitleUpdate) {
	var socket = new SockJS(url, null, {rtt:5000});
	var stompClient = Stomp.over(socket);
	stompClient.connect({}, function(frame) {
		console.log('Connected: ' + frame);
		stompClient.subscribe('/topic/operation/' + docid, function(notification) {
			console.log('Received data: ' + notification);
			notifyReceive(JSON.parse(notification.body));
		});
		stompClient.subscribe('/topic/title/' + docid, function(titleUpdate) {
			console.log('Received data: ' + titleUpdate);
			notifyTitleUpdate(JSON.parse(titleUpdate.body));
		});		
	});
	return stompClient;
}

function stompDisconnect(stompClient) {
	stompClient.disconnect();
	console.log("Disconnected");
}

//function stompSend(stompClient, docid, doctitle, contents) {
//	var docinfo = JSON.stringify({'id': docid, 'title': doctitle, 'contents': contents});
//	console.log("Sending docinfo: " + docinfo);
//	stompClient.send("/app/collab", {}, docinfo);
//}

function stompSendOperation(stompClient, operation) {
	var op = JSON.stringify(operation);
	console.log("Sending operation: " + op);
	stompClient.send("/app/operation", {}, op);
}

function stompSendTitle(stompClient, titleUpdate) {
	var op = JSON.stringify(titleUpdate);
	console.log("Sending title: " + op);
	stompClient.send("/app/title", {}, op);
}