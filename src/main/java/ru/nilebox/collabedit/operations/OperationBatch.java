package ru.nilebox.collabedit.operations;

import java.util.ArrayList;
import java.util.Collection;
import ru.nilebox.collabedit.transform.service.DocumentChangeRequest;
import ru.nilebox.collabedit.transform.service.OperationContainer;

/**
 *
 * @author nile
 */
public class OperationBatch extends ArrayList<Operation> {
	private static final long serialVersionUID = -8561058327173530209L;

	private int documentVersion;
	
	public int getDocumentVersion() {
		return documentVersion;
	}

	public void setDocumentVersion(int documentVersion) {
		this.documentVersion = documentVersion;
	}	
	
	public static OperationBatch fromDocumentChangeRequest(DocumentChangeRequest request) {
		OperationBatch collection = new OperationBatch();
		collection.documentVersion = request.getDocumentVersion();
		for (OperationContainer c : request.getOperations()) {
			collection.add(c.toOperation());
		}
		return collection;
	}
	
}
