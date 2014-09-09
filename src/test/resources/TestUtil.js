/*!
 * Utils for JavaScript unit tests
 * Copyright 2014 nilebox@gmail.com
*/

function retainOp(length) {
	return OperationContainer.createRetain(length);
}

function insertOp(text) {
	return OperationContainer.createInsert(text);
}

function deleteOp(length) {
	return OperationContainer.createDelete(length);
}

function operationBatch() {
	var oc = new OperationBatch();
	for (var i = 0; i < arguments.length; i++) {
		var op = arguments[i];
		oc.add(op);
	}
	return oc;
}