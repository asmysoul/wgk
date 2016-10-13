CDT = {
	taskCellTemp: null,
	currPageNo: 1,
	pageSize: 20,
};
//加载任务
function loadTask(pageNo) {
	var platform = $('#platform').val(),
		device = $('#device').val(),
		status = $('#status').val();
	Tr.get('/seller/task/list', {
		'vo.platform': platform,
		'vo.device': device,
		'vo.status': status,
		'vo.pageNo': pageNo,
		'vo.pageSize': CDT.pageSize
	}, function(data) {
		if (data.code != 200) {
			return;
		}
		
		var output = Mustache.render(CDT.taskCellTemp, $.extend(data, {
			isNotPass: function() {
				return this.status=='NOT_PASS';
			},
			platIcon: function(){
				return 'plat_' + this.platform;
			},
			createTimeStr: function() {
				return this.createTime == null? '':new Date(this.createTime).Format('yyyy-MM-dd hh:mm');
			},
			examineTimeStr: function() {
				return this.examineTime == null? '':new Date(this.examineTime).Format('yyyy-MM-dd hh:mm');
			}
		}));
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
	//查询
	$('#queryBtn').click(function(){
		loadTask(1);
	});

	$(document).on('click','.boost',function(){
		$('#boostWnd').show();
	});

	$('.btnNext').click(function(){
		$('.btnPrev,#stepShower-2').show();
		$('#stepShower-1').hide();
		$(this).hide();
	});
	$('.btnPrev').click(function(){
		$('.btnNext,#stepShower-1').show();
		$('#stepShower-2').hide();
		$(this).hide();		
	});	

}

$(function(){
	CDT.taskCellTemp = $('#taskCellTemp').remove().val();
	Mustache.parse(CDT.taskCellTemp);

	initBase();
	loadTask(1);
});