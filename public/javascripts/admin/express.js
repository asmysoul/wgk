CDT={
	orderTemp: null,
	currPageNo: 1,
	pageSize: 20,
	status:'WAIT_SEND_GOODS'
};

//加载订单
function loadOrder(pageNo){
	Tr.get('/admin/express/listOrders', {
		'vo.pageNo': pageNo,
		'vo.pageSize': CDT.pageSize,
		'vo.status': CDT.status
	}, function(data) {
		if(data.code != 200) return;
		if (data.results.length <= 0) {
			$('.normTable').hide();
			$('#noMsg').show();
			$('.pagin-btm').hide();
			return;
		}
		var output = Mustache.render(CDT.orderTemp, $.extend(data, {
			isWaitImport: function() {
				return this.status == 'WAIT_EXPRESS_PRINT'||this.status == 'EXPRESS_PRINT';
			},modifyTimeStr:function(){
				return this.modifyTime == null ? '' : new Date(this.modifyTime).Format('yyyy-MM-dd hh:mm:ss');
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

function initBase(){

	//导出订单
	var validator = $('#exportForm').validate({
		onkeyup: false,
		rules: {
			'vo.exportNo': {
				number: true,
				min: 1
			}
		},
		messages: {
			'vo.exportNo': {
				required: Tr.error('必填'),
				number: Tr.error('必须为数字'),
				min: Tr.error('不能小于1')
			}
		}
	});
	$('#btnExportOrder').click(function() {
		if($('#orderContainer tr').length == 0){
			alert('当前没有要打印运单号的订单');
			return;
		}
		// 校验表单参数
		if (validator.form() && confirm('确定要导出订单么？一旦点击就会修改数据状态！')) {
			$('#shuaxin').show();
			$('#exportForm').submit();
			loadOrder(1);
		}
	});

	$('#shuaxin').click(function(){
		window.location.reload();
	});

	//导入运单号
	$('#upload').click(function(){
		$('#uploadForm').submit();
		$('#iframe').show();
		loadOrder(1);

	});
	$(document).on('click', '.dingdan a', function(){
		$(this).addClass('focus').siblings().removeClass('focus');
	});

	$('#waitExport').click(function(){
		CDT.status = 'WAIT_SEND_GOODS';
		loadOrder(1);
	});
	
	$('#finishedUpload').click(function(){
		CDT.status = 'EXPRESS_PRINT';
		loadOrder(1);
	});

	$('#waitUpload').click(function(){
		CDT.status = 'WAIT_EXPRESS_PRINT';
		loadOrder(1);
	});

	// 重置待导入的订单状态
	$(document).on('click', '#orderContainer .btnResetStatus', function() {
		var orderNo = $(this).parent().parent().find('td').eq(0).text();
		if (!confirm('确认要重置订单记录【' + orderNo + '】到导出前的状态？')) {
			return;
		}
		Tr.post('/admin/express/reset', {
			buyerTaskId: $(this).attr('data-id')
		}, function(data) {
			if (data.code != 200) return;
			loadOrder(1);
		});
	});
	
	$(document).on('click', '#orderContainer .getExpressNo', function() {
		var orderNo = $(this).parent().parent().find('td').eq(0).text();
		Tr.get('/admin/fabaoguo/getExpressNo', {
			orderId: $(this).attr('data-id')
		}, function(data) {
			if (data.code != 200){ 
				alert(data.msg);
				return false;
			}
			loadOrder(1);
		});
	});
}
$(function() {
	CDT.orderTemp = $('#orderTemp').remove().val();
	Mustache.parse(CDT.orderTemp);
	
	initBase();
	loadOrder(1);
});
