package ru.nilebox.collabedit.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ForwardingQueue;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.Queue;

/**
 * Clone of Google Guava's EvictingQueue with added descendingIterator() method
 * @author nile
 */
public final class EvictingDeque<E> extends ForwardingQueue<E> {

	private final Deque<E> delegate;
	private final int maxSize;

	private EvictingDeque(int maxSize) {
		checkArgument(maxSize > 0, "maxSize (%s) must be positive", maxSize);
		this.delegate = new ArrayDeque<E>(maxSize);
		this.maxSize = maxSize;
	}

	/**
	 * Creates and returns a new evicting queue that will hold up to {@code maxSize} elements.
	 */
	public static <E> EvictingDeque<E> create(int maxSize) {
		return new EvictingDeque<E>(maxSize);
	}

	@Override
	protected Queue<E> delegate() {
		return delegate;
	}
	
	public Iterator<E> descendingIterator() {
		return delegate.descendingIterator();
	}

  /**
   * Adds the given element to this queue. If the queue is currently full, the element at the head
   * of the queue is evicted to make room.
   *
   * @return {@code true} always
   */
  @Override
	public boolean offer(E e) {
		return add(e);
	}

	/**
	 * Adds the given element to this queue. If the queue is currently full, the element at the head
	 * of the queue is evicted to make room.
	 *
	 * @return {@code true} always
	 */
	@Override
	public boolean add(E e) {
		checkNotNull(e);  // check before removing
		if (size() == maxSize) {
			delegate.remove();
		}
		delegate.add(e);
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends E> collection) {
		return standardAddAll(collection);
	}

	@Override
	public boolean contains(Object object) {
		return delegate().contains(checkNotNull(object));
	}

	@Override
	public boolean remove(Object object) {
		return delegate().remove(checkNotNull(object));
	}
}
