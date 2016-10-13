CDT={
	orderTakenContainerTemp:null,
	orderTemp: null,
	currPageNo: 1,
	pageSize: 7,
};

//加载订单
function loadOrder(pageNo){
	Tr.get('/admin/express/listExpressCount', {
		'vo.pageNo': pageNo,
		'vo.pageSize': CDT.pageSize
	}, function(data) {
		if(data.code != 200) return;
		if (data.results.length <= 0) {
			$('.normTable').hide();
			$('#noMsg').show();
			$('.pagin-btm').hide();
			return;
		}
		var output = Mustache.render(CDT.orderTemp, $.extend(data, {
			date:function(){
				return new Date(this.takeTime).Format('yyyy-MM-dd');
			}
		}));
		$('.normTable').show();
		$('#noMsg').hide();
		$('.pagin-btm').show();
		$('#orderContainer').html(output);
		$('.pagination').pagination(data.totalCount, {
			items_per_page: CDT.pageSize,
			num_display_entries: 5,
			current_page: pageNo,
			num_edge_entries: 2,
			callback: loadOrderCallBack,
			callback_run: false
		});
		CDT.currPageNo = pageNo;
	});
}

$(document).on('click', '#queryBtn', function() {
	Tr.get('/admin/searchExpressTotal', {
		'vo.sellerNick':$("#sellerNick").val(),
		'vo.startTime':$("#modifyTimeStart").val(),
		'vo.endTime':$("#modifyTimeEnd").val()
	}, function(data) {
		if(data.code != 200) return;
		$("#ydkd").text(data.results.ydkd);
		$("#kjkd").text(data.results.kjkd);
		$("#sellerkd").text(data.results.sellerKd);
		$("#fabaoguo").text(data.results.fabaoguo);
	});
});


$(function(){
	$('.inputDate').datePicker({
		startDate:'2016-01-01',
		endDate: '2018-01-01',
		clickInput: true,
		verticalOffset: 35
	});
});

function loadOrderCallBack(index, jq) {
    loadOrder(index + 1);
}

$(function() {
	CDT.orderTemp = $('#orderTemp').remove().val();
	Mustache.parse(CDT.orderTemp);
	loadOrder(1);
	
});
