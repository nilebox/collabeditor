package ru.nilebox.collabedit.old.transform;

import java.util.ArrayList;
import java.util.List;
import ru.nilebox.collabedit.model.Document;

/**
 *
 * @author nile
 */
public class OperationHistory {
	private final Document document;
	private final List<OperationOld> operations = new ArrayList<OperationOld>();

	public OperationHistory(Document document) {
		this.document = document;
	}
	
	public Document getDocument() {
		return document;
	}
	
	public List<OperationOld> getOperationsForDifference(OperationOld operation) {
		List<OperationOld> result = new ArrayList<>();
		for (OperationOld op : operations) {
			if (op.getVersion() >= operation.getVersion()) {
				result.add(op);
			}
		}
		return result;
	}
	
	public void addOperation(OperationOld operation) {
		operations.add(operation);
	}
}
