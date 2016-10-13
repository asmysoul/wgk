package enums.pay;


/**
 * 
 * 快钱支付所支持的网银平台.
 * 
 * @author youblade
 * @since  v1.0
 * @created 2014年11月13日 下午8:31:44
 */
public enum KQpayPlatform{

    //@formatter:off
    CMB("CMB", "招商银行"),
    ICBC("ICBC", "中国工商银行"),
    ABC("ABC", "中国农业银行"),
    CCB("CCB", "中国建设银行"),
    BOC("BOC", "中国银行"),
    SPDB("SPDB", "上海浦东发展银行"),
    /**
     * 注意：页面上使用代码为BCOM，支付通知成功后使用代码为COMM
     */
    COMM("BCOM", "交通银行"),
    CMBC("CMBC", "中国民生银行"),
    SDB("SDB", "深圳发展银行"),
    GDB("GDB", "广发银行"),
    CITIC("CITIC", "中信银行"),
    HXB("HXB", "华夏银行"),
    CIB("CIB", "兴业银行"),
    GZCB("GZCB", "广州银行"),
    SRCB("SRCB", "上海农村商业银行"),
    BOB("BOB", "北京银行"),
    CBHB("CBHB", "渤海银行"),
    BJRCB("BJRCB", "北京农商银行"),
    NJCB("NJCB", "南京银行"),
    CEB("CEB", "光大银行"),
    BEA("BEA", "东亚银行"),
    NBCB("NBCB", "宁波银行"),
    HZB("HZB", "杭州银行"),
    PAB("PAB", "平安银行"),
    HSB("HSB", "徽商银行"),
    ZSB("CZB", "浙商银行"),
    SHB("SHB", "上海银行"),
    POST("PSBC", "中国邮政储蓄银行"),
//    UPOP("UPOP", "银联在线支付"),
    JSB("JSB", "江苏银行"),
    
//    DLB("DLB", "大连银行"),
    ;
    //@formatter:on

    /**
     * 机构代码
     */
    public String code;
    public String title;

    private KQpayPlatform(String code, String title) {
        this.code = code;
        this.title = title;
    }
    
    public static KQpayPlatform buildFromCode(String code){
        for (KQpayPlatform p : KQpayPlatform.values()) {
            if(p.code.equals(code)){
                return p;
            }
        }
        // 增加对“交通银行”的特殊处理
        if(COMM.toString().equals(code)){
            return COMM;
        }
        return null;
    }
}
