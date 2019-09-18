import java.util.Arrays;

/**
 * Class representing a circular suffix array
 * @author ckingsley
 *
 */
public class CircularSuffixArray {
	private final char[] text;
	private Suffix[] suffixes;
	
	/**
	 * Constructor for a circular suffix array of s
	 * @param s the string to generate the circular suffix array
	 */
	public CircularSuffixArray(String s) {
		text = s.toCharArray();
		suffixes = new Suffix[ text.length ];
		for (int i = 0; i < suffixes.length; i++) {
			suffixes[i] = new Suffix(i);
		}
		Arrays.sort(suffixes);
	}
    
	
	/**
	 * Returns the length of the text to be compressed
	 * @return length of the text to be compressed
	 */
	public int length() {
		return text.length;
	}
    
	
	/**
	 * Returns index of ith sorted suffix
	 * @param i the index
	 * @return index of ith sorted suffix
	 */
	public int index(int i) {
    	return(suffixes[i].startIndex);
    }
	
	
	/**
	 * Inner class representing the circular permuted strings
	 * @author ckingsley
	 *
	 */
	private class Suffix implements Comparable<Suffix> {
		int startIndex;
		
		/**
		 * Constructor
		 * @param startIndex starting index of the circularly permuted string for this suffix
		 */
		Suffix (int startIndex) {
			this.startIndex = startIndex;
		}
		
		/**
		 * Comparable implementation to sort permuted strings lexographically
		 */
		public int compareTo(Suffix other) {
			for (int i = 0; i < CircularSuffixArray.this.text.length; i++) {
				char thisChar = getCharAt(i), otherChar = other.getCharAt(i);
				if (thisChar > otherChar) {
					return 1;
				} else if (thisChar < otherChar) {
					return -1;
				}
			}
			
			return 0;
		}
		
		/**
		 * Returns the character at the specified position in the circularly permuted string
		 * @param pos position (0 based) in the circularly permuted string
		 * @return the character at the specified position in the circularly permuted string
		 */
		private char getCharAt(int pos) {
			char[] text = CircularSuffixArray.this.text;
			pos = startIndex + pos;
			
			if (pos >= text.length) {
				pos = pos - text.length;
			}
			
			return text[pos];
		}
	}
	
	
	/**
	 * main method for debugging
	 * @param args cmd line args
	 */
	public static void main(String[] args) {
		CircularSuffixArray circArray = new CircularSuffixArray("ABRACADABRA!");
		for (int i = 0; i < circArray.text.length; i++) {
			System.out.printf("%d %d\n", i, circArray.index(i));
		}
	}
}