package enums;

public enum Platform3 {
    //@formatter:off
    
    TAOBAO("淘宝"),
    TMALL("天猫"),
    ;
    //@formatter:on
    
    public String title;

    private Platform3(String title) {
        this.title = title;
    }
}
