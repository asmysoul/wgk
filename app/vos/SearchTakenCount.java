package vos;


/**
 * 
 * 后台统计新买手接单、新商家放单
 * 
 * @author fufei
 * @since  v2.9
 * @created 2015年3月23日 下午4:13:27
 */
public class SearchTakenCount extends Page {
    public String nick;
    public int orderNum;
    /**
     * Constructs a <code>SearchTakenCount</code>
     *
     * @since  v2.9
     */
    public SearchTakenCount(String nick, int orderNum) {
        super();
        this.nick = nick;
        this.orderNum = orderNum;
    }
    
}
