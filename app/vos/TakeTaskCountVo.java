package vos;

import java.util.Date;

/**
 * 
 * 
 *每日接任务单数统计
 * 
 * @author fufei
 * @since  v2.0
 * @created 2015年2月5日 上午10:38:42
 */
public class TakeTaskCountVo extends Page{
	public Date takeTime;
	public int count;
	public Integer expenseSellerCount;//放单商家数量
	public int takenBuyerCount;//接手任务买手数
	public Integer newSeller;//新增商家（已放单）
	public Integer newBuyer;//新增买手(已接单)
	public Integer buyerCount;
	public Integer sellerCount;
	
	public TakeTaskCountVo expenseSellerCount(Integer expenseSellerCount) {
		this.expenseSellerCount=expenseSellerCount==null?0:expenseSellerCount;
		return this;
	}
	
	public TakeTaskCountVo newSeller(Integer newSeller) {
		this.newSeller=newSeller==null?0:newSeller;
		return this;
	}
	
	public TakeTaskCountVo newBuyer(Integer newBuyer) {
		this.newBuyer=newBuyer==null?0:newBuyer;
		return this;
	}
	
	public TakeTaskCountVo buyerCount(Integer buyerCount) {
		this.buyerCount=buyerCount==null?0:buyerCount;
		return this;
	}
	
	public TakeTaskCountVo sellerCount(Integer sellerCount) {
		this.sellerCount=sellerCount==null?0:sellerCount;
		return this;
	}
}
