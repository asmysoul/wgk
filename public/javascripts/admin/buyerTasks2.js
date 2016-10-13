CDT = {
	taskTemp: null,
	currPageNo: 1,
	pageSize: 20
};
//加载任务
function loadTask(pageNo) {
	var modifyTimeStart = ($.trim($('#modifyTimeStart').val())) ? $.trim($('#modifyTimeStart').val()) + ' 00:00:00' : '',
	    modifyTimeEnd = ($.trim($('#modifyTimeEnd').val())) ? $.trim($('#modifyTimeEnd').val()) + ' 00:00:00' : '';
	Tr.get('/admin/buyerTask/listAll2', {
		'vo.platform': $('#platform').val(),
		'vo.device': $('#device').val(),
		'vo.status': $('#status').val(),
		'vo.buyerNick': $('#buyerNick').val(),
		'vo.sellerNick': $('#sellerNick').val(),
		'vo.buyerAccountNick': $('#buyerAccount').val(),
		'vo.shopName': $.trim($('#shopName').val()),
		'vo.taskId': $.trim($('#taskId').val()),
		'vo.buyerTaskId': $('#buyerTaskId').val(),
		'vo.orderId':$("#orderId").val(),
		'vo.modifyTimeStart': modifyTimeStart,
		'vo.modifyTimeEnd': modifyTimeEnd,
		'vo.pageNo': pageNo,
		'vo.pageSize': CDT.pageSize
	}, function(data) {
		if (data.code != 200) return;
		if (data.results.length <= 0) {
			$('.normTable').hide();
			$('#noMsg').show();
			$('.pagin-btm').hide();
			return;
		}

		var output = Mustache.render(CDT.taskTemp, $.extend(data, {
			isPC: function() {
				return this.device == 'PC';
			},
			orderId: function() {
				if (this.orderId) {
					return this.orderId;
				}
				return '----';
			},
			expressNo: function() {
				if (this.expressNo) {
					return this.expressNo;
				}
				return '----';
			},
			paidFeeStr: function() {
				return $.number(this.paidFee/100, 2);
			},
			rewardIngotStr: function() {
				return $.number(this.rewardIngot/100, 2);
			},
			platIcon: function() {
				return 'plat_' + this.platform;
			},
			modifyTimeStr: function() {
				if (this.modifyTime) {
					return new Date(this.modifyTime).Format('yyyy-MM-dd hh:mm:ss');
				}
				return '----';
			},
			takeTimeStr: function() {
				return new Date(this.takeTime).Format('yyyy-MM-dd hh:mm:ss');
			},
			sellerAdminNameStr:function() {
				return this.sellerAdminName!=null ? this.sellerAdminName:"";
			},
			buyerAdminNameStr:function() {
				return this.buyerAdminName!=null ? this.buyerAdminName:"";
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

function cancelTask(id){
	Tr.get('/admin/buyerTask/cancel2/'+id,{
	    'id': id
	},function(data){
		if(data.code == 400){
			alert('该子任务已取消');
		}
		if(data.code == 555){
			alert('该子任务已完成，不能取消');
		}
		if(data.code == 599){
			alert('找不到该子任务');
		}
        if (data.code != 200) return;
        alert('成功取消');
        loadTask(1);
	});
}

function loadTaskCallBack(index, jq) {
	loadTask(index + 1);
}

function initBaseBind() {
	$(document).on('click', '.queryBtn', function() {
		loadTask(1);
	});
	$(document).on('click','.cancel',function(){
		var id = $(this).attr('data-id');
		if(!confirm('确认取消该任务？')){
			return;
		}
		cancelTask(id);
	});
	$('.inputDate').datePicker({
		startDate: '2016-01-01',
		endDate: '2018-01-01',
		clickInput: true,
		verticalOffset: 35
	});
	
}

$(function() {
	CDT.taskTemp = $('#taskTemp').remove().val();
	initBaseBind();

	loadTask(1);
});