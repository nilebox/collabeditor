package ru.nilebox.collabedit.operations;

import ru.nilebox.collabedit.util.Pair;

/**
 *
 * @author nile
 */
public class RetainOperation extends Operation {
	private int length;
	
	public RetainOperation() {
	}
	
	public RetainOperation(int length) {
		this.length = length;
	}

	@Override
	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}	
	
	@Override
	public Pair<Operation> splitToPair(int length) {
		return new Pair<Operation>(
				new RetainOperation(length),
				new RetainOperation(getLength() - length)
				);
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 43 * hash + this.length;
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
		final RetainOperation other = (RetainOperation) obj;
		if (this.length != other.length) {
			return false;
		}
		return true;
	}
	
}
