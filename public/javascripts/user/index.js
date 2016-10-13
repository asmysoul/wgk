CDT = {
	currentId:'',
	loginModifyModule :null,
	payModifyModule: null,
	qqModifyModule: null,
	emailModifyModule: null,
	mobileModifyModule: null,
	noticeModule: null
};

//修改登录密码函数
function modifyloginPass(){
	var oldPass = $.trim($('#txtOldPassword').val()),
		newPass = $.trim($('#txtNewPassword').val());
	if (oldPass == newPass) {
		alert('新密码不能与旧密码相同，请重新输入！');
		return;
	}
	Tr.post('/user/modify', {
		'user.id': CDT.currentId,
		'user.password': newPass,
		oldPass: oldPass
	}, function(data) {
		if (data.code != 200) {
			return;
		}
		$('#modifyModule1').html('');
		alert('修改成功');
	});
}

//修改用户单个信息函数
function modifyDetail(params,module){
	Tr.post('/user/modify',params,function(data){
		if(data.code!=200){
			return;
		}
		$('#'+module).html('');
		alert('修改成功');
	});

}

function loadNotice(){
	Tr.get('/user/notice/list',{num:5},function(data){
		if(data.code != 200)return;
		if (data.results.length <= 0) {
			$('.announceList').addClass('warnBox').html('<span class="iconfont">&#xf00b6;</span>没有公告!');
			return;
		}
		var output = Mustache.render(CDT.noticeModule,$.extend(data,{
			createtime:function(){
				return this.createTime == null ? '':new Date(this.createTime).Format('yyyy-MM-dd hh:mm:ss');
			}
		}));
		$('.announceList').removeClass('warnBox').html(output);
	});
}

function existMsg(){
	return Tr.error('旧密码错误');
}

function initBase(){
	$(document).on('click', '.loginModifyBtn', function() {
		$('#modifyModule1').html(CDT.loginModifyModule);
		$('#modifyModule2').html('');
	});
	$(document).on('click', '.payModifyBtn', function() {
		$('#modifyModule1').html(CDT.payModifyModule);
		$('#modifyModule2').html('');
	});
	$(document).on('click', '.qqModifyBtn', function() {
		$('#modifyModule2').html(CDT.qqModifyModule);
		$('#modifyModule1').html('');
	});
	$(document).on('click', '.emailModifyBtn', function() {
		$('#modifyModule2').html(CDT.emailModifyModule);
		$('#modifyModule1').html('');
	});
	$(document).on('click', '.mobileModifyBtn', function() {
		$('#modifyModule2').html(CDT.mobileModifyModule);
		$('#modifyModule1').html('');
	});

	var validator = $('#modifyForm').validate({
		onkeyup: false,
		rules: {
			'oldPassword': {
				minlength: 6,
				maxlength: 20,
				remote: '/checkPass'
			},
			'newPassword': {
				minlength: 6,
				maxlength: 20
			},
			'checkPassword': {
				equalTo: '#txtNewPassword'
			},
			'newPayPass':{
				minlength: 6,
				maxlength: 20
			},
			'checkPayPass':{
				equalTo: '#txtPayPass'
			},
			'qq':{
				qq: true
			},
			'mobile':{
				mobile:true
			}
		},
		messages: {
			'oldPassword': {
				remote: existMsg()
			}
		}
	});

	//登录密码修改
	$(document).on('click','#modifyloginBtn', function(){
		//校验
		if(validator.form()){
			modifyloginPass();
		}
	});

	//支付密码修改
	$(document).on('click','#modifyPayBtn', function(){
		if(validator.form()){
			var payPass = $('#txtPayPass').val();
			var params = {
				'user.id': CDT.currentId,
				'user.payPassword': payPass
			};
			modifyDetail(params,'modifyModule1');
			$('.payModifyBtn').remove();
			$('.noData').hide();
			$('.hasData').show();
		}
	});

	//qq修改
	$(document).on('click','#modifyQqBtn', function(e){
		if(validator.form()){
			var qq = $('#txtQq').val();
			var params = {
				'user.id': CDT.currentId,
				'user.qq': qq
			};
			modifyDetail(params,'modifyModule2');
			$('.qqData').text(qq);
		}

	});

	//手机号码修改
	$(document).on('click','#modifyMobileBtn', function(){
		if(validator.form()){
			var mobile = $('#txtMobile').val();
			var params = {
				'user.id': CDT.currentId,
				'user.mobile': mobile
			};
			modifyDetail(params,'modifyModule2');
			$('.mobileData').text(mobile);
		}
	});

	//email修改
	$(document).on('click','#modifyEmailBtn', function(){
		if(validator.form()){
			var email = $('#txtEmail').val();
			var params = {
				'user.id': CDT.currentId,
				'user.email': mobile
			};
			modifyDetail(params,'modifyModule2');
			$('.emailData').text(email);
		}
	});
	
	/*财务情况区域*/
	// 发布任务
	$('#btnPublishTask').click(function() {
		window.location.href = '/task/publish';
	});
	$('#btnDepositWithdraw').click(function() {
		window.location.replace('/user/withdraw');
	});
}

$(function(){
	CDT.loginModifyModule = $('#loginModifyModule').remove().val();
	CDT.payModifyModule = $('#payModifyModule').remove().val();
	CDT.qqModifyModule = $('#qqModifyModule').remove().val();
	CDT.mobileModifyModule = $('#mobileModifyModule').remove().val();
	CDT.emailModifyModule = $('#emailModifyModule').remove().val();
	CDT.currentId = $('#userDetail').attr('userId');
	CDT.noticeModule = $('#noticeModule').remove().val();
	// 自定义消息格式
	$.extend($.validator.messages, {
		required: Tr.error('必填'),
		email: Tr.error('邮箱地址不正确'),
		qq: Tr.error('QQ号码不正确'),
		equalTo: Tr.error('两次输入密码不相同'),
		mobile: Tr.error('手机号码不正确'),
		maxlength: Tr.error('不能超过{0}个字符'),
		minlength: Tr.error('至少{0}个字符')
	});
	initBase();
	loadNotice();
});