import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class Election {
    Scanner input = new Scanner(System.in);
    int numberOfCandidates;
    ArrayList<String> candidatesNames;
    ArrayList<ArrayList<Integer>> candidates = new ArrayList<ArrayList<Integer>>(1);
    ArrayList<Integer> tallies;
    int numberOfVoters = 0;
    int round = 1;
    Random random = new Random();
    private PrintWriter file;

    Election(int numberOfCandidates, ArrayList<String> candidateNames, PrintWriter FileOuput) {
        this.file = FileOuput;
        this.numberOfCandidates = numberOfCandidates;
        this.candidatesNames = candidateNames;
        for (int i = 0; i < numberOfCandidates; i++) {
            candidates.add(new ArrayList<Integer>(1));
        }
    }

    /** Reads the preferences of every voter */
    public void getPreferences(String[] voters) {
        // for every vote
        for (String vote : voters) {
            // split the string into its rankings
            String[] preferences = vote.trim().split(" ");
            // pass on the voter's ranking to the candidate
            for (int candidate = 0; candidate < numberOfCandidates; candidate++) {
                candidates.get(candidate).add(new Integer(Integer.parseInt(preferences[candidate])));
                
            }
            numberOfVoters++;
        }
    }

    public void tally() {
        /* For every candidate, count how many of the highest ranking they have */
        int highestPreference = 1;
        // keeps track of how many votes each candidate gets
        tallies = new ArrayList<Integer>(numberOfCandidates);
        // for every candidate...
        for (int candidate = 0; candidate < numberOfCandidates; candidate++) {
            int tally = 0;
            // go through their rankings, check if its a one and add it as a vote
            for (Integer preference : candidates.get(candidate)) {
                if (preference.intValue() == highestPreference) {
                    tally++;
                }
            }
            tallies.add(tally);
        }

        // Display the tallies
        System.out.println("Round " + round);
        file.println("Round " + round);
        for (int candidate = 0; candidate < numberOfCandidates; candidate++) {
            System.out.println("\t" + candidate + ". " + candidatesNames.get(candidate) + ": " +
            tallies.get(candidate) );
            file.println("\t" + candidate + ". " + candidatesNames.get(candidate)  + ": " + tallies.get(candidate) + " ");
            // for (int voter = 0; voter < numberOfVoters; voter++) {
            //     file.print(candidates.get(candidate).get(voter) + "\t");
            // }

        }
        this.determineWinner();
    }

    public void determineWinner() {
        int majority = numberOfVoters/2 + 1, candidate = 0, majorityFrequency = 0;
        // for every candidate...
        for (int i = 0; i < numberOfCandidates; i++) {
            // check if he or she got a majority
            if (tallies.get(i) >= majority) {
                majority = tallies.get(i);
                candidate = i;
                majorityFrequency = 1;
            }
        }
        if (majorityFrequency == 1) { // If there is a clear winner
            System.out.println(candidatesNames.get(candidate));
            file.println(candidatesNames.get(candidate));
        } else if (numberOfVoters%numberOfCandidates == 0) {
            System.out.println("It's a tie between " + candidatesNames);
            file.println("It's a tie between " + candidatesNames);
        }
        else {
            this.newRound();
        }
    }

    public void newRound() {
        int smallestTally = Collections.min(tallies);

        ArrayList<Integer> indicesOfCandidatesWithSmallestTally = new ArrayList<Integer>(1);
        // Check to see who has the smallest tally
        for (int i = 0; i < numberOfCandidates; i++) {
            if (tallies.get(i) == smallestTally) {
                indicesOfCandidatesWithSmallestTally.add(i);
            }
        }
        Collections.reverse(indicesOfCandidatesWithSmallestTally);
        for (int removedCandidate: indicesOfCandidatesWithSmallestTally) {
            this.removeCandidate(removedCandidate);
        }
        System.out.println();
        file.println();
        round++;
        this.tally();
    }

    public void removeCandidate(int index) {
        System.out.println("\t\t" + candidatesNames.get(index) + " will be removed.");
        file.println("\t\t" + candidatesNames.get(index) + " will be removed.");
        // adjust the tallies
        for (int voter = 0; voter < numberOfVoters; voter++) {
            for (int candidate = 0; candidate < numberOfCandidates; candidate++) {
                if (candidates.get(candidate).get(voter) >= candidates.get(index).get(voter) && candidate != index) {
                    candidates.get(candidate).set(voter, candidates.get(candidate).get(voter) - 1);
                }
            }
        }
        candidatesNames.remove(index);
        tallies.remove(index);
        candidates.remove(index);
        numberOfCandidates--;

        // Display adjusted tallies
        // for (int i = 0; i < numberOfCandidates; i++) {
        //     System.out.println(candidatesNames.get(i) + candidates.get(i));
        // }
    }

    public static void main(String[] args) throws IOException {
        PrintWriter file = new PrintWriter("bugFinder.txt");
        FileReader fr = new FileReader("test.txt");
        Scanner inFile = new Scanner(fr);
        int elections = Integer.parseInt(inFile.nextLine());
        // skip line
        inFile.nextLine();

        for (int rounds = 0; rounds < elections; rounds++) {
            // int and arraylist are passed on to election constructor
            int numberOfCandidates = Integer.parseInt(inFile.nextLine());
            ArrayList<String> candidateNames = new ArrayList<String>(numberOfCandidates);
            // get all of the candidates names
            for (int i = 0; i < numberOfCandidates; i++) {
                String candidateName = inFile.nextLine();
                if (candidateName.length() < 10) {
                    while(candidateName.length() < 9) {
                        candidateName = candidateName + " ";
                    }
                }
                candidateNames.add(candidateName);
            }
            // get all of the votes and store them in an array
            ArrayList<String> votes = new ArrayList<String>(1);
            String line;
            while ( !(line = inFile.nextLine()).isEmpty() ) {
                votes.add(line);
            }
            String[] votes_parameter = new String[votes.size()];
            // if (rounds == 0) {
                System.out.println("Election " + (rounds + 1));
                Election election = new Election(numberOfCandidates, candidateNames, file);
                election.getPreferences(votes.toArray(votes_parameter));
                election.tally();
                System.out.println();
            // }
        }
        file.close();
        inFile.close();
    }
}