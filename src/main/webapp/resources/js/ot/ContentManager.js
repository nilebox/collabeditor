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