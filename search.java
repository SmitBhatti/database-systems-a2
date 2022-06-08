public class search {
    //Helper class for implementing search

    public static void beginSearch(String command, String heapFile, InternalNode highNode) {
        String[] commands = command.split("\\s+");
        btindex.leafTraversal(travelDownForSearch(Integer.parseInt(commands[0]), highNode), 
        Integer.parseInt(commands[1]), heapFile);
    }

    public static LeafNode travelDownForSearch(Integer date, InternalNode currentNode) {
        System.out.println("SIZE: " + currentNode.getInternalPointers().size());

        for (int i = 0; i < currentNode.getInternalPointers().size(); i++) {
            if (currentNode.getInternalPointers().size() == 1) {

                if (currentNode.getInternalPointers().get(i).getDate() > date) {
                    if (currentNode.getInternalPointers().get(i).getLeftLeaf() == null) {
                        return travelDownForSearch(date, currentNode.getInternalPointers().get(i).getLeftInternal());
                    } else {
                        return currentNode.getInternalPointers().get(i).getLeftLeaf();
                    }
                } else {
                    if (currentNode.getInternalPointers().get(i).getRightLeaf() == null) {
                        return travelDownForSearch(date, currentNode.getInternalPointers().get(i).getRightInternal());
                    } else {
                        return currentNode.getInternalPointers().get(i).getRightLeaf();
                    }
                }

            } else if (currentNode.getInternalPointers().get(i).getDate() > date) {
                if (currentNode.getInternalPointers().get(i).getLeftLeaf() == null) {
                    return travelDownForSearch(date, currentNode.getInternalPointers().get(i).getLeftInternal());
                } else {
                    return currentNode.getInternalPointers().get(i).getLeftLeaf();
                }
            } else if (currentNode.getInternalPointers().get(i).getDate() == date) {

                if (currentNode.getInternalPointers().get(i).getRightLeaf() == null) {
                    return travelDownForSearch(date, currentNode.getInternalPointers().get(i).getRightInternal());
                } else {
                    return currentNode.getInternalPointers().get(i).getRightLeaf();
                }
            } else if (i == (currentNode.getInternalPointers().size() - 1)) {
                if (currentNode.getInternalPointers().get(i).getRightLeaf() == null) {
                    return travelDownForSearch(date, currentNode.getInternalPointers().get(i).getRightInternal());
                } else {
                    return currentNode.getInternalPointers().get(i).getRightLeaf();
                }
            }
        }
        System.exit(0);
        return null;
    }
}
