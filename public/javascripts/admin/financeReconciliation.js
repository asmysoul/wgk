CDT={
	orderTemp:null,
	orderTemp1: null,
	orderTemp2:null,
	currPageNo: 1,
	pageSize: 7,
};

//加载订单
function loadLog(pageNo){
	Tr.get('/admin/financeReconciliation/list', {
		'vo.pageNo': pageNo,
		'vo.pageSize': CDT.pageSize
	}, function(data) {
		if(data.code != 200) return;
		if (data.results.length <= 0) {
			$('.normTable').hide();
			$('#noMsg').show();
			$('.pagin-btm').hide();
			return;
		}
		var output = Mustache.render(CDT.orderTemp, $.extend(data, {
			dates:function(){
				return new Date(this.date).Format('yyyy-MM-dd');
			},
			amountStr:function() {
				return $.number(this.amount / 100, 2) + '元';
			},
			platAmountStr:function() {
				return $.number(this.platAmount / 100, 2) + '元';
			},
			adminAmountStr:function() {
				return $.number(this.adminAmount / 100, 2) + '元';
			}
		}));
		$('.normTable').show();
		$('#noMsg').hide();
		$('.pagin-btm').show();
		$('#orderContainer').html(output);
		$('.pagination').pagination(data.totalCount, {
			items_per_page: CDT.pageSize,
			num_display_entries: 5,
			current_page: pageNo,
			num_edge_entries: 2,
			callback: loadLogCallBack,
			callback_run: false
		});
		CDT.currPageNo = pageNo;
	});
}

//后台资金收取记录
$(document).on('click', '.adminAmount', function() {
	var date=$(this).attr("data-time");
	Tr.get('/admin/financeReconciliation/adminAmountList', {
		'date':date
	}, function(data) {
		if(data.code != 200) return;
		if (data.results.length <= 0) {
			$(".normTable1").hide();
			return;
		}
		var output = Mustache.render(CDT.orderTemp2, $.extend(data, {
			dates:function(){
				return new Date(this.createTime).Format('yyyy-MM-dd hh:mm:ss');
			},
			amountStr:function() {
				return $.number(this.amount / 100, 2) + '元';
			},
			typeStr:function() {
				if (this.type == 'BUYER_INGOT') {
					return '买手金币充值';
				}
				if (this.status == 'SELLER_INGOT') {
					return '商家金币充值';
				}
				if (this.status == 'SELLER_PLEDGE') {
					return '商家押金充值';
				}
				return '买手押金充值';
			},
			userTypeStr:function() {
				if (this.userType == 'BUYER') {
					return '买手';
				}
				return '商家';
			}
		}));
		$('.normTable1').show();
		$('#orderContainer1').html(output);
	});
});

//平台资金收取记录
$(document).on('click', '.platAmount', function() {
	var date=$(this).attr("data-time");
	Tr.get('/admin/financeReconciliation/payAmountList', {
		'date':date
	}, function(data) {
		if(data.code != 200) return;
		if (data.results.length <= 0) {
			$(".normTable1").hide();
			return;
		}
		var output = Mustache.render(CDT.orderTemp1, $.extend(data, {
			dates:function(){
				return new Date(this.modifyTime).Format('yyyy-MM-dd hh:mm:ss');
			},
			amountStr:function() {
				return $.number(this.amount / 100, 2) + '元';
			},
			feeStr:function() {
				return $.number(this.fee / 100, 2) + '元';
			},
			typeStr:function() {
				if (this.type == 'MEMBER') {
					return '会员开通、续费';
				}
				if (this.type == 'PLEDGE') {
					return '【卖家】充值押金';
				}
				if (this.type == 'INGOT') {
					return '充值购买金币';
				}
				if (this.type == 'DEPOSIT') {
					return '本金';
				}
				return '任务';
			},
			userTypeStr:function() {
				if (this.userType == 'BUYER') {
					return '买手';
				}
				return '商家';
			}
		}));
		$('.normTable1').show();
		$('#orderContainer1').html(output);
	});
});

function loadLogCallBack(index, jq) {
    loadLog(index + 1);
}

$(function() {
	CDT.orderTemp = $('#orderTemp').remove().val();
	Mustache.parse(CDT.orderTemp);
	CDT.orderTemp1 = $('#orderTemp1').remove().val();
	Mustache.parse(CDT.orderTemp1);
	CDT.orderTemp2 = $('#orderTemp2').remove().val();
	Mustache.parse(CDT.orderTemp1);
	loadLog(1);
});
