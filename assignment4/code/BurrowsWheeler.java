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
		CircularSuffixArray circArray = new CircularSuffixArray("ABRACADABRA!");
		char[] encoding = new char[ circArray.length() ];
		int first = -1;
		
		for (int i = 0; i < circArray.length(); i++) {
			if (circArray.index(i) == 0) {
				first = i;
				BinaryStdOut.write(i);
			}
			
			int pos = (circArray.index(i) + circArray.length() - 1) % circArray.length();
			encoding[i] = circArray.getText()[pos];
//			encoding[i] = getLastCharAt(circArray.getText(), circArray.index(i));
		}
		
		System.out.println(first + " " + Arrays.toString(encoding));
    }
	
//	/**
//	 * Get the last character from the permuted string at the specified index in the circular array
//	 * @param text original unencoded text
//	 * @param index array index of the circular permuted 
//	 * @return
//	 */
//	private static char getLastCharAt(char[] text, int index) {
//		int pos = (index + text.length - 1) % text.length;
//		
//		return text[pos];
//	}

    /**
     * Apply Burrows-Wheeler decoding, reading from standard input and writing to standard output
     */
    public static void decode() {
    	int firstPos = 3;
    	char[] last = new String("ARD!RCAAAABB").toCharArray(), first = new char[ last.length ];
    	int[] counts = new int[ R ];
    	
    	// use key index counting to sort char array in O(n) time
    	for (int i = 0; i < last.length; i++) {
    		counts[ last[i] ]++;
    	}
    	int idx = 0;
    	for (int i = 0; i < counts.length; i++) {
			for (int j = 0; j < counts[i]; j++) {
				first[idx++] = (char)i;
			}
			if (idx == last.length) {
				break;
			}
    	}
    	System.out.println("first:" + Arrays.toString(first));
    	
    }

    
    /**
     * Main method for debugging
     * 
     * if args[0] is '-', apply move-to-front encoding
     * if args[0] is '+', apply move-to-front decoding
     * @param args command line args
     */
    public static void main(String[] args) {
//    	encode(); System.exit(0);
    	decode(); System.exit(0);
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