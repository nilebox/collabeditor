package ru.nilebox.collabedit.transform;

import java.util.ArrayList;
import java.util.List;
import ru.nilebox.collabedit.model.Document;

/**
 *
 * @author nile
 */
public class OperationHistory {
	private final Document document;
	private final List<Operation> operations = new ArrayList<Operation>();

	public OperationHistory(Document document) {
		this.document = document;
	}
	
	public Document getDocument() {
		return document;
	}
	
	public List<Operation> getOperationsForDifference(Operation operation) {
		List<Operation> result = new ArrayList<>();
		for (Operation op : operations) {
			if (op.getVersion() >= operation.getVersion()) {
				result.add(op);
			}
		}
		return result;
	}
}
