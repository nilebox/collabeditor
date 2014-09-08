package ru.nilebox.collabedit.transform.service;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nile
 */
public class DocumentChangeRequest {
	private String requestId;
	private String clientId;
	private Long documentId;
	private int baseDocumentVersion;
	private List<OperationContainer> operations = new ArrayList<OperationContainer>();	

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public Long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}

	public int getBaseDocumentVersion() {
		return baseDocumentVersion;
	}

	public void setBaseDocumentVersion(int documentVersion) {
		this.baseDocumentVersion = documentVersion;
	}

	public List<OperationContainer> getOperations() {
		return operations;
	}

	public void setOperations(List<OperationContainer> operations) {
		this.operations = operations;
	}
	
}
