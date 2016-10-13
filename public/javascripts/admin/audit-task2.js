CDT = {
	taskTemp: null,
	taskDetailTemp: null,
	taskDetailEmptyTemp: null,
	currPageNo: 1,
	pageSize: 20,
	token: '',
	memo: ''
};

//加载任务
function loadTask(pageNo) {
	var platform = $('#platform').val(),
		shopName = $('#shopName').val(),
		nick = $('#nick').val(),
		status = $('.taskTypeTab.focus').attr('status');
	Tr.get('/admin/sellerTask/exmine/list2', {
		'vo.platform': platform,
		'vo.sellerNick': nick,
		'vo.shopName': shopName,
		'vo.pageNo': CDT.currPageNo,
		'vo.pageSize': CDT.pageSize,
		'vo.status': status
	}, function(data) {
		if (data.code == 8001) {
			alert('该功能暂不支持您目前所使用的浏览器！');
		}
		if (data.code != 200) return;
		if (data.results.length <= 0) {
			$('.normTable').hide();
			$('.pagin-btm').hide();
			$('#noMsg').show();
			return;
		}
		var output = Mustache.render(CDT.taskTemp, $.extend(data, {
			platIcon: function() {
				return 'plat_' + this.platform;
			},
			createTimeStr: function() {
				return new Date(this.createTime).Format('yyyy-MM-dd hh:mm');
			},
			nameStr:function() {
				return this.name!=null ? "【" + this.name+"】":"";
			}
		}));
		$('.normTable').show();
		$('#noMsg').hide();
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

















function loadTaskCallBack(index, jq) {
	loadTask(index + 1);
}

//加载任务详情
function loadTaskDetail(tid) {
	Tr.get('/admin/taskDetail2', {
		id: tid
	}, function(data) {
		var output = Mustache.render(CDT.taskDetailTemp, $.extend(data,{
			platformStr: function() {
				return this.platformTitle;
			},
			typeStr: function(){
				if(this.type=='TOUPIAO'){
					return "投票";
				}
				if(this.type=='QUNFA'){
					return "群发";
				}
				return '朋友圈';
			},
			isSubwayItem: function(){
				return this.type=='PENGYOUQUAN';
			},
			pcOrderNum: function(){
				return this.totalOrderNum - this.mobileOrderNum;
			},
			hasExtraRewardIngot: function(){
				return this.extraRewardIngot ==null ? false:true;
			},
			itemPriceStr: function(){
				return this.itemPrice == null ? 0.00:$.number(this.itemPrice/100,2);
			},
			hasSpeedTaskIngot: function(){
				return this.speedTaskIngot == null ? false:true;
			},
			hasBuyTimeInterval: function() {
				return this.buyTimeInterval == null ? false:true;
			},
			hasitemDisplayPrice: function() {
				return this.itemDisplayPrice == null ?false:true;
			},
			sysRefundFee: function(){
				return $.number((this.itemPrice * this.totalOrderNum * this.itemBuyNum * 0.6)/10000,2);
			},
			itemPerFee: function(){
				return this.itemPrice * this.itemBuyNum/100;
			},expressIngotStr:function(){
				return this.totalOrderNum*this.expressIngot/100;
			},hasExpressIngot:function(){
				return this.expressIngot/100;
			},itemWeigth:function(){
				return this.expressWeight == null ? 0.00:$.number(this.expressWeight/100,2);
			},subwayPic:function(){
				return this.itemSubwayPic==null?"":this.itemSubwayPic.split(",");
			},commentPic:function(){
				return this.goodCommentImg==null?"":this.goodCommentImg.split(",");
			}
		}));
		$('#taskDetail').html(output);
	});
}














//更新任务状态
function updateStatus(id, status) {
	Tr.post('/admin/sellerTask/exmine2', {
		't.id': id,
		't.status': status,
		'memo': CDT.memo,
		authenticityToken: CDT.token
	}, function(data) {
		$('.centerbtn').remove();
		if (data.code != 200) {
			return;
		}
		$('#queryBtn').removeClass('disabled').addClass('queryBtn');
		loadTask(1);
		$('#taskDetail').html(CDT.taskDetailEmptyTemp);
		alert('审核成功');
	});
}













function initBase() {
	var output=$('#reasonShower').html();
	$(document).on('click', '.queryBtn', function() {
		loadTask(1);
	});

	
	
	//开始审核
	$(document).on('click', '.examine', function() {
		var tid = $(this).attr('tid');
		if (confirm('确认要开始审核吗？')) {
			Tr.post('/admin/sellerTask/exmine2', {
				't.id': tid,
				't.status': 'EXAMINING',
				authenticityToken: CDT.token
			}, function(data) {
				if (data.code != 200) {
					return;
				}
				loadTaskDetail(tid);
				$('#queryBtn').removeClass('queryBtn').addClass('disabled');
				$('#taskContainer .isWorking').removeClass('examine').addClass('disabled');
			});
		} else {
			return false;
		}
	});

	
	
	
	
	
	
	
	
	
	
	//审核通过
	$(document).on('click', '#examinePassBtn', function() {
		var id = $(this).attr('tid'),
		status = 'PUBLISHED';
		if (!confirm('审核通过不能再次修改，是否确认审核通过？')) {
			return;
		}
		updateStatus(id, status);

	});
	
	//帮商家修改任务要求
	$(document).on('click', '#modifyTaskRequestBtn', function() {
		var id = $(this).attr('tid'),
			taskRequest=$('#taskRequest').val();
		if (!confirm('确认修改？')) {
			return;
		}
		Tr.post('/admin/sellerTask/modifyTaskRequest', {
			taskRequest:taskRequest,
			id:id
		}, function(data) {
			if(data.code!=200) return;
			alert('修改成功');
			$('#taskRequest').val(taskRequest);
		});

	});
	
	//审核不通过
	$(document).on('click', '#examineNotPassBtn', function() {
		$('#reasonShower').html(output);
		$('#refuseReasonWnd').show();
		$('#txtReason').blur(function(){
			CDT.memo = $.trim($('#txtReason').val());
			if (CDT.memo.length <= 0) {
				$('#error').html(Tr.error('必填'));
				return;
			}else{
				$('#error').html('');
			}
		});
	});
	

	$(document).on('click', '#btnRefuse', function(){
		var id = $('#examinePassBtn').attr('tid'),
			status = 'NOT_PASS';
			CDT.memo = $.trim($('#txtReason').val());
			if (CDT.memo.length <= 0) {
					$('#error').html(Tr.error('必填'));
					return;
				}else{
					$('#error').html('');
				}
				
		 	updateStatus(id, status);
		 	$('#refuseReasonWnd').hide();
			$('#txtReason').val('');
			$('.centerbtn').remove();
	});

	$(document).on('click', '.taskTypeTab', function() {
		var id_toshow = $(this).addClass('focus').siblings().removeClass('focus').end().attr('for');
		loadTask(1);
	});
}

$(function() {
	CDT.token = $("input[name='authenticityToken']").val();
	CDT.taskTemp = $('#taskTemp').remove().val();
	Mustache.parse(CDT.taskTemp);
	CDT.taskDetailTemp = $('#taskDetailTemp').remove().val();
	Mustache.parse(CDT.taskDetailTemp);
	CDT.taskDetailEmptyTemp = $('#taskDetailEmptyTemp').remove().val();
	Mustache.parse(CDT.taskDetailEmptyTemp);

	$('#taskDetail').html(CDT.taskDetailEmptyTemp);
	initBase();

	loadTask(1);

});