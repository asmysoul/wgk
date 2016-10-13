CDT = {
	taskTemp: null,
	currPageNo: 1,
	pageSize: 20
};
//加载任务
function loadTask(pageNo) {
	var modifyTimeStart = ($.trim($('#modifyTimeStart').val())) ? $.trim($('#modifyTimeStart').val()) + ' 00:00:00' : '',
		    modifyTimeEnd = ($.trim($('#modifyTimeEnd').val())) ? $.trim($('#modifyTimeEnd').val()) + ' 00:00:00' : '';
	Tr.get('/admin/listTask3', {
		'vo.platform': $('#platform').val(),
		'vo.device': $('#device').val(),
		'vo.status': $('#status').val(),
		'vo.shopName': $.trim($('#shopName').val()),
		'vo.sellerNick': $.trim($('#nick').val()),
		'vo.taskId': $.trim($('#txtTid').val()),
		'vo.pageNo': pageNo,
		'vo.pageSize': CDT.pageSize,
		'vo.publishTimeStart': modifyTimeStart,
		'vo.publishTimeEnd': modifyTimeEnd
	}, function(data) {
		if (data.code != 200) return;
		if (data.results.length <= 0) {
			$('.normTable').hide();
			$('#noMsg').show();
			$('.pagin-btm').hide();
			return;
		}

		var output = Mustache.render(CDT.taskTemp, $.extend(data, {
			cancelFlag: function() {
				if (this.status != 'PUBLISHED' && this.status != 'WAIT_PUBLISH') {
					return false;
				}
				return true;
			},
			platformStr: function() {
				return this.platformTitle;
			},
			platIcon: function() {
				return 'plat_' + this.platform;
			},
			publishTimeStr: function() {
				if (this.publishTime) {
					return new Date(this.publishTime).Format('yyyy-MM-dd hh:mm');
				}
				return '----';
			},
			createTimeStr: function() {
				return new Date(this.createTime).Format('yyyy-MM-dd hh:mm');
			},
			sellerAdminNameStr: function() {
				return this.sellerAdminName!=null ? "【" + this.sellerAdminName+"】":"";
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

function initBaseBind() {
	$(document).on('click', '#queryBtn', function() {
		loadTask(1);
	});
		// 撤销未接单任务
	$(document).on('click', '.cancel', function() {
		CDT.currId = $(this).attr('taskId');
		Tr.get('/admin/task/cancelData3', {
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
		Tr.post('/admin/task/cancel3', {
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
			loadTask(1);
		});
	});
	//修改任务信息弹出窗
	$(document).on('click', '.modify', function() {
		CDT.currId=$(this).attr('taskId');
		Tr.get('/admin/task/getTaskDetail3', {
			id:CDT.currId
		}, function(data) {
			if(data.code==5001) alert("此任务不存在!");
			if(data.code!=200) return;
			$('#taskRequest').val(data.results.taskRequest);
			$("#itemWeight").val(data.results.expressWeight);
			Tr.popup('modifyTask');
		});
	});
	//修改任务信息
	$(document).on('click', '#btnModifyTask', function() {
		var taskRequest = $('#taskRequest').val();
		Tr.post('/admin/task/modifyTask3', {
			id:CDT.currId,
			taskRequest:taskRequest,
			expressWeight:$("#itemWeight").val()
		}, function(data) {
			if(data.code!=200) return;
			alert("修改成功");
			$('#modifyTask').hide();
		});
	});
	
	$('.inputDate').datePicker({
		startDate: '2014-01-01',
		endDate: '2020-01-01',
		clickInput: true,
		verticalOffset: 35
	});
	
	$("#exportOverdue").click(function(){
		var modifyTimeStart = ($.trim($('#modifyTimeStart').val())) ? $.trim($('#modifyTimeStart').val()) + ' 00:00:00' : '',
			    modifyTimeEnd = ($.trim($('#modifyTimeEnd').val())) ? $.trim($('#modifyTimeEnd').val()) + ' 00:00:00' : '';
			    count=$("#exportNum").val();
			    if($.trim($('#modifyTimeStart').val())==""||count==""){
			    	return alert("请输入导出条数和发布时间！");
			    }
			    var params="count="+count+"&vo.publishTimeStart="+modifyTimeStart+"&vo.publishTimeEnd="+modifyTimeEnd;
			    location.href="/admin/exportTask3?"+params;
			   
	});
}

$(function() {
	CDT.taskTemp = $('#taskTemp').remove().val();
	initBaseBind();

	loadTask(1);
});