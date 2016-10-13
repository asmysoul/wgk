package enums;

public enum TaskType3 {
    //@formatter:off
    
	ORDER("自然搜索"), 
    JHS("淘口令/二维码"),
    SUBWAY("直通车")
    ;
    //@formatter:on
    
    public String title;

    private TaskType3(String title) {
        this.title = title;
    }
}
