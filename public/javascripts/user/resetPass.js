$(function(){

	// 自定义消息格式
	$.extend($.validator.messages, {
		required: Tr.error('必填'),
		equalTo: Tr.error('两次输入密码不相同'),
		maxlength: Tr.error('不能超过{0}个字符'),
		minlength: Tr.error('至少{0}个字符'),
	});

	var validator = $('#resetForm').validate({
		onkeyup: false,
		rules: {
			'password': {
				minlength: 6,
				maxlength: 20
			},
			'password2': {
				equalTo: '#txtPass'
			}
		}
	});

	// 修改
	$('#btnModifyPass').click(function() {
		// 校验表单参数
		if (validator.form()) {
			$('#resetForm').submit();
		}

	});

});