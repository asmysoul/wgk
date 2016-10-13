CDT = {
	dianpuTemp: null,
	quyuTemp: null,
	countShops: '',
	shopId: '',
	VipStatus: ''
};
//根据平台取某个用户已绑定店铺信息
function loadDianpu(platform){
	Tr.get('/user/trialshop/' + platform, {}, function(data) {
		var output = '';
		CDT.countShops = data.results.length;
		if (CDT.countShops <= 0) {
			output = '<span class="iconfont">&#xf00b6;</span>您还没有绑定店铺!';
			$('#dianpuContainer').addClass('warnBox').html(output);  /*--无数据样式todo*/
		} else {
			output = Mustache.render(CDT.dianpuTemp, data);
			$('#dianpuContainer').removeClass('warnBox').html(output);
		}
		
	});
}

//获取取某个用户的等级
function loadVipStatus() {
	Tr.get('/user/vipStatus', {}, function(data) {
		if (data.code != 200) return;
		CDT.VipStatus = data.results.VipStatus;
	});
}

function addshop() {
	var wangzhi = $.trim($('#txtWangzhi').val()),
		wangwang = $.trim($('#txtWangwang').val()),
		firstAdd = $.trim($('#first').val()),
		secondAdd = $.trim($('#second').val()),
		thridAdd = $.trim($('#thrid').val()),
		sellerName = $.trim($('#sellerName').val()),
		mobile = $.trim($('#moblie').val()),
		street = $.trim($('#street').val()),
		branch = $.trim($('#branch').val());
	var address = firstAdd + '-' + secondAdd + '-' + thridAdd;
	var platform = $('.selectedCtb').attr('value');
	Tr.post('/user/trialshop/add', {
		'shop.platform': platform,
		'shop.url': wangzhi,
		'shop.address': address,
		'shop.nick': wangwang,
		'shop.sellerName': sellerName,
		'shop.mobile': mobile,
		'shop.street': street,
		'shop.branch': branch
	}, function(data) {
		if (data.code == 800101) {
			alert(data.msg);
		}
		if (data.code != 200) return;
		
		loadDianpu($('.selectedCtb').attr('value'));
		$('#txtWangzhi').val('');
		$('#txtWangwang').val('');
		$('.selectAddress').val('');
		$('#sellerName').val('');
		$('#moblie').val('');
		$('#street').val('');
		$('#branch').val('');
		$('span.error').remove();
		$('#bindShopWnd').hide();
	});
}
//更改店铺信息
function modifyShop() {
	var firstAdd = $.trim($('#newFirst').val()),
		secondAdd = $.trim($('#newSecond').val()),
		thridAdd = $.trim($('#newThrid').val()),
		sellerName = $.trim($('#newSellerName').val()),
		mobile = $.trim($('#newMoblie').val()),
		street = $.trim($('#newStreet').val()),
		branch = $.trim($('#newBranch').val());
	var address = firstAdd + '-' + secondAdd + '-' + thridAdd;
	Tr.post('/user/trialshop/modify', {
		address: address,
		sellerName: sellerName,
		mobile: mobile,
		street: street,
		branch: branch,
		id:CDT.shopId
	}, function(data) {
		if (data.code == 800101) {
			alert(data.msg);
		}
		if (data.code != 200) return;
		
		loadDianpu($('.selectedCtb').attr('value'));
		alert('修改成功');
		$('#modifyShopWnd').hide();
	});
}

//初始化地址数据
function initQuyu(state, city, region) {
	$('#newFirst').val(state);
	var id = $('#newFirst').find('option:selected').attr('rid');
	Tr.get('/user/region', {
		id: id
	}, function(data) {
		if (data.code != 200) return;
		var output = Mustache.render(CDT.quyuTemp, data);
		$('#newSecond').append(output);
		$('#newSecond').val(city);
		var cid = $('#newSecond').find('option:selected').attr('rid');
		Tr.get('/user/region', {
			id: cid
		}, function(data) {
			if (data.code != 200) return;
			var output = Mustache.render(CDT.quyuTemp, data);
			$('#newThrid').append(output);
			$('#newThrid').val(region);
		});

	});
}


function initBase(){
	// 绑定店铺
	$('#btnBindShop').click(function(){
		var maxCount = 3;
		if(CDT.VipStatus=='VIP1') {
			maxCount = 5;
		}else if(CDT.VipStatus=='VIP2'){
			maxCount = 10;
		}
		if(CDT.VipStatus != 'VIP3' && CDT.countShops >= maxCount){
			alert('绑定店铺数量已达到上限!');
			return;
		}
		$('#bindShopWnd').show();
	});

	$(document).on('click', '.checkTextBtn', function() {
		var $me = $(this);
		var platform = $me.attr('value');
		$('.checkTextBtn').removeClass('selectedCtb');
		$me.addClass('selectedCtb');
		loadDianpu(platform);
	});
	
	//修改店铺弹出框
	$(document).on('click', '.modifyShop', function() {
		CDT.shopId = $(this).attr('data-uid');
		var id = CDT.shopId;
		Tr.get('/user/trialshop/detail/'+id, {}, function(data) {
			$('#newTxtWangzhi').html(data.results.url);
			$('#newTxtWangwang').html(data.results.nick);
			$('#newSellerName').val(data.results.sellerName);
			$('#newMoblie').val(data.results.mobile);
			$('#newBranch').val(data.results.branch);
			var str=data.results.address; //这是一字符串 
			var strs= new Array(); //定义一数组 
			strs=str.split("-"); //字符分割
			initQuyu(strs[0],strs[1],strs[2]);
			$('#newStreet').val(data.results.street);
		});
		$('#modifyShopWnd').show();
	});
	
	//选择区域
	$(document).on('change', '#first', function() {
		var $me = $(this);
		var id = $me.find('option:selected').attr('rid');
		$('#second').find('option').eq(0).nextAll('option').remove();
		$('#thrid').find('option').eq(0).nextAll('option').remove();
		if (!$me.find('option:selected').val()) {
			return;
		}
		Tr.get('/user/region', {
			id: id
		}, function(data) {
			if (data.code != 200) return;
			if (data.results.length <= 0) return;
			var output = Mustache.render(CDT.quyuTemp, data);
			$('#second').append(output);
		});
	});
	
	$(document).on('change', '#second', function() {
		var $me = $(this);
		var id = $me.find('option:selected').attr('rid');
		$('#thrid').find('option').eq(0).nextAll('option').remove();
		if (!$me.find('option:selected').val()) {
			return;
		}
		Tr.get('/user/region', {
			id: id
		}, function(data) {
			if (data.results.length <= 0) return;
			var output = Mustache.render(CDT.quyuTemp, data);
			$('#thrid').append(output);
		});
	});
	
	
	//选择区域
	$(document).on('change', '#newFirst', function() {
		var $me = $(this);
		var id = $me.find('option:selected').attr('rid');
		$('#newSecond').find('option').eq(0).nextAll('option').remove();
		$('#newThrid').find('option').eq(0).nextAll('option').remove();
		if (!$me.find('option:selected').val()) {
			return;
		}
		Tr.get('/user/region', {
			id: id
		}, function(data) {
			if (data.code != 200) return;
			if (data.results.length <= 0) return;
			var output = Mustache.render(CDT.quyuTemp, data);
			$('#newSecond').append(output);
		});
	});

	$(document).on('change', '#newSecond', function() {
		var $me = $(this);
		var id = $me.find('option:selected').attr('rid');
		$('#newThrid').find('option').eq(0).nextAll('option').remove();
		if (!$me.find('option:selected').val()) {
			return;
		}
		Tr.get('/user/region', {
			id: id
		}, function(data) {
			if (data.results.length <= 0) return;
			var output = Mustache.render(CDT.quyuTemp, data);
			$('#newThrid').append(output);
		});
	});

	
	$(document).on('click', '.wnd_Close_Icon', function() {
		$('#txtWangzhi').val('');
		$('#txtWangwang').val('');
		$('.selectAddress').val('');
		$('span.error').remove();
	});
	
	(function(op){
		var validator = $('#addShop').validate(op);
		var validator1 = $('#modifyShop').validate(op);
		$('#btnConfirmSave').click(function(){
			if (validator.form()) {
				addshop();
			}
		});

		$('#btnConfirmModify').click(function(){
			if(validator1.form()){
				modifyShop();
			}
		});
	})(validateOptionsSet());
	
	
}


function validateOptionsSet(){
	//绑定店铺
	/* 校验器 */
	// 自定义提示消息
	$.extend($.validator.messages, {
		required: Tr.error('必填'),
		url:Tr.error('格式不正确'),
		nick: Tr.error('格式不正确'),
		equalTo: Tr.error('两次输入卡号不相同'),
		wangzhi: Tr.error('网址重复'),
		wangwang:Tr.error('旺旺ID重复'),
		sellerName:Tr.error('发货人姓名过长'),
		telPhone: Tr.error('电话号码不正确'),
		street:Tr.error('详细地址过长'),
		branch:Tr.error('编码格式不正确'),
		rangelength: Tr.error("长度应在 {0} 和 {1} 之间"),
		digits:Tr.error('请输入合法的编号')
	});
	$.validator.addMethod("telPhone", function(value) {    
		var length = value.length;
		if(length>13) return false;
	    return ((/^(\d{13})$/.test(value)) || (/^((\d{3,4})|(\d{3,4})-)+(\d{5,8})?$/.test(value)));   
	});
	$.validator.addMethod('sellerName', function(value) {
		var flag=true;
		if(value.length>=20) flag=false;
		return flag;
	});
	$.validator.addMethod('street', function(value) {
		var flag=true;
		if(value.length>=200) flag=false;
		return flag;
	});
	$.validator.addMethod('branch', function(value) {
		var flag=false;
		if(value.val.match(/\w{4,8}/)) flag=true;
		return flag;
	});
	$.validator.addMethod('nick', function(value) {
		var flag=true;
		flag=Tr.validateName(value);
		return flag;
	});
	$.validator.addMethod('wangzhi', function(value) {
		var flag = true;
			value = $.trim(value);
			$('#dianpuContainer .taskRow').each(function(){
				if(value == $.trim($(this).find('a').text())) flag = false;   		
			});
			return flag;
	});
	$.validator.addMethod('wangwang', function(value) {
		var flag = true;
		value = $.trim(value);  	
		$('#dianpuContainer .taskRow').each(function(){
			if(value == $.trim($(this).find('.r_seller').text())) flag = false;
		});
		return flag;
	});
	// 校验规则
	var options = {
			onkeyup: false,
		rules: {
			wangzhi: {
				url:true,
				wangzhi:true,
				maxlength:200
			},
			nick:{
				nick:true,
				wangwang:true
			},
			third:{
				required: {
					depends: function(element) {
						return $('#thrid option').length!=1 ;
					}			
				}
			},
			sellerName: {
				sellerName:true
			},
			branch: {
				rangelength:[4,8],
				digits:true
			},
			street: {
				street:true
			},
			mobile: {
				telPhone:true
			}
		},
		// 不显示成功的小对号
		success: function (label, element) {
		}
	};
	return options;
}
$(function() {
	CDT.dianpuTemp = $('#dianpuTemp').remove().val();
	Mustache.parse(CDT.dianpuTemp);
	CDT.quyuTemp = $('#quyuTemp').remove().val();
	Mustache.parse(CDT.quyuTemp);
	initBase();

	//初始化默认渲染第一个平台
	var $that = $('.checkTextBtn').eq(0);
	$that.addClass('selectedCtb');
	loadDianpu($that.attr('value'));
	loadVipStatus();
});