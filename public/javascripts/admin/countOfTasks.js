CDT={
	orderTemp: null,
	currPageNo: 1,
	pageSize: 20
};

//加载订单
function loadOrder(pageNo){
	Tr.get('/admin/listCountOfTasks', {
		'type':$("#type").val(),
		'takenTime':$("#takenTime").val(),
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
				return "";
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


function loadOrderCallBack(index, jq) {
    loadOrder(index + 1);
}

$(function() {
	CDT.orderTemp = $('#orderTemp').remove().val();
	Mustache.parse(CDT.orderTemp);
	loadOrder(1);
});
