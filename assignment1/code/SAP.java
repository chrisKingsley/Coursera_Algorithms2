/**
 * Class that implements shortest ancestral path on a digraph
 * @author ckingsley
 *
 */
public class SAP {
	Digraph G;
	
	
	/**
	 * Constructor
	 * @param G A digraph (not necessarily a DAG)
	 */
	public SAP(Digraph G) {
		DirectedCycle dc = new DirectedCycle(G);
		if (dc.hasCycle()) {
			throw new IllegalArgumentException("Passed directed graph has cycle(s)");
		}
		
		this.G = G;
	}

	
	/**
	 * Returns length of shortest ancestral path between v and w, or -1 if no such path exists
	 * @param v first item to determine distance along shortest path
	 * @param w second item to determine distance along shortest path
	 * @return the shortest path distance from v and w as found by breadth first search
	 */
	public int length(int v, int w) {
		validateVertex(v);
		validateVertex(w);
		
		DeluxeBFS bfs_v = new DeluxeBFS(G, v);
		DeluxeBFS bfs_w = new DeluxeBFS(G, w);
		
		return getMinSapDist(bfs_v, bfs_w);
	}

	 
	/**
	 * Returns a common ancestor of v and w that participates in a shortest ancestral path, or -1 if no such path
	 * @param v first set of items to determine common ancestor along shortest path
	 * @param w second set of items to determine common ancestor along shortest path
	 * @return a common ancestor along the shortest path from v and w as found by breadth first search
	 */
	public int ancestor(int v, int w) {
		validateVertex(v);
		validateVertex(w);
		
		DeluxeBFS bfs_v = new DeluxeBFS(G, v);
		DeluxeBFS bfs_w = new DeluxeBFS(G, w);
		
		return getAncestor(bfs_v, bfs_w);
	}
	
	
	/**
	 * Returns the length of shortest ancestral path between any vertex in v and any vertex in w, or -1 if no such path
	 * exists
	 * @param v first set of items to determine distance along shortest path
	 * @param w second set of items to determine distance along shortest path
	 * @return the shortest path distance from v and w as found by breadth first search
	 */
	public int length(Iterable<Integer> v, Iterable<Integer> w) {
		validateVertices(v);
		validateVertices(w);
		
		DeluxeBFS bfs_v = new DeluxeBFS(G, v);
		DeluxeBFS bfs_w = new DeluxeBFS(G, w);
		
		return getMinSapDist(bfs_v, bfs_w);
	}
	
	
	/**
	 * Returns a common ancestor that participates in shortest ancestral path, or -1 if no such path exists
	 * @param v first set of items to determine common ancestor along shortest path
	 * @param w second set of items to determine common ancestor along shortest path
	 * @return a common ancestor along the shortest path from v and w as found by breadth first search 
	 */
	public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
		validateVertices(v);
		validateVertices(w);
		
		DeluxeBFS bfs_v = new DeluxeBFS(G, v);
		DeluxeBFS bfs_w = new DeluxeBFS(G, w);
		
		return getAncestor(bfs_v, bfs_w);
	}
	
	
	/**
	 * Returns a shortest ancestral path distance from two breadth first search results
	 * @param bfs_v DeluxeBFS object containing breadth first search results from item/set v
	 * @param bfs_w DeluxeBFS object containing breadth first search results from item/set w
	 * @return the shortest path distance from v and w as found by breadth first search
	 */
	private int getMinSapDist(DeluxeBFS bfs_v, DeluxeBFS bfs_w) {
		int minDist = Integer.MAX_VALUE;
		
		for (int i = 0; i < G.V(); i++) {
			if (bfs_v.hasPathTo(i) && bfs_w.hasPathTo(i)) {
				int dist = bfs_v.distTo(i) + bfs_w.distTo(i);
				if (dist < minDist) {
					minDist = dist;
				}
			}
		}
		
		return (minDist == Integer.MAX_VALUE ? -1 : minDist);
	}
	
	/**
	 * Returns a common ancestor from the shortest ancestral path between two breadth first search results
	 * @param bfs_v bfs_v DeluxeBFS object containing breadth first search results from item/set v
	 * @param bfs_w bfs_v DeluxeBFS object containing breadth first search results from item/set w
	 * @return a common ancestor along the shortest path from v and w as found by breadth first search
	 */
	private int getAncestor(DeluxeBFS bfs_v, DeluxeBFS bfs_w) {
		int minDist = Integer.MAX_VALUE;
		int ancestor = -1;
		
		for (int i = 0; i < G.V(); i++) {
			if (bfs_v.hasPathTo(i) && bfs_w.hasPathTo(i)) {
				int dist = bfs_v.distTo(i) + bfs_w.distTo(i);
				if (dist < minDist) {
					minDist = dist;
					ancestor = i;
				}
			}
		}
		
		return ancestor;
	}
	
	
	// throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
        if (v < 0 || v >= G.V())
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (G.V()-1));
    }
    
    
    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertices(Iterable<Integer> vertices) {
        if (vertices == null) {
            throw new IllegalArgumentException("argument is null");
        }
        
        for (int v : vertices) {
            if (v < 0 || v >= G.V()) {
                throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (G.V()-1));
            }
        }
    }

	
	/**
	 * For unit testing of this class
	 * @param args command line args
	 */
	public static void main(String[] args) {
		In in = new In("C:/My_Stuff/Courses/Coursera_Algorithms2/assignment1/wordnet/digraph1.txt");
	    Digraph G = new Digraph(in);
	    SAP sap = new SAP(G);
	    
        int test[][] = {{3,11}, {9,12}, {7,2}, {1,6}};
	    for (int i = 0; i < test.length; i++) {
	    	int length   = sap.length(test[i][0], test[i][1]);
	        int ancestor = sap.ancestor(test[i][0], test[i][1]);
	        StdOut.printf("v:%d w:%d length:%d, ancestor:%d\n", test[i][0], test[i][1], length, ancestor);
	    }
        
	}
}
