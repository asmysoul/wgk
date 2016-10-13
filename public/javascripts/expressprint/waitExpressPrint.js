CDT = {
	orderTemp : null,
	currPageNo : 1,
	pageSize : 100,
	status : 'WAIT_SEND_GOODS'
};

// 加载订单
function loadOrder(pageNo) {
	var shopName = $("#nick").val();
	Tr.get('/express/listOrders', {
		'vo.pageNo' : pageNo,
		'vo.shopName' : shopName,
		'vo.pageSize' : CDT.pageSize,
		'vo.status' : CDT.status
	}, function(data) {
		if (data.code != 200)
			return;
		if (data.results.length <= 0) {
			$('.normTable').hide();
			$('#noMsg').show();
			$('.pagin-btm').hide();
			return;
		}
		var output = Mustache.render(CDT.orderTemp, $.extend(data, {
			isWaitImport : function() {
				return this.status == 'WAIT_EXPRESS_PRINT';
			}
		}));
		$('.normTable').show();
		$('#noMsg').hide();
		$('.pagin-btm').show();
		$('#orderContainer').html(output);
		$('.pagination').pagination(data.totalCount, {
			items_per_page : CDT.pageSize,
			num_display_entries : 5,
			current_page : pageNo,
			num_edge_entries : 2,
			callback : loadOrderCallBack,
			callback_run : false
		});
		CDT.currPageNo = pageNo;
	});
	
	Tr.get('/express/orderCount',{}, function(data) {
		var waitPrint="待打印订单("+data.waitExpressPrintCount+")";
		var printing="正在打印订单("+data.expressPrintingCount+")";
		var finished="已经打印订单("+data.expressPrintFinishCount+")";
		$("#waitExpressPrintText").text(waitPrint);
		$("#expressPrintingText").text(printing);
		$("#expressPrintFinishText").text(finished);
	});
}

function loadOrderCallBack(index, jq) {
	loadOrder(index + 1);
}
function initBase() {
	var validator = $('#waitPrintForm').validate({
		onkeyup : false,
		rules : {
			'vo.exportNo' : {
				number : true,
				min : 1
			}
		},
		messages : {
			'vo.exportNo' : {
				required : Tr.error('必填'),
				number : Tr.error('必须为数字'),
				min : Tr.error('不能小于1')
			}
		}
	});

	$("#queryBtn").click(function() {
		loadOrder(1);
	});
	$(document).on('click', '#orderContainer .queryByShopName', function() {
		$("#nick").val($(this).attr('data-name'));
		loadOrder(1);
	});
	// 准备打印订单
	$("#btnPrintOrder")
			.click(
					function() {
						if ($('#orderContainer tr').length == 0) {
							alert('当前没有要打印运单号的订单');
							return;
						}
						// 校验表单参数
						if (validator.form()
								&& confirm('确定要打印订单么？一旦点击就会修改数据状态！')) {
							$('#shuaxin').show();
							var printId = "";// 需要打印订单的id
							var printNum = $("#printNum").val();// 设置打印的数量
							var waitPrintNum = $('#orderContainer tr').length >= printNum ? printNum
									: $('#orderContainer tr').length;// 获取可以打印的订单数量
							for (var i = 0; i < waitPrintNum; i++) {
								var tab = "#orderContainer tr:eq(" + i
										+ ") td:eq(0)";// 获取表格第i行第一列的位置
								var id = $(tab).text().split("-")[1];
								printId += id + ",";
							}
							Tr.get('/express/preparePrintExpress', {
								'vo.exportNo' : waitPrintNum,
								'vo.printId' : printId
							}, function(data) {
								if (data.code == 200) {
									loadOrder(1);
									return;
								} else {
									alert("操作失败！请联系管理员");
									return;
								}
							});
						}
					});
}
$(function() {
	CDT.orderTemp = $('#orderTemp').remove().val();
	Mustache.parse(CDT.orderTemp);
	loadOrder(1);
	initBase();
});
