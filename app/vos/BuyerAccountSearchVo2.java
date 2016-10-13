package vos;

import enums.Platform;
import enums.Platform2;

public class BuyerAccountSearchVo2 extends Page{

	public Platform2 platform;
	public ExamineStatus2 status;
	public Long userId;
	public Long accountId;
	public String userNick;
	public String accountNick;
	
	public static BuyerAccountSearchVo2 newInstance(){
		return new BuyerAccountSearchVo2();
	}
	
	public BuyerAccountSearchVo2 status(ExamineStatus2 status){
	    this.status = status;
	    return this;
	}
	
	public BuyerAccountSearchVo2 platform(Platform2 platform){
	    this.platform = platform;
	    return this;
	}
	
	public BuyerAccountSearchVo2 userId(long userId){
	    this.userId = userId;
	    return this;
	}
	
	public enum ExamineStatus2{
	    WAIT_EXAMINE("待审核"), 
	    EXAMINING("审核中"), 
	    EXAMINED("审核通过"),
	    NOT_PASS("审核未通过"),
	    ;
	    public String title;
	    
        private ExamineStatus2(String title) {
            this.title = title;
        }
	}
}
