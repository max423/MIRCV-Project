package it.unipi.dii.aide.mircv.utils;
import it.unipi.dii.aide.mircv.models.CollectionStatistics;
import it.unipi.dii.aide.mircv.models.DocumentIndexElem;
import it.unipi.dii.aide.mircv.models.VocabularyElem;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileUtils {
    public static int MAX_TERM_LENGTH = 20; // in bytes
    // path stop words
    public static String Path_StopWords = "src/main/java/it/unipi/dii/aide/mircv/resources/stopwords.txt"; // https://gist.github.com/larsyencken/1440509
    // path Uncompressed collection
    //public static String Path_Uncompressed_Collection = "/Users/massimo/Downloads/collection.tsv";
    public static String Path_Uncompressed_Collection = "src/main/java/it/unipi/dii/aide/mircv/resources/collection_prova.tsv";
    // path Compressed collection
    public static String Path_Compressed_Collection = "/Volumes/S/collection.tar.gz";
    // path to the configuration json file
    public static String Path_Configuration = "src/main/java/it/unipi/dii/aide/mircv/resources/configuration.json";

    // log file per tempi di esecuzione indexer
    public static String Path_Log = "src/main/resources/log.txt";

    // path to the document index
    public static String Path_DocumentIndex = "src/main/resources/document_index";

    // path to the Partial Vocabulary
    public static String Path_PartialVocabulary = "src/main/resources/partial_vocabulary";
    // path to the Partial Posting-DocId
    public static String Path_PartialDocId = "src/main/resources/partial_docid";
    // path to the Partial Postings-TermFreq
    public static String Path_PartialTermFreq = "src/main/resources/partial_termfreq";

    // path to the Final Vocabulary
    public static String Path_FinalVocabulary = "src/main/resources/final_vocabulary";
    // path to the Final Posting-DocId
    public static String Path_FinalDocId = "src/main/resources/final_docid";
    // path to the Final Postings-TermFreq
    public static String Path_FinalTermFreq = "src/main/resources/final_termfreq";
    // path to the Final Collection Statistics
    public static String Path_FinalCollectionStatistics = "src/main/resources/final_collection_statistics";

    public static RandomAccessFile docIndex_RAF;

    public static final HashMap<Integer, ArrayList<RandomAccessFile>> skeleton_RAF = new HashMap<>();

    public static String Path_Skipping = "src/main/resources/skip";

    // path to collection test
    public static String Path_CollectionTest = "src/main/java/it/unipi/dii/aide/mircv/resources/collection_prova.tsv";

    // HashMap to store the vocabulary
    public static HashMap<String, VocabularyElem> vocabulary = new HashMap<>();

    // HashMap to store the document index
    public static HashMap<Integer, DocumentIndexElem> documentIndex = new HashMap<>();

    // collection statistics
    public static CollectionStatistics collectionStatistics = new CollectionStatistics();



    // clear data folder
    public static void clearDataFolder() {
        System.out.println("Clearing data folder...");
        File dataFolder = new File("src/main/resources");
        if (dataFolder.exists()) {
            for (File file : dataFolder.listFiles()) {
                file.delete();
            }
        }
    }

    // remove partial files
    public static void removePartialFiles() {
        System.out.println("Removing partial files...");
        File dataFolder = new File("src/main/resources");
        if (dataFolder.exists()) {
            for (File file : dataFolder.listFiles()) {
                if (file.getName().contains("partial_")) {
                    file.delete();
                }
            }
        }
    }

    // compute the total dimension
    public static double getTotalFolderSize() {
        File folder = new File("src/main/resources");
        long totalSizeInBytes = 0;
        double totalSizeInMB =0;
        if (folder.exists()){
            for (File file : folder.listFiles()) {
                if (file.isFile()) {
                    totalSizeInBytes += file.length();
                    //System.out.println(file.getName());
                }
            }
            totalSizeInMB = totalSizeInBytes / (1024.0 * 1024.0);
        }
        return totalSizeInMB;
    }



    // read the collection according to the compression flag
    public static BufferedReader initBuffer(boolean compressed, boolean testing) throws IOException {

        // for testing, read collection test
        if(testing) {
            return Files.newBufferedReader(Paths.get(FileUtils.Path_CollectionTest), StandardCharsets.UTF_8);
        }
        if(compressed) {
            //read from compressed collection
            TarArchiveInputStream tarInput = null;
            try {
                tarInput = new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream(FileUtils.Path_Compressed_Collection)));
                tarInput.getNextTarEntry();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (tarInput == null) {
                System.out.println("Cannot access to the collection.");
                System.exit(-1);
            }
            return new BufferedReader(new InputStreamReader(tarInput, StandardCharsets.UTF_8));
        }
        //read from uncompressed collection
        return Files.newBufferedReader(Paths.get(FileUtils.Path_Uncompressed_Collection), StandardCharsets.UTF_8);
    }

    // inizialize the docIndex_RAF
    public static void initDocIndex_RAF() throws IOException {
        docIndex_RAF = new RandomAccessFile(new File(Path_DocumentIndex), "rw");
    }

    public static void createTempFile(int blockNum) {
        // temp file for data structure in spimi run : partial termlist, partial vocabulary, partial postings per block

        ArrayList<RandomAccessFile> array_RAF = new ArrayList<>();
        try {
            array_RAF.add(new RandomAccessFile(new File(Path_PartialVocabulary + blockNum), "rw"));     // i= 0 - vocabulary
            array_RAF.add(new RandomAccessFile(new File(Path_PartialDocId + blockNum), "rw"));          // i= 1 - docid (posting list)
            array_RAF.add(new RandomAccessFile(new File(Path_PartialTermFreq + blockNum), "rw"));       // i= 2 - termfreq (posting list)
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // add to the skeleton
        skeleton_RAF.put(blockNum, array_RAF);

    }

    // take the RAF of the final files
    public static void takeFinalRAF(){
        ArrayList<RandomAccessFile> array_RAF = new ArrayList<>();
        try {
            array_RAF.add(new RandomAccessFile((Path_FinalVocabulary), "rw"));                      // i= 0 - vocabulary
            array_RAF.add(new RandomAccessFile((Path_FinalDocId), "rw"));                           // i= 1 - docid (posting list)
            array_RAF.add(new RandomAccessFile((Path_FinalTermFreq), "rw"));                        // i= 2 - termfreq (posting list)
            array_RAF.add(new RandomAccessFile((Path_FinalCollectionStatistics), "rw"));            // i= 3 - collection statistics
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // add to the skeleton
        skeleton_RAF.put(-1, array_RAF);    // position -1 for the final file
    }

    // create final files
    public static void CreateFinalStructure() throws IOException {
        System.out.println("Creating final structure...");
        File dataFolder = new File("src/main/resources");
        if (dataFolder.exists()) {// add Path_FinalVocabulary

            ArrayList<RandomAccessFile> array_RAF = new ArrayList<>();
            try {
                array_RAF.add(new RandomAccessFile(new File(Path_FinalVocabulary), "rw"));                      // i= 0 - vocabulary
                array_RAF.add(new RandomAccessFile(new File(Path_FinalDocId), "rw"));                           // i= 1 - docid (posting list)
                array_RAF.add(new RandomAccessFile(new File(Path_FinalTermFreq), "rw"));                        // i= 2 - termfreq (posting list)
                array_RAF.add(new RandomAccessFile(new File(Path_FinalCollectionStatistics), "rw"));            // i= 3 - collection statistics
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // add to the skeleton
            skeleton_RAF.put(-1, array_RAF);    // position -1 for the final file
        }
    };

    // retrive RAF of v,d,f corrispondig to the block i
    public static FileChannel GetCorrectChannel(int blockNum, int i) {
        //System.out.println("GetCorrectChannel: " + blockNum + " " + i);
        return skeleton_RAF.get(blockNum).get(i).getChannel();
    }


    // save the time of execution of spimi and merger
    public static void saveLog(long elapsedTimeSpimi, long elapsedTimeMerger, Integer blockNumber) {

        double folderDim = getTotalFolderSize();

        // save the log of the execution
        try {
            FileWriter myWriter = new FileWriter(Path_Log, true);
            myWriter.write("Block number: " + blockNumber + "\n");
            myWriter.write("Total folder dimension: " + folderDim + "\n");
            myWriter.write("Spimi execution time: " + elapsedTimeSpimi + " ms\n");
            myWriter.write("Merger execution time: " + elapsedTimeMerger + " ms\n");
            myWriter.write("Total execution time: " + (elapsedTimeSpimi + elapsedTimeMerger) + " ms\n\n");
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // per query handler load in memory vocabulary and document index
    public static void loadFinalStructure() throws IOException {
        takeFinalRAF();
        System.out.println("Loading Vocabulary ...");
        loadVocabulary();
        System.out.println("Loading Document Index ...");
        loadDocumentIndex();
        System.out.println("Loading Collection Statistics ...");
        collectionStatistics.readFromDisk(FileUtils.GetCorrectChannel(-1, 3), 0);

    }


    private static void loadVocabulary() throws IOException {
        // Initial offset
        long currentOffset = 0;
        int VOCABULARY_ELEM_SIZE = 60;

        // Get the channel
        FileChannel channelVocabulary = FileUtils.GetCorrectChannel(-1, 0);

        // Read the vocabulary elements
        while (currentOffset + VOCABULARY_ELEM_SIZE <= channelVocabulary.size()) {
            VocabularyElem vocabularyElem = new VocabularyElem();
            vocabularyElem.readFromDisk(channelVocabulary, currentOffset);

            // Add the vocabulary element to the vocabulary map
            vocabulary.put(vocabularyElem.getTerm(), vocabularyElem);

            // Update the offset
            currentOffset += VOCABULARY_ELEM_SIZE;
        }
    }

    private static void loadDocumentIndex() throws IOException {
        int position = 0;
        int DOC_INDEX_ELEM_SIZE =28;

        try (FileChannel docIndexFC = new RandomAccessFile(FileUtils.Path_DocumentIndex, "rw").getChannel()) {
            while (position < docIndexFC.size()) {

                // Read the document index element
                DocumentIndexElem docElem = new DocumentIndexElem();
                docElem.readFromDisk(docIndexFC, position);

                // Add the document index element to the document index hsshmap
                documentIndex.put(docElem.getDocId(), docElem);

                position += DOC_INDEX_ELEM_SIZE;
            }
        }
    }



}