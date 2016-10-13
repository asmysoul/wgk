package enums;
/**
 * 推广类型
 * @author Administrator
 *
 */
public enum TaskType2 {
    //@formatter:off
    
	PENGYOUQUAN("朋友圈"), 
    TOUPIAO("投票"),
    QUNFA("群发"),
    ;
    //@formatter:on
    
    public String title;

    private TaskType2(String title) {
        this.title = title;
    }
}
