CDT = {
	currPageNo: 1,
	pageSize: 20
};

function loadNotice(pageNo) {
	var role = $('#selRole').val(),
		title = $('#txtTitle').val(),
		startTime = ($.trim($('#regTimeStart').val()))?$.trim($('#regTimeStart').val()) + ' 00:00:00' : '',
	    endTime = ($.trim($('#regTimeEnd').val()))? $.trim($('#regTimeEnd').val()) + ' 00:00:00':'';
		isDisplay = $('#isDisplay').val(),
		role = $('#selRole').val(),
		type = $('#selType').val();
	Tr.get('/admin/notice/list', {
		'vo.isDisplay': isDisplay,
		'vo.title': title,
		'vo.createTimeStart': startTime,
		'vo.createTimeEnd': endTime,
		'vo.role':role,
		'vo.type':type,
		'vo.pageNo': pageNo,
		'vo.pageSize': CDT.pageSize
	}, function(data) {
		if (data.code != 200) {
			return;
		}
		if (data.results.length <= 0) {
			$('.normTable').hide();
			$('#noMsg').show();
			$('.pagin-btm').hide();
			return;
		}
		var output = Mustache.render(CDT.rowTemp, $.extend(data, {
			createtime: function() {
				return new Date(this.createTime).Format('yyyy-MM-dd hh:mm:ss');
			},
			modifytime: function() {
				return this.modifyTime == null ? '':new Date(this.modifyTime).Format('yyyy-MM-dd hh:mm:ss');
			}
		}));
		$('.normTable').show();
		$('#noMsg').hide();
		$('.pagin-btm').show();
		$('#noticeListTable tbody').html(output);
		$('.pagination').pagination(data.totalCount, {
			items_per_page: CDT.pageSize,
			num_display_entries: 5,
			current_page: pageNo,
			num_edge_entries: 2,
			callback: loadNoticeCallBack,
			callback_run: false
		});
	});

}

function loadNoticeCallBack(index, jq) {
	loadNotice(index + 1);
}
/*元素事件绑定*/
function initBaseBind() {
	$('#btnQuery').click(function() {
		loadNotice(1);
	});

	$(document).on('click', '.btnRowView', function() {

	});
	$('.inputDate').datePicker({
		startDate: '2016-01-01',
		endDate: '2018-01-01',
		clickInput: true,
		verticalOffset: 35
	});

	//编辑公告
	$(document).on('click', '.btnRowEdit', function() {
		var id = $(this).attr('data-id'),
			win = window.open();
		setTimeout(function() {
			win.location = '/admin/notice/edit?id=' + id;
		}, 200);

	});

	//置顶公告
	$(document).on('click', '.btnShowTop', function() {
		var id = $(this).attr('data-id');
		Tr.post('/admin/notice/top', {
			'notice.id' : id,
		},function(data) {
			if(data.code != 200) 
				return;
			alert('置顶成功');
			loadNotice(1);
		});
	});
	
	//操作公告：隐藏、显示
	$(document).on('click', '.btnRowDisplay', function() {
		var id = $(this).attr('data-id'),
			isDisplay = $(this).attr('isDisplay');

		Tr.post('/admin/notice/display', {
				'notice.id': id,
				'notice.isDisplay': isDisplay
			},
			function(data) {
				if (data.code != 200) return;
				alert('修改成功');
				loadNotice(1);
			});
	});
}

$(function() {
	CDT.rowTemp = $('#rowTemp').remove().val();
	Mustache.parse(CDT.rowTemp);
	initBaseBind();

	loadNotice(1);
});