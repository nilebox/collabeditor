function ContentManager(content) {
	this.content = content;
}

ContentManager.prototype.applyOperations = function(batch) {
	var operations = batch.containers;
	var index = 0;
	for (var i = 0; i < operations.length; i++) {
		var op = operations[i];
		switch (op.getType()) {
			case 'RETAIN':
				index += op.retainOp.length;
				continue;
			case 'INSERT':
				this.content = this.content.insert(index, op.insertOp.text);
				index += op.getLength();
				continue;
			case 'DELETE':
				this.content = this.content.removeSubstring(index, op.deleteOp.length);
				continue;
		}
	}
};

ContentManager.applyOperations = function(content, batch) {
	var operations = batch.containers;
	var index = 0;
	for (var i = 0; i < operations.length; i++) {
		var op = operations[i];
		switch (op.getType()) {
			case 'RETAIN':
				index += op.retainOp.length;
				continue;
			case 'INSERT':
				content = content.insert(index, op.insertOp.text);
				index += op.getLength();
				continue;
			case 'DELETE':
				content = content.removeSubstring(index, op.deleteOp.length);
				continue;
		}
	}
	return content;
};

ContentManager.transformCursor = function(cursor, batch) {
	var operations = batch.containers;
	var index = 0;
	for (var i = 0; i < operations.length; i++) {
		var op = operations[i];
		switch (op.getType()) {
			case 'RETAIN':
				index += op.getLength();
				continue;
			case 'INSERT':
				if (index < cursor)
					cursor = cursor + op.getLength();
				continue;
			case 'DELETE':
				if (index < cursor)
					cursor = cursor - Math.min(op.getLength(), cursor - index);
				continue;
		}
	}
	return cursor;
};