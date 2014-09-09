package ru.nilebox.collabedit.format;

/**
 *
 * @author nile
 */
public class CaretUpdate {
	private Long documentId;
	private String clientId;
	private String username;	
	private int caretPosition;

	public Long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
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

	public int getCaretPosition() {
		return caretPosition;
	}

	public void setCaretPosition(int caretPosition) {
		this.caretPosition = caretPosition;
	}
	
}
