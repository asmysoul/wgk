package controllers;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import models.AdminOperatorLog;
import models.AdminTradeLog;
import models.AdminUser;
import models.BuyerDepositRecord;
import models.BuyerTask;
import models.FundAccount;
import models.MemberChargeRecord;
import models.PayTradeLog;
import models.SellerPledgeRecord;
import models.AdminOperatorLog.LogType;
import models.AdminTradeLog.AdminTradeType;
import models.SellerPledgeRecord.PledgeAction;
import models.User;
import models.User.UserType;
import models.UserFlowRecord;
import models.UserIngotRecord;
import models.UserWithdrawRecord;
import models.UserWithdrawRecord.WithdrawStatus;
import models.mappers.BuyerTaskMapper;
import models.mappers.SellerPledgeRecordMapper;
import models.mappers.UserFlowRecordMapper;
import models.mappers.UserIngotRecordMapper;
import models.mappers.UserWithdrawRecordMapper;
import net.sf.jxls.exception.ParsePropertyException;

import org.apache.ibatis.session.SqlSession;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.data.validation.Min;
import play.data.validation.Required;
import play.mvc.With;
import vos.BuyerDepositVo;
import vos.BuyerTaskVo;
import vos.OrderExpress;
import vos.Page;
import vos.TaskSearchVo;

import com.aton.base.BaseController;
import com.aton.base.secure.Secure;
import com.aton.config.AppMode;
import com.aton.config.BizConstants;
import com.aton.config.Config;
import com.aton.config.ReturnCode;
import com.aton.db.SessionFactory;
import com.aton.util.ExcelUtil;
import com.aton.util.KQpayUtil;
import com.aton.util.MixHelper;
import com.aton.util.NumberUtils;
import com.aton.util.StringUtils;
import com.google.common.base.Strings;

import enums.Sign;
import enums.TaskStatus;
import enums.pay.KQpayPlatform;
import enums.pay.PayPlatform;
import enums.pay.TradeType;

/**
 * 
 * 资金相关功能.
 * 
 * @author youblade
 * @since 0.1
 * @created 2014年8月25日 下午4:18:58
 */
@With(Secure.class)
public class MoneyManage extends BaseController {

	private static final Logger log = LoggerFactory
			.getLogger(MoneyManage.class);

	/**
	 * 
	 * 向第三方支付网关发请求.
	 * 
	 * @param platform
	 * @param id
	 * @since v0.2.4
	 * @author youblade
	 * @created 2014年10月31日 下午5:57:30
	 */
	public static void pay(@Required KQpayPlatform p, @Required @Min(1) long id) {
		handleWrongInput(false);

		// 根据ID查询交易记录
		PayTradeLog log = PayTradeLog.findById(id);
		if (log == null || log.userId.longValue() != getCurrentUser().id
				|| log.result != enums.pay.TradeResult.UNTREATED) {
			notFound();
		}

		// 1元钱测试
		if (AppMode.get().testPay) {
			log.amount(1);
		}

		// 调用支付平台接口
		String url = KQpayUtil.getPaymentUrl(p, log.tradeNo, log.amount);
		// MixHelper.print(url);
		redirect(url);
	}

	/**
	 * 
	 * 资金记录的类型.
	 * 
	 * @author youblade
	 * @since 0.1
	 * @created 2014年8月25日 下午4:38:34
	 */
	enum RecordType {
		/** 【商家】押金 */
		pledge,
		/** 金币 */
		ingot,
		/** 提现 */
		withdraw,
		/** 会员充值续费 */
		member,
		/** 【买手】佣金 */
		premium,
		/** 【买手】本金 */
		deposit,
		/** 流量 */
		flow
	}

	public static class MoneyRecordSearchVo extends Page {

		public Long userId;
		public RecordType type;
		public boolean isReward;

		public String userNick;
		public Long taskId;
		public Date createTimeStart;
		public Date createTimeEnd;

		public Long amountStart;
		public Long amountEnd;

		public Sign sign;
		
		public String fields;
		
		public static MoneyRecordSearchVo newInstance() {
			MoneyRecordSearchVo vo = new MoneyRecordSearchVo();
			return vo;
		}

		public MoneyRecordSearchVo userId(long userId) {
			this.userId = userId;
			return this;
		}
		public MoneyRecordSearchVo fields(String fields) {
			this.fields = fields;
			return this;
		}
	}

	/**
	 * 
	 * 分页获取资金相关记录.
	 * 
	 * @param type
	 * @since 0.1
	 * @author youblade
	 * @created 2014年8月25日 下午4:24:28
	 */
	public static void listRecord(@Required MoneyRecordSearchVo vo) {
		if (vo != null) {
			validation.required("vo.type", vo.type);
			validation.min("vo.pageNo", vo.pageNo, 1);
			validation.max("vo.pageNo", vo.pageNo, 100);

			// validation.min(vo.pageSize, 1);
			validation.max(vo.pageSize, 50);
		}
		handleWrongInput(false);

		vo.pageSize = Math.min(vo.pageSize, 20);
		vo.userId = getCurrentUser().id;
		Page p = findPageBySearchVo(vo);
		renderPageJson(p.items, p.totalCount);
	}

	/**
	 * 
	 * 【后台管理】-资金管理，查询列表.
	 * 
	 * @param vo
	 * @since 0.1
	 * @author youblade
	 * @created 2014年10月1日 上午11:29:14
	 */
	public static void listAll(@Required MoneyRecordSearchVo vo) {
		
		
		
		if (vo != null) {
			validation.required("vo.type", vo.type);
			validation.min("vo.pageNo", vo.pageNo, 1);
			validation.min(vo.pageSize, 1);
		}
		handleWrongInput(false);

		vo.userNick = StringUtils.trimToNull(vo.userNick);
		if (vo.amountEnd != null) {
			vo.amountEnd = vo.amountEnd * 100;
		}
		if (vo.amountStart != null) {
			vo.amountStart = vo.amountStart * 100;
		}
		Page p = findPageBySearchVo(vo);
		renderPageJson(p.items, p.totalCount);
	}

	private static Page findPageBySearchVo(MoneyRecordSearchVo vo) {
		switch (vo.type) {
		case member:
			return MemberChargeRecord.findByPage(vo);
		case pledge:
			return SellerPledgeRecord.findByPage(vo);
		case ingot:
			System.out.println("==================="+vo.toString());
			System.out.println("------------------"+vo.userId);
			return UserIngotRecord.findByPage(vo);
			
		case withdraw:
			return UserWithdrawRecord.findByPage(vo);
		case premium:
			return UserIngotRecord.findByPage(vo);
		case deposit:
			return BuyerDepositRecord.findByPage(vo);
		case flow:
			return UserFlowRecord.findByPage(vo);
		default:
			return Page.EMPTY;
		}
	}

	
	
	
	
	
	
	
	
	/**
	 * 
	 * 资金管理->充值：（卖家）账户押金、金币，（买手）金币.
	 * 
	 * @param amount
	 *            :单位是【元】
	 * @since 0.1
	 * @author youblade
	 * @created 2014年9月15日 下午1:23:54
	 */
	public static void recharge(@Required TradeType type, @Required long amount) {

		// 【卖家】最少500元，买手最少10元
		User user = getCurrentUser();
		if (user.type == UserType.SELLER && type == TradeType.PLEDGE) {
			validation.required(type);
			validation.min(amount, 1);
		} else {
			validation.min(amount, 1);
			type = TradeType.INGOT;
		}
		handleWrongInput(false);

		PayTradeLog log = PayTradeLog.newInstance(type, user.id).amount(
				amount * 100);
		log.save();
		renderJson(log.id);
	}

	/**
	 * 
	 * 资金管理-（卖家押金、买手佣金）提现申请.
	 * 
	 * @param amount
	 * @param payPass
	 * @since 0.1
	 * @author youblade
	 * @created 2014年9月15日 下午1:24:30
	 */
	public synchronized static  void applyWithdraw(@Required int amount,
			@Required String payPass) {
		// 最少100元
		validation.min("amount", amount, 100);
		handleWrongInput(false);

		User user = getCurrentUser();
		// 检查支付密码
		if (Strings.isNullOrEmpty(user.payPassword)) {
			renderFailedJson(ReturnCode.BIZ_LIMIT, "需要先设置支付密码~~");
		}
		if (!StringUtils.equals(payPass, user.payPassword)) {
			renderFailedJson(ReturnCode.BIZ_LIMIT, "支付密码不正确");
		}

		// 检查账户余额是否足够：卖家提现押金，买手提现金币
		long centAmount = amount * 100;
		if (user.type == UserType.SELLER) {
			validation.min(user.pledge, centAmount);
		} else {
			validation.min(user.ingot, centAmount);
		}
		handleWrongInput(false);

		/*
		 * 检查是否设置提现账号 1、卖家必须已绑定银行卡 2、买手必须已绑定银行卡 与 财付通
		 */
		FundAccount bank = FundAccount.findBank(user.id);
		if (user.isSeller() && bank == null) {
			renderFailedJson(ReturnCode.BIZ_LIMIT, "必须先绑定【银行卡】账号~~");
		}
		//目前提现不需要走财付通
//		if (user.isBuyer()) {
//			FundAccount tenpay = FundAccount.findByType(PayPlatform.TENPAY,
//					user.id);
//			if (bank == null || tenpay == null) {
//				renderFailedJson(ReturnCode.BIZ_LIMIT, "必须先绑定【银行卡】~~");
//			}
//		}

		// 检查本月已申请提现次数：申请次数不计入
		if (user.isBuyer()) {
			WithdrawSearchVo svo = WithdrawSearchVo.newInstance().userId(
					user.id);
			if (UserWithdrawRecord.countThisMonthApply(svo) >= 3) {
				renderFailedJson(ReturnCode.BIZ_LIMIT, "每个月最多申请提现3次~~");
			}
		}

		UserWithdrawRecord.newInstance(centAmount).createApply(user);
		renderSuccessJson();
	}

	/**
	 * 
	 * 资金管理-买手垫付本金提现申请
	 * 
	 * @param tids
	 *            ：买手本金提现时的相关任务ID
	 * @param payPass
	 * @since v1.7
	 * @author youblade
	 * @created 2014年11月29日 下午8:31:44
	 */
	public synchronized static void applyBuyerDepositWithdraw(@Required List<Long> tids,
			@Required String payPass) {
		handleWrongInput(false);

		User buyer = getCurrentUser();
		// 检查支付密码
		if (Strings.isNullOrEmpty(buyer.payPassword)) {
			renderFailedJson(ReturnCode.BIZ_LIMIT, "需要先设置支付密码~~");
		}
		if (!StringUtils.equals(payPass, buyer.payPassword)) {
			renderFailedJson(ReturnCode.BIZ_LIMIT, "支付密码不正确");
		}

		// 检查是否设置银行卡
		FundAccount bank = FundAccount.findBank(buyer.id);
		if (bank == null) {
			renderFailedJson(ReturnCode.BIZ_LIMIT, "需要先绑定银行卡~~");
		}

		// 提现金额后续使用任务垫付资金来计算得出
		UserWithdrawRecord.newInstance(0).createDepositApply(bank, tids);
		renderSuccessJson();
	}

	public static class WithdrawSearchVo extends Page {
		public Long id;
		public Long userId;
		// 1、【提现处理】用户昵称 2、【买手本金提现处理】买手昵称
		public String userNick;
		public UserType userType;

		public String sellerNick;
		public String buyerAccountNick;
		public Long taskId;
		public Long buyerTaskId;

		public String tradeNo;
		public WithdrawStatus status;

		public Long amount;
		public Long applyAmountStart;
		public Long applyAmountEnd;

		public Date applyTimeStart;
		public Date applyTimeEnd;

		public boolean isBuyerDeposit;

		public static WithdrawSearchVo newInstance() {
			return new WithdrawSearchVo();
		}

		public WithdrawSearchVo status(WithdrawStatus status) {
			this.status = status;
			return this;
		}

		public WithdrawSearchVo userId(long userId) {
			this.userId = userId;
			return this;
		}
	}

	/**
	 * 
	 * 【后台管理】取得待处理的“提现申请记录”列表
	 * 
	 * @param vo
	 * @since v0.1
	 * @author moloch
	 * @created 2014-10-3 下午12:54:07
	 */
	public static void listWithdrawRecord(@Required WithdrawSearchVo vo) {
		handleWrongInput(false);

		Page<UserWithdrawRecord> page = UserWithdrawRecord
				.findByPageForAdmin(vo);
		renderPageJson(page.items, page.totalCount);
	}

	/**
	 * 
	 * 【提现处理】申请记录列表->（买手提现垫付本金）查看相关任务
	 * 
	 * @param id
	 *            : 提现记录ID
	 * @since v1.7
	 * @author youblade
	 * @created 2014年11月29日 下午7:04:27
	 */
	public static void listBuyerDepositWithdrawTask(@Required @Min(1) long id) {
		handleWrongInput(false);

		TaskSearchVo vo = TaskSearchVo.newInstance().pageSize(30);

		List<BuyerTaskVo> list = null;
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			UserWithdrawRecord record = ss.getMapper(
					UserWithdrawRecordMapper.class).findById(id);
			vo.userWithdrawRecordSn = record.sn;
			list = ss.getMapper(BuyerTaskMapper.class)
					.selectListForBuyerDepositWithdraw(vo);
		} finally {
			ss.close();
		}
		renderJson(list);
	}

	/**
	 * 
	 * 查看用户提现申请所需的资金账户信息.
	 * 
	 * @param uid
	 * @since 0.1.7
	 * @author youblade
	 * @created 2014年10月17日 下午12:01:36
	 */
	public static void viewWithdrawFundAccount(@Required long uid) {
		handleWrongInput(false);
		List<FundAccount> list = FundAccount.findByUserId(uid);
		renderJson(list);
	}

	/**
	 * 
	 * 管理员确认提现
	 * 
	 * @param id
	 * @param tradeInfo
	 * @since v1.1.7
	 * @author youblade
	 * @created 2014年10月17日 下午1:15:31
	 */
	public static void confirmWithdraw(@Required long id,
			@Required String tradeInfo) {
		handleWrongInput(false);

		// 检查数据状态：只能处理“待处理”的记录
		UserWithdrawRecord record = UserWithdrawRecord.findById(id);
		if (record.status != WithdrawStatus.WAIT) {
			renderFailedJson(ReturnCode.WRONG_INPUT);
		}

		UserWithdrawRecord withdraw = new UserWithdrawRecord();
		withdraw.id = id;
		withdraw.status = WithdrawStatus.FINISHED;
		withdraw.tradeNo = tradeInfo;
		withdraw.save();
		renderSuccessJson();
	}

	/**
	 * 
	 * 【管理员】修正卖家的押金
	 * 
	 * @param nick
	 * @param yuan
	 *            :精确到 元
	 * @param btNo
	 * @since v0.2.1
	 * @author youblade
	 * @created 2014年10月27日 上午10:58:17
	 */
	public static void correct(@Required String nick, @Required TradeType type,
			@Required String yuan, long taskId, @Required String memo) {

		// 转换为分
		long amount = new BigDecimal(yuan).movePointRight(2).longValue();
		validation.min("amount", amount, 1);
		handleWrongInput(false);

		User user = User.findByNick(nick);
		notFoundIfNull(user);
		
		AdminUser adminAccount = AdminUser.findByIdWichCache(Long.valueOf(Long.parseLong(session.get("admin-authcode"))));
		AdminTradeLog adminTradeLog = new AdminTradeLog();
		adminTradeLog = adminTradeLog.sign(Sign.MINUS).adminId(adminAccount.id).amount(amount).userId(user.id);
		// 生成卖家押金扣款记录
		if (type == TradeType.PLEDGE) {
		    if(user.isSeller()){
			SellerPledgeRecord.newInstance(user.id, null).taskId(taskId)
					.action(PledgeAction.DEDUCT, amount).memo(memo)
					.createTime(new Date()).create();
 
            String message = MixHelper.format("商家{} 给商家减押金 金额 {}元", user.nick, (double)amount/100);
        	AdminOperatorLog.insert(adminAccount.name, LogType.SELLER_PLEDGE, message);
        	adminTradeLog = adminTradeLog.memo(memo).tradeType(AdminTradeType.BUYER_INGOT);
        	AdminTradeLog.insert(adminTradeLog);
		    }else {
                BuyerDepositRecord.deductDeposit(amount, user.id, memo);
                String message = MixHelper.format("买手{}减本金 金额 {}元", user.nick, (double)amount/100);
                AdminOperatorLog.insert(adminAccount.name, LogType.BUYER_DEPOSIT, message);
                adminTradeLog = adminTradeLog.memo(memo).tradeType(AdminTradeType.BUYER_INGOT);
                AdminTradeLog.insert(adminTradeLog);
            }
		}

		// 生成用户金币扣款记录
		if (type == TradeType.INGOT) {
			UserIngotRecord.newInstance(user.id, null).minus(amount).memo(memo)
					.createTime(new Date()).create();
			
        	if(user.type == UserType.BUYER) {	
            	String message = MixHelper.format("买手{}减金币 金额 {}元", user.nick, (double)amount/100);
            	AdminOperatorLog.insert(adminAccount.name, LogType.BUYER_INGOT, message);
            	adminTradeLog = adminTradeLog.memo(memo).tradeType(AdminTradeType.BUYER_INGOT);
            	AdminTradeLog.insert(adminTradeLog);
        	}else {
            	String message = MixHelper.format("商家{} 给商家减金币 金额 {}元", user.nick, (double)amount/100);
            	AdminOperatorLog.insert(adminAccount.name, LogType.SELLER_INGOT, message);
            	adminTradeLog = adminTradeLog.memo(memo).tradeType(AdminTradeType.BUYER_INGOT);
            	AdminTradeLog.insert(adminTradeLog);
        	}
		}

		if(type==TradeType.DEPOSIT){
			BuyerDepositRecord.newInstance(user.id, null).taskId(taskId).minus(amount).memo(memo).createTime(new Date()).create();
		}
		
		renderSuccessJson();
	}

	/**
	 * 
	 * (买手) 输出所有申请垫付本金返款（未完成转账部分）
	 * 
	 * @since v1.6
	 * @author playlaugh
	 * @created 2014年12月5日 下午6:10:37
	 */
	public static void exportAllUntradeBuyerDeposit(@Required boolean isBuyerDispose)
			throws ParsePropertyException, InvalidFormatException, IOException {
	    handleWrongInput(false);
		// 取出需要导出的数据
		List<BuyerDepositVo> buyerDeposit = BuyerTask.findUntradeBuyerDeposit(isBuyerDispose);
		if (MixHelper.isEmpty(buyerDeposit)) {
			renderSuccessJson();
		}
		for (BuyerDepositVo bt : buyerDeposit) {
			bt.parseApplyAmount(bt.applyAmount);
			bt.parseApplyTime(bt.applyTime);
				bt.province =StringUtils.replace(StringUtils.split(bt.address, ",")[0], "省", "");
				bt.city = StringUtils.replace(StringUtils.split(bt.address, ",")[1], "市", "");;
//				if(bt.city.contains("襄阳"))
//					bt.city="襄樊";
			// 更新状态，“处理中”
			UserWithdrawRecord record = UserWithdrawRecord.findById(bt.id);
			record.status = WithdrawStatus.PROCESSING;
			record.save();
		}

		// 设置render方式
		String date = DateTime.now().toString("yyyyMMdd");
		String fileName = "buyer_deposit_" + date + ".xls";
		response.contentType = "application/x-download";
		renderBinary(ExcelUtil.buildExportFile(
				Config.allUntradeBuyerDepositXlsTemplate, buyerDeposit),
				fileName);
	}

	/**
	 * 导入已处理垫付本金返款的数据
	 * 
	 * @param file
	 * @throws Exception
	 */
	public static void uploadAllUntradeBuyerDeposit(@Required File file)
			throws Exception {
		handleWrongInput(false);

		List<BuyerDepositVo> depositVos = ExcelUtil.parseExcelFileToBeans(file,
				Config.buyerDepositConfig);

		int count = 0;
		for (BuyerDepositVo bv : depositVos) {
			if (Strings.isNullOrEmpty(bv.memo)) {
				renderText("请检查该条数据：ID：" + bv.id + " ,备注为空！导入失败");
			}
			if (StringUtils.trim(bv.memo).length() > 50) {
				renderText("请检查该条数据：ID：" + bv.id + " ,备注不能超过50个字符！导入失败");
			}
			if (Strings.isNullOrEmpty(bv.tradeNo)) {
                renderText("请检查该条数据：ID：" + bv.id + " ,交易号为空！导入失败");
            }
			if (StringUtils.trim(bv.tradeNo).length() > 50) {
				renderText("请检查该条数据：ID：" + bv.id + " ,交易号不能超过50个字符！导入失败");
			}
			

			UserWithdrawRecord record = UserWithdrawRecord.findById(bv.id);

			if (record == null) {
				renderText("请检查该条数据：ID：" + bv.id + " ,该数据不存在！导入失败");
			}

			if (record.status != WithdrawStatus.PROCESSING) {
				renderText("请检查该条数据：ID：" + record.id + ",导入失败，该任务状态不符合条件");
			}
		}
		
		for (BuyerDepositVo buyerDepositVo : depositVos) {
			UserWithdrawRecord record = UserWithdrawRecord.findById(buyerDepositVo.id);
			UserWithdrawRecord withdraw = new UserWithdrawRecord();
			withdraw.id = record.id;
			withdraw.status = WithdrawStatus.FINISHED;
			withdraw.tradeNo = buyerDepositVo.tradeNo;
			withdraw.memo = StringUtils.trim(buyerDepositVo.memo);
			withdraw.save();
			// 统计导入成功数目
			count++;
		}
		renderText("导入成功，共" + count + "条");
		renderSuccessJson();
	}
	/**
	 * 
	 * 充值流量
	 *
	 * @param amount
	 * @since  v2.7
	 * @author fufei
	 * @created 2015年3月6日 上午11:15:12
	 */
	public synchronized static void rechargeFlow(@Required int amount, boolean useIngot, boolean usePledge) {
		handleWrongInput(false);
		// 至少选择一种支付方式
		validation.isTrue("useIngot or usePledge", useIngot || usePledge);
		validation.isTrue("amount",amount>0);
		User user = getCurrentUser();
		log.info("User={} pay for flow", user.id);
		if (user == null || !user.isSeller()) {
			renderFailedJson(ReturnCode.BIZ_LIMIT, "请先登录用户后再操作！");
		}
		if(!(useIngot||usePledge)){
			renderFailedJson(ReturnCode.BIZ_LIMIT, "至少选择一种支付方式！");
		}
		long payFee = amount * 100;
		boolean flag = useIngot && usePledge && (user.pledge.longValue() + user.ingot.longValue()) < payFee;
		boolean flag2 = useIngot && !usePledge && user.ingot.longValue() < payFee;
		boolean flag3 = usePledge && !useIngot && user.pledge.longValue() < payFee;
		if (flag || flag2 || flag3) {
			renderFailedJson(ReturnCode.BIZ_LIMIT, "您选择的支付方式，余额不足以支付本次任务的费用");
		}
		payFlow(user,payFee,useIngot,usePledge);
		renderSuccessJson();

	}
	
	/**
	 * 
	 * 流量支付
	 * 
	 * @since v2.7
	 * @author fufei
	 * @created 2015年3月9日 下午2:18:04
	 */
	public static void payFlow(User user, long amount, boolean useIngot, boolean usePledge) {
		Integer flow = BizConstants.SELLER_FLOW_FEE.get(amount);
		if (flow == null || flow <= 0) {
			renderFailedJson(ReturnCode.BIZ_LIMIT, "套餐选择错误！");
		}
		String memo = "";
		Date now = DateTime.now().toDate();
		SqlSession ss = SessionFactory.getSqlSessionWithoutAutoCommit();
		try {
			// 取出上一条金币记录
			UserIngotRecordMapper ingotMapper = ss.getMapper(UserIngotRecordMapper.class);
			UserIngotRecord lastIngot = ingotMapper.selectLastRecord(user.id);

			// 取出上一条押金记录
			SellerPledgeRecordMapper pledgeMapper = ss.getMapper(SellerPledgeRecordMapper.class);
			SellerPledgeRecord lastPledge = pledgeMapper.selectLastRecord(user.id);
			if(lastIngot==null&&lastPledge==null){
			    renderFailedJson(ReturnCode.BIZ_LIMIT, "充值失败！资金不足");
			}
			// 混合金币和押金支付
			if (useIngot && usePledge) {
			    if(lastIngot==null||lastPledge==null){
			        renderFailedJson(ReturnCode.BIZ_LIMIT, "充值失败！资金或押金不足！");
			    }
				UserFlowRecord.payFlowWithMultipleWay(user, ss, lastIngot, lastPledge, amount);
			} else if (usePledge&&!useIngot) {
				// 使用押金支付
			    if(lastPledge==null){
	                renderFailedJson(ReturnCode.BIZ_LIMIT, "充值失败！押金不足");
	            }
				memo = "购买流量套餐使用账户押金";
				lastPledge = SellerPledgeRecord.newInstance(user.id, lastPledge).action(PledgeAction.EXCHANGE_INGOT, amount).memo(memo)
						.createTime(now);
				pledgeMapper.insert(lastPledge);
				User.findByIdWichCache(user.id).ingot(lastIngot.balance).pledge(lastPledge.balance).updateCache();
			} else {
			    if(lastIngot==null){
                    renderFailedJson(ReturnCode.BIZ_LIMIT, "充值失败！金币不足");
                }
				memo = "购买流量套餐花费" + amount/100 + "金币";
				// 扣除金币
				lastIngot = UserIngotRecord.newInstance(user.id, lastIngot).minus(amount).memo(memo).createTime(now);
				ingotMapper.insert(lastIngot);
				User.findByIdWichCache(user.id).ingot(lastIngot.balance).pledge(lastPledge.balance).updateCache();
			}

			// 加入流量
			UserFlowRecordMapper userFlowRecordMapper = ss.getMapper(UserFlowRecordMapper.class);
			UserFlowRecord flowRecord = userFlowRecordMapper.selectLastRecord(user.id);
			memo = "购买流量套餐存入" + flow + "个流量";
			flowRecord = UserFlowRecord.newInstance(user.id, flowRecord).plus(flow).memo(memo).createTime(now);
			userFlowRecordMapper.insert(flowRecord);
			User.findByIdWichCache(user.id).flow(flowRecord.balance).updateCache();
			ss.commit();

		} catch (Exception e) {
		    log.info("flow recharge faild!");
			renderFailedJson(ReturnCode.BIZ_LIMIT, "充值失败！请联系管理员");
		} finally {
			ss.close();
		}

        renderSuccessJson();
	}
}
