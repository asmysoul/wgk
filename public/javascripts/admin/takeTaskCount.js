CDT={
	orderTakenContainerTemp:null,
	orderTemp: null,
	currPageNo: 1,
	pageSize: 7,
};

//加载订单
function loadOrder(pageNo){
	Tr.get('/admin/listTakeCount', {
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

$(document).on('click', '.takenBuer', function() {
	var takeTime=$(this).attr("data-time");
	Tr.get('/admin/findNewBuyerTakenNum', {
		'takenTime':takeTime
	}, function(data) {
		if(data.code != 200) return;
		if (data.results.length <= 0) {
			$("#detail").html("");
			return;
		}
		var html="";
		for (var i = 0; i < data.results.length; i++) {
			html+="<tr><td>"+data.results[i].nick+"</td><td>"+data.results[i].orderNum+"</td></tr>";
		}
		$("#detail").html(html);
	});
});


$(document).on('click', '.publishSeller', function() {
	var takeTime=$(this).attr("data-time");
	Tr.get('/admin/findNewSellerPublishNum', {
		'takenTime':takeTime
	}, function(data) {
		if(data.code != 200) return;
		if (data.results.length <= 0) {
			$("#detail").html("");
			return;
		}
		var html="";
		for (var i = 0; i < data.results.length; i++) {
			html+="<tr><td>"+data.results[i].nick+"</td><td>"+data.results[i].orderNum+"</td></tr>";
		}
		$("#detail").html(html);
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
