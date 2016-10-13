package vos;

import java.util.Date;

import enums.Platform;

public class ShopSearchVo extends Page{
	 	public Long id;
	    
	 	public long sellerId;

	    /** 所属电商平台 */
	    public Platform platform;
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
	        if (this.platform != null) {
	            this.platformTitle = this.platform.title;
	        }
	    }
}
