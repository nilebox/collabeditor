package ru.nilebox.collabedit.util;

/**
 * Utility class to hold pair of two same-type objects
 * @author nile
 */
public class Pair<T> {
	private T first;
	private T second;

	public Pair(T first, T second) {
		this.first = first;
		this.second = second;
	}	
	
	public T getFirst() {
		return first;
	}

	public void setFirst(T first) {
		this.first = first;
	}

	public T getSecond() {
		return second;
	}

	public void setSecond(T second) {
		this.second = second;
	}

}
