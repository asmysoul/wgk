package models.marketing;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import models.User.UserType;

import com.aton.util.JsonUtil;
import com.google.common.base.Strings;

/**
 * 
 * 活动“邀请注册”的规则及配置.
 * 
 * @author youblade
 * @since  v2.0
 * @created 2014年12月17日 下午6:22:29
 */
public class InviteRegistActivityVo {

    public Float buyerRewardRate;
    public Float sellerRewardRate;
    
    public String toJson(){
        return JsonUtil.toJson(this);
    }
    
    public static InviteRegistActivityVo valueOf(String json){
        if(Strings.isNullOrEmpty(json)){
            return null;
        }
        return JsonUtil.toBean(json, InviteRegistActivityVo.class);
    }
    
    /**
     * 
     * 算邀请人获得的金币奖励值
     *
     * @param userType
     * @param memberRechargeAmt
     * @return
     * @since  v2.0
     * @author youblade
     * @created 2014年12月20日 下午5:30:46
     */
    public long calculateRwardIngot(UserType userType, long memberRechargeAmt) {
        Float rewardRate = (userType == userType.SELLER) ? this.sellerRewardRate : this.buyerRewardRate;
        return new BigDecimal(rewardRate * 0.01).multiply(BigDecimal.valueOf(memberRechargeAmt)).longValue();
    }
}
