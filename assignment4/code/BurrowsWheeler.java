import java.util.Arrays;

/**
 * Class that implements the Burrows-Wheeler encoding/decoding of strings
 * @author ckingsley
 *
 */
public class BurrowsWheeler { 
    public static final int R = 256;
    
	/**
     * Apply Burrows-Wheeler encoding, reading from standard input and writing to standard output
     */
	public static void encode() {
		String s = BinaryStdIn.readString();
		CircularSuffixArray circArray = new CircularSuffixArray(s);
		char[] encoded = new char[ s.length() ];
		int firstPos = -1;
		
		for (int i = 0; i < s.length(); i++) {
			if (circArray.index(i) == 0) {
				firstPos = i;
			}
			int pos = (circArray.index(i) + s.length() - 1) % s.length();
			encoded[i] = s.charAt(pos);
		}
		
		BinaryStdOut.write(firstPos);
		BinaryStdOut.write(new String(encoded));
		BinaryStdOut.close();
    }
	

    /**
     * Apply Burrows-Wheeler decoding, reading from standard input and writing to standard output
     */
    public static void decode() {
    	int firstPos = BinaryStdIn.readInt();
    	String s = BinaryStdIn.readString();
    	char[] last = s.toCharArray(), first = new char[ s.length() ];
    	int[] counts = new int[ R + 1 ], next = new int[ s.length() ];
    	
    	// use key index counting to sort last column char array and generate next array in O(n) time 
    	for (int i = 0; i < last.length; i++) {
    		counts[ last[i] + 1 ]++;
    	}
    	for (int r = 0; r < R; r++) {
    		counts[ r + 1 ] += counts[ r ];
    	}
    	for (int i = 0; i < first.length; i++) {
    		first[ counts[ last[i] ] ] = last[i];
    		next[ counts[ last[i] ] ] = i;
    		counts[ last[i] ]++;
    	}
    	
    	// decode string using next array
    	int nextIndex = firstPos;
    	for (int i = 0; i < next.length; i++) {
    		BinaryStdOut.write(first[ nextIndex ]);
    		nextIndex = next[ nextIndex ];
    	}
    	BinaryStdOut.close();
    }

    
    /**
     * Main method for debugging
     * 
     * if args[0] is '-', apply move-to-front encoding
     * if args[0] is '+', apply move-to-front decoding
     * @param args command line args
     */
    public static void main(String[] args) {
    	if (args == null || args.length == 0) {
    		System.out.println("Usage: BurrowsWheeler +/- stdin");
    		System.exit(1);
    	}
    	
    	if ("-".equals(args[0])) {
    		encode();
    	} else if ("+".equals(args[0])) {
    		decode();
    	} else {
    		System.out.println("Invalid argument to BurrowsWheeler - must be + or -");
    	}
    }
}