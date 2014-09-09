/*!
 * Unit tests for JavaScript part of transformation
 * Copyright 2014 nilebox@gmail.com
*/

function checkTransform(assert, original, first, second, expected) {
	var transform = OperationTransformer.transformBatches(first, second);

	var cm1 = new ContentManager(original);
	cm1.applyOperations(first);
	cm1.applyOperations(transform.second);

	var cm2 = new ContentManager(original);
	cm2.applyOperations(second);
	cm2.applyOperations(transform.first);

	assert.equal(cm1.content, cm2.content, "A+B' vs B+A' transformation");
	assert.equal(cm1.content, expected, "Final transformation result");
}
;

QUnit.test("simpleTransform", function(assert) {
	var original = "A B C D E";
	var first = operationBatch(insertOp("a "), retainOp(2), insertOp("c "), retainOp(2), deleteOp(4));
	var second = operationBatch(insertOp("b "), retainOp(6), insertOp("d "), retainOp(2), deleteOp(2));
	var transform = OperationTransformer.transformBatches(first, second);
	var cm1 = new ContentManager(original);
	cm1.applyOperations(first);
	assert.equal(cm1.content, "a A c B E", "A transformation");
	cm1.applyOperations(transform.second);

	var cm2 = new ContentManager(original);
	cm2.applyOperations(second);
	assert.equal(cm2.content, "b A B C d D ", "B transformation");
	cm2.applyOperations(transform.first);

	assert.equal(cm1.content, cm2.content, "A+B' vs B+A' transformation");
	assert.equal(cm1.content, "a b A c B d ", "Final transformation result");

	// However not needed for this test case, check method's consistency
	checkTransform(assert, original, first, second, "a b A c B d ");
});

QUnit.test("splitDeleteTransform", function(assert) {
	var original = "ABCDEF";

	var first = operationBatch(retainOp(3), insertOp("x"));
	var second = operationBatch(retainOp(2), deleteOp(3));

	checkTransform(assert, original, first, second, "ABxF");
});

QUnit.test("simultaneousDeleteTransform", function(assert) {
	var original = "ABCDEF";

	var first = operationBatch(retainOp(2), deleteOp(3));
	var second = operationBatch(retainOp(2), deleteOp(2));

	checkTransform(assert, original, first, second, "ABF");
});

QUnit.test("chainTransform", function(assert) {
	var original = "ABCDEF";

	var first = operationBatch(retainOp(3), insertOp("x"));
	var second = operationBatch(retainOp(3), insertOp("y"));
	var third = operationBatch(retainOp(3), insertOp("z"));
	var fourth = operationBatch(retainOp(2), deleteOp(3));

	second = OperationTransformer.transformBatches(first, second).second;

	third = OperationTransformer.transformBatches(first, third).second;
	third = OperationTransformer.transformBatches(second, third).second;

	fourth = OperationTransformer.transformBatches(first, fourth).second;
	fourth = OperationTransformer.transformBatches(second, fourth).second;
	fourth = OperationTransformer.transformBatches(third, fourth).second;

	var cm = new ContentManager(original);
	cm.applyOperations(first);
	cm.applyOperations(second);
	cm.applyOperations(third);
	cm.applyOperations(fourth);

	assert.equal(cm.content, "ABxyzF", "Final result");
});
