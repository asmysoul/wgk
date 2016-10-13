package vos;

/**
 * 
 * 
 * 撤销任务信息
 * 
 * @author moloch
 * @since  v0.1
 * @created 2014-9-22 下午3:23:24
 */
public class CancledTaskVo {
	public int cancledNum;
	public long cancledPledge;
	public long cancledIngot;

	public boolean isSysRefund;
	
    public static CancledTaskVo newInstance(int cancledNum, long cancledPledge, long cancledIngot) {
        CancledTaskVo vo = new CancledTaskVo();
        vo.cancledNum = cancledNum;
        vo.cancledPledge = cancledPledge;
        vo.cancledIngot = cancledIngot;
        return vo;
    }
}
