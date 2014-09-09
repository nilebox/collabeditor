package ru.nilebox.collabedit.editor.operations;

import ru.nilebox.collabedit.util.Pair;

/**
 *
 * @author nile
 */
public abstract class Operation {

	/**
	 * Returns the length of the operation in relation to the original content
	 */
	public abstract int getLength();

	public Pair<Operation> split(int length) {
		// if length for split >= length of this operation then return [this operation, null]
		if (length >= this.getLength())
			return new Pair<Operation>(this, null);
		// if length for split = 0 then return [null, this operation]
		if (length == 0)
			return new Pair<Operation>(null, this);
		// otherwise return [new operation 0-length, new operation length-end]
		// - note that the operation having split called on it is not modified
		return splitToPair(length);
	}
	
	public abstract Pair<Operation> splitToPair(int length);	
}
