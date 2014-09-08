/////////////////////////////////////////////////////
// Unit tests for JavaScript part of composition
/////////////////////////////////////////////////////

function checkComposition(assert, original, first, second, expected) {
	var batch = first.composeWith(second);

	var cm = new ContentManager(original);
	cm.applyOperations(batch);

	assert.equal(cm.content, expected, "Final transformation result");
};

QUnit.test("insertCompose", function(assert) {
	var original = "A B C D E";
	var first = operationBatch(retainOp(2), insertOp("a "));
	var second = operationBatch(retainOp(4), insertOp("b "));
	assert.ok(first.canComposeWith(second));
	assert.ok(!second.canComposeWith(first));

	checkComposition(assert, original, first, second, "A a b B C D E");
});

QUnit.test("deleteCompose", function(assert) {
	var original = "A B C D E F";
	var first = operationBatch(retainOp(2), deleteOp(2));
	var second = operationBatch(retainOp(2), deleteOp(4));
	assert.ok(first.canComposeWith(second));
	assert.ok(second.canComposeWith(first));

	checkComposition(assert, original, first, second, "A E F");
});
