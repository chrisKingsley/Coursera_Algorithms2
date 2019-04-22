

/**
 * Class for solving the baseball elimination problem using MaxFlow
 * @author ckingsley
 *
 */
class BaseballElimination {
	private ST<String, TeamInfo> teams;
	private int[][] schedule;
	
	/**
	 * Constructor
	 * Create a baseball division from given filename in format specified below
	 * @param filename
	 */
	public BaseballElimination(String filename) {
		In in = new In(filename);
		int numTeams = in.readInt();
		
		teams = new ST<String, TeamInfo>();
		schedule = new int[numTeams][numTeams];
		
		for (int i = 0; i < numTeams; i++) {
			teams.put(in.readString(), new TeamInfo(i, in.readInt(), in.readInt(), in.readInt()));
			
			for (int j = 0; j < numTeams; j++) {
				schedule[i][j] = in.readInt();
			}
		}
		
		in.close();
		
		printTeamInfo();
		
		//FlowNetwork flowNet = new FlowNetwork(numTeams);
		//FordFulkerson flowSearch = new FordFulkerson(flowNet, 0, 0);
	}
	
	/**
	 * Return the number of teams
	 * @return
	 */
	public int numberOfTeams() {
		return teams.size();
	}
	
	/**
	 * Return an iterable over all teams
	 * @return
	 */
	public Iterable<String> teams() {
		return teams.keys();
	}
	
	/**
	 * Return the number of wins for given team
	 * @param team
	 * @return
	 */
	public int wins(String team) {
		checkTeamName(team);
		return teams.get(team).wins;
	}
	
	/**
	 * Return the number of losses for given team
	 * @param team
	 * @return
	 */
	public int losses(String team) {
		checkTeamName(team);
		return teams.get(team).losses;
	}
	
	/**
	 * Return the number of remaining games for given team 
	 * @param team
	 * @return
	 */
	public int remaining(String team) {
		checkTeamName(team);
		return teams.get(team).left;
	}
	
	/**
	 * Return the number of remaining games between team1 and team2
	 * @param team1
	 * @param team2
	 * @return
	 */
	public int against(String team1, String team2) {
		checkTeamName(team1);
		checkTeamName(team2);
		int i = teams.get(team1).index, j = teams.get(team2).index;
		return schedule[ i ][ j ];
	}
	
	/**
	 * Return a boolean indicating whether a given team has been eliminated
	 * @param team
	 * @return
	 */
	public boolean isEliminated(String team) {
		checkTeamName(team);
		
		return true;
	}
	
	/**
	 * Returns the subset R of teams that eliminates given team or null if not eliminated
	 * @param team
	 * @return
	 */
	public Iterable<String> certificateOfElimination(String team) {
		checkTeamName(team);
		return new Bag<String>();
	}
	
	
	/**
	 * Class containing information for an individual team
	 * @author ckingsley
	 *
	 */
	private class TeamInfo {
		int index, wins, losses, left;
		
		private TeamInfo(int index, int wins, int losses, int left) {
			this.index = index;
			this.wins = wins;
			this.losses = losses;
			this.left = left;
		}
		
		public String toString() {
			return String.format("index:%d wins:%d, losses:%d left:%d\n", index, wins, losses, left);
		}
	}
	
	/**
	 * Prints information and schedule for all teams
	 */
	private void printTeamInfo() {
		for (String team : teams) {
			TeamInfo teamInfo = teams.get(team);
			System.out.printf("%s %s", team, teamInfo.toString());
		}
		for (int i = 0; i < teams.size(); i++) {
			for (int j = 0; j < teams.size(); j++) {
				System.out.print(schedule[i][j] + " ");
			}
			System.out.println();
		}
	}
	
	/**
	 * Checks that the passed team name is valid
	 * @param team team name to be validated
	 */
	private void checkTeamName(String team) {
		if (!team.contains(team)) {
			throw new java.lang.IllegalArgumentException("Invalid team name: " + team);
		}
	}
	
	
	public static void main(String[] args) {
	    BaseballElimination division = new BaseballElimination("../teams/teams5.txt");
	    
//	    for (String team : division.teams()) {
//	        if (division.isEliminated(team)) {
//	            StdOut.print(team + " is eliminated by the subset R = { ");
//	            for (String t : division.certificateOfElimination(team))
//	                StdOut.print(t + " ");
//	            StdOut.println("}");
//	        }
//	        else {
//	            StdOut.println(team + " is not eliminated");
//	        }
//	    }
	}
}