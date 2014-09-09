/*!
 * Unit tests for JavaScript part of composition
 * Copyright 2014 nilebox@gmail.com
*/

function checkComposition(assert, original, first, second, expected) {
	var batch = first.composeWith(second);

	var cm = new ContentManager(original);
	cm.applyOperations(batch);

	assert.equal(cm.content, expected, "Final transformation result");
};

QUnit.test("insertCompose1", function(assert) {
	var original = "A B C D E";
	var first = operationBatch(retainOp(2), insertOp("a "));
	var second = operationBatch(retainOp(4), insertOp("b "));
	assert.ok(first.canComposeWith(second));
	assert.ok(!second.canComposeWith(first));

	checkComposition(assert, original, first, second, "A a b B C D E");
});

QUnit.test("insertCompose2", function(assert) {
	var original = "ABCDE";
	var first = operationBatch(retainOp(2), insertOp("a"));
	var second = operationBatch(retainOp(3), insertOp("b"));
	var third = operationBatch(retainOp(4), insertOp("c"));
	var fourth = operationBatch(retainOp(5), insertOp("d"));
	
	assert.ok(first.canComposeWith(second));
	assert.ok(!second.canComposeWith(first));
	first = first.composeWith(second);
	
	assert.ok(first.canComposeWith(third));
	assert.ok(!third.canComposeWith(first));
	first = first.composeWith(third);
	
	assert.ok(first.canComposeWith(fourth));
	assert.ok(!fourth.canComposeWith(first));
	first = first.composeWith(fourth);
	
	var cm = new ContentManager(original);
	cm.applyOperations(first);

	var expected = "ABabcdCDE";
	assert.equal(cm.content, expected, "Final transformation result");
});

QUnit.test("deleteCompose", function(assert) {
	var original = "A B C D E F";
	var first = operationBatch(retainOp(2), deleteOp(2));
	var second = operationBatch(retainOp(2), deleteOp(4));
	assert.ok(first.canComposeWith(second));
	assert.ok(second.canComposeWith(first));

	checkComposition(assert, original, first, second, "A E F");
});
