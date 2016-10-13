package com.aton.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.gson.Gson;

import controllers.Admin;
import play.libs.Codec;
import vos.FaBaoGuoVo;
import vos.OrderExpress;

/**
 * 
 * 发包裹
 * 
 * @author fufei
 * @since v3.4
 * @created 2015年4月29日 下午1:49:53
 */
public class FaBaoGuoUtil {
    private static final Logger log = LoggerFactory.getLogger(FaBaoGuoUtil.class);

    public static final String PARTNER_ID = "waguke";// 合作id
    public static final String PASSWORD = "e0a948dbba7ea33a739da680bb0cea47";
    public static final String URL = "http://123.59.53.123/fbgapi/add";
    public static final String URL_INFO = "http://123.59.53.123/fbgapi/account_info";
    
    /**
     * 
     * 获取参数
     *
     * @param vo
     * @return
     * @since  v3.4
     * @author fufei
     * @created 2015年4月29日 下午2:09:35
     */
    public static Map<String, String> getParam(FaBaoGuoVo vo) {
        Map<String, String> map = new HashMap<String, String>();
        String data = JsonUtil.toJson(vo);
        StringBuffer sb = new StringBuffer(PARTNER_ID);
        sb.append(PASSWORD).append(data);
        map.put("partnerid", PARTNER_ID);
        map.put("password", PASSWORD);
        map.put("jsondata", data);
        map.put("validation", Codec.hexMD5(sb.toString()));
        return map;
    }
    
    public static FaBaoGuoReturn execute(FaBaoGuoVo vo) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(WebUtils.doPost(URL, getParam(vo), "UTF-8", 2000, 5000), FaBaoGuoReturn.class);
        } catch (IOException e) {
            log.info("execute is faild!!");
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 
     * 获取账户信息
     *
     * @return
     * @since  v3.4
     * @author fufei
     * @created 2015年5月6日 下午5:11:48
     */
  public static FaBaoGuoInfoReturn getInfo() {
      try {
          Map<String, String> map = new HashMap<String, String>();
          map.put("partnerid", PARTNER_ID);
          map.put("password", PASSWORD);
          Gson gson = new Gson();
          return gson.fromJson(WebUtils.doPost(URL_INFO, map, "UTF-8", 2000, 5000), FaBaoGuoInfoReturn.class);
      } catch (IOException e) {
          log.info("execute is faild!!");
          e.printStackTrace();
      }
      return null;
  }
  
  public class FaBaoGuoInfoReturn{
      public int res;
      public String user_score;
      public String sum_score;
      public String user_price;
  }
    
  public class FaBaoGuoReturn{
        public int res;
        public String express_no;
        public Object jsondata;
    }
  
  public static final Map<Integer,String> RETURN_CODE = Maps.newHashMapWithExpectedSize(4);
  static{
      RETURN_CODE.put(7, "帐户余额不足");
      RETURN_CODE.put(10, "订单号没有传入或值为空字符串");
      RETURN_CODE.put(11, "发件人姓名没有传入");
      RETURN_CODE.put(12, "发件人电话没有传入");
      RETURN_CODE.put(13, "发件地址没有传入或值为空字符串");
      RETURN_CODE.put(14, "收件人姓名没有传入");
      RETURN_CODE.put(15, "收件人电话没有传入");
      RETURN_CODE.put(16, "收件地址没有传入或值为空字符串");
      RETURN_CODE.put(17, "包裹重量没有传入或值设置错误");
      RETURN_CODE.put(18, "商品名称没有传入");
      RETURN_CODE.put(19, "网点编号没有传入或值为空字符串");
      RETURN_CODE.put(20, "备注没有传入");
      RETURN_CODE.put(21, "订单号重复");
      RETURN_CODE.put(22, "系统错误, 请联系发包裹");
  }
  
  /**
   * 
   * 把order转换为发包裹的类
   *
   * @param orders
   * @return
   * @since  v3.4
   * @author fufei
   * @created 2015年5月4日 下午3:55:01
   */
  public static List<FaBaoGuoVo> convert(List<OrderExpress> orders) {
      List<FaBaoGuoVo> baoGuoVos = new ArrayList<FaBaoGuoVo>();
      for (OrderExpress orderExpress : orders) {
          FaBaoGuoVo guoVo = FaBaoGuoVo.newInstance();
          guoVo.id = orderExpress.id;
          guoVo.net_no = orderExpress.branch;
          guoVo.comment = "";
          guoVo.goods_name = "";
          guoVo.order_sn = orderExpress.orderId;
          guoVo.receive_addr = orderExpress.fullAddress;
          guoVo.receive_name = orderExpress.consignee;
          guoVo.receive_tel = orderExpress.mobile;
          guoVo.send_addr = orderExpress.shipperfullAddress;
          guoVo.send_name = orderExpress.shipper;
          guoVo.send_tel = orderExpress.sellerMobile;
          guoVo.sendTimeStr = orderExpress.modifyTimeStr;
          guoVo.weight=orderExpress.weight>0?orderExpress.weight+"":"";
          baoGuoVos.add(guoVo);
      }
      return baoGuoVos;
  }
 
}
