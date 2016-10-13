CDT={
	configTemp: null,
	currPageNo: 1,
	pageSize: 20
};

//加载
function loadOrder(pageNo){
	Tr.get('/admin/listSysConfig', {
	}, function(data) {
		if(data.code != 200) return;
		if (data.results.length <= 0) {
			$('#noMsg').show();
			return;
		}
		var output = Mustache.render(CDT.configTemp, $.extend(data, {
			modifyTimeStr:function(){
				return new Date(this.modifyTime).Format('yyyy-MM-dd');
			}
		}));
		$('.normTable').show();
		$('#noMsg').hide();
		$('.pagin-btm').show();
		$('#sysContainer').html(output);
	});
}

function initBase(){
	//修改流量任务
	$(document).on('click', '#sysContainer .editBtn', function() {
		var id=$(this).parent().parent().attr("data-id");
		Tr.get('/admin/sysConfig/findConfigById', {
			id:id
		}, function(data) {
			if(data.code!=200){
				return;
			}
			$("#editId").val(data.results.id);
			$("#sysConfigKey").val(data.results.key);
			$("#configValue").val(data.results.value);
			$("#record").val(data.results.record);
			Tr.popup('editConfig');
			$("#editBtnSave").attr("disabled",false);
		});
	});
	//修改后保存
	$("#editBtnSave").click(function(){
		var $id=$("#editId").val();
		var configValue=$("#configValue").val();
		var record=$("#record").val();
		if($.trim(configValue)==""||$.trim(record)==""){
			return alert("值和备注均不能为空！");
		}
			$("#editBtnSave").attr("disabled",true);
			Tr.get('/admin/updateSysConfig', {
				'sysConfig.id':$id,
				'sysConfig.value':configValue,
				'sysConfig.record':record
			}, function(data) {
				if(data.code!=200){
					return;
				}
				loadOrder(1);
				$(".popWrapper").hide();
			});
		
	});
}

function loadOrderCallBack(index, jq) {
    loadOrder(index + 1);
}

$(function() {
	CDT.configTemp = $('#orderTemp').remove().val();
	Mustache.parse(CDT.configTemp);
	loadOrder(1);
	initBase();
});
