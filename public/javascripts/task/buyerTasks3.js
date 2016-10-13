CDT = {
	searchType:'PROCESS',
	taskCellTemp: null,
	accountTemp: null,
	currPageNo: 1,
	pageSize: 20,
	buyerAccountId: '',
	platform:'',
	status:'',
	authenticityToken:''
};

//ajax请求，并渲染页面
function loadTaskData(platform,buyerAccountId,status,pageNo,takeTimeStart,takeTimeEnd,id,orderId,taskId){
	Tr.get('/buyer/task/list3', {
		'vo.platform': platform,
		'vo.buyerAccountId': buyerAccountId,
		'vo.status': status,
		'vo.pageNo': pageNo,
		'vo.pageSize': CDT.pageSize,
		'vo.takeTimeStart' :takeTimeStart,
		'vo.takeTimeEnd' :takeTimeEnd,
		'vo.buyerTaskId':id,
		'vo.orderId':orderId,
		'vo.taskId':taskId,
		'type':CDT.searchType
	}, function(data) {
		if (data.code != 200) {
			return;
		}
		if (data.results.length <= 0) {
			$('.taskWrapper').addClass('warnBox').html('<span class="iconfont">&#xf00b6;</span>没有任务!');
			$('.pagin-btm').hide();
			return;
		}
		//todo
		var output = Mustache.render(CDT.taskCellTemp, $.extend(data, {
			paidFeeStr: function() {
				return $.number(this.paidFee / 100, 2);
			},
			platIcon:function(){
				return 'plat_' + this.platform;
			},
			commission: function() {
				return $.number(this.rewardIngot/10, 2);
			},
			staActive: function() {
				if(this.status == 'RECIEVED'){
					return '[立即做任务]';
				}else if(this.status == 'WAIT_PAY'){
					return '[继续做任务]';
				}
//				else if(this.status == 'WAIT_CONFIRM'){
//					return '[确认收货]';
//				}else if(this.status == 'REFUNDING'){
//					return '[核实退款]';
//				}else if(this.status == 'WAIT_BUYER_CONFIRM_EDITED_SYS_REFUND' || this.status == 'WAIT_BUYER_CONFIRM_SYS_REFUND'){
//					return '[核实退款]';
//				}
				else{
					return '';
				}
			},
			staUrl: function(){
				if(this.status == 'RECIEVED'){
					return '/buyer/task/perform3/'+ this.id;
				}else if(this.status == 'WAIT_PAY'){
					return '/buyer/task/perform3/'+ this.id;
				}else if(this.status == 'WAIT_CONFIRM'){
					return '/buyer/task/confirmGoods/'+this.platform+'/'+this.id;
				}else if(this.status == 'REFUNDING'){
					return '/buyer/task/verifyRefund?platform='+this.platform;
				}else if(this.status == 'WAIT_BUYER_CONFIRM_EDITED_SYS_REFUND' || this.status == 'WAIT_BUYER_CONFIRM_SYS_REFUND'){
					return '/buyer/tasks/sysRefund';
				}else {
					return '';
				}
			},
			statusAction: function(){
				if(this.status == 'RECIEVED' || this.status == 'WAIT_PAY'){
					return '[撤销任务]';
				}
			},
			taketime: function() {
				return new Date(this.takeTime).Format('yyyy-MM-dd hh:mm:ss');
			}
		}));
        $('.pagin-btm').show();
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
//通过查询加载任务
function loadTask(pageNo) {
	var platform = $('#platform').val(),
		buyerAccountId = $('#buyerAccountId').val(),
		status = $('#status').val(),
		takeTimeStart = ($.trim($('#takeTimeStart').val()))?$.trim($('#takeTimeStart').val()) + ' 00:00:00' : '',
		takeTimeEnd = ($.trim($('#takeTimeEnd').val()))?$.trim($('#takeTimeEnd').val()) + ' 00:00:00' : '',
		id = $('#id').val(),
		orderId = $('#orderId').val(),
		taskId = $('#taskId').val();
	loadTaskData(platform,buyerAccountId,status,pageNo,takeTimeStart,takeTimeEnd,id,orderId,taskId);
}

function loadTaskCallBack(index, jq) {
	loadTask(index + 1);
}

//页面上端快捷方式查询
function loadTaskQuick(pageNo){
	loadTaskData(CDT.platform,CDT.buyerAccountId,CDT.status,pageNo);
}

function loadTaskQuickCallBack(index, jq){
	loadTaskQuick(index + 1);
}

//加载买号
function loadAccount(platform) {

	Tr.get('/buyer/account/list3', {platform:platform}, function(data) {
		if (data.code != 200) return;
		var output = Mustache.render(CDT.accountTemp, data);
		$('#buyerAccountId').find('option').eq(0).nextAll().remove();
		$('#buyerAccountId').append(output);
	});
}

function initBase() {
	//查询
	$('#queryBtn').click(function() {
		loadTask(1);
	});

	//切换平台，买号数据切换
	$('#platform').change(function() {
		loadAccount($('#platform').val());
	});

	//页面上端快捷查询
	$(document).on('click', '.selectCell', function(){
		var $me = $(this);
		CDT.platform = $me.attr('platform');
		CDT.status = $me.attr('status');
		$('#platform').val(CDT.platform);
		$('#status').val(CDT.status);
		loadTaskQuick(1);
	});

	$(document).on('click', '.taskCell .cancel', function(){
		if(!confirm('中断任务扣除押金1金币，确认撤销么？')){
			return;
		}
		var id = $(this).attr('btId');
		Tr.post('/buyer/task/cancel3', {
			id: id,
			authenticityToken: CDT.authenticityToken
		}, function(data) {
			if(data.code == 8001){
				alert('该任务当前状态下不能撤销任务');
				return;
			}
			if(data.code != 200)return;
			
			alert('撤销成功');
			loadTask(1);		
		});
	});

	$(document).on('click', '.chargeTypeTab', function() {
		var id_toshow = $(this).addClass('focus').siblings().removeClass('focus').end().attr('for');
		CDT.searchType=$(this).attr("data-status");
		if(CDT.searchType=='FINISHED'||CDT.searchType=='CANCEL'){
			$("#status").attr("disabled",true);
		}else if(CDT.searchType=='PROCESS'){
			$("select[id='status'] option").each(function(){
				if($(this).val()=='_FINISHED'||$(this).val()=='CANCLED'){
					$(this).hide();
				}
			});
			$("#status").attr("disabled",false);
		}else{
			$("select[id='status'] option").each(function(){
				if($(this).val()=='_FINISHED'||$(this).val()=='CANCLED'){
					$(this).show();
				}
			});
			$("#status").attr("disabled",false);
		}
		loadTask(1);
	});
	
	$('.inputDate').datePicker({
		startDate: '2014-01-01',
		endDate: '2020-01-01',
		clickInput: true,
		verticalOffset: 35
		//yearRange:"-20:20"
	});

	
}

$(function() {
	CDT.taskCellTemp = $('#taskCellTemp').remove().val();
	Mustache.parse(CDT.taskCellTemp);

	CDT.accountTemp = $('#accountTemp').remove().val();
	Mustache.parse(CDT.accountTemp);

	CDT.authenticityToken = $('input[name="authenticityToken"]').val();

	initBase();
	loadAccount('TAOBAO');
	$("select[id='status'] option").each(function(){
		if($(this).val()=='_FINISHED'||$(this).val()=='CANCLED'){
			$(this).hide();
		}
	});
	loadTask(1);
});