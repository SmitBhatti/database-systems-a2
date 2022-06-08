import java.util.ArrayList;

public class LeafNode {

    private ArrayList<LeafPointer> LeafPointers;
    private LeafNode nextLeaf;

    public LeafNode() {
        this.LeafPointers= new ArrayList<>();
        this.nextLeaf = null;
    }

    public ArrayList<LeafPointer> getLeafPointers() {
        return this.LeafPointers;
    }  

    public LeafNode getNextLeaf() {
        return this.nextLeaf;
    }

    public void setNextLeaf(LeafNode leaf) {
        this.nextLeaf = leaf;
    }

    public int getSizeOfLeaf() {
        return LeafPointers.size();
    }
}