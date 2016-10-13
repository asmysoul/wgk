CDT = {
	recordTemp: null,
	currPageNo:1,
	pageSize:30,
	type:'maxTime',
	isReward: false
};
function initBase() {
	
	$(document).on('click', '.foundTypeSwitchWrapper .foundTab', function(){
		$(this).addClass('focus').siblings().removeClass('focus');
		CDT.type= $(this).attr('type');
		CDT.isReward = $(this).attr('isReward');
		loadRecord(1);
	});
	//查询
	$(document).on('click', '#queryBtn', function(){
		CDT.type= $('.focus').attr('type');
		CDT.isReward = $(this).attr('isReward');
		loadRecord(1);
	});
	//导出
	$("#exportBtn").click(function(){
		CDT.type= $('.focus').attr('type');
		CDT.isReward = $(this).attr('isReward');
		var takeTaskStartDate = ($.trim($('#takeTaskTimeStart').val()))?$.trim($('#takeTaskTimeStart').val()) + ' 00:00:00' : '',
			takeTaskEndDate = ($.trim($('#takeTaskTimeEnd').val()))? $.trim($('#takeTaskTimeEnd').val()) + ' 00:00:00':'',
			buyerCreateStartDate = ($.trim($('#buyerCreateTimeStart').val()))?$.trim($('#buyerCreateTimeStart').val()) + ' 00:00:00' : '',
			buyerCreateEndDate = ($.trim($('#buyerCreateTimeEnd').val()))?$.trim($('#buyerCreateTimeEnd').val()) + ' 00:00:00' : '';
		var params="vo.type="+CDT.type+"&vo.pageNo=1&vo.pageSize="+CDT.pageSize+"&vo.takeTaskTimeStart="+takeTaskStartDate+
			"&vo.takeTaskTimeEnd="+takeTaskEndDate+"&vo.buyerNick="+$('#buyerNick').val()+"&vo.adminName="+$('#adminName').val()+
			"&vo.buyerCreateTimeStart="+buyerCreateStartDate+"&vo.buyerCreateTimeEnd="+buyerCreateEndDate+"&vo.isExportOperator=true";
			location.href="/admin/buyerTakeTask/export?"+params;
	});
	
	$('.inputDate').datePicker({
		startDate: '2016-01-01',
		endDate: '2018-01-01',
		clickInput: true,
		verticalOffset: 35
	});
	//默认显示第一个资金记录
	loadRecord(1);
	
}

function loadRecord(pageNo) {
	var takeTaskStartDate = ($.trim($('#takeTaskTimeStart').val()))?$.trim($('#takeTaskTimeStart').val()) + ' 00:00:00' : '',
		takeTaskEndDate = ($.trim($('#takeTaskTimeEnd').val()))? $.trim($('#takeTaskTimeEnd').val()) + ' 00:00:00':'',
		buyerCreateStartDate = ($.trim($('#buyerCreateTimeStart').val()))?$.trim($('#buyerCreateTimeStart').val()) + ' 00:00:00' : '',
		buyerCreateEndDate = ($.trim($('#buyerCreateTimeEnd').val()))?$.trim($('#buyerCreateTimeEnd').val()) + ' 00:00:00' : '';
	Tr.get('/admin/buyerTakeTask/list', {
		'vo.type': CDT.type,
		'vo.pageNo': pageNo,
		'vo.pageSize': CDT.pageSize,
		'vo.takeTaskTimeStart': takeTaskStartDate,
		'vo.takeTaskTimeEnd':takeTaskEndDate,
		'vo.buyerNick':$('#buyerNick').val(),
		'vo.adminName':$('#adminName').val(),
		'vo.buyerCreateTimeStart':buyerCreateStartDate,
		'vo.buyerCreateTimeEnd':buyerCreateEndDate
	}, function(data) {
		if (data.code != 200) return;
		var output = '';
		if (data.results.length <= 0) {
			output = '<div class="iconfont warnBox">&#xf00b6;</span>没有记录!';
		} else {
			output = Mustache.render(CDT.recordTemp, $.extend(data, {
				maxTimeStr: function() {
					if(this.maxTime==null) return "—————————";
					return new Date(this.maxTime).Format('yyyy-MM-dd hh:mm:ss');
				},
				createTimeStr: function() {
					if(this.createTime==null) return "—————————";
					return new Date(this.createTime).Format('yyyy-MM-dd hh:mm:ss');
				}
			}));
		}
		$('#recordContainer').html(output);
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
$(function(){
	CDT.recordTemp = $('#recordTemp').remove().val();
	Mustache.parse(CDT.recordTemp);
	
	initBase();
});
