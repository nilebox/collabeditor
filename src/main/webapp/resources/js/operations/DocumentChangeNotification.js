/*!
 * Document content change notification format (server->client)
 * Copyright 2014 nilebox@gmail.com
*/

function DocumentChangeNotification(obj) {
	this.requestId = obj.requestId;
	this.clientId = obj.clientId;
	this.username = obj.username;	
	this.documentId = obj.documentId;	
	this.baseDocumentVersion = obj.baseDocumentVersion;
	this.newDocumentVersion = obj.newDocumentVersion;
	
	this.batch = new OperationBatch(obj.baseDocumentVersion);
	for (i = 0; i < obj.operations.length; i++) {
		var container = OperationContainer.fromObject(obj.operations[i]);
		this.batch.add(container);
	}

}