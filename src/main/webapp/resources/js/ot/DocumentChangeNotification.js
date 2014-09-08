function DocumentChangeNotification(obj) {
	this.requestId = obj.requestId;
	this.clientId = obj.clientId;
	this.username = obj.username;	
	this.documentId = obj.documentId;	
	this.baseDocumentVersion = obj.baseDocumentVersion;
	this.newDocumentVersion = obj.newDocumentVersion;
	
	this.batch = new OperationBatch(obj.baseDocumentVersion);
	for (i = 0; i < obj.operations.length; i++) {
		var container = obj.operations[i];
		this.batch.add(container);
	}

}