CDT = {
	rowTemp: null,
	currPageNo: 1,
	pageSize: 20
};

//加载列表
function loadItems(pageNo) {
	Tr.get('/admin/taskInvite/list', {
		'vo.userNick': $.trim($('#nick').val()),
		'vo.inviteUserNick': $.trim($('#inviteNick').val()),
		'vo.taskFinishedTimeStart': $.trim($('#rewardTimeStart').val()),
		'vo.taskFinishedTimeEnd': $.trim($('#rewardTimeEnd').val()),
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
			taskFinishedTimeStr: function() {
				return new Date(this.taskFinishedTime).Format('yyyy-MM-dd hh:mm:ss');
			},
			rewardIngotLong: function(){
				if(!this.rewardIngot){
					return '——';
				}
				return $.number(this.rewardIngot/100, 2);
			}
		}));
		$('#noMsg').hide();
		$('.normTable').show();
		$('.pagin-btm').show();
		$('#accountContainer').html(output);

		$('.pagination').pagination(data.totalCount, {
			items_per_page: CDT.pageSize,
			num_display_entries: 5,
			current_page: pageNo,
			num_edge_entries: 2,
			callback: loadItemsCallBack,
			callback_run: false
		});
		CDT.currPageNo = pageNo;
	});
}

function loadItemsCallBack(index, jq) {
	loadItems(index + 1);
}


function initBase() {
	$('#copyInviteUrl').zclip({
		path: '/public/javascripts/ZeroClipboard.swf',
		copy: $('#inviteUrl').text()
	});
	// 查询记录
	$(document).on('click', '#queryBtn', function() {
		loadItems(1);
	});
	
	$("#rewardTimeStart").datetimepicker({format:'Y-m-d H:i:s',formatDate:'Y-m-d'});
	$("#rewardTimeEnd").datetimepicker({format:'Y-m-d H:i:s',formatDate:'Y-m-d'});
}

$(function() {
	CDT.rowTemp = $('#rowTemp').remove().val();
	Mustache.parse(CDT.rowTemp);
	initBase();

	loadItems(1);
});