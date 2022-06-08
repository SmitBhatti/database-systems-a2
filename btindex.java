import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

/**
 *The code for B+ tree is in this class
 */
public class btindex {

    static misc helper = new misc();
    static ArrayList<Integer> InternalNodeDates = new ArrayList<>();
    static int[] finalColumnSize = new int[11]; 
    static Integer TreeLevel; //Height of tree tracker
    static LeafNode initialNode; //The first node
    static final Integer FANOUT = 138; //Fanout which is hrdcoded and must be >=126
    static InternalNode highNode; //This is the top node
    final static int allRecords = 96300; //Total records present on system
    static final int sizeOfRecords = misc.calculate();
     
    public static void main(String[] args) {
        final int OnPageRecords = (Integer.parseInt(args[2].substring(5))) / sizeOfRecords; 
        final int sizeOfPage = Integer.parseInt(args[2].substring(5));

        int InsertionDate = 0; //Helps in tracking number of dates inserted into array
        TreeLevel = 1; //Increasing the tree level to one
        initialNode = new LeafNode();
        highNode = new InternalNode();

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

        long beginTime = System.currentTimeMillis(); //System timer begins

        File heapFile = new File(args[2]);
        try {
            int readingRecords = 0;
            int byteCount = 0;

            while (readingRecords < allRecords) {
                //Record storing in this two-dimensional array
                byte[][] byteRecords = new byte[OnPageRecords][sizeOfRecords];

                for (int i = 0; i < OnPageRecords; i++) {
                    byteRecords[i] = helper.byteFR(sizeOfRecords, byteCount + (i * sizeOfRecords), heapFile);
                }

                int currRecord = 0;
                for (byte[] record : byteRecords) {
                    int bDate = misc.getBDate(record, 65);
                    if (bDate != 0) {
                        int recordStartPosition = byteCount + (sizeOfRecords * currRecord);
                        LeafPointer leafPointer = new LeafPointer(recordStartPosition, bDate);
                        insertNewPointer(leafPointer);                        
                        InsertionDate++;
                    }
                    currRecord++;
                }
                readingRecords += OnPageRecords;
                byteCount += sizeOfPage;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        misc.stats(endTime, beginTime, InsertionDate, TreeLevel);

        Scanner sc = new Scanner(System.in);
        System.out.println("\nEnter the dates you want to search for: (start date) (end date)");
        String userInput = sc.nextLine(); //Command Line Interface for searching the records from dates int he same heap file
        sc.close();

        beginTime = System.currentTimeMillis();
        search.beginSearch(userInput, args[2], highNode);
        endTime = System.currentTimeMillis();
        misc.stats(endTime, beginTime);
    }

    public static void DivideTopNode(InternalPointer p) {
        InternalNode internalNode = new InternalNode();
        InternalNode internalNode2 = new InternalNode(); //Declare & Initialize 2 internal nodes
        internalNode.getInternalPointers().add(p);

        for (Iterator<InternalPointer> it = highNode.getInternalPointers().iterator(); it.hasNext(); ) {
            InternalPointer ip = it.next();
            if (ip.getDate() < p.getDate()) {
                internalNode2.getInternalPointers().add(ip);
                it.remove();
            }
        }

        InternalNode node = highNode;
        internalNode.getInternalPointers().get(0).setRightInternal(node);
        internalNode.getInternalPointers().get(0).setLeftInternal(internalNode2);
        highNode = internalNode;
        TreeLevel++; //Incrementing height of tree
    }

    public static void topNodeCopyUp(LeafNode node) {
        InternalNode parentNode = new InternalNode();
        LeafNode leafNode = new LeafNode();

        Integer index = FANOUT/2;
        InternalPointer internalPointer = new InternalPointer(node.getLeafPointers().get(index).getDate());
        parentNode.getInternalPointers().add(internalPointer);

        leafNode.setNextLeaf(node);

        for (Iterator<LeafPointer> iterator = node.getLeafPointers().iterator(); iterator.hasNext(); ) {
            LeafPointer value = iterator.next();
            if (value.getDate() < internalPointer.getDate()) {
                leafNode.getLeafPointers().add(value);
                iterator.remove();
            }
        }

        parentNode.getInternalPointers().get(0).setRightLeaf(node);
        parentNode.getInternalPointers().get(0).setLeftLeaf(leafNode);

        initialNode = null;
        highNode = parentNode;
        TreeLevel++;
    }

    public static void insertNewPointer(LeafPointer pointer) {
        if (initialNode != null) {
            initialNode.getLeafPointers().add(pointer);
            initialNode.getLeafPointers().sort((o1, o2) -> o1.getDate().compareTo(o2.getDate()));
            if (initialNode.getLeafPointers().size() >= FANOUT) {
                topNodeCopyUp(initialNode);
            }
        } 
        else if (!(highNode == null)) {
            continuePointerInsertion(pointer, highNode);
        } else
            System.exit(0);
        
    }

    public static InternalPointer continuePointerInsertion(LeafPointer pointer, InternalNode node) {

        boolean pointerInserted = false;

        // going through the node (formerly topNode for some reason).
        for (int i = 0; i < node.getInternalPointers().size(); i++) {
            InternalPointer internalPointer = null;

                if (node.getInternalPointers().get(i).getDate() > pointer.getDate()) {

                    pointerInserted = true;
                    
                    // Checking to see if the next node is a leaf node.
                    if (node.getInternalPointers().get(i).getLeftLeaf() != null) {
                        internalPointer = insertIntoLeaf(node.getInternalPointers().get(i).getLeftLeaf(), pointer);
                    } else {
                        internalPointer = continuePointerInsertion(pointer, node.getInternalPointers().get(i).getLeftInternal());
                    }
                } else if (i == node.getInternalPointers().size() - 1) {

                    pointerInserted = true;

                    // Checking to see if the next node is a leaf node.
                    if (node.getInternalPointers().get(i).getLeftLeaf() != null) {
                        internalPointer = insertIntoLeaf(node.getInternalPointers().get(i).getRightLeaf(), pointer);
                    } else {
                        internalPointer = continuePointerInsertion(pointer, node.getInternalPointers().get(i).getRightInternal());
                    }
                }

            // This is where we will try and insert a pointer
            // and perform a split of the leaf node.
            if (internalPointer != null) {

                    // Insert and sort.
                    node.getInternalPointers().add(internalPointer);
                    node.getInternalPointers().sort((o1, o2) -> o1.getDate().compareTo(o2.getDate()));

                    // Index of the inserterted Pointer
                    int index = node.getInternalPointers().indexOf(internalPointer);


                    // Here we check to see if there exists a left and right pointer
                    if (index == 0) {

                        // Checking if the node below is a leaf or a parent.
                        if (node.getInternalPointers().get(index + 1).getLeftLeaf() != null) {

                            // Adding a new node.
                            LeafNode leafNode = new LeafNode();

                            // Moving all nodes less than the pointer we just inserted above
                            // over to the new node. Also removing those pointers from the
                            // node we are taking them from.
                            for (Iterator<LeafPointer> iterator = node.getInternalPointers().get(index+1).getLeftLeaf().getLeafPointers().iterator(); iterator.hasNext(); ) {
                                LeafPointer value = iterator.next();
                                if (value.getDate() < internalPointer.getDate()) {
                                    leafNode.getLeafPointers().add(value);
                                    iterator.remove();
                                }
                            }

                            // Making the new leaf point to the next leaf.
                            leafNode.setNextLeaf(node.getInternalPointers().get(index+1).getLeftLeaf());

                            // Setting the node pointers up for the newly inserted pointer.
                            node.getInternalPointers().get(index).setLeftLeaf(leafNode);
                            node.getInternalPointers().get(index).setRightLeaf(node.getInternalPointers().get(index+1).getLeftLeaf());

                        } else {

                            // Adding a new node.
                            InternalNode parentNode = new InternalNode();

                            // Moving all nodes less than the pointer we just inserted above
                            // over to the new node. Also removing those pointers from the
                            // node we are taking them from.
                            for (Iterator<InternalPointer> iterator = node.getInternalPointers().get(index+1).getLeftInternal().getInternalPointers().iterator(); iterator.hasNext(); ) {
                                InternalPointer value = iterator.next();
                                if (value.getDate() < internalPointer.getDate()) {
                                    parentNode.getInternalPointers().add(value);
                                    iterator.remove();
                                }
                            }

                            // Setting the node pointers up for the newly inserted pointer.
                            node.getInternalPointers().get(index).setLeftInternal(parentNode);
                            node.getInternalPointers().get(index).setRightInternal(node.getInternalPointers().get(index+1).getLeftInternal());
                            
                        }

                    } else if (index == node.getInternalPointers().size() - 1) {

                        // Checking if the node below is a leaf or a parent.
                        if (node.getInternalPointers().get(index-1).getRightLeaf() != null) {

                            // Adding a new node.
                            LeafNode leafNode = new LeafNode();

                            // Moving all nodes greater than the pointer we just inserted above
                            // over to the new node. Also removing those pointers from the
                            // node we are taking them from.
                            for (Iterator<LeafPointer> iterator = node.getInternalPointers().get(index-1).getRightLeaf().getLeafPointers().iterator(); iterator.hasNext(); ) {
                                LeafPointer value = iterator.next();
                                if (value.getDate() >= internalPointer.getDate()) {
                                    leafNode.getLeafPointers().add(value);
                                    iterator.remove();
                                }
                            }

                            // Making the leaf to the left point to the new leaf
                            node.getInternalPointers().get(index-1).getRightLeaf().setNextLeaf(leafNode);

                            // Setting the node pointers up for the newly inserted pointer.
                            node.getInternalPointers().get(index).setRightLeaf(leafNode);
                            node.getInternalPointers().get(index).setLeftLeaf(node.getInternalPointers().get(index-1).getRightLeaf());

                        } else {

                            // Adding a new node.
                            InternalNode parentNode = new InternalNode();

                            // Moving all nodes less than the pointer we just inserted above
                            // over to the new node. Also removing those pointers from the
                            // node we are taking them from.
                            for (Iterator<InternalPointer> iterator = node.getInternalPointers().get(index-1).getRightInternal().getInternalPointers().iterator(); iterator.hasNext(); ) {
                                InternalPointer value = iterator.next();
                                if (value.getDate() > internalPointer.getDate()) {
                                    parentNode.getInternalPointers().add(value);
                                    iterator.remove();
                                }
                            }

                            // Setting the node pointers up for the newly inserted pointer.
                            node.getInternalPointers().get(index).setRightInternal(parentNode);
                            node.getInternalPointers().get(index).setLeftInternal(node.getInternalPointers().get(index-1).getRightInternal());
                        }
                    } else {
                        // Checking if the node below is a leaf or a parent.
                        if (node.getInternalPointers().get(index-1).getRightLeaf() != null) {

                            // Adding a new node.
                            LeafNode leafNode = new LeafNode();

                            // Moving all nodes less than the pointer we just inserted above
                            // over to the new node. Also removing those pointers from the
                            // node we are taking them from.
                            for (Iterator<LeafPointer> iterator = node.getInternalPointers().get(index-1).getRightLeaf().getLeafPointers().iterator(); iterator.hasNext(); ) {
                                LeafPointer value = iterator.next();
                                if (value.getDate() >= internalPointer.getDate()) {
                                    leafNode.getLeafPointers().add(value);
                                    iterator.remove();
                                }
                            }  

                            // Setting the pointer to the left so that it points to the new node.
                            node.getInternalPointers().get(index+1).setLeftLeaf(leafNode);

                            // Setting the node pointers up for the newly inserted pointer.
                            node.getInternalPointers().get(index).setRightLeaf(leafNode);
                            node.getInternalPointers().get(index).setLeftLeaf(node.getInternalPointers().get(index-1).getRightLeaf());

                            // Getting the lowerleaf of the pointer to the right and point it to
                            // the correct next leaf, then get the lowerleaf of the new pointer to
                            // point to that new pointers upperleaf.
                            node.getInternalPointers().get(index).getLeftLeaf().setNextLeaf(node.getInternalPointers().get(index).getRightLeaf());
                            node.getInternalPointers().get(index).getRightLeaf().setNextLeaf(node.getInternalPointers().get(index+1).getRightLeaf());


                        } else {

                            // Adding a new node.
                            InternalNode parentNode = new InternalNode();

                            // Moving all nodes less than the pointer we just inserted above
                            // over to the new node. Also removing those pointers from the
                            // node we are taking them from.
                            for (Iterator<InternalPointer> iterator = node.getInternalPointers().get(index-1).getRightInternal().getInternalPointers().iterator(); iterator.hasNext(); ) {
                                InternalPointer value = iterator.next();
                                if (value.getDate() < internalPointer.getDate()) {
                                    parentNode.getInternalPointers().add(value);
                                    iterator.remove();
                                }
                            }

                            // Setting the pointer to the left so that it points to the new node.
                            node.getInternalPointers().get(index-1).setRightInternal(parentNode);

                            // Setting the node pointers up for the newly inserted pointer.
                            node.getInternalPointers().get(index).setLeftInternal(parentNode);
                            node.getInternalPointers().get(index).setRightInternal(node.getInternalPointers().get(index+1).getLeftInternal());
                        }
                    }

                    if (node.getInternalPointers().size() >= FANOUT) {
                        
                        // If the parent node does not have space,
                        // push up the middle element.
                        Integer centreIndex = FANOUT/2;
                        InternalPointer centrePointer = new InternalPointer(node.getInternalPointers().get(centreIndex).getDate());

                        // Here we check if upper bound node of the pointer we just inserted is a leaf or a parent.
                        // Then we remove the pointer we just inserted in the current level from the level below (if it
                        // is an internal/parent node), thus completing our pushup.
                        if (node.getInternalPointers().get(index).getRightInternal() != null) {

                                for (Iterator<InternalPointer> iterator = node.getInternalPointers().get(index).getRightInternal().getInternalPointers().iterator(); iterator.hasNext(); ) {
                                    InternalPointer value = iterator.next();
                                    if (value.getDate() == node.getInternalPointers().get(index).getDate()) {
                                        iterator.remove();
                                    }
                                }
                        }

                        if (node.equals(highNode)) {
                            DivideTopNode(centrePointer);
                            return null;
                        }

                        return centrePointer;

                    } else {
                        return null;
                    }
            }

            // If the pointer has already been inseted,
            // retrun and do not continue.
            if (pointerInserted == true) {
                return null;
            }
        }
        return null;
    }

    public static InternalPointer insertIntoLeaf(LeafNode leafNode, LeafPointer pointer) {
        if (leafNode.getLeafPointers().size() < FANOUT - 1) {
            leafNode.getLeafPointers().add(pointer);
            leafNode.getLeafPointers().sort((o1, o2) -> o1.getDate().compareTo(o2.getDate()));
            return null;
        } else {
            leafNode.getLeafPointers().add(pointer);
            leafNode.getLeafPointers().sort((o1, o2) -> o1.getDate().compareTo(o2.getDate()));
            Integer index = FANOUT/2;
            InternalPointer internalPointer = new InternalPointer(leafNode.getLeafPointers().get(index).getDate());

            if (InternalNodeDates.contains(internalPointer.getDate())) {
                for (Integer i = index; i < leafNode.getLeafPointers().size(); i++) {
                    if (!InternalNodeDates.contains(leafNode.getLeafPointers().get(i).getDate())) {
                        internalPointer = new InternalPointer(leafNode.getLeafPointers().get(i).getDate());
                        InternalNodeDates.add(leafNode.getLeafPointers().get(i).getDate());
                        return internalPointer;
                    }
                }
            }
            InternalNodeDates.add(internalPointer.getDate());
            return internalPointer;
        }
    }

    public static void leafTraversal(LeafNode leaf, Integer upperDate, String heapFileString) {

        final int OnPageRecords = (Integer.parseInt(heapFileString.substring(5))) / sizeOfRecords; 
        final int sizeOfPage = Integer.parseInt(heapFileString.substring(5));
        if (leaf != null) {

            for (LeafPointer pointer : leaf.getLeafPointers()) {
                if (pointer.getDate() <= upperDate) {
                    Integer onPage = pointer.getRecordPosition()/sizeOfPage;

                    File heapFile = new File(heapFileString);
                    try {
                        int readingRecords = 0;
                        while (readingRecords < OnPageRecords) {
                            byte[][] byteRecords = new byte[OnPageRecords][sizeOfRecords];
                            Integer wantedRecord = 0;
                            for (int i = 0; i < OnPageRecords; i++) {
                                if ((onPage * sizeOfPage) + (i * sizeOfRecords) == pointer.getRecordPosition()) {
                                    wantedRecord = i;
                                }
                                byteRecords[i] = helper.byteFR(sizeOfRecords, (onPage * sizeOfPage) + (i * sizeOfRecords), heapFile);
                            }
                            int currRecord = 0;
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
                            readingRecords += OnPageRecords;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            leafTraversal(leaf.getNextLeaf(), upperDate, heapFileString);
        }
    }
}