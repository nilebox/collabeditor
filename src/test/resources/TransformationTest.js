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

QUnit.test("simpleTransform", function(assert) {
	var original = "A B C D E";
	var first = operationBatch(insertOp("a "), retainOp(2), insertOp("c "), retainOp(2), deleteOp(4));
	var	second = operationBatch(insertOp("b "), retainOp(6), insertOp("d "), retainOp(2), deleteOp(2));
	var transform = OperationTransformer.transformBatches(first, second);
	var	cm1 = new ContentManager(original);
	cm1.applyOperations(first);
	assert.equal(cm1.content, "a A c B E", "A transformation");
	cm1.applyOperations(transform.second);

	var	cm2 = new ContentManager(original);
	cm2.applyOperations(second);
	assert.equal(cm2.content, "b A B C d D ", "B transformation");
	cm2.applyOperations(transform.first);

	assert.equal(cm1.content, cm2.content, "A+B' vs B+A' transformation");
	assert.equal(cm1.content, "a b A c B d ", "Final transformation result");
});


