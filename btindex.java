import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

//The code for B+ tree is in this class
public class btindex {
    static misc helper = new misc();
    static ArrayList<Integer> InternalNodeDates = new ArrayList<>();
    static int[] finalColumnSize = misc.allocateSizes(); 
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

        long beginTime = System.currentTimeMillis(); //System timer begins

        File heapFile = new File(args[2]);
        try {
            int readingRecords = 0;
            int byteCount = 0;

            while (readingRecords < allRecords) {
                //Record storing in this two-dimensional array
                byte[][] byteRecords = new byte[OnPageRecords][sizeOfRecords];

                int i = 0;
                while(i < OnPageRecords) {
                    byteRecords[i] = helper.byteFR(sizeOfRecords, byteCount + (i * sizeOfRecords), heapFile);
                    i++;
                }

                int currRecord = 0;
                for (byte[] record : byteRecords) {
                    int bDate = misc.getBDate(record, 65);
                    if (bDate != 0) {
                        int recordStartPosition = byteCount + (sizeOfRecords * currRecord);
                        LeafPointer leafPointer = new LeafPointer(recordStartPosition, bDate);
                        addAPointer(leafPointer);                        
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
        misc.searchAndDisplay(misc.takeUserInput(), args, highNode, finalColumnSize);
    }

    public static void DivideTopNode(InternalPointer p) {
        InternalNode internalNode, internalNode2;
        internalNode = new InternalNode();
        internalNode2 = new InternalNode(); //Declare & Initialize 2 internal nodes
        internalNode.getInternalPointers().add(p);

        Iterator<InternalPointer> it = highNode.getInternalPointers().iterator(); it.hasNext();
        while(it.hasNext()) {
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

        Iterator<LeafPointer> it = node.getLeafPointers().iterator();
        while(it.hasNext()) {
            LeafPointer value = it.next();
            if (value.getDate() < internalPointer.getDate()) {
                leafNode.getLeafPointers().add(value);
                it.remove();
            }
        }

        parentNode.getInternalPointers().get(0).setRightLeaf(node);
        parentNode.getInternalPointers().get(0).setLeftLeaf(leafNode);

        initialNode = null;
        highNode = parentNode;
        TreeLevel++;
    }

    public static void addAPointer(LeafPointer pointer) {
        if (initialNode != null) {
            initialNode.getLeafPointers().add(pointer);
            initialNode.getLeafPointers().sort((o1, o2) -> o1.getDate().compareTo(o2.getDate()));
            int length = initialNode.getLeafPointers().size();
            if (length >= FANOUT) {
                topNodeCopyUp(initialNode);
            }
        } 
        else if (!(highNode == null)) {
            keepAddingPointer(pointer, highNode);
        }
         else
            System.exit(0);
        
    }

    public static InternalPointer keepAddingPointer(LeafPointer LePointer, InternalNode iNode) {
        boolean isPointerInserted = false;
        ArrayList<InternalPointer> nodeIPs = iNode.getInternalPointers();
        for (int i = 0; i < nodeIPs.size(); i++) {
            InternalPointer internalPointer = null;
            if (nodeIPs.get(i).getDate() > LePointer.getDate()) {
                isPointerInserted = true;                
                if (nodeIPs.get(i).getLeftLeaf() == null)
                    internalPointer = keepAddingPointer(LePointer, nodeIPs.get(i).getLeftInternal()); 
                else
                    internalPointer = leafInsertion(nodeIPs.get(i).getLeftLeaf(), LePointer);
            } 
            else if (i == nodeIPs.size() - 1) {
                    isPointerInserted = true;
                    if (nodeIPs.get(i).getLeftLeaf() != null) {
                        internalPointer = leafInsertion(nodeIPs.get(i).getRightLeaf(), LePointer);
                    } 
                    else
                        internalPointer = keepAddingPointer(LePointer, nodeIPs.get(i).getRightInternal());
                }
            if (internalPointer != null) {
                    nodeIPs.add(internalPointer);
                    nodeIPs.sort((o1, o2) -> o1.getDate().compareTo(o2.getDate()));
                    int IPindex = nodeIPs.indexOf(internalPointer);
                    InternalPointer IPointerAtIndex = nodeIPs.get(IPindex);
                    if (IPindex == 0) {
                        if (nodeIPs.get(IPindex + 1).getLeftLeaf() != null) {
                            LeafNode leafNode = new LeafNode();
                            Iterator<LeafPointer> iterator = nodeIPs.get(IPindex+1).getLeftLeaf().getLeafPointers().iterator();
                            while(iterator.hasNext()){
                                LeafPointer value = iterator.next();
                                if (value.getDate() < internalPointer.getDate()) {
                                    leafNode.getLeafPointers().add(value);
                                    iterator.remove();
                                }
                            }
                            leafNode.setNextLeaf(nodeIPs.get(IPindex+1).getLeftLeaf());
                            IPointerAtIndex.setLeftLeaf(leafNode);
                            IPointerAtIndex.setRightLeaf(nodeIPs.get(IPindex+1).getLeftLeaf());
                        } 
                        else {
                            InternalNode internalNode = new InternalNode();
                            Iterator<InternalPointer> iterator = nodeIPs.get(IPindex+1).getLeftInternal().getInternalPointers().iterator();
                            while(iterator.hasNext()){
                                InternalPointer value = iterator.next();
                                if (value.getDate() < internalPointer.getDate()) {
                                    internalNode.getInternalPointers().add(value);
                                    iterator.remove();
                                }
                            }
                            IPointerAtIndex.setLeftInternal(internalNode);
                            IPointerAtIndex.setRightInternal(nodeIPs.get(IPindex+1).getLeftInternal());                            
                        }
                    } 
                    else if (IPindex == nodeIPs.size() - 1) {
                        if (nodeIPs.get(IPindex-1).getRightLeaf() != null) {
                            LeafNode leafNode = new LeafNode();
                            Iterator<LeafPointer> iterator = nodeIPs.get(IPindex-1).getRightLeaf().getLeafPointers().iterator();
                            while(iterator.hasNext()){
                                LeafPointer value = iterator.next();
                                if (value.getDate() >= internalPointer.getDate()) {
                                    leafNode.getLeafPointers().add(value);
                                    iterator.remove();
                                }
                            }
                            nodeIPs.get(IPindex-1).getRightLeaf().setNextLeaf(leafNode);
                            IPointerAtIndex.setRightLeaf(leafNode);
                            IPointerAtIndex.setLeftLeaf(nodeIPs.get(IPindex-1).getRightLeaf());

                        } 
                        else {
                            InternalNode parentNode = new InternalNode();
                            Iterator<InternalPointer> iterator = nodeIPs.get(IPindex-1).getRightInternal().getInternalPointers().iterator();
                            while(iterator.hasNext()){
                                InternalPointer value = iterator.next();
                                if (value.getDate() > internalPointer.getDate()) {
                                    parentNode.getInternalPointers().add(value);
                                    iterator.remove();
                                }
                            }
                            IPointerAtIndex.setRightInternal(parentNode);
                            IPointerAtIndex.setLeftInternal(nodeIPs.get(IPindex-1).getRightInternal());
                        }
                    } 
                    else {
                        if (nodeIPs.get(IPindex-1).getRightLeaf() != null) {
                            LeafNode leafNode = new LeafNode();
                            Iterator<LeafPointer> iterator = nodeIPs.get(IPindex-1).getRightLeaf().getLeafPointers().iterator();
                            while(iterator.hasNext()){
                                LeafPointer value = iterator.next();
                                if (value.getDate() >= internalPointer.getDate()) {
                                    leafNode.getLeafPointers().add(value);
                                    iterator.remove();
                                }
                            }
                            nodeIPs.get(IPindex+1).setLeftLeaf(leafNode);
                            IPointerAtIndex.setRightLeaf(leafNode);
                            IPointerAtIndex.setLeftLeaf(nodeIPs.get(IPindex-1).getRightLeaf());

                            IPointerAtIndex.getLeftLeaf().setNextLeaf(IPointerAtIndex.getRightLeaf());
                            IPointerAtIndex.getRightLeaf().setNextLeaf(nodeIPs.get(IPindex+1).getRightLeaf());
                        } 
                        else {
                            InternalNode internalNode = new InternalNode();
                            Iterator<InternalPointer> iterator = nodeIPs.get(IPindex-1).getRightInternal().getInternalPointers().iterator();
                            while(iterator.hasNext()){
                                InternalPointer value = iterator.next();
                                if (value.getDate() < internalPointer.getDate()) {
                                    internalNode.getInternalPointers().add(value);
                                    iterator.remove();
                                }
                            }
                            nodeIPs.get(IPindex-1).setRightInternal(internalNode);
                            IPointerAtIndex.setLeftInternal(internalNode);
                            IPointerAtIndex.setRightInternal(nodeIPs.get(IPindex+1).getLeftInternal());
                        }
                    }
                    if (nodeIPs.size() >= FANOUT) {                        
                        Integer centreIndex = FANOUT/2;
                        InternalPointer centrePointer = new InternalPointer(nodeIPs.get(centreIndex).getDate());
                        if (IPointerAtIndex.getRightInternal() != null) {
                            Iterator<InternalPointer> iterator = IPointerAtIndex.getRightInternal().getInternalPointers().iterator();
                            while(iterator.hasNext()){
                                InternalPointer value = iterator.next();
                                if (value.getDate() == IPointerAtIndex.getDate()) {
                                    iterator.remove();
                                }
                            }
                        }
                        if (iNode.equals(highNode)) {
                            DivideTopNode(centrePointer);
                            return null;
                        }
                        return centrePointer;
                    } 
                    else
                        return null;
            }
            if (isPointerInserted)
                return null; //No point in continuing if there already is a pointer inserted.
        }
        return null;
    }

    public static InternalPointer leafInsertion(LeafNode leafNode, LeafPointer pointer) {
        ArrayList<LeafPointer> leafPointers = leafNode.getLeafPointers();
        if (leafPointers.size() < FANOUT - 1) {
            leafPointers.add(pointer);
            leafPointers.sort((o1, o2) -> o1.getDate().compareTo(o2.getDate()));
            return null;
        } else {
            leafPointers.add(pointer);
            leafPointers.sort((o1, o2) -> o1.getDate().compareTo(o2.getDate()));
            Integer RecIndex = FANOUT/2;
            InternalPointer internalPointer = new InternalPointer(leafPointers.get(RecIndex).getDate());

            if (InternalNodeDates.contains(internalPointer.getDate())) {
                for (Integer i = RecIndex; i < leafPointers.size(); i++) {
                    if (!InternalNodeDates.contains(leafPointers.get(i).getDate())) {
                        internalPointer = new InternalPointer(leafPointers.get(i).getDate());
                        InternalNodeDates.add(leafPointers.get(i).getDate());
                        return internalPointer;
                    }
                }
            }
            InternalNodeDates.add(internalPointer.getDate());
            return internalPointer;
        }
    }

}