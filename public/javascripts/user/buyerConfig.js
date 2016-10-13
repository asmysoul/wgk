CDT = {
	
};

//加载列表

function initBase() {
	
	$(document).on('click', '#queryBtn', function() {
		var isClearView = $('#isClearView').val();
		Tr.post('/buyer/buyerConfig/modifyBuyerConfig', {
			'config.isClearView': isClearView
		}, function(data) {
			if(data.code==800101) alert(data.msg);
			if(data.code!=200) return;
			alert('修改成功');
		});
	});
}

$(function() {
	
	initBase();
	
});