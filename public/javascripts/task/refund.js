CDT = {
	taskCellTemp: null,
	currPageNo: 1,
	pageSize: 20,
	status: 'WAIT_REFUND'
};

function loadCount() {
	var platform = $('.platform').val(),
		shopId = $('#shop').val(),
		orderId = $.trim($('#orderId').val()),
		buyerAccountNick = $.trim($('#buyerAccountId').val()),
		tenpayNo = $.trim($('#tenpayNo').val());
	Tr.get('/seller/task/selfCount', {
		'vo.platform': platform,
		'vo.shopId': shopId,
		'vo.taskId': $('#txtTaskId').val(),
		'vo.orderId': orderId,
		'vo.buyerAccountNick': buyerAccountNick,
		'vo.isSysRefundTask':false,
		'vo.tenpayNo': tenpayNo,
		'vo.status': CDT.status
	}, function(data) {
		if (data.code != 200) return;
		$("#waitRefund").text("待退款订单("+data.results.count1+")");
		$("#refunding").text("已退款，待核实订单("+data.results.count2+")");
		$("#finished").text("已完成订单("+data.results.count3+")");
	});
}


//加载任务
function loadTask(pageNo) {
	loadCount();
	var platform = $('.platform').val(),
		shopId = $('#shop').val(),
		orderId = $.trim($('#orderId').val()),
		buyerAccountNick = $.trim($('#buyerAccountId').val()),
		tenpayNo = $.trim($('#tenpayNo').val());
	Tr.get('/seller/task/listRefund', {
		'vo.platform': platform,
		'vo.shopId': shopId,
		'vo.taskId': $('#txtTaskId').val(),
		'vo.orderId': orderId,
		'vo.buyerAccountNick': buyerAccountNick,
		'vo.isSysRefundTask':false,
		'vo.tenpayNo': tenpayNo,
		'vo.pageNo': pageNo,
		'vo.pageSize': CDT.pageSize,
		'vo.status': CDT.status
	}, function(data) {
		if (data.code != 200) return;
		if (data.results.length <= 0) {
			$('.taskWrapper').addClass('warnBox').html('<span class="iconfont">&#xf00b6;</span>没有任务!');
			return;
		}

		var output = Mustache.render(CDT.taskCellTemp, $.extend(data, {
			platIcon: function() {
				return 'plat_' + this.platform;
			},
			price: function() {
				return $.number(this.paidFee / 100, 2);
			},
			isRefund: function() {
				return this.status == 'REFUNDING' ? '' : 'WAIT_REFUND';
				
			},
			isWaitRefund: function() {
				return this.status == 'WAIT_REFUND' ? 'WAIT_REFUND':'';
				
			},
			modifyTimeStr: function() {
				if(!this.modifyTime){
					return '---';
				}
				return new Date(this.modifyTime).Format('yyyy-MM-dd hh:mm');
			}
		}));
		$('#taskContainer').removeClass('warnBox').html(output);
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

//初始化店铺信息
function initShop(platform) {
	$('#shop').find('option').eq(0).nextAll('option').remove();
	Tr.get('/user/shop/' + platform, {
		platform: platform
	}, function(data) {
		if (data.code != 200) {
			return;
		}
		var output = '';
		$.each(data.results, function(i, n) {
			output = output + '<option value="' + n.id + '">' + n.nick + '</option>';
		});
		$('#shop').append(output);
	});
}

function initBase() {
	$(document).on('change', '.platform', function() {
		var $me = $(this),
			platform = $me.val();
		initShop(platform);

	});

	$('#queryTask').click(function() {
		loadTask(1);
	});

	//选择订单类型
	$(document).on('click', '.taskTab', function() {
		var $me = $(this);
		$me.addClass('focus').siblings().removeClass('focus');
		CDT.status = $me.attr('status');
		loadTask(1);
	});

	//确认已退款
	$(document).on('click', '#taskContainer .refundBtn', function() {
		var id = $(this).attr('oldId'),
			transNo = $(this).parent().parent().find('.transNo').val(),
			buyerId = $(this).attr('buyerId');
		if (!transNo) {
			alert('请输入交易号');
			return;
		}
		if (parseFloat(transNo.length) != 28 || isNaN(transNo)) {
			alert('订单号格式错误');
			return;
		}
		if(!confirm('再检查一遍，避免搞错订单，确认要提交吗？')){
			return;
		}

		$refundBtn = $(this);
		Tr.post('/seller/task/confirmRefund', {
			'id': id,
			'transNo': transNo,
			'isSubtraction':false
		}, function(data) {
			if (data.code != 200) return;
			// 隐藏确认按钮，显示OK信息
			$refundBtn.parents('.refound-panel').find('.refundBtnBox').hide().end().find('.okInfoBox').show();
		});
	});
}

$(function() {

	CDT.taskCellTemp = $('#taskCellTemp').remove().val();
	Mustache.parse(CDT.taskCellTemp);

	initBase();
	initShop($('.platform').val());
	loadTask(1);
});