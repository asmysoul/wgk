CDT = {
	rowTemp: null,
	tenpayAccountInfoTpl: null,
	bankAccountInfoTpl:null,
	taskCellTemp: null,
	currPageNo: 1,
	pageSize: 20,
	recordId: null
};

//加载提现记录列表
function loadRecord(pageNo) {
	Tr.get('/admin/withdraw/list', {
		'vo.userId': $.trim($('#uid').val()),
		'vo.userNick': $.trim($('#nick').val()),
		'vo.status': $.trim($('#status').val()),
		'vo.isBuyerDeposit':false,
		'vo.pageNo': pageNo,
		'vo.pageSize': CDT.pageSize
	}, function(data) {
		if (data.code != 200) return;
		if (data.results.length <= 0) {
			$('.normTable').hide();
			$('#noMsg').show();
			$('.pagin-btm').hide();
			return;
		}
		var output = Mustache.render(CDT.rowTemp, $.extend(data, {
			role: function() {
				return this.userType == 'SELLER' ? '商家' : '买手';
			},
			withdrawStatus: function() {
				if (this.status == 'WAIT') {
					return '待处理';
				}
				if (this.status == 'FINISHED') {
					return '已完成';
				}
			},
			applyTimeFor: function() {
				return new Date(this.applyTime).Format('yyyy-MM-dd hh:mm:ss');
			},
			amountStr: function(){
				return $.number(this.applyAmount/100,2);
			},
			realAmountStr: function(){
				return $.number(this.amount/100,2);
			},
			hasTradeNo: function() {
				return this.status == 'WAIT' ? false:true;
			},
			fundAccount: function(){
				return 'XXX';
			}
		}));
		$('.normTable').show();
		$('#noMsg').hide();
		$('.pagin-btm').show();
		$('#tbContainer').html(output);
		$('.pagination').pagination(data.totalCount, {
			items_per_page: CDT.pageSize,
			num_display_entries: 5,
			current_page: pageNo,
			num_edge_entries: 2,
			callback: loadRecordCallBack,
			callback_run: false
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

	if(!confirm('再检查一遍，确定要提交么？')){
		return;
	}

	Tr.post('/admin/withdraw/confirm', {
		'id': id,
		'tradeInfo': tradeInfo
	}, function(data) {
		if (data.code == '8001') {
			alert('确认提现失败');
		}
		if (data.code != '200') return;

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
			'uid': userId
		}, function(data) {
			if (data.code != '200') return;

			var output = Mustache.render(CDT.tenpayAccountInfoTpl, $.extend(data, {
				nick: function() {
					return nick;
				},
				role: function() {
					return role=='SELLER'?'商家':'买手';
				},
				amount: function() {
					return amount;
				},
				bank: function() {
					return this.type;
				},
				isTenpay: function() {
					return this.type == 'TENPAY';
				}
			}));
			$('#infoBox').html(output);
			// 清空上次填写的交易信息
			$('#txtTradeInfoOld').val('');
			Tr.popup('fundAccountWnd');
		});
	});
	// [旧版]确认已退款
	$(document).on('click', '#btnConfirmRefundOld', function() {
		confirmWithdrawRefund(CDT.recordId, $('#txtTradeInfoOld').val());
		$('#fundAccountWnd').hide();
	});

	// 确认退款
	$(document).on('click', '#tbContainer .refundBtn', function() {
		var userId = $(this).attr('data-uid');
		var nick = $(this).attr('data-nick');
		var role = $(this).attr('data-role');
		var amount = $(this).attr('data-amount');
		var bank = $(this).attr('data-bank');
		// 设置当前处理的记录ID
		CDT.recordId = $(this).attr('data-id');
		Tr.get('/admin/withdraw/account', {
			'uid': userId
		}, function(data) {
			if (data.code != '200') return;
			var output = Mustache.render(CDT.bankAccountInfoTpl, $.extend(data, {
				bankNick: function() {
					return nick;
				},
				role: function() {
					return role=='SELLER'?'商家':'买手';
				},
				amount: function() {
					return amount;
				},
				bank: function() {
					return this.type;
				},
				isBank: function(){
					return this.type != 'TENPAY' && this.type != 'ALIPAY';
				}
			}));
			$('#bankInfoBox').html(output);

		    // 清空上次填写的交易信息
		    $('#txtTradeInfo').val('');
		    Tr.popup('tradeInfoAddWnd');
		});
	});
	// 确认已退款
	$(document).on('click', '#btnConfirmRefund', function() {
		confirmWithdrawRefund(CDT.recordId, $('#txtTradeInfo').val());
		$('#tradeInfoAddWnd').hide();
	});
	//校验id全为数字
	$('#uid').blur(function(){
		if($('#uid').val()==''){
			return;
		}else{
			if(isNaN($('#uid').val())){
				alert('id格式错误');
			}
		}
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
	
	$("#uploadBuyerDeposit").click(function() {
		if ($("#uploadBuyerDepositFile").val() == '') {
			return alert("请选择上传文件！");
		}
		$("#uploadBuyerDepositForm").submit();
		$('#iframe').show();
		loadRecord(1);
	});
}

$(function() {
	CDT.rowTemp = $('#rowTemp').remove().val();
	Mustache.parse(CDT.rowTemp);

	CDT.tenpayAccountInfoTpl = $('#tenpayAccountInfoTpl').remove().val();
	Mustache.parse(CDT.tenpayAccountInfoTpl);

	CDT.bankAccountInfoTpl = $('#bankAccountInfoTpl').remove().val();
	Mustache.parse(CDT.bankAccountInfoTpl);
	
	CDT.taskCellTemp = $('#taskCellTemp').remove().val();
	Mustache.parse(CDT.taskCellTemp);

	initBaseBind();

	loadRecord(1);
});