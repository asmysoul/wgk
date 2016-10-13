package vos;

import enums.Platform3;


public class BuyerAccountSearchVo3 extends Page{

	public Platform3 platform;
	public ExamineStatus3 status;
	public Long userId;
	public Long accountId;
	public String userNick;
	public String accountNick;
	
	public static BuyerAccountSearchVo3 newInstance(){
		return new BuyerAccountSearchVo3();
	}
	
	public BuyerAccountSearchVo3 status(ExamineStatus3 status){
	    this.status = status;
	    return this;
	}
	
	public BuyerAccountSearchVo3 platform(Platform3 platform){
	    this.platform = platform;
	    return this;
	}
	
	public BuyerAccountSearchVo3 userId(long userId){
	    this.userId = userId;
	    return this;
	}
	
	public enum ExamineStatus3{
	    WAIT_EXAMINE("待审核"), 
	    EXAMINING("审核中"), 
	    EXAMINED("审核通过"),
	    NOT_PASS("审核未通过"),
	    ;
	    public String title;
	    
        private ExamineStatus3(String title) {
            this.title = title;
        }
	}
}
