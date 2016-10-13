package vos;

import enums.Platform;

public class BuyerAccountSearchVo extends Page{

	public Platform platform;
	public ExamineStatus status;
	public Long userId;
	public Long accountId;
	public String userNick;
	public String accountNick;
	
	public static BuyerAccountSearchVo newInstance(){
		return new BuyerAccountSearchVo();
	}
	
	public BuyerAccountSearchVo status(ExamineStatus status){
	    this.status = status;
	    return this;
	}
	
	public BuyerAccountSearchVo platform(Platform platform){
	    this.platform = platform;
	    return this;
	}
	
	public BuyerAccountSearchVo userId(long userId){
	    this.userId = userId;
	    return this;
	}
	
	public enum ExamineStatus{
	    WAIT_EXAMINE("待审核"), 
	    EXAMINING("审核中"), 
	    EXAMINED("审核通过"),
	    NOT_PASS("审核未通过"),
	    ;
	    public String title;
	    
        private ExamineStatus(String title) {
            this.title = title;
        }
	}
}
