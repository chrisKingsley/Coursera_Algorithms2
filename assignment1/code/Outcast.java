
public class Outcast {
	WordNet wordNet;
	
	/**
	 * Constructor
	 * @param wordnet WordNet object
	 */
	public Outcast(WordNet wordnet) {
		this.wordNet = wordnet;
	}

	
	/**
	 * Given an array of WordNet nouns, return an outcast
	 * @param nouns array of WordNet nouns
	 * @return an outcast
	 */
	public String outcast(String[] nouns) {
		int maxDist = Integer.MIN_VALUE;
		String outcast = "No outcast found";
		
		for (int i = 0; i < nouns.length; i++) {
			int distSum = 0;
			
			for (int j = 0; j < nouns.length; j++) {
				if (i != j) {
					distSum += wordNet.distance(nouns[i], nouns[j]);
				}
			}
			
			if (distSum > maxDist) {
				maxDist = distSum;
				outcast = nouns[i];
			}
		}
		
		return outcast;
	}

	
	/**
	 * For unit testing of this class
	 * @param args command line args
	 */
	public static void main(String[] args) {
		WordNet wordnet = new WordNet("wordnet/synsets.txt", "wordnet/hypernyms.txt");
	    Outcast outcast = new Outcast(wordnet);
	    
	    String[] outcastFiles = {"wordnet/outcast5.txt","wordnet/outcast8.txt","wordnet/outcast11.txt",};
	    for (String outcastFile : outcastFiles) {
	        String[] nouns = new In(outcastFile).readAllStrings();
	        StdOut.println(outcastFile + ": " + outcast.outcast(nouns));
	    }
	}
}
