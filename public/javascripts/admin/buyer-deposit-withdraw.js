CDT = {
	rowTemp : null,
	accountInfoTpl : null,
	taskCellTemp : null,
	currPageNo : 1,
	pageSize : 20,
	recordId : null
};

// 加载提现记录列表
function loadRecord(pageNo) {
	Tr.get('/admin/withdraw/list', {
		'vo.id':$.trim($("#recordId").val()),
		'vo.userId' : $.trim($('#uid').val()),
		'vo.userNick' : $.trim($('#nick').val()),
		'vo.status' : $.trim($('#status').val()),
		'vo.sellerNick' : $.trim($('#sellerNick').val()),
		'vo.buyerAccountNick' : $.trim($('#buyerAccountNick').val()),
		'vo.taskId' : $.trim($('#taskId').val()),
		'vo.buyerTaskId' : $.trim($('#buyerTaskId').val()),
		'vo.applyTimeStart' : $.trim($('#applyTimeStart').val()),
		'vo.applyTimeEnd' : $.trim($('#applyTimeEnd').val()),
		'vo.isBuyerDeposit' : true,
		'vo.pageNo' : pageNo,
		'vo.pageSize' : CDT.pageSize
	}, function(data) {
		if (data.code != 200)
			return;
		if (data.results.length <= 0) {
			$('.normTable').hide();
			$('#noMsg').show();
			$('.pagin-btm').hide();
			$('#sumAccount').number(0, 2);
			return;
		}
		var output = Mustache.render(CDT.rowTemp, $.extend(data, {
			role : function() {
				return this.userType == 'SELLER' ? '商家' : '买手';
			},
			withdrawStatus : function() {
				if (this.status == 'WAIT') {
					return '待处理';
				}
				if (this.status == 'FINISHED') {
					return '已退款';
				}
				if (this.status == 'PROCESSING') {
					return '处理中';
				}
			},
			applyTimeFor : function() {
				return new Date(this.applyTime).Format('yyyy-MM-dd hh:mm:ss');
			},
			amountStr : function() {
				return $.number(this.applyAmount / 100, 2);
			},
			realAmountStr : function() {
				return $.number(this.amount / 100, 2);
			},
			hasTradeNo : function() {
				return this.status == 'WAIT' ? false : true;
			},
			fundAccount : function() {
				return 'XXX';
			},
			isShowReset:function(){
				if (this.status == 'WAIT') {
					return false;
				}
				return true;
			}
		}));
		$('.normTable').show();
		$('#noMsg').hide();
		$('.pagin-btm').show();
		$('#tbContainer').html(output);
		var realAmount = 0;
		$('.realAmountStr:visible').each(function() {
			var realAmountStr = parseFloat($(this).attr('data-amount'), 10);
			realAmount += realAmountStr;
		});
		$('#sumAccount').number(realAmount, 2);
		$('.pagination').pagination(data.totalCount, {
			items_per_page : CDT.pageSize,
			num_display_entries : 5,
			current_page : pageNo,
			num_edge_entries : 2,
			callback : loadRecordCallBack,
			callback_run : false
		});
		CDT.currPageNo = pageNo;
	});
}

function loadRecordCallBack(index, jq) {
	loadRecord(index + 1);
}

// 确认提现退款
function confirmWithdrawRefund(id, tradeInfo) {
	if ($.trim(tradeInfo).length <= 0) {
		alert('交易信息不能为空！');
		return;
	}

	if ($.trim(tradeInfo).length > 50) {
		alert('交易信息字数不能超过50！');
		return;
	}
	if (!confirm('再检查一遍，确定要提交么？')) {
		return;
	}

	Tr.post('/admin/withdraw/confirm', {
		'id' : id,
		'tradeInfo' : tradeInfo
	}, function(data) {
		if (data.code == '8001') {
			alert('确认提现失败');
		}
		if (data.code != '200')
			return;

		alert('提现处理成功！');
		loadRecord(1);
	});
}

function initBaseBind() {
	// 查询用户
	$(document).on('click', '#queryBtn', function() {
		loadRecord(1);
	});

	// [旧版]查看资金账户并退款
	$(document).on('click', '#tbContainer .btnViewAccount', function() {
		// 设置当前处理的记录ID
		CDT.recordId = $(this).attr('data-id');

		var userId = $(this).attr('data-uid');
		var nick = $(this).attr('data-nick');
		var role = $(this).attr('data-role');
		var amount = $(this).attr('data-amount');
		var bank = $(this).attr('data-bank');

		Tr.get('/admin/withdraw/account', {
			'uid' : userId
		}, function(data) {
			if (data.code != '200')
				return;

			var output = Mustache.render(CDT.accountInfoTpl, $.extend(data, {
				nick : function() {
					return nick;
				},
				role : function() {
					return role == 'SELLER' ? '商家' : '买手';
				},
				amount : function() {
					return amount;
				},
				bank : function() {
					return this.type;
				},
				isBank : function() {
					return this.type != 'TENPAY' && this.type != 'ALIPAY';
				}
			}));
			$('#infoBox').html(output);
			// 清空上次填写的交易信息
			$('#txtTradeInfoOld').val('财付通：');
			$('#fundAccountWnd').show();
		});
	});
	// [旧版]确认已退款
	$(document).on('click', '#btnConfirmRefundOld', function() {
		confirmWithdrawRefund(CDT.recordId, $('#txtTradeInfoOld').val());
		$('#fundAccountWnd').hide();
	});

	// 确认退款
	$(document).on('click', '#tbContainer .refundBtn', function() {
		// 设置当前处理的记录ID
		CDT.recordId = $(this).attr('data-id');
		// 清空上次填写的交易信息
		$('#txtTradeInfo').val('');
		$('#tradeInfoAddWnd').show();
	});
	// 确认已退款
	$(document).on('click', '#btnConfirmRefund', function() {
		confirmWithdrawRefund(CDT.recordId, $('#txtTradeInfo').val());
		$('#tradeInfoAddWnd').hide();
	});

	// 买手本金提现时点击查看任务列表
	$(document)
			.on(
					'click',
					'#tbContainer .J_viewBuyerTask',
					function() {
						var $me = $(this), id = $(this).attr('data-id');
						if (!$me.parents('tr').next('tr').is(':visible')) {
							$me.parents('tr').next('tr').show();
							Tr
									.get(
											'/admin/withdraw/listBuyerTask',
											{
												'id' : id
											},
											function(data) {
												if (data.code != 200)
													return;
												var output = Mustache
														.render(
																CDT.taskCellTemp,
																$
																		.extend(
																				data,
																				{
																					platIcon : function() {
																						return 'plat_' + this.platform;
																					},
																					paidFeeStr : function() {
																						return $
																								.number(
																										this.paidFee / 100,
																										2);
																					},
																					Time : function() {
																						return new Date(
																								this.modifyTime)
																								.Format('yyyy-MM-dd hh:mm:ss');
																					}
																				}));
												$me.parents('tr').next('tr')
														.find('.task-info')
														.html(output);
											});
						} else {
							$me.parents('tr').next('tr').hide();
						}
					});

	// 校验id全为数字
	$('#uid').blur(function() {
		if ($('#uid').val() == '') {
			return;
		} else {
			if (isNaN($('#uid').val())) {
				alert('id格式错误');
			}
		}
	});

	$("#uploadBuyerDeposit").click(function() {
		if ($("#uploadBuyerDepositFile").val() == '') {
			return alert("请选择上传文件！");
		}
		$("#uploadBuyerDepositForm").submit();
		$('#iframe').show();
		loadRecord(1);
	});
	// 重置状态按钮
	$(document).on('click', '#tbContainer .resetStatusBtn', function() {
		// 设置当前处理的记录ID
		if (confirm("确定要重置到待处理状态吗?")) {
			var id = $(this).parent().attr('data-id');
			Tr.get('/admin/resetWithdrawal', {
				'id' : id
			}, function(data) {
				if (data.code != '200') {
					alert(data.msg);
					return;
				}
				loadRecord(1);
			});
		}
	});

}

$(function() {
	CDT.rowTemp = $('#rowTemp').remove().val();
	Mustache.parse(CDT.rowTemp);

	CDT.accountInfoTpl = $('#accountInfoTpl').remove().val();
	Mustache.parse(CDT.accountInfoTpl);

	CDT.taskCellTemp = $('#taskCellTemp').remove().val();
	Mustache.parse(CDT.taskCellTemp);

	initBaseBind();

	loadRecord(1);
});