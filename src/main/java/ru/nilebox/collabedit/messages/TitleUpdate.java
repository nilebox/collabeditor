package ru.nilebox.collabedit.messages;

/**
 * Title change notification message format (client<->server)
 * @author nile
 */
public class TitleUpdate {
	private Long documentId;
	private String clientId;
	private String title;

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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
}
