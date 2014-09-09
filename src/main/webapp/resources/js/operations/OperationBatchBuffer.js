/*!
 * Client-side buffer for operation batches
 * Copyright 2014 nilebox@gmail.com
*/

function OperationBatchBuffer() {
	this.elements = [];
}

OperationBatchBuffer.prototype.enqueue = function(element) {
	this.elements.push(element);
};

OperationBatchBuffer.prototype.dequeue = function() {
	return this.elements.shift();
};

OperationBatchBuffer.prototype.peek = function() {
	return this.elements[0];
};

OperationBatchBuffer.prototype.size = function() {
	return this.elements.length;
};

OperationBatchBuffer.prototype.get = function(index) {
	return this.elements[index];
};

OperationBatchBuffer.prototype.set = function(index, value) {
	this.elements[index] = value;
};

OperationBatchBuffer.prototype.setLast = function(value) {
	this.elements[elements.length-1] = value;
};

OperationBatchBuffer.prototype.addBatch = function(batch) {
	// Try to compose with latest batch in queue
	if (this.queue.size() > 0) {
		var lastBatch = queue.last();
		if (lastBatch.canComposeWith(batch)) {
			var newBatch = lastBatch.composeWith(batch);
			this.queue.setLast(newBatch);
			return;
		}
	}
	
	// Otherwise just add batch to queue
	this.queue.enqueue(batch);
};




