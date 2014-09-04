function stompConnect(url, docid, notifyReceive) {
	var socket = new SockJS(url, null, {rtt:5000});
	var stompClient = Stomp.over(socket);
	stompClient.connect({}, function(frame) {
		console.log('Connected: ' + frame);
		stompClient.subscribe('/topic/collab/' + docid, function(docinfo) {
			console.log('Received data: ' + docinfo);
			notifyReceive(JSON.parse(docinfo.body));
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

function stompSend(stompClient, operation) {
	var op = JSON.stringify(operation);
	console.log("Sending operation: " + op);
	stompClient.send("/app/diff", {}, op);
}