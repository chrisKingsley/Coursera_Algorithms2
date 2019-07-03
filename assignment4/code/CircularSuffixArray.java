/**
 * 
 * @author ckingsley
 *
 */
public class CircularSuffixArray {
	private String inputString;
	
	
	/**
	 * Constructor for a circular suffix array of s
	 * @param s the string to generate the circular suffix array
	 */
	public CircularSuffixArray(String s) {
		inputString = new String(s);
	}
    
	
	/**
	 * Return the length of the string
	 * @return length of the string
	 */
	public int length() {
		return(inputString.length());
	}
    
	
	/**
	 * Returns index of ith sorted suffix
	 * @param i the index
	 * @return index of ith sorted suffix
	 */
	public int index(int i) {
    	return(0);
    }
}