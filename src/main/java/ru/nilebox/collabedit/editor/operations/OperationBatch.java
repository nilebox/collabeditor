package ru.nilebox.collabedit.editor.operations;

import java.util.ArrayList;
import java.util.List;
import ru.nilebox.collabedit.messages.DocumentChangeRequest;
import ru.nilebox.collabedit.messages.OperationContainer;

/**
 * Batch of operations (single unit of text editing)
 * @author nile
 */
public class OperationBatch extends ArrayList<Operation> {
	private static final long serialVersionUID = -8561058327173530209L;

	private String clientId;
	private int baseDocumentVersion;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
	public int getBaseDocumentVersion() {
		return baseDocumentVersion;
	}

	public void setBaseDocumentVersion(int documentVersion) {
		this.baseDocumentVersion = documentVersion;
	}	
	
	public static OperationBatch fromDocumentChangeRequest(DocumentChangeRequest request) {
		OperationBatch batch = new OperationBatch();
		batch.clientId = request.getClientId();
		batch.baseDocumentVersion = request.getBaseDocumentVersion();
		for (OperationContainer c : request.getOperations()) {
			batch.add(c.toOperation());
		}
		return batch;
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
