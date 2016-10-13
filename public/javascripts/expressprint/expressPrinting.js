CDT = {
	orderTemp : null,
	currPageNo : 1,
	pageSize : 500,
	status : 'WAIT_EXPRESS_PRINT'
};
//打印订单
function CreateDataBill(expressNo,state,city,region,address,consignee,mobile,tbSellerNick,fullAddress,dataTime,allAddress) {

	LODOP.SET_PRINT_PAPER(0,0,872,489,expressNo);
	LODOP.ADD_PRINT_TEXT(96,139,164,25,tbSellerNick);
	LODOP.SET_PRINT_STYLEA(1,"FontSize",12);
	LODOP.SET_PRINT_STYLEA(1,"FontColor",16711680);
	LODOP.SET_PRINT_STYLEA(1,"Bold",1);
	LODOP.ADD_PRINT_TEXT(188,136,214,26,tbSellerNick);
	LODOP.SET_PRINT_STYLEA(2,"FontSize",12);
	LODOP.SET_PRINT_STYLEA(2,"FontColor",16711680);
	LODOP.SET_PRINT_STYLEA(2,"Bold",1);
	LODOP.ADD_PRINT_TEXT(94,488,112,28,consignee);
	LODOP.SET_PRINT_STYLEA(3,"FontSize",15);
	LODOP.SET_PRINT_STYLEA(3,"FontColor",16711680);
	LODOP.SET_PRINT_STYLEA(3,"Bold",1);
	LODOP.ADD_PRINT_TEXT(163,479,271,56,address);
	LODOP.SET_PRINT_STYLEA(4,"FontSize",12);
	LODOP.SET_PRINT_STYLEA(4,"FontColor",16711680);
	LODOP.SET_PRINT_STYLEA(4,"Bold",1);
	LODOP.ADD_PRINT_TEXT(130,477,90,26,state);
	LODOP.SET_PRINT_STYLEA(5,"FontSize",12);
	LODOP.SET_PRINT_STYLEA(5,"FontColor",16711680);
	LODOP.SET_PRINT_STYLEA(5,"Bold",1);
	LODOP.ADD_PRINT_TEXT(129,573,93,29,city);
	LODOP.SET_PRINT_STYLEA(6,"FontSize",12);
	LODOP.SET_PRINT_STYLEA(6,"FontColor",16711680);
	LODOP.SET_PRINT_STYLEA(6,"Bold",1);
	LODOP.ADD_PRINT_TEXT(127,677,75,32,region);
	LODOP.SET_PRINT_STYLEA(7,"FontSize",12);
	LODOP.SET_PRINT_STYLEA(7,"FontColor",16711680);
	LODOP.SET_PRINT_STYLEA(7,"Bold",1);
	LODOP.ADD_PRINT_TEXT(223,505,180,27,mobile);
	LODOP.SET_PRINT_STYLEA(8,"FontSize",12);
	LODOP.SET_PRINT_STYLEA(8,"FontColor",16711680);
	LODOP.SET_PRINT_STYLEA(8,"Bold",1);
	LODOP.ADD_PRINT_TEXT(92,660,95,30,state);
	LODOP.SET_PRINT_STYLEA(9,"FontSize",15);
	LODOP.SET_PRINT_STYLEA(9,"FontColor",16711680);
	LODOP.SET_PRINT_STYLEA(9,"Bold",1);
	LODOP.ADD_PRINT_TEXT(326,102,109,43,"已验试");
	LODOP.SET_PRINT_STYLEA(10,"FontSize",20);
	LODOP.SET_PRINT_STYLEA(10,"FontColor",16711680);
	LODOP.SET_PRINT_STYLEA(10,"Bold",1);
	LODOP.ADD_PRINT_TEXT(369,443,312,42,allAddress);
	LODOP.SET_PRINT_STYLEA(11,"FontSize",20);
	LODOP.SET_PRINT_STYLEA(11,"FontColor",16711680);
	LODOP.SET_PRINT_STYLEA(11,"Bold",1);
	LODOP.ADD_PRINT_TEXT(383,222,100,20,dataTime);
	LODOP.SET_PRINT_STYLEA(12,"FontSize",10);
	LODOP.SET_PRINT_STYLEA(12,"FontColor",16711680);
	LODOP.SET_PRINT_STYLEA(12,"Bold",1);
}

// 加载订单
function loadOrder(pageNo) {
	Tr.get('/express/listOrders', {
		'vo.pageNo' : pageNo,
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
	var validator = $('#printingForm').validate({
		onkeyup : false,
		rules : {
			'vo.exportNo' : {
				number : true,
				min : 1,
				max : 500
			},
			'vo.expressNo' : {
				required : true,
				number:true
			}
		},
		messages : {
			'vo.exportNo' : {
				required : Tr.error('必填'),
				number : Tr.error('必须为数字'),
				min : Tr.error('不能小于1'),
				max : Tr.error('不能大于500')
			},
			'vo.expressNo' : {
				required : Tr.error('必填'),
				number : Tr.error('必须为数字')
			}
		}
	});
	// 重置待导入的订单状态
	$(document).on('click', '#orderContainer .btnResetStatus', function() {
		var orderNo = $(this).parent().parent().find('td').eq(0).text();
		if (!confirm('确认要重置订单记录【' + orderNo + '】到处理前的状态？')) {
			return;
		}
		Tr.post('/express/reset', {
			buyerTaskId : $(this).attr('data-id')
		}, function(data) {
			if (data.code != 200)
				return;
			loadOrder(1);
		});
	});
	// 自动生成快递单号
	$("#orderBtnOK")
			.click(
					function() {
						// 初始化运单号
						for (var j = 0; j < $('#orderContainer tr').length; j++) {
							var tab = "#orderContainer tr:eq(" + j
									+ ") td:eq(6)";// 获取表格第i行第5列的位置
							$(tab).text("待处理");
						}
						// 开始自动生成
						if (validator.form()) {
							var expressNo = $("#expressNo").val();// 起始单号
							var printNum = $("#printNum").val();// 打印数量
							var printExpress = $('#orderContainer tr').length >= printNum ? printNum
									: $('#orderContainer tr').length;// 获取可以打印的订单数量
							for (var i = 0; i < printExpress; i++) {
								var tab = "#orderContainer tr:eq(" + i
										+ ") td:eq(6)";// 获取表格第i行第6列的位置
								var restExpressNum = parseInt(expressNo) + i;// 自动生成的订单号
								$(tab).text(restExpressNum);
							}
						}

					});
	// 打印订单
	$("#printOrderBtn").click(function() {
		if (validator.form()) {
			// TODO判断需要打印的订单是否比实际的订单量大，调用控件打印订单
			var printNum = $("#printNum").val();// 打印数量
			var tabRow=$('#orderContainer tr').length;
			Tr.get('/express/listOrders', {
				'vo.pageNo' : CDT.currPageNo,
				'vo.pageSize' : CDT.pageSize,
				'vo.status' : CDT.status
			}, function(data) {
				if (data.code != 200)
					return;
				var actualPrintNum=printNum>=tabRow?tabRow:printNum;
				var rest=data.results;
				for (var i = 0; i < actualPrintNum; i++) {
					var tabId="#orderContainer tr:eq(" + i + ") td:eq(0)";
					if($.trim($(tabId).text().split("-")[1])==rest[i].id){
					  var tabExpressNo = "#orderContainer tr:eq(" + i + ") td:eq(6)";// 获取表格第i行第6列的位置
					  var expressNo= $(tabExpressNo).text();//获取订单号
					  if(expressNo=='待处理'){//防止有人生成单号后修改打印数量
						  break;
					  }
					  var state=rest[i].state;//省
					  var city=rest[i].city;//市
					  var region=rest[i].region;//区
					  var address=rest[i].address;
					  var consignee=rest[i].consignee;//收货人
					  var mobile=rest[i].mobile;
					  var tbSellerNick=rest[i].tbSellerNick;//卖家呢称
					  var fullAddress=rest[i].fullAddress;
					  var now = new Date();
					  var dataTime=now.getFullYear()+"-"+(now.getMonth()+1)+"-"+now.getDate();
					  var allAddress=state+" "+city+" "+region;
					  //传参调用打印控件
					  CreateDataBill(expressNo,state,city,region,address,consignee,mobile,tbSellerNick,fullAddress,dataTime,allAddress);
					 // LODOP.PRINT_DESIGN();//打印预览
					  LODOP.PRINT();
					}
				 }
				});
		}
	});
	// 确认打印完毕 将运单号保存到数据库
	$("#reviewBtn").click(function() {
		if(!confirm("确认生成的订单号，已经打印完毕?")){
			return;
		}
		var printintOrders = "";// 需要保存数据可得运单号,id-expressNo 格式
		for (var k = 0; k < $('#orderContainer tr').length; k++) {
			var tab = "#orderContainer tr:eq(" + k + ") td:eq(6)";// 获取表格第i行第6列的位置
			if ($(tab).text() != '待处理') {
				var tabId = "#orderContainer tr:eq(" + k + ") td:eq(0)";// 获取表格第i行第1列的位置
				var id = $(tabId).text().split("-")[1];
				printintOrders += id + "-" + $(tab).text() + ",";
			}
		}
		if (printintOrders == '') {
			alert("请先生成订单号后在操作!");
			return;
		}
		// 提交打印的订单
		Tr.get('/express/printOrderFinsh', {
			expressNos : printintOrders
		}, function(data) {
			if (data.code != 200) {
				alert(data.msg);
				return;
			}
			if(data.code==200){
				var expressNo = $("#expressNo").val('');// 起始单号
				var printNum = $("#printNum").val('');// 打印数量
				alert("保存成功！共保存"+data.results+"条数据！");
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
