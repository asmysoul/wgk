package enums;

public enum TaskType {
    //@formatter:off
    
    ORDER("订单"), 
    JHS("聚划算"),
    SUBWAY("直通车"),
    ;
    //@formatter:on
    
    public String title;

    private TaskType(String title) {
        this.title = title;
    }
}
