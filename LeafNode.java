import java.util.ArrayList;

public class LeafNode {

    private ArrayList<LeafPointer> LeafNodes = new ArrayList<>();

    public ArrayList<LeafPointer> getInternalNodes()
    {
        return this.LeafNodes;
    }  
}