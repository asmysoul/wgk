package enums;

/**
 * 
 * @ClassName: CollectionType 
 * @Description: 收藏加购
 * @author: wangsm
 * @date: 2016年3月19日 上午11:49:29
 */
public enum CollectionType {
	ONE("24小时"), 
//  KJKD("快捷快递"),//by DavidLiu 20151021
  TWO("48小时"),
  THREE("72小时"),
  ;
  //@formatter:on
  
  public String title;

  private CollectionType(String title) {
      this.title = title;
  }
}
