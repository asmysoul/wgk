package domain;

import java.util.Date;

import models.BuyerTask;
import models.BuyerTask2;
import models.BuyerTaskStep;
import models.BuyerTaskStep2;
import models.SellerPledgeRecord;
import models.Task;
import models.Task2;
import models.User;
import models.UserIngotRecord;
import models.SellerPledgeRecord.PledgeAction;
import models.mappers.BuyerTaskMapper;
import models.mappers.BuyerTaskMapper2;
import models.mappers.BuyerTaskStepMapper;
import models.mappers.BuyerTaskStepMapper2;
import models.mappers.SellerPledgeRecordMapper;
import models.mappers.UserIngotRecordMapper;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.Validate;
import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aton.config.BizConstants;
import com.aton.db.SessionFactory;

import controllers.TaskExecutor;
import enums.BuyerTaskStepType;
import enums.Sign;
import enums.TaskStatus;

/**
 * 
 * 买手任务的业务处理.
 * 
 * @author youblade
 * @since v1.7
 * @created 2014年12月3日 下午3:59:03
 */
public class BuyerTaskProcessor2 {

	private static final Logger log = LoggerFactory.getLogger(TaskExecutor.class);
	public static final Logger buyerTaskStepLog = LoggerFactory.getLogger("taskstep");

	/**
	 * 
	 * 【买手】核实“平台返款”任务的退款金额
	 * 
	 * @param buyerTask
	 * @param isReject
	 * @since v1.7
	 * @author youblade
	 * @created 2014年12月3日 下午3:10:47
	 */
	public synchronized static void confirmSysRefund(BuyerTask2 buyerTask, boolean isReject) {
		Validate.notNull(buyerTask.id);
		Validate.notNull(buyerTask.buyerId);
		BuyerTask2 bt = BuyerTask2.instance(buyerTask.id).status(TaskStatus.WAIT_SYS_REFUND);
		if (isReject) {
			bt.status(TaskStatus.BUYER_REJECT_SYS_REFUND);
		}

		SqlSession ss = SessionFactory.getSqlSessionWithoutAutoCommit();
		try {
			// 更新任务状态
			BuyerTaskMapper2 mapper = ss.getMapper(BuyerTaskMapper2.class);
			Date now = DateTime.now().toDate();
			mapper.updateById(bt.modifyTime(now));

			BuyerTaskStepMapper2 stepMapper = ss.getMapper(BuyerTaskStepMapper2.class);

			// 若买手驳回待核实的任务，则无须保存该步骤进度，但要重置商家上一步的进度
			if (bt.status == TaskStatus.BUYER_REJECT_SYS_REFUND) {
				BuyerTaskStep2 sellerconfirmStep = stepMapper.selectByTaskIdAndTypeAndBuyerId(bt.id, BuyerTaskStepType.REFUND, buyerTask.buyerId);
				// 更新显示标记及修改时间
				if (sellerconfirmStep != null && BooleanUtils.isTrue(sellerconfirmStep.isValid)) {
					stepMapper.updateById(BuyerTaskStep2.instance(sellerconfirmStep.id).valid(false).modifyTime(now));
				}
				return;
			}

			buyerTask.confirmSysRefund(ss);
			/*
			 * 保存买手任务进度，用于买手任务详情页的显示
			 */
			BuyerTaskStep2 confirmStep = stepMapper.selectByTaskIdAndTypeAndBuyerId(bt.id, BuyerTaskStepType.CONFIRM_REFUND, buyerTask.buyerId);

			// 若之前未进行过改操作则新建一个进度记录
			if (confirmStep == null) {
				BuyerTaskStep2 taskStep = BuyerTaskStep2.newInstance(bt.id, buyerTask.buyerId).type(BuyerTaskStepType.CONFIRM_REFUND).createTime(now)
						.modifyTime(now);
				stepMapper.insert(taskStep);
				return;
			}

			// 若之前进行过该操作，则直接更新显示标记及修改时间
			if (BooleanUtils.isFalse(confirmStep.isValid)) {
				stepMapper.updateById(BuyerTaskStep2.instance(confirmStep.id).valid(true).modifyTime(now));
			}
			
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			ss.rollback();
			throw new RuntimeException("驳回操作失败");
		} finally {
			ss.commit();
			ss.close();
		}
	}

	/**
	 * 
	 * 【买手】撤销已核实确认的“平台返款”任务
	 * 
	 * @param bt
	 * @since v1.7
	 * @author youblade
	 * @created 2014年12月3日 下午4:20:40
	 */
	public synchronized static void cancelConfirmedSysRefund(BuyerTask2 bt) {
		Validate.isTrue(bt.status == TaskStatus.WAIT_SYS_REFUND);

		SqlSession ss = SessionFactory.getSqlSessionWithoutAutoCommit();
		try {
			// 更新任务状态
			BuyerTaskMapper2 mapper = ss.getMapper(BuyerTaskMapper2.class);
			Date now = DateTime.now().toDate();
			mapper.updateById(bt.modifyTime(now));

			/*
			 * 保存买手任务进度，用于买手任务详情页的显示
			 */
			// 将之前操作的任务进度标记置为“无效”
			BuyerTaskStepMapper2 stepMapper = ss.getMapper(BuyerTaskStepMapper2.class);
			BuyerTaskStep2 confirmStep = stepMapper.selectByTaskIdAndTypeAndBuyerId(bt.id, BuyerTaskStepType.CONFIRM_REFUND, bt.buyerId);
			if (BooleanUtils.isTrue(confirmStep.isValid)) {
				stepMapper.updateById(BuyerTaskStep2.instance(confirmStep.id).valid(false).modifyTime(now));
			}

			ss.commit();
		} finally {
			ss.close();
		}
	}

	/**
	 * 
	 * 撤销子任务时返款给商家
	 * 
	 * @param ss
	 * @param task
	 * @param bt
	 * @since v1.6
	 * @author playlaugh
	 * @created 2014年12月15日 下午6:52:05
	 */

	public static void refundCancledBuyerTask(SqlSession ss, Task2 task, BuyerTask2 bt) {

		Date now = DateTime.now().toDate();
		/*
		 * 退还押金
		 */

		// 每单任务押金
		long pledge = task.calculateItemPledge();
		// 非包邮商品运费押金
		if (BooleanUtils.isFalse(task.isFreeShipping)) {
			pledge += (BizConstants.TASK_EXPRESS_PLEDGE * 100);
		}
		// 押金计算方式（+ —）
		PledgeAction action = PledgeAction.UNLOCK;
		// 操作
		SellerPledgeRecordMapper pledgeMapper = ss.getMapper(SellerPledgeRecordMapper.class);
		SellerPledgeRecord lastRecord = pledgeMapper.selectLastRecord(task.sellerId);
		SellerPledgeRecord record = SellerPledgeRecord.newInstance(task.sellerId, lastRecord).taskId(task.id).action(action, pledge).createTime(now)
				.memo("买手撤销任务[" + task.id + "]解冻押金");
		pledgeMapper.insert(record);

		/*
		 * 退还金币
		 */

		// 每单基础服务费
		long ingot = task.computeFeePerOrder(task.itemPrice * task.itemBuyNum,task.platform);
		// 移动端额外费用
		if (bt.device == bt.device.MOBILE) {
			ingot += (long) (BizConstants.TASK_MOBILE_INGOT * 100);
		}
		// 平台返款费用
		if (task.sysRefund) {
			ingot += task.calculateSysRefundBuyerTaskFee();//撤销子任务不需要乘总单数
		}
		// 快递费
	//	ingot += task.expressIngot;
		// 操作
		UserIngotRecordMapper ingotMapper = ss.getMapper(UserIngotRecordMapper.class);
		UserIngotRecord lastIngot = ingotMapper.selectLastRecord(task.sellerId);
		UserIngotRecord ingotRecord = UserIngotRecord.newInstance(task.sellerId, lastIngot).taskId(task.id).plus(ingot).createTime(now)
				.memo("买手撤销任务[" + task.id + "]，退还任务佣金");
		ingotMapper.insert(ingotRecord);
		
		// 更新商家user缓存
        User.findByIdWichCache(task.sellerId).pledge(record.balance).ingot(ingotRecord.balance).updateCache();
	}
	
	/**
	 * 
	 * 返回买手接任务的押金
	 *
	 * @param bt
	 * @since  v2.0
	 * @author Mark Xu
	 * @created 2015-3-23 下午1:27:55
	 */
	public static void returnTaskIngot(BuyerTask2 bt) {
		Date now = DateTime.now().toDate();
		SqlSession ss = SessionFactory.getSqlSession();

		try {
			UserIngotRecordMapper ingotMapper = ss.getMapper(UserIngotRecordMapper.class);
			// 获取上一次的金币记录计算结余
			UserIngotRecord lastRecord = ingotMapper.selectLastRecord(bt.buyerId);
			//获取此任务的最后一次的金币扣除（接任务的时候扣除的任务押金）
			int lastIngotRecord = ingotMapper.selectLastRecordAboutThisRecord(bt.buyerId, bt.taskId);
			UserIngotRecord record = UserIngotRecord.newInstance(bt.buyerId, lastRecord).plus(lastIngotRecord)
					.memo("平台撤销任务[" + bt.taskId + "]返还押金").taskId(bt.taskId).createTime(now);
			ingotMapper.insert(record);

			// 更新用户缓存
			User.findByIdWichCache(bt.buyerId).ingot(record.balance).updateCache();
		} catch (Exception e) {
			ss.rollback();
			log.error(e.getMessage(), e);
			throw new RuntimeException("撤销任务操作失败！");
		} finally {
			ss.close();
		}

	}

}
