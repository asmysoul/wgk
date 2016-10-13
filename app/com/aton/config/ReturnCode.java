package com.aton.config;

public class ReturnCode {

    /*---------------- OK ---------------------*/
    public static final int OK = 200;
    public static final int FALSE = 599;
    public static final int WRONG_INPUT = 400;
    public static final int UNAUTHORIZED = 401;

    public static final int FAIL = 555;
    public static final int INNER_ERROR = 500;
    public static final int SYSTEM_BUSY = 503;


    /*---------------- Session Related---------------------*/
    public static final int INVALID_SESSION = 561;
    public static final int INVALID_PRIVILEGE = 583;

    /* ------------- Taobao Api Internal ------------------------*/
    public static final int API_CALL_LIMIT = 591;

    
    /* ------------- Words Business Error ------------------*/
    /**
     * 搜索服务不可用
     */
    public static final int ES_SERVICE_UNAVAILABLE = 5005;	
    /**
     * 搜索返回查询结果为空
     */
    public static final int ES_SEARCH_RETURN_EMPTY = 5001;	
    
    
    /** 
     * 业务操作受限：根据业务规则，限制用户使用某些功能等<br>
     * (ps:biz is short for "business" in English)
     */
    public static final int BIZ_LIMIT = 8001;
    /** 
     * 业务操作受限：根据业务规则，限制用户进行操作等<br>
     * 
     * 适用场景：
     * 1、待添加的数据已存在，不允许重复添加
     * 2、上个操作提交的异步任务正在执行中，尚未完成，禁止用户提交新任务
     * 
     * (ps:biz is short for "business" in English)
     */
    public static final int OP_BIZ_LIMIT = 800101;
    
    /** 
     * 免费用户业务操作受限：根据业务规则，限制免费版用户使用某些功能<br>
     * (ps:biz is short for "business" in English)
     */
    public static final int FREE_BIZ_LIMIT = 8002;
    
    /** 
     * 业务操作受限：上次的任务未完成
     */
    public static final int TASK_UNFINISHED = 8011;
    /** 
     * 业务操作受限：任务已全被领取完毕
     */
    public static final int TASK_TAKE_OVER = 8012;
}
