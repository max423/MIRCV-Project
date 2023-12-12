package it.unipi.dii.aide.mircv.models;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unipi.dii.aide.mircv.utils.FileUtils;

import java.io.FileReader;
import java.io.IOException;

public class Configuration {
    // compression
    private static boolean compressionON ;
    // stemming
    private static boolean stemming_stopwordON ;

    static {
        try {
            // read json configuration file
            JsonObject jsonObject = JsonParser.parseReader(new FileReader(FileUtils.Path_Configuration)).getAsJsonObject();
            compressionON = jsonObject.get("compressionON").getAsBoolean();
            stemming_stopwordON = jsonObject.get("stemming_stopwordON").getAsBoolean();
            // print configuration
            System.out.println("Configuration: \n" + "compression = " + compressionON + "\n" + "stemming_stopword = " + stemming_stopwordON);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Configuration(boolean compressionON, boolean stemming_stopwordON) {
        this.compressionON = compressionON;
        this.stemming_stopwordON = stemming_stopwordON;
    }


    public static boolean isCompressionON() {
        return compressionON;
    }

    public static boolean isStemming_stopwordON() {
        return stemming_stopwordON;
    }


    @Override
    public String toString() {
        return "Configuration{" +
                "compressionON=" + compressionON +
                ", stemming_stopwordON=" + stemming_stopwordON +
                '}';
    }
}
