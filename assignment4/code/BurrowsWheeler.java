/**
 * 
 * @author ckingsley
 *
 */
public class BurrowsWheeler { 
    
	/**
     * Apply Burrows-Wheeler encoding, reading from standard input and writing to standard output
     */
	public static void encode() {
    	
    }

    /**
     * Apply Burrows-Wheeler decoding, reading from standard input and writing to standard output
     */
    public static void decode() {
    	
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
    	
    	if ("+".equals(args[0])) {
    		encode();
    	} else if ("-".equals(args[0])) {
    		decode();
    	} else {
    		System.out.println("Invalid argument to BurrowsWheeler - must be + or -");
    	}
    }
}