package ru.nilebox.collabedit.format;

import java.util.Date;

/**
 *
 * @author nile
 */
public class ClientInfo {
	private final String clientId;
	private final String username;
	private int caretPosition;
	private Date lastActivity;

	public ClientInfo(String clientId, String username) {
		this.clientId = clientId;
		this.username = username;
		this.lastActivity = new Date();
	}
	
	public String getClientId() {
		return clientId;
	}

	public String getUsername() {
		return username;
	}
	
	public int getCaretPosition() {
		return caretPosition;
	}

	public void setCaretPosition(int caretPosition) {
		this.caretPosition = caretPosition;
	}

	public Date getLastActivity() {
		return lastActivity;
	}

	public void setLastActivity(Date lastActivity) {
		this.lastActivity = lastActivity;
	}
	
}
