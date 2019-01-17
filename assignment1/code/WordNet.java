
/**
 * Class representing a WordNet
 * @author ckingsley
 *
 */
public class WordNet {
	
	private ST<String, Queue<Integer>> nouns;
	private Digraph hypernyms;
	
	/**
	 * Constructor
	 * @param synsetFile path to synsets file
	 * @param hypernymFile path to hypernyms file
	 */
	public WordNet(String synsetFile, String hypernymFile) {
		// load synsets
		nouns = new ST<String, Queue<Integer>>();
		In inSynset = new In(synsetFile);
		while (inSynset.hasNextLine()) {
			String[] tokens = inSynset.readLine().split(",");
			int id = Integer.parseInt(tokens[0]);
			
			String[] synonyms = tokens[1].split(" ");
			for (String synonym : synonyms) {
				Queue<Integer> synIds;
				if (nouns.contains(synonym)) {
					synIds = nouns.get(synonym);
				} else {
					synIds = new Queue<Integer>();
					nouns.put(synonym, synIds);
				}
				synIds.enqueue(id);
			}
		}
		inSynset.close();
		
		// load hypernyms
		hypernyms = new Digraph(nouns.size());
		In inHyper = new In(hypernymFile);
		
		while (inHyper.hasNextLine()) {
			String[] tokens = inHyper.readLine().split(",");
			int v = Integer.parseInt(tokens[0]);
			for (int i = 1; i < tokens.length; i++) {
				int w = Integer.parseInt(tokens[i]);
				hypernyms.addEdge(v, w);
			}
		}
		inHyper.close();
		
	}

	 
	/**
	 * Returns all WordNet nouns
	 * @return
	 */
	public Iterable<String> nouns() {
		return nouns.keys();
	}

	
	/**
	 * Is the word a WordNet noun?
	 * @param word
	 * @return
	 */
	public boolean isNoun(String word) {
		return nouns.contains(word);
	}

	
	/**
	 * Returns the distance between nounA and nounB
	 * @param nounA
	 * @param nounB
	 * @return
	 */
	public int distance(String nounA, String nounB) {
		Queue<Integer> synsetA = nouns.get(nounA);
		Queue<Integer> synsetB = nouns.get(nounB);
		
		SAP sap = new SAP(hypernyms);
		return sap.length(synsetA, synsetB);
	}

	
	/**
	 * Returns a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
	 * in a shortest ancestral path (defined below)
	 * @param nounA
	 * @param nounB
	 * @return
	 */
	public String sap(String nounA, String nounB) {
		
		return "hello";
	}

	 
	/**
	 * For unit testing of this class
	 * @param args command line args
	 */
	public static void main(String[] args) {
		
	}
}
