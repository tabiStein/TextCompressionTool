/*
 * Tabi Stein
 * TCSS 342 C - Winter 2015
 * CompressedLiterature2
 */

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * A hash table with specified, fixed number of buckets that uses linear probing to handle
 * collisions and that holds keys of type K with values of type V.
 * (Note: contains a suppressed warning due to the creation of a generic array.)
 * @author Tabi Stein
 * @version 1.0
 * @param <K> the type used for keys
 * @param <V> the type used for values
 */
public class MyHashTable<K, V> {

	private Entry[] myArray;

	private int numEntries;
	
	/**
	 * Each index correspond to a number of probes taken for a key in the put() method, 
	 * and the value at that index is the number of times entries needed this many probes.
	 */
	private int[] histOfProbes;
	
	/**
	 * Creates a new hash table with the given capacity and key and value types.
	 * @param capacity the number of buckets for this MyHashTable.
	 */
	@SuppressWarnings("unchecked")
	public MyHashTable(int capacity) {
		myArray = new MyHashTable.Entry[capacity];
		numEntries = 0;
		histOfProbes = new int[10]; //a modest starting estimate for max number of probes;
								    //may need to grow.
	}
	
	/**
	 * Adds the given key to the hash table using linear probing as necessary, if 1) the 
	 * table is not full and 2) the the key isn't already in the table. In case 1, nothing
	 * happens; in case 2, the value of the given key is updated to the given value.
	 * @param searchKey the key to determine the location to put the given value.
	 * @param newValue the value stored with the key.
	 */
	public void put(K searchKey, V newValue) {
		//check if contains key first and replace its value
		int index = find(searchKey);
		if (index >= 0) { //-1 means not found
			//key found; replace value			
			myArray[index].val = newValue;
		} else if (numEntries < myArray.length) {

			int code = hash(searchKey);
			int numProbes = 0;
			if (myArray[code] != null && !myArray[code].isDeleted ) { //Check whether probing is needed
				index = (code + 1) % myArray.length; //Increment index
				numProbes = 1;
				while (myArray[index] != null && !myArray[index].isDeleted && index != code) {
					index = (index + 1) % myArray.length;
					numProbes++;
				}
				if (index == code) { //means went through every index but there was nowhere to put the key
					code = -1;
				} else {
					code = index;
				}
			}
			if (code > -1) { //Confirm a spot was found
				myArray[code] = new Entry(searchKey, newValue);
				numEntries++;
				//update probes array, growing size if needed
				if (histOfProbes.length <= numProbes) {
					histOfProbes = Arrays.copyOf(histOfProbes, numProbes * 2);
				}
				histOfProbes[numProbes]++;
			}

		}
	}
	
	/**
	 * Returns whether the given key is in this table.
	 */
	public boolean containsKey(K searchKey) {
		boolean result = false;
		int index = find(searchKey);
		if (index >= 0 && !myArray[index].isDeleted()) {
			result = true;
		}
		return result;
	}
	
	/**Returns the value associated with this given key if it is in the table, or null
	 * if it is not.*/
	public V get(K searchKey) {
		V toReturn = null;
		int index = find(searchKey);
		if (index >= 0 && !myArray[index].isDeleted()) {
			toReturn = myArray[index].val;
		}
		return toReturn;
	}
	
	/**
	 * Returns the set of all keys in this MyHashTable. If K is a mutable type, changes
	 * to any key in this set could break this MyHashTable, as a different key could have
	 * a different hashcode, and might no longer be in its expected location. (I would
	 * instead return clones of the keys, but the assignment specs didn't allow for the 
	 * keys to be cloneable).
	 * @return keySet all keys in this MyHashTable
	 */
	public Set<K> keySet() {
		Set<K> keySet = new HashSet<K>();
		for (Entry e : myArray) {

			if (e != null && !e.isDeleted()) {
				keySet.add(e.key);
			}
		}
		return keySet;
	}
	
	/**
	 * Returns the index of the given key, or -1 if not in hashmap.
	 */
	private int find(K searchKey) {
		int code = hash(searchKey);
		int index = -1; //-1 signifies the item is not in map
		if (myArray[code] != null) { //if null, can't be in table
			index = code;
			if (!myArray[index].key.equals(searchKey)) { //key not located at code - search
				index = (index + 1) % myArray.length;
				while (myArray[index] != null && index != code) { //until a null is reached
					if (myArray[index].key.equals(searchKey)) { 
						break; //found key
					}
					index = (index + 1) % myArray.length; //increment index, wrapping as needed
				}
				if (myArray[index] == null || index == code) { index = -1;} //key not found	
				
			}
		}
		return index;
	}

	/**Calculates and returns that hash code for the given key.*/
	private int hash(K key) {
		return Math.abs(key.hashCode() * 31 % myArray.length);
	}
	
	/**Prints out the statistics of this hash table to the console.*/
	public void stats() {
		System.out.println("Hash Table Stats\n================");
		System.out.println("Number of Entries: " + numEntries);
		System.out.println("Number of Buckets: " + myArray.length);

		//Determine index of max number of probes
		int maxProbe = histOfProbes.length - 1;		
		while (histOfProbes[maxProbe] <= 0) {
			maxProbe --;
		}
		
		System.out.print("Histogram of probes: [" + histOfProbes[0]);
		long totalNumProbes = 0;
		for (int i = 1; i <= maxProbe; i++) {
			System.out.print(", " + histOfProbes[i]);
			totalNumProbes += histOfProbes[i] * i;
		}
		System.out.println("]");
		
		System.out.println("Fill Percentage: " + (double) numEntries / myArray.length);
		System.out.println("Max Linear Prob: " + maxProbe);
		double avgLinProb = (maxProbe <= 0) ? 0 : (double) totalNumProbes / numEntries;
		System.out.println("Average Linear Prob: " + avgLinProb);
		
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Entry e : myArray) {
			if (e != null && !e.isDeleted) {
				sb.append(e.toString() + System.getProperty("line.separator"));
			}
		}
		return sb.toString();
	}
	
	/**
	 * Private inner class for storing Entries in MyHashTable. Holds the key and value of
	 * the entry, 
	 * @author Tabi
	 *
	 */
	private class Entry {
		
		private final K key;
		private V val;
		private boolean isDeleted;
		
		Entry(K theKey, V theVal) {
			key = theKey;
			val = theVal;
			isDeleted = false;
		}
		
		/**
		 * Marks this Entry as deleted. Not used but provided to show understanding of how
		 * linear probing works.
		 */
		private void delete() {
			isDeleted = true;
		}
		
		private boolean isDeleted() {
			return isDeleted;
		}
		
		@Override
		public String toString() {
			return "(" + key.toString() + "=" + val.toString() + ")";
		}
		
	}
	

	
}
