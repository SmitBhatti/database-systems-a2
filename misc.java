import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class misc {
    //Class for helper methods
    public byte[] byteFR(int length, int offset, File file) throws IOException {
        byte[] byteData = new byte[length];
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            randomAccessFile.seek(offset);
            randomAccessFile.readFully(byteData);
        }
        return byteData;
    }

    public static int calculate()
    {
        int sum = 65 + 4 + 160 + 4 + 229 + 748 + 1886 + 421 + 206 + 4 + 468;
        return sum;
    }

    public static void stats(long endTime, long beginTime, int InsertionDate, int TreeLevel)
    {
        System.out.println("Time taken for building of B+ Tree (in ms): " + (endTime - beginTime));
        System.out.println("Total no. of Indexed records: " + InsertionDate);
        System.out.println("Height level of the built tree is " + TreeLevel);
    }

    public static void stats(long endTime, long beginTime)
    {
        System.out.println("Searching time taken (in ms): " + (endTime - beginTime));
    }

    public static int getBDate(byte[] record, int RDFLength) {
        byte[] bDateByte = new byte[4];
        for (int j = 0; j < 4; j++) {     
            bDateByte[j] = record[RDFLength + j];
        }
        return ByteBuffer.wrap(bDateByte).getInt();
    }
}