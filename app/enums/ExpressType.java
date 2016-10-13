package enums;

public enum ExpressType {
    //@formatter:off
    
    SELLERKD("商家快递"), 
    KJKD("快捷快递"),
    YDKD("韵达快递"),
    ;
    //@formatter:on
    
    public String title;

    private ExpressType(String title) {
        this.title = title;
    }
}
