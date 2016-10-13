package domain;

import enums.TaskStatus;


public class UserMoneyStats {
    /*
     *  以下字段为SQL返回结果构造vo使用
     */
    public long amount;
    public Boolean sysRefund;
    public TaskStatus staus;
    public int count;
}
