package controllers;

import models.MemberChargeRecord;
import models.PayTradeLog;
import models.SellerPledgeRecord;
import models.SellerPledgeRecord.PledgeAction;
import models.Task;
import models.UserIngotRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.data.validation.Required;
import play.mvc.Util;

import com.aton.base.BaseController;
import com.aton.config.Config;
import com.aton.util.KQpayUtil;
import com.aton.util.MixHelper;
import com.aton.util.StringUtils;

import enums.pay.KQpayPlatform;
import enums.pay.PayPlatform;
import enums.pay.TradeResult;
import enums.pay.TradeType;

/**
 * 
 * 处理公开页面（不需要登录即可访问）.
 * 
 * @author youblade
 * @since 0.1
 * @created 2014年9月11日 下午5:36:19
 */
public class Seaport extends BaseController {

    public static final Logger log = LoggerFactory.getLogger(Seaport.class);

    /**
     * 
     * 接收来自支付网关的交易结果通知.
     *
     * @param merchantAcctId
     * @param version
     * @param language
     * @param signType
     * @param payType
     * @param bankId
     * @param orderId
     * @param orderTime
     * @param orderAmount
     * @param bindCard
     * @param bindMobile
     * @param dealId
     * @param bankDealId
     * @param dealTime
     * @param payAmount
     * @param fee
     * @param ext1
     * @param ext2
     * @param payResult
     * @param errCode
     * @param signMsg
     * @since  v1.0
     * @author youblade
     * @created 2014年11月14日 下午2:32:45
     */
    public static void payNotify(String merchantAcctId, String version, String language, String signType,
        String payType, String bankId, String orderId, String orderTime, long orderAmount, String bindCard,
        String bindMobile, String dealId, String bankDealId, String dealTime, long payAmount, long fee, String ext1,
        String ext2, String payResult, String errCode, @Required String signMsg) {
        handleWrongInput(false);

        // 检查该条交易记录是否已处理过
        PayTradeLog tradeLog = PayTradeLog.findByTradeNo(orderId);
        if (tradeLog == null || tradeLog.result != TradeResult.UNTREATED) {
            log.info("交易记录不存在或已处理过, trade_no={}", orderId);
            response(tradeLog.type, "支付失败", "交易记录[" + orderId + "]不存在或已处理过");
        }

        // 检查交易金额
        if (orderAmount < tradeLog.amount) {
            // TODO 发送通知给管理员
            log.error("Trade={} expected amount={},but was {}", new Object[] { tradeLog.id, tradeLog.amount,
                    orderAmount });
            response(tradeLog.type, "支付失败", "交易[" + orderId + "]金额不一致");
        }

        // 校验参数签名
        boolean isValid = KQpayUtil.verifySign(merchantAcctId, version, language, signType, payType, bankId, orderId,
            orderTime, orderAmount, bindCard, bindMobile, dealId, bankDealId, dealTime, payAmount, fee, ext1, ext2,
            payResult, errCode, signMsg);
        if (!isValid) {
            Object[] args = new Object[] { merchantAcctId, version, language, signType, payType, bankId, orderId,
                    orderTime, orderAmount, bindCard, bindMobile, dealId, bankDealId, dealTime, payAmount, fee, ext1,
                    ext2, payResult, errCode, signMsg };
            log.error(
                "签名校验失败：merchantAcctId={},version={},language={},signType,payType={},bankId={},orderId={},orderTime={},orderAmount={},bindCard,bindMobile={},dealId={},bankDealId={},dealTime={},payAmount={},fee={},ext1,ext2={},payResult={},errCode={},signMsg={}",
                args);
            forbidden("签名校验失败");
        }

        // 更新交易记录
        TradeResult result = payResult.equals("10") ? TradeResult.OK : TradeResult.FAIL;
        
        PayPlatform bank = null;
        if (StringUtils.isNotBlank(bankId)) {
            // 需要先转化为KQpayPlatform实例，再转换为PayPlatform实例
            bank = PayPlatform.buildFrom(KQpayPlatform.buildFromCode(bankId));
        }
        PayTradeLog.instance(tradeLog.id).dealId(dealId).bank(bank, bankDealId).fee(fee).result(result).save();

        // 支付成功的后续业务处理
        if (result == TradeResult.OK) {
            log.info("Payment notify OK and Process for payTradeLog={}", tradeLog.id);
            processBiz(tradeLog);
            response(tradeLog.type, null, null);
        }
        
        // 支付失败
        response(tradeLog.type, "支付失败", "[ErrCode]:" + errCode);
    }
    

    @Util
    public static void processBiz(PayTradeLog tradeLog) {
        switch (tradeLog.type) {
        case TASK:
            // 使用网银支付发布任务时，一个操作包含三个动作：账户押金充值、冻结任务押金、充值金币?
            Task task = Task.findById(tradeLog.bizId);
            log.info("Process notify for task={},totalPledge={},totalIngot={}", tradeLog.bizId, task.totalPledge,
                task.totalIngot);
            task.pay(task.totalIngot + task.totalPledge, false, false);
            break;
        case PLEDGE:
            // 账户充值押金
            log.info("Recharging user_pledge={} for tenpayTrade={}", new Object[] { tradeLog.amount, tradeLog.id });
            SellerPledgeRecord.newInstance(tradeLog.userId, null).action(PledgeAction.RECHARGE, tradeLog.amount)
                .memo("账户押金充值").create();
            break;
        case MEMBER:
            // 会员充值、续费
            log.info("Member amount={} for tenpayTrade={}", new Object[] { tradeLog.amount, tradeLog.id });
            MemberChargeRecord.newInstance(tradeLog.userId, tradeLog.bizMemberMonth).amount(tradeLog.amount).simpleCreate();
            break;
        case INGOT:
            // 充值购买金币
            log.info("Buying user_ingot={} for tenpayTrade={}", new Object[] { tradeLog.amount, tradeLog.id });
            UserIngotRecord.newInstance(tradeLog.userId, null).plus(tradeLog.amount).memo("购买金币").buy();
            break;
        default:
            break;
        }
    }


    /**
     * 
     * 交易回调：用户支付完成后“返回商铺”时的URL.
     * 
     * @param type
     * @since 0.1
     * @author youblade
     * @created 2014年9月11日 下午5:24:14
     */
    public static void response(@Required TradeType type, String title, String msg) {
        handleWrongInput(false);
        String redirectUrl = "";
        switch (type) {
        case TASK:
            redirectUrl = "seller/tasks";
            break;
        case PLEDGE:
            redirectUrl = "seller/tasks";
            break;
        case MEMBER:
            redirectUrl = "user";
            break;
        case INGOT:
            redirectUrl = "user";
            break;

        // 失败消息页面
        default:
            redirectUrl = MixHelper.format("result?title={}&msg={}", title, msg);
            break;
        }
        redirectUrl = Config.APP_URL + redirectUrl;
        // "1"表示业务处理成功
        renderText("<result>1</result><redirecturl>" + redirectUrl + "</redirecturl>");
    }
}