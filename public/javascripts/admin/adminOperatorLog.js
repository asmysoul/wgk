CDT = {
	rowTemp: null,
	currPageNo: 1,
	pageSize: 20,
	uid: null,
	nick: null,
	type: null,
	status: null,
};

//加载管理员操作记录
function loadAccount(pageNo) {
	var startDate = ($.trim($('#operatorTimeStart').val()))?$.trim($('#operatorTimeStart').val()) + ' 00:00:00' : '',
		    endDate = ($.trim($('#operatorTimeEnd').val()))? $.trim($('#operatorTimeEnd').val()) + ' 00:00:00':'';
	Tr.get('/admin/adminOperatorLog/list', {
		'vo.adminAccount': $.trim($('#adminAccount').val()),
		'vo.logType': $.trim($('#logType').val()),
		'vo.message': $.trim($('#message').val()),
		'vo.operatorTimeStart': startDate,
		'vo.operatorTimeEnd': endDate,
		'vo.pageNo': pageNo,
		'vo.pageSize': CDT.pageSize
	}, function(data) {
		if (data.code != 200) return;
		if (data.results.length <= 0) {
			$('.normTable').hide();
			$('.pagin-btm').hide();
			$('#noMsg').show();
			return;
		}
		var output = Mustache.render(CDT.rowTemp, $.extend(data, {
			adminLogType: function() {
				if (this.logType == 'BUYER_INGOT') {
					return '变更买手金币';
				}
				if (this.logType == 'BUYER_DEPOSIT') {
					return '变更买手本金';
				}
				if (this.logType == 'SELLER_INGOT') {
					return '变更商家金币员';
				}
				if (this.logType == 'SELLER_PLEDGE') {
					return '变更商家押金';
				}
				if (this.logType == 'AUDIT_SELLER') {
					return '审核买号';
				}
				if (this.logType == 'AUDIT_SELLER_FAIL') {
					return '审核买号不通过';
				}
				if (this.logType == 'AUDIT_TASK') {
					return '审核任务通过';
				}
				if (this.logType == 'AUDIT_TASK_FAIL') {
					return '审核任务不通过';
				}
				if (this.logType == 'CANCEL_TASK') {
					return '取消任务';
				}
				if (this.logType == 'CANCEL_SUB_TASK') {
					return '取消子任务';
				}
				if(this.logType == "CHANGE_USER") {
					return '更改用户信息';
				}
			},
			operatorTimes: function() {
				if(!this.operatorTime){
					return '——';
				}else{
					return new Date(this.operatorTime).Format('yyyy-MM-dd hh:mm:ss');
				}
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
			callback: loadAccountCallBack,
			callback_run: false
		});
		CDT.currPageNo = pageNo;
	});
}
function loadAccountCallBack(index, jq) {
	loadAccount(index + 1);
}

function initBase() {
	//查询用户
	$(document).on('click', '#queryBtn', function() {
		loadAccount(1);
	});
	$('.inputDate').datePicker({
		startDate: '2016-01-01',
		endDate: '2018-01-01',
		clickInput: true,
		verticalOffset: 35
	});
}

$(function() {
	CDT.rowTemp = $('#rowTemp').remove().val();
	Mustache.parse(CDT.rowTemp);
	initBase();
	loadAccount(1);
});