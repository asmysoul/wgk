CDT = {
	orderTemp : null,
	currPageNo : 1,
	pageSize : 20,
	status : 'EXPRESS_PRINT'
};

// 加载订单
function loadOrder(pageNo) {
	Tr.get('/express/listOrders', {
		'vo.pageNo' : pageNo,
		'vo.pageSize' : CDT.pageSize,
		'vo.status' : CDT.status,
		'vo.expressNo':$("#queryExpressNo").val()
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
			},
			modifytime: function() {
				return this.modifyTime == null ? '' : new Date(this.modifyTime).Format('yyyy-MM-dd hh:mm:ss');
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
	$(document)
			.on(
					'click',
					'#orderContainer .btnUpdateStatus',
					function() {
						var id = $(this).attr('data-id');
						var expressNo = $(this).parent().parent().find(
								".expressNo").html();
						if ($(this).text() == "修改") {
							var htmlForm = "<form id='updateForm' method='post'><input type='text' maxlength='15' style='width:110px;' class='required inputText' value='"
									+ expressNo
									+ "' name='expressNum' id='updateExpressNum' /></form>";
							$(this).parent().parent().find(".expressNo").html(
									htmlForm);
							$("#updateExpressNum").focus();
							$(this).text("确定");
						} else if ($(this).text() == "确定") {
							var validator = $('#updateForm').validate({
								onkeyup : false,
								rules : {
									'expressNum' : {
										number : true,
										min : 1
									}
								},
								messages : {
									'expressNum' : {
										required : Tr.error('必填'),
										number : Tr.error('必须为数字'),
										min : Tr.error('不能小于1')
									}
								}
							});
							if (validator.form()) {
								var updateExpressNum = $("#updateExpressNum")
										.val();
								Tr.get('/express/updateById', {
									'id' : id,
									'expressNo' : updateExpressNum
								}, function(data) {
									if (data.code != 200) {
										alert(data.msg);
										return;
									}
									loadOrder(1);
								});
							}
						}
					});
	
	// 重置正在打印的订单状态
	$(document).on('click', '#orderContainer .btnResetStatus', function() {
		var orderNo = $(this).parent().parent().find('td').eq(0).text();
		if (!confirm('确认要重置订单记录【' + orderNo + '】到处理前的状态？')) {
			return;
		}
		Tr.post('/express/resetToPrinting', {
			buyerTaskId : $(this).attr('data-id')
		}, function(data) {
			if (data.code != 200)
				return;
			loadOrder(1);
		});
	});
	
	$("#queryBtn").click(function(){
		loadOrder(1);
	});
}

$(function() {
	CDT.orderTemp = $('#orderTemp').remove().val();
	Mustache.parse(CDT.orderTemp);
	loadOrder(1);
	initBase();
});
