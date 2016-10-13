package models;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.libs.WS;

import com.aton.config.CacheType;
import com.aton.util.CacheUtil;
import com.aton.util.UploadImgUtil;
import com.aton.util.UrlUtil;

import enums.Platform;
import enums.Platform3;
import enums.TaskType;
import enums.TaskType3;

/**
 * 
 * 商品信息.
 * 
 * @author youblade
 * @since 0.1
 * @created 2014年8月1日 下午2:31:34
 */
public class Item implements Serializable{

    public String id;
    public String title;
    public String url;
    public String imgUrl;
    public String price;
    
    //七牛上该图片地址
    public String itemPic;

    public static Logger log = LoggerFactory.getLogger(Item.class);

    /**
     * 
     * 根据URL获取商品信息.
     *
     * @param url
     * @param platform
     * @return  [id,title,imgUrl,price]
     * @since  0.1
     * @author youblade
     * @created 2014年8月1日 下午3:44:14
     */
    public static Item findByUrl(String url, Platform platform,TaskType taskType) {
        try {
        	//先从缓存中取
        	String id = UrlUtil.getParam("id", url);
        	String key = CacheType.TASK_ITEM_INFO.getKey(id,platform);
        	
        	Item item = CacheUtil.get(key);
        	if(item != null){
        		return item;
        	}
        	
            String html = WS.url(url).getAsync().get(8, TimeUnit.SECONDS).getString();
            Document doc = Jsoup.parse(html);
            
            item = new Item();
			switch (platform) {
			case TAOBAO:
				item.id = UrlUtil.getParam("id", url);
				item.title = doc.select("#J_Title .tb-main-title").text();
				item.imgUrl = doc.select("#J_ImgBooth").attr("data-src");
				item.price = doc.select("#J_StrPrice .tb-rmb-num").text();
				break;
			case TMALL:
				item.id = UrlUtil.getParam("id", url);
				item.title = doc.select("#J_ImgBooth").attr("alt");
				item.imgUrl = doc.select("#J_ImgBooth").attr("src");
				item.price = doc.select("#J_Detail .originPrice").text();
				break;
			case JD:
				item.id = StringUtils.substring(url, url.lastIndexOf("/")+1, url.lastIndexOf("."));
				item.title = doc.select("#name h1").text();
				item.imgUrl = doc.select("#spec-n1 img").attr("src");
				item.price = doc.select("#itemInfo #summary-price #jd-price").text();
				break;
			case MOGUJIE:
				if(StringUtils.contains(url, "?")){
					item.id = StringUtils.substring(url, url.lastIndexOf("/")+1,url.indexOf("?"));
				}else {
					item.id = StringUtils.substring(url, url.lastIndexOf("/")+1);
				}
				item.title = doc.select("#J_GoodsInfo .goods-title").text();
				item.imgUrl = doc.select("#J_BigImg").attr("src");
				item.price = doc.select("#J_NowPrice").text();
				break;
//			case YHD:
//				if(StringUtils.contains(url, "?")){
//					item.id = StringUtils.substring(url, url.lastIndexOf("/")+1,url.indexOf("?"));
//				}else {
//					item.id = StringUtils.substring(url, url.lastIndexOf("/")+1);
//				}
//				item.title = doc.select("#productMainName").text();
//				item.imgUrl = doc.select("#J_prodImg").attr("src");
//				item.price = doc.select("#current_price").text();
//				break;
//			case JUMEI:
//				item.id = StringUtils.substring(url, url.indexOf("_")+1,url.lastIndexOf("."));
//				item.title = doc.select("#detail_top .title").text();
//				item.imgUrl = doc.select("#albums .ac_container img").eq(0).attr("big");
//				item.price = doc.select("#mall_price").text();
//				break;
//			case AMAZON:
//				if(StringUtils.contains(url, "?")){
//					item.id = StringUtils.substring(url, url.indexOf("dp/")+3 ,url.lastIndexOf("/"));
//				}else {
//					item.id = StringUtils.substring(url, url.lastIndexOf("/")+1);
//				}
//				item.title = doc.select("#productTitle").text();
//				item.imgUrl = doc.select("#imgTagWrapperId img").attr("data-old-hires");
//				item.price = doc.select("#priceblock_ourprice").text();
//				item.price = StringUtils.substring(item.price, item.price.indexOf("￥")+1);
//				break;
//			case DANGDANG:
//				item.id = StringUtils.substring(url, url.lastIndexOf("/")+1, url.indexOf(".html"));
//				item.title = doc.select(".head h1").text();
//				item.imgUrl = doc.select("#largePic").attr("wsrc");
//				item.price = doc.select("#salePriceTag").text();
//				break;
//			case QQ:
//				if(StringUtils.contains(url, "?")){
//					item.id = StringUtils.substring(url, url.lastIndexOf("/")+1,url.indexOf("?"));
//				}else {
//					item.id = StringUtils.substring(url, url.lastIndexOf("/")+1);
//				}
//				item.title = doc.select("#itemForm .pp_prop_fn").text();
//				item.imgUrl = StringUtils.trim(doc.select("#pfhlkd_picshower img").attr("src"));
//				item.price = doc.select("#commodityCurrentPrice").text();
//				break;
//			case ALIBABA:
//				item.id = StringUtils.substring(url, url.lastIndexOf("/")+1,url.indexOf(".html"));
//				item.title = doc.select("#mod-detail-title .d-title").text();
//				item.imgUrl = doc.select("#mod-detail-bd .box-img img").attr("src");
//				item.price = doc.select("#mod-detail-price .price .value").text();
//				break;
//			case MEILISHUO:
//				if (StringUtils.contains(url, "?")) {
//					item.id = StringUtils.substring(url, url.lastIndexOf("/") + 1, url.indexOf("?"));
//				} else {
//					item.id = StringUtils.substring(url, url.lastIndexOf("/") + 1);
//				}
//				item.title = doc.select(".item-title").text();
//				item.imgUrl = doc.select("#picture img").attr("src");
//				item.price = doc.select("#price-now").text();
//				break;
//			case GUOMEI:
//				if(StringUtils.contains(url, "-")){
//					item.id = StringUtils.substring(url, url.lastIndexOf("/")+1,url.indexOf("-"));
//				}else {
//					item.id = StringUtils.substring(url, url.lastIndexOf("/")+1,url.indexOf(".html"));
//				}
//				item.title = doc.select(".prdtit").text();
//				item.imgUrl = doc.select(".j-bpic-b").attr("jqimg");
//				item.price = doc.select("#prdPrice").text();
//				break;
//			case SUNING:
//				item.id = StringUtils.substring(url, url.lastIndexOf("detail_")+7,url.indexOf(".html"));
//				item.title = doc.select("#itemDisplayName").text();
//				item.imgUrl = doc.select("#bigImg img").attr("src");
//				item.price = doc.select("#promotionPrice").text();
//				item.price = StringUtils.substring(item.price, item.price.indexOf("¥")+1);
//				break;
			}
			
			if (StringUtils.isBlank(item.imgUrl)) {
				switch (taskType) {
				case JHS:
					item.id = UrlUtil.getParam("id", url);
					item.title = doc.select(".J_mainBox .title").text();
					item.imgUrl = doc.select(".normal-pic .item-pic-wrap .J_zoom").attr("src");
					item.price = doc.select(".J_juBuyBtns .currentPrice").text();
					break;
				default:
					return null;
				}
			}
            // 检测到数据 放缓存
			if (StringUtils.isNotBlank(item.imgUrl)) {
				
				//将图片上传到七牛
				item.itemPic = UploadImgUtil.uploadImg(item.imgUrl);
				
				//放缓存
				//CacheUtil.set(key, item, CacheType.TASK_ITEM_INFO.expiredTime);
			}
            
            return item;
            
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }
    
    
    /**
     * 
     * 根据URL获取商品信息.
     *
     * @param url
     * @param platform
     * @return  [id,title,imgUrl,price]
     * @since  0.1
     * @author youblade
     * @created 2014年8月1日 下午3:44:14
     */
    public static Item findByUrl(String url, Platform3 platform,TaskType3 taskType) {
        try {
        	//先从缓存中取
        	String id = UrlUtil.getParam("id", url);
        	String key = CacheType.TASK_ITEM_INFO.getKey(id,platform);
        	
        	Item item = CacheUtil.get(key);
        	if(item != null){
        		return item;
        	}
        	
            String html = WS.url(url).getAsync().get(8, TimeUnit.SECONDS).getString();
            Document doc = Jsoup.parse(html);
            
            item = new Item();
			switch (platform) {
			case TAOBAO:
				item.id = UrlUtil.getParam("id", url);
				item.title = doc.select("#J_Title .tb-main-title").text();
				item.imgUrl = doc.select("#J_ImgBooth").attr("data-src");
				item.price = doc.select("#J_StrPrice .tb-rmb-num").text();
				break;
			case TMALL:
				item.id = UrlUtil.getParam("id", url);
				item.title = doc.select("#J_ImgBooth").attr("alt");
				item.imgUrl = doc.select("#J_ImgBooth").attr("src");
				item.price = doc.select("#J_Detail .originPrice").text();
				break;
			}
			
			if (StringUtils.isBlank(item.imgUrl)) {
				switch (taskType) {
				case JHS:
					item.id = UrlUtil.getParam("id", url);
					item.title = doc.select(".J_mainBox .title").text();
					item.imgUrl = doc.select(".normal-pic .item-pic-wrap .J_zoom").attr("src");
					item.price = doc.select(".J_juBuyBtns .currentPrice").text();
					break;
				default:
					return null;
				}
			}
            // 检测到数据 放缓存
			if (StringUtils.isNotBlank(item.imgUrl)) {
				
				//将图片上传到七牛
				item.itemPic = UploadImgUtil.uploadImg(item.imgUrl);
				
				//放缓存
				//CacheUtil.set(key, item, CacheType.TASK_ITEM_INFO.expiredTime);
			}
            
            return item;
            
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }
    
    

    @Override
    public String toString() {
        return "Item [id=" + id + ", title=" + title + ", url=" + url + ", imgUrl=" + imgUrl + ", price=" + price + "]";
    }
}
