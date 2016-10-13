package vos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonUnwrapped;

import com.aton.util.CollectionUtils;
import com.aton.util.JsonUtil;
import com.google.common.base.Strings;

/**
 * 
 * 封装买手任务进度的数据，序列化为json存入BuyerTaskStep的content字段.
 * 
 * @author youblade
 * @since  0.1
 * @created 2014年8月24日 上午10:40:28
 */
public class BuyerTaskStepVo {
    /**
     * 添加好友截图+追加评论
     */
    public List<String> itemUrls;
    /**
     * 朋友圈截图
     */
    public List<String> picUrls;
    
    @Transient
    public String secUrl;

    public String expressCompany;
    public String expressNo;
    
    //*==========买手做任务Step 5=================
    /** 订单号 */
    public String orderNo;
    
    /** 交易号*/
    public String transNo;
    
    /** 实际付款金额  */
    public String realPaidFee;

    @Deprecated
    public List<GoodRate> goodRates;
    @Deprecated
    public class GoodRate {
        public String buyerAccountNick;
        public String itemTitle;
        public String additionalItemTitle;
        public String comment;
    }
    
    /**
     * 
     * 校验商品链接、图片链接.
     *
     * @param urls
     * @param minSize
     * @return
     * @since  v0.1.8
     * @author youblade
     * @created 2014年10月20日 下午5:32:16
     */
    public static boolean validateUrls(List<String> urls, int minSize) {
        if (CollectionUtils.isEmpty(urls)) {
            return false;
        }
        if (minSize > 0 && urls.size() < minSize) {
            return false;

        }
        for (String url : urls) {
            if (Strings.isNullOrEmpty(url)) {
                return false;
            }
        }
        return true;
    }
    
    
    public static boolean validate(List<String> urls) {
        if (CollectionUtils.isEmpty(urls)) {
            return false;
        }
        for (String url : urls) {
            if (Strings.isNullOrEmpty(url)) {
                return false;
            }
        }
        return true;
    }
    
    public static BuyerTaskStepVo newInstance() {
        return new BuyerTaskStepVo();
    }

    public BuyerTaskStepVo transNo(String transNo) {
        this.transNo = transNo;
        return this;
    }
    
    public String toJson(){
        return JsonUtil.toJson(this);
    }
}
