/*
 * Tabi Stein
 * TCSS 342 C - Winter 2015
 * CompressedLiterature2
 */

import java.util.Arrays;


/**
 * An array implementation of a priority queue holding items of type T.
 * (Note: contains a suppressed warning due to the creation of a generic array.)
 * @author Tabi Stein
 * @version 1.0
 * @param <T> the type of items stored in this MyPriorityQueue
 */
public class MyPriorityQueue<T extends Comparable<T>> {

	/**An array holding the items in this MyPriorityQueue.*/
	private T[] myArray;
	
	/**
	 * The number of items in this MyPriorityQueue. Equivalent to the last items index,
	 * because the queue uses a 1-based indexing system.
	 */
	private int size;
	
	
	/**
	 * Creates a new MyPriorityQueue storing items of type T.
	 */
	@SuppressWarnings("unchecked")
	public MyPriorityQueue() {
		myArray = (T[]) new Comparable[2];
		size = 0;
	}
	
	/**
	 * Removes and returns the minimum item in this MyPriorityQueue.
	 * @return
	 */
	public T remove() {
		T toReturn = null;
		if (size > 0) {
			toReturn = myArray[1];			
			myArray[1] = myArray[size];
			size--;
			bubbleDown(1);			
		}
		return toReturn;
	}
	
	public void add(T item) {
		if (size + 1 >= myArray.length) {
			myArray = Arrays.copyOf(myArray, myArray.length * 2);
		}
		myArray[++size] = item;
		bubbleUp(size);
		
	}
	
	/**
	 * Returns true if this MyPriorityQueue contains no items; false otherwise.
	 * @return
	 */
	public boolean isEmpty() {
		return size <= 0;
	}
	
	/**
	 * Returns the number of items in this MyPriorityQueue.
	 * @return the number of items.
	 */
	public int size() {
		return size;
	}
	
	/**
	 * Fixes the priority queue invariant by bubbling the root down until it is in the
	 * correct location.
	 */
	private void bubbleDown(int parentIndex) {
		T parent = myArray[parentIndex];
		int leftChildIndex = parentIndex*2;
		int rightChildIndex = leftChildIndex + 1;
		//check whether parent has left child
		if (size >= leftChildIndex) {
			T leftChild = myArray[leftChildIndex];
			//checks whether parent has right child
			if (size >= rightChildIndex) {
				T rightChild = myArray[rightChildIndex];
				//check if parent is larger than the min of its children; if so, swap them,
				//and try to bubble down again.
				int minIndex = leftChildIndex;
				if (rightChild.compareTo(leftChild) < 0) {
					minIndex = rightChildIndex;
				}
				if (myArray[minIndex].compareTo(parent) < 0) {
					swap(parentIndex, minIndex);
					parentIndex = minIndex;
					bubbleDown(parentIndex);
				}
			} else {
				//only has a left child; compare sizes
				if (leftChild.compareTo(parent) < 0) {
					swap(parentIndex, leftChildIndex);
					parentIndex = leftChildIndex;
					bubbleDown(parentIndex);
				}
			}
			
		}
	}
	
	/**
	 * Fixes the priority queue invariant by bubbling the root up until it is in the
	 * correct location.
	 */
	private void bubbleUp(int childIndex) {
		T child = myArray[childIndex];
		int parentIndex = childIndex/2;
		if (parentIndex >= 1) {
			T parent = myArray[parentIndex];
			if (child.compareTo(parent) < 0) {
				swap(childIndex, parentIndex);
				if(parentIndex > 1) {
					bubbleUp(parentIndex);
				}
			}
		}
	}
	
	/**
	 * Swaps the two items at the given indexes.
	 */
	private void swap(int indexA, int indexB) {
		T itemB = myArray[indexB];
		myArray[indexB] = myArray[indexA];
		myArray[indexA] = itemB;
	}
	
	/**
	 * Returns level-order String representation of this MyPriorityQueue, with each level
	 * on a separate line for readability.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		int level = 1;
		for (int i = 1; i <= size; i++) {
			if (i >= level * 2) {
				sb.append("\n");
				level = level * 2;
			}
			sb.append(myArray[i].toString() + " ");
		}
		sb.append("\n");
		return sb.toString();
	}
	
}
