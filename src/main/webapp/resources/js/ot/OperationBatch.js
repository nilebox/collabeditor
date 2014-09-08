function OperationBatch(documentVersion) {
	this.documentVersion = documentVersion;
	this.containers = [];
}

OperationBatch.prototype.add = function(operation) {
	this.containers.push(operation);
};

OperationBatch.prototype.iterator = function() {
	var index = 0,
			data = this.containers,
			length = this.containers.length;

	return {
		next: function() {
			var element;
			if (!this.hasNext()) {
				return null;
			}
			element = data[index];
			index++;
			return element;
		},
		hasNext: function() {
			return index < length;
		},
		rewind: function() {
			index = 0;
			return data[index];
		},
		current: function() {
			return data[index];
		}
	};
};

OperationBatch.prototype.getMainContainer = function() {
	for (i = 0; i < this.containers.length; i++) {
		var container = this.containers[i];
		switch (container.getType()) {
			case 'INSERT':
				return container;
				break;
			case 'DELETE':
				return container;
		}
	}
	return null;
};

OperationBatch.prototype.getMainType = function() {
	for (i = 0; i < this.containers.length; i++) {
		var container = this.containers[i];
		switch (container.getType()) {
			case 'INSERT':
				return 'INSERT';
				break;
			case 'DELETE':
				return 'DELETE';
		}
	}
	return null;
};

OperationBatch.prototype.canCompose = function(operationBatch) {
	if (this.containers.length !== 2 || operationBatch.containers.length !== 2
			|| this.getMainType() !== operationBatch.getMainType())
		return false; // only simple batches are allowed to compose
	var firstRetain = this.containers[0].retainOp;
	var secondRetain = operationBatch.containers[0].retainOp;
	switch(getMainType()) {
		case 'INSERT':
			var firstInsert = this.containers[1].insertOp;
			var secondInsert = operationBatch.containers[1].insertOp;
			return (firstRetain.length <= secondRetain.length
					&& firstRetain.length + firstInsert.length >= secondRetain.length)
					|| (secondRetain.length <= firstRetain.length
					&& secondRetain.length + secondInsert.length >= firstRetain.length);
		case 'DELETE':
			var firstDelete = this.containers[1].deleteOp;
			var secondDelete = operationBatch.containers[1].deleteOp;
			return (firstRetain.length <= secondRetain.length
					&& firstRetain.length + firstDelete.length >= secondRetain.length)
					|| (secondRetain.length <= firstRetain.length
					&& secondRetain.length + secondDelete.length >= firstRetain.length);
	}
};

