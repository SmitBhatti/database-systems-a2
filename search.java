import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class search {
    //Helper class for implementing search

    public static void beginSearch(String command, String heapFile, InternalNode highNode, int[] finalColumnSize) {
        String[] commands = command.split("\\s+");
        leafTraversal(moveBelowSearch(Integer.parseInt(commands[0]), highNode), 
        Integer.parseInt(commands[1]), heapFile, misc.calculate(), finalColumnSize);
    }

    public static LeafNode moveBelowSearch(Integer dateField, InternalNode currentNode) {
        ArrayList<InternalPointer> currIP = currentNode.getInternalPointers();
        LeafNode leftLeaf, rightLeaf;
        InternalNode leftInternal, rightInternal;
        Integer currDate;
        System.out.println("Size is: " + currIP.size());

        for (InternalPointer current : currIP) {
            leftLeaf = current.getLeftLeaf();
            rightLeaf = current.getRightLeaf();
            leftInternal = current.getLeftInternal();
            rightInternal = current.getRightInternal();
            currDate = current.getDate();

            if (currIP.size() == 1) {

                if (currDate > dateField) {
                    if (leftLeaf == null) {
                        return moveBelowSearch(dateField, leftInternal);
                    } 
                    else {
                        return leftLeaf;
                    }
                } 
                else {
                    if (rightLeaf == null) {
                        return moveBelowSearch(dateField, rightInternal);
                    } 
                    else {
                        return rightLeaf;
                    }
                }

            }
            else if (currDate > dateField) {
                if (leftLeaf == null) {
                    return moveBelowSearch(dateField, leftInternal);
                } 
                else {
                    return leftLeaf;
                }
            } 
            else if (currDate == dateField) {

                if (rightLeaf == null) {
                    return moveBelowSearch(dateField, rightInternal);
                } 
                else {
                    return rightLeaf;
                }
            } 
            else if (currIP.indexOf(current) == (currIP.size() - 1)) {
                if (rightLeaf == null) {
                    return moveBelowSearch(dateField, rightInternal);
                } 
                else {
                    return rightLeaf;
                }
            }
        }
        System.exit(0);
        return null;
    }

    public static void leafTraversal(LeafNode leaf, Integer upperDate, String heapFileString, int sizeOfRecords, int[] finalColumnSize) {

        misc helper = new misc();
        final int OnPageRecords = (Integer.parseInt(heapFileString.substring(5))) / sizeOfRecords; 
        final int sizeOfPage = Integer.parseInt(heapFileString.substring(5));
        if (leaf != null) {

            for (LeafPointer pointer : leaf.getLeafPointers()) {
                if (pointer.getDate() <= upperDate) {
                    Integer onPage = pointer.getRecordPosition()/sizeOfPage;

                    File hf = new File(heapFileString);
                    try {
                        int readingRecords = 0;
                        while (readingRecords < OnPageRecords) {
                            byte[][] byteRecords = new byte[OnPageRecords][sizeOfRecords];
                            Integer wantedRecord = 0;
                            for (int i = 0; i < OnPageRecords; i++) {
                                if ((onPage * sizeOfPage) + (i * sizeOfRecords) == pointer.getRecordPosition()) {
                                    wantedRecord = i;
                                }
                                byteRecords[i] = helper.byteFR(sizeOfRecords, (onPage * sizeOfPage) + (i * sizeOfRecords), hf);
                            }
                            int currRecord = 0;
                            search.searchByteRecords(byteRecords, wantedRecord, currRecord, finalColumnSize);
                            readingRecords += OnPageRecords;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            leafTraversal(leaf.getNextLeaf(), upperDate, heapFileString, sizeOfPage, finalColumnSize);
        }
    }

    public static void searchByteRecords(byte[][] byteRecords, Integer wantedRecord, int currRecord, int[] finalColumnSize)
    {
        for (byte[] record : byteRecords) {
            if (currRecord == wantedRecord) {
                int recByte = 0;
                for (int i = 0; i < 11; i++) {
                    byte[] valueBytes = new byte[finalColumnSize[i]];

                    for (int j = recByte; j < (recByte + finalColumnSize[i]); j++) {
                        valueBytes[j - recByte] = record[j];
                    }
                    if (i == 1 || i == 3 || i == 9) {
                        System.out.print(" | " + ByteBuffer.wrap(valueBytes).getInt());                                            
                    } else {                                       
                        System.out.print(" | " + new String(valueBytes, StandardCharsets.UTF_8));                                            
                    }
                    recByte += finalColumnSize[i];
                }
                System.out.print("\n\n");
            }
            currRecord++;
        }
    }
}
