$(function() {
	// 自定义消息格式
	$.extend($.validator.messages, {
		required: Tr.error('必填'),
		email: Tr.error('格式不正确')
	});

	var validator = $('#passForm').validate({
		onkeyup: false,
		rules: {
			'email': {
				email: true
			}
		}
	});

	// 修改
	$('#btnBeginToVerify').click(function() {
		// 校验表单参数
		if (validator.form()) {
			$('#passForm').submit();
		}

	});

	//看不清楚，换一张
	$('#changeCaptcha').click(function(){
		$('.firstCap').hide();
		$('#capIframe').show();
	});
});
