package vos;

import com.aton.config.BizConstants;

/**
 * 
 * 个人中心-【买手】财务信息相关字段的封装.
 * 
 * @author youblade
 * @since  0.1
 * @created 2014年9月30日 下午5:20:28
 */
public class PersonalInfoVo {

    public int taskCount;
    public long sumRewardIngot;
    public long sumPaidFee;
    
    public long getLockedIngot(){
        return this.taskCount * BizConstants.TASK_TAKING_INGOT * 100;
    }
}
