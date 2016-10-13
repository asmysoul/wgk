CDT = {
	taskCellTemp: null,
	currPageNo: 1,
	pageSize: 20,
	status: 'WAIT_SELLER_CONFIRM_SYS_REFUND',
	module:null,
};
//加载待退款的任务数量
function loadCount() {
	var platform = $('.platform').val(),
	shopId = $('#shop').val(),
	orderId = $.trim($('#orderId').val()),
	buyerAccountNick = $.trim($('#buyerAccountId').val()),
	tenpayNo = $.trim($('#tenpayNo').val());
	Tr.get('/seller/task/count2', {
	'vo.platform': platform,
	'vo.shopId': shopId,
	'vo.taskId': $('#txtTaskId').val(),
	'vo.orderId': orderId,
	'vo.buyerAccountNick': buyerAccountNick,
	'vo.tenpayNo': tenpayNo,
	'vo.status': CDT.status,
	'vo.module':CDT.status
	}, function(data) {
		if (data.code != 200) return;
		$("#waitCheckOrder").text("待检查的返款订单("+data.results.count1+")");
		$("#buyerRejectSys").text("买手驳回的订单("+data.results.count2+")");
		$("#refunding").text("已确认返款的订单("+data.results.count3+")");
	});
}

//加载任务
function loadTask(pageNo) {
	//alert('test');
	loadCount();
	var platform = $('.platform').val(),
		shopId = $('#shop').val(),
		orderId = $.trim($('#orderId').val()),
		buyerAccountNick = $.trim($('#buyerAccountId').val()),
		tenpayNo = $.trim($('#tenpayNo').val());
	Tr.get('/seller/task/listRefund2', {
		'vo.platform': platform,
		'vo.shopId': shopId,
		'vo.taskId': $('#txtTaskId').val(),
		'vo.orderId': orderId,
		'vo.buyerAccountNick': buyerAccountNick,
		'vo.tenpayNo': tenpayNo,
		'vo.pageNo': pageNo,
		'vo.pageSize': CDT.pageSize,
		'vo.status': CDT.status,
		'vo.module':CDT.status
	}, function(data) {
		if (data.code != 200) return;
		if (data.results.length <= 0) {
			$('.taskWrapper').addClass('warnBox').html('<span class="iconfont">&#xf00b6;</span>没有推广!');
			return;
		}
		var output = Mustache.render(CDT.taskCellTemp, $.extend(data, {
			platIcon: function() {
				return 'plat_' + this.platform;
			},
			taskRewardIngot:function(){
				return $.number(this.rewardIngot / 100, 2);
			},
			price: function() {
				return $.number(this.paidFee / 100, 2);
			},
			isWaitRefund: function() {
				return this.status == 'WAIT_SELLER_CONFIRM_SYS_REFUND' || this.status == 'BUYER_REJECT_SYS_REFUND';
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
	Tr.get('/user/shop2/' + platform, {
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

	//单个确认平台返款
	$(document).on('click', '#taskContainer .J_confirmRefund', function() {
		if (!confirm('请仔细检查金额是否正确，确认要退款么？')) {
			return;
		}
		var $tr = $(this).parents('tr');
		var tid = $tr.attr('data-tid');
		var amount = parseFloat($tr.find('.J_refundAmt').val());
		// console.log(tid);
		// console.log(amount);
		Tr.post('/seller/task/confirmSysRefund2', {
			'id': tid,
			'amount': amount
		}, function(data) {
			if (data.code == 800101) {
				alert('填写的退款金额不能大于该单任务发布金额的150%！');
			}
			if(data.code == 8001){
				alert('押金不足以支付超额部分，请充值押金！');
			}
			if(data.code == 400){

				alert('返款金额不能小于0！');
			}

			if (data.code != 200) {
				return;
			}
			alert('确认成功');
			loadTask(1);
		});
	});
	
}
$(function() {

	CDT.taskCellTemp = $('#taskCellTemp').remove().val();
	Mustache.parse(CDT.taskCellTemp);
	
	initBase();
	//填入化待退款的数量
	
	if($('.platform').val().length>0){
		initShop($('.platform').val());
	}
	loadTask(1);
});