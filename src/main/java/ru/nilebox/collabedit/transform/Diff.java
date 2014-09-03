package ru.nilebox.collabedit.transform;

/**
 *
 * @author nile
 */
public class Diff {

	private Long id;
	private Operation operation;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}
	
}
