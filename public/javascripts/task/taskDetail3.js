CDT = {
	taskTemp: null,
	currPageNo: 1,
	pageSize: 10
};

//加载子任务
function loadTask(pageNo) {
	var id = $('#taskId').text();
	var url = '/seller/buyerTask/list3';
	if (App.userRole == 'admin') {
		url = '/admin/buyerTask/list3';
	}
	Tr.get(url, {
		'vo.taskId': id,
		'vo.isSysRefundTask':App.isSysRefundTask,
		'vo.pageNo': pageNo,
		'vo.pageSize': CDT.pageSize
	}, function(data) {
		if (data.code != 200) return;
		//任何情况都显示金钱明细
//		if (data.results.length <= 0) {
//			$('.normTable').hide();
//			$('#noMsg').show();
//			return;
//		}
		var output = Mustache.render(CDT.taskTemp, $.extend(data, {
			paidFeeStr: function() {
				return $.number(this.paidFee / 100, 2);
			},
			deviceTitle: function() {
				return this.device == 'PC' ? '电脑' : '手机';
			},
			deviceCss: function() {
				return this.device == 'PC' ? '&#xf0099;' : '&#xf00a2;';
			}
		}));
		$('.normTable').show();
		$('#noMsg').hide();
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
function initBase(){
	$('#copyUrl').zclip({
		path: '/public/javascripts/ZeroClipboard.swf',
		copy: $('#urlMsg').text()
	});

	$('#btnExportCsv').click(function() {
		if($('#taskContainer tr').length <= 0){
			alert('当前没有可导出的买手任务！');
			return;
		}
		$('#exportForm').submit();
	});
	$('#ckShowtCancledTask').click(function(){
		var $me = $(this);
		if ($me.hasClass('inputCheckBox-checked')) {
			$(this).removeClass('inputCheckBox-checked');
			loadTask(1);
			return;
		}
		$(this).addClass('inputCheckBox-checked');
		loadTask(1);
	});
}
$(function() {
	CDT.taskTemp = $('#taskTemp').remove().val();
	Mustache.parse(CDT.taskTemp);
    initBase();
	loadTask(1);
});