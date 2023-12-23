package it.unipi.dii.aide.mircv.models;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class CollectionStatistics {
    // # of documents = size of documnetIndex
    private static long docCount;
    // total number of terms
    private long totalLength;


    public CollectionStatistics() {
        this.docCount = 0;
        this.totalLength = 0;
    }

    public CollectionStatistics(long docCount, long totalLength) {
        this.docCount = docCount;
        this.totalLength = totalLength;
    }

    public static long getDocCount() {
        return docCount;
    }

    public long getTotalLength() {
        return totalLength;
    }

    // incremento numero di termini
    public void incrementTotalLength() {
        this.totalLength += 1;
    }

    // incremento numero di documenti
    public void setDocCount(int lastDocId) {
        this.docCount = lastDocId +1;   // DocId parte da 0
    }

    @Override
    public String toString() {
        return "CollectionStatistics{" +
                "docCount=" + docCount +
                ", totalLength=" + totalLength +
                '}';
    }

    public void writeToDisk(FileChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(8+8);

        channel.position(channel.size());

        buffer.putLong(docCount);
        buffer.putLong(totalLength);

        buffer = ByteBuffer.wrap(buffer.array());

        // writing into channel
        while (buffer.hasRemaining())
            channel.write(buffer);

    }

    public void readFromDisk(FileChannel channel, long offset) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(8+8);

        channel.position(offset);

        while (buffer.hasRemaining())
            channel.read(buffer);

        buffer.rewind(); // reset the buffer position to 0
        this.docCount = buffer.getLong();                        // reading docCount from buffer
        this.totalLength = buffer.getLong();                     // reading totalLength from buffer
    }
}
