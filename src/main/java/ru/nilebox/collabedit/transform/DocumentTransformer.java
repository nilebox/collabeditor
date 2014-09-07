package ru.nilebox.collabedit.transform;

import ru.nilebox.collabedit.operations.OperationBatch;
import ru.nilebox.collabedit.operations.Operation;
import ru.nilebox.collabedit.operations.InsertOperation;
import ru.nilebox.collabedit.operations.RetainOperation;
import ru.nilebox.collabedit.operations.DeleteOperation;
import java.util.Iterator;
import ru.nilebox.collabedit.util.Pair;

/**
 *
 * @author nile
 */
public class DocumentTransformer {

	public static Pair<OperationBatch> transformBatches(OperationBatch first, OperationBatch second) throws TransformationException {
		OperationBatch firstTransformed = new OperationBatch();
		OperationBatch secondTransformed = new OperationBatch();
		Iterator<Operation> firstIter = first.iterator();
		Iterator<Operation> secondIter = second.iterator();
		Operation firstOperation = null;
		Operation secondOperation = null;

		while ((firstOperation != null || firstIter.hasNext()) && (secondOperation != null || secondIter.hasNext())) {
			if (firstOperation == null) {
				firstOperation = firstIter.next();
			}
			if (secondOperation == null) {
				secondOperation = secondIter.next();
			}

			Pair<Pair<Operation>> parts = splitOperations(firstOperation, secondOperation);
			firstOperation = parts.getFirst().getSecond();
			secondOperation = parts.getSecond().getSecond();

			Pair<Operation> transformed = transformOperations(parts.getFirst().getFirst(), parts.getSecond().getFirst());
			if (transformed.getFirst() != null) {
				firstTransformed.add(transformed.getFirst());
			}
			if (transformed.getSecond() != null) {
				secondTransformed.add(transformed.getSecond());
			}
		}

		while (firstOperation != null || firstIter.hasNext()) {
			if (firstOperation == null) {
				firstOperation = firstIter.next();
			}
			firstTransformed.add(firstOperation);
			firstOperation = null;
		}

		while (secondOperation != null || secondIter.hasNext()) {
			if (secondOperation == null) {
				secondOperation = secondIter.next();
			}
			secondTransformed.add(secondOperation);
			secondOperation = null;
		}

		return new Pair<OperationBatch>(firstTransformed, secondTransformed);
	}

	private static Pair<Pair<Operation>> splitOperations(Operation first, Operation second) {
		if (first instanceof InsertOperation) {
			return new Pair<Pair<Operation>>(
					new Pair<Operation>(first, null),
					new Pair<Operation>(null, second));
		} else if (second instanceof InsertOperation) {
			return new Pair<Pair<Operation>>(
					new Pair<Operation>(null, first),
					new Pair<Operation>(second, null));
		} else {
			return new Pair<Pair<Operation>>(
					first.split(second.getLength()),
					second.split(first.getLength()));
		}
	}

	private static Pair<Operation> transformOperations(Operation first, Operation second) {
		// insert, nothing -> insert, retain
		if (first instanceof InsertOperation) {
			return new Pair<Operation>(first, new RetainOperation(first.getLength()));
		}

		// nothing, insert -> retain, insert
		if (second instanceof InsertOperation) {
			return new Pair<Operation>(new RetainOperation(second.getLength()), second);
		}

		// retain, retain -> retain, retain
		if (first instanceof RetainOperation && second instanceof RetainOperation) {
			return new Pair<Operation>(first, second);
		}

		// delete, delete -> nothing, nothing
		if (first instanceof DeleteOperation && second instanceof DeleteOperation) {
			return new Pair<Operation>(null, null);
		}

		// delete, retain -> delete, nothing
		if (first instanceof DeleteOperation && second instanceof RetainOperation) {
			return new Pair<Operation>(first, null);
		}

		// retain, delete -> nothing, delete
		if (first instanceof RetainOperation && second instanceof DeleteOperation) {
			return new Pair<Operation>(null, second);
		}

		return new Pair<Operation>(null, null);
	}
}
