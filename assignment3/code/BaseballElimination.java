

/**
 * Class for solving the baseball elimination problem using MaxFlow
 * @author ckingsley
 *
 */
class BaseballElimination {
	
	/**
	 * Constructor
	 * Create a baseball division from given filename in format specified below
	 * @param filename
	 */
	public BaseballElimination(String filename) {
		FordFulkerson ff = new FordFulkerson(null, 0, 0);
	}
	// 
	
	/**
	 * Return the number of teams
	 * @return
	 */
	public int numberOfTeams() {
		return 0;
	}
	
	/**
	 * Return an iterable over all teams
	 * @return
	 */
	public Iterable<String> teams() {
		
	}
	
	/**
	 * Return the number of wins for given team
	 * @param team
	 * @return
	 */
	public int wins(String team) {
		
		
		return 0;
	}
	
	/**
	 * Return the number of losses for given team
	 * @param team
	 * @return
	 */
	public int losses(String team) {
		
		
		return 0;
	}
	
	/**
	 * Return the number of remaining games for given team
	 * @param team
	 * @return
	 */
	public int remaining(String team) {
		
		
		return 0;
	}
	
	/**
	 * Return the number of remaining games between team1 and team2
	 * @param team1
	 * @param team2
	 * @return
	 */
	public int against(String team1, String team2) {
		
		
		return 0;
	}
	
	/**
	 * Return a boolean indicating whether a given team has been eliminated
	 * @param team
	 * @return
	 */
	public boolean isEliminated(String team) {
		
		
		return true;
	}
	
	/**
	 * Returns the subset R of teams that eliminates given team or null if not eliminated
	 * @param team
	 * @return
	 */
	public Iterable<String> certificateOfElimination(String team) {
		
	}
	
	
	
	public static void main(String[] args) {
	    BaseballElimination division = new BaseballElimination("../teams/teams1.txt");
	    
	    for (String team : division.teams()) {
	        if (division.isEliminated(team)) {
	            StdOut.print(team + " is eliminated by the subset R = { ");
	            for (String t : division.certificateOfElimination(team))
	                StdOut.print(t + " ");
	            StdOut.println("}");
	        }
	        else {
	            StdOut.println(team + " is not eliminated");
	        }
	    }
	}
}