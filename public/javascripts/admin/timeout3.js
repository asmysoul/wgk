CDT = {
	recordTemp: null,
	currPageNo:1,
	pageSize:20,
	type:'sllerGood',
	isReward: false ,
	currUid: null ,
	taskIdStr: null
};
function initBase() {
	// 查询用户
	$(document).on('click', '#queryBtn', function() {
		loadtask(1);
	});
	$('.inputDate').datePicker({
		startDate: '2014-01-01',
		endDate: '2020-01-01',
		clickInput: true,
		verticalOffset: 35
	});
	$(document).on('click', '.foundTypeSwitchWrapper .foundTab', function(){
		$(this).addClass('focus').siblings().removeClass('focus');
		CDT.type= $(this).attr('type');
		loadTask(1);
	});
	
	$('.checkTextBtn').click(function() {
		//$(this).parent().children('.selectedCtb').removeClass('selectedCtb');
		$('.checkTextBtn').removeClass('selectedCtb');
		$(this).addClass('selectedCtb');
		CDT.type= $(this).attr('type');
		loadTask(1);
		$('.allTask').addClass('focus').siblings().removeClass('focus');
	});
	
	//默认显示第一个子任务超期记录
	loadTask(1);
	
	//处理各类超时
	$(document).on('click', '#btnConfirm', function() {
		if(CDT.type=='sllerGood') {
			Tr.post('/admin/seller/task/sendGood3', {
				'buyerTask.id': CDT.currUid
			}, function(data) {
				if(data.code !=200) return;
				alert('操作成功');
				$('#confirmWnd').hide();
				loadTask(1);
			}); 
		}else if(CDT.type=='sellerRefund') {
			Tr.post('/admin/buyerTask/processTimeOut3', {
				id:CDT.currUid,
				transNo: '商家返款超时，由平台退还任务垫付本金到平台本金，请查看本金记录',
				isSubtraction:false
			}, function(data) {
				if(data.code !=200) return;
				alert("操作成功！");
				$('#confirmWnd').hide();
				loadTask(1);
			});
		}else if(CDT.type=='sellerRefundPlat') {
			Tr.post('/admin/buyerTask/processTimeOutPlat3', {
				id:CDT.currUid,
				isSubtraction:false
			}, function(data) {
				if(data.code !=200) return;
				alert("操作成功！");
				$('#confirmWnd').hide();
				loadTask(1);
			});
		}else if(CDT.type=='buyerGood') {
			Tr.post('/admin/task/confirmRecvGoods3', {
				//'vo.picUrls': 'http://digg-test.qiniudn.com/o_19lqndfqqb7p1744p8m1p7goas9.jpg',
				'vo.picUrls': 'http://www.tianrenqi.com/public/images/weizhuan.jpg',
				id: CDT.currUid
			}, function(data) {
				if (data.code != 200) return;
				alert("操作成功！");
				$('#confirmWnd').hide();
				loadTask(1);
				//删除该任务数据
				$('.taskCell[taskId="' + id + '"]').remove();
				$.each(App.tasks.data, function(i, n) {
					if (n.id == id) {
						App.tasks.data.splice($.inArray(n, App.tasks.data), 1);
					}
				});
			});
		}else if(CDT.type=='buyerRefund') {
			Tr.post('/admin/buyer/task/confirmRefund3', {
				'bt.id': CDT.currUid,
				'bt.status': 'FINISHED',
				'bt.taskId': CDT.taskIdStr
			}, function(data) {
				if (data.code != 200) return;
				alert("操作成功！");
				$('#confirmWnd').hide();
				loadTask(1);
			});
		}else if(CDT.type=='buyerRefundPlat') {
			Tr.post('/admin/buyer/task/sysRefund/confirm3', {
				'id': CDT.currUid,
				'isReject': false
			}, function(data) {
				if (data.code != 200) {
					return;
				}
				alert("操作成功！");
				$('#confirmWnd').hide();
				loadTask(1);
			});
		}else {
			alert('没有此类型');
		}
	});
	$(document).on('click', '#btnConfirmSubtraction', function() {
		if(CDT.type=='sellerRefund') {
			Tr.post('/admin/buyerTask/processTimeOut3', {
				id:CDT.currUid,
				transNo: '商家返款超时，由平台退还任务垫付本金到平台本金，请查看本金记录',
				isSubtraction:true
			}, function(data) {
				if(data.code !=200) return;
				alert("操作成功！");
				$('#confirmWnd').hide();
				loadTask(1);
			});
		}else if(CDT.type=='sellerRefundPlat') {
			Tr.post('/admin/buyerTask/processTimeOutPlat3', {
				id:CDT.currUid,
				isSubtraction:true
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
				alert("操作成功！");
				$('#confirmWnd').hide();
				loadTask(1);
			});
		}else {
			alert('没有此类型');
		}
	});
	//确认操作弹出框
	$(document).on('click', '.timeOut', function() {
		CDT.currUid = $(this).attr('data-id');
		CDT.taskIdStr = $(this).attr('bid');
		$('#btnConfirmSubtraction').show();
		if(CDT.type != 'sellerRefund' && CDT.type != 'sellerRefundPlat') {
			$('#btnConfirmSubtraction').hide();
		}
		Tr.popup('confirmWnd');
	});
}


//加载任务记录
function loadTask(pageNo) {
	var createTimeStart = ($.trim($('#createTimeStart').val())) ? $.trim($('#createTimeStart').val()) + ' 00:00:00' : '',
	    createTimeEnd = ($.trim($('#createTimeEnd').val())) ? $.trim($('#createTimeEnd').val()) + ' 00:00:00' : '';
	loadCount();
	Tr.get('/admin/buyerTask/listAllTimeout3', {
		'vo.platform': $('#platform').val(),
		'vo.device': $('#device').val(),
		'vo.type': CDT.type,
		'vo.buyerNick': $('#buyerNick').val(),
		'vo.sellerNick': $('#sellerNick').val(),
		'vo.buyerAccountNick': $('#buyerAccount').val(),
		'vo.orderId': $('#orderId').val(),
		'vo.shopName': $.trim($('#shopName').val()),
		'vo.taskId': $.trim($('#taskId').val()),
		'vo.buyerTaskId': $('#buyerTaskId').val(),
		'vo.pageNo': pageNo,
		'vo.pageSize': CDT.pageSize
	}, function(data) {
		if (data.code != 200) return;
		var output = '';
		if (data.results.length <= 0) {
			output = '<div class="iconfont warnBox">&#xf00b6;没有记录!</div>';
		} else {
			output = Mustache.render(CDT.recordTemp, $.extend(data, {
				isPC: function() {
					return this.device == 'PC';
				},
				orderId: function() {
					if (this.orderId) {
						return this.orderId;
					}
					return '----';
				},
				paidFeeStr: function() {
					return $.number(this.paidFee/100, 2);
				},
				rewardIngotStr: function() {
					return $.number(this.rewardIngot/100, 2);
				},
				platIcon: function() {
					return 'plat_' + this.platform;
				},
				modifyTimeStr: function() {
					if (this.modifyTime) {
						return new Date(this.modifyTime).Format('yyyy-MM-dd hh:mm:ss');
					}
					return '----';
				},
				takeTimeStr: function() {
					return new Date(this.takeTime).Format('yyyy-MM-dd hh:mm');
				},
				sellerAdminNameStr:function() {
					return this.sellerAdminName!=null ? "【" + this.sellerAdminName+"】":"";
				},
				buyerAdminNameStr:function() {
					return this.buyerAdminName!=null ? "【" + this.buyerAdminName+"】":"";
				}
			}));
		}
		$('.pagin-btm').show();
		$('#taskContainer').html(output);
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
//各超时状态的任务的数量
function loadCount() {
	var createTimeStart = ($.trim($('#createTimeStart').val())) ? $.trim($('#createTimeStart').val()) + ' 00:00:00' : '',
		createTimeEnd = ($.trim($('#createTimeEnd').val())) ? $.trim($('#createTimeEnd').val()) + ' 00:00:00' : '';
	Tr.get('/admin/buyerTask/listAllTimeoutCount3', {
		'vo.platform': $('#platform').val(),
		'vo.device': $('#device').val(),
		'vo.type': CDT.type,
		'vo.buyerNick': $('#buyerNick').val(),
		'vo.sellerNick': $('#sellerNick').val(),
		'vo.buyerAccountNick': $('#buyerAccount').val(),
		'vo.orderId': $('#orderId').val(),
		'vo.shopName': $.trim($('#shopName').val()),
		'vo.taskId': $.trim($('#taskId').val()),
		'vo.buyerTaskId': $('#buyerTaskId').val(),
	}, function(data) {
		if (data.code != 200) return;
		$('.clearfix a').eq(2).text('商家发货超时(' + data.results.count1 + ')');
		$('.clearfix a').eq(3).text('商家退款超时(' + data.results.count2 + ')');
		$('.clearfix a').eq(4).text('商家退款超时（平台）(' + data.results.count3 + ')');
		$('.clearfix a').eq(5).text('买手收货超时(' + data.results.count4 + ')');
		$('.clearfix a').eq(6).text('买手确认退款超时(' + data.results.count5 + ')');
		$('.clearfix a').eq(7).text('买手确认退款超时（平台）(' + data.results.count6 + ')');
	});
}

function loadTaskCallBack(index, jq) {
	loadTask(index + 1);
}
$(function(){
	CDT.recordTemp = $('#recordTemp').remove().val();
	Mustache.parse(CDT.recordTemp);
	
	initBase();
});
