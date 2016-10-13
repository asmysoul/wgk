CDT = {
	searchType:'PROCESS',
	taskCellTemp: null,
	currPageNo: 1,
	pageSize: 20,
	currId:''
};
//加载任务
function loadTask(pageNo) {
	var publishTimeStart = ($.trim($('#publishTimeStart').val())) ? $.trim($('#publishTimeStart').val()) + ' 00:00:00' : '',
	    publishTimeEnd = ($.trim($('#publishTimeEnd').val())) ? $.trim($('#publishTimeEnd').val()) + ' 00:00:00' : '',
	    platform = $('#platform').val(),
		device = $('#device').val(),
		status = $('#status').val(),
		itemTitle=$('#itemTitle').val();
	Tr.get('/seller/task/list3', {
		'vo.platform': platform,
		'vo.device': device,
		'vo.status': status,
		'vo.itemTitle':itemTitle,
		'vo.publishTimeStart': publishTimeStart,
		'vo.publishTimeEnd': publishTimeEnd,
		'vo.buyerAccountNick': $.trim($('#buyerAccountNick').val()),//买家旺旺
		'vo.orderId': $.trim($('#orderId').val()),
		'vo.buyerTaskId': $.trim($('#buyerTaskId').val()),
		'vo.buyerNick': $.trim($('#buyerNick').val()),//买家账号
		'vo.pageNo': pageNo,
		'vo.pageSize': CDT.pageSize,
		'type':CDT.searchType
	}, function(data) {
		if (data.code != 200) return;
		if (data.results.length <= 0) {
			$('.taskWrapper').addClass('warnBox').html('<span class="iconfont">&#xf00b6;</span>没有任务!');
			$('.pagination').empty();
			return;
		}

		var output = Mustache.render(CDT.taskCellTemp,data,$.extend(data,{
			cancelFlag: function() {
				if (this.status != 'PUBLISHED' && this.status != 'WAIT_PUBLISH') {
					return false;
				}
				return this.notTakenCount >= 0;
			},
			isWaitEdit: function(){
				return this.status == 'WAIT_EDIT' || this.status == 'NOT_PASS';
			},
			isWaitingPay: function(){
				return this.status == 'WAITING_PAY';
			},
			isRealTask: function(){
				return this.status == 'WAITING_PAY'|| this.status == 'WAIT_EDIT' || this.status == 'WAIT_EXAMINE' || this.status == 'NOT_PASS';
			},
			platIcon: function(){
				return 'plat_' + this.platform;
			},
			pubTime: function(){
				if(!this.publishTime){
					return '未发布';
				}else{
					return new Date(this.publishTime).Format('yyyy-MM-dd hh:mm:ss');
				}
			}
		}));
		$('#taskContainer').removeClass('warnBox').html(output);
		$('.WAIT_EDIT,.WAITING_PAY').parents('.taskCell').addClass('newtaskCell');
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


function initBase(){
	//查询
	$('#queryBtn').click(function(){
		loadTask(1);
	});
    $('.inputDate').datePicker({
		startDate: '2014-01-01',
		endDate: '2020-01-01',
		clickInput: true,
		verticalOffset: 35
	});
	// 撤销未接单任务
	$(document).on('click', '.cancel', function() {
		CDT.currId = $(this).attr('taskId');
		Tr.get('/seller/task/cancelData3', {
			id: CDT.currId
		}, function(data) {
			if (data.code == 8001) {
				alert('该任务已经全部被接单');
			}
			if (data.code != 200) return;
			if(!data.results){
				$('#cancelWnd .cancledNum').text(0);
				$('#cancelWnd .cancledPledge').text(0);
			    $('#cancelWnd .cancledIngot').text(0);
			    Tr.popup('cancelWnd');
			}
			else{	
				$('#cancelWnd .cancledNum').text(data.results.cancledNum);
				$('#cancelWnd .cancledPledge').text($.number(data.results.cancledPledge / 100, 2));
			    $('#cancelWnd .cancledIngot').text($.number(data.results.cancledIngot / 100, 2));
			    Tr.popup('cancelWnd');
			}
			
		});
	});

	// 确认撤销任务
	$(document).on('click', '#cancelWnd .btnPay', function() {
		Tr.post('/seller/task/cancel3', {
			id: CDT.currId
		}, function(data) {
			if (data.code == 800101) {
				alert('该功能维护中，暂不开放！');
			}
			if (data.code == 8001) {
				alert('该任务已经全部被接单');
			}
			if (data.code != 200) {
				$('#cancelWnd').hide();
				return;
			}
			$('#cancelWnd').hide();
			alert('撤销成功');
			window.location.href='/seller/tasks3';
			loadTask(1);
		});
	});

	$('.btnNext').click(function() {
		if (!$('.speed:checked').val()) {
			alert('请选择推荐任务的金币数');
			return;
		}
		$('.btnPrev,#stepShower-2, .btnPay').show();
		$('#stepShower-1').hide();
		$(this).hide();
		//校验价格
		var speed = parseFloat($('#stepShower-1 .panelLine').eq(1).find('input:checked').val());
		var txtPlusComm= parseFloat($('#txtPlusComm').val());
		var price = speed + txtPlusComm;
		var ingot = parseFloat($('span.ingot').text());
		var pledge = parseFloat($('span.pledge').text());
		Tr.checkprice(price ,ingot,pledge);
	});


	$('.btnPrev').click(function() {
		$('.btnNext,#stepShower-1').show();
		$('#stepShower-2, .btnPay').hide();
		$(this).hide();
	});
	
	$(document).on('click', '.chargeTypeTab', function() {
		var id_toshow = $(this).addClass('focus').siblings().removeClass('focus').end().attr('for');
		CDT.searchType=$(this).attr("data-status");
		if(CDT.searchType=='FINISHED'||CDT.searchType=='CANCEL'){
			$("#status").attr("disabled",true);
		}else if(CDT.searchType=='PROCESS'){
			$("select[id='status'] option").each(function(){
				if($(this).val()=='FINISHED'||$(this).val()=='CANCLED'){
					$(this).hide();
				}
			});
			$("#status").attr("disabled",false);
		}else{
			$("select[id='status'] option").each(function(){
				if($(this).val()=='FINISHED'||$(this).val()=='CANCLED'){
					$(this).show();
				}
			});
			$("#status").attr("disabled",false);
		}
		loadTask(1);
	});

}

$(function(){
	CDT.taskCellTemp = $('#taskCellTemp').remove().val();
	Mustache.parse(CDT.taskCellTemp);
	$("select[id='status'] option").each(function(){
		if($(this).val()=='FINISHED'||$(this).val()=='CANCLED'){
			$(this).hide();
		}
	});
	initBase();
	loadTask(1);
});
//选中
$(function(){
	$(document).on('click', '.pay', function() {
		$(this).find('input').attr('checked','checked');
		$('input[type="checkbox"]').removeAttr('checked');
	});
	$(document).on('click', '#platformSelect .checkTextBtn', function() {
		var platform = $(this).attr('value');
		$(this).parent().children('.selectedCtb').removeClass('selectedCtb').end().end().addClass('selectedCtb');
		$('#' + platform +'Bind').show().siblings().hide();
	});
	$('input[type="checkbox"]').click(function(){
		$('input[type="radio"]').removeAttr('checked');
		$('.pay').removeClass('selectedCtb');
	});
	
});
