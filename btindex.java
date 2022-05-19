import java.io.*;
import java.io.File;  
import java.io.IOException;
import java.nio.ByteBuffer;

public class btindex {
    public static void main (String[] args ) throws IOException {
        btindex bd = new btindex();
        final int RDFSchema = 65;
        final int bDate = 4;
        final int allRecords = 96300;

        int[] NamesArr =    {65, //RDFSchema
                            4,   //bDate
                            160, //bPlace
                            4,   //deathDateByteSize, 
                            229, //fieldLabelByteSize, 
                            748, //genreLabelByteSize, 
                            1886,//instrumentLabelByteSize, 
                            421, //nationalityLabelByteSize, 
                            206, //thumbnailByteSize, 
                            4,   //wikiPageIDRDFSchema, 
                            468, //descriptionByteSize
                            };

        // Space required for a single record is:
        int recordsSpace = 0;
        for (int i = 0; i < NamesArr.length; i++) {
            recordsSpace += NamesArr[i];
        }
        File f = new File(args[2]);
        // Keep the position ontrack for the file
        int endOfPg = 0;
        int totalRecords = 0;

        // Extracting page size from the heap file name
        int pageSize = Integer.parseInt(args[2].substring(5));
        int recordsOnPg = pageSize / recordsSpace;    

        try {
            // Loop to iterate through every record present ont he file (allRecords)
            for (totalRecords = 0; totalRecords < allRecords;) {

                byte[][] PgRecs = new byte[recordsOnPg][recordsSpace];
                
                // Reading pages form heap and storing it in array 'PgRecs'
                int recordNo = 0;
                RandomAccessFile rf = new RandomAccessFile(f, "r");
                while (recordNo != recordsOnPg) {
                    byte[] RecDataInBytes = new byte[recordsSpace];
                    rf.seek(endOfPg + (recordsSpace * recordNo));
                    rf.readFully(RecDataInBytes);
                    PgRecs[recordNo] = RecDataInBytes;
                    recordNo = bd.increment(recordNo);
                }
                rf.close();

                // Going over the records and extracting the date
                recordNo = 0;
                while (recordNo != recordsOnPg) {
                    byte[] currRec = PgRecs[recordNo];
                    byte[] birthDate = new byte[bDate];

                    for (int i = 0; i < 4; i++) {
                        birthDate[i] = currRec[RDFSchema + i];;
                    }
                    
                    // Extracting the birthDate as an integer
                    int bdateInteger = ByteBuffer.wrap(birthDate).getInt();                                       // For each value in each field of the record
                    int recordPos = 0;
                    for (int fieldValue = 0; fieldValue < 11; fieldValue++) {
                        recordPos += NamesArr[fieldValue];
                        }
                    System.out.println();         
                    recordNo++;
                }
                endOfPg += pageSize;
                totalRecords += recordsOnPg;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }  
    }
    public int increment(int i)
    {
        i = i + 1;
        return i;
    }
}