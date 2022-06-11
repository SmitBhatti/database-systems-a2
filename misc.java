import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class misc {
    //Class for helper methods
    public static int[] allocateSizes() {
        int[] finalColumnSize = new int[11];
        finalColumnSize[0]  = 65;   //RDFSchema
        finalColumnSize[1]  = 4;    //Birth Date
        finalColumnSize[2]  = 160;  //Birth Place
        finalColumnSize[3]  = 4;    //Death Date
        finalColumnSize[4]  = 229;  //Field
        finalColumnSize[5]  = 748;  //Genre
        finalColumnSize[6]  = 1886; //Instrument 
        finalColumnSize[7]  = 421;  //Nationality
        finalColumnSize[8]  = 206;  //Thumbnail
        finalColumnSize[9]  = 4;    //WikiPage ID
        finalColumnSize[10] = 468;  //Description
        return finalColumnSize;
    }
    
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

    public static void searchAndDisplay(String userInput, String[] args, InternalNode highNode, int[] finalColumnSize) { 
        long beginTime, endTime;
        beginTime = System.currentTimeMillis();
        search.beginSearch(userInput, args[2], highNode, finalColumnSize);
        endTime = System.currentTimeMillis();
        misc.stats(endTime, beginTime);
    }

    public static String takeUserInput() {
        System.out.println("\nPlease input the range of date you want to search for: (start date) (end date)");
        Scanner sc = new Scanner(System.in);
        String userInput = sc.nextLine();
        sc.close();
        return userInput;
    }
}