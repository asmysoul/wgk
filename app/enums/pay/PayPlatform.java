package enums.pay;

import models.mappers.FundAccountMapper;

import org.codehaus.groovy.transform.trait.Traits.Implemented;

/**
 * 
 * 通用的支付平台枚举类.<br>
 * 
 * 1、代码中使用此枚举协调前端、后端、DB，定义的其他支付平台枚举类要统一转换为该类进行使用.
 * 1.1 DB中存储的数据为该枚举类实例的name
 * 
 * 2、定义的其他支付平台枚举类（如：YeepayPlatform）仅作为前端页面渲染时展示数据
 * 3、定义的其他支付平台枚举类中的枚举实例的name必须是该枚举类实例name的子集
 * 
 * @author youblade
 * @since  v0.2.5
 * @created 2014年11月5日 下午6:03:14
 */
public enum PayPlatform {

    //@formatter:off
    ICBC("中国工商银行"), 
    CCB("中国建设银行"), 
    ABC("中国农业银行"), 
    COMM("交通银行"), 
    BOC("中国银行"), 
    CMB("招商银行"), 
    CITIC("中信银行"), 
    SDB("深圳发展银行"), 
    GDB("广东发展银行"), 
    SPDB("上海浦东发展银行"), 
    CEB("中国光大银行"), 
    CMBC("中国民生银行"), 
    PAB("平安银行"), 
    CIB("兴业银行"), 
    POST("中国邮政储蓄银行"), 
    HXB("华夏银行"),
    BEA("东亚银行"), 
    CBHB("渤海银行"),
    SHB("上海银行"),
    SRCB("上海农村商业银行"),
    BOB("北京银行"), 
    BJRCB("北京农商银行"),
    GZCB("广州银行"),
    HZB("杭州银行"),
    NBCB("宁波银行"),
    JSB("江苏银行"),
    NJCB("南京银行"),
    DLB("大连银行"),
    HSB("徽商银行"),
    ZSB("浙商银行"),
    UPOP("银联在线支付"),
    
    TENPAY("财付通"), 
    ALIPAY("支付宝"),
    YEEPAY("易宝付宝"),
    ;
    //@formatter:on

    public String title;

    private PayPlatform(String title) {
        this.title = title;
    }
    
    public static PayPlatform buildFrom(KQpayPlatform platform){
        return PayPlatform.valueOf(platform.toString());
    }
}
