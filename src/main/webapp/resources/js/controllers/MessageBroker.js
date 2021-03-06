/*!
 * Cliend-side implementation of client-server interaction protocol
 * Copyright 2014 nilebox@gmail.com
*/

function MessageBroker(url) {
	this.socket = new SockJS(url, null, {rtt:5000});
	this.stompClient = Stomp.over(this.socket);
}

MessageBroker.prototype.connect = function(documentId, notifyReceive, notifyTitleUpdate, notifyCaretUpdate, notifyDisconnect) {
	var stompClient = this.stompClient;
	stompClient.connect({}, function(frame) {
		console.log('Connected: ' + frame);
		stompClient.subscribe('/topic/operation/' + documentId, function(notification) {
			console.log('Received data: ' + notification);
			notifyReceive(JSON.parse(notification.body));
		});
		stompClient.subscribe('/topic/title/' + documentId, function(titleUpdate) {
			console.log('Received data: ' + titleUpdate);
			notifyTitleUpdate(JSON.parse(titleUpdate.body));
		});
		stompClient.subscribe('/topic/caret/' + documentId, function(titleCaret) {
			console.log('Received data: ' + titleCaret);
			notifyCaretUpdate(JSON.parse(titleCaret.body));
		});
		stompClient.subscribe('/topic/disconnect/' + documentId, function(client) {
			console.log('Received data: ' + client);
			notifyDisconnect(JSON.parse(client.body));
		});			
	});
};

MessageBroker.prototype.disconnect = function() {
	this.stompClient.disconnect();
	console.log("Disconnected");
};

MessageBroker.prototype.sendOperation = function(operation) {
	var message = JSON.stringify(operation);
	console.log("Sending operation: " + message);
	this.stompClient.send("/app/operation", {}, message);
};

MessageBroker.prototype.sendTitle = function(titleUpdate) {
	var message = JSON.stringify(titleUpdate);
	console.log("Sending title: " + message);
	this.stompClient.send("/app/title", {}, message);
};

MessageBroker.prototype.sendCaret = function(caretUpdate) {
	var message = JSON.stringify(caretUpdate);
	console.log("Sending caret: " + message);
	this.stompClient.send("/app/caret", {}, message);
};

MessageBroker.prototype.sendDisconnect = function(client) {
	var message = JSON.stringify(client);
	console.log("Sending disconnect: " + message);
	this.stompClient.send("/app/disconnect", {}, message);
};

MessageBroker.prototype.isConnectionLost = function() {
	return (this.socket.readyState === SockJS.CLOSING) || (this.socket.readyState === SockJS.CLOSED);
};
