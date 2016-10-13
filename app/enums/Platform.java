package enums;

public enum Platform {
    //@formatter:off
    
    TAOBAO("淘宝"),
    TMALL("天猫"),
    JD("京东"),
    MOGUJIE("蘑菇街"),
//    YHD("1号店"),
//    JUMEI("聚美"),
//    AMAZON("亚马逊"),
//    DANGDANG("当当"),
//    QQ("拍拍"),
//    ALIBABA("阿里巴巴"),
//    MEILISHUO("美丽说"),
//    GUOMEI("国美"),
//    SUNING("苏宁"),
    ;
    //@formatter:on
    
    public String title;

    private Platform(String title) {
        this.title = title;
    }
}
