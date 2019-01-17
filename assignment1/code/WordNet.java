
/**
 * Class representing a WordNet
 * @author ckingsley
 *
 */
public class WordNet {
	
	private ST<String, Queue<Integer>> nouns;
	private ST<Integer, String> indexedSynsets;
	private Digraph hypernyms;
	
	/**
	 * Constructor
	 * @param synsetFile path to synsets file
	 * @param hypernymFile path to hypernyms file
	 */
	public WordNet(String synsetFile, String hypernymFile) {
		// load synsets from synsetFile
		nouns = new ST<String, Queue<Integer>>();
		indexedSynsets = new ST<Integer, String>();
		
		In inSynset = new In(synsetFile);
		while (inSynset.hasNextLine()) {
			String[] tokens = inSynset.readLine().split(",");
			int id = Integer.parseInt(tokens[0]);
			
			// add id -> synset mapping to indexedSynsets
			indexedSynsets.put(id, tokens[1]);
			
			// add noun -> id mapping to hypernyms
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
		
		
		// load hypernyms from hypernymFile into Digraph
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
		validateNoun(nounA);
		validateNoun(nounB);
		
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
		validateNoun(nounA);
		validateNoun(nounB);
		
		Queue<Integer> synsetA = nouns.get(nounA);
		Queue<Integer> synsetB = nouns.get(nounB);
		
		SAP sap = new SAP(hypernyms);
		
		int ancestor = sap.ancestor(synsetA, synsetB);
		if (indexedSynsets.contains(ancestor)) {
			return indexedSynsets.get(ancestor);
		}
		return "No ancestor Found";
	}
	
	
	/**
	 * Checks that the passed noun is present in the synset
	 * @param noun
	 */
	private void validateNoun(String noun) {
		if (!isNoun(noun)) {
			throw new IllegalArgumentException("\'" + noun + "\' is not a valid noun in the synset");
		}
	}

	 
	/**
	 * For unit testing of this class
	 * @param args command line args
	 */
	public static void main(String[] args) {
		WordNet wordNet = new WordNet("wordnet/synsets.txt", "wordnet/hypernyms.txt");
		System.out.println(wordNet.sap("individual","edible_fruit"));
		System.out.println(wordNet.distance("individual","edible_fruit"));
		System.out.println(wordNet.distance("municipality", "region"));
		System.out.println(wordNet.distance("Black_Plague", "black_marlin"));
		System.out.println(wordNet.distance("American_water_spaniel", "histology"));
		System.out.println(wordNet.distance("Brown_Swiss", "barrel_roll"));
	}
}
