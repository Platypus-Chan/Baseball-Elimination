// This work is confirmed by Dylan Wang to be entirely his work and is in accordance with class Academic Integrity and Collaboration Policy found in the syllabus.

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BaseballElimination {

    private final Map<String, Integer> teamMap = new HashMap<String, Integer>();
    private final Team[] teams;
    private final Map<String, Set<String>> stats = new HashMap<String, Set<String>>();
    private final int[][] matches;

    public BaseballElimination(String filename) {
        In input = new In(filename);
        int count = input.readInt();

        teams = new Team[count];
        matches = new int[count][count];

        for (int i = 0; i < count; i++) {
            String name = input.readString();
            teams[i] = new Team(name);
            teamMap.put(name, i);

            teams[i].setWin(input.readInt());
            teams[i].setLoss(input.readInt());
            teams[i].setRemaining(input.readInt());

            matches[i] = new int[count];
            for (int j = 0; j < count; j++) {
                matches[i][j] = input.readInt();
            }
        }
    }

    public int numberOfTeams() {
        return teamMap.size();
    }

    public Iterable<String> teams() {
        return teamMap.keySet();
    }

    public int wins(String team) {
        validName(team);
        return teams[teamMap.get(team)].getWin();
    }

    public int losses(String team) {
        validName(team);
        return teams[teamMap.get(team)].getLoss();
    }

    public int remaining(String team) {
        validName(team);
        return teams[teamMap.get(team)].getRemaining();
    }

     public int against(String team1, String team2) {
        validName(team1);
        validName(team2);
        return matches[teamMap.get(team1)][teamMap.get(team2)];
    }

    
    public boolean isEliminated(String team) {
        validName(team);
        int x = teamMap.get(team);
        Set<String> minCut = new HashSet<String>(); 

        // trivial elimination
        for (int i = 0; i < numberOfTeams(); i++) {
            if (wins(team) + remaining(team) < teams[i].getWin()) {
                minCut.add(teams[i].getName());
                stats.put(team, minCut);
                return true;
            }
        }


        // nontrivial elimination
        int[] opps = new int[numberOfTeams()-1];
        int k = 0;
        for (int i = 0; i < numberOfTeams(); i++) {
            if (i != x) {
                opps[k] = i;
                k++;
            }
        }


        // Counting the nodes was definetly the most challenging part of this assignment. Trying to trace out the actual flowchart and explain in code where each node goes and what it should do was extremly hard.
        // I had to draw out the actual flowchart on a whiteboard and take it step by step. I would also take a look at some examples from the intructions as well as the lesson to help me out.
        // Once I had the nodes visualized it was just a matter of piecing it together with the math and code. Really proud of how it turned out.
        // construct FlowNetwork
        int totalNodes = 2 + opps.length + opps.length * (opps.length - 1) / 2;
        FlowNetwork newFlow = new FlowNetwork(totalNodes);
        int node = 1;
        for (int i = 0; i < opps.length - 1; i++) {
            for (int j = i + 1; j < opps.length; j++) {
                FlowEdge e = new FlowEdge(0, node, matches[opps[i]][opps[j]]);
                newFlow.addEdge(e);
                e = new FlowEdge(node, opps.length * (opps.length - 1) / 2 + 1 + i, Integer.MAX_VALUE);
                newFlow.addEdge(e);
                e = new FlowEdge(node, opps.length * (opps.length - 1) / 2 + 1 + j, Integer.MAX_VALUE);
                newFlow.addEdge(e);
                node++;
            }
        }

        for (int i = 0; i < opps.length; i++) {
            FlowEdge ee = new FlowEdge(opps.length * (opps.length - 1) / 2 + 1 + i, 
                            totalNodes - 1, wins(team) + remaining(team) - teams[opps[i]].getWin());
            newFlow.addEdge(ee);
        }

        FordFulkerson ford = new FordFulkerson(newFlow, 0, totalNodes - 1);
        
        for (int i = 0; i < opps.length; i++) {
            if (ford.inCut(opps.length * (opps.length - 1) / 2 + 1 + i)) {
                minCut.add(teams[opps[i]].getName());
            }
        }
        stats.put(team, minCut);
        if (!minCut.isEmpty()) {
            return true;
        }
        else {
            return false;
        }
    }

    
    public Iterable<String> certificateOfElimination(String team) {
        validName(team);
        if (!stats.containsKey(team)) {
            isEliminated(team);
        }
        
        return stats.get(team);
    }
    

    private void validName(String name) {
        if (!teamMap.containsKey(name)) {
            throw new IllegalArgumentException();
        }
    }
    
    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination("teams5.txt");

        for (String team : division.teams()) {
            /*
            StdOut.print(team + "\t" + division.wins(team) + "\t" + division.losses(team) + "\t" + division.remaining(team));
            
            for (String team2 : division.teams()) {
                StdOut.print("\t" + division.against(team, team2));
            }

            StdOut.println("");
            */

            
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
            
        }
    }


}
