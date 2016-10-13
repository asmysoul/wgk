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
		var url1 = '/admin/task/count';
		var url2 = '/admin/task/deviceCount';
		var url3 = '/admin/task/taskTypeCount';
	}else {
		var url1 = '/task/count';
		var url2 = '/task/deviceCount';
		var url3 = '/task/taskTypeCount';
	}
	Tr.get(url1, {}
			, function(data) {
		if (data.code != 200) 
			return;
		$('#TAOBAO').text("淘宝("+data.results.TAOBAO+")");
		$('#JD').text("京东("+data.results.JD+")");
		$('#MOGUJIE').text("蘑菇街("+data.results.MOGUJIE+")");
		$('#YHD').text("1号店("+data.results.YHD+")");
		$('#JUMEI').text("聚美("+data.results.JUMEI+")");
		$('#AMAZON').text("亚马逊("+data.results.AMAZON+")");
		$('#DANGDANG').text("当当("+data.results.DANGDANG+")");
		$('#QQ').text("拍拍("+data.results.QQ+")");
		$('#ALIBABA').text("阿里巴巴("+data.results.ALIBABA+")");
		$('#MEILISHUO').text("美丽说("+data.results.MEILISHUO+")");
		$('#GUOMEI').text("国美("+data.results.GUOMEI+")");
		$("#SUNING").text("苏宁("+data.results.SUNING+")");
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
			$('#ORDER').text("订单("+data.results.ORDER+")");
			$('#JHS').text("聚划算("+data.results.JHS+")");
			$('#SUBWAY').text("直通车("+data.results.SUBWAY+")");
	});
}
//加载买号
function loadBuyerAccount() {
	var platform = $('#platform .selectedCtb').attr('value');
	if(platform!='TAOBAO'){
		$("#taskType a[value='JHS']").hide();
		$("#taskType a[value='SUBWAY']").hide();
	}else{
		$("#taskType a[value='JHS']").show();
		$("#taskType a[value='SUBWAY']").show();
	}
	var userType=$("#userType").val();
	if(userType=='BUYER'){
	Tr.get('/buyer/account/' + platform, {receive:true
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
		//var $me = $(this);
		//$me.find('.taskCover').width($me.width()).show();
	}).on('mouseleave', '.taskRow', function() {
		//var $me = $(this);
		//$me.find('.taskCover').hide();
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
			Tr.get('/buyer/task/taskDetil', {
				taskId: $(this).attr('data-id')
				}, function(data){
					if(data.code!=200) return;
					$('#taskInfoShower .spTaskRequest').text(data.results);
					Tr.popup('takePickerWnd');
			});
			$('#takePickerWnd').attr('data-id',$(this).attr('data-id'));
			$('#taskInfoShower .spOrderType').text($('#condition .selectedCtb').eq(2).text());
			$('#taskInfoShower .spSeller').text($taskRow.find('.r_seller').text());
			$('#taskInfoShower .spAmount').text($taskRow.find('.prePayment').attr('pre'));
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
		Tr.post('/task/take', p, function(data) {
			if (data.code == 8011 && confirm('上次接手的任务尚未完成，马上去做？')) {
				window.location.replace('/buyer/task/perform/' + data.msg);
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
				window.location.href = '/buyer/task/perform/' + data.results;
			
		});
	});
    
}

function loadPage(pageNo) {
	var device = $('#device .selectedCtb').attr('value');
	var nowUrl = window.location.href;
	var url = nowUrl.indexOf("admin")>0?'/admin/task/list':'/task/list';
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