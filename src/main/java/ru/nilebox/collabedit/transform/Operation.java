package ru.nilebox.collabedit.transform;

/**
 *
 * @author nile
 */
public class Operation {
	private String clientId;
	private int version;
	private OperationType type;
	private int position;
	private String insertedText;
	private int deleteCount;


	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}	
	
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
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
            this.position++;
        }
	}
	
	public void transformWithDelete(Operation op) throws TransformException {
		if (op.type != OperationType.Delete)
			throw new TransformException("Invalid operation type");
		
//		if(this.type == OperationType.Delete && this.position == op.position) {
//            return null;
//        }
		
		if (this.position > op.position) {
			this.position--;
		}
	}

}
