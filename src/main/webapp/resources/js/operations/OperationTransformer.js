/*!
 * Implementation of operational transformation algorithm
 * More details about idea of the algorithm: http://www.codecommit.com/blog/java/understanding-and-applying-operational-transformation
 * Copyright 2014 nilebox@gmail.com
*/

function OperationTransformer() {};

OperationTransformer.transformBatches = function(first, second) {
	var firstTransformed = new OperationBatch();
	var secondTransformed = new OperationBatch();
	var firstIter = first.iterator();
	var secondIter = second.iterator();
	var firstOperation = null;
	var secondOperation = null;

	while ((firstOperation !== null || firstIter.hasNext()) && (secondOperation !== null || secondIter.hasNext())) {
		if (firstOperation === null) {
			firstOperation = firstIter.next();
		}
		if (secondOperation === null) {
			secondOperation = secondIter.next();
		}

		var parts = OperationTransformer.splitOperations(firstOperation, secondOperation);
		firstOperation = parts.first.second;
		secondOperation = parts.second.second;

		var transformed = OperationTransformer.transformOperations(parts.first.first, parts.second.first);
		if (transformed.first !== null) {
			firstTransformed.add(transformed.first);
		}
		if (transformed.second !== null) {
			secondTransformed.add(transformed.second);
		}
	}

	while (firstOperation !== null || firstIter.hasNext()) {
		if (firstOperation === null) {
			firstOperation = firstIter.next();
		}
		firstTransformed.add(firstOperation);
		firstOperation = null;
	}

	while (secondOperation !== null || secondIter.hasNext()) {
		if (secondOperation === null) {
			secondOperation = secondIter.next();
		}
		secondTransformed.add(secondOperation);
		secondOperation = null;
	}

	return new Pair(firstTransformed, secondTransformed);
};

OperationTransformer.splitOperations = function(first, second) {
	if (first !== null && first.getType() === 'INSERT') {
		return new Pair(
				new Pair(first, null),
				new Pair(null, second));
	} else if (second !== null && second.getType() === 'INSERT') {
		return new Pair(
				new Pair(null, first),
				new Pair(second, null));
	} else {
		return new Pair(
				first.split(second.getLength()),
				second.split(first.getLength()));
	}
};

OperationTransformer.transformOperations = function(first, second) {
	// insert, nothing -> insert, retain
	if (first !== null && first.getType() === 'INSERT') {
		return new Pair(first, OperationContainer.createRetain(first.getLength()));
	}

	// nothing, insert -> retain, insert
	if (second !== null && second.getType() === 'INSERT') {
		return new Pair(OperationContainer.createRetain(second.getLength()), second);
	}
	
	// nothing, retain -> nothing, nothing
	if (first === null || second === null)
		return new Pair(null, null);

	// retain, retain -> retain, retain
	if (first.getType() === 'RETAIN' && second.getType() === 'RETAIN') {
		return new Pair(first, second);
	}

	// delete, delete -> nothing, nothing
	if (first.getType() === 'DELETE' && second.getType() === 'DELETE') {
		return new Pair(null, null);
	}

	// delete, retain -> delete, nothing
	if (first.getType() === 'DELETE' && second.getType() === 'RETAIN') {
		return new Pair(first, null);
	}

	// retain, delete -> nothing, delete
	if (first.getType() === 'RETAIN' && second.getType() === 'DELETE') {
		return new Pair(null, second);
	}

	return new Pair(null, null);
};
