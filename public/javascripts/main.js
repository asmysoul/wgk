App = {};

$(function() {
	$('.b_navCell[module="' + App.module + '"]').addClass('focus');
    $('.logout[module="' + App.module + '"]').addClass('logoutChecked');
	$('.maskLayer').click(function() {
		$(this).parents('.popWrapper').hide();
		return false;
	});
	$('.wnd_Close_Icon').click(function() {
		$(this).parents('.popWrapper').hide();
	});
});
$(window).resize(function() {

});
(function(){
	var oParseFloat = window.parseFloat;
	window.parseFloat = function(str,dec){
		if(!dec) dec=10;
		if(typeof(str) === "string"){
			str = str.replace(/,/g,'');
		}
	  	return oParseFloat(str,dec);
	};
})();
$.fn.outerHTML = function() {
	var $t = $(this);
	if ($t[0].outerHTML) {
		return $t[0].outerHTML;
	} else {
		var content = $t.clone().wrap('<div/>').parent().html();
		return content;
	}
};

/*
	半角范围：u0000 - u00FF, uFF61 - uFF9F, uFFE8 - uFFEE 
	全角范围： 
	全角数字(0-9) uFF10 - uFF19 
	全角大文字(A-Z): uFF21 - uFF3A 
	全角小文字(a-z): uFF41 - uFF5A 
	全角平仮名：u3040 - u309F 
	全角片仮名：u30A0 - u30FF 
	全角Latin: uFF01 - uFF5E 
	全角Symbol: uFFE0 - uFFE5
	以下是判断全角半角混合的宝贝标题字符串的字数。(2个字节为1个字，半角被认为是1个字节)
*/
Tr.countWords = function(text) {
	var count = 0;
	var uFF61 = parseInt("FF61", 16);
	var uFF9F = parseInt("FF9F", 16);
	var uFFE8 = parseInt("FFE8", 16);
	var uFFEE = parseInt("FFEE", 16);
	for (var i = 0; i < text.length; i++) {
		var c = parseInt(text.charCodeAt(i));
		if (c <= 255) {
			count++;
			continue;
		}
		if ((uFF61 <= c) && (c <= uFF9F)) {
			count++;
			continue;
		}
		if ((uFFE8 <= c) && (c <= uFFEE)) {
			count++;
			continue;
		}
		count = count + 2;
	}
	return Math.round(count / 2);
};

// 校验旺旺名称格式
Tr.validateName = function(name) {
	var length = name.replace(/[^\x00-\xff]/g, "**").length;
	if (!/^[a-zA-Z0-9_\u4e00-\u9fa5]+$/.test(name)) {
		/*alert('亲，旺旺名称只能包含大小写字母，汉字，下划线哦！');*/
		return false;
	}
	if (!/^(?!_)(?!.*?_$)/.test(name)) {
		/*alert('亲，旺旺名称不能以下划线开头和结尾的');*/
		return false;
	}
	if (/^\d+$/.test(name)) {
		/*alert('亲，旺旺名称不能全数字的');*/
		return false;
	}
	if (length > 25 || length < 4) {
		/*alert('亲，旺旺名称字符长度为4~25个，一个汉字算两个字符哦');*/
		return false;
	}
	return true;
};

Tr.ok = '<span class="iconfont" style="color:green">&#xf0156;</span>';
Tr.okLeft = '<span class="iconfont floatLeft" style="color:green">&#xf0156;</span>';
Tr.err = '<span class="iconfont" style="color:red">&#xf0155;</span>';
Tr.errLeft = '<span class="iconfont floatLeft" style="color:red">&#xf0155;</span>';
Tr.errRequired = '<span class="iconfont floatLeft" style="color:red">&#xf0155;&nbsp;必填</span>';

Tr.regs = {
	nick: /^[\u4e00-\u9fa5a-zA-Z0-9]{4,15}$/,
	loginPass: ['6nb16', [4, /^\d+$/],
		[4, /^[A-Za-z]+$/],
		[4, /^[^A-Za-z0-9]+$/]
	],
	payPass: ['6nb16', [4, /^\d+$/],
		[4, /^[A-Za-z]+$/],
		[4, /^[^A-Za-z0-9]+$/]
	],
	email: /^([a-zA-Z0-9]+[_|\-|\.]?)*[a-zA-Z0-9]+@(vip.qq|qq|163|126)\.com/,
	qq: /^[1-9]{1}[0-9]{4,10}$/,
	mobile: /^0{0,1}(13[0-9]|14[6|7]|15[0-3]|15[5-9]|18[0-9]|17[0-9])[0-9]{8}$/,
	captcha:/^\d{6}$/,
	khmname: [
		[3, /[\u4e00-\u9fa5]/]
	],
	bankforlast: [],
	bank: /^\d{16,19}$/,
	alipay: /^(0{0,1}(13[0-9]|14[6|7]|15[0-3]|15[5-9]|18[0-9])[0-9]{8}||([a-zA-Z0-9]+[_|\_|\-|\.]?)*@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3})$/,
	tenpay: /^(0{0,1}(13[0-9]|14[6|7]|15[0-3]|15[5-9]|18[0-9])[0-9]{8}||([a-zA-Z0-9]+[_|\_|\-|\.]?)*@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}||[1-9]{1}[0-9]{4,10})$/,
	url: /^http:\/\/[a-z0-9-]+\.[a-z0-9-]{1,}/,
	num: /^(([1-9])|([1-9][0-9]{1,}))$/,
	price: /^(([1-9][0-9]{1,}\.[0-9][1-9]{1,})|([1-9][0-9]{1,}\.[0-9]{1,2})|([1-9][0-9]{1,})|([0-9]{1,})|([0-9]\.[0-9]{1,}))$/,
	lgprice: /^(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*))$/,
	keyword: /^(([\u4E00-\u9FA5\x00-\xff]+)|([\u4E00-\u9FA5\x00-\xff]+\s+[\u4E00-\u9FA5\x00-\xff]+))$/,
	num09: /^[0-9]$/,
	name: /^[\u4E00-\u9FA5\x00-\xffa-zA-Z0-9-_]+$/,
	empty: /^\S+$/,
	every: function(val) {
		if (val && $.trim(val) != '')
		 {
			return true;
		}
		return false;
	},
	gt100: function(val) {
		if (val > 100) {
			return true;
		}
		return false;
	},
	lt300: function(val) {
		if (val < 300) {
			return true;
		}
		return false;
	}
};

/* 设置校验框架默认配置参数 */
$.validator.setDefaults({
	onsubmit: false,
	ignoreTitle: true,
	errorElement: 'span',
	errorPlacement: function(error, element) {
		element.after(error);
	},
	success: function(label, element) {
		label.html(Tr.ok);
	}
});

Tr.error = $.validator.format('<span style="color:red"><span class="iconfont">&#xf0155;</span>&nbsp;{0}</span>');
Tr.errorLeft = $.validator.format('<span class="floatLeft" style="color:red"><span class="iconfont">&#xf0155;</span>&nbsp;{0}</span>');

/* 修改校验框架默认提示信息 */
$.extend($.validator.messages, {
	required: Tr.errorLeft('必填'),
	email: Tr.errorLeft('Email 格式不正'),
	url: Tr.errorLeft('格式不正确'),
	number: Tr.errorLeft('{0}必须为数字'),
	digits: Tr.errorLeft('必须为数字'),
	maxlength: Tr.errorLeft('不能超过{0}个字符'),
	minlength: Tr.errorLeft('至少{0}个字符'),
	rangelength: Tr.errorLeft('长度只能为 {0} 到 {1} 个字符'),
	range: Tr.errorLeft('只能输入 {0} 到 {1} 之间的数值'),
	max: Tr.errorLeft('最大不能超过{0}'),
	min: Tr.errorLeft('不能小于{0}'),
	nick: Tr.errorLeft('用户名只能由英文字母及数字组成'),
	qq: Tr.errorLeft('QQ号码不正确'),
	mobile: Tr.errorLeft('手机号码不正确'),
	maxwords: Tr.errorLeft('不能超过{0}个字'),
	minwords: Tr.errorLeft('不能少于{0}个字'),
	price: Tr.errorLeft('价格不正确'),
	remote: "Please fix this field",
	date: "Please enter a valid date",
	dateISO: "Please enter a valid date ( ISO )",
	creditcard: "Please enter a valid credit card number.",
	equalTo: "Please enter the same value again."
});

/* 增加额外校验方法 */
// 平台注册昵称
$.validator.methods.nick = function(value, element, param) {
	return Tr.regs.nick.test(value);
};
$.validator.methods.qq = function(value, element, param) {
	return Tr.regs.qq.test(value);
};
$.validator.methods.mobile = function(value, element, param) {
	return Tr.regs.mobile.test(value);
};
$.validator.methods.maxwords = function(value, element, param) {
	return Tr.countWords(value) <= param;
};
$.validator.methods.minwords = function(value, element, param) {
	return Tr.countWords(value) >= param;
};
$.validator.methods.price = function(value, element, param) {
	return Tr.regs.price.test(value);
};

/*上传组件默认配置*/
Tr.uploadOption = function() {
	return {
		runtimes: 'html5,flash,html4', //上传模式,依次退化
		browse_button: 'btnPickfiles', //上传选择的点选按钮ID，**必需**
		unique_names: true, // 默认 false，key为文件名。若开启该选项，SDK为自动生成上传成功后的key（文件名）。
		// save_key: true,   // 默认 false。若在服务端生成uptoken的上传策略中指定了 `sava_key`，则开启，SDK会忽略对key的处理
		domain: '', //bucket 域名，下载资源时用到，**必需**
		container: 'container', //上传区域DOM ID，默认是browser_button的父元素，
		max_file_size: '5mb', //最大文件体积限制
		flash_swf_url: '/public/javascripts/plupload/Moxie.swf', //引入flash,相对路径
		max_retries: 3, //上传失败最大重试次数
		dragdrop: true, //开启可拖曳上传
		drop_element: 'container', //拖曳上传区域元素的ID，拖曳文件或文件夹后可触发上传
		chunk_size: '4mb', //分块上传时，每片的体积
		auto_start: true, //选择文件后自动上传，若关闭需要自己绑定事件触发上传
		multi_selection: false,
		filters: {
			mime_types: [{
				title: 'Image files',
				extensions: 'jpg,gif,png'
			}],
			max_file_size: "1mb",
			prevent_duplicates: true
		},
		init: {
			'FilesAdded': function(up, files) {
				plupload.each(files, function(file) {
					var progress = new FileProgress(file, 'fsUploadProgress');
					progress.setStatus("等待...");
				});
				// plupload.each(files, function(file) {
				// 	// 文件添加进队列后,处理相关的事情
				// });
			},
			'BeforeUpload': function(up, file) {
				// 每个文件上传前,处理相关的事情
				// return confim('确定要上传图片');
			},
			'UploadProgress': function(up, file) {
				// 每个文件上传时,处理相关的事情，转菊花、显示进度等
			},
			'FileUploaded': function(up, file, info) {},
			'Error': function(up, err, errTip) {
				//上传出错时,处理相关的事情
				alert('上传失败！');
			},
			'UploadComplete': function() {
				//队列文件处理完毕后,处理相关的事情
			},
			'Key': function(up, file) {
				// 若想在前端对每个文件的key进行个性化处理，可以配置该函数
				// 该配置必须要在 unique_names: false , save_key: false 时才生效

				var key = "";
				// do something with key here
				return key;
			}
		}
	};

};

//判断链接是否含有http://
Tr.checkurl = function(url) {
	if(url.indexOf("http://")==0||url==""||url.indexOf("https://")==0){
		return url;
	}else{
		return "http://" + url;
	}
};

Tr.checkprice = function(price ,ingot,pledge){
    var ex_price;
	if($('span.ingot').hasClass('seller')){
		ex_price = parseFloat($('span.ingot').text()) + parseFloat($('span.pledge').text());
	}
	else{
		ex_price = parseFloat($('span.ingot').text());
	}
	if (price > ex_price) {
		$('input[type="checkbox"]').attr('disabled','true').removeAttr('checked');
		if($('span.ingot').hasClass('buyer')){
			$('#payType').find('.panelLine:eq(1)').show();
		}
		$('#payType').find('.panelLine:eq(2)').show();
		$('.pay:eq(0)').addClass('selectedCtb').find('input').attr('checked','checked');
		return true;
	}else{
		$('input[type="checkbox"]').removeAttr('disabled').attr('checked','checked');
		if($('span.ingot').hasClass('buyer')){
			$('#payType').find('.panelLine:eq(1)').hide();
		}
		$('#payType').find('.panelLine:eq(2)').hide();
		$('.pay:eq(0)').removeClass('selectedCtb').find('input').removeAttr('checked');
		if(ingot<price&&pledge>=price){
			if(ingot==0){
				$('input.ingot').attr('disabled','true');
				$('input.ckIngot').attr('disabled','true');
			}
			$('input.ingot').removeAttr('checked');
			$('input.ckIngot').removeAttr('checked');
		}else{
			if(ingot>=price){
				if(pledge==0){
					$('input.pledge').attr('disabled','true');
					$('input.ckPledge').attr('disabled','true');
				}
				$('input.pledge').removeAttr('checked');
				$('input.ckPledge').removeAttr('checked');
			}
		}
		
	}
};
