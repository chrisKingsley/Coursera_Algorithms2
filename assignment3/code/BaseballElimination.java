

/**
 * Class for solving the baseball elimination problem using MaxFlow/MinCut
 * @author ckingsley
 *
 */
public final class BaseballElimination {
	private final ST<String, TeamInfo> teams;
	private final int[][] schedule;
	private final FordFulkerson[] teamFlowSearches;
	
	
	/**
	 * Constructor
	 * Create a baseball division by reading information from the passed filename and constructing
	 * flow networks for all teams that are not trivially eliminated
	 * @param filename The full path of the input file
	 */
	public BaseballElimination(String filename) {
		In in = new In(filename);
		int numTeams = in.readInt();
		
		teams = new ST<String, TeamInfo>();
		schedule = new int[numTeams][numTeams];
		teamFlowSearches = new FordFulkerson[numTeams];
		
		// read information from input file
		for (int i = 0; i < numTeams; i++) {
			teams.put(in.readString(), new TeamInfo(i, in.readInt(), in.readInt(), in.readInt()));
			
			for (int j = 0; j < numTeams; j++) {
				schedule[i][j] = in.readInt();
			}
		}
		
		in.close();
		
		// build flow network and do flow search for each team that is not trivially eliminated
		for (String team : teams) {
			if (!isTriviallyEliminated(team)) {
				buildFlowNetwork(team);
			}
		}
	}
	
	
	/**
	 * Return the number of teams
	 * @return The number of teams
	 */
	public int numberOfTeams() {
		return teams.size();
	}
	
	/**
	 * Return an iterable over all team names
	 * @return an iterable over all team names
	 */
	public Iterable<String> teams() {
		return teams.keys();
	}
	
	/**
	 * Return the number of wins for given team
	 * @param team The name of the team
	 * @return The number of wins for passed team 
	 */
	public int wins(String team) {
		checkTeamName(team);
		return teams.get(team).wins;
	}
	
	/**
	 * Return the number of losses for given team
	 * @param team The name of the team
	 * @return The number of losses for passed team 
	 */
	public int losses(String team) {
		checkTeamName(team);
		return teams.get(team).losses;
	}
	
	/**
	 * Return the number of remaining games for given team 
	 * @param team The name of the team
	 * @return The number of remaining games for passed team 
	 */
	public int remaining(String team) {
		checkTeamName(team);
		return teams.get(team).left;
	}
	
	/**
	 * Return the number of remaining games between team1 and team2
	 * @param team1 The name of the first team
	 * @param team2 The name of the second team
	 * @return The number of remaining games between team1 and team2
	 */
	public int against(String team1, String team2) {
		checkTeamName(team1);
		checkTeamName(team2);
		int i = teams.get(team1).index, j = teams.get(team2).index;
		return schedule[ i ][ j ];
	}
	
	
	/**
	 * Checks whether the team is trivially eliminated because another team has
	 * more wins than max possible wins of the passed team 
	 * @param team
	 * @return true if team is trivially eliminated, false otherwise
	 */
	private boolean isTriviallyEliminated(String team) {
		TeamInfo teamInfo = teams.get(team);
		int maxPossibleWins = teamInfo.wins + teamInfo.left;
		
		for (String otherTeam : teams) {
			if(maxPossibleWins < teams.get(otherTeam).wins) {
				return(true);
			}
		}
		
		return(false);
	}
	
	/**
	 * For the passed team, build a flow graph and run the Ford Fulkerson algorithm on that graph to
	 * determine whether a team can be eliminated
	 * @param team The name of the team for which the flow network is to be constructed
	 */
	private void buildFlowNetwork(String team) {
		TeamInfo teamInfo = teams.get(team);
		int numVertices = teams.size() + (teams.size() - 1)*(teams.size() - 2)/2 + 2;
		FlowNetwork flowNet = new FlowNetwork(numVertices);
		int sourceIndex = teams.size(), targetIndex = teams.size() + 1;
		
		// add edges from each team to target
		for (String otherTeam : teams) {
			if (!otherTeam.equals(team)) {
				TeamInfo otherTeamInfo = teams.get(otherTeam);
				double capacity = teamInfo.wins + teamInfo.left - otherTeamInfo.wins;
				FlowEdge e = new FlowEdge(otherTeamInfo.index, targetIndex, capacity);
				flowNet.addEdge(e);
			}
		}
		
		// add edges from source to each remaining game, and from remaining games to individual teams
		int vertexNum = targetIndex + 1;
		for (int i = 0; i < teams.size() - 1; i++) {
			for (int j = i + 1; j < teams.size(); j++) {
				if (i != teamInfo.index && j != teamInfo.index) {
					flowNet.addEdge( new FlowEdge(sourceIndex, vertexNum, schedule[i][j]) );
					flowNet.addEdge( new FlowEdge(vertexNum, i, Double.POSITIVE_INFINITY) );
					flowNet.addEdge( new FlowEdge(vertexNum, j, Double.POSITIVE_INFINITY) );
					vertexNum++;
				}
			}
		}
		
		// add flow search for current team to array of flow searches
		teamFlowSearches[ teamInfo.index ] = new FordFulkerson(flowNet, sourceIndex, targetIndex);
	}
	
	
	/**
	 * Return a boolean indicating whether a given team has been eliminated
	 * @param team The name of the team
	 * @return Whether or not the team was eliminated
	 */
	public boolean isEliminated(String team) {
		checkTeamName(team);
		
		FordFulkerson flowSearch = teamFlowSearches[ teams.get(team).index ];
		
		// team trivially eliminated
		if (flowSearch == null) {
			return(true);
		}
		
		// if not trivially eliminated, check that vertices from games are in the same side of cut with the source
		int vertexNum = teams.size() + 2, numVertices = (teams.size() - 1)*(teams.size() - 2)/2;
		for (int i = vertexNum; i < vertexNum + numVertices; i++) {
			if (flowSearch.inCut(i)) {
				return(true);
			}
		}
		
		return(false);
	}
	
	/**
	 * Returns the subset R of teams that eliminates given team or null if not eliminated
	 * @param team The name of the team
	 * @return The subset R of teams that eliminates given team or null if not eliminated
	 */
	public Iterable<String> certificateOfElimination(String team) {
		if (!isEliminated(team)) {
			return(null);
		}
		
		TeamInfo teamInfo = teams.get(team);
		FordFulkerson flowSearch = teamFlowSearches[ teamInfo.index ];
		Bag<String> eliminatingTeams = new Bag<String>();
		
		// add teams that eliminate the passed team, trivially or through the flow network
		for (String otherTeam : teams) {
			if (!otherTeam.equals(team)) {
				TeamInfo otherTeamInfo = teams.get(otherTeam);
				if (flowSearch == null) {
					if (otherTeamInfo.wins > teamInfo.wins + teamInfo.left) {
						eliminatingTeams.add(otherTeam);
					}
				} else {
					if (flowSearch.inCut(otherTeamInfo.index)) {
						eliminatingTeams.add(otherTeam);
					}
				}
			}
		}
		
		return eliminatingTeams;
	}
	
	
	/**
	 * Prints max wins for team and average possible wins for teams in certificate
	 * of elimination
	 * @param team The name of the team
	 * @param eliminatingTeams An iterable collection of the names of the teams that eliminated
	 * the passed team
	 */
	private void printCertificateInfo(String team, Iterable<String> eliminatingTeams) {
		int totalTeams = 0, totalWins = 0, totalLeft = 0;
		for (String team1 : eliminatingTeams) {
			TeamInfo team1Info = teams.get(team1);
			totalWins += team1Info.wins;
			totalTeams += 1;
			
			// games between teams in the collection eliminatingTeams
			for (String team2 : eliminatingTeams) {
				TeamInfo team2Info = teams.get(team2);
				if (team1Info.index < team2Info.index) {
					totalLeft += schedule[ team1Info.index ][ team2Info.index ];
				}
			}
		}
		
		TeamInfo teamInfo = teams.get(team);
		System.out.printf("%s max possible wins:%d  aR:%.2f %s\n", team, 
				teamInfo.left + teamInfo.wins, (totalWins + totalLeft)/(float)totalTeams,
				teamFlowSearches[ teamInfo.index ] == null ? "- eliminated trivially":"");
	}
	
	
	/**
	 * Class containing information for an individual team
	 * @author ckingsley
	 *
	 */
	private class TeamInfo {
		int index, wins, losses, left;
		
		/**
		 * Constuctor
		 * @param index numerical index of the vertex representing the team
		 * @param wins number of wins of the team
		 * @param losses number of losses of the team
		 * @param left number of games left of the team
		 */
		private TeamInfo(int index, int wins, int losses, int left) {
			this.index = index;
			this.wins = wins;
			this.losses = losses;
			this.left = left;
		}
		
		/**
		 * Returns string representation of the team information
		 */
		public String toString() {
			return String.format("index:%d wins:%d, losses:%d left:%d\n", index, wins, losses, left);
		}
	}
	
	
	/**
	 * Prints information and schedule for all teams
	 */
	protected void printTeamInfo() {
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
	 * @param team The team name to be validated
	 */
	private void checkTeamName(String team) {
		if (!teams.contains(team)) {
			throw new java.lang.IllegalArgumentException("Invalid team name: " + team);
		}
	}
	
	
	/**
	 * Main method for debugging
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
	    BaseballElimination division = new BaseballElimination("../teams/teams5.txt");
	    
	    for (String team : division.teams()) {
	        if (division.isEliminated(team)) {
	        	StdOut.print(team + " is eliminated by the subset R = { ");
	        	Iterable<String> eliminatingTeams = division.certificateOfElimination(team);
	            for (String t : eliminatingTeams)
	                StdOut.print(t + " ");
	            StdOut.println("}");
	            division.printCertificateInfo(team, eliminatingTeams);
	        }
	        else {
	            StdOut.println(team + " is not eliminated");
	        }
	    }
	}
}
