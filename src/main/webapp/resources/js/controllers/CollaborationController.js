function CollaborationController(clientId, messageBroker, documentId, documentVersion, elementController) {
	this.clientId = clientId;
	this.messageBroker = messageBroker;
	this.documentId = documentId;
	this.documentVersion = documentVersion;
	this.pendingRequestId = null;
	this.batchBuffer = new OperationBatchBuffer();
	this.elementController = elementController;
	this.elementController.onTitleChanged(this.notifyTitle.bind(this));
	this.elementController.onRemove(this.notifyRemove.bind(this));
	this.elementController.onInsert(this.notifyInsert.bind(this));
	this.elementController.onCaretChanged(this.notifyCaret.bind(this));
}

CollaborationController.prototype.notifyTitle = function(newTitle) {
	var titleUpdate = new TitleUpdate(this.clientId, this.documentId, newTitle);
	this.messageBroker.sendTitle(titleUpdate);
};

CollaborationController.prototype.notifyRemove = function(start, length) {
	//send operation to server
	var batch = new OperationBatch(this.documentVersion);
	batch.add(OperationContainer.createRetain(start));
	batch.add(OperationContainer.createDelete(length));
	this.addBatch(batch);
};

CollaborationController.prototype.notifyInsert = function(start, text) {
	//send operation to server
	var batch = new OperationBatch(this.documentVersion);
	batch.add(OperationContainer.createRetain(start));
	batch.add(OperationContainer.createInsert(text));
	this.addBatch(batch);
};

CollaborationController.prototype.notifyCaret = function(newPosition) {
	if (this.pendingRequestId !== null)
		return; // client states are different
	//send operation to server
	var caretUpdate = new CaretUpdate(this.clientId, this.documentId, newPosition);
	this.messageBroker.sendCaret(caretUpdate);
};

CollaborationController.prototype.notifyClose = function() {
	//send notification to server
	var client = new ClientMessage(this.clientId, this.documentId);
	this.messageBroker.sendDisconnect(client);
	this.messageBroker.disconnect();
};

CollaborationController.prototype.addBatch = function(batch) {
	if (this.pendingRequestId === null) {
		// Immediately send the operation to server
		sendBatch(batch);
	} else {
		// Add operation to queue
		this.batchBuffer.enqueue(batch);
	}
};

CollaborationController.prototype.addBatch = function(batch) {
	batch.baseDocumentVersion = this.documentVersion;
	var request = new DocumentChangeRequest(this.clientId, this.documentId, batch);
	this.pendingRequestId = request.requestId;
	this.messageBroker.sendOperation(request);
};

CollaborationController.prototype.sendBatchFromBuffer = function() {
	if (this.batchBuffer.size() > 0) {
		var batch = this.batchBuffer.dequeue();
		sendBatch(batch);
	}
};

CollaborationController.prototype.remoteNotify = function(obj) {
	var notification = new DocumentChangeNotification(obj);
	this.documentVersion = notification.newDocumentVersion;

	if (notification.clientId === this.clientId) {
		if (notification.requestId !== this.pendingRequestId) {
			console.error("Invalid operation index, not equal to pending: " + this.pendingRequestId);
			return;
		}
		console.log("Received operation confirmation: " + this.pendingRequestId);
		this.pendingRequestId = null;
		this.sendBatchFromBuffer();
		return;
	}
	
	var remoteBatch = this.transformRemoteBatch(notification.batch);
	this.applyRemoteBatch(remoteBatch);
	this.showRemoteCaret(notification.clientId, notification.username, remoteBatch);	
};

CollaborationController.prototype.transformRemoteBatch = function(remoteBatch) {
	for (i = 0; i < this.batchBuffer.size(); i++) {
		var batch = this.batchBuffer.get(i);
		var transformed = OperationTransformer.transformBatches(remoteBatch, batch);
		remoteBatch = transformed.first;
		batch = transformed.second;
		batch.baseDocumentVersion = remoteBatch.baseDocumentVersion + 1;
		this.batchBuffer.set(i, batch);
	}
	return remoteBatch;
};

CollaborationController.prototype.applyRemoteBatch = function(batch) {
	var oldText = this.elementController.getText();
	var newText = ContentManager.applyOperations(oldText, batch);
	
	var oldSelection = this.elementController.getTextSelection();
	var newSelection = [ContentManager.transformCaret(oldSelection[0], batch), ContentManager.transformCaret(oldSelection[1], batch)];
	
	this.elementController.setText(newText);
	this.elementController.setTextSelection(newSelection);
};

CollaborationController.prototype.remoteTitleUpdate = function(titleUpdate) {
	if (titleUpdate.clientId === this.clientId)
		return; // skip own updates
	this.elementController.setTitle(titleUpdate.title);
};

CollaborationController.prototype.remoteCaretUpdate = function(caretUpdate) {
	if (caretUpdate.clientId === this.clientId)
		return; // skip own updates
	if (this.pendingRequestId !== null)
		return; // client states are different
	this.elementController.showRemoteCaret(caretUpdate.clientId, caretUpdate.username, caretUpdate.caretPosition);
};

CollaborationController.prototype.remoteDisconnect = function(client) {
	if (client.clientId === this.clientId)
		return; // skip own updates
	this.elementController.removeRemoteClient(client.clientId);
};

CollaborationController.prototype.showRemoteCaret = function(clientId, username, batch) {
	var remoteCaret = ContentManager.getRemoteCaret(batch);
	this.elementController.showRemoteCaret(clientId, username, remoteCaret);
};
