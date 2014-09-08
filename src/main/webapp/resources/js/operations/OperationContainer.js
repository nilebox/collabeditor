function OperationContainer() {
	this.retainOp = null;
	this.insertOp = null;	
	this.deleteOp = null;
}

OperationContainer.prototype.getType = function() {
	if (this.retainOp !== null)
		return 'RETAIN';
	if (this.insertOp !== null)
		return 'INSERT';
	if (this.deleteOp !== null)
		return 'DELETE';
	return null;
};

OperationContainer.prototype.getLength = function() {
	switch(this.getType()) {
		case 'RETAIN':
			return this.retainOp.length;
		case 'INSERT':
			return this.insertOp.text.length;
		case 'DELETE':
			return this.deleteOp.length;
	}
};

OperationContainer.prototype.split = function(length) {
	// if length for split >= length of this operation then return [this operation, null]
	if (length >= this.getLength())
		return new Pair(this, null);
	// if length for split = 0 then return [null, this operation]
	if (length === 0)
		return new Pair(null, this);
	// otherwise return [new operation 0-length, new operation length-end]
	// - note that the operation having split called on it is not modified
	switch(this.getType()) {
		case 'RETAIN':
			return new Pair(
					new OperationContainer.createRetain(length),
					new OperationContainer.createRetain(this.getLength() - length)
					);
		case 'DELETE':
			return new Pair(
					new OperationContainer.createDelete(length),
					new OperationContainer.createDelete(this.getLength() - length)
					);
	}
};

OperationContainer.createRetain = function(length) {
	var container = new OperationContainer();
	container.retainOp = new RetainOperation(length);
	return container;
};

OperationContainer.createDelete = function(length) {
	var container = new OperationContainer();
	container.deleteOp = new DeleteOperation(length);
	return container;	
};

OperationContainer.createInsert = function(text) {
	var container = new OperationContainer();
	container.insertOp = new InsertOperation(text);
	return container;	
};

OperationContainer.fromObject = function(obj) {
	var container = new OperationContainer();
	
	if (obj.retainOp && obj.retainOp !== null)
		container.retainOp = new RetainOperation(obj.retainOp.length);
	else if (obj.insertOp && obj.insertOp !== null)
		container.insertOp = new InsertOperation(obj.insertOp.text);
	else if (obj.deleteOp && obj.deleteOp !== null)
		container.deleteOp = new DeleteOperation(obj.deleteOp.length);
	return container;
}


function RetainOperation(length) {
	this.length = length;
};

function DeleteOperation(length) {
	this.length = length;
}

function InsertOperation(text) {
	this.text = text;
}


