package enums;

import models.mappers.FundAccountMapper;

import org.codehaus.groovy.transform.trait.Traits.Implemented;

/**
 * 
 * 财付通支持的支付平台.
 * 
 * @author youblade
 * @since 0.1
 * @created 2014年9月10日 下午1:52:08
 */
@Deprecated
public enum TenpayPlatform{

    //@formatter:off
    ICBC("ICBC_D", "中国工商银行"), 
    CCB("CCB_D", "中国建设银行"), 
    ABC("ABC", "中国农业银行"), 
    COMM("COMM_D", "交通银行"), 
    BOC("BOC_D", "中国银行"), 
    CMB("CMB_D", "招商银行"), 
    SDB("SDB", "深圳发展银行"), 
    CITIC("CITIC", "中信银行"), 
    GDB("GDB_D", "广东发展银行"), 
    SPDB("SPDB", "上海浦东发展银行"), 
    CEB("CEB_D", "中国光大银行"), 
    CMBC("CMBC_D", "中国民生银行"), 
    PAB("PAB", "平安银行"), 
    CIB("CIB_D", "兴业银行"), 
//    POSTGC("POSTGC","中国邮政储蓄银行（仅支持广东地区）"), 
    BOB("BOB", "北京银行"), 
//    BEA("BEA_D", "东亚银行"), 
    TENPAY("0", "财付通"), 
    ALIPAY("1","支付宝"),
//    WHT("1033", "网汇通"),
    ;
    //@formatter:on

    /**
     * 机构代码
     */
    public String code;
    public String name;

    private TenpayPlatform(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
