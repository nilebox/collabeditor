/*!
 * Controller which runs operational transformation logic
 * and provides communication between UIElementController and MessageBroker 
 * Copyright 2014 nilebox@gmail.com
*/

function CollaborationController(clientId, messageBroker, documentId, documentVersion, elementController) {
	this.clientId = clientId;
	this.messageBroker = messageBroker;
	this.documentId = documentId;
	this.documentVersion = documentVersion;
	this.pendingRequestId = null;
	this.pendingBatch = null;
	this.batchBuffer = new OperationBatchBuffer();
	this.elementController = elementController;
	this.elementController.onTitleChanged(this.notifyTitle.bind(this));
	this.elementController.onRemove(this.notifyRemove.bind(this));
	this.elementController.onInsert(this.notifyInsert.bind(this));
	this.elementController.onCaretChanged(this.notifyCaret.bind(this));
}

CollaborationController.prototype.checkConnection = function() {
	if (!this.messageBroker.isConnectionLost())
		return; // OK
	this.elementController.reloadPage();
};

CollaborationController.prototype.notifyTitle = function(newTitle) {
	this.checkConnection();	
	var titleUpdate = new TitleUpdate(this.clientId, this.documentId, newTitle);
	this.messageBroker.sendTitle(titleUpdate);
};

CollaborationController.prototype.notifyRemove = function(start, length) {
	this.checkConnection();	
	//send operation to server
	var batch = new OperationBatch(this.documentVersion);
	batch.add(OperationContainer.createRetain(start));
	batch.add(OperationContainer.createDelete(length));
	this.addBatch(batch);
};

CollaborationController.prototype.notifyInsert = function(start, text) {
	this.checkConnection();	
	//send operation to server
	var batch = new OperationBatch(this.documentVersion);
	batch.add(OperationContainer.createRetain(start));
	batch.add(OperationContainer.createInsert(text));
	this.addBatch(batch);
};

CollaborationController.prototype.notifyCaret = function(newPosition) {
	this.checkConnection();
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
		this.sendBatch(batch);
	} else {
		// Add operation to queue
		this.batchBuffer.enqueue(batch);
	}
};

CollaborationController.prototype.sendBatch = function(batch) {
	batch.baseDocumentVersion = this.documentVersion;
	var request = new DocumentChangeRequest(this.clientId, this.documentId, batch);
	this.pendingBatch = batch;
	this.pendingRequestId = request.requestId;
	this.messageBroker.sendOperation(request);
};

CollaborationController.prototype.sendBatchFromBuffer = function() {
	if (this.batchBuffer.size() > 0) {
		var batch = this.batchBuffer.dequeue();
		this.sendBatch(batch);
	}
};

CollaborationController.prototype.remoteNotify = function(obj) {
	// Convert JSON obj into notification
	var notification = new DocumentChangeNotification(obj);
	if (this.documentVersion >= notification.newDocumentVersion)
		console.error("Duplicate document version: " + notification.newDocumentVersion);

	if (notification.clientId === this.clientId) {
		if (notification.requestId !== this.pendingRequestId) {
			console.error("Invalid operation index, not equal to pending: " + this.pendingRequestId);
			return;
		}
		console.log("Received operation confirmation: " + this.pendingRequestId);
		this.pendingRequestId = null;
		this.documentVersion = notification.newDocumentVersion;
		this.sendBatchFromBuffer();
		return;
	} else {
		// Before applying remote operation, ensure that there are no unreported local changes
		this.elementController.handleTextChanged();
		// Apply remote operation
		this.documentVersion = notification.newDocumentVersion;		
		var remoteBatch = this.transformRemoteBatch(notification.batch);
		this.applyRemoteBatch(remoteBatch);
		this.showRemoteCaret(notification.clientId, notification.username, remoteBatch);
	}
};

CollaborationController.prototype.transformRemoteBatch = function(remoteBatch) {
	if (this.pendingRequestId !== null) {
		// We need to transform with pending batch first
		var transformed = OperationTransformer.transformBatches(remoteBatch, this.pendingBatch);
		remoteBatch = transformed.first;
	}
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
