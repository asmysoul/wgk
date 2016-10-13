CDT = {
  rowTemp:null,
  pageSize:20,
  taskTemp:null
};

function loadPage(pageNo) {
	var type=$(".tabSwitchWrapper .focus").attr("status");
	var platform=$("#taskPlatform").val();
	var preUrl=window.location.href.indexOf("/admin")>0?"/admin":"/seller";
	var sellerNick=$("#sellerNick").val();
	var shop=$("#shop").val();
	var beginTakeTime=($.trim($("#beginTakeTime").val())) ? $.trim($("#beginTakeTime").val()) + ' 00:00:00' : '';
	var endTakeTime=($.trim($("#endTakeTime").val())) ? $.trim($("#endTakeTime").val()) + ' 00:00:00' : '';
	
	Tr.get(preUrl+'/task/blanceList', {
		'type':type,
		'vo.pageNo' : pageNo,
		'vo.pageSize' : CDT.pageSize,
		'vo.platform':platform,
		'vo.shopName':shop,
		'vo.beginTakeTime':beginTakeTime,
		'vo.endTakeTime':endTakeTime,
		'vo.sellerNick':sellerNick
	}, function(data) {
		if (data.code != 200)
			return;
		if (data.results.length <= 0) {
			$('.normTable').hide();
			$('#noMsg').show();
			$('.pagin-btm').hide();
			return;
		}
		var url=preUrl+"/task/exportSellerBlance?vo.platform="+platform+"&&vo.shopName="+shop+"&&vo.beginTakeTime="+beginTakeTime+"&&vo.endTakeTime="+endTakeTime+"&&type="+type+"&&vo.sellerNick="+sellerNick;
		$("#expoptBtn").attr("href",url);
		if($.trim(type)=='BUYERTASK'){
			var output = Mustache.render(CDT.rowTemp, $.extend(data, {
				takenTime : function() {
					return new Date(this.takeTime).Format('yyyy-MM-dd hh:mm:ss');
				},expressIngotStr:function(){
					return this.expressIngot/100;
				},itemPriceStr:function(){
					return this.itemPrice/100;
				}
			}));
			$('#blanceContainer').html(output);
			$('.buyerTaskTable').show();
			$('#noMsg').hide();
		}else if($.trim(type)=='TASK'){
			var taskOutput = Mustache.render(CDT.taskTemp, $.extend(data, {
				takenTime : function() {
					return new Date(this.takeTime).Format('yyyy-MM-dd hh:mm:ss');
				},expressIngotStr:function(){
					return this.expressIngot/100;
				},itemPriceStr:function(){
					return this.itemPrice/100;
				}
			}));
			$("#taskBlanceContainer").html(taskOutput);
			$('.TaskTable').show();
			$('#taskNoMsg').hide();
		}
		$("#iframe").show();
		
		$('.pagin-btm').show();
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
}
function loadOrderCallBack(index, jq) {
	loadPage(index + 1);
}
function initBaseBind() {
	$('.inputDate').datePicker({
		startDate: '2016-01-01',
		endDate: '2018-01-01',
		clickInput: true,
		verticalOffset: 35
	});
	$("#taskPlatform").change(function() {
		$("#shop").html("");
		var platform = $("#taskPlatform").val();
		var html="<option value=''>全部</option>";
		if (platform != '') {
			var url = '/user/shop/' + platform;
			Tr.get(url, {
				
			}, function(data) {
				if (data.code != 200)
					return;
				for (var i = 0; i < data.results.length; i++) {
					html+="<option value='"+data.results[i].nick+"'>"+data.results[i].nick+"</option>";
				}
				$("#shop").append(html);
			});
		}else {
			$("#shop").append(html);
		}
		
	});
	
	$("#queryBtn").click(function(){
		loadPage(1);
	});
	$("#nuExpoptBtn").click(function(){
		alert("当前导出数量过多！请按条件查询后再导出");
	});
	
	$(document).on('click', '.taskTypeTab', function() {
		var id_toshow = $(this).addClass('focus').siblings().removeClass('focus').end().attr('for');
		 $('.buyerTask-panel').hide();
		 if (id_toshow == 'buyerTask') {
			$("#taskTable").hide();
		}
		 $('#' + id_toshow).show();
		 loadPage(1);
	});
	
}
$(function() {
	CDT.rowTemp = $('#rowTemp').remove().val();
	Mustache.parse(CDT.rowTemp);
	CDT.taskTemp = $('#taskTemp').remove().val();
	Mustache.parse(CDT.taskTemp);
	loadPage(1);
	initBaseBind();
});