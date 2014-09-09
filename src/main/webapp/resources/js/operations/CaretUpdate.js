/*!
 * CaretUpdate message format
 * Copyright 2014 nilebox@gmail.com
*/

function CaretUpdate(clientId, documentId, caretPosition) {
	this.clientId = clientId;
	this.documentId = documentId;	
	this.caretPosition = caretPosition;
}