package vos;

import java.util.Date;

import models.User.UserType;
import models.UserWithdrawRecord.WithdrawStatus;
import enums.pay.PayPlatform;

public class UserWithdrawRecordVo extends Page {
	public Long id;
	public Long userId;
	public Long amount;
	public Long realAmount;
	public String tradeNo;
	public WithdrawStatus status;
	public String memo;
	public Date applyTime;
	public Date modifyTime;

	public UserType type;
	public String sellerNick;
	public PayPlatform payPlatform;
	public String bankName;
	public String no;

	public void value() {
		this.bankName = this.payPlatform.title;
		if (type == UserType.BUYER) {
			this.realAmount = this.amount * 95 / 100;

		} else if (type == UserType.SELLER) {
			this.realAmount = this.amount * 997 / 1000;
		}
	}
}
