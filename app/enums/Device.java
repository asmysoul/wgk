package enums;

public enum Device {
    //@formatter:off
    
    PC("电脑"), 
    MOBILE("手机"),
    ;
    //@formatter:on
    
    public String title;

    private Device(String title) {
        this.title = title;
    }
}
