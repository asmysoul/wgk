package enums;

import com.aton.config.BizConstants;

public enum TaskListType {

    //@formatter:off
    COMMON("常规", BizConstants.TASK_EXPERIENCE_COMMON), 
    NEW_SHOP("新商家", BizConstants.TASK_EXPERIENCE_NEW_SHOP), 
    SYS_RECOMMEND("推荐", BizConstants.TASK_EXPERIENCE_SYS_RECOMMEND), 
    EXTRA_REWARD("加赏", 0),
    ;
    //@formatter:on

    public String title;
    /**
     * 不同类型的任务所对应的经验值
     */
    public int experience;

    private TaskListType(String title, int experience) {
        this.title = title;
        this.experience = experience;
    }
}
