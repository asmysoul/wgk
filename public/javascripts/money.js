CDT = {
	memberRecordTemp: null,
	pledgeRecordTemp: null,
	withdrawRecordTemp: null,
	flowRecordTemp:null,
	taskCellTemp: null,
	currPageNo: 1,
	pageSize: 20,
	type: 'withdraw',
	isReward: false,
	userType: null
};

function initBase() {
	// 设置押金充值支付方式，金币充值套餐、支付方式的选中样式
	$(document).on('click', '.checkTextBtn', function() {
		$(this).parent().children('.selectedCtb').removeClass('selectedCtb').end().end().addClass('selectedCtb');
	});
	$(document).on('click', '.chargeTypeTab', function() {
		var id_toshow = $(this).addClass('focus').siblings().removeClass('focus').end().attr('for');
		$('.charge-type-panel').hide();
		$('#' + id_toshow).show();
	});
	/* 购买金币套餐：选择套餐后修改显示文案 */
	$(document).on('click', '#ingotSelBox .checkTextBtn', function() {
		$('#ingot').text($(this).find('.package-box').find('p').eq(0).text());
		$('.money').text($(this).find('.package-box').find('p').eq(1).text());
		
	});
	
	$(document).on('click', '#flowSelBox .checkTextBtn', function() {
		$('#folw').text($(this).find('.package-box').find('p').eq(0).text());
		$('.flowMoney').text($(this).find('.package-box').find('p').eq(1).text());
		
	});

	$(document).on('change', '#second', function() {
		if ($(this).val()) {
			var add = $.trim($('#first').val()) + $.trim($('#second').val());
			$('#address').val(add);
			$('.quyu').remove();
			$('#quyu').append('<span class="iconfont quyu" style="color:green">&#xf0156;</span>');
		} else {
			$('#address').val('');
		}
	});

	$(document).on('click', '.fundTab', function() {
		$(this).addClass('focus').siblings().removeClass('focus');
		CDT.type = $(this).attr('type');
		CDT.isReward = $(this).attr('isReward');
		loadMemberRecord(1);
	});
	$(document).on('click', '#recordContainer .J_viewBuyerTask', function() {
		var $me =$(this),
		id = $(this).attr('data-id');
		if(!$me.parents('tr').next('tr').is(':visible')){
			$me.parents('tr').next('tr').show(); 
			Tr.get('/withdraw/listBuyerTask', {
				'id': id
			}, function(data) {
				if (data.code != 200) return;
				var output = Mustache.render(CDT.taskCellTemp, $.extend(data, {
					platIcon:function(){
						return 'plat_' + this.platform;
					},
					paidFeeStr:function(){
						return $.number(this.paidFee/100,2);
					},
					Time:function(){
						return new Date(this.modifyTime).Format('yyyy-MM-dd hh:mm:ss'); 
					}
				}));
				$me.parents('tr').next('tr').find('.task-info').html(output); 
			});       
		}
		else{
			$me.parents('tr').next('tr').hide();
		}
		
	});

	//默认显示第一个资金记录
	loadMemberRecord(1);
}

//加载资金记录
function loadMemberRecord(pageNo) {
	Tr.get('/user/money/record/list', {
		'vo.type': CDT.type,
		'vo.pageNo': pageNo,
		'vo.isReward': CDT.isReward,
		'vo.pageSize': CDT.pageSize
	}, function(data) {
		if (data.code != 200) return;
		var output = '';
		if (CDT.type == 'withdraw') {
			if (data.results.length <= 0) {
				output = '<div class="iconfont warnBox">&#xf00b6;</span>没有记录!';
			} else {
				output = Mustache.render(CDT.withdrawRecordTemp, $.extend(data, {
					applyTimeStr: function() {
						return new Date(this.applyTime).Format('yyyy-MM-dd hh:mm');
					},
					endTimeStr: function() {
						return new Date(this.endTime).Format('yyyy-MM-dd hh:mm');
					},
					amountStr: function() {
						return $.number(this.amount / 100, 2);
					},
					statusStr: function() {
						if (this.status == 'WAIT') {
							return '待处理';
						}
						if (this.status == 'FINISHED') {
							return '已退款';
						}
						if (this.status == 'PROCESSING') {
							return '处理中';
						}
					}
				}));
			}
		}
		if (CDT.type == 'member') {
			if (data.results.length <= 0) {
				output = '<div class="iconfont warnBox">&#xf00b6;没有记录!</div>';
			} else {
				output = Mustache.render(CDT.memberRecordTemp, $.extend(data, {
					createTimeStr: function() {
						return new Date(this.createTime).Format('yyyy-MM-dd hh:mm');
					},
					endTimeStr: function() {
						return new Date(this.endTime).Format('yyyy-MM-dd hh:mm');
					},
					amountStr: function() {
						return $.number(this.amount / 100, 2);
					},
					ignotStr: function() {
						return $.number(this.ignot / 100, 2);
					}
				}));
			}
		}
		if (CDT.type == 'pledge' || CDT.type == 'ingot' || CDT.type == 'premium' || CDT.type == 'deposit') {
			if (data.results.length <= 0) {
				output = '<div class="iconfont warnBox">&#xf00b6;没有记录!</div>';
			} else {
				output = Mustache.render(CDT.pledgeRecordTemp, $.extend(data, {
					createTimeStr: function() {
						return new Date(this.createTime).Format('yyyy-MM-dd hh:mm');
					},
					plusAmountStr: function() {
						if (this.sign == 'PLUS')
							return $.number(this.amount / 100, 2) + '元';
					},
					minusAmountStr: function() {
						if (this.sign == 'MINUS')
							return $.number(this.amount / 100, 2) + '元';
					},
					balanceStr: function() {
						return $.number(this.balance / 100, 2);
					}
				}));
			}
		}
		if(CDT.type=='flow'){
			if (data.results.length <= 0) {
				output = '<div class="iconfont warnBox">&#xf00b6;没有记录!</div>';
			} else {
				output = Mustache.render(CDT.flowRecordTemp, $.extend(data, {
					createTimeStr: function() {
						return new Date(this.createTime).Format('yyyy-MM-dd hh:mm');
					},
					plusAmountStr: function() {
						if (this.sign == 'PLUS')
							return this.amount+"个";
					},
					minusAmountStr: function() {
						if (this.sign == 'MINUS')
							return this.amount+"个";
					},
					balanceStr: function() {
						return this.balance +"个";
					}
				}));
			}
		}
		$('#recordContainer').html(output);
		$('.pagination').pagination(data.totalCount, {
			items_per_page: CDT.pageSize,
			num_display_entries: 5,
			current_page: pageNo,
			num_edge_entries: 2,
			callback: loadMemberRecordCallBack,
			callback_run: false
		});
		CDT.currPageNo = pageNo;
	});
}

function loadMemberRecordCallBack(index, jq) {
	loadMemberRecord(index + 1);
}


$(function() {
	CDT.userType = App.userType;
	CDT.flowRecordTemp = $('#flowRecordTemp').remove().val();
	Mustache.parse(CDT.flowRecordTemp);
	CDT.pledgeRecordTemp = $('#pledgeRecordTemp').remove().val();
	Mustache.parse(CDT.pledgeRecordTemp);
	CDT.memberRecordTemp = $('#memberRecordTemp').remove().val();
	Mustache.parse(CDT.memberRecordTemp);
	CDT.withdrawRecordTemp = $('#withdrawRecordTemp').remove().val();
	Mustache.parse(CDT.withdrawRecordTemp);
	CDT.taskCellTemp = $('#taskCellTemp').remove().val();
	Mustache.parse(CDT.taskCellTemp);
    
	$.extend($.validator.messages, {
		required: Tr.error('必填'),
		minlength: Tr.error('至少{0}个字符'),
		mobile: Tr.error('格式不正确'),
		equalTo: Tr.error('两次输入卡号不相同'),
		maxlength: Tr.error('不能超过{0}个字符'),
		number: Tr.error('请输入合法的数字')
	});
	$.validator.addMethod('alipayNo', function(value) {
		return Tr.regs.alipay.test($('#txtAlipayNo').val());
	}, Tr.error('格式错误！'));

	$.validator.addMethod('tenpayNO', function(value) {
		return Tr.regs.tenpay.test($('#txtTenpayNo').val());
	}, Tr.error('格式错误！'));

	$.validator.addMethod('bankNo', function(value) {
		return Tr.regs.bank.test($('#txtBankNo').val());
	}, Tr.error('格式错误！'));
	$.validator.addMethod('price', function(value) {
		var flag = true;
		if (parseFloat($('#txtWithdrawAmount').val()) > parseFloat($('#pledge').text())) {
			flag = false;
		}
		return flag;
	}, Tr.error('不能超过可提现金额！'));
	$.validator.addMethod('Recharge', function(value) {
		var flag = true;
		if (parseFloat($('#txtChargeVal').val()) < 1) {
			flag = false;
		}
		return flag;
	}, Tr.error('金额不能小于1元！'));


	initBase();



	$(document).on('click', '.pay', function() {
		$(this).find('input').attr('checked', 'checked');
	});
	// 切换资金操作状态
	$(document).on('click', '.withdrawOpTab', function() {
		var id_toshow = $(this).addClass('focus').siblings().removeClass('focus').end().attr('for');
		$('.withdraw-setting-panel').hide();
		$('#' + id_toshow).show();
	});
	//网银支付弹窗支付结果的两个方式
	$('#btnbankpay').click(function() {
		$('#bankPayment').hide();
		location.href = '/user';
	});
	$('#btnpayerror').click(function() {
		$('#bankPayment').hide();
	});
	var validator2 = $('#fm0').validate({
		onkeyup: false,
		rules: {
			'ChargeVal': {
				number: true,
				Recharge: true
			}
		}
	});
	//充值按钮
	$(document).on('click', '.btnCharge', function() {
		var amount, platform;

		// 卖家充值押金
		var tradeType = $(this).attr('data-value');
		if (tradeType == 'PLEDGE') {
			if (!validator2.form()) {
				return;
			}
			amount = $('#txtChargeVal').val();
			platform = $('#pledgePaySelBox .bank:checked').val();
		} else {
			// 卖家、买手充值金币
			amount = $('#ingotSelBox .checkTextBtn.selectedCtb').attr('data-value');
			platform = $('#ingotPaySelBox .bank:checked').val();
		}
		if (!confirm('确认支付？')) {
			return;
		}
		var win = window.open(),
			openDtf = function(url) {
				setTimeout(function() {
					win.location = url;
				}, 200);
			};
		Tr.post('/user/money/recharge', {
			platform: platform,
			amount: amount,
			type: tradeType
		}, function(data) {
			if (data.code == 800101 || data.code == 8001) alert(data.msg);
			if (data.code != 200) return;
			// 弹出模态窗口：支付成功 or 支付遇到问题
			var url = '/user/pay' + '?p=' + platform + '&id=' + data.results + '&type=' + tradeType;
			Tr.popup('bankPayment');
			openDtf(url);
		});
	});
	//充值流量
	$("#btnChargeFlow").click(function(){
		var amount = $('#flowSelBox .checkTextBtn.selectedCtb').attr('data-value');
		var useIngot = false,usePledge = false;
		if($('#payType .ingot').attr('checked')){
			useIngot = true;
		}

		if($('#payType .pledge').attr('checked')){
			usePledge = true;
		}

		var tip="确认支付"+amount+"金币购买流量吗?"
		if (!confirm(tip)) {
			return;
		}
		Tr.post('/user/money/rechargeFlow', {
			amount: amount,
			useIngot:useIngot,
			usePledge:usePledge
		}, function(data) {
			if (data.code == 800101 || data.code == 8001) alert(data.msg);
			if (data.code != 200) return;
			alert("充值成功！");
		});
	});
});