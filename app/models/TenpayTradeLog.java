package models;

import java.util.Date;

import models.mappers.TenpayTradeLogMapper;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aton.db.SessionFactory;
import com.aton.util.TenpayUtil;

/**
 * 
 * 财付通交易记录.
 * 
 * @author youblade
 * @since 0.1
 * @created 2014年9月10日 下午3:12:30
 */
public class TenpayTradeLog {

    //TODO 配置log单独输出到指定文件
    public static final Logger log = LoggerFactory.getLogger(TenpayTradeLog.class);

    public static final String TABLE_NAME = "tenpay_trade_log";

    public Long id;
    /**
     * 商户订单号
     */
    public String outTradeNo;
    /**
     * 本地业务记录的ID
     */
    public Long bizId;
    /**
     * 交易金额：精确到分
     */
    public Long amount;
    public Long userId;

    public TradeType type;

    @Deprecated
    public enum TradeType {
        TASK, PLEDGE, MEMBER, INGOT
    }

    public String memo;
    public TradeResult result;

    @Deprecated
    public enum TradeResult {
        /** 成功 */
        OK,
        /** 失败 */
        FAIL,
        /** 未处理 */
        UNTREATED
    }

    public Date createTime;
    public Date modifyTime;

    /**
     * 
     * TODO 保存/更新交易记录.
     * 
     * @since 0.1
     * @author youblade
     * @created 2014年9月10日 下午3:15:59
     */
    public void save() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            TenpayTradeLogMapper mapper = ss.getMapper(TenpayTradeLogMapper.class);
            this.modifyTime = DateTime.now().toDate();

            // 更新记录
            if (this.id != null && this.id > 0) {
                mapper.updateById(this);
                return;
            }

            // 创建新记录
            this.outTradeNo = TenpayUtil.createOutTradeNo();
            this.createTime = modifyTime;
            mapper.insert(this);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("[财付通]交易记录保存失败~~");
        } finally {
            ss.close();
        }
    }

    /**
     * 
     * 查询记录.
     * 
     * @param out_trade_no
     * @return
     * @since 0.1
     * @author youblade
     * @created 2014年9月11日 下午7:18:26
     */
    public static TenpayTradeLog findById(String out_trade_no) {
        SqlSession ss = SessionFactory.getSqlSessionForBatch();
        try {
            TenpayTradeLogMapper mapper = ss.getMapper(TenpayTradeLogMapper.class);
            return mapper.selectById(Long.valueOf(out_trade_no));
        } finally {
            ss.close();
        }
    }
    
    /**
     * 
     * 根据"外部交易号"查询记录.
     *
     * @param outTradeNo
     * @return
     * @since  0.1
     * @author youblade
     * @created 2014年9月17日 下午7:01:41
     */
    public static TenpayTradeLog findByOutTradeNo(String outTradeNo) {
        SqlSession ss = SessionFactory.getSqlSessionForBatch();
        try {
            TenpayTradeLogMapper mapper = ss.getMapper(TenpayTradeLogMapper.class);
            return mapper.selectByField("out_trade_no", outTradeNo);
        } finally {
            ss.close();
        }
    }
    
    public static TenpayTradeLog newInstance(TradeType type, String bizId) {
        TenpayTradeLog log = new TenpayTradeLog();
        log.type = type;
        if(StringUtils.isNotBlank(bizId)){
            log.bizId = Long.valueOf(bizId);
        }
        log.result = TradeResult.UNTREATED;
        return log;
    }

    public static TenpayTradeLog instance(String id) {
        TenpayTradeLog log = new TenpayTradeLog();
        if (StringUtils.isNotBlank(id)) {
            log.id = Long.valueOf(id);
        }
        return log;
    }

    public TenpayTradeLog userId(long userId) {
        this.userId = userId;
        return this;
    }

    public TenpayTradeLog amount(long amount) {
        this.amount = amount;
        return this;
    }

    public TenpayTradeLog type(TradeType type) {
        this.type = type;
        return this;
    }

    public TenpayTradeLog result(TradeResult result) {
        this.result = result;
        return this;
    }

}
