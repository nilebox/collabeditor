package ru.nilebox.collabedit.transform.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Objects;
import ru.nilebox.collabedit.operations.DeleteOperation;
import ru.nilebox.collabedit.operations.InsertOperation;
import ru.nilebox.collabedit.operations.Operation;
import ru.nilebox.collabedit.operations.RetainOperation;

/**
 *
 * @author nile
 */
@JsonInclude(Include.NON_NULL)
public class OperationContainer {
	private RetainOperation retain;
	private InsertOperation insert;
	private DeleteOperation delete;

	public RetainOperation getRetain() {
		return retain;
	}

	public void setRetain(RetainOperation retain) {
		this.retain = retain;
	}

	public InsertOperation getInsert() {
		return insert;
	}

	public void setInsert(InsertOperation insert) {
		this.insert = insert;
	}

	public DeleteOperation getDelete() {
		return delete;
	}

	public void setDelete(DeleteOperation delete) {
		this.delete = delete;
	}
	
	public Operation toOperation() {
		if (retain != null)
			return retain;
		if (insert != null)
			return insert;
		if (delete != null)
			return delete;
		throw new IllegalStateException("No operation is set in container");
	}
	
	public static OperationContainer fromOperation(Operation op) {
		if (op == null)
			throw new IllegalArgumentException("Operation cannot be null");
		
		OperationContainer container = new OperationContainer();
		if (op instanceof RetainOperation) {
			container.retain = (RetainOperation) op;
			return container;
		}
		if (op instanceof InsertOperation) {
			container.insert = (InsertOperation) op;
			return container;
		}
		if (op instanceof DeleteOperation) {
			container.delete = (DeleteOperation) op;
			return container;
		}
		throw new IllegalArgumentException("Unknown type of operation: " + op.getClass().getCanonicalName());
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 89 * hash + Objects.hashCode(this.retain);
		hash = 89 * hash + Objects.hashCode(this.insert);
		hash = 89 * hash + Objects.hashCode(this.delete);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final OperationContainer other = (OperationContainer) obj;
		if (!Objects.equals(this.retain, other.retain)) {
			return false;
		}
		if (!Objects.equals(this.insert, other.insert)) {
			return false;
		}
		if (!Objects.equals(this.delete, other.delete)) {
			return false;
		}
		return true;
	}
	
	
}
