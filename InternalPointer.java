public class InternalPointer {
   
    private Integer date;
    private InternalNode rightInternal;
    private InternalNode leftInternal;
    private LeafNode rightLeaf;
    private LeafNode leftLeaf;

    public InternalPointer(Integer date) {
        this.date = date;
    }

    public Integer getDate() {
        return date;
    }
    
    public InternalNode getLeftInternal() {
        return leftInternal;
    }

    public void setRightInternal(InternalNode node) {
        this.rightInternal = node;
    }

    public InternalNode getRightInternal() {
        return rightInternal;
    }

    public void setLeftInternal(InternalNode node) {
        this.leftInternal = node;
    }

    public LeafNode getRightLeaf() {
        return rightLeaf;
    }

    public void setRightLeaf(LeafNode node) {
        this.rightLeaf = node;
    }

    public LeafNode getLeftLeaf() {
        return leftLeaf;
    }

    public void setLeftLeaf(LeafNode node) {
        this.leftLeaf = node;
    }
}