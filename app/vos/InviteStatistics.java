package vos;

/**
 * 邀请统计类
 * 
 * @author flyed
 * @since 2016/06/25
 *
 */
public class InviteStatistics {

	private Long buyerCount;
	private Long sellerCount;
	private Long sum;
	
	public Long getBuyerCount() {
		return buyerCount;
	}
	public void setBuyerCount(Long buyerCount) {
		this.buyerCount = buyerCount;
	}
	public Long getSellerCount() {
		return sellerCount;
	}
	public void setSellerCount(Long sellerCount) {
		this.sellerCount = sellerCount;
	}
	public Long getSum() {
		return sum;
	}
	public void setSum(Long sum) {
		this.sum = sum;
	}
	
}
