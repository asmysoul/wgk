package domain;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.libs.F.Promise;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.libs.WS.WSRequest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aton.config.Config;
import com.aton.config.ReturnCode;
import com.aton.util.JsonUtil;
import com.aton.util.MixHelper;
import com.aton.vo.AjaxResult;

public class BizService {

    private static final Logger log = LoggerFactory.getLogger(BizService.class);

    private static String SEAVER_URI = "http://" + Config.BIZ_HOST + ":" + Config.BIZ_POST;

    // --------------------用户登录---------------------------------------
    public static String USER_LOGIN = "/user/doLogin?user={}&nick={}";

    // --------------------资金相关---------------------------------------
    public static String MEMBER = "/user/doLogin";

    // --------------------任务模块---------------------------------------
    /*
     * 商家任务
     */
    public static String TASK_INFO = "/seller/task/{}/info";
    
    public static String TASK_DETAIL = "/seller/task/{}";
    public static String TASK_FIELDS_DETAIL = "/seller/task/{}?fields={}";
    public static String TASK_FINISHED_COUNT = "/seller/task/{}/finised";
    public static String TASK_NOTPASS_COUNT = "/seller/task/count/notpass?uid={}";
    
    public static String TASK_ADD = "/seller/task/add";
    public static String TASK_SAVE = "/seller/task/{}/save";
    public static String TASK_CANCEL = "/seller/task/{}/cancel";

    public static String TASK_ITEM = "/seller/task/item?url={}&p={}";
    public static String TASK_ORDER_FEE = "/seller/task/item";
    
    public static String TASK_CONFIRM_PAY = "/task/publish/confirmPay?ingot={}&pledge={}&tid={}";

    /*
     * 买手任务
     */
    public static String BUYER_TASK_INFO = "/buyer/task/{}/info";
    public static String BUYER_TASK_DETAIL = "/buyer/task/{}";
    public static String BUYER_TASK_ADD = "/buyer/task/add";
    public static String BUYER_TASK_CANCEL = "/buyer/task/{}/cancel";
    public static String BUYER_TASK_STEP_ADD = "/buyer/task/step/add";

    // --------------------资金管理---------------------------------------
    public static String PAY_REQUEST = "/user/pay/{}?p={}";

    // --------------------后台管理---------------------------------------
    public static String ADMINB_LOGIN = "/admin/doLogin";

    public enum HttpMethod {
        GET, POST
    }

    /**
     * 
     * 获取单个业务对象.
     *
     * @param clazz
     * @param route
     * @param params
     * @return
     * @author youblade
     * @created 2014年11月12日 下午3:59:28
     */
    public static <T> T fetchOne(Class clazz, String route, Object... params) {
        String json = request(HttpMethod.GET, 5000, route, params);
        AjaxResult result = JsonUtil.toBean(json, AjaxResult.class);
        if (result.code != ReturnCode.OK) {
            return null;
        }
        if(result.results instanceof JSONObject){
            JSONObject jsonObject = (JSONObject) result.results;
            return JsonUtil.toBean(jsonObject.toJSONString(), clazz);
        }
        return (T)result.results;
    }

    public static <T> List<T> fetchList(Class clazz, String route, Object... params) {
        String json = request(HttpMethod.GET, 5000, route, params);
        AjaxResult result = JsonUtil.toBean(json, AjaxResult.class);
        if (result.code != ReturnCode.OK) {
            return null;
        }
        JSONArray jsonArray = (JSONArray) result.results;
        return JsonUtil.toList(jsonArray.toJSONString(), clazz);
    }
    
    public static String GET(String route, Object... params) {
        return request(HttpMethod.GET, 5000, route, params);
    }
    
    public static String POST(String route, Object... params) {
        return request(HttpMethod.POST, 5000, route, params);
    }
    
    public static boolean postRequest(String route, Object... params) {
        String json = request(HttpMethod.POST, 10000, route, params);
        AjaxResult result = JsonUtil.toBean(json, AjaxResult.class);
        return (result.code != ReturnCode.OK);
    }
    
    private static String request(HttpMethod method, long timeoutMills, String route, Object... params) {
        String url = SEAVER_URI + MixHelper.format(route, params);
        //Debug
        MixHelper.print("-->{}", method.toString());
        MixHelper.print(url);
        WSRequest request = WS.url(url);
        try {
            Promise<HttpResponse> promise = null;
            if (method == HttpMethod.POST) {
                promise = request.postAsync();
            } else {
                promise = request.getAsync();
            }
            String json = promise.get(timeoutMills, TimeUnit.MILLISECONDS).getString();
            MixHelper.print("==>{}");
            MixHelper.print(json);
            return json;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        // 返回失败
        return JsonUtil.toJson(new AjaxResult(ReturnCode.FAIL, StringUtils.EMPTY, null));
    }
}
