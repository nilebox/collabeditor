package ru.nilebox.collabedit.transform.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import ru.nilebox.collabedit.operations.OperationBatch;

/**
 *
 * @author nile
 */
public class DocumentChangeNotification {
	private String requestId;
	private String clientId;
	private String username;
	private Long documentId;
	private int baseDocumentVersion;
	private int newDocumentVersion;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	public void setBaseDocumentVersion(int baseDocumentVersion) {
		this.baseDocumentVersion = baseDocumentVersion;
	}

	public int getNewDocumentVersion() {
		return newDocumentVersion;
	}

	public void setNewDocumentVersion(int newDocumentVersion) {
		this.newDocumentVersion = newDocumentVersion;
	}	

	public List<OperationContainer> getOperations() {
		return operations;
	}

	public void setOperations(List<OperationContainer> operations) {
		this.operations = operations;
	}
	
	public static DocumentChangeNotification create(DocumentChangeRequest request, OperationBatch batch, Principal principal) {
		DocumentChangeNotification notification = new DocumentChangeNotification();
		notification.setClientId(request.getClientId());
		notification.setDocumentId(request.getDocumentId());
		notification.setBaseDocumentVersion(batch.getBaseDocumentVersion());
		notification.setNewDocumentVersion(batch.getBaseDocumentVersion() + 1);
		notification.setRequestId(request.getRequestId());
		notification.setUsername(principal.getName());
		notification.setOperations(batch.toOperationContainers());
		return notification;
	}
}
