/*!
 * Document content change request format (client->server)
 * Copyright 2014 nilebox@gmail.com
*/

function DocumentChangeRequest(clientId, documentId, batch) {
	this.requestId = uuid.v4();
	this.clientId = clientId;
	this.documentId = documentId;	
	this.baseDocumentVersion = batch.baseDocumentVersion;
	this.operations = batch.getOperations();
}