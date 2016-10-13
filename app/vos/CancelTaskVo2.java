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
public class CancelTaskVo2 {
	public int cancledNum;
	public long cancledIngot;

	public boolean isSysRefund;
	
    public static CancelTaskVo2 newInstance(int cancledNum, long cancledIngot) {
        CancelTaskVo2 vo = new CancelTaskVo2();
        vo.cancledNum = cancledNum;
        vo.cancledIngot = cancledIngot;
        return vo;
    }
}
