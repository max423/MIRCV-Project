package it.unipi.dii.aide.mircv.query;

import it.unipi.dii.aide.mircv.models.CollectionStatistics;
import it.unipi.dii.aide.mircv.models.Configuration;
import it.unipi.dii.aide.mircv.models.PostingList;

import java.io.IOException;
import java.util.*;

// receive a query and return the top k (10 or 20) results
public class queryHandler {

    public static ArrayList<PostingList> orderedPostingList = new ArrayList<>();
    public static ArrayList<PostingList> postingListQuery = new ArrayList<>();
    public static HashMap<Integer, Double> hashMapScore = new HashMap<>();
    public static HashMap<Integer, Integer> hashMapLength = new HashMap<>();
    public static ArrayList<Double> maxScoreOrder = new ArrayList<>();

    // receive a query and return the top k (10 or 20) results
    public static void executeQuery(ArrayList<String> tokens, int k) throws IOException {

        int position = 0;

        // ArrayList of tokens removing duplicates
        ArrayList<String> tokensNoDuplicates = new ArrayList<>();
        for (String token : tokens) {
            if (!tokensNoDuplicates.contains(token)) {
                tokensNoDuplicates.add(token);
            }
        }

        // process each token of the query
        for (String token : tokensNoDuplicates) {

            // posting list initialization
            PostingList postingList = new PostingList(token); // crea nuova e setta term

            // obtain the posting list for the token
            postingList.getPostingList(token);

            // check if the posting list is empty (the token is not in the vocabulary)
            if (postingList.getPostingList().size() == 0) {
                System.out.println("The token " + token + " is not in the vocabulary");
                continue;
            }

            // add the posting list to the posting list with the tokens in the query
            postingListQuery.add(postingList);

            // todo vogliamo mantenere queste modalità?
            // check the type of configuration
            if (Configuration.isMaxScoreON()) {
                // max score configuration
                if (Configuration.isScoreON())
                    hashMapScore.put(position, postingList.getMaxBM25());
                else
                    hashMapScore.put(position, postingList.getMaxTFIDF());
            }

            // add the size of the posting list to the hashmap
            hashMapLength.put(position, postingList.getPostingList().size());

            // increment the position
            position++;

        }

        // initialize a priority queue of scoreDoc objects
        PriorityQueue<scoreDoc> priorityQueue;

        // check if the posting list with the tokens in the query is empty
        if (postingListQuery.size() == 0) {
            System.out.println("All the tokens are not in the vocabulary");
            return;
        }

        // todo manteniamo queste modalità?
        // check the type of configuration
        if (Configuration.isConjunctiveON()) {
            // conjunctive configuration
            // todo conjunctive query
            //priorityQueue = utils.conjunctiveQuery(k);
        } else {
            if (Configuration.isMaxScoreON()) {
                // max score configuration

                // sort the posting list in increasing order of score
                hashMapScore = (HashMap<Integer, Double>) utils.sortByValue(hashMapScore);

                // add the posting list in the priority queue in increasing order of score
                for (Map.Entry<Integer, Double> entry : hashMapScore.entrySet()) {
                    //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                    orderedPostingList.add(postingListQuery.get(entry.getKey()));
                }

                // create an array list of score increasing order of score (for max score algorithm)
                maxScoreOrder = new ArrayList<>(hashMapScore.values());
                //todo max score query
                //priorityQueue = utils.maxScoreQuery(k);

            } else {

                // DAAT configuration
                // todo DAAT query
                //priorityQueue = utils.DAATQuery(k);
            }
        }

        // todo print results
        // print the results
        //printResults(priorityQueue, k);

        // reset the data structures
        resetDataStructures();
        
    }

    private static void printResults(PriorityQueue<scoreDoc> priorityQueue, int k) {

            // print the results
            System.out.println("Results for query: ");
            System.out.println("--------------------------------------------------");

            // check if the priority queue is empty
            if (priorityQueue.isEmpty()) {
                System.out.println("No results found");
                return;
            }

            // print the results
            for (int i = 0; i < k; i++) {
                scoreDoc scoreDoc = priorityQueue.poll();
                System.out.println("DocID: " + scoreDoc.getDocID() + " Score: " + scoreDoc.getScore());
            }

            System.out.println("--------------------------------------------------");
    }

    private static void resetDataStructures() {

        //priorityQueue.clear();

        if (postingListQuery != null)
            postingListQuery.clear();

        if (orderedPostingList != null)
            orderedPostingList.clear();

        if (hashMapScore != null)
            hashMapScore.clear();

        if (hashMapLength != null)
            hashMapLength.clear();

        if (maxScoreOrder != null)
            maxScoreOrder.clear();
    }


}
