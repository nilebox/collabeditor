function stompConnect(docid) {
	var socket = new SockJS('/collab');
	var stompClient = Stomp.over(socket);
	stompClient.connect({}, function(frame) {
		setConnected(true);
		console.log('Connected: ' + frame);
		stompClient.subscribe('/topic/collab/' + docid, function(docinfo) {
			refreshDoc(JSON.parse(docinfo.body));
		});
	});
	return stompClient;
}

function stompDisconnect(stompClient) {
	stompClient.disconnect();
	console.log("Disconnected");
}

function stompSend(stompClient, docid, doctitle, contents) {
	stompClient.send("/app/collab", {}, JSON.stringify({'id': docid, 'title': doctitle, 'contents': contents}));
}

function refreshDoc(docInfo) {
	//document.getElementById('doctitle').innerHTML = docInfo.title;
	document.getElementById('textarea').innerHTML = docInfo.contents;
}