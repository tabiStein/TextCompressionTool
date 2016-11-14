/*
 * Tabi Stein
 * TCSS 342 C - Winter 2015
 * CompressedLiterature2
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
 * A controller for the CodingTree class. Loads the file to be compressed, writes the compressed
 * file and its codes to files. Then it reads from these files and decompresses the message,
 * and displays run time statistics. Also includes test methods.
 * 
 * @author Tabi Stein
 * @version 1.0
 */
public class Main {

	
	public static final File WAR_AND_PEACE = new File("textFiles/WarAndPeace.txt");
	public static final File COMPRESSED_DESTINATION = new File("textFiles/compressed.txt");
	public static final File CODES_DESTINATION = new File("textFiles/codes.txt");
	public static final File DECODE_DESTINATION = new File("textFiles/decoded.txt");
	
	/**An alternative long test file.*/
	public static final File EXTRA_TEST_FILE = new File("textFiles/WellAtTheWorldsEnd.txt");
	public static final File EXTRA_COMPRESSED_DESTINATION = new File("textFiles/WellCompressed.txt");
	public static final File EXTRA_CODES_DEST = new File("textFiles/WellCodes.txt");
	public static final File EXTRA_DECODE_DEST = new File("textFiles/WellDecoded.txt");
	
	/**A short repetitive text with special characters -- the lyrics to "Einmal moechte ich 
	 *ein Boeser sein" by EAV.*/
	public static final File SMALL_TEST_FILE = new File("textFiles/Lyrics.txt");
	public static final File SMALL_TEST_COMPRESSED_DEST = new File("textFiles/LyricsCompressed.txt");
	public static final File SMALL_TEST_CODES_DEST = new File("textFiles/LyricsCodes.txt");
	public static final File SMALL_TEST_DECODE_DEST = new File("textFiles/LyricsDecoded.txt");
	
	
	private static final Random RANDY = new Random();
	
	
	/**
	 * Runs the compression and decode methods and/or the test methods.
	 */
	public static void main(String[] args) {
		compress(WAR_AND_PEACE, COMPRESSED_DESTINATION, 
				CODES_DESTINATION);
		decode(CODES_DESTINATION, COMPRESSED_DESTINATION, DECODE_DESTINATION);
		//compress(EXTRA_TEST_FILE, EXTRA_COMPRESSED_DESTINATION, 
				//EXTRA_CODES_DEST);
		//decode(EXTRA_CODES_DEST, EXTRA_COMPRESSED_DESTINATION, EXTRA_DECODE_DEST);
		//compress(SMALL_TEST_FILE, SMALL_TEST_COMPRESSED_DEST, 
				//SMALL_TEST_CODES_DEST);
		//decode(SMALL_TEST_CODES_DEST, SMALL_TEST_COMPRESSED_DEST, SMALL_TEST_DECODE_DEST);
		//testMapGeneration(new File(SMALL_TEST_FILE), new File(SMALL_TEST_CODES_DEST));
		//testMyPriorityQueue();
		//testMyHashTable();
		//testBitWriteAndRead();
	}

	/**
	 * Reads from the given uncompressed File, passing the text into a CodingTree. Then
	 * grabs the 1s and 0s from the CodingTree, passing them through a BitReader, and printing
	 * the ASCII characters it generates from the bits to the second file argument. Also
	 * prints the codes to the third file argument. The statistics for the operation are
	 * printed to the console.
	 * Also used for testing by passing in a smaller, more manageable File to catch bugs.
	 * @param uncompressedFile the File to compress
	 * @param compressedFile the File to write the compressed text to
	 * @param codesFile the File to write the compressed text's codes to
	 */
	private static void compress(File uncompressedFile, File compressedFile, File codesFile) {
		StringBuilder sb = new StringBuilder();

		try {
			long uncompressedSize = uncompressedFile.length();
			long startTime = System.currentTimeMillis();
			//Read from file
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(
							new FileInputStream(uncompressedFile), CodingTree.CHARSET));
			int nextChar = reader.read();
			while (nextChar > -1) {
				sb.append((char) nextChar);
				nextChar = reader.read();
			}
			reader.close();
			//Send text to CodingTree
			CodingTree tree = new CodingTree(sb.toString());
			
			//Construct a BitWriter to write to the compressedFile
			BitWriter bw = new BitWriter(compressedFile);			
			//Send 1s and 0s from tree to BitReader
			bw.append(tree.bits);			
			//Print BitReader's compressed ASCII text to compressedFile
			bw.write();
			long totalTime = System.currentTimeMillis() - startTime;

			
			MyHashTable<String, String> codes = tree.codes();	
			//Print hash table stats
			codes.stats();
			//Write codes to file
			PrintStream codesWriter = new PrintStream(codesFile, CodingTree.CHARSET);		
			codesWriter.print(codes.toString());			
			codesWriter.close();

			long compressedSize = compressedFile.length();
			System.out.println("\n  Compressed file size: " + compressedSize + " bytes");
			System.out.println("     Compression ratio: " + ((double)compressedSize/uncompressedSize));
			System.out.println("Total compression time: " + totalTime + " milliseconds");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private static void decode(File codesFile, File compressedFile, File decompressedFile) {
		MyHashTable<String, String> codes = CodingTree.codesFromFile(codesFile);
		
		//Read from compressedFile
		StringBuilder sb = new StringBuilder();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader
					(new FileInputStream(compressedFile), CodingTree.CHARSET));
			int nextChar = reader.read();
			while (nextChar >= 0) {
				sb.append((char) nextChar);
				nextChar = reader.read();
			}
			reader.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		//Turn file text into a String of 1s and 0s
		BitReader br = new BitReader(sb.toString());
		
		//Decode 1s and 0s
		String decodedText = CodingTree.decode(br.getBits(), codes);
		
		
		try {
			PrintStream decompressedWriter = new PrintStream(decompressedFile, CodingTree.CHARSET);
			decompressedWriter.print(decodedText);		
			decompressedWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}
	



	/**
	 * Passes a sequence of 1s and 0s into a BitWriter, which writes to a file
	 * using ascii characters; then reads from this file, passing the text into
	 * a BitReader; then prints the BitReader's work, which should be the
	 * original sequence of 1s and 0s plus extra 0s at the end for padding.
	 */
	private static void testBitWriteAndRead() {
		String myName = "01010100010000010100001001001001"; //TABI in ASCII
		BitWriter bw = new BitWriter(new File("nameOutFile.txt"));

		bw.append(myName);
		bw.write();
		
		//Read from file
		StringBuilder sb = new StringBuilder();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader
					(new FileInputStream("nameOutFile.txt"), "ASCII"));
			int nextChar = reader.read();
			while (nextChar >= 0) {
				sb.append((char) nextChar);
				nextChar = reader.read();
			}
			reader.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 		
		
		BitReader br = new BitReader(sb.toString());
		String readBits = br.getBits();
		System.out.println(readBits); 
		//should have extra 3 zeroes appended to end as a result of making string divisible 
		//by 7 to avoid sequences invalid to ascii. (4*8 = 24, + 3 = 27; 7 | 3)

	}
	
	private static void randomBitWriting() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 20; i++) {
			if (RANDY.nextBoolean()) {
				sb.append('1');
			} else {
				sb.append('0');
			}
		}
		String testString = sb.toString();
		System.out.println("Bits: " + testString);
		BitWriter bw = new BitWriter(new File("randomBitWriting.txt"));

		bw.append(testString);
	}

	/**
	 * Tests the put and stats method of MyHashTable of a small capacity with several test 
	 * entries. Confirms that linear probing works to handle collisions, that puting a key
	 * already contained in the table but with a new value replaces the old value associated
	 * with that key, and that an attempt to add an additional key beyond the capacity
	 * has no effect.
	 */
	private static void testMyHashTable() {
		MyHashTable<String, String> table = new MyHashTable<String, String>(4);
		table.put("searchKey1", "newValue");
		table.put("searchKey2", "newValue");
		table.put("searchKey3", "newValue");
		table.put("searchKey4", "newValue");
		table.put("searchKey1", "newValue2");
		table.put("searchKey5", "newValue");
		System.out.println(table.toString());
		table.stats();
	}	
	
	
	
	/**
	 * A basic method passing the given file into huffman tree and printing out 
	 * its map to the given file to quickly indicate bugs.
	 */
	private static void testMapGeneration(File inFile, File outFile) {
		StringBuilder sb = new StringBuilder();

		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(
							new FileInputStream(inFile), CodingTree.CHARSET));
			int nextChar = reader.read();
			while (nextChar > -1) {
				sb.append((char) nextChar);
				nextChar = reader.read();
			}
			reader.close();
			CodingTree testTree = new CodingTree(sb.toString());

			MyHashTable<String, String> codes = testTree.codes();
			
			PrintStream codesWriter = new PrintStream(outFile, CodingTree.CHARSET);
			
			codesWriter.print(codes.toString());
			
			codesWriter.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * A method to see whether MyPriorityQueue functions properly. Tested out using
	 * Integers. Prints out and adds 10 randomly generated integers < 100, then prints out
	 * the queue, and then removes each one, printing out the queue each time.
	 */
	private static void testMyPriorityQueue() {
		MyPriorityQueue<Integer> testQ = new MyPriorityQueue<Integer>();
		
		
		for (int i = 0; i < 10; i++) {
			Integer randInt = RANDY.nextInt(100);
			//System.out.println(randInt);
			System.out.println(testQ);
			testQ.add(randInt);
		}

		while (!testQ.isEmpty()) {
			System.out.println(testQ);
			testQ.remove();
		}
	}
	
}
