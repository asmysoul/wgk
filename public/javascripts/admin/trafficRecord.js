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
	Tr.get('/admin/traffic/list', {
		'vo.pageNo': pageNo,
		'vo.pageSize': CDT.pageSize,
		'vo.status':$("#status").val(),
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

function initBase(){
	var validator = $('#addTrafficForm').validate({
		onkeyup : false,
		rules : {
			'vo.shopType' : {
				required : true
			},
			'vo.kwd' : {
				required : true
			},
			'vo.nid' : {
				required : true,
				number:true
			},
			'vo.times' : {
				required : true,
				number:true
			},
			'vo.path1' : {
				required : true,
				number:true,
				min:0,
				max:100
			},
			'vo.path2' : {
				required : true,
				number:true,
				min:0,
				max:100
			},
			'vo.path3' : {
				required : true,
				number:true,
				min:0,
				max:100
			},
			'vo.sleepTime' : {
				required : true,
				number:true
			},
			'vo.clickStart' : {
				min:0,
				max:23,
				required : true,
				number:true
			},
			'vo.clickEnd' : {
				min:0,
				max:23,
				required : true,
				number:true
			},
			'vo.beginTime' : {
				required : true
			},
			'vo.endTime' : {
				required : true
			}
		},
		messages : {
			'vo.shopType' : {
				required : Tr.error('必填')
			},
			'vo.kwd' : {
				required : Tr.error('必填')
			},
			'vo.nid' : {
				required : Tr.error('必填'),
				number:Tr.error('必须为数字')
			},
			'vo.times' : {
				required : Tr.error('必填'),
				number:Tr.error('必须为数字')
			},
			'vo.path1' : {
				required : Tr.error('必填'),
				number:Tr.error('必须为数字'),
				min:Tr.error('不能小于0'),
				max:Tr.error('不能大于100')
			},
			'vo.path2' : {
				required : Tr.error('必填'),
				number:Tr.error('必须为数字'),
				min:Tr.error('不能小于0'),
				max:Tr.error('不能大于100')
			},
			'vo.path3' : {
				required : Tr.error('必填'),
				number:Tr.error('必须为数字'),
				min:Tr.error('不能小于0'),
				max:Tr.error('不能大于100')
			},
			'vo.sleepTime' : {
				required : Tr.error('必填'),
				number:Tr.error('必须为数字')
			},
			'vo.clickStart' : {
				min:Tr.error('不能小于0'),
				max:Tr.error('不能大于23'),
				required : Tr.error('必填'),
				number:Tr.error('必须为数字')
			},
			'vo.clickEnd' : {
				min:Tr.error('不能小于0'),
				max:Tr.error('不能大于23'),
				required : Tr.error('必填'),
				number:Tr.error('必须为数字')
			},
			'vo.beginTime' : {
				required : Tr.error('必填')
			},
			'vo.endTime' : {
				required : Tr.error('必填')
			}
		}
	});
	
	var validatorEdit = $('#editTrafficForm').validate({
		onkeyup : false,
		rules : {
			'editNid' : {
				required : true,
				number:true
			},
			'editTimes' : {
				required : true,
				number:true
			},
			'editPath1' : {
				required : true,
				number:true,
				min:0,
				max:100
			},
			'editPath2' : {
				required : true,
				number:true,
				min:0,
				max:100
			},
			'editPath3' : {
				required : true,
				number:true,
				min:0,
				max:100
			},
			'editSleepTime' : {
				required : true,
				number:true
			},
			'editClickStart' : {
				min:0,
				max:23,
				required : true,
				number:true
			},
			'editClickEnd' : {
				min:0,
				max:23,
				required : true,
				number:true
			},
			'editBeginTime' : {
				required : true
			},
			'editEndTime' : {
				required : true
			}
		},
		messages : {
			'editNid' : {
				required : Tr.error('必填'),
				number:Tr.error('必须为数字')
			},
			'editTimes' : {
				required : Tr.error('必填'),
				number:Tr.error('必须为数字')
			},
			'editPath1' : {
				required : Tr.error('必填'),
				number:Tr.error('必须为数字'),
				min:Tr.error('不能小于0'),
				max:Tr.error('不能大于100')
			},
			'editPath2' : {
				required : Tr.error('必填'),
				number:Tr.error('必须为数字'),
				min:Tr.error('不能小于0'),
				max:Tr.error('不能大于100')
			},
			'editPath3' : {
				required : Tr.error('必填'),
				number:Tr.error('必须为数字'),
				min:Tr.error('不能小于0'),
				max:Tr.error('不能大于100')
			},
			'editSleepTime' : {
				required : Tr.error('必填'),
				number:Tr.error('必须为数字')
			},
			'editClickStart' : {
				min:Tr.error('不能小于0'),
				max:Tr.error('不能大于23'),
				required : Tr.error('必填'),
				number:Tr.error('必须为数字')
			},
			'editClickEnd' : {
				min:Tr.error('不能小于0'),
				max:Tr.error('不能大于23'),
				required : Tr.error('必填'),
				number:Tr.error('必须为数字')
			},
			'editBeginTime' : {
				required : Tr.error('必填')
			},
			'editEndTime' : {
				required : Tr.error('必填')
			}
		}
	});
	$("#addTrafficBtn").click(function(){
		$("#btnSave").attr("disabled",false);
		var fmtTime=new Date().Format('yyyy-MM-dd');
		$("#begin_time").val(fmtTime);
		$("#end_time").val(fmtTime);
		Tr.popup('addTraffic');
	});
	$("#type").change(function(){
		if($.trim($("#type").val())=='JDPC'){
			$("#shopTypeDiv").hide();
		}else{
			$("#shopTypeDiv").show();
		}
	});
	$("#shopType").change(function(){
		if($.trim($("#shopType").val())=='c'){
			$("#path2").val(0);
			$("#path2").attr("disabled",true);
			$("#path3").val(0);
			$("#path3").attr("disabled",true);
		}else {
			$("#path2").removeAttr("disabled",false);
			$("#path3").removeAttr("disabled",false);
		}
	});
	$("#addTaskId").blur(function(){
		Tr.get('/admin/getSellerIdByTask', {
			taskId:$("#addTaskId").val()
		}, function(data) {
			if(data.code!=200){
				return;
			}
			$("#userId").val(data.results);
		});
	});
	$("#queryBtn").click(function(){
		loadTask(1);
	});
	//判断3路径之和为100
	function isPath(path1,path2,path3){
		var rest=parseInt(path1)+parseInt(path2)+parseInt(path3);
		return rest==100;
	}
	
	$("#btnSave").click(function(){
		if (validator.form()) {
			//TODO添加到数据库
			if(!isPath($("#path1").val(), $("#path2").val(), $("#path3").val())){
				return alert("3路径之和必须为100");
			}
			$("#addTrafficForm").submit();
			$("#btnSave").attr("disabled",true);
		}
	});
	//修改流量任务
	$(document).on('click', '#trafficContainer .editBtn', function() {
		var id=$(this).parent().parent().attr("data-id");
		Tr.get('/admin/traffic/findTraffic', {
			id:id
		}, function(data) {
			if(data.code==-2){
				return alert(data.msg);
			}
			if(data.code!=200){
				return;
			}
			$("#editId").val(data.results.id);
			$("#editTimes").val(data.results.times);
			$("#editPath1").val(data.results.path1);
			$("#editPath2").val(data.results.path2);
			$("#editPath3").val(data.results.path3);
			$("#editSleepTime").val(data.results.sleepTime);
			$("#editClickStart").val(data.results.clickStart);
			$("#editClickEnd").val(data.results.clickEnd);
			$("#editBeginTime").val(data.results.beginTime);
			$("#editEndTime").val(data.results.endTime);
			$("#shopType").val(data.results.shopType);
			
			var $shopType=data.results.shopType;
			if($shopType=='c'){
				$(".editSearchPath").hide();
			}else{
				$(".editSearchPath").show();
			}
			Tr.popup('editTraffic');
			$("#editBtnSave").attr("disabled",false);
		});
	});
	//修改后保存
	$("#editBtnSave").click(function(){
		var $id=$("#editId").val();
		var $editTimes=$("#editTimes").val();
		var $editPath1=parseInt($("#editPath1").val())>0?parseInt($("#editPath1").val()):0;
		var $editPath2=parseInt($("#editPath2").val())>0?parseInt($("#editPath2").val()):0;
		var $editPath3=parseInt($("#editPath3").val())>0?parseInt($("#editPath3").val()):0;
		var $editSleepTime=$("#editSleepTime").val();
		var $editClickStart=$("#editClickStart").val();
		var $editClickEnd=$("#editClickEnd").val();
		var $editBeginTime=$("#editBeginTime").val();
		var $editEndTime=$("#editEndTime").val();
		if(!isPath($editPath1,$editPath2, $editPath3)){
			return alert("3路径之和必须为100");
		}
		if(validatorEdit.form()){
			$("#editBtnSave").attr("disabled",true);
			Tr.get('/admin/traffic/modifyTraffic', {
				'vo.id':$id,
				'vo.times':$editTimes,
				'vo.path1':$editPath1,
				'vo.path2':$editPath2,
				'vo.path3':$editPath3,
				'vo.sleepTime':$editSleepTime,
				'vo.clickStart':$editClickStart,
				'vo.clickEnd':$editClickEnd,
				'vo.beginTime':$editBeginTime,
				'vo.endTime':$editEndTime
			}, function(data) {
				if(data.code!=200){
					return;
				}
				loadTask(1);
				$(".popWrapper").hide();
			});
		}
	});
	$('.inputDate').datePicker({
		startDate:'2016-01-01',
		endDate: '2018-01-01',
		clickInput: true,
		verticalOffset: 35
	});
}

$(function() {
	CDT.token = $("input[name='authenticityToken']").val();
	CDT.trafficTemp = $('#trafficTemp').remove().val();
	Mustache.parse(CDT.trafficTemp);
	initBase();
	loadTask(1);
});