package models;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

import jobs.TaskKeeper;
import models.SellerPledgeRecord.PledgeAction;
import models.User.UserType;
import models.mappers.BuyerAccountMapper3;
import models.mappers.BuyerExperienceRecordMapper;
import models.mappers.BuyerTaskMapper;
import models.mappers.BuyerTaskMapper3;
import models.mappers.BuyerTaskStepMapper3;
import models.mappers.FundAccountMapper;
import models.mappers.SellerPledgeRecordMapper;
import models.mappers.TaskMapper3;
import models.mappers.UserIngotRecordMapper;
import models.mappers.fund.BuyerDepositRecordMapper;
import models.marketing.TaskRewardLog;
import models.marketing.UserInvitedRecord;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.Validate;
import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vos.BuyerDepositVo;
import vos.BuyerTaskSearchVo3;
import vos.BuyerTaskStepVo;
import vos.BuyerTaskVo;
import vos.BuyerTaskVo3;
import vos.ExpressCountVo;
import vos.FaBaoGuoVo;
import vos.OrderExpress;
import vos.Page;
import vos.SellerBalanceVo;
import vos.TakeTaskCountVo;
import vos.TaskCountVo3;
import vos.TaskSearchVo3;
import vos.TaskSearchVo3.SearchModule;

import com.aton.config.BizConstants;
import com.aton.db.SessionFactory;
import com.aton.util.JsonUtil;
import com.aton.util.MixHelper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import domain.BuyerTaskProcessor3;
import domain.TaskStats3;
import enums.BuyerTaskStepType;
import enums.Device;
import enums.ExpressType;
import enums.Platform3;
import enums.Sign;
import enums.TaskStatus;
import enums.TaskType3;
import enums.pay.PayPlatform;

/**
 * 
 * 买手任务（子任务）
 * 
 * @author moloch
 * @since v0.1
 * @created 2014-7-31 下午4:44:29
 */
public class BuyerTask3 {
	
	private static final Logger log = LoggerFactory.getLogger(BuyerTask3.class);

	public static final String TABLE_NAME = "buyer_task3";
	public Long id;

	/** 父任务ID */
	public Long taskId;
	public Long sellerId;

	public Long buyerId;
	public Long buyerAccountId;
	public String orderId;
	public String expressNo;
	public String expressCompany;
	public Long paidFee;
	public String transNo;
	public Long messageId;

	/** 任务押金 */
	public Long pledgeIngot;
	/**
	 * 任务最终佣金：<br>
	 * 1、包含加赏部分，接手时写入 2、即使后续父任务有所变化也不再修改，故要求用户修改【发布后】的父任务时不能降低费用
	 */
	public Long rewardIngot;
	/** 任务经验值 */
	public Integer experience;

	/** 任务状态 */
	public TaskStatus status;
	public Device device;

	/**
	 * 接手任务的时间（同时也是记录的创建时间）
	 */
	public Date takeTime;
	public Date startTime;
	public Date finishTime;
	public Date modifyTime;

	public String buyerAccountNick;
	/**
	 * 提现记录流水号：本金提现时使用
	 */
	public Long userWithdrawRecordSn;
	//搜索方案id
	public long searchPlanId;

	/*
	 * 页面展示用字段
	 */
	@Transient
	public FundAccount refundAccount;
	@Transient
	public String statusTitle;

	/*
	 * 批量更新用
	 */
	public List<Long> ids;

	/**
	 * 未完成任务的上一步进度
	 */
	@Transient
	public BuyerTaskStepType lastStep;
	/**
	 * 未完成任务下一步要进行的步骤
	 */
	@Transient
	public BuyerTaskStepType nextStep;

	@Transient
	public String buyerNick;
	/**
	 * 格式化之后的退款金额：单位为“元”
	 */
	@Transient
	public String refundAmt;

	public BuyerTask3() {
	}

	/**
	 * 
	 * Constructs a <code>BuyerTask</code>
	 * 
	 * @since v0.1
	 */
	public BuyerTask3(OrderExpress order) {
		String[] num = order.number.split("-");
		this.id = Long.parseLong(num[1]);
		this.taskId = Long.parseLong(num[0]);
		this.expressNo = order.expressNo;
		this.orderId = order.orderId;
	}

	/**
	 * 
	 * 创建买手任务，【买手】领取任务.
	 * 
	 * @since 0.1
	 * @author youblade
	 * @created 2014年8月11日 下午4:30:41
	 */
	public void create() {
		Validate.notNull(this.device, "BuyerTask3.device must not be null");

		SqlSession ss = SessionFactory.getSqlSessionWithoutAutoCommit();
		try {
			BuyerTaskMapper3 mapper = ss.getMapper(BuyerTaskMapper3.class);
            
            // 再次检查是否重复领取
            boolean isTaken = _checkBuyerTaskExist(buyerAccountId, taskId, mapper);
            if (isTaken) {
                log.error("使用买号={}重复领取任务={}，已被拦截", buyerAccountId, taskId);
                return;
            }
			this.takeTime = DateTime.now().toDate();
			this.modifyTime = this.takeTime;
			
			System.out.println("----rewardIngot---------"+this.rewardIngot);
			
			System.out.println("----rewardIngot---------"+this.rewardIngot);
			
			System.out.println("----rewardIngot---------"+this.rewardIngot);
			
			
			System.out.println("----rewardIngot---------"+this.rewardIngot);
			
			System.out.println("----rewardIngot---------"+this.rewardIngot);
			
			mapper.insert(this);

			/*
			 * 更新父任务
			 */
			TaskMapper3 taskMapper = ss.getMapper(TaskMapper3.class);
			Task3 task = taskMapper.selectFieldsById("pc_order_num,mobile_order_num,pc_taken_count,mobile_taken_count,"
					+ "publish_timer_value,publish_timer_interval,last_batch_publish_time," + "total_order_num", this.taskId);

			// 累加已接手任务数
			Task3 taskToSave = Task3.instance(this.taskId).modifyTime(DateTime.now().toDate());
			if (this.device == Device.PC) {
				task.pcTakenCount += 1;
				taskToSave.pcTakenCount = task.pcTakenCount;
			} else {
				task.mobileTakenCount += 1;
				taskToSave.mobileTakenCount = task.mobileTakenCount;
			}

			// 再次检查已被领取的任务数量
			if (task.pcTakenCount > task.pcOrderNum || task.mobileTakenCount > task.mobileOrderNum) {
			    log.error("Task={} pcTakenCount={} and mobileTakenCount={} greatter than totalOrderNum={}", new Object[] { this.taskId,
			        task.pcTakenCount, task.mobileTakenCount, task.totalOrderNum });
			    throw new RuntimeException("该任务已全部被领完~~");
			}
			
			// 再次检查已被领取的任务数量
			TaskSearchVo3 svo = TaskSearchVo3.newInstance().taskId(taskId).statusExclude(TaskStatus.CANCLED);
			int pcTaskCount = mapper.count(svo.device(Device.PC));
			int mobileTaskCount = mapper.count(svo.device(Device.MOBILE));
            if ((task.pcOrderNum > 0 && pcTaskCount > task.pcOrderNum)
                || (task.mobileOrderNum > 0 && mobileTaskCount > task.mobileOrderNum)) {
                log.error("Task={} pcTakenCount={} and mobileTakenCount={} greatter than totalOrderNum={}",
                    new Object[] { this.taskId, task.pcTakenCount, task.mobileTakenCount, task.totalOrderNum });
                throw new RuntimeException("该任务已全部被领完~~");
            }
			
			
			if ((task.pcOrderNum>0&&task.pcTakenCount > task.pcOrderNum) && (task.mobileTakenCount > task.mobileOrderNum&&task.mobileOrderNum>0)) {
			    log.error("Task={} pcTakenCount={} and mobileTakenCount={} greatter than totalOrderNum={}", new Object[] { this.taskId,
			        task.pcTakenCount, task.mobileTakenCount, task.totalOrderNum });
			    throw new RuntimeException("该任务已全部被领完~~");
			}

			// 如果是定时任务，检查是否需要放回任务池
			if (task.checkIsTimerTask() && task.checkIsCurrentGroupTasksTakenOver()) {
				TaskKeeper.log.info("Put Task={} into pool, after buyer taken buyerTask={}", this.taskId, this.id);
				taskToSave.setStatus(TaskStatus.WAIT_PUBLISH);
			}
			taskMapper.updateById(taskToSave);

			/*
			 * 扣除做任务押金（待任务完成时再返还）
			 */
			String memo = MixHelper.format("任务编号：{} 已接手，扣除押金", this.taskId);
			UserIngotRecordMapper ingotMapper = ss.getMapper(UserIngotRecordMapper.class);
			// 获取上一次的金币记录（用于计算结余）
			UserIngotRecord lastRecord = ingotMapper.selectLastRecord(this.buyerId);
			UserIngotRecord record = UserIngotRecord.newInstance(this.buyerId, lastRecord).minus(BizConstants.TASK_TAKING_INGOT * 100).memo(memo)
					.taskId(this.taskId);
			record.createTime = this.modifyTime;
			ingotMapper.insert(record);

			// 更新用户当前金币余额
			User.findByIdWichCache(this.buyerId).ingot(record.balance).updateCache();

			// 更新“任务列表(X)”中的任务数量
			Task3.updateTaskCount(Sign.MINUS, 1);

			// 更新买手接手任务统计的缓存
			TaskStats3.updateTaskTakenCount(this.buyerAccountId, Sign.PLUS);

			ss.commit();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			ss.rollback();
			throw new RuntimeException("接手任务失败~~");
		} finally {
			ss.close();
		}
	}

	/**
	 * 
	 * 【测试用】更新买手任务.
	 * 
	 * @since 0.1
	 * @author youblade
	 * @created 2014年8月20日 下午5:38:27
	 */
	public void save() {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerTaskMapper3 mapper = ss.getMapper(BuyerTaskMapper3.class);
			mapper.updateById(this);
		} finally {
			ss.close();
		}
	}

	
	/**
	 * 查询指定的【买号】是否符合性别.
	 */
	public static boolean isSexTaking(long buyerAccountId, long taskId) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerTaskMapper3 btMapper = ss.getMapper(BuyerTaskMapper3.class);
			BuyerAccountMapper3 mapper=ss.getMapper(BuyerAccountMapper3.class);
			BuyerAccount3 account=mapper.selectById(buyerAccountId);
			System.out.println("=================="+account.gender);

			TaskMapper3 mapper2=ss.getMapper(TaskMapper3.class);
			Task3 task=mapper2.selectById(taskId);
			System.out.println("================="+task.genderConfig);
			if(account.gender.equals(task.genderConfig)){
				return true;
			}else{
				if(task.genderConfig==null ||task.genderConfig.equals(null)){
					return true;
				}else{
					return false;
				}
			}
			
        } finally {
            ss.close();
        }
    }
	/**
	 * 查询指定的【买号】是否符合地区限制.
	 */
	public static boolean isZoneTaking(long buyerAccountId, long taskId) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerTaskMapper3 btMapper = ss.getMapper(BuyerTaskMapper3.class);
			BuyerAccountMapper3 mapper=ss.getMapper(BuyerAccountMapper3.class);
			BuyerAccount3 account=mapper.selectById(buyerAccountId);

			TaskMapper3 mapper2=ss.getMapper(TaskMapper3.class);
			Task3 task=mapper2.selectById(taskId);
			String state=account.state.substring(0, 2);
			System.out.println("--------------"+state);
			System.out.println("--------------"+state);
			System.out.println("--------------"+state);
			System.out.println("--------------"+state);
			System.out.println("--------------"+state);
			System.out.println("===========task=========="+account.state);
			System.out.println("===========task=========="+account.state);
			System.out.println("===========task=========="+account.state);
			System.out.println("===========task=========="+account.state);
			System.out.println("=======buyerLocationConfig==========:"+task.buyerLocationConfig +"-----");
			System.out.println("=======buyerLocationConfig==========:"+task.buyerLocationConfig +"-----");
			System.out.println("=======buyerLocationConfig==========:"+task.buyerLocationConfig +"-----");
			System.out.println("=======buyerLocationConfig==========:"+task.buyerLocationConfig +"-----");
			
			
			if(state.equals(task.buyerLocationConfig)||task.buyerLocationConfig.contains(state)){
				return true;
			}else{
				if("".equals(task.buyerLocationConfig.trim())){
					return true;
				}else{
					return false;
				}
			}
			
        } 
		finally {
            ss.close();
        }
    }
	
	
	
	
	
	
	
	
	
	
	/**
	 * 
	 * 查询指定的【买号】是否已接手过该任务.
	 * 
	 * @param buyerAccountId
	 * @param taskId
	 * @return
	 * @since 0.1
	 * @author youblade
	 * @created 2014年9月24日 下午3:15:00
	 */
	public static boolean isDuplicateTaking(long buyerAccountId, long taskId) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerTaskMapper3 btMapper = ss.getMapper(BuyerTaskMapper3.class);
            return _checkBuyerTaskExist(buyerAccountId, taskId, btMapper);
        } finally {
            ss.close();
        }
    }

    private static boolean _checkBuyerTaskExist(long buyerAccountId, long taskId, BuyerTaskMapper3 btMapper) {
        TaskSearchVo3 vo = TaskSearchVo3.newInstance().status(TaskStatus.CANCLED).taskId(taskId);
        vo.buyerAccountId = buyerAccountId;
        return btMapper.selectForCheckTaking(vo) != null;
    }

	/**
	 * 
	 * 查询某个买家是否有在执行的任务
	 * 
	 * @param vo
	 * @return
	 * @since v0.1
	 * @author moloch
	 * @created 2014-7-31 下午4:50:53
	 */
	public static boolean hasExecuteTask(BuyerTaskSearchVo3 vo) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerTaskMapper3 btMapper = ss.getMapper(BuyerTaskMapper3.class);
			return btMapper.countExecute(vo) == null ? false : true;
		} finally {
			ss.close();
		}
	}

	/**
	 * 
	 * 判断买号是否有接手任务
	 * 
	 * @param vo
	 * @return
	 * @since v0.1
	 * @author moloch
	 * @created 2014-8-6 上午10:24:29
	 */
	public static boolean isTakeTask(BuyerTaskSearchVo3 vo) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerTaskMapper3 btMapper = ss.getMapper(BuyerTaskMapper3.class);
			return btMapper.hasTask(vo) == null ? false : true;
		} finally {
			ss.close();
		}
	}

	/**
	 * 
	 * 获取待处理任务的统计信息.
	 * 
	 * @param buyerId
	 * @since 0.1
	 * @author youblade
	 * @created 2014年8月20日 上午11:51:54
	 */
	public static Multimap<String, TaskCountVo3> findWaitingTaskCountInfo(long buyerId) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerTaskMapper3 mapper = ss.getMapper(BuyerTaskMapper3.class);
			Multimap<String, TaskCountVo3> map = ArrayListMultimap.create();
			// 统计
			for (TaskCountVo3 taskCountVo : mapper.countWaitingTasksByBuyerId(buyerId)) {
				if (taskCountVo.status == TaskStatus.RECIEVED) {
					map.put(TaskStatus.WAIT_PAY.name(), taskCountVo);
				} else {
					map.put(taskCountVo.status.toString(), taskCountVo);
				}
			}

			List<TaskCountVo3> vos = (List<TaskCountVo3>) map.get(TaskStatus.WAIT_PAY.name());
			if (MixHelper.isEmpty(vos)) {
				return map;
			}

			// WAIT_PAY, RECIEVED 用一个key标记（recieved）
			TaskCountVo3 tmall = new TaskCountVo3(Platform3.TMALL, TaskStatus.WAIT_PAY);
			TaskCountVo3 taobao = new TaskCountVo3(Platform3.TAOBAO, TaskStatus.WAIT_PAY);
			List<TaskCountVo3> tvos = Lists.newArrayList();
			for (TaskCountVo3 vo : vos) {
				if (vo.platform == Platform3.TAOBAO) {
					taobao.count = taobao.count + vo.count;
				} else {
					tmall.count = tmall.count + vo.count;
				}
			}
			tvos.add(taobao);
			tvos.add(tmall);
			map.putAll("recieved", tvos);

			return map;
		} finally {
			ss.close();
		}
	}
	
	
	/**
	 * 
	 * 获取买手任务.
	 * 
	 * @param vo
	 * @return
	 * @since 0.1
	 * @author youblade
	 * @created 2014年8月7日 下午6:02:15
	 */
	public static Page<BuyerTaskVo3> findListByPage(TaskSearchVo3 vo) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerTaskMapper3 mapper = ss.getMapper(BuyerTaskMapper3.class);
			int totalCount;
			if (vo.module == SearchModule.SELLER_REFUND) {
				totalCount = mapper.count(vo);
			} else if (vo.module != null) {
				// 提现时仅查询“已完成”且“未提现”的任务记录
				if (vo.module == SearchModule.SYS_REFUND_WITHDRAW) {
					vo.status(TaskStatus.FINISHED);
				}
				totalCount = mapper.sysRefundCount(vo);
			} else {
				totalCount = mapper.count(vo);
			}

			if (totalCount <= 0) {
				return Page.EMPTY;
			}

			Page<BuyerTaskVo3> page = Page.newInstance(vo.pageNo, vo.pageNo, totalCount);
			List<BuyerTaskVo3> vos = null;
			// 获取商家退款所需的数据
			if (vo.module == SearchModule.SELLER_REFUND) {
				vos = mapper.selectListForSellerRefund(vo);
			} else if (vo.module != null) {
				vos = mapper.selectListForSysRefund(vo);
			} else {
				vos = mapper.selectList(vo);
			}

			// 赋值taskIdStr 并解析付款截图URL
			for (BuyerTaskVo3 btVo : vos) {
				btVo.value();
				if (btVo.itemPicUrl != null && btVo.itemPicUrl.startsWith("{")) {
					BuyerTaskStepVo btSvo = JsonUtil.toBean(btVo.itemPicUrl, BuyerTaskStepVo.class);
					btVo.itemPicUrl = btSvo.picUrls.get(0);
				}
			}
			page.items = vos;
			return page;
		} finally {
			ss.close();
		}
	}

	/**
	 * 
	 * 获取任务订单列表（待打印快递单状态）
	 * 
	 * @param vo
	 * @return
	 * @since v0.1
	 * @author moloch
	 * @created 2014-8-13 下午3:35:06
	 */
	public static Page<OrderExpress> findOrdersByPage(TaskSearchVo3 vo) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerTaskMapper3 btMapper = ss.getMapper(BuyerTaskMapper3.class);
			int count = btMapper.selectYDOrderCount(vo);
			if (count <= 0) {
				return Page.EMPTY;
			}
			Page<OrderExpress> page = Page.newInstance(vo.pageNo, vo.pageSize, count);
			List<OrderExpress> orders = btMapper.selectYDOrders(vo);
			// 赋值number、fullAddress字段
			for (OrderExpress oe : orders) {
				oe.resolve();
			}
			page.items = orders;
			return page;
		} finally {
			ss.close();
		}
	}

	/**
	 * 
	 * 获取买手“待确认收货”的任务.
	 * 
	 * @param vo
	 * @return
	 * @since 0.1
	 * @author youblade
	 * @created 2014年8月21日 下午1:33:58
	 */
	public static List<BuyerTaskVo3> findListForConfirm(TaskSearchVo3 vo) {
		Validate.notNull(vo.buyerId);
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerTaskMapper3 mapper = ss.getMapper(BuyerTaskMapper3.class);
			return mapper.selectListForBuyerConfirm(vo);
		} finally {
			ss.close();
		}
	}

	/**
	 * 
	 * 取得订单List 导出订单用
	 * 
	 * @param vo
	 * @return
	 * @since v0.1
	 * @author moloch
	 * @created 2014-8-13 下午6:07:43
	 */
	public static List<OrderExpress> findOrders(TaskSearchVo3 vo) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerTaskMapper3 btMapper = ss.getMapper(BuyerTaskMapper3.class);
			
			List<OrderExpress> orders = btMapper.selectYDOrders(vo);

			// 赋值number、fullAddress字段
			for (OrderExpress oe : orders) {
				oe.resolve();
			}
			return orders;
		} finally {
			ss.close();
		}
	}

	/**
	 * 
	 * 获取子任务列表，用于【卖家/管理员】查看任务详情时展示子任务.
	 * 
	 * @param vo
	 * @return
	 * @since 0.1
	 * @author youblade
	 * @created 2014年8月11日 下午3:41:43
	 */
	public static Page<BuyerTask3> findByPageForSellerTask(TaskSearchVo3 vo) {
		Validate.notNull(vo.taskId);

		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerTaskMapper3 mapper = ss.getMapper(BuyerTaskMapper3.class);
			int totalCount = mapper.countForTaskDetail(vo);
			if (totalCount <= 0) {
				return Page.EMPTY;
			}

			Page<BuyerTask3> page = Page.newInstance(vo.pageNo, vo.pageSize, totalCount);

			// 赋值statusTitle字段 方便显示状态信息
			List<BuyerTask3> list = mapper.selectListForTaskDetail(vo);
			for (BuyerTask3 bt : list) {
				bt.statusTitle = bt.status.title;
			}
			page.items = list;
			return page;
		} finally {
			ss.close();
		}
	}

	/**
	 * 
	 * 更新任务
	 * 
	 * @param bt
	 * @since v0.1
	 * @author moloch
	 * @created 2014-8-12 上午10:32:47
	 */
	public static Integer modify(BuyerTask3 bt) {
		Validate.notNull(bt.id);

		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerTaskMapper3 btMapper = ss.getMapper(BuyerTaskMapper3.class);
			bt.modifyTime = DateTime.now().toDate();
			return btMapper.update(bt);
		} finally {
			ss.close();
		}
	}
	
	/**
	 * 
	 *更新韵达快递获取的运单号
	 *
	 * @param bt
	 * @return
	 * @since  v3.4
	 * @author fufei
	 * @created 2015年5月4日 下午4:22:02
	 */
    public static Integer modifyYDKD(BuyerTask3 bt) {
        Validate.notNull(bt.id);
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            BuyerTaskMapper3 btMapper = ss.getMapper(BuyerTaskMapper3.class);
            bt.modifyTime = DateTime.now().toDate();
            return btMapper.modifyYDKD(bt);
        } finally {
            ss.close();
        }
    }

	/**
	 * 
	 * 【买手】确认收货并好评.
	 * 
	 * @since 0.1.1
	 * @author youblade
	 * @created 2014年9月30日 下午6:33:00
	 */
	public void confirmGoodsAndRate(BuyerTaskStepVo stepVo) {
		Validate.notNull(stepVo);
		Validate.notNull(this.id);
		Validate.notNull(this.buyerId);
		Validate.notNull(this.taskId);

		SqlSession ss = SessionFactory.getSqlSessionWithoutAutoCommit();
		try {
			// 保存任务进度
			BuyerTaskStep3 taskStep = new BuyerTaskStep3();
			taskStep.buyerId = this.buyerId;
			taskStep.buyerTaskId = this.id;
			taskStep.type = BuyerTaskStepType.CONFIRM_AND_COMMENT;
			taskStep.no = taskStep.type.getOrder();
			taskStep.content = JsonUtil.toJson(stepVo);

			Date now = DateTime.now().toDate();
			taskStep.createTime = now;
			ss.getMapper(BuyerTaskStepMapper3.class).insert(taskStep);

			// 更新任务状态：默认为“待退款”，若为【平台返款】任务则为“待卖家确认平台返款金额”
			BuyerTask3 buyerTask = BuyerTask3.instance(this.id).status(TaskStatus.WAIT_REFUND).modifyTime(now);
			Task3 task = ss.getMapper(TaskMapper3.class).selectFieldsById("sys_refund", this.taskId);
			if (task.sysRefund) {
				buyerTask.status(TaskStatus.WAIT_SELLER_CONFIRM_SYS_REFUND);
			}

			ss.getMapper(BuyerTaskMapper3.class).updateById(buyerTask);

			ss.commit();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			ss.rollback();
			throw new RuntimeException("确认收货操作失败~~");
		} finally {
			ss.close();
		}
	}

	/**
	 * 
	 * 核实退款并记录经验值
	 * 
	 * @since v0.1
	 * @author moloch & youblade
	 * @created 2014-9-1 下午4:17:52
	 */
	public void confirmSellerRefund() {
		Validate.notNull(this.id);
		Validate.notNull(this.buyerId);

		SqlSession ss = SessionFactory.getSqlSessionWithoutAutoCommit();
		try {
			Date now = DateTime.now().toDate();
			User userCache = processRefund(ss, now, 0);

			/*
			 * 更新用户缓存数据，实时显示押金、经验值的变化
			 */
			userCache.updateCache();

			ss.commit();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			ss.rollback();
			throw new RuntimeException("核实退款操作失败~~");
		} finally {
			ss.close();
		}
	}

	private User processRefund(SqlSession ss, Date now, long paidFee) {
		/*
		 * 更新买手任务状态
		 */
		BuyerTaskMapper3 mapper = ss.getMapper(BuyerTaskMapper3.class);
		BuyerTask3 buyerTask = BuyerTask3.instance(this.id).buyerId(this.buyerId).status(TaskStatus.FINISHED);
		buyerTask.modifyTime = now;
		mapper.updateByIdAndBuyerId(buyerTask);
		UserIngotRecordMapper userIngotmapper = ss.getMapper(UserIngotRecordMapper.class);
		
		/*
		 * 如果次买手是被邀请账户，则发放奖励金币到邀请人，记入邀请记录表和金币表，更新缓存 
		 */
		if (User.isInvited(this.buyerId) && BizConstants.IS_BUYER_TASK_REWARD) {
			UserInvitedRecord invitedRecord = UserInvitedRecord.findMyInvitedRecord(this.buyerId);
			User inviter = User.findById(invitedRecord.inviteUserId);
			UserIngotRecord inviterLastRecord = userIngotmapper.selectLastRecord(inviter.id);
			BigDecimal fee = new BigDecimal(this.paidFee);
			BigDecimal rewardFee = null;
			if(0<=fee.intValue() && fee.intValue()<=5000) {
				rewardFee = new BigDecimal(30);
			}else if(5000<fee.intValue() && fee.intValue()<=15000){
				rewardFee = new BigDecimal(40);
			}else if(15000<fee.intValue() && fee.intValue()<=25000){
				rewardFee = new BigDecimal(50);
			}else if(25000<fee.intValue() && fee.intValue()<=35000){
				rewardFee = new BigDecimal(60);
			}else if(fee.intValue()>=35000){
				rewardFee = new BigDecimal(70);
			}
			String memo = "您邀请的买手" + User.findById(this.buyerId).nick + "完成一次任务，奖励您" + rewardFee.movePointLeft(2) + "金币";
			
			//将记录插入奖励记录表中
			if(rewardFee.longValue()!=0){
				TaskRewardLog rewardLog = new TaskRewardLog();
				rewardLog.inviteUserId = invitedRecord.inviteUserId;
				rewardLog.userId = this.buyerId;
				rewardLog.taskId = this.taskId;
				rewardLog.taskFinishedTime = buyerTask.modifyTime;
				rewardLog.memo = memo;
				rewardLog.rewardIngot = rewardFee;
				rewardLog.insert(ss, rewardLog);
			}
			
			
			
			UserIngotRecord rewardIngot = UserIngotRecord.newInstance(inviter.id, inviterLastRecord).plus(rewardFee.longValue()).createTime(new Date()).memo(memo);
			userIngotmapper.insert(rewardIngot);
			// 更新用户缓存
			User.findByIdWichCache(inviter.id).ingot(rewardIngot.balance).updateCache();
		}
		
		/*
		 * 如果商家账户是被邀请账户，则奖励邀请人对应的邀请奖励
		 */
		if (User.isInvited(this.sellerId) && BizConstants.IS_SELLER_TASK_REWARD) {
			UserInvitedRecord invitedRecord = UserInvitedRecord.findMyInvitedRecord(this.sellerId);
			User inviter = User.findById(invitedRecord.inviteUserId);
			UserIngotRecord inviterLastRecord = userIngotmapper.selectLastRecord(inviter.id);
			BigDecimal fee = new BigDecimal(this.paidFee);
			BigDecimal rewardFee = null;
			if(0<=fee.intValue() && fee.intValue()<=5000) {
				rewardFee = new BigDecimal(15);
			}else if(5000<fee.intValue() && fee.intValue()<=15000){
				rewardFee = new BigDecimal(20);
			}else if(15000<fee.intValue() && fee.intValue()<=25000){
				rewardFee = new BigDecimal(25);
			}else if(25000<fee.intValue() && fee.intValue()<=35000){
				rewardFee = new BigDecimal(30);
			}else if(fee.intValue()>=35000){
				rewardFee = new BigDecimal(35);
			}
			String memo = "您邀请的商家" + User.findById(this.sellerId).nick + "发布的子任务已经完成，奖励您" + rewardFee.movePointLeft(2) + "金币";
			//将记录插入奖励记录表中
			if(rewardFee.longValue()!=0) {
				TaskRewardLog rewardLog = new TaskRewardLog();
				rewardLog.inviteUserId = invitedRecord.inviteUserId;
				rewardLog.userId = this.sellerId;
				rewardLog.taskId = this.taskId;
				rewardLog.taskFinishedTime = buyerTask.modifyTime;
				rewardLog.memo = memo;
				rewardLog.rewardIngot = rewardFee;
				rewardLog.insert(ss, rewardLog);
			}
			
			
			
			UserIngotRecord rewardIngot = UserIngotRecord.newInstance(inviter.id, inviterLastRecord).plus(rewardFee.longValue()).createTime(new Date()).memo(memo);
			userIngotmapper.insert(rewardIngot);
			// 更新用户缓存
			User.findByIdWichCache(inviter.id).ingot(rewardIngot.balance).updateCache();
		}
		
		/*
		 * 发放本次任务的经验值
		 */
		long newexperience = 0;
		if (this.experience > 0) {
			BuyerExperienceRecordMapper experienceMapper = ss.getMapper(BuyerExperienceRecordMapper.class);
			// 获取上一次的记录计算结余
			BuyerExperienceRecord lastRecord = experienceMapper.selectLastRecord(this.buyerId);
			BuyerExperienceRecord record = BuyerExperienceRecord.newInstance(this.buyerId, lastRecord).plus(this.experience);
			record.createTime = now;
			experienceMapper.insert(record);

			newexperience = record.balance;
		}

		/*
		 * 退还接手任务时的押金金币
		 */
		String memo = MixHelper.format("任务编号：{} 已完成，返还押金", this.getTaskNo());
		UserIngotRecordMapper ingotMapper = ss.getMapper(UserIngotRecordMapper.class);
		// 获取上一次的金币记录计算结余
		UserIngotRecord lastRecord = ingotMapper.selectLastRecord(this.buyerId);
		//获取此任务的最后一次的金币扣除（接任务的时候扣除的任务押金）
		int lastIngotRecord = ingotMapper.selectLastRecordAboutThisRecord(this.buyerId, this.taskId);
		UserIngotRecord record = UserIngotRecord.newInstance(this.buyerId, lastRecord).plus(lastIngotRecord).memo(memo)
				.taskId(this.taskId);
		record.createTime = now;
		ingotMapper.insert(record);

		/*
		 * 发放佣金
		 */
		memo = MixHelper.format("任务编号：{} 已完成，获得佣金", this.getTaskNo());
		// 获取上一次的金币记录（用于计算结余）
		UserIngotRecord record2 = UserIngotRecord.newInstance(this.buyerId, record).plus(this.rewardIngot).memo(memo).isReward(true)
				.taskId(this.taskId);
		record2.createTime = now;
		ingotMapper.insert(record2);

		/*
		 * 更新买手用户缓存，实时显示押金、经验值的变化
		 */
		User buyerCache = User.findByIdWichCache(this.buyerId);
		buyerCache.experience = newexperience;
		buyerCache.ingot = record2.balance;

		/*
		 * 更新父任务“已完成任务数”，解冻押金
		 */
		TaskMapper3 taskMapper = ss.getMapper(TaskMapper3.class);
		Task3 t = taskMapper.selectFieldsById(
				"id,seller_id,pledge,finished_count,total_order_num,status,sys_refund,is_free_shipping,item_price,item_buy_num", taskId);

		/*
		 * 处理任务押金
		 */
		long pledgeBalance = 0;
		SellerPledgeRecordMapper pledgeMapper = ss.getMapper(SellerPledgeRecordMapper.class);
		SellerPledgeRecord lastPledge = pledgeMapper.selectLastRecord(t.sellerId);

		// 平台返款任务：解冻5%的退款保证金+非包邮商品的运费押金+商家确认退款金额与品单价*数量的差额，生成扣款押金记录，并标记对应的押金冻结记录为“已扣款”
		if (BooleanUtils.isTrue(t.sysRefund)) {
			long itemPerFee = t.calculateItemPledge();
			// 非包邮商品
			if (BooleanUtils.isFalse(t.isFreeShipping)) {
				itemPerFee += BizConstants.TASK_EXPRESS_PLEDGE * 100;
			}
			long unlockAmt = itemPerFee - paidFee;
			SellerPledgeRecord unlockRecord = SellerPledgeRecord.newInstance(t.sellerId, lastPledge).taskId(taskId).createTime(now);
			if (unlockAmt >= 0) {
				memo = MixHelper.format("买手核实退款，解冻押金，押金:{},买手垫付：{},退还押金：{},任务编号：[{}-{}]", new BigDecimal(itemPerFee).movePointLeft(2).toString(),
						new BigDecimal(paidFee).movePointLeft(2).toString(), new BigDecimal(unlockAmt).movePointLeft(2).toString(), t.id,
						buyerTask.id);
				unlockRecord.action(PledgeAction.UNLOCK_SYS_REFUND, unlockAmt).memo(memo);
			} else {
				memo = MixHelper.format("买手核实退款，解冻押金，押金:{},买手垫付：{},扣取押金：{},任务编号：[{}-{}]", new BigDecimal(itemPerFee).movePointLeft(2).toString(),
						new BigDecimal(paidFee).movePointLeft(2).toString(), new BigDecimal(Math.abs(unlockAmt)).movePointLeft(2).toString(), t.id,
						buyerTask.id);
				unlockAmt = Math.abs(unlockAmt);
				unlockRecord.action(PledgeAction.DEDUCT_SYS_REFUND, unlockAmt).memo(memo);
			}
			pledgeMapper.insert(unlockRecord);

			pledgeBalance = unlockRecord.balance;

			SellerPledgeRecord lastLock = pledgeMapper.selectLastLock(taskId, t.sellerId, PledgeAction.LOCK_SYS_REFUND);
			pledgeMapper.updateIsDeduct(lastLock.modifyTime(now));

		} else {
			// 非平台返款任务：解冻全部押金（该单商品刷单金额+5%退款保证金+非包邮商品的运费押金）
			SellerPledgeRecord pledgeRecord = SellerPledgeRecord.newInstance(t.sellerId, lastPledge).taskId(taskId)
					.action(PledgeAction.UNLOCK, t.pledge).createTime(now).memo("买手核实退款，解冻押金，任务编号：[" + t.id + "-" + buyerTask.id + "]");
			pledgeMapper.insert(pledgeRecord);
			pledgeBalance = pledgeRecord.balance;

			/*
			 * 添加任务进度记录
			 */
			BuyerTaskStep3 taskStep = new BuyerTaskStep3();
			taskStep.type = BuyerTaskStepType.CONFIRM_REFUND;
			taskStep.buyerId = this.buyerId;
			taskStep.buyerTaskId = this.id;
			BuyerTaskStepMapper3 btMapper = ss.getMapper(BuyerTaskStepMapper3.class);
			taskStep.no = taskStep.type.getOrder();
			taskStep.createTime = now;
			btMapper.insert(taskStep);
		}

		// 更新卖家用户缓存
		User.findByIdWichCache(t.sellerId).pledge(pledgeBalance).updateCache();

		/*
		 * 若当前完成的子任务为最后一个，则设置父任务为“已完成”
		 */
		t.finishedCount = t.finishedCount + 1;
		boolean isTaskFinished = t.finishedCount >= t.totalOrderNum;

		Task3 task = Task3.instance(taskId);
		task.finishedCount = t.finishedCount;
		if (t.status != TaskStatus.CANCLED && isTaskFinished) {
			task.setStatus(TaskStatus.FINISHED);
		}
		task.modifyTime = DateTime.now().toDate();
		taskMapper.updateById(task);

		return buyerCache;
	}

	/**
	 * 
	 * 确认进行“平台返款”操作. 1、【定时任务触发】平台自动转账到买手账户 2、【管理员】后台手动触发
	 * 
	 * @since v1.7
	 * @author youblade
	 * @created 2014年11月28日 下午5:03:34
	 */
	public void confirmSysRefund() {
		Validate.notNull(this.id);
		Validate.notNull(this.taskId);
		Validate.notNull(this.buyerId);
		Validate.notNull(this.paidFee);
		Validate.notNull(this.experience);
		Validate.notNull(this.rewardIngot);

		SqlSession ss = SessionFactory.getSqlSessionWithoutAutoCommit();
		try {
			confirmSysRefund(ss);
			ss.commit();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			ss.rollback();
			throw new RuntimeException("核实退款操作失败~~");
		} finally {
			ss.close();
		}
	}

	/**
	 * 
	 * 确认进行“平台返款”操作. 1、【定时任务触发】平台自动转账到买手账户 2、【管理员】后台手动触发
	 * 
	 * @since v1.7
	 * @author youblade
	 * @created 2014年11月28日 下午5:03:34
	 */
	public void confirmSysRefund(SqlSession ss) {
		Validate.notNull(this.id);
		Validate.notNull(this.taskId);
		Validate.notNull(this.buyerId);
		Validate.notNull(this.paidFee);
		Validate.notNull(this.experience);
		Validate.notNull(this.rewardIngot);

		Date now = DateTime.now().toDate();
		User buyerCache = processRefund(ss, now, this.paidFee);

		/*
		 * 将之前冻结的卖家押金转账给买手
		 */
		String memo = MixHelper.format("平台退还任务[{}-{}]的垫付本金", this.taskId, this.id);
		BuyerDepositRecordMapper ingotMapper = ss.getMapper(BuyerDepositRecordMapper.class);
		// 获取上一次的金币记录计算结余
		BuyerDepositRecord lastRecord = ingotMapper.selectLastRecord(this.buyerId);
		BuyerDepositRecord record = BuyerDepositRecord.newInstance(this.buyerId, lastRecord).plus(this.paidFee).memo(memo).taskId(this.taskId)
				.createTime(now);
		ingotMapper.insert(record);
		// 设置用户缓存中的本金余额
		buyerCache.deposit(record.balance).updateCache();

	}

	/**
	 * 
	 * 批量更新任务状态
	 * 
	 * @param ids
	 * @param ts
	 * @since v0.1
	 * @author moloch
	 * @created 2014-8-12 上午10:52:56
	 */
	public static void batchModifyStatus(List<Long> ids, TaskStatus ts, Date modifyTime) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerTaskMapper3 btMapper = ss.getMapper(BuyerTaskMapper3.class);
			btMapper.batchUpdateStatus(ids, ts, modifyTime);
		} finally {
			ss.close();
		}
	}

	/**
	 * 
	 * 按照ID获取任务，用于核对商品链接.
	 * 
	 * @param id
	 * @param buyerId
	 * @return
	 * @since 0.1
	 * @author youblade
	 * @created 2014年8月13日 下午2:21:21
	 */
	public static BuyerTaskVo3 findVoById(long id, long buyerId) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerTaskMapper3 mapper = ss.getMapper(BuyerTaskMapper3.class);
			return mapper.selectByIdAndBuyerId(id, buyerId);
		} finally {
			ss.close();
		}
	}

	/**
	 * 
	 * 获取买手做任务所需的任务信息.
	 * 
	 * @param id
	 * @return
	 * @since 0.1
	 * @author youblade
	 * @created 2014年8月13日 下午5:13:35
	 */
	public static BuyerTaskVo3 findForPerforming(long id, long buyerId) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerTaskMapper3 mapper = ss.getMapper(BuyerTaskMapper3.class);
			BuyerTaskVo3 vo = mapper.selectVoByIdForPerform(id, buyerId);

			// 仅“已接手，未开始”、“已下单，未支付”状态的任务才可以做
			if (vo == null || (vo.status != TaskStatus.RECIEVED && vo.status != TaskStatus.WAIT_PAY)) {
				return null;
			}

			// 仅保留标题开头3个字和最后5个字
			String originTitle = vo.itemTitle;
			int length = originTitle.length();
			if (length > 8) {
				vo.itemTitle = originTitle.substring(0, 3) + "**************" + originTitle.substring(length - 5, length);
			} else if (length > 5) {
				vo.itemTitle = "**************" + vo.itemTitle.substring(length - 5, length);
			}

			// 店铺名仅显示前3个字符
			vo.subShopNamePrefix();
			return vo;
		} finally {
			ss.close();
		}
	}

	/**
	 * 
	 * 获取任务详情.
	 * 
	 * @param id
	 * @return
	 * @since 0.1
	 * @author youblade
	 * @created 2014年8月23日 下午7:13:52
	 */
	public static BuyerTaskVo3 findDetail(long id) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerTaskMapper3 mapper = ss.getMapper(BuyerTaskMapper3.class);
			BuyerTaskVo3 buyerTaskVo = mapper.selectByIdForViewDetail(id);

			// 平台返款任务，显示银行信息和卡号;非平台返款任务，需要查询“财付通”账号用于显示
			if (buyerTaskVo != null && buyerTaskVo.sysRefund) {
				FundAccount fundAccount = ss.getMapper(FundAccountMapper.class).selectBank(buyerTaskVo.buyerId);
				if (fundAccount == null) {
					return buyerTaskVo;
				}
				buyerTaskVo.refundAccountNo = fundAccount.no.substring(0, 4) + " **** **** ****";
				buyerTaskVo.refundAccountType = PayPlatform.valueOf(String.valueOf(fundAccount.type));
			}else{
				FundAccount fundAccount = ss.getMapper(FundAccountMapper.class).selectByType(buyerTaskVo.buyerId, PayPlatform.TENPAY.toString());
				if (fundAccount == null) {
					return buyerTaskVo;
				}
				buyerTaskVo.refundAccountNo = fundAccount.no;
				buyerTaskVo.refundAccountType = PayPlatform.TENPAY;
			}

			return buyerTaskVo;
		} finally {
			ss.close();
		}
	}

	/**
	 * 
	 * 根据id获取任务数据
	 * 
	 * @param id
	 * @return
	 * @since v0.1
	 * @author moloch
	 * @created 2014-8-18 下午5:47:45
	 */
	public static BuyerTask3 findById(long id) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerTaskMapper3 mapper = ss.getMapper(BuyerTaskMapper3.class);
			return mapper.selectById(id);
		} finally {
			ss.close();
		}
	}
	/**
	 * 
	 * 根据订单查询
	 *
	 * @param orderId
	 * @return
	 * @since  v3.4
	 * @author fufei
	 * @created 2015年4月23日 下午3:17:22
	 */
    public static BuyerTask3  findByOrderId(String orderId) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            BuyerTaskMapper3 mapper = ss.getMapper(BuyerTaskMapper3.class);
            return mapper.findByOrderId(orderId);
        } finally {
            ss.close();
        }
    }
	/**
	 * 
	 * 获取判断任务类型的数据
	 * 
	 * @param id
	 * @return
	 * @since v0.1
	 * @author moloch
	 * @created 2014-8-30 下午5:28:01
	 */
	public static BuyerTaskVo3 findByIdForTaskType(long id) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerTaskMapper3 mapper = ss.getMapper(BuyerTaskMapper3.class);
			return mapper.selectByIdForTaskType(id);
		} finally {
			ss.close();
		}
	}

	/**
	 * 
	 * 根据任务类型取得获取经验值数目
	 * 
	 * @param id
	 * @return
	 * @since v0.1
	 * @author moloch
	 * @created 2014-8-30 下午5:57:57
	 */
	public static Integer getByType(long id) {
		BuyerTaskVo3 vo = findByIdForTaskType(id);
		boolean newShop = vo.registTime.after(LocalDate.now().minusWeeks(1).toDate());

		// 新商家 经验值为3
		if (newShop) {
			return 3;
		}

		// 推荐任务 经验值为1
		if (vo.recommendIngot > 0) {
			return 1;
		}
		return 0;
	}

	/**
	 * 
	 * 检查任务是否属于某个买手用户.
	 * 
	 * @param user
	 * @return
	 * @since 0.1
	 * @author youblade
	 * @created 2014年8月24日 下午2:18:35
	 */
	public boolean isBelongTo(User user) {
		if (user == null || this.buyerId == null) {
			return false;
		}
		return user.id == this.buyerId.longValue();
	}

	public boolean isNotBelongTo(User user) {
		return !isBelongTo(user);
	}

	/**
	 * 
	 * 获取买手未做完（已开始做的，尚未下单支付）的任务.
	 * 
	 * @param buyerId
	 * @return
	 * @since 0.1
	 * @author youblade
	 * @created 2014年8月24日 下午2:46:32
	 */
	public static BuyerTask3 findTodoTask(long buyerId) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerTaskMapper3 mapper = ss.getMapper(BuyerTaskMapper3.class);
			BuyerTask3 buyerTask = mapper.selectLastWaitPayAndStepByBuyerId(buyerId);
			if (buyerTask == null) {
				return null;
			}

			// 默认是第一步
			if(Task.isTaoBaoAndTmall(buyerTask.taskId)){
				buyerTask.nextStep = BuyerTaskStepType.ORDER_AND_PAY;
			}else{
				buyerTask.nextStep = BuyerTaskStepType.CHOOSE_ITEM;
			}
			if (buyerTask.lastStep != null) {
				buyerTask.nextStep = buyerTask.lastStep.getNext();
			}
			return buyerTask;
		} finally {
			ss.close();
		}
	}

	/**
	 * 
	 * 判断父任务是否完成
	 * 
	 * @param taskId
	 * @return
	 * @since v0.1
	 * @author moloch
	 * @created 2014-9-3 下午6:02:54
	 */
	public static boolean isParentTaskFinished(long taskId) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerTaskMapper3 mapper = ss.getMapper(BuyerTaskMapper3.class);
			TaskCountVo3 vo = mapper.countForTaskFinish(taskId);
			if (vo.count == vo.totalOrderNum) {
				return true;
			}
			return false;
		} finally {
			ss.close();
		}
	}

	/**
	 * 
	 * 创建单表记录（测试用）.
	 * 
	 * @since 0.1
	 * @author youblade
	 * @created 2014年9月9日 下午3:48:01
	 */
	public BuyerTask3 simpleCreate() {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerTaskMapper3 mapper = ss.getMapper(BuyerTaskMapper3.class);
			this.modifyTime = this.takeTime;
			mapper.insert(this);
		} finally {
			ss.close();
		}
		return this;
	}

	/**
	 * 
	 * 批量查询单表记录（测试用）.
	 * 
	 * @param vo
	 * @return
	 * @since 0.1
	 * @author youblade
	 * @created 2014年9月9日 下午4:31:15
	 */
	public static List<BuyerTask3> findList(TaskSearchVo3 vo) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerTaskMapper3 mapper = ss.getMapper(BuyerTaskMapper3.class);
			return mapper.select(vo);
		} finally {
			ss.close();
		}
	}
	
	/**
	 * 
	 * 获取不同平台的未接任务数量
	 *
	 * @param platform
	 * @return
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-2-26 下午4:55:13
	 */
	public static Integer findCountByPlatform(Platform3 platform) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			TaskMapper3 mapper = ss.getMapper(TaskMapper3.class);
			return (mapper.pcPublishAndNotTokenOverWithPlatform(platform.toString())!=null?mapper.pcPublishAndNotTokenOverWithPlatform(platform.toString()):0)
					+ (mapper.mobilePublishAndNotTokenOverWithPlatform(platform.toString())!=null?mapper.mobilePublishAndNotTokenOverWithPlatform(platform.toString()):0);
		}finally {
			ss.close();
		}
	}
	
	
	/**
	 * 
	 * 获取买手未做完（已开始做的，尚未下单支付）的任务列表.
	 * 
	 * @param buyerId
	 * @return
	 * @since 0.1
	 * @author youblade
	 * @created 2014年8月24日 下午2:46:32
	 */
	public static List<BuyerTaskVo3> findTodoTasks(long buyerId) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerTaskMapper3 mapper = ss.getMapper(BuyerTaskMapper3.class);
			List<BuyerTaskVo3> bts = mapper.selectLastWaitPaysAndStepsByBuyerId(buyerId);
			if (bts.size()==0||bts == null) {
				return null;
			}
			return bts;
		} finally {
			ss.close();
		}
	}
	
	
	
	/**
	 * 
	 * 获取PC端的未接任务数量
	 *
	 * @param platform
	 * @return
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-2-27 上午11:13:05
	 */
	public static Integer findPcCountByPlatform(Platform3 platform, TaskType3 taskType) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			TaskMapper3 mapper = ss.getMapper(TaskMapper3.class);
			if(taskType == null) {
				return (mapper.pcPublishAndNotTokenOverWithPlatform(platform.toString())!=null?mapper.pcPublishAndNotTokenOverWithPlatform(platform.toString()):0);
			}else {
				Integer a = (mapper.pcPublishAndNotTokenOverWithDevice(platform.toString(), taskType.toString()));
				return (a!=null?a:0);
			}
		}finally {
			ss.close();
		}
	}
	
	/**
	 * 
	 * 获取 手机/pad 端的未接任务数量
	 *
	 * @param platform
	 * @return
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-2-27 上午11:13:05
	 */
	public static Integer findPadCountByPlatform(Platform3 platform, TaskType3 taskType) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			TaskMapper3 mapper = ss.getMapper(TaskMapper3.class);
			if(taskType == null) {
				return (mapper.mobilePublishAndNotTokenOverWithPlatform(platform.toString())!=null?mapper.mobilePublishAndNotTokenOverWithPlatform(platform.toString()):0);
			}else {
				Integer a = (mapper.mobilePublishAndNotTokenOverWithDevice(platform.toString(), taskType.toString()));
				return (a!=null?a:0);
			}
		}finally {
			ss.close();
		}
	}
	
	/**
	 * 
	 * 发货操作
	 * 
	 * @since v0.1
	 * @author moloch
	 * @created 2014-9-16 下午4:30:01
	 */
	public void sendGood() {
		SqlSession ss = SessionFactory.getSqlSessionWithoutAutoCommit();
		try {

			// 更新任务状态
			BuyerTaskMapper3 btMapper = ss.getMapper(BuyerTaskMapper3.class);
			BuyerTask3 bt = new BuyerTask3();
			bt.id = this.id;
			bt.status = TaskStatus.WAIT_CONFIRM;
			bt.modifyTime = DateTime.now().toDate();
			btMapper.update(bt);

			// 保存任务进展步骤
			BuyerTaskStep3 taskStep = new BuyerTaskStep3();
			taskStep.type = BuyerTaskStepType.SEND_GOODS;
			BuyerTask3 bta = btMapper.selectById(this.id);
			taskStep.buyerId = bta.buyerId;
			taskStep.buyerTaskId = this.id;
			BuyerTaskStepMapper3 btsMapper = ss.getMapper(BuyerTaskStepMapper3.class);
			taskStep.no = taskStep.type.getOrder();
			taskStep.createTime = DateTime.now().toDate();

			btsMapper.insert(taskStep);

			ss.commit();

		} finally {
			ss.close();
		}
	}

	/**
	 * 
	 * 撤销买手任务
	 * 
	 * @param this.id
	 * @since v0.1
	 * @author moloch
	 * @created 2014-9-23 上午10:39:34
	 */
	public void cancel() {
		SqlSession ss = SessionFactory.getSqlSessionWithoutAutoCommit();
		try {
			cancelStep(ss);
			ss.commit();
			// 修改关键词对应接单数
			if (this.searchPlanId > 0) {
				TaskItemSearchPlan3 p = TaskItemSearchPlan3.findById(this.searchPlanId);
				p.takenNum -= 1;
				TaskItemSearchPlan3.update(p);
			}
			// 更新“任务列表(X)”中的任务数量
			Task3.updateTaskCount(Sign.PLUS, 1);

			// 更新买手接手任务统计的缓存
			TaskStats3.updateTaskTakenCount(this.buyerAccountId, Sign.MINUS);

		} catch (Exception e) {
			ss.rollback();
			log.error(e.getMessage(), e);
			throw new RuntimeException("撤销任务操作失败！");
		} finally {
			ss.close();
		}
	}

	/**
	 * 
	 * 从数据库中获取所有未返款的【买手本金提现申请】
	 * 
	 * @return
	 * @since v1.6
	 * @author playlaugh
	 * @created 2014年12月6日 上午10:55:57
	 */

	public static List<BuyerDepositVo> findUntradeBuyerDeposit(boolean isBuyerDispose) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerTaskMapper3 mapper = ss.getMapper(BuyerTaskMapper3.class);
			if(isBuyerDispose)
			return mapper.selectUntradeBuyerDeposit();//本金提现
			else
		    return mapper.selectUntradeBuyerWidthdraw();//佣金提现
		} finally {
			ss.close();
		}
	}

	/**
	 * 
	 * 买手任务撤销步骤
	 * 
	 * @param ss
	 * @since v0.1
	 * @author moloch
	 * @created 2014-9-23 下午2:14:01
	 */
	public void cancelStep(SqlSession ss) {
		BuyerTaskMapper3 mapper = ss.getMapper(BuyerTaskMapper3.class);
		BuyerTask3 bt = mapper.selectById(this.id);

		// 更新买手任务状态
		Date now = DateTime.now().toDate();
		bt.status(TaskStatus.CANCLED).modifyTime(now);
		mapper.updateById(bt);

		// 更新主任务接手数量
		TaskMapper3 tMapper = ss.getMapper(TaskMapper3.class);
		Task3 task = tMapper.selectFieldsById("*", bt.taskId);
		bt.updateTaskTakenCount(tMapper, task, Sign.MINUS);

		// 如果主任务为已取消状态，将撤销子任务的费用退还给卖家。
		if (task.status == task.status.CANCLED) {
			BuyerTaskProcessor3.refundCancledBuyerTask(ss, task, bt);
		}
	}

	// 根据任务终端对父任务的“已接手数”进行增减
	private Task3 updateTaskTakenCount(TaskMapper3 taskMapper, Task3 t, Sign sign) {
		Validate.notNull(this.taskId, "BuyerTask.taskId must not be null.");
		Validate.notNull(t.pcTakenCount, "Task.pcTakenCount must not be null.");
		Validate.notNull(t.mobileTakenCount, "Task.mobileTakenCount must not be null.");

		Task3 taskToSave = Task3.instance(this.taskId).modifyTime(DateTime.now().toDate());
		switch (this.device) {
		case PC:
			if (Sign.MINUS == sign) {
				t.pcTakenCount -= 1;
			} else {
				t.pcTakenCount += 1;
			}
			taskToSave.pcTakenCount = t.pcTakenCount;
			break;
		case MOBILE:
			if (Sign.MINUS == sign) {
				t.mobileTakenCount -= 1;
			} else {
				t.mobileTakenCount += 1;
			}
			taskToSave.mobileTakenCount = t.mobileTakenCount;
			break;
		default:
			break;
		}

		taskMapper.updateById(taskToSave);
		return taskToSave;
	}

	public boolean isTheSameBuyerAccount(Long buyerAccountId) {
		return buyerAccountId.longValue() == this.buyerAccountId.longValue();
	}

	/**
	 * 
	 * 该任务是否禁止被进行“平台返款”确认操作.
	 * 
	 * @return
	 * @since v1.7
	 * @author youblade
	 * @created 2014年12月2日 上午11:52:29
	 */
	public boolean forbiddenConfirmSysRefund() {
		Validate.notNull(this.status);
		return this.status != TaskStatus.WAIT_SELLER_CONFIRM_SYS_REFUND && this.status != TaskStatus.WAIT_BUYER_CONFIRM_EDITED_SYS_REFUND
				&& this.status != TaskStatus.BUYER_REJECT_SYS_REFUND;
	}

	/**
	 * 
	 * 获取任务编号：父任务与子任务组合而成的字符串，形如524139983175417856-6.
	 * 
	 * @return
	 * @since v0.2
	 * @author youblade
	 * @created 2014年10月22日 下午2:39:35
	 */
	public String getTaskNo() {
		return String.valueOf(this.taskId) + "-" + String.valueOf(this.id);
	}

	public static BuyerTask3 newInstance() {
		BuyerTask3 bt = new BuyerTask3();
		return bt;
	}

	public static BuyerTask3 instance(Long id) {
		BuyerTask3 bt = new BuyerTask3();
		bt.id = id;
		return bt;
	}

	public BuyerTask3 buyerId(Long buyerId) {
		this.buyerId = buyerId;
		return this;
	}

	public BuyerTask3 buyerAccountId(Long buyerAccountId) {
		this.buyerAccountId = buyerAccountId;
		return this;
	}

	public BuyerTask3 status(TaskStatus status) {
		this.status = status;
		return this;
	}

	public BuyerTask3 modifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
		return this;
	}

	public BuyerTask3 takeTime(Date takeTime) {
		this.takeTime = takeTime;
		return this;
	}

	public BuyerTask3 taskId(Long taskId) {
		this.taskId = taskId;
		return this;
	}

	public BuyerTask3 paidFee(Long paidFee) {
		this.paidFee = paidFee;
		return this;
	}

	public BuyerTask3 withdrawSn(Long userWithdrawRecordSn) {
		this.userWithdrawRecordSn = userWithdrawRecordSn;
		return this;
	}

	/**
	 * 
	 * 按状态获取用户“平台返款”任务的数量.
	 * 
	 * @param user
	 *            :根据用户类型可分别统计【买手】【商家】的子任务
	 * @return
	 * @since v1.7
	 * @author youblade
	 * @created 2014年11月27日 下午4:06:52
	 */
	public static int findSysRefundCount(User user) {
		Validate.notNull(user);
		Validate.notNull(user.id);
		Validate.notNull(user.type);

		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerTaskMapper3 mapper = ss.getMapper(BuyerTaskMapper3.class);

			// 商家：买手确认收货后的状态、买手驳回后的状态
			TaskSearchVo3 vo = TaskSearchVo3.newInstance();
			if (user.type == UserType.SELLER) {
				vo.sellerId(user.id).statusIn(TaskStatus.WAIT_SELLER_CONFIRM_SYS_REFUND, TaskStatus.BUYER_REJECT_SYS_REFUND);
				return mapper.countSysRefundByUserIdAndStatus(vo);
			}

			// 买手：商家直接确认后的状态、修改价格后再确认后的状态
			vo.buyerId(user.id).statusIn(TaskStatus.WAIT_BUYER_CONFIRM_SYS_REFUND, TaskStatus.WAIT_BUYER_CONFIRM_EDITED_SYS_REFUND);
			return mapper.countSysRefundByUserIdAndStatus(vo);
		} finally {
			ss.close();
		}
	}

	/**
	 * 
	 * 【后台管理】分页获取买手任务
	 * 
	 * @param vo
	 * @return
	 * @since v1.9
	 * @author youblade
	 * @created 2014年12月10日 下午6:54:53
	 */
    public static Page<BuyerTaskVo3> findByPageForAdmin(TaskSearchVo3 vo) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerTaskMapper3 mapper = ss.getMapper(BuyerTaskMapper3.class);
			int totalCount = mapper.count(vo);
			if (totalCount <= 0) {
				return Page.EMPTY;
			}

			List<BuyerTaskVo3> vos = mapper.selectListForAdmin(vo);
			// 赋值taskIdStr
			for (BuyerTaskVo3 btVo : vos) {
				btVo.value();
				if (btVo.itemPicUrl != null && btVo.itemPicUrl.startsWith("{")) {
					BuyerTaskStepVo btSvo = JsonUtil.toBean(btVo.itemPicUrl, BuyerTaskStepVo.class);
					btVo.itemPicUrl = btSvo.picUrls.get(0);
				}
			}
			Page<BuyerTaskVo3> page = Page.newInstance(vo.pageNo, vo.pageSize, totalCount);
			page.items = vos;
			return page;
		} finally {
			ss.close();
		}
	}
	//获取某特定状态下的任务数
	public static int count(TaskSearchVo3 vo) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerTaskMapper3 btMapper = ss.getMapper(BuyerTaskMapper3.class);
			return btMapper.count(vo);
		}finally {
			ss.close();
		}
	}
	
	/**
	 * 获取准备打印的订单
	 * 
	 * @author fufei
	 * @param vo
	 * @return
	 */
	public static List<OrderExpress> waitPrintOrders(TaskSearchVo3 vo) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerTaskMapper3 btMapper = ss.getMapper(BuyerTaskMapper3.class);
			int count = btMapper.count(vo);
			if (count <= 0) {
				return Collections.EMPTY_LIST;
			}
			List<Long> ids = Lists.newArrayList();
			// 将需要打印的订单id放入集合
			String[] printIds = vo.printId.split(",");
			for (int i = 0; i < printIds.length; i++) {
				if (printIds[i] != null && !"".equals(printIds[i])) {
					ids.add(Long.parseLong(printIds[i]));
				}
			}
			List<OrderExpress> orders = btMapper.selectOrdersByIds(vo.status, ids);

			// 赋值number、fullAddress字段
			for (OrderExpress oe : orders) {
				oe.resolve();
			}
			return orders;
		} finally {
			ss.close();
		}
	}

	/**
	 * 根据id批量查询
	 * 
	 * @author fufei
	 * @param ids
	 * @return
	 */
	public static List<OrderExpress> findOrderByIds(List<Long> ids) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerTaskMapper3 btMapper = ss.getMapper(BuyerTaskMapper3.class);
			List<OrderExpress> orders = btMapper.selectOrdersByIds(null, ids);

			// 赋值number、fullAddress字段
			for (OrderExpress oe : orders) {
				oe.resolve();
			}
			return orders;
		} finally {
			ss.close();
		}
	}

	/**
	 * 修改运单号
	 * 
	 * @author fufei
	 * @param bt
	 * @return
	 */
	public static Integer updateExpressNoById(BuyerTask3 bt) {
		Validate.notNull(bt.id);

		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerTaskMapper3 btMapper = ss.getMapper(BuyerTaskMapper3.class);
			bt.modifyTime = DateTime.now().toDate();
			return btMapper.updateExpressNoById(bt);
		} finally {
			ss.close();
		}
	}

	/**
	 * 查询某个状态的运单号数量
	 * 
	 * @author fufei
	 * @param bt
	 * @return
	 */
	public static Integer selectOrdersCountByStatus(TaskStatus status) {
		Validate.notNull(status);
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerTaskMapper3 btMapper = ss.getMapper(BuyerTaskMapper3.class);
			return btMapper.selectOrdersCountByStatus(status);
		} finally {
			ss.close();
		}
	}
	/**
	 * 
	 * 每日接单数统计
	 *
	 * @param status
	 * @return
	 * @since  v2.4
	 * @author fufei
	 * @created 2015年2月5日 上午10:45:49
	 */
	public static Page<TakeTaskCountVo> findTakeTaskCount(TakeTaskCountVo vo) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerTaskMapper3 btMapper = ss.getMapper(BuyerTaskMapper3.class);
			int totalCount = btMapper.findTakeTaskTotalCount().size();
			if (totalCount <= 0) {
				return Page.EMPTY;
			}
			Page<TakeTaskCountVo> page =Page.newInstance(vo.pageNo, vo.pageSize, totalCount);
			SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
			page.items = btMapper.findTakeTaskCount(vo);
			for (TakeTaskCountVo takeTaskCountVo : page.items) {
				takeTaskCountVo.newBuyer(btMapper.findNewBuyerCount(sf.format(takeTaskCountVo.takeTime)));
				takeTaskCountVo.newSeller(btMapper.findNewSellerCount(sf.format(takeTaskCountVo.takeTime)));
				takeTaskCountVo.expenseSellerCount(btMapper.findExpenseSellerCount(sf.format(takeTaskCountVo.takeTime)));
				takeTaskCountVo.buyerCount(btMapper.findBuyerCount(sf.format(takeTaskCountVo.takeTime)));
				takeTaskCountVo.sellerCount(btMapper.findSellerCount(sf.format(takeTaskCountVo.takeTime)));
			}
			return page;
		} finally {
			ss.close();
		}
	}
	/**
	 * 
	 * 查询商家账单
	 *
	 * @param vo
	 * @return
	 * @since  v2.6
	 * @author fufei
	 * @created 2015年3月2日 上午10:36:05
	 */
	public static Page<SellerBalanceVo> findBuyerTaskBalance(SellerBalanceVo vo) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerTaskMapper3 btMapper = ss.getMapper(BuyerTaskMapper3.class);
			int totalCount = btMapper.findBuyerTaskBalanceCount(vo);
			if (totalCount <= 0) {
				return Page.EMPTY;
			}
			Page<SellerBalanceVo> page =Page.newInstance(vo.pageNo, vo.pageSize, totalCount);
			page.items = btMapper.findBuyerTaskBalance(vo);
			return page;
		} finally {
			ss.close();
		}
	}
	
	/**
	 * 
	 * 导出商家父任务账单
	 *
	 * @param vo
	 * @return
	 * @since  v2.6
	 * @author fufei
	 * @created 2015年3月2日 上午10:36:05
	 */
	public static List<SellerBalanceVo> exprotSellerTaskBalance(SellerBalanceVo vo) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerTaskMapper3 btMapper = ss.getMapper(BuyerTaskMapper3.class);
			return  btMapper.exportSellerTaskBalance(vo);
		} finally {
			ss.close();
		}
	}
	
	/**
	 * 
	 * 导出商家子任务账单
	 *
	 * @param vo
	 * @return
	 * @since  v2.6
	 * @author fufei
	 * @created 2015年3月2日 上午10:36:05
	 */
	public static List<SellerBalanceVo> exprotBuyerTaskBalance(SellerBalanceVo vo) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerTaskMapper3 btMapper = ss.getMapper(BuyerTaskMapper3.class);
			return  btMapper.exportBuyerTaskBalance(vo);
		} finally {
			ss.close();
		}
	}
	
	/**
	 * 
	 * 后台查询商家账单
	 *
	 * @param vo
	 * @return
	 * @since  v2.6
	 * @author fufei
	 * @created 2015年3月2日 上午10:36:05
	 */
	public static Page<SellerBalanceVo> findSellerTaskBalance(SellerBalanceVo vo) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerTaskMapper3 btMapper = ss.getMapper(BuyerTaskMapper3.class);
			int totalCount = btMapper.findSellerTaskBalanceCount(vo);
			if (totalCount <= 0) {
				return Page.EMPTY;
			}
			Page<SellerBalanceVo> page =Page.newInstance(vo.pageNo, vo.pageSize, totalCount);
			page.items = btMapper.findSellerTaskBalance(vo);
			return page;
		} finally {
			ss.close();
		}
	}
	/**
	 * 查询买号所以已完成的任务
	 *
	 * @param buyerAccountId
	 * @return
	 * @since  v2.7
	 * @author fufei
	 * @created 2015年3月9日 下午12:13:37
	 */
	public static int findByBuyerAccount(long buyerAccountId) {
		SqlSession ss = SessionFactory.getSqlSession();
		try {
			BuyerTaskMapper3 btMapper = ss.getMapper(BuyerTaskMapper3.class);
			return btMapper.findByBuyerAccount(buyerAccountId);
		} finally {
			ss.close();
		}
	}

    /**
     * 分页获取发包裹订单
     *
     * @param vo
     * @return
     * @since  v2.9
     * @author fufei
     * @created 2015年4月29日 下午5:19:28
     */
    public static Page<FaBaoGuoVo> findFaBaoGuoOrders(TaskSearchVo3 vo) {
        SqlSession ss = SessionFactory.getSqlSession();

        try {
            BuyerTaskMapper3 btMapper = ss.getMapper(BuyerTaskMapper3.class);
            int totalCount = btMapper.findFaBaoGuoOrdersCount(vo);
            if (totalCount <= 0) {
                return Page.EMPTY;
            }
            Page<FaBaoGuoVo> page = Page.newInstance(vo.pageNo, vo.pageSize, totalCount);
            page.items = btMapper.findFaBaoGuoOrders(vo);
            for (FaBaoGuoVo v : page.items) {
                v.resolve();
            }
            return page;
        } finally {
            ss.close();
        }
    }
    /**
     * 
     * 导出发包裹订单
     *
     * @param vo
     * @return
     * @since  v3.4
     * @author fufei
     * @created 2015年4月30日 下午2:15:34
     */
    public static List<FaBaoGuoVo> exportFaBaoGuoOrders(TaskSearchVo3 vo) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            BuyerTaskMapper3 btMapper = ss.getMapper(BuyerTaskMapper3.class);
           List<FaBaoGuoVo> vos = btMapper.findFaBaoGuoOrders(vo);
           for (FaBaoGuoVo faBaoGuoVo : vos) {
               faBaoGuoVo.resolve();
              }
            return vos;
        } finally {
            ss.close();
        }
    }
    
   /**
    * 
    * 批量更新发包裹状态
    *
    * @param ids
    * @param ts
    * @param modifyTime
    * @since  v3.4
    * @author fufei
    * @created 2015年4月30日 下午2:19:27
    */
    public static void batchFaBaoGuoModifyStatus(List<Long> ids, TaskStatus ts, Date modifyTime) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            BuyerTaskMapper3 btMapper = ss.getMapper(BuyerTaskMapper3.class);
            btMapper.batchFaBaoGuoModifyStatus(ids, ts, modifyTime);
        } finally {
            ss.close();
        }
    }
    
    /**
     * 
     * 删除导出的数据
     *
     * @param ids
     * @param ts
     * @param modifyTime
     * @since  v3.4
     * @author fufei
     * @created 2015年4月30日 下午2:19:27
     */
     public static void batchDeleteFaBaoGuo(List<Long> ids, TaskStatus ts) {
         SqlSession ss = SessionFactory.getSqlSession();
         try {
             BuyerTaskMapper3 btMapper = ss.getMapper(BuyerTaskMapper3.class);
             btMapper.batchDeleteFaBaoGuo(ids, ts);
         } finally {
             ss.close();
         }
     }
    /**
     * 
     * 按id更新
     *
     * @param id
     * @param ts
     * @since  v3.4
     * @author fufei
     * @created 2015年4月30日 下午5:28:36
     */
    public static void updateFaBaoGuoById(long id, TaskStatus ts) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            BuyerTaskMapper3 btMapper = ss.getMapper(BuyerTaskMapper3.class);
            btMapper.updateFaBaoGuoById(id, ts.toString());
        } finally {
            ss.close();
        }
    }
    
    /**
     * 
     * 更新
     *
     * @param id
     * @param ts
     * @since  v3.4
     * @author fufei
     * @created 2015年5月4日 上午11:20:16
     */
    public static void updateFaBaoGuoExpressNoById(long id, TaskStatus ts,String expressNo) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            BuyerTaskMapper3 btMapper = ss.getMapper(BuyerTaskMapper3.class);
            btMapper.updateFaBaoGuoExpressNoById(id,expressNo, ts.toString());
        } finally {
            ss.close();
        }
    }
    /**
     * 
     *按id查询
     *
     * @param id
     * @return
     * @since  3.5
     * @author fufei
     * @created 2015年4月30日 下午5:29:12
     */
    public static FaBaoGuoVo findFaBaoGuoById(long id) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            BuyerTaskMapper3 btMapper = ss.getMapper(BuyerTaskMapper3.class);
            return btMapper.findFaBaoGuoById(id);
        } finally {
            ss.close();
        }
    }
    /**
     * 
     * 按订单号查询
     *
     * @param orderId
     * @return
     * @since  v3.4
     * @author fufei
     * @created 2015年4月30日 下午5:29:30
     */
    public static FaBaoGuoVo findFaBaoGuoByprderSn(String orderId) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            BuyerTaskMapper3 btMapper = ss.getMapper(BuyerTaskMapper3.class);
            return btMapper.findFaBaoGuoByprderSn(orderId);
        } finally {
            ss.close();
        }
    }
    /**
     * 
     *添加订单
     *
     * @param vo
     * @since  v3.4
     * @author fufei
     * @created 2015年4月30日 下午5:29:49
     */
    public static void inserFabaoguoOrders(FaBaoGuoVo vo) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            BuyerTaskMapper3 btMapper = ss.getMapper(BuyerTaskMapper3.class);
            btMapper.inserFabaoguoOrders(vo);
        } finally {
            ss.close();
        }
    }
    /**
     * 
     * 查询发快递时间
     *
     * @return
     * @since  v3.4
     * @author fufei
     * @created 2015年5月6日 下午2:17:04
     */
    public static Page<ExpressCountVo> findExpressList(ExpressCountVo vo) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            BuyerTaskMapper3 btMapper = ss.getMapper(BuyerTaskMapper3.class);
            Integer totalCount = btMapper.findExpressListCount();
            if (totalCount <= 0||totalCount==null) {
                return Page.EMPTY;
            }
            Page<ExpressCountVo> page = Page.newInstance(vo.pageNo, vo.pageSize, totalCount);
            page.items = btMapper.findExpressList(vo.startIndex,vo.pageSize);
            for (ExpressCountVo countVo : page.items) {
                ExpressCountVo search=ExpressCountVo.newInstance();
                search.takeTime=countVo.takeTime;
                search.expressType=ExpressType.KJKD;
                countVo.kjkd=btMapper.findExpressCount(search);
                
                search.expressType=ExpressType.YDKD;
                countVo.ydkd=btMapper.findExpressCount(search);
                
                search.expressType=ExpressType.SELLERKD;
                countVo.sellerKd=btMapper.findExpressCount(search);
                countVo.fabaoguo=btMapper.findFaBaoGuoCount(search);
            }
            return page;
        } finally {
            ss.close();
        }
    }
    
    /**
     * 
     * 查询快递总数
     *
     * @return
     * @since  v3.4
     * @author fufei
     * @created 2015年5月6日 下午2:17:04
     */
    public static Integer findExpressCount(ExpressCountVo vo) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            BuyerTaskMapper3 btMapper = ss.getMapper(BuyerTaskMapper3.class);
           return btMapper.findExpressCount(vo);
        } finally {
            ss.close();
        }
    }
    
    /**
     * 
     * 查询发包裹快递总数
     *
     * @return
     * @since  v3.4
     * @author fufei
     * @created 2015年5月6日 下午2:17:04
     */
    public static Integer findFaBaoGuoCount(ExpressCountVo vo) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            BuyerTaskMapper3 btMapper = ss.getMapper(BuyerTaskMapper3.class);
           return btMapper.findFaBaoGuoCount(vo);
        } finally {
            ss.close();
        }
    }
    
    
}
