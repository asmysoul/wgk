package vos;

import java.util.Date;

import enums.Platform;
import enums.Platform2;

public class ShopSearchVo2 extends Page{
	 	public Long id;
	    
	 	public long sellerId;

	    /** 所属电商平台 */
	    public Platform2 platform2;
	    /**
	     * 平台的标题文字
	     */
	    public String platformTitle;
	    /** 店铺地址 */
	    public String url;
	    /** 店铺名字 */
	    public String name;
	    /** 卖家账号（旺旺ID等） */
	    public String nick;
	    /** 发件地址 */
	    public String address;
	    
	    public Date createTime;
	    public Date createTimeStart;
	    public Date createTimeEnd;
	    public String userNick;
	    
	    public String sellerName;
	    public String mobile;
	    public String branch;
	    public String street;
	    
	    public void displayPlatformTitle() {
	        if (this.platform2 != null) {
	            this.platformTitle = this.platform2.title;
	        }
	    }
}
