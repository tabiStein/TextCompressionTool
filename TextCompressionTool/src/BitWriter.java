/*
 * Tabi Stein
 * TCSS 342 C - Winter 2015
 * CompressedLiterature2
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * Reads a String of 1s and 0s, building a String of bits. For encoding.
 * 
 * The 8th bit is unused because I had to resort to writing to the file in ascii, as all
 * other charsets were variable length, had surrogate characters, and had invalid bit
 * sequences; ascii is the only charset I could find that is defined for each of its
 * values [0, 127]. This means that an extra bit is written to the file for every 7 bits.
 * This is handled in my bitreader by only looking at the low 7 bits (using a bitmask) for
 * each character it receives.
 */
public class BitWriter {

	private StringBuilder bits;
	
	/**The next bits index to write. Starts at 6 and decrements to 0.*/
	private int nextBit;
	
	/**The char being written.*/
	private char currentChar;
	
	private File myOutFile;
	
	private boolean closed;
	
	/**
	 * The file where this BitWriter writes.
	 * @param outFile
	 */
	public BitWriter(File outFile) {
		myOutFile = outFile;
		bits = new StringBuilder();
		nextBit = 6;
		currentChar = 0;
		closed = false;
	}
	
	/**
	 * Appends the given String of 1s and 0s. Any character other than a 1 is treated as a
	 * 0.
	 */
	public void append(String s) {
		if (!closed) {
			// Goes through each bit from left to right
			for (int i = 0; i < s.length(); i++) {
				if (nextBit < 0) {
					// append currentChar to StringBuilder
					bits.append(currentChar);
					nextBit = 6;
					// reset currentByte
					currentChar = 0;
				}
				if (s.charAt(i) == '1') {
					currentChar += (1 << nextBit);
				}
				nextBit--;
			}
		}
	}
	
	/**
	 * Writes to file in ASCII, padding with zeros as necessary.
	 */
	public void write() {
		if (!closed) {
			closed = true;
			if (currentChar > 0) {
				// pads end with 0s
				bits.append((char) currentChar);
			}
			try {
				PrintStream ps = new PrintStream(myOutFile, "ASCII");
				ps.print(bits.toString());
				ps.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}
	
}
