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
	private RetainOperation retainOp;
	private InsertOperation insertOp;
	private DeleteOperation deleteOp;

	public RetainOperation getRetainOp() {
		return retainOp;
	}

	public void setRetainOp(RetainOperation retainOp) {
		this.retainOp = retainOp;
	}

	public InsertOperation getInsertOp() {
		return insertOp;
	}

	public void setInsertOp(InsertOperation insertOp) {
		this.insertOp = insertOp;
	}

	public DeleteOperation getDeleteOp() {
		return deleteOp;
	}

	public void setDeleteOp(DeleteOperation deleteOp) {
		this.deleteOp = deleteOp;
	}

	public Operation toOperation() {
		if (retainOp != null)
			return retainOp;
		if (insertOp != null)
			return insertOp;
		if (deleteOp != null)
			return deleteOp;
		throw new IllegalStateException("No operation is set in container");
	}
	
	public static OperationContainer fromOperation(Operation op) {
		if (op == null)
			throw new IllegalArgumentException("Operation cannot be null");
		
		OperationContainer container = new OperationContainer();
		if (op instanceof RetainOperation) {
			container.retainOp = (RetainOperation) op;
			return container;
		}
		if (op instanceof InsertOperation) {
			container.insertOp = (InsertOperation) op;
			return container;
		}
		if (op instanceof DeleteOperation) {
			container.deleteOp = (DeleteOperation) op;
			return container;
		}
		throw new IllegalArgumentException("Unknown type of operation: " + op.getClass().getCanonicalName());
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 89 * hash + Objects.hashCode(this.retainOp);
		hash = 89 * hash + Objects.hashCode(this.insertOp);
		hash = 89 * hash + Objects.hashCode(this.deleteOp);
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
		if (!Objects.equals(this.retainOp, other.retainOp)) {
			return false;
		}
		if (!Objects.equals(this.insertOp, other.insertOp)) {
			return false;
		}
		if (!Objects.equals(this.deleteOp, other.deleteOp)) {
			return false;
		}
		return true;
	}
	
	
}
