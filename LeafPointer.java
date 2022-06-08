public class LeafPointer {

    private Integer recordPosition;
    private Integer date;

    public LeafPointer(Integer recordPosition, Integer date) {        
        this.recordPosition = recordPosition;
        this.date = date;
    }    
    public Integer getDate() {
        return this.date;
    }

    public Integer getRecordPosition() {
        return this.recordPosition;
    }

    public String toString() {
        return this.date.toString() + "@" + this.recordPosition;
    }
}