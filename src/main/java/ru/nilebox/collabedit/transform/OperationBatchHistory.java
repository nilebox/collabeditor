package ru.nilebox.collabedit.transform;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import ru.nilebox.collabedit.operations.OperationBatch;

/**
 * History of operation batches applied to document
 * @author nile
 */
public class OperationBatchHistory {
	private final Deque<OperationBatch> batches = new ArrayDeque<OperationBatch>();
	
	public List<OperationBatch> getBatchesForDifference(int version) {
		List<OperationBatch> result = new ArrayList<OperationBatch>();
		// doc versions are monotonically increasing
		// so it's faster to go from tail to head
		Iterator<OperationBatch> it = batches.descendingIterator();
		while (it.hasNext()) {
			OperationBatch batch = it.next();
			if (batch.getDocumentVersion() < version)
				break; //there are no more conflicting batches
			result.add(batch);
		}
		return result;
	}
	
	public void addBatch(OperationBatch batch) {
		batches.add(batch);
	}
}
