package ru.nilebox.collabedit.transform;

/**
 *
 * @author nile
 */
public class Operation {
	private Long documentId;
	private String clientId;
	private String username;
	private Integer operationIndex;
	private int version;
	private int newVersion;
	private OperationType type;
	private int position;
	private String insertedText;
	private int deleteCount;

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

	public Integer getOperationIndex() {
		return operationIndex;
	}

	public void setOperationIndex(Integer operationIndex) {
		this.operationIndex = operationIndex;
	}
	
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getNewVersion() {
		return newVersion;
	}

	public void setNewVersion(int newVersion) {
		this.newVersion = newVersion;
	}

	public OperationType getType() {
		return type;
	}

	public void setType(OperationType type) {
		this.type = type;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getInsertedText() {
		return insertedText;
	}

	public void setInsertedText(String insertedText) {
		this.insertedText = insertedText;
	}

	public int getDeleteCount() {
		return deleteCount;
	}

	public void setDeleteCount(int deleteCount) {
		this.deleteCount = deleteCount;
	}
	
	public void transformWith(Operation op) throws TransformException {
		switch(op.getType()) {
			case Insert:
				transformWithInsert(op);
				break;
			case Delete:
				transformWithDelete(op);
				break;
			default:
				throw new AssertionError(op.getType().name());
		}
	}
	
	public void transformWithInsert(Operation op) throws TransformException {
		if (op.type != OperationType.Insert)
			throw new TransformException("Invalid operation type");
		
        if(this.position >= op.position) {
            this.position += op.getInsertedText().length();
        }
	}
	
	public void transformWithDelete(Operation op) throws TransformException {
		if (op.type != OperationType.Delete)
			throw new TransformException("Invalid operation type");
		
		if (op.position < 0)
			return; //skipped operation
		
		boolean deleteIntersects = false;
		if(this.type == OperationType.Delete) {
			// if current operation's start index is in range of op's delete operation
            if (this.position >= op.position && this.position < op.position + op.deleteCount) {
				deleteIntersects = true;
				int oldPosition = this.position;
				this.position = op.position + op.deleteCount;
				this.deleteCount -= this.position - oldPosition;
			}
			int endIndex = this.position + this.deleteCount - 1;
			// if current operation's end index is in range of op's delete operation
			if (endIndex >= op.position && endIndex < op.position + op.deleteCount) {
				deleteIntersects = true;
				endIndex = op.position - 1;
				this.deleteCount = endIndex - this.position + 1;
			}
			if (this.deleteCount <= 0 || this.position < 0) {
				this.position = -1;
				this.deleteCount = 0;
				return;
			}
        }
		
		if (!deleteIntersects && this.position >= op.position) {
			this.position -= op.getDeleteCount();
		}
	}

	@Override
	public String toString() {
		return "Operation{" + "documentId=" + documentId + ", clientId=" + clientId
				+ ", operationIndex=" + operationIndex + ", version=" + version
				+ ", type=" + type + ", position=" + position
				+ ", insertedText=" + insertedText + ", deleteCount=" + deleteCount + '}';
	}

}
