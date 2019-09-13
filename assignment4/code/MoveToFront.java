import java.util.Arrays;

/**
 * 
 * @author ckingsley
 *
 */
public class MoveToFront {
    static final int R = 256;
    
	/**
     * Apply move-to-front encoding, reading from standard input and writing to standard output
     */
    public static void encode() {
    	char[] sequence = new char[R];
    	int[] lookup = new int[R];
    	initArrays(sequence, lookup);
    	
    	while (StdIn.hasNextChar()) {
    		char c = StdIn.readChar();
    		StdOut.print(lookup[c] + " ");
    		shiftArrays(sequence, lookup, lookup[c]);
    	}
    }
    
    
    /**
     * Apply move-to-front decoding, reading from standard input and writing to standard output
     */
    public static void decode() {
    	char[] sequence = new char[R];
    	int[] lookup = new int[R];
    	initArrays(sequence, lookup);
    	
    	while (!BinaryStdIn.isEmpty()) {
    		int pos = BinaryStdIn.readInt();
    		BinaryStdOut.write(sequence[pos]);
    		shiftArrays(sequence, lookup, pos);
    	}
    	BinaryStdOut.close();
    }
    
    /**
     * Initialize the sequence and lookup arrays
     * @param sequence array containing character sequence
     * @param lookup array containing position information for each character
     */
    private static void initArrays(char[] sequence, int[] lookup) {
    	for (int i = 0; i < R; i++) {
    		sequence[i] = (char)i;
    		lookup[i] = i;
    	}
    }
    
    /**
     * Shift the arrays when a character is moved to the front
     * @param sequence array containing character sequence
     * @param lookup array containing position information for each character
     * @param pos position of the character to be shifted to the front
     */
    private static void shiftArrays(char[] sequence, int[] lookup, int pos) {
    	char c = sequence[pos];
    	
    	for (int i = pos; i > 0; i--) {
			lookup[ sequence[i - 1] ] = i;
			sequence[i] = sequence[i - 1];
		}
    	sequence[0] = c;
		lookup[c] = 0;
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
    		System.out.println("Usage: MoveToFront +/- stdin");
    		System.exit(1);
    	}
    	
    	if ("-".equals(args[0])) {
    		encode();
    	} else if ("+".equals(args[0])) {
    		decode();
    	} else {
    		System.out.println("Invalid argument to MoveToFront - must be + or -");
    	}
    }
}