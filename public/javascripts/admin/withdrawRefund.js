CDT = {
	rowTemp: null,
	accountInfoTpl: null,
	currPageNo: 1,
	pageSize: 20
};

//加载提现记录列表
function loadRecord(pageNo) {
	Tr.get('/admin/withdraw/list', {
		'vo.userId': $.trim($('#uid').val()),
		'vo.userNick': $.trim($('#nick').val()),
		'vo.status': $.trim($('#status').val()),
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
		$('#accountContainer').html(output);
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


function initBaseBind() {
	// 查询用户
	$(document).on('click', '#queryBtn', function() {
		loadRecord(1);
	});

	// 查看资金账户
	$(document).on('click', '.btnViewAccount', function() {
		var userId = $(this).attr('data-uid');
		var nick = $(this).attr('data-nick');
		var role = $(this).attr('data-role');
		var amount = $(this).attr('data-amount');
		var bank = $(this).attr('data-bank');
		
		if(role == 'SELLER'){
			window.top.location.replace('/admin/withdraw/refund?uid=' + userId);
			return;
		}
		Tr.get('/admin/withdraw/account', {
			'uid': userId
		}, function(data) {
			if (data.code != '200') return;

			var output = Mustache.render(CDT.accountInfoTpl, $.extend(data, {
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
					return bank;
				}
			}));
			$('#infoBox').html(output);
			$('#fundAccountWnd').show();
		});
	});
	// 关闭资金账户窗口
	$(document).on('click', '#btnCloseWnd', function() {
		$('#fundAccountWnd').hide();
	});


	// 提现处理
	$(document).on('click', '.btnWithdraw', function(){
		var $me = $(this),
			tradeNo = $.trim($me.parents('td').prev('td').find('input').val()),
			id = $me.attr('recordId');
			if(tradeNo==''){
				alert('交易号不能为空');
				return;
			}
		Tr.post('/admin/withdraw/confirm', {
			'id': id,
			'tradeNo': tradeNo
		},function(data){
			if(data.code == '8001'){
				alert('确认提现失败');
				return;
			}
			if(data.code != '200')return;
			alert('提现处理成功！');
			loadRecord(1);
		});
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
	
}

$(function() {
	CDT.rowTemp = $('#rowTemp').remove().val();
	Mustache.parse(CDT.rowTemp);

	CDT.accountInfoTpl = $('#accountInfoTpl').remove().val();
	Mustache.parse(CDT.accountInfoTpl);
	

	initBaseBind();

	// loadRecord(1);
});