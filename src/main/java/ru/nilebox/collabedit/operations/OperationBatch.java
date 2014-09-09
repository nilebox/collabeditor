package ru.nilebox.collabedit.operations;

import java.util.ArrayList;
import java.util.List;
import ru.nilebox.collabedit.format.DocumentChangeRequest;
import ru.nilebox.collabedit.format.OperationContainer;

/**
 *
 * @author nile
 */
public class OperationBatch extends ArrayList<Operation> {
	private static final long serialVersionUID = -8561058327173530209L;

	private int baseDocumentVersion;
	
	public int getBaseDocumentVersion() {
		return baseDocumentVersion;
	}

	public void setBaseDocumentVersion(int documentVersion) {
		this.baseDocumentVersion = documentVersion;
	}	
	
	public static OperationBatch fromDocumentChangeRequest(DocumentChangeRequest request) {
		OperationBatch collection = new OperationBatch();
		collection.baseDocumentVersion = request.getBaseDocumentVersion();
		for (OperationContainer c : request.getOperations()) {
			collection.add(c.toOperation());
		}
		return collection;
	}
	
	public List<OperationContainer> toOperationContainers() {
		List<OperationContainer> containers = new ArrayList<OperationContainer>();
		for (Operation op : this) {
			OperationContainer container = OperationContainer.fromOperation(op);
			containers.add(container);
		}
		return containers;
	}
	
}
