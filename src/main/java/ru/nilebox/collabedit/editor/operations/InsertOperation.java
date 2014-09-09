package ru.nilebox.collabedit.editor.operations;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;
import ru.nilebox.collabedit.util.Pair;

/**
 *
 * @author nile
 */
public class InsertOperation extends Operation {
	private String text;	

	public InsertOperation() {
	}
	
	public InsertOperation(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@JsonIgnore
	@Override
	public int getLength() {
		return text.length();
	}

	@Override
	public Pair<Operation> splitToPair(int length) {
		throw new UnsupportedOperationException("Insert operation is not splittable");
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 83 * hash + Objects.hashCode(this.text);
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
		final InsertOperation other = (InsertOperation) obj;
		if (!Objects.equals(this.text, other.text)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "insert(" + text + ")";
	}
	
}
