CDT = {
	rowTemp: null,
	currPageNo: 1,
	pageSize: 20,
	currId: null
};

//加载数据列表
function loadItems(pageNo) {
	Tr.get('/admin/activity/list', {}, function(data) {
		if (data.code != 200) return;
		if (data.results.length <= 0) {
			$('.pagin-btm').hide();
			$('#noMsg').show();
			return;
		}
		var output = Mustache.render(CDT.rowTemp, $.extend(data, {
			statusTitle: function() {
				return this.status == 'VALID' ? '进行中' : '已停止';
			},
			startTimeStr: function() {
				return new Date(this.startTime).Format('yyyy-MM-dd hh:mm:ss');
			},
			endTimeStr: function() {
				return new Date(this.endTime).Format('yyyy-MM-dd hh:mm:ss');
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

	/*创建运营活动*/
	$(document).on('click', '#addActivity', function() {
		// 清理编辑遗留的数据
		CDT.currId = null;
		$('#title').val('');
		$('#startTime').val('');
		$('#endTime').val('');
		$('#status').find('.INVALID').attr('checked', true);
	
		$('#editActivityWnd .topTile').text('添加新活动');
		Tr.popup('editActivityWnd');
	});

	/*修改运营活动*/
	$(document).on('click', '#accountContainer .J_edit', function() {
		var $tr = $(this).parents('tr');
		CDT.currId = $tr.attr('data-id');
		$('#title').val($tr.find('.J_title').text());
		$('#startTime').val($tr.find('.J_startTime').text());
		$('#endTime').val($tr.find('.J_endTime').text());

		var status = $tr.attr('data-status');
		$('#status').find('.' + status).attr('checked', true);

		$('#editActivityWnd .topTile').text('修改活动');
		Tr.popup('editActivityWnd');
	});

	/*保存运营活动*/
	$(document).on('click', '#saveActivity', function() {
		var p = {
			'a.id':CDT.currId,
			'a.title':$('#title').val(),
			'a.startTime':$('#startTime').val(),
			'a.endTime':$('#endTime').val(),
			'a.status':$('#status input:checked').val(),
		};
		Tr.post('/admin/activity/save', p, function(data) {
			if (data.code != 200) return;
			alert('保存运营活动成功！');
			$('#editActivityWnd').hide();
			loadItems(1);
		});
	});

	/*保存“注册邀请”活动的规则*/
	$(document).on('click', '#saveRule', function() {
		if(!confirm('确定要更新活动规则么？')){
			return;
		}
		var p = {
			'type':'INVITE_REG',
			'vo.buyerRewardRate':$('#buyerRewardRate').val(),
			'vo.sellerRewardRate':$('#sellerRewardRate').val()
		};
		Tr.post('/admin/activity/rule/save', p, function(data) {
			if (data.code != 200) return;
			alert('保存运营活动成功！');
		});
	});
}

$(function() {
	CDT.rowTemp = $('#rowTemp').remove().val();
	Mustache.parse(CDT.rowTemp);

	initBase();

	loadItems(1);
});