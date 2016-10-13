var CDT = {
	isDEV:App.dev,
	userVipStaus:App.userVipStaus,
	currStep: 1,
	token: null,
	uptoken: null,
	STEP_COUNT_DELAYS : [0, 100, 0, 180, 15, 15],
	isTaoBaoAndTmall:false//是天猫、淘宝订单true
};

function setTargetStep(step) {
	if (step < 1 || step > 6) return;
	var $stepFa = $('.stepWrapper .stepWaiting'),
		index = step - 1,
		$prev = $stepFa.eq(index - 1),
		$curr = $stepFa.eq(index),
		$next = $stepFa.eq(index + 1);
	if (index > 0)
		$prev.html('<div class="stepFinish"></div>' + $prev.find('.caption').outerHTML());
	$curr.html('<div class="stepOn">' + ($curr.index() + 1) + '</div>' + $curr.find('.caption').outerHTML());
	if (index < 4)
		$next.html(($next.index() + 1) + $next.find('.caption').outerHTML());
	$('.stepCard').hide().eq(step - 1).show();

	if (index == 4 || index == 3) {
		for (i = 0; i < index; i++) {
			var $new_prev = $stepFa.eq(i);
			$new_prev.html('<div class="stepFinish"></div>' + $new_prev.find('.caption').outerHTML());
		}
	}

	if (step == 3 || step == 5) {
		initUploader(false, step);
	}
	//同时开始倒计时
	countSecondFunc(step);
}

//任务截止时间倒计时
function taskDeadlineCountdown() {
	var takeTime = $('#takeTime').text();
	var str = takeTime.replace(/-/g, "/");
	var date = new Date(str),
		nowTime = new Date();
	var dhour=1;
	var period=60 * 60;
	var taskType=$('#taskType').val();
//	if(taskType=='JHS'){
//		dhour=1;
//	}
//	var collectionType=$('#collectionType').val();
//	if(collectionType!=null&&collectionType!=''){
//		dhour=getCollectionTime(collectionType);
//	}
	period=period*dhour;
	var intDiff = Math.floor(period - ((nowTime - date) / 1000)); //倒计时总秒数量
	setInterval(function() {
		var day = 0,
			hour = 0,
			minute = 0,
			second = 0; //时间默认值
		if (intDiff > 0) {
			day = Math.floor(intDiff / (60 * 60 * dhour));
			hour = Math.floor(intDiff / (60 * 60)) - (day * 24);
			minute = Math.floor(intDiff / 60) - (day * 24 * 60) - (hour * 60);
			second = Math.floor(intDiff) - (day * 24 * 60 * 60) - (hour * 60 * 60) - (minute * 60);
		}
		if (minute <= 9) minute = '0' + minute;
		if (second <= 9) second = '0' + second;
		$('#hr').text(hour);
		$('#min').text(minute);
		$('#sec').text(second);
		intDiff--;
		
		if(intDiff<0){            //'id': App.currId
			Tr.post('/buyer/task/cancel3', {
				id: App.currId,
				authenticityToken: CDT.authenticityToken
			}, function(data) {
				if(data.code == 8001){
					alert('该任务当前状态下不能撤销任务');
					return;
				}
				if(data.code != 200)return;
				
				alert('任务超时，自动撤销');
				window.location.replace('/buyer/tasks3');
			});
			
			clearInterval(clock);
		}
		
	}, 1000);
}
/**
 * 如果有收藏加购获取倒计时时间
 * @param collectionType 收藏加购类型
 * @returns
 */
function getCollectionTime(collectionType){
	var dhour=3;
	if(collectionType=='ONE'){
		dhour=24;
	}
	if(collectionType=='TWO'){
		dhour=48;
	}
	if(collectionType=='THREE'){
		dhour=72;
	}
	return dhour
}
//每一步的下一步的倒计时
//function countSecondFunc(step) {
//	if (step < 1 || step > 6) return;
//	var secondRemain = CDT.STEP_COUNT_DELAYS[step];
//	if(step==4){
//		var collectionType=$('#collectionType').val();
//		if(collectionType!=null||collectionType!=''){
//			var dhour=getCollectionTime(collectionType);
//			secondRemain=(dhour-12)*60*60;
//		}
//	}
//	var clock = setInterval(function(){
//		if(secondRemain <= 0){
//			$('#taskStep-' + step + ' .nextBtn').addClass('nextEnable').removeClass('disabled').next('.tip').hide();			
//			clearInterval(clock);
//			return;
//		}
//		$('#taskStep-'+ step +' .second').text(--secondRemain);
//	},1E3);
//}
//每一步的下一步的倒计时
function countSecondFunc(step) {
	if (step < 1 || step > 6) return;
	var secondRemain = CDT.STEP_COUNT_DELAYS[step];
	var takeTime = $('#takeTime').text();
	var str = takeTime.replace(/-/g, "/");
	var date = new Date(str),
		nowTime = new Date();
	var collectionType=$('#collectionType').val();
	if((collectionType!=null||collectionType!='')&&step==4){
		dhour=getCollectionTime(collectionType);
		var period=(dhour-12)*60*60;
		secondRemain = Math.floor(period - ((nowTime - date) / 1000)); //倒计时总秒数量
	}
	var clock = setInterval(function(){
		if(secondRemain <= 0){
			$('#taskStep-' + step + ' .nextBtn').addClass('nextEnable').removeClass('disabled').next('.tip').hide();			
			clearInterval(clock);
			return;
		}
		$('#taskStep-'+ step +' .second').text(--secondRemain);
	},1E3);
}


function getFormValidator() {
	if (CDT.FORM_VALIDATOR) {
		return CDT.FORM_VALIDATOR;
	}
	var validator = '';
	if(CDT.isTaoBaoAndTmall){
	validator=$('#stepForm').validate({
		onkeyup: false,
		rules: {
			'taskItemUrl': {
				url: true
			},
			'itemUrl1': {
				checkItemUrl: true,
				url: true
			},
			'itemUrl2': {
				checkItemUrl: true,
				url: true
			},
			'itemUrl3': {
				checkItemUrl: true,
				url: true
			},
			'itemUrl4': {
				checkItemUrl: true,
				url: true
			},
			'itemUrl5': {
				checkItemUrl2: true,
				url: true
			},
			'itemUrl6': {
				checkItemUrl2: true,
				url: true
			},
			'itemUrl7': {
				checkItemUrl2: true,
				url: true
			},
//			'itemUrl8': {
//				checkItemUrl2: true,
//				url: true
//			},
			'payNum': {
				number: true,
				max: 10000
			},
			'orderId': {
				number: true,
				'OrderId': true
			}
		},
		messages: {},
		success: function(label, element) {
			var inputName = $(element).attr('name');
			if (inputName == 'orderId') {
				label.html(Tr.okLeft);
				return;
			}
			if (inputName != 'taskItemUrl') {
				label.html(Tr.ok);
			}
		}
	});
	}else{
		validator=$('#stepForm').validate({
			onkeyup: false,
			rules: {
				'payNum': {
					number: true,
					max: 10000
				},
				'orderId': {
					number: true,
					'OrderId': true
				}
			},
			messages: {},
			success: function(label, element) {
				var inputName = $(element).attr('name');
				if (inputName == 'orderId') {
					label.html(Tr.okLeft);
					return;
				}
			}
		});
	}
	CDT.FORM_VALIDATOR = validator;
	return validator;
}

function resValidateImp() {
	$.validator.addMethod('checkItemUrl', function(value, element) {
		var flag = true,
			inputs = $('.fellow-item-url').not(element);
		$.each(inputs, function(i) {
			if (value == inputs.eq(i).val() && value != '') flag = false;
		});
		return flag;
	}, Tr.error('链接不能重复！'));

	$.validator.addMethod('checkItemUrl2', function(value, element) {
		var flag = true,
			inputs = $('.item-url-inshop').not(element);
		$.each(inputs, function(i) {
			if (value == inputs.eq(i).val() && value != '') flag = false;
		});
		return flag;
	}, Tr.error('链接不能重复！'));

	$.validator.addMethod('OrderId', function(value) {
		if(CDT.isTaoBaoAndTmall&&App.platform!='JD'&&App.platform!='MOGUJIE'&&App.platform!='MEILISHUO'){
			return $('#txtOrderId').val().length == 15||$('#txtOrderId').val().length == 16;
		}
		if(App.platform=='MOGUJIE'||App.platform=='MEILISHUO'){
			return $('#txtOrderId').val().length == 14;
		}
		else {
			return true;
		}
	}, Tr.errorLeft('订单号格式错误'));

	$.extend($.validator.messages, {
		number: Tr.errorLeft('格式不正确')
	});
}

function initBaseBind() {
	//查看订单示例截图
	$("#btn-imgOrder-model").mouseover(function(){
		if(App.platform=='TAOBAO'){
			$(".imgOrder-model").attr("src","/public/images/taobao-order.png");
		}else if (App.platform=='TMALL') {
			$(".imgOrder-model").attr("src","/public/images/tmall-order.png");
		}else if (App.platform=='JD') {
			$(".imgOrder-model").attr("src","/public/images/jd-order.png");
		}else if (App.platform=='MOGUJIE') {
			$(".imgOrder-model").attr("src","/public/images/mogujie-order.png");
		}
		$(".imgOrder-model").show();
	});

	$("#btn-imgOrder-model").mouseout(function(){
		$(".imgOrder-model").hide();
	});
	//查看聊天示例截图
	$("#btn-imgChat-model").mouseover(function(){
		$(".imgChat-model").attr("src","/public/images/taobao-chat.png");
		$(".imgChat-model").show();
	});

	$("#btn-imgChat-model").mouseout(function(){
		$(".imgChat-model").hide();
	});
	
	$('input[name^="itemUrl"]').on('keyup', function() {
		$(this).val(Tr.checkurl($(this).val()));
	});

	$('input[name="taskItemUrl"]').on('keyup', function() {
		$(this).val(Tr.checkurl($(this).val()));
	});
	//第1步清空按钮
	$('.btnClearItemUrl').click(function() {
		$(this).parent('div').find('input').eq(0).val('');
	});
	//第4、5步复制，绑定2处
	$('#copyconfirm,#copyit').zclip({
		path: '/public/javascripts/ZeroClipboard.swf',
		copy: $('#' + $(this).attr('for')).text()
	});
	//确认窗口的按钮
	$('#btnConfirm').click(function() {
		$('#confirmToOrderWnd').hide();
	});
	$('#btnLookupSearchMsg').click(function(){
		$('#lookupSearchInfoBox').show();
	});
	/*下一步*/
	$(document).on('click', '.buttonSection .nextEnable', function() {
		//检查输入
		var validator = getFormValidator();
		if (!validator.form()) {
			return;
		}


		var p = {
			'type': null,
			'authenticityToken': CDT.token,
			'id': App.currId
		};
		if (CDT.currStep == 1) {
			var itemUrls = [];
			$.each($('.fellow-item-url'), function(i,n) {
				if($(n).val()){
					itemUrls.push($(n).val());
				}
			});

			p.type = 'CHOOSE_ITEM';
			p['vo.itemUrls'] = itemUrls;
		}
		if (CDT.currStep == 2) {
			//核对商品链接
			var $input = $('input[name="taskItemUrl"]');
			Tr.get('/buyer/task/perform/checkItem3', {
				itemUrl: $input.val()
			}, function(data) {
				if (data.code == 555) {
					alert('请检查您输入的商品url是否正确');
					return;
				}
				if (data.code != 200) 
					return;
				$('#spCheckItemUrl').html(Tr.ok);
			});
			p.type = 'CONFIRM_REFUND';
			//alert(p.type);
			p['vo.secUrl'] = $input.val();
			//return;
		}
//		if (CDT.currStep == 3) {
//			if (!$('#taskStep-3 .chatP').find('img').attr('src')||
//					!$('#taskStep-3 .jietuA').find('img').attr('src')||
//					!$('#taskStep-3 .jietuB').find('img').attr('src')||
//					!$('#taskStep-3 .jietuC').find('img').attr('src')) {
//				alert('没有主图');
//				return;
//			}
//
//			var itemUrl1 = [],
//				picUrls = [];
//			$.each($('.item-url-inshop'), function(i,n) {
//				if($(n).val()){
//					itemUrl1.push($(n).val());
//				}
//			});
//			picUrls.push($('#taskStep-3 .chatP').find('img').attr('src'));
//			itemUrl1.push($('#taskStep-3 .jietuA').find('img').attr('src'));
//			itemUrl1.push($('#taskStep-3 .jietuB').find('img').attr('src'));
//			itemUrl1.push($('#taskStep-3 .jietuC').find('img').attr('src'));
//			itemUrl1.push($(".item-url-inshop").val());
//			p.type = 'VIEW_AND_INQUIRY';
//			p['vo.itemUrls'] = itemUrl1;
//			p['vo.picUrls'] = picUrls;
//		}
//		if (CDT.currStep == 4) {
//			setTargetStep(++CDT.currStep);
//			Tr.popup('confirmToOrderWnd');
//			return;
//		}
//		if (CDT.currStep == 5) {
//			if (!$('#taskStep-5 .itemPicBox').find('img').attr('src')) {
//				alert('请先上传订单截图！');
//				return;
//			}
//			Tr.popup('submitToOrderWnd');
//			$('#orderCount').text($('#txtOrderId').val());
//			$('#realCount').text($('#txtPayAmount').val());
//			return;
//			
//		}

		// 保存任务进度
		Tr.post('/buyer/task/saveStep3', p, function(data) {
			if (data.code == 800101) {
				alert('系统检测到数据异常，该操作被拒绝，请退出重新登录，若问题依旧请与管理员联系解决~~');
			}
			if (data.code != 200)
				return;
			if (p.type == 'CONFIRM_REFUND') {
				alert('任务完成');
				window.location.replace('/buyer/tasks3');
				return;
			}
			// 保存成功才能进行下一步
			setTargetStep(++CDT.currStep);
		});
	});

	/*修复上传失败*/
	$('.J_fixUploadFailed').click(function() {
		initUploader(true, CDT.currStep);
		alert('系统已尝试进行自动修复，请再试一次。若问题依旧，请联系客服解决！');
	});
	$('#btnConfirmSubmit').click(function(){
		var p = {
			'type': null,
			'authenticityToken': CDT.token,
			'id': App.currId
		};
		var picUrls1 = [];
		picUrls1.push($('#taskStep-5 .itemPicBox').find('img').attr('src'));
		p.type = 'ORDER_AND_PAY';
		p['vo.picUrls'] = picUrls1;
		p['vo.orderNo'] = $.trim($('#txtOrderId').val());
		p['vo.realPaidFee'] = $.trim($('#txtPayAmount').val());
		p['messageId'] = $('#taskStep-5 .nextBtn').attr('messageId');
		Tr.post('/buyer/task/saveStep3', p, function(data) {
			if (data.code == 800101) {
				alert('系统检测到数据异常，该操作被拒绝，请退出重新登录，若问题依旧请与管理员联系解决~~');
			}
			if (data.code != 200) return;
			if (p.type == 'ORDER_AND_PAY') {
				alert('下单并支付成功，等待商家发货');
				window.location.replace('/buyer/tasks3');
				return;
			}
			// 保存成功才能进行下一步	
			setTargetStep(++CDT.currStep);
		});
	});
	$('#btncancel').click(function(){
		$('#submitToOrderWnd').hide();
	});

}

function initUploader(force, step) {
	// 先获取token
	Tr.post('/user/upload/token', {
		force: force
	}, function(data) {
		if (data.code != 200) return;

		// 初始化SDK
		CDT.uptoken = data.results;

		if (step == 3) {
			//三个截图
			//第一个
			var optionA = getChatUploaderOptionA();
			var newButtonIdA = 'btnPickfilesA';
			if (force) {
				var button = '<input type="button" id="' + newButtonIdA + '" value="选择并上传聊天截图">';
				$('#chatUploadBtnBoxA').html(button);
			}
			optionA.browse_button = newButtonIdA;
			new QiniuJsSDK().uploader(optionA);
			//第二个
			var optionB = getChatUploaderOptionB();
			var newButtonIdB = 'btnPickfilesB';
			if (force) {
				var button = '<input type="button" id="' + newButtonIdB + '" value="选择并上传聊天截图">';
				$('#chatUploadBtnBoxB').html(button);
			}
			optionB.browse_button = newButtonIdB;
			new QiniuJsSDK().uploader(optionB);
			//第三个
			var optionC = getChatUploaderOptionC();
			var newButtonIdC = 'btnPickfilesC';
			if (force) {
				var button = '<input type="button" id="' + newButtonIdC + '" value="选择并上传聊天截图">';
				$('#chatUploadBtnBoxC').html(button);
			}
			optionC.browse_button = newButtonIdC;
			new QiniuJsSDK().uploader(optionC);
			//最后一个
			var option = getChatUploaderOption();
			var newButtonId = 'btnPickfiles';
			if (force) {
				var button = '<input type="button" id="' + newButtonId + '" value="选择并上传聊天截图">';
				$('#chatUploadBtnBox').html(button);
			}
			option.browse_button = newButtonId;
			new QiniuJsSDK().uploader(option);
		}

		// 为Step5再初始化一个
		if (step == 5) {
			var option2 = getOrderUploaderOption();
			var newButtonId = 'btnPickfiles2';
			if (force) {
				var button = '<input type="button" class="middle ls floatLeft" id="' + newButtonId + '" value="上传订单截图" />';
				$('#orderUploadButtonBox').html(button);
			}
			option2.browse_button = newButtonId;
			new QiniuJsSDK().uploader(option2);
		}
	});
}
function getChatUploaderOption() {
	var option = Tr.uploadOption();	
	option.domain = 'http://' + App.QnTaskFileBucket + '.qiniudn.com/';
	option.uptoken = CDT.uptoken;
	option.browse_button = 'btnPickfiles';
	option.max_retries = 0;
	option.init = {
		'FileUploaded': function(up, file, info) {
			var res = $.parseJSON(info);
			//var imgUrl = up.getOption('domain') + res.key;
			var imgUrl = "http://7xj5si.com1.z0.glb.clouddn.com/" + res.key;
			var imgElement = '<a href="' + imgUrl + '" class="img" target="_blank"><img src="' + imgUrl + '" /></a>';
			$('#taskStep-3 .chatP').html(imgElement);
		},
		'Error': function(up, err, errTip) {
			alert('上传失败！');
			$('#taskStep-3 .J_fixUploadFailed').show();
		}
	};
	return option;
}
function getChatUploaderOptionA() {
	var option = Tr.uploadOption();	
	option.domain = 'http://' + App.QnTaskFileBucket + '.qiniudn.com/';
	option.uptoken = CDT.uptoken;
	option.browse_button = 'btnPickfilesA';
	option.max_retries = 0;
	option.init = {
		'FileUploaded': function(up, file, info) {
			var res = $.parseJSON(info);
			//var imgUrl = up.getOption('domain') + res.key;
			var imgUrl = "http://7xj5si.com1.z0.glb.clouddn.com/" + res.key;
			var imgElement = '<a href="' + imgUrl + '" class="img" target="_blank"><img src="' + imgUrl + '" /></a>';
			$('#taskStep-3 .jietuA').html(imgElement);
		},
		'Error': function(up, err, errTip) {
			alert('上传失败！');
			$('#taskStep-3 .J_fixUploadFailed').show();
		}
	};
	return option;
}
function getChatUploaderOptionB() {
	var option = Tr.uploadOption();	
	option.domain = 'http://' + App.QnTaskFileBucket + '.qiniudn.com/';
	option.uptoken = CDT.uptoken;
	option.browse_button = 'btnPickfilesB';
	option.max_retries = 0;
	option.init = {
		'FileUploaded': function(up, file, info) {
			var res = $.parseJSON(info);
			//var imgUrl = up.getOption('domain') + res.key;
			var imgUrl = "http://7xj5si.com1.z0.glb.clouddn.com/" + res.key;
			var imgElement = '<a href="' + imgUrl + '" class="img" target="_blank"><img src="' + imgUrl + '" /></a>';
			$('#taskStep-3 .jietuB').html(imgElement);
		},
		'Error': function(up, err, errTip) {
			alert('上传失败！');
			$('#taskStep-3 .J_fixUploadFailed').show();
		}
	};
	return option;
}
function getChatUploaderOptionC() {
	var option = Tr.uploadOption();	
	option.domain = 'http://' + App.QnTaskFileBucket + '.qiniudn.com/';
	option.uptoken = CDT.uptoken;
	option.browse_button = 'btnPickfilesC';
	option.max_retries = 0;
	option.init = {
		'FileUploaded': function(up, file, info) {
			var res = $.parseJSON(info);
			//var imgUrl = up.getOption('domain') + res.key;
			var imgUrl = "http://7xj5si.com1.z0.glb.clouddn.com/" + res.key;
			var imgElement = '<a href="' + imgUrl + '" class="img" target="_blank"><img src="' + imgUrl + '" /></a>';
			$('#taskStep-3 .jietuC').html(imgElement);
		},
		'Error': function(up, err, errTip) {
			alert('上传失败！');
			$('#taskStep-3 .J_fixUploadFailed').show();
		}
	};
	return option;
}

function getOrderUploaderOption() {
	var option2 = Tr.uploadOption();
	option2.domain = 'http://' + App.QnTaskFileBucket + '.qiniudn.com/';
	option2.uptoken = CDT.uptoken;
	option2.browse_button = 'btnPickfiles2';
	option2.max_retries = 0;
	option2.init = {
		'FileUploaded': function(up, file, info) {
			var res = $.parseJSON(info);
			//var imgUrl = up.getOption('domain') + res.key;
			var imgUrl = "http://7xj5si.com1.z0.glb.clouddn.com/" + res.key;
			var imgElement = '<a href="' + imgUrl + '" class="img" target="_blank"><img src="' + imgUrl + '" /></a>';
			$('#taskStep-5 .itemPicBox').html(imgElement);
		},
		'Error': function(up, err, errTip) {
			alert('上传失败！');
			$('#taskStep-5 .J_fixUploadFailed').show();
		}
	};
	return option2;
}


$(function() {
	if(CDT.userVipStaus=='SPECIAL'||CDT.isDEV){
		CDT.STEP_COUNT_DELAYS= [0, 0, 0, 0, 0, 0];
	}
	CDT.token = $('input[name="authenticityToken"]').val();
	CDT.currStep = App.initStep;
	if(App.platform=='TAOBAO'||App.platform=='TMALL'||App.platform=='JD'||App.platform=='MOGUJIE'||App.platform=='MEILISHUO'){//淘宝和天猫的订单
		CDT.isTaoBaoAndTmall=true;
	}
	if(CDT.isTaoBaoAndTmall){
		$("#TaoBao_Platform").show();
		$("#JHS_Platform").hide();
		$("#JHS_Platform #taskStep-5").remove();
		$("#JHS_Platform_Process").show();
	}else{
		CDT.currStep = App.initStep+1;
		$("#TaoBao_Platform").hide();
		$("#TaoBao_Platform #taskStep-5").remove();
		$("#JHS_Platform").show();
		$("#JHS_Platform #taskStep-5").show();
		$("#JHS_Platform_Process").hide();
	}
	//任务倒计时
	taskDeadlineCountdown();

	initBaseBind();
	resValidateImp();
	getFormValidator();
	setTargetStep(CDT.currStep);
	//为第五步渲染买号收货地址信息
	Tr.get('/buyer/task/buyerAccount3', {
		id: $('#taskStep-5').attr('data-account-id')
	}, function(data) {
		if (data.code != 200) return;
		var ba = data.results,info = ba.state + ba.city + ' ' + ba.address + ' ' + ba.consignee + ' ' + ba.mobile;
		$('#taskStep-5 .consigneeInfo').text(info);
		$('#taskStep-4 .consigneeInfo').text(info);
		$('#confirmToOrderWnd .consigneeInfo').text(info);
	});
	 $('#btnCopy').zclip({
			path: '/public/javascripts/ZeroClipboard.swf',
			copy: $('#copyItemUrl').text()
		});
});