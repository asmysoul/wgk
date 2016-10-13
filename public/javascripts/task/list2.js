CDT = {
	dataRow:null,
	listType:null,
	taskRowTempl: null,
	buyerAccountTempl: null,
	platform:null,
	device:null,
	currPageNo:1,
	pageSize:30
};
//加载各平台的任务数量以及选中平台的个终端数量
function loadCount() {
	var platform = $('#platform .selectedCtb').attr('value');
	var device = $('#device .selectedCtb').attr('value');
	var url1, url2, url3;
	if(window.location.href.indexOf("admin")>0) {
		var url1 = '/admin/task/count2';
		var url2 = '/admin/task/deviceCount2';
		var url3 = '/admin/task/taskTypeCount2';
	}else {
		var url1 = '/task/count2';
		var url2 = '/task/deviceCount2';
		var url3 = '/task/taskTypeCount2';
	}
	Tr.get(url1, {}
			, function(data) {
		if (data.code != 200) 
			return;
		$('#WEIXIN').text("微信("+data.results.WEIXIN+")");
		$('#QQ').text("QQ("+data.results.QQ+")");
		$('#WEIBO').text("微博("+data.results.WEIBO+")");
		$('#OTHER').text("其他("+data.results.OTHER+")");
	});
	Tr.get(url2, {
			platform : platform}
			, function(data) {
			if(data.code !=200) return;
			$('#PC').text("电脑("+data.results.PC+")");
			$('#MOBILE').text("手机/pad("+data.results.PAD+")");
	});
	Tr.get(url3, {platform : platform, device : device}, 
		function(data) {
		if(data.code !=200) return;
			$('#TOUPIAO').text("投票("+data.results.TOUPIAO+")");
			$('#QUNFA').text("群发("+data.results.QUNFA+")");
			$('#PENGYOUQUAN').text("朋友圈("+data.results.PENGYOUQUAN+")");
	});
}


//加载买号
function loadBuyerAccount() {
	var platform = $('#platform .selectedCtb').attr('value');
	var userType=$("#userType").val();
	if(userType=='BUYER'){
		Tr.get('/buyer/account2/' + platform, {receive:true
		}, function(data) {
			if (data.code != 200) return;
			var output = Mustache.render(CDT.buyerAccountTempl, data);
			$('#buyerAccountPlatform').html(output);
			if (data.results && data.results.length > 0) {
				$('#buyerAccountPlatform a:first').addClass('selectedCtb');
			}
		});
	}
}













function initBaseBind() {
	$(document).on('mouseenter', '.taskRow', function() {
	}).on('mouseleave', '.taskRow', function() {
	});

	// 筛选任务
	$('#condition .checkTextBtn').click(function() {
		$(this).parent().children('.selectedCtb').removeClass('selectedCtb');
		$(this).addClass('selectedCtb');
		loadPage(1);
		loadCount();
		$('.allTask').addClass('focus').siblings().removeClass('focus');
	});
	
	$('#platform .checkTextBtn').click(function() {
		$("#buyerAccountPlatform .checkTextBtn").removeClass('selectedCtb');
		loadBuyerAccount();
	});
	
	
	$('#device .checkTextBtn').click(function(){
		if($(this).attr('value')=='PC'){
			$('#pnlWireless').hide();
			$('#pnlPC').show();
			return;
		}
		$('#pnlPC').hide();
		$('#pnlWireless').show();
	});

	// 切换任务展示类型
	$('.taskTypeTab').click(function() {
		CDT.listType=$(this).attr('value');
		loadPage(1);
		$(this).addClass('focus').siblings().removeClass('focus');
	});

	/* 接手任务 */
	// 买号选择
	$(document).on('click', '#buyerAccountPicker .checkTextBtn', function() {
		$(this).parent().children('.selectedCtb').removeClass('selectedCtb');
		$(this).addClass('selectedCtb');
	});
	//平台买号筛选
	$(document).on('click', '#buyerAccountPlatform .checkTextBtn', function() {
		$(this).parent().children('.selectedCtb').removeClass('selectedCtb');
		$(this).addClass('selectedCtb');
		loadPage(1);
	});

	$(document).on('click', '.taskWrapper .btnTake', function() {
		var id=$(this).attr('data-id');
		CDT.dataRow=$(this).parents('.taskRow').eq(0);
		$("#btnSureTake").attr("data-id",id);
		Tr.popup('takeRequiredWnd');
	});
	// 显示接手任务窗口
	$(document).on('click', '.btnSureTake', function() {
		// 获取可接手的买号
		$("#takeRequiredWnd").hide();
		var platform = $('#platform .selectedCtb').attr('value');
		$('#buyerAccountPicker').html($('#buyerAccountPlatform .selectedCtb').clone());
		//判断金币是否足够
		if(parseFloat($('#allingot').text(),10)>=1){
			//当前任务信息渲染到弹出框
			var $taskRow = CDT.dataRow;
			Tr.get('/buyer/task/taskDetil2', {
				taskId: $(this).attr('data-id')
				}, function(data){
					if(data.code!=200) return;
					$('#taskInfoShower .spTaskRequest').text(data.results);
					Tr.popup('takePickerWnd');
			});
			$('#takePickerWnd').attr('data-id',$(this).attr('data-id'));
			$('#taskInfoShower .spOrderType').text($('#condition .selectedCtb').eq(2).text());
			$('#taskInfoShower .spSeller').text($taskRow.find('.r_seller').text());
			$('#taskInfoShower .spCommission').text($taskRow.find('.rewardIngotSum').attr('rew'));
			$('#taskInfoShower .spExpr').text($taskRow.find('.experience').attr('exp'));
		}else{
			alert('您的金币不足，暂时无法领取任务，请充值之后再操作！');
		}		
	});
	$("#btnCancleTake").click(function(){
		$("#takePickerWnd").css("display","none");
	});
	
	
	
	
	
	
	
	
	
	
	
	// 确认接手
	$('#btnConfirmTake').click(function() {
		var taskId = $('#takePickerWnd').attr('data-id');
		var $buyerAccount = $('#buyerAccountPicker .selectedCtb');
		var buyerAccountId = $buyerAccount.attr('data-id');
		if (!buyerAccountId) {
			alert('请选择要接手任务的买号！');
			return;
		}
		var p = {
			'bt.taskId': taskId,
			'bt.buyerAccountId': buyerAccountId,
			'bt.buyerAccountNick': $buyerAccount.text(),
			'bt.device': $('#device .selectedCtb').attr('value')
		};
//		alert("taskId:"+taskId+",buyerAccountId:"+buyerAccountId+",buyerAccountNick:"+$buyerAccount.text()+",device"+$('#device .selectedCtb').attr('value'));
		Tr.post('/task/take2', p, function(data) {
			if (data.code == 8011 && confirm('上次接手的任务尚未完成，马上去做？')) {
				window.location.replace('/buyer/task/perform2/' + data.msg);
			}
			if (data.code == 800101 && confirm('首次接任务前需要先绑定【财付通】作为退款账号，立即绑定？')) {
				window.location.replace('/user/withdraw');
			}
			if (data.code == 8012) {
				alert('来晚了哦，这个任务已被大家抢光了，换一个吧~~');
				$('#takePickerWnd').hide();
				return;
			}
			if (data.code == 8001) {
				alert(data.msg);
				$('#takePickerWnd').hide();
				return;
			}

			if (data.code != 200) return;
				window.location.href = '/buyer/task/perform2/' + data.results;
			
		});
	});
    
}







function loadPage(pageNo) {
	var device = $('#device .selectedCtb').attr('value');
	var nowUrl = window.location.href;
	var url = nowUrl.indexOf("admin")>0?'/admin/task/list2':'/task/list2';
	var platform = $('#platform .selectedCtb').attr('value');
	var buyerAccountId=0;
	if(CDT.platform==platform){
		buyerAccountId = $('#buyerAccountPlatform .selectedCtb').attr('data-id');
	}else{
		CDT.platform=platform;
	}
	Tr.get(url, {
		"vo.platform": platform,
		"vo.device": device,
		'vo.pageNo' : pageNo,
		'vo.pageSize' : CDT.pageSize,
		"vo.taskType": $('#taskType .selectedCtb').attr('value'),
		"vo.taskListType": CDT.listType,
		'buyerAccountId':buyerAccountId
	}, function(data) {
		if (data.code != 200) return;
		if (data.results.length <= 0) {
			$('.taskWrapper').addClass('warnBox').html('<span class="iconfont">&#xf00b6;</span>没有任务!');
			return;
		}
		// 渲染页面
		var output = Mustache.render(CDT.taskRowTempl, $.extend(data, {
			prePayment: function() {
				return $.number(this.itemPrice * this.itemBuyNum / 100, 2);
			},
			experience: function() {
				//常规|新商家
				if (this.listType == 'COMMON' || this.listType == 'NEW_SHOP') {
					return 1.0;
				}
				if (this.listType == 'RECOMMEND') {
					return 3.0;
				}
				return 0.0;
			},
			receivedRate: function() {
				if (device == 'PC') {
					return $.number(this.pcTakenCount / this.pcOrderNum * 100, 0);
				}
				return $.number(this.mobileTakenCount / this.mobileOrderNum * 100, 0);
			},
			baseOrderIngotSum: function() {
				var imgIngot=0;
				if(this.goodCommentImg!=null&&this.goodCommentImg!=""){
					imgIngot=0.5;
				}
				if(!this.baseOrderIngot){
					return this.extraRewardIngot == null ? 0:$.number(this.extraRewardIngot/100*0.9+imgIngot,2);
				}
				if(!this.extraRewardIngot){
					if(device == 'PC'){
						return this.baseOrderIngot == null ? 0:$.number(this.baseOrderIngot/100*0.9+imgIngot, 2);
					}
					if(device == 'MOBILE'){
						return this.baseOrderIngot == null ? 0:$.number(this.baseOrderIngot/100*0.9+0.5+imgIngot, 2);
					}
				}	
				if(device == 'PC'){
					return $.number((parseInt(this.baseOrderIngot)/100*0.9+imgIngot + parseInt(this.extraRewardIngot)*0.8),2);
				}
				if(device == 'MOBILE'){
					return $.number((parseInt(this.baseOrderIngot)/100*0.9+0.5+imgIngot + parseInt(this.extraRewardIngot)*0.8),2);
				}
			},
			extraRewardIngotFormat: function(){
				// 80%加赏佣金
				return $.number(this.extraRewardIngot * 80 / 100, 1);
			},
			baseOrderIngotFormat: function(){
				if(device == 'PC'){
					return $.number(this.baseOrderIngot/100*0.9,2);
				}
				if(device == 'MOBILE'){
					return $.number(this.baseOrderIngot/100*0.9+0.5,2);
				}	
			},
			totalNum: function(){
				if(device == 'PC'){
					return this.pcOrderNum;
				}
				if(device == 'MOBILE'){
					return this.mobileOrderNum;
				}
			}
		}));
		$('.taskWrapper').removeClass('warnBox').html(output);
		
		$('.pagin-btm').show();
		$('.pagination').pagination(data.totalCount, {
			items_per_page : CDT.pageSize,
			num_display_entries : 5,
			current_page : pageNo,
			num_edge_entries : 2,
			callback : loadOrderCallBack,
			callback_run : false
		});
		CDT.currPageNo = pageNo;
	});
}



function loadOrderCallBack(index, jq) {
	loadPage(index + 1);
}

$(function() {
	CDT.taskRowTempl = $('#taskRowTempl').remove().val();
	Mustache.parse(CDT.taskRowTempl);
	CDT.buyerAccountTempl = $('#buyerAccountTempl').remove().val();
	Mustache.parse(CDT.buyerAccountTempl);
	
	initBaseBind();
	loadCount();
	loadPage(1);
	loadBuyerAccount();
});