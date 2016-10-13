CDT = {
	smsSent: false,
	countDownNum:0,
	counter:void 0,
	formSent:true
};

function existMsg(field) {
	return Tr.error('该' + field + '已经被注册');
}

function countingFunc(){
	if(CDT.countDownNum>0){
		$('#btncaptcha').val(--CDT.countDownNum+'秒后可重发');
	}else{
		$('#btncaptcha').removeAttr('disabled').val('重新发送');
		clearInterval(CDT.counter);
		CDT.counter = void 0;
		CDT.countDownNum = 0;
	}
}

function validateRulesSet(){
	// 自定义消息格式
	$.extend($.validator.messages, {
		required: Tr.error('必填'),
		email: Tr.error('邮箱地址不正确'),
		qq: Tr.error('QQ号码不正确'),
		equalTo: Tr.error('两次输入密码不同'),
		mobile: Tr.error('手机号码不正确'),
		nick: Tr.error('用户名只能由中文英文字母及数字组成'),
		maxlength: Tr.error('不能超过{0}个字符'),
		minlength: Tr.error('至少{0}个字符'),
		same: Tr.error('不能与用户名相同')

	});
	$.validator.addMethod('same', function(value) {
		if(value!=$('input[name="user.nick"]').val()){
			return true;
		}
		return false;
	});
	$.validator.addMethod('smsValidCode', function(value) {
			return Tr.regs.captcha.test($('#smsValidCode').val());
	}, Tr.error('验证码格式错误'));

	var validator = $('#registForm').validate({
		onkeyup: false,
		rules: {
			'user.nick': {
				minlength: 4,
				maxlength: 15,
				nick: true,
				remote: '/checkRegist'
			},
			'user.password': {
				minlength: 6,
				maxlength: 20,
				same:true
			},
			'user.qq': {
				qq: true
			},
			'user.email': {
				email: true
			},
			'user.mobile': {
				mobile: true
				
			},
			'smsValidCode':{
				required: {
					depends: function(element) {
						return $('#nick').val().length > 0 && $('#mobile').val().length > 0 && CDT.smsSent;
					}
				},
				smsValidCode: {
					depends: function(element) {
						return $('#nick').val().length > 0 && $('#mobile').val().length > 0 && CDT.smsSent;
					}
				},
				remote: {
					url: '/checkRegist',
					type: 'get',
					data: {
						'user.nick': function() {
							return $("#nick").val();
						}
					}
				}
			},
			'pass2': {
				equalTo: '#password'
			}
		},
		messages: {
			'user.nick': {
				remote: existMsg('用户名')
			},
			'user.qq': {
				remote: existMsg('QQ')
			},
			'user.email': {
				remote: existMsg('邮箱地址')
			},
			'user.mobile': {
				remote: existMsg('手机号码')
			},
			'smsValidCode':{
				remote: Tr.error('验证码错误')
			}
		}
	});
	return validator;
}

//获取长度为len的随机字符串
function _getRandomString(len) {
    len = len || 6;
    var $chars = 'abcdefghijklmnprstwxyz'; // 默认去掉了容易混淆的字符oOLl,9gq,Vv,Uu,I1
    var maxPos = $chars.length;
    var pwd = '';
    for (i = 0; i < len; i++) {
        pwd += $chars.charAt(Math.floor(Math.random() * maxPos));
    }
    return pwd;
}

$(function() {
	$("#nick").val(_getRandomString());
	var validator = validateRulesSet();
	// 注册
	$('#btnRegist').click(function() {
		if(!$('#radAgree').is(':checked')){
			alert('请先同意《挖顾客服务协议》！');
			return;
		}
		// 校验表单参数
		if (validator.form()) {

			$('#registForm').submit();
			$('#btnRegist').attr('disabled','true');
		}

	});

	// 重新发送验证邮件
	$('#btnResend').click(function() {
		Tr.post('/regist/resend', {
			email: $(this).attr('data'),
			authenticityToken: $('input[name="authenticityToken"]').val()
		}, function(data) {
			if (data.code == 800101) {
				alert('操作被系统拒绝！');
				return;
			}

			if (data.code != 200) return;
			alert('邮件已重新发送！');
		});
	});
	//获取验证码
	$('#btncaptcha').click(function(){
		CDT.smsSent = false;
		// 当其他校验通过之后才可以发送
		if (!validator.form()) {
			return;
		}
		var mobile = $('#mobile').val(),nick = $('#nick').val();
		
	});

});