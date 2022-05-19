public class LeafPointer {

    private Integer recordPosition;
    private Integer date;

    LeafPointer(Integer recordPosition, Integer date) {
        
        this.recordPosition = recordPosition;
        this.date = date;
    }    
    public Integer getDate() {
        return this.date;
    }
}