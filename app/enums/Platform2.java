package enums;
/**
 * 绑定推广
 * @author Administrator
 *
 */
public enum Platform2 {
    //@formatter:off
    
    WEIXIN("微信"),
    QQ("QQ"),
    WEIBO("微博"),
    OTHER("其他"),
    ;
    //@formatter:on
    
    public String title;

    private Platform2(String title) {
        this.title = title;
    }
}
