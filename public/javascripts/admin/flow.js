CDT = {
	trafficTemp: null,
	currPageNo: 1,
	pageSize: 20,
	token: ''
};

//加载任务
function loadTask(pageNo) {
	var modifyTimeStart = ($.trim($('#modifyTimeStart').val())) ? $.trim($('#modifyTimeStart').val())  : '',
		    modifyTimeEnd = ($.trim($('#modifyTimeEnd').val())) ? $.trim($('#modifyTimeEnd').val()): '';
	Tr.get('/flow/listFlowRecord', {
		'vo.pageNo': CDT.currPageNo,
		'vo.pageSize': CDT.pageSize,
		'vo.status':'WAIT',
		'vo.kwd':$("#kwd").val(),
		'vo.userNick':$("#userNick").val(),
		'vo.taskId':$("#taskId").val(),
		'vo.beginTime':modifyTimeStart,
		'vo.endTime':modifyTimeEnd
	}, function(data) {
		if (data.code == 8001) {
			alert('该功能暂不支持您目前所使用的浏览器！');
		}
		if (data.code != 200) return;
		if (data.results.length <= 0) {
			$('.normTable').hide();
			$('.pagin-btm').hide();
			$('#noMsg').show();
			return;
		}
		//todo
		var output = Mustache.render(CDT.trafficTemp, $.extend(data, {
			statusStr: function() {
				if(this.status=='WAIT'){
					return '待处理';
				}
				if(this.status=='PROCESSING'){
					return '处理中';
				}
				if(this.status=='FINISHED'){
					return '已完成';
				}
			},typeStr:function(){
				if(this.type=="TAOBAOPC"){
					return "PC端";
				}
				if(this.type=="TAOBAOMOBILE"){
					return "app端";
				}
				if(this.type=="JDPC"){
					return "京东PC";
				}
				if(this.type=="TBAD"){
					return "淘宝直通车";
				}
			},shopTypeStr:function(){
				if(this.shopType=='b'){
					return "天猫";
				}if(this.shopType=='c'){
					return "淘宝";
				}
			}
		}));
		$('.normTable').show();
		$('#noMsg').hide();
		$('.pagin-btm').show();
		$('#trafficContainer').html(output);
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

function loadTaskCallBack(index, jq) {
	loadTask(index + 1);
}

	//修改流量任务
$(document).on('click', '#trafficContainer .finishedBtn', function() {
		var id=$(this).parent().parent().attr("data-id");
		Tr.get('/flow/editFlowFinished', {
			id:id,
			status:'PROCESSING'
		}, function(data) {
			if(data.code==-2){
				return alert(data.msg);
			}
			if(data.code!=200){
				return;
			}
			location.href=location.href;
		});
	});
	
function initBase(){
	$("#checkAll").click(function(){
		if($("#checkAll").attr("checked")=='checked'){
			$(".checkFlow").attr("checked","checked");
		}else {
			$(".checkFlow").removeAttr("checked");
		}
	});
	
	$("#allFolwBtn").click(function(){
		var ids="";
		$("input[class=checkFlow]").each(function(){
			if($(this).attr("checked")=='checked'){
				ids+=$(this).parent().parent().attr("data-id")+",";
			}
		});
		if($.trim(ids)==""){
			alert("至少选择一条记录！");
			return;
		}
		Tr.get('/flow/batchFlowFinished', {
			ids:ids,
			status:'PROCESSING'
		}, function(data) {
			if(data.code!=200){
				return;
			}
			location.href=location.href;
		});
	});
	
	$('.inputDate').datePicker({
		startDate:'2016-01-01',
		endDate: '2018-01-01',
		clickInput: true,
		verticalOffset: 35
	});
	
	$("#queryBtn").click(function(){
		loadTask(1);
	});
}

$(function() {
	CDT.token = $("input[name='authenticityToken']").val();
	CDT.trafficTemp = $('#trafficTemp').remove().val();
	Mustache.parse(CDT.trafficTemp);
	initBase();
	loadTask(1);
});