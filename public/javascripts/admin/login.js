$(function() {
	$('#changeCaptcha').click(function(){
		$('.firstCap').hide();
		$('#capIframe').show();
	});
	// 自定义消息格式
	$.extend($.validator.messages, {
		required: Tr.error('必填'),
		name: Tr.error('用户名只能由中文英文字母及数字组成'),
		maxlength: Tr.error('不能超过{0}个字符'),
		minlength: Tr.error('至少{0}个字符')
	});
	var validator = $('#loginForm').validate({
		rules: {
			name: {
				minlength: 2,
				maxlength: 15,
				name: true
			},
			pass: {
				minlength: 6,
				maxlength: 20
			}
		}
	});
	$('#btnLogin').click(function() {
		if (validator.form()) {
			$('#loginForm').submit();
		}
	});

	$('#pass').keydown(function(event) {
		if (event.keyCode == '13' && validator.form()) {
			$('#loginForm').submit();
		}
	});

});