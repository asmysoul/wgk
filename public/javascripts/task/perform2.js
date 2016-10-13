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
	if (step == 3 || step == 5||step==4) {
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
	var intDiff = Math.floor((60 * 60 * 3) - ((nowTime - date) / 1000)); //倒计时总秒数量
	setInterval(function() {
		var day = 0,
			hour = 0,
			minute = 0,
			second = 0; //时间默认值
		if (intDiff > 0) {
			day = Math.floor(intDiff / (60 * 60 * 6));
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
			Tr.post('/buyer/task/cancel2', {
				id: App.currId,
				authenticityToken: CDT.authenticityToken
			}, function(data) {
				if(data.code == 8001){
					alert('该任务当前状态下不能撤销任务');
					return;
				}
				if(data.code != 200)return;
				
				alert('任务超时，自动撤销');
				window.location.replace('/buyer/tasks2');
			});
			
			clearInterval(clock);
		}
		
	}, 1000);
}
//每一步的下一步的倒计时
function countSecondFunc(step) {
	if (step < 1 || step > 6) return;
	var secondRemain = CDT.STEP_COUNT_DELAYS[step];
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
			/*'taskItemUrl': {
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
			},*/
			/*'itemUrl5': {
				checkItemUrl2: true,
				url: true
			},
			'itemUrl6': {
				checkItemUrl2: true,
				url: true
			},*/
			/*'itemUrl7': {
				checkItemUrl2: true,
				url: true
			},*/
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
		messages: {
		},
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
		if(CDT.isTaoBaoAndTmall&&App.platform!='JD'){
			return $('#txtOrderId').val().length == 15||$('#txtOrderId').val().length == 16;
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
		if(App.platform=='WEIXIN'){
			$(".imgOrder-model").attr("src","/public/images/taobao-order.png");
		}else if (App.platform=='WEIBO') {
			$(".imgOrder-model").attr("src","/public/images/tmall-order.png");
		}else if (App.platform=='QQ') {
			$(".imgOrder-model").attr("src","/public/images/jd-order.png");
		}else if (App.platform=='OTHER') {
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
	/*$('.btnClearItemUrl').click(function() {
		$(this).parent('div').find('input').eq(0).val('');
	});*/
	//第一步复制
	$('.copyUrl').click(function() {
		$('.copyUrl').zclip({
			path: '/public/javascripts/ZeroClipboard.swf',
			copy: $('#urlMsg').val()
		});
	});
	//第一步打开
	$('.openURL').click(function() {
		var url=$('#url').val();
		window.open(url);
	});
	$('#downloadImg').click(function() {
		alert('ss');
		savepic();
	});
	//下载图片
	 function  savepic()
     {   
		 alert('ss');
       if(document.all.a1==null)
             {   
                       objIframe=document.createElement("IFRAME");   
                       document.body.insertBefore(objIframe);   
                       objIframe.outerHTML=   "<iframe   name=a1   style='width:0;hieght:0'   src="+pic1.src+"></iframe>";   
                       re=setTimeout("savepic()",1)   
               }   
      else
             {   
                       clearTimeout(re)   
                       pic   =   window.open(pic1.src,"a1")   
                       pic.document.execCommand("SaveAs")   
                       document.all.a1.removeNode(true)   
               }
     }   

	
	//第2步复制
		$('.copysellerAcccount').click(function() {
			$('.copysellerAcccount').zclip({
				path: '/public/javascripts/ZeroClipboard.swf',
				copy: $('#sellerAcccount').val()
			});
		});
		$('.copyPingLunContent').click(function() {
			$('.copyPingLunContent').zclip({
				path: '/public/javascripts/ZeroClipboard.swf',
				copy: $('#PingLunContent').val()
			});
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
			//alert('第二个页面下一步');
			setTargetStep(++CDT.currStep);
			return;
		}
		if (CDT.currStep == 3) {
			 
			var itemUrl1 = [],
				picUrls = [];
			$.each($('.item-url-inshop'), function(i,n) {
				if($(n).val()){
					itemUrl1.push($(n).val());
				}
			});
			picUrls.push($('#taskStep-3 .itemPicBox').find('img').attr('src'));
			p.type = 'VIEW_AND_INQUIRY';
			if(itemUrl1==""){
				p['vo.itemUrls'] = "www.baidu.com";
			}else{
				p['vo.itemUrls'] = itemUrl1;
			}
			p['vo.picUrls'] = picUrls;
		}
		if (CDT.currStep == 4) {
			setTargetStep(++CDT.currStep);
			return;
		}
		if (CDT.currStep == 5) {
			
			
			Tr.popup('submitToOrderWnd');
			$('#orderCount').text($('#txtOrderId').val());
			$('#realCount').text($('#txtPayAmount').val());
			return;
			
		}

		// 保存任务进度
		Tr.post('/buyer/task/saveStep2', p, function(data) {
			if (data.code == 800101) {
				alert('系统检测到数据异常，该操作被拒绝，请退出重新登录，若问题依旧请与管理员联系解决~~');
			}
			if (data.code != 200) return;
			// 保存成功才能进行下一步
			setTargetStep(++CDT.currStep);
			//alert(CDT.currStep);
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
		//var picUrls1 = [];
		//picUrls1.push($('#taskStep-5 .itemPicBox').find('img').attr('src'));
		p.type = 'ORDER_AND_PAY';
		//p['vo.picUrls'] = picUrls1;
		p['vo.orderNo'] = $.trim($('#txtOrderId').val());
		p['vo.realPaidFee'] = $.trim($('#txtPayAmount').val());
		p['messageId'] = $('#taskStep-5 .nextBtn').attr('messageId');
		Tr.post('/buyer/task/saveStep2', p, function(data) {
			if (data.code == 800101) {
				alert('系统检测到数据异常，该操作被拒绝，请退出重新登录，若问题依旧请与管理员联系解决~~');
			}
			if (data.code != 200) return;
			if (p.type == 'ORDER_AND_PAY') {
				alert('下单并支付成功，等待商家审核');
				window.location.replace('/buyer/tasks2');
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
			var option = getChatUploaderOption();
			var newButtonId = 'btnPickfiles';
			if (force) {
				var button = '<input type="button" id="' + newButtonId + '" value="选择并上传聊天截图">';
				$('#chatUploadBtnBox').html(button);
			}
			option.browse_button = newButtonId;
			new QiniuJsSDK().uploader(option);
			
			var itemUrl5Opt= getItemUrl5Option();
			var itemUrl5Btn = 'btnItemUrl5';
			itemUrl5Opt.browse_button = itemUrl5Btn;
			new QiniuJsSDK().uploader(itemUrl5Opt);
			
			var itemUrl6Opt= getItemUrl6Option();
			var itemUrl6Btn = 'btnItemUrl6';
			itemUrl6Opt.browse_button = itemUrl6Btn;
			new QiniuJsSDK().uploader(itemUrl6Opt);
			
			var itemUrl7Opt= getItemUrl7Option();
			var itemUrl7Btn = 'btnItemUrl7';
			itemUrl7Opt.browse_button = itemUrl7Btn;
			new QiniuJsSDK().uploader(itemUrl7Opt);
			
			
		}

		/*if (step == 4) {
			
			var option = getChatUploaderOption();
			var newButtonId = 'btnPickfiles';
			if (force) {
				var button = '<input type="button" id="' + newButtonId + '" value="选择并上传截图">';
				$('#chatUploadBtnBox').html(button);
			}
			option.browse_button = newButtonId;
			new QiniuJsSDK().uploader(option);
			
			var itemUrl7Opt= getItemUrl7Option();
			var itemUrl7Btn = 'btnItemUrl7';
			itemUrl7Opt.browse_button = itemUrl7Btn;
			new QiniuJsSDK().uploader(itemUrl7Opt);
		}*/
		
		
		
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
	option.domain = 'http://' + App.QnTaskFileBucket + '.jzniu.cn/';
	option.uptoken = CDT.uptoken;
	option.browse_button = 'btnPickfiles';
	option.max_retries = 0;
	option.init = {
		'FileUploaded': function(up, file, info) {
			var res = $.parseJSON(info);
			var imgUrl = up.getOption('domain') + res.key;
			var imgElement = '<a href="' + imgUrl + '" class="img" target="_blank"><img src="' + imgUrl + '"/></a>';
			$('#taskStep-3 .itemPicBox').html(imgElement);
		},
		'Error': function(up, err, errTip) {
			alert('上传失败！');
			$('#taskStep-3 .J_fixUploadFailed').show();
		}
	};
	return option;
}

function getItemUrl5Option() {
	var option = Tr.uploadOption();	
	option.domain = 'http://' + App.QnTaskFileBucket + '.jzniu.cn/';
	option.uptoken = CDT.uptoken;
	option.browse_button = 'btnItemUrl5';
	option.max_retries = 0;
	option.init = {
		'FileUploaded': function(up, file, info) {
			var res = $.parseJSON(info);
			var imgUrl = up.getOption('domain') + res.key;
			$('input[name=itemUrl5]').val(imgUrl);
			var imgElement = '<a href="' + imgUrl + '" class="img" target="_blank"><img src="' + imgUrl + '"  width="400" height="300" /></a>';
			$('#taskStep-3 #itemUrl5Img').html(imgElement);
		},
		'Error': function(up, err, errTip) {
			alert('上传失败！');
		}
	};
	return option;
}

function getItemUrl6Option() {
	var option = Tr.uploadOption();	
	option.domain = 'http://' + App.QnTaskFileBucket + '.jzniu.cn/';
	option.uptoken = CDT.uptoken;
	option.browse_button = 'btnItemUrl6';
	option.max_retries = 0;
	option.init = {
		'FileUploaded': function(up, file, info) {
			var res = $.parseJSON(info);
			var imgUrl = up.getOption('domain') + res.key;
			$('input[name=itemUrl6]').val(imgUrl);
			var imgElement = '<a href="' + imgUrl + '" class="img" target="_blank"><img src="' + imgUrl + '"  width="400" height="300" /></a>';
			$('#taskStep-3 #itemUrl6Img').html(imgElement);
		},
		'Error': function(up, err, errTip) {
			alert('上传失败！');
		}
	};
	return option;
}

function getItemUrl7Option() {
	var option = Tr.uploadOption();	
	option.domain = 'http://' + App.QnTaskFileBucket + '.jzniu.cn/';
	option.uptoken = CDT.uptoken;
	option.browse_button = 'btnItemUrl7';
	option.max_retries = 0;
	option.init = {
		'FileUploaded': function(up, file, info) {
			var res = $.parseJSON(info);
			var imgUrl = up.getOption('domain') + res.key;
			$('input[name=itemUrl7]').val(imgUrl);
			var imgElement = '<a href="' + imgUrl + '" class="img" target="_blank"><img src="' + imgUrl + '"  width="400" height="300" /></a>';
			$('#taskStep-3 #itemUrl7Img').html(imgElement);
		},
		'Error': function(up, err, errTip) {
			alert('上传失败！');
		}
	};
	return option;
}


function getOrderUploaderOption() {
	var option2 = Tr.uploadOption();
	option2.domain = 'http://' + App.QnTaskFileBucket + '.jzniu.cn/';
	option2.uptoken = CDT.uptoken;
	option2.browse_button = 'btnPickfiles2';
	option2.max_retries = 0;
	option2.init = {
		'FileUploaded': function(up, file, info) {
			var res = $.parseJSON(info);
			var imgUrl = up.getOption('domain') + res.key;
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

function listImg(){
	var str=$(".imgNum").text();
	arr=str.split(',');
	var imgNum = $(".size").text();
	var itempic = $(".itempic").text();
	var div = document.createElement("div");
	var str ="";
	if(imgNum>0){
		str +='<table class="ta">' +'<tr>' +'<td>' +'<img alt="ss"  src="'+arr[0]+'">'+'</td>';
	}
	for(var i = 1; i < imgNum; i++){
		if(i%3==0){
			str +='</tr>' + '<tr>'+'<td>' +'<img alt="ss"  src="'+arr[i]+'">' +'</td>' ;
		}else{
			str +='<td>' +'<img alt="ss"  src="'+arr[i]+'">' +'</td>';
		}
	}
	str +='</tr>' +'</table>';
	div.innerHTML = str;
	$("#imgList").append(div);
}

$(function() {
	listImg();
	if(CDT.userVipStaus=='SPECIAL'||CDT.isDEV){
		CDT.STEP_COUNT_DELAYS= [0, 0, 0, 0, 0, 0];
	}
	CDT.token = $('input[name="authenticityToken"]').val();
	CDT.currStep = App.initStep;
	//alert(CDT.currStep);
	/*if((App.platform=='WEIXIN')&&App.taskType!='PENGYOUQUAN'){//淘宝和天猫的订单
		CDT.isTaoBaoAndTmall=true;
	}*/
	
/*	if(CDT.isTaoBaoAndTmall){
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
	}*/
	//任务倒计时
	taskDeadlineCountdown();
	initBaseBind();
	resValidateImp();
	getFormValidator();
	setTargetStep(CDT.currStep);
	//为第五步渲染买号收货地址信息
	Tr.get('/buyer/task/buyerAccount2', {
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