CDT = {
	accountTemp: null,
	quyuTemp: null,
	currentId: '',
	nick: null,
	NeedDisbale: false,//限制传参
	vipStatus:App.vipStatus
};

function loadAccount(platform) {
	if(platform == 'TAOBAO'){
		$('.r_seller').html('旺旺ID');
	}else{
		$('.r_seller').html('买号');
	}
	Tr.get('/buyer/account/list', {
		'platform': platform
	}, function(data) {
		if (data.code != 200) return;
		var output = Mustache.render(CDT.accountTemp, $.extend(data,{
			accountStatus:function(){
				if(this.status == 'WAIT_EXAMINE'){
					return '待审核';
				}else if(this.status == 'EXAMINING'){
					return '审核中';
				}else if(this.status == 'NOT_PASS'){
					return '审核未通过';
				}else{
					return '审核通过';
				}
			},
			dateTimeStr:function(){
				return new Date(this.dateTime).Format('yyyy-MM-dd hh:mm:ss'); 
			}
		}));
		if (data.results.length <= 0) {
			output = '<span class="iconfont">&#xf00b6;</span>没有数据';
			$('#accountContainer').addClass('warnBox').html(output);
			return;
		}
		$('#accountContainer').removeClass('warnBox').html(output);
        $('.zcStatus[status="EXAMINED"]').addClass('examined');
        $('.zcStatus:not([status="EXAMINED"])').addClass('unexamined');
	});
}

function addAccount() {
	var nick = $('#txtNick').val(),
		consignee = $('#txtConsignee').val(),platform = $('.selectedCtb').attr('value'),
		state = $('#first').val(), city = $('#second').val(), region = $('#thrid').val(), 
		address = $('#txtAddress').val(), mobile = $('#txtMobile').val();
	Tr.post('/buyer/account/add', {
		'account.platform': platform,
		'account.nick': nick,
		'account.consignee': consignee,
		'account.state': state,
		'account.city': city,
		'account.region': region,
		'account.address': address,
		'account.mobile': mobile
	}, function(data) {
		if(data.code == 8001) {
			alert('最多只能绑定2个买号！');
		}
		if(data.code == 800101) {
			alert('昵称、手机号、收货人姓名或地址已绑定过，请重新输入！');
		}
		if(data.code != 200) return;
		$('#addAccountWnd').hide();
		$('#addAccountWnd input').val('');
		alert('绑定成功！');
		loadAccount(platform);
	});
}

function modifyAccount() {
	var $that = $('.currentCell').eq(0),
		nick = $.trim($('#txtNickMo').val()) == $.trim($that.find('.zcNick').text()) ? '' : $('#txtNickMo').val(),
		platform = $('.selectedCtb').attr('value'),
		consignee = $.trim($('#txtConsigneeMo').val()) == $.trim($that.find('.zcConsignee').text()) ? '' : $('#txtConsigneeMo').val(),
		state = $('#firstMo').val() == $that.find('.zcState').text() ? '' : $('#firstMo').val(),
		city = $('#secondMo').val() == $that.find('.zcCity').text() ? '' : $('#secondMo').val(),
		region = $('#thridMo').val() == $that.find('.zcRegion').text() ? '' : $('#thridMo').val(),
		address = $.trim($('#txtAddressMo').val()) == $.trim($that.find('.zcAddress').text()) ? '' : $('#txtAddressMo').val(),
		mobile = $.trim($('#txtMobileMo').val()) == $.trim($that.find('.zcMobile').text()) ? '' : $('#txtMobileMo').val();

	if (!nick && !consignee && !state && !city && !region && !address && !mobile) {
		alert('您没有修改任何内容');
		return;
	}
	var params = {
		'account.platform': platform,
		'account.nick': $.trim($('#txtNickMo').val()),
		'account.consignee': $('#txtConsigneeMo').val(),
		'account.state': $('#firstMo').val(),
		'account.city': $('#secondMo').val(),
		'account.region': $('#thridMo').val(),
		'account.address': $.trim($('#txtAddressMo').val()),
		'account.mobile': $.trim($('#txtMobileMo').val()),
		'account.id': CDT.currentId
	};
	CDT.NeedDisbale = true;
	Tr.post('/buyer/account/add', params, function(data) {
		if (data.code == 8001) {
			alert('最多只能添加2个买号！');
		}
		if (data.code == 800101) {
			alert('昵称、手机号、收货人姓名或地址已绑定过，请重新输入！');
		}
		if (data.code != 200) return;
		$('#bindAccountWnd').hide();
		$('#bindAccountWnd input').val('');
		alert('修改成功！');
		CDT.NeedDisbale = false;
		loadAccount(platform);
	});
}
function modifyOrderNumber() {
	var platform = $('.selectedCtb').attr('value');
	Tr.post('/buyer/account/modifyOrderNumber', {
		id:CDT.currentId,
		orderNumber:$('#orderNumberModify').val()
	}, function(data) {
		if(data.code==8001) alert(date.msg);
		if(data.code!=200) return;
		loadAccount(platform);
		$('#orderNumberWnd').hide();
	});
}

//初始化地址数据
function initQuyu(state, city, region) {
	$('#firstMo').val(state);
	var id = $('#firstMo').find('option:selected').attr('rid');
	Tr.get('/user/region', {
		id: id
	}, function(data) {
		if (data.code != 200) return;
		var output = Mustache.render(CDT.quyuTemp, data);
		$('#secondMo').append(output);
		$('#secondMo').val(city);
		var cid = $('#secondMo').find('option:selected').attr('rid');
		Tr.get('/user/region', {
			id: cid
		}, function(data) {
			if (data.code != 200) return;
			var output = Mustache.render(CDT.quyuTemp, data);
			$('#thridMo').append(output);
			$('#thridMo').val(region);
		});

	});
}

function validateOptionsSet(){
	// 自定义提示消息
    $.extend($.validator.messages, {
	    required: Tr.error('必填'),
	    minlength: Tr.error('至少{0}个字符.'),
	    maxlength: Tr.error('最多{0}个字符'),
	    mobile: Tr.error('格式不正确'),
	    equalTo: Tr.error('两次输入卡号不相同'),
	    zcNick: Tr.error('昵称重复'),
	    zcConsignee:Tr.error('收货人姓名重复，请重新输入！'),
	    zcAddress:Tr.error('地址重复，请重新输入！'),
	    zcMobile:Tr.error('手机号重复，请重新输入！'),
	    orderNumber:Tr.error('排序数为0-10之间的整数!')
    });
    //-------------------test
    $.validator.addMethod('zcNick', function(value) {
    	var flag = true;
    	    value = $.trim(value);
    	$('#accountContainer .taskRow').each(function(){
    		if($(this).find('tr').hasClass('currentCell')){
    			return true;
    		}
    		if(value == $.trim($(this).find('.zcNick').text())) flag = false;
    	});
    	return flag;
    });
    $.validator.addMethod('zcConsignee', function(value) {
    	var flag = true;
    	value = $.trim(value);
    	$('#accountContainer .taskRow').each(function(){
    		if($('#nickModify').text()==$.trim($(this).find('.zcNick').text())||$(this).find('tr').hasClass('currentCell')){
    			return true;
    		} 
    		if(value == $.trim($(this).find('.zcConsignee').text())) flag = false;   		
    	});
    	return flag;
    });
    $.validator.addMethod('zcMobile', function(value) {
    	var flag = true;
    	value = $.trim(value);
    	$('#accountContainer .taskRow').each(function(){
    		if($('#nickModify').text()==$.trim($(this).find('.zcNick').text())||$(this).find('tr').hasClass('currentCell')){
    			return true;
    		} 
    		if(value == $.trim($(this).find('.zcMobile').text())) flag = false;
    	});
    	return flag;
    });
    $.validator.addMethod('zcAddress', function(value) {
    	var flag = true;
    	value = $.trim(value);
    	$('#accountContainer .taskRow').each(function(){
    		if($('#nickModify').text()==$.trim($(this).find('.zcNick').text())||$(this).find('tr').hasClass('currentCell')){
    			return true;
    		} 
    		if(value == $.trim($(this).find('.zcAddress').text())) flag = false;	
    	});
    	return flag;
    });			


	// 校验规则
	var options = {
		onkeyup: false,
		rules: {
			nick: {
				zcNick: true
			},
			consignee: {
				minlength: 2,
				maxwords: 6,
				zcConsignee: true
			},
			address: {
				minlength: 5,
				maxwords: 50,
				zcAddress: true
			},
			mobile: {
				mobile: true,
				zcMobile: true
			},
			thrid:{
				required: {
					depends: function(element) {
						if($('#bindAccountWnd').is(':visible')){
							return $('#thridMo option').length!=1;
						}
						if($('#addAccountWnd').is(':visible')){
							return $('#thrid option').length!=1;
						}												
					}			
				}
			},
			orderNumber: {
				min: 0,
				max: 10
			}
		},
		// 不显示成功的小对号
		success: function (label, element) {
		}
	};
	return options;
}

function initBaseBind() {
	CDT.addAccFormContent = $('#addForm').html();
	$('#btnAddAccount').click(function() {
		var platform = $('#platform .selectedCtb').attr('value');
		if(CDT.vipStatus == 'SPECIAL' || $('#accountContainer .taskRow').length<2){
			if(platform == 'TAOBAO'){
				$('.wangwangID').html('旺旺ID&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;');
			}
			Tr.popup('addAccountWnd');
		}else{
			alert('最多只能绑定2个买号！');
		}
	});

	$('#btnCloseAddWnd').click(function(){		
		$('#addForm').html(CDT.addAccFormContent);
	});
	$('#txtNick').focus(function(){
		$('#nickMsg').show();
	});
	
	$(document).on('click', '.checkTextBtn', function() {
		var $me = $(this);
		var platform = $me.attr('value');
		$('.checkTextBtn').removeClass('selectedCtb');
		$me.addClass('selectedCtb');
		loadAccount(platform);
	});


	//修改买号
	$(document).on('click', '.modifyBtn', function() {
		var $that = $(this).parents('tr');
		var id = $(this).attr('aid');
		CDT.currentId = id;
		var status = $that.find('.zcStatus').attr('status');
		Tr.get('/buyer/account/taskStatus', {
			'btVo.accountId': id
		}, function(data) {
			if (data.code != 200) return;
			if (data.results.hasTaskExecuting) {
				alert('您有任务未完成，无法修改');
				$(this).text('任务未完成 无法修改');
				return;
			}
			if (status == 'NOT_PASS' && !data.results.hasTask) {
				$('#nickModify').html('<input type="text" class="inputText floatLeft ls long required" id="txtNickMo" name="nick" value="' + $that.find('.zcNick').text() + '"/>');
			} else {
				$('#nickModify').html($that.find('.zcNick').text());
			}
		});
		//给修改窗口中的修改项赋初值
		$('#txtConsigneeMo').val($that.find('.zcConsignee').text());
		$('#txtAddressMo').val($that.find('.zcAddress').text());
		$('#txtMobileMo').val($that.find('.zcMobile').text());
		initQuyu($that.find('.zcState').text(), $that.find('.zcCity').text(), $that.find('.zcRegion').text());
		$('tr').removeClass('currentCell');
		$that.addClass('currentCell');
		$('span.error').remove();
		Tr.popup('bindAccountWnd');
	});

	//修改排序号弹出窗
	$(document).on('click', '.modifyOrderNumberBtn', function() {
		$('#orderNumberModify').val('');
		CDT.currentId = $(this).attr('aid');
		Tr.popup('orderNumberWnd');
	});
	
	//选择区域
	$(document).on('change', '.province', function() {
		var $me = $(this),
		$city = $me.siblings('.city').eq(0),
		$region = $me.siblings('.region').eq(0);
		var id = $me.find('option:selected').attr('rid');
		$city.find('option').eq(0).nextAll('option').remove();
		$region.find('option').eq(0).nextAll('option').remove();
		if (!$me.find('option:selected').val()) {
			return;
		}
		Tr.get('/user/region', {
			id: id
		}, function(data) {
			if (data.code != 200) return;
			var output = Mustache.render(CDT.quyuTemp, data);
			$city.append(output);
		});
	});

	$(document).on('change', '.city', function() {
		var $me = $(this),
		$region = $me.siblings('.region').eq(0);
		var id = $me.find('option:selected').attr('rid');
		$region.find('option').eq(0).nextAll('option').remove();
		if (!$me.find('option:selected').val()) {
			return;
		}
		Tr.get('/user/region', {
			id: id
		}, function(data) {
			if (data.code != 200) return;
			var output = Mustache.render(CDT.quyuTemp, data);
			$region.append(output);
		});
	});

	(function(op){
		var validator = $('#addForm').validate(op);
		var validator1 = $('#modifyForm').validate(op);
		var validator2 = $('#modifyOrderNumberForm').validate(op);
		$('#btnAccountSave').click(function(){
			if (validator.form()) {
				addAccount();
			}
		});

		$('#btnConfirmSave').click(function(){
			if(validator1.form()){
				modifyAccount();
			}
		});
		
		$('#btnOrderNumberSave').click(function(){
			if(validator2.form()){
				modifyOrderNumber();
			}
		});
		
		
	})(validateOptionsSet());

	
}

$(function() {
	CDT.quyuTemp = $('#quyuTemp').remove().val();
	Mustache.parse(CDT.quyuTemp);
	CDT.accountTemp = $('#accountTemp').remove().val();
	Mustache.parse(CDT.accountTemp);

   
	initBaseBind();
	loadAccount('TAOBAO');
	
});

