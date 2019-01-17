
public class Outcast {
	
	
	/**
	 * Constructor
	 * @param wordnet WordNet object
	 */
	public Outcast(WordNet wordnet) {
		
	}

	
	/**
	 * Given an array of WordNet nouns, return an outcast
	 * @param nouns array of WordNet nouns
	 * @return an outcast
	 */
	public String outcast(String[] nouns) {
		
		return "";
	}

	
	/**
	 * For unit testing of this class
	 * @param args command line args
	 */
	public static void main(String[] args) {
		WordNet wordnet = new WordNet(args[0], args[1]);
	    Outcast outcast = new Outcast(wordnet);
	    
	    for (int t = 2; t < args.length; t++) {
	        String[] nouns = In.readStrings(args[t]);
	        StdOut.println(args[t] + ": " + outcast.outcast(nouns));
	    }
	}

}
