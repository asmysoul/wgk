CDT = {
	taskCellTemp: null,
	currPageNo: 1,
	pageSize: 20,
	status:null
};

function loadCount() {
	var platform = $('#platform').val(),
		orderId = $('#orderId').val(),
		nick = $('#buyerAccountNick').val();
	Tr.get('/buyer/tasks/count', {
		'vo.platform': platform,
		'vo.orderId': orderId,
		'vo.buyerAccountNick': nick,
		'vo.status': CDT.status,
		'vo.module':CDT.status,
	}, function(data) {
		if (data.code != 200) return;
		$("#waitBuyerConfirmSysRefund").text("待确认返款订单("+data.results.count1+")");
		$("#waitBuyerConfirmEditedSysRefund").text("商家修改过返款本金的订单("+data.results.count2+")");
		$("#waitSysRefund").text("已确认返款订单("+data.results.count3+")");
	});
}
//加载任务
function loadTask(pageNo) {
	loadCount();
	var platform = $('#platform').val(),
		orderId = $('#orderId').val(),
		nick = $('#buyerAccountNick').val();
	Tr.get('/buyer/tasks/listRefund', {
		'vo.platform': platform,
		'vo.orderId': orderId,
		'vo.buyerAccountNick': nick,
		'vo.status': CDT.status,
		'vo.module':CDT.status,
		'vo.pageNo': pageNo,
		'vo.pageSize': CDT.pageSize
	}, function(data) {
		if (data.code != 200) {
			return;
		}
		//todo
		if (data.results.length <= 0) {
			$('#taskContainer').addClass('warnBox').html('<span class="iconfont">&#xf00b6;</span>没有任务!');
			return;
		}
		var output = Mustache.render(CDT.taskCellTemp, $.extend(data, {
			platIcon: function() {
				return 'plat_' + this.platform;
			},
			taskRewardIngot:function(){
				return $.number(this.rewardIngot / 100, 2);
			},
			paidFeeFor: function() {
				return $.number(this.paidFee / 100, 2);
			},
			rewardIngotFor: function() {
				return $.number(this.rewardIngot / 100, 2);
			},
			isConfirmed: function() {
				return this.status == 'WAIT_SYS_REFUND';
			}
		}));
		$('#taskContainer').removeClass('warnBox').html(output);
		loadButtomData();
		$('.pagination').pagination(data.totalCount, {
			items_per_page: CDT.pageSize,
			num_display_entries: 5,
			current_page: pageNo,
			num_edge_entries: 2,
			callback: loadTaskCallBack,
			callback_run: false
		});
		CDT.currPageNo = pageNo;
	});
}

function loadTaskCallBack(index, jq) {
	loadTask(index + 1);
}

//列表底端统计信息渲染
function loadButtomData() {
	var inputs = $('#taskContainer .inputCheckBox-checked'),
		paidFeeCount = 0,
		rewardIngotCount = 0,
		count = 0;
	$.each(inputs, function(i) {
		paidFeeCount = paidFeeCount + parseInt(inputs.eq(i).attr('paidFee'));
		rewardIngotCount = rewardIngotCount + parseInt(inputs.eq(i).attr('rewardIngot'));
		count++;
	});

	$('#taskCount').text(count);
	$('#paidFeeCount').text($.number(paidFeeCount / 100, 2));
	$('#rewardIngotCount').text($.number(rewardIngotCount / 100, 2));
}

function confirmSysRefundAmt(id, reject) {
	Tr.post('/buyer/task/sysRefund/confirm', {
		'id': id,
		'isReject': reject
	}, function(data) {
		if (data.code != 200) {
			return;
		}
		alert('操作成功！');
		loadTask(1);
	});
}

function initBase() {
	CDT.status="WAIT_BUYER_CONFIRM_SYS_REFUND";
	$('#platform').val(App.currplatform);

	//查询
	$('#queryTask').click(function() {
		CDT.status="WAIT_BUYER_CONFIRM_SYS_REFUND";
		loadTask(1);
	});

	//选择订单类型
	$(document).on('click', '.taskTab', function() {
		var $me = $(this);
		$me.addClass('focus').siblings().removeClass('focus');
		CDT.status = $me.attr('status');
		loadTask(1);
	});

	// 确认返款金额
	$(document).on('click', '#taskContainer .J_confirm', function() {
		if(!confirm('请仔细检查返款金额，确认要提交？')){
			return;
		}
		var id = $(this).parent().attr('data-id');
		confirmSysRefundAmt(id, false);
	});
	// 驳回待确认返款的订单
	$(document).on('click', '#taskContainer .J_reject', function() {
		if(!confirm('确认要进行驳回操作？')){
			return;
		}
		var id = $(this).parent().attr('data-id');
		confirmSysRefundAmt(id, true);
	});
	// 撤销已确认返款的订单
	$(document).on('click', '#taskContainer .J_cancel', function() {
		if (!confirm('确认要进行撤销操作？')) {
			return;
		}
		var id = $(this).parent().attr('data-id');
		Tr.post('/buyer/task/sysRefund/cancel', {
			'id': id
		}, function(data) {
			if (data.code != 200) {
				return;
			}
			alert('操作成功！');
			loadTask(1);
		});
	});

	//批量核实
	$('#btnConfirmAll').click(function() {
		var ids = [],
			inputs = $('#taskContainer .inputCheckBox-checked');
		$.each(inputs, function(i) {
			ids.push(inputs.eq(i).attr('tid'));
		});
		if (ids.length <= 0) {
			alert('请选择要核实的任务');
			return;
		}
		Tr.post('/buyer/task/batchCheckRefund', {
			'bt.ids': ids
		}, function(data) {
			if (data.code != 200) {
				return;
			}
			alert('批量核实成功');
			loadTask(1);
		});
	});
}

//渲染底部退款账号
function loadAccount() {
	Tr.get('/user/money/fundAccount/get', {
		'account.type': 'TENPAY'
	}, function(data) {
		if (data.code != 200) return;
		$('#account').text(data.results.no);
	});
}

$(function() {
	$(document).on('click', '.taskWrapper .inputCheckBox', function() {
		if ($(this).hasClass('inputCheckBox-checked')) {
			$(this).removeClass('inputCheckBox-checked').parents('.taskCell').removeClass('chosen');
			return;
		}
		$(this).addClass('inputCheckBox-checked').parents('.taskCell').addClass('chosen');
	});

	$('.lnkChoseAll').click(function() {
		if ($(this).hasClass('inputCheckBox-checked')) {
			$('.taskWrapper .inputCheckBox').removeClass('inputCheckBox-checked').parents('.taskCell').removeClass('chosen');
			$(this).removeClass('inputCheckBox-checked');
			return;
		}
		$('.taskWrapper .inputCheckBox').addClass('inputCheckBox-checked').parents('.taskCell').addClass('chosen');
		$(this).addClass('inputCheckBox-checked');
	});

	CDT.taskCellTemp = $('#taskCellTemp').remove().val();
	Mustache.parse(CDT.taskCellTemp);
	initBase();
	loadAccount();

	loadTask(1);
});