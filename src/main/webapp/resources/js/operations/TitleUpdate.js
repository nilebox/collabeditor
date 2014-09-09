/*!
 * Title change notification message format (client<->server)
 * Copyright 2014 nilebox@gmail.com
*/

function TitleUpdate(clientId, documentId, title) {
	this.clientId = clientId;
	this.documentId = documentId;	
	this.title = title;
}

