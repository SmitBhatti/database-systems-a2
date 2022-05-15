public class InternalPointer {
   
    private int date;
    private InternalNode upperInternal;
    private InternalNode lowerInternal;
    private LeafNode upperLeaf;
    private LeafNode lowerLeaf;

    public InternalPointer(int date) {
        this.date = date;
    }

    public int getDate()
    {
        return date;
    }
    
    public InternalNode getUpperInternal()
    {
        return upperInternal;
    }

    public void setUpperInternal(InternalNode node)
    {
        this.upperInternal = node;
    }

    public InternalNode getLowerInternal()
    {
        return lowerInternal;
    }

    public void setlowerInternal(InternalNode node)
    {
        this.upperInternal = node;
    }

    public LeafNode getUpperLeaf()
    {
        return upperLeaf;
    }

    public void setUpperLeaf(LeafNode node)
    {
        this.upperLeaf = node;
    }

    public LeafNode getLowerLeaf()
    {
        return lowerLeaf;
    }

    public void setLowerLeaf(LeafNode node)
    {
        this.upperLeaf = node;
    }
}