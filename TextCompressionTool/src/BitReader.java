/*
 * Tabi Stein
 * TCSS 342 C - Winter 2015
 * CompressedLiterature2
 */

import java.io.IOException;
import java.io.StringReader;

/**
 * Accepts a String, reading it character by character, and returns a String of 1s and 0s.
 * Only looks at the low 7 bits of each character, since it expects ascii characters.
 * For decoding.
 */
public class BitReader {

	/**Reads the message given in the constructor.*/
	private StringReader myReader;
	
	/**Signifies whether getBits has been called, to prevent IO exceptions.*/
	boolean closed;

	/**
	 * @param bits
	 */
	public BitReader(String theString) {
		myReader = new StringReader(theString);
		closed = false;
	}
	
	/**
	 * Returns a String of 1s and 0s.
	 */
	public String getBits() {
		StringBuilder sb = new StringBuilder();
		if (!closed) {			
			try {
				int nextChar = myReader.read();
				while (nextChar > -1) {
					sb.append(getSevenLowBits((char) nextChar));
					nextChar = myReader.read();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			myReader.close();
			closed = true;
		}
			return sb.toString();
		
	}
	
	/**Returns a String of 1s and 0s representing the low 7 bits of this given character
	 * in binary.
	 */
	private String getSevenLowBits(char c) {
		StringBuilder sb = new StringBuilder();
		for (int i = 6; i >= 0; i--) {
			int mask = 1 << i;
			int bit = (mask & c) == 0 ? 0 : 1; 
			sb.append(bit);
		}
		
		return sb.toString();
	}
	
}
