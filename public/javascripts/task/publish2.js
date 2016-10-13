var CDT = {
	taskIdStr : '',
	currStep : 1,
	searchWordPlanTempl : null, // 关键词方案
	subwayTemp : null,
	dianpuTempl : null,
	orderMsgCount : 1,
	commentWordCount : 1,
	orderItemPrice : 0,// 商品单价*购买件数
	baseOrderFee : 1,
	flowNum : App.flowNum,
	commissionFee : 0,
	mobileOrderFee:0,
	freight : 0,// 运费
	totalOrderNum : 10, // 默认要刷的总单量，PC+无线
	//pcOrderNum : 0,
	mobileOrderNum : 0,
	mobileExtIngot : 0.5,// 移动端订单额外收费
	sysRefund : 0.006, // 平台返款服务费比例
	taskInsuranceRate : 0, // 退款保障金比例【默认5%】，暂时取消
	CALC_STEP_4 : {}, // 计算第4步的最后的收费清单的函数集
	uptoken : '',
	len : 0,
	goodCommentKwdPrice : 0,
	goodCommentPicPrice : 0
};

//计算第4步的最后的收费清单的第一行总价
CDT.CALC_STEP_4.calc1 = function() {
	var insurance = parseFloat(CDT.orderItemPrice, 10) * CDT.taskInsuranceRate;
	insurance = insurance.toFixed(2);
	$('.insurance').number(insurance, 2);
	freightPledge = parseFloat($('input.isFreeShipping:checked').val(), 10);
	$('.freight').text(freightPledge);
	
	var unit = parseFloat(CDT.orderItemPrice, 10) + parseFloat(insurance, 10)
			+ parseFloat(freightPledge, 10);
	$('.spRow-unit-1').number(unit, 2);
	$('.spRow-total-1').number(unit * CDT.totalOrderNum, 2);
};
// 计算第4步的最后的收费清单的第二行总价
CDT.CALC_STEP_4.calc2 = function() {
	var $spSumCost = $('.spSumCost');
	//var count = parseFloat($('input[name="pcOrderNum"]').val());
	//$('.spPcOrderCount').text(count);
	var THEcount = parseFloat($('input[name="wirelessOrderNum"]').val());
	$('.spWirelessOrderCount').text(THEcount);
	var unit = parseFloat($spSumCost.text().replace(',', ''), 10)
			+ parseFloat(CDT.freight * CDT.totalOrderNum, 10);
	$('.spRow-total-2').number(unit, 2);
};
// 计算第4步的最后的收费清单的第三行总价
CDT.CALC_STEP_4.calc3 = function() {
	var $feeDiv = $('#feeWrapper'), platformRefund = parseFloat(
			CDT.orderItemPrice, 10), spPlatformRefundNum = sysRefundCount(platformRefund)
			* CDT.totalOrderNum;
	ingotNames = [ 'rewardIngotNum', 'buyTimeIntervalIngot', 'goodCommentKwd',
			'goodCommentPic','AddFenIngot' ], onceNames = [ 'speedTaskIngot', 'priorIngot',
			'publishIntervalIngot', 'spPlatformRefundNum' ], total = 0,
			unit = 0;
	$('#sysRefund a').hasClass('inputCheckBox-checked') ? $(
			'.spPlatformRefundNum').number(spPlatformRefundNum, 2) : $(
			'.spPlatformRefundNum').text(0);
	for ( var y in onceNames) {
		var value = $('.' + onceNames[y] + ':first', $feeDiv).text();
		value = value.replace(/,/g, '');

		total += parseFloat(value, 10);
	}
	for ( var x in ingotNames) {
		var value = $('.' + ingotNames[x] + ':first', $feeDiv).text();
		value = (value.length > 0) ? value : 0;
		unit += parseFloat(value, 10);
	}
	$('.spRow-unit-3').number(unit, 2);
	$('.spRow-total-3').number(total + unit * CDT.totalOrderNum, 2);
};
// 计算第四步金币的总价
CDT.CALC_STEP_4.calc4 = function() {
	var spRowtotal3 = parseFloat($('.spRow-total-3').text().replace(/,/g, ''));
	var spRowtotal2 = parseFloat($('.spRow-total-2').text().replace(/,/g, ''));
	var ingot = spRowtotal2 + spRowtotal3;
//	alert(ingot);
	$('.spRow-total-4').number(ingot, 2);
};

























function loadDianpu(platform) {
	Tr.get('/user/shop2/' + platform, {}, function(data) {
		if (data.code != 200) 
			{alert(''+data.code);
			return;
			}
		var output = Mustache.render(CDT.dianpuTempl, data);
		$('#dianpuContainer').html(output);

		// （编辑旧任务时）默认选择当前已选择的店铺
		var currShopId = $('#dianpuContainer').attr('curr-shop'),$currShop = $('#dianpuContainer .checkTextBtn[value="' + currShopId + '"]');
		if($currShop.length){
			$currShop.addClass('selectedCtb');
				return;
		}	
		// 默认选择第一个店铺
		$('#dianpuContainer .checkTextBtn:first').addClass('selectedCtb');
	});
}




function sysRefundCount(platformRefund) {
	var spPlatformRefundNum = CDT.sysRefund * platformRefund;
	return parseFloat(Math.ceil(spPlatformRefundNum * 100) / 100, 10);

}


function setTargetStep(step) {
	//alert('当前执行第'+step+'步');
	if (step < 1 || step > 5) {
		return
	};
	/*var $stepFa = $('.stepWrapper .stepWaiting'), index = step - 1, $prev = $stepFa
			.eq(index - 1), $curr = $stepFa.eq(index), $next = $stepFa
			.eq(index + 1);*/
	var $stepFa = $('.stepWrapper .stepWaiting'), index = step-1 , $prev = $stepFa
	.eq(index - 1), $curr = $stepFa.eq(index), $next = $stepFa
	.eq(index + 1);
	
	if (index > 0)
		$prev.html('<div class="stepFinish"></div>'
				+ $prev.find('.caption').outerHTML());
	$curr.html('<div class="stepOn">' + ($curr.index() + 1) + '</div>'
			+ $curr.find('.caption').outerHTML());
	
	if (index < 4)
		$next.html(($next.index() + 1) + $next.find('.caption').outerHTML());
	
	if (index == 1) {
		var id = $('#dianpuContainer .shop.selectedCtb').attr('value');
		Tr.get('/user/shop2/detail/' + id, {}, function(data) {
			if (data.code != 200)
				{
				return;
				}
			if (data.results.sellerName == null || data.results.mobile == null
					|| data.results.street == null
					) {
				alert("由于快递切换，请填写发货详细信息，请进入【绑定店铺】菜单完善店铺信息！");
				CDT.currStep = 1;
				return;
			} else {
				$('.stepCard').hide().eq(step - 1).show();
			}
		});
	} else {
		$('.stepCard').hide().eq(step - 1).show();
	}
	
	
	
	
	
	if (index == 4) {
		//alert('当前是第四步');
		for (i = 0; i < index; i++) {
			var $new_prev = $stepFa.eq(i);
			$new_prev.html('<div class="stepFinish"></div>'
					+ $new_prev.find('.caption').outerHTML());
		}
	}
	if (index == 1 && !$('#ckSearchTmall').hasClass('inputCheckBox-checked')) {
		//alert('sssss');
		$('.panelBox2').hide();
		initUploader();
	}
}






function initUploader(force) {
	// 先获取token
	Tr.post('/user/upload/token', {}, function(data) {
		if (data.code != 200) return;
		// 初始化SDK
		CDT.uptoken = data.results;
		if($('.subway').is(':visible')){
			var option1 = getImgSubwayUploaderOption();
			var newButtonId = 'btnPickfiles1';
			var button = '<input type="button" id="' + newButtonId + '" value="选择并上传素材图">';
			$('#subwayBox').html(button);

			option1.browse_button = newButtonId;
			new QiniuJsSDK().uploader(option1);
			
			var option2 = getImgSubwayUploaderOption2(2);
			var newButtonId2 = 'btnPickfiles2';
			option2.browse_button = newButtonId2;
			new QiniuJsSDK().uploader(option2);
			
			var option3 = getImgSubwayUploaderOption2(3);
			var newButtonId3 = 'btnPickfiles3';
			option3.browse_button = newButtonId3;
			new QiniuJsSDK().uploader(option3);
			
			var option4 = getImgSubwayUploaderOption2(4);
			var newButtonId4 = 'btnPickfiles4';
			option4.browse_button = newButtonId4;
			new QiniuJsSDK().uploader(option4);
			
			var option5 = getImgSubwayUploaderOption2(5);
			var newButtonId5 = 'btnPickfiles5';
			option5.browse_button = newButtonId5;
			new QiniuJsSDK().uploader(option5);
			
			var option6 = getImgSubwayUploaderOption2(6);
			var newButtonId6 = 'btnPickfiles6';
			option6.browse_button = newButtonId6;
			new QiniuJsSDK().uploader(option6);
			
			var option7 = getImgSubwayUploaderOption2(7);
			var newButtonId7 = 'btnPickfiles7';
			option7.browse_button = newButtonId7;
			new QiniuJsSDK().uploader(option7);
			
			var option8 = getImgSubwayUploaderOption2(8);
			var newButtonId8 = 'btnPickfiles8';
			option8.browse_button = newButtonId8;
			new QiniuJsSDK().uploader(option8);
			
			var option9 = getImgSubwayUploaderOption2(9);
			var newButtonId9 = 'btnPickfiles9';
			option8.browse_button = newButtonId9;
			new QiniuJsSDK().uploader(option9);
			
		}
		var option = getItemImgUploaderOption();
		var newButtonId = 'btnPickfiles';
		option.browse_button = newButtonId;
		new QiniuJsSDK().uploader(option);
	});
}

function initUploaderComment(force) {
	// 先获取token
	Tr.post('/user/upload/token', {}, function(data) {
		if (data.code != 200) return;
		// 初始化SDK
		CDT.uptoken = data.results;
		if($('.commpentBoxs').is(':visible')){
			var option1 = getImgCommentUploaderOption(1);
			var newButtonId1 = 'btnCommentfiles1';
			option1.browse_button = newButtonId1;
			new QiniuJsSDK().uploader(option1);
			
			var option2 = getImgCommentUploaderOption(2);
			var newButtonId2 = 'btnCommentfiles2';
			option2.browse_button = newButtonId2;
			new QiniuJsSDK().uploader(option2);
			
			var option3 = getImgCommentUploaderOption(3);
			var newButtonId3 = 'btnCommentfiles3';
			option3.browse_button = newButtonId3;
			new QiniuJsSDK().uploader(option3);
			
			var option4 = getImgCommentUploaderOption(4);
			var newButtonId4 = 'btnCommentfiles4';
			option4.browse_button = newButtonId4;
			new QiniuJsSDK().uploader(option4);
			
			
			var option5 = getImgSubwayUploaderOption2(5);
			var newButtonId5 = 'btnPickfiles5';
			option5.browse_button = newButtonId5;
			new QiniuJsSDK().uploader(option5);
			
			var option6 = getImgSubwayUploaderOption2(6);
			var newButtonId6 = 'btnPickfiles6';
			option6.browse_button = newButtonId6;
			new QiniuJsSDK().uploader(option6);
			
			var option7 = getImgSubwayUploaderOption2(7);
			var newButtonId7 = 'btnPickfiles7';
			option7.browse_button = newButtonId7;
			new QiniuJsSDK().uploader(option7);
			
			var option8 = getImgSubwayUploaderOption2(8);
			var newButtonId8 = 'btnPickfiles8';
			option8.browse_button = newButtonId8;
			new QiniuJsSDK().uploader(option8);
			
			var option9 = getImgSubwayUploaderOption2(9);
			var newButtonId9 = 'btnPickfiles9';
			option8.browse_button = newButtonId9;
			new QiniuJsSDK().uploader(option9);
		}
	});
}

function getImgCommentUploaderOption(index) {
	var option1 = Tr.uploadOption();
	option1.domain = 'http://' + App.QnTaskFileBucket + '.jzniu.cn/';
	option1.uptoken = CDT.uptoken;
	option1.browse_button = 'btnCommentfiles'+index;
	option1.max_retries = 0;
	option1.init = {
		'UploadProgress': function(up, file) {
				// 每个文件上传时,处理相关的事情，转菊花、显示进度等
				$('#btnCommentfiles'+index).attr("disabled", "disabled");
		},
		'FileUploaded': function(up, file, info) {
			var res = $.parseJSON(info);
			var imgUrl = up.getOption('domain') + res.key;
			$('#imgCommentWrapperInStep'+index).html('<img id="imgComment'+index+'" name="imgComment'+index+'" src="'+imgUrl+'" />');
			$('#txtCommentPicUrl'+index).attr('commentpic'+index, imgUrl).val(imgUrl);
			$('#btnCommentfiles'+index).removeAttr("disabled");
			$('.Prompt').show();
		},
		'Error': function(up, err, errTip) {
			alert('上传失败！');
			$('#taskStep-5 .J_fixUploadFailed').show();
		}
	};
	return option1;
}

function getItemImgUploaderOption() {
	var option = Tr.uploadOption();	
	option.domain = 'http://' + App.QnTaskFileBucket + '.jzniu.cn/';
	option.uptoken = CDT.uptoken;
	option.browse_button = 'btnPickfiles';
	option.max_retries = 0;
	option.init = {
		'FileUploaded': function(up, file, info) {
			var res = $.parseJSON(info);
			var imgUrl = up.getOption('domain') + res.key;
			$('#itemImgWrapperInStep1').html('<img id="itemImg" name="itemImg" src='+imgUrl+' />');
			$('#txtItemPicUrl').attr('itemPic', imgUrl).val(imgUrl);
			$('#btnPickfiles').removeAttr("disabled");
			$('.Prompt').show();
		},
		'Error': function(up, err, errTip) {
			alert('上传失败！');
			$('#taskStep-3 .J_fixUploadFailed').show();
		}
	};
	return option;
}

function getImgSubwayUploaderOption() {
	var option1 = Tr.uploadOption();
	option1.domain = 'http://' + App.QnTaskFileBucket + '.jzniu.cn/';
	option1.uptoken = CDT.uptoken;
	option1.browse_button = 'btnPickfiles1';
	option1.max_retries = 0;
	option1.init = {
		'UploadProgress': function(up, file) {
				// 每个文件上传时,处理相关的事情，转菊花、显示进度等
				$('#btnPickfiles1').attr("disabled", "disabled");
		},
		'FileUploaded': function(up, file, info) {
			var res = $.parseJSON(info);
			var imgUrl = up.getOption('domain') + res.key;
			$('#imgSubwayWrapperInStep1').html('<img id="itemImg" name="itemImg" src="'+imgUrl+'" />');
			$('#txtItemSubwayPicUrl').attr('itemPic', imgUrl).val(imgUrl);
			$('#btnPickfiles1').removeAttr("disabled");
			$('.Prompt').show();
		},
		'Error': function(up, err, errTip) {
			alert('上传失败！');
			$('#taskStep-5 .J_fixUploadFailed').show();
		}
	};
	return option1;
}

function getImgSubwayUploaderOption2(index) {
	var option1 = Tr.uploadOption();
	option1.domain = 'http://' + App.QnTaskFileBucket + '.jzniu.cn/';
	option1.uptoken = CDT.uptoken;
	option1.browse_button = 'btnPickfiles'+index;
	option1.max_retries = 0;
	option1.init = {
		'UploadProgress': function(up, file) {
				// 每个文件上传时,处理相关的事情，转菊花、显示进度等
				$('#btnPickfiles'+index).attr("disabled", "disabled");
		},
		'FileUploaded': function(up, file, info) {
			var res = $.parseJSON(info);
			var imgUrl = up.getOption('domain') + res.key;
			$('#imgSubwayWrapperInStep'+index).html('<img id="itemImg'+index+'" name="itemImg'+index+'" src="'+imgUrl+'" />');
			$('#txtItemSubwayPicUrl'+index).attr('itemPic'+index, imgUrl).val(imgUrl);
			$('#btnPickfiles'+index).removeAttr("disabled");
			$('.Prompt').show();
		},
		'Error': function(up, err, errTip) {
			alert('上传失败！');
			$('#taskStep-5 .J_fixUploadFailed').show();
		}
	};
	return option1;
}




function getFormValidator() {
	if (CDT.FORM_VALIDATOR) {
		return CDT.FORM_VALIDATOR;
	}
	var validator = $('#form').validate({
	rules: {
		itemUrl: {
			required: false,
			url: true,
			maxlength: 300
		},
		itemTitle: {
			maxwords: 1500
		},
		itemPicUrl: {
			required: false,
			url: true,
			maxlength: 400
		},
		itemSubwayPicUrl: {
			url: true,
			maxlength: 400
		},
		itemPrice: {
			price: true,
			min: 0.01,
			max: 10000
		},
		itemDisplayPrice: {
			price: {
				depends: function(element) {
					return $.trim($('#itemDisplayPrice').val()).length > 0;
				}
			},
			min: 1,
			max: 10000
		},
		itemBuyNum: {
			digits: true,
			min: 1,
			max: 1000
		},searchFlowNum:{
			digits: true
		},
		expressWeight:{
			range:[0,500]
		},
		tbWord: {
			required: {
				depends: function(element) {
					return $('#ckSearchTaobao').hasClass('inputCheckBox-checked');
				}
			},
			maxwords: 15,
			minwords: 5
		},
		tmWord: {
			required: true,
			maxwords: 15,
			minwords: 5
		},
		qq:{
			required: true
		},
		startPrice: {
			price: {
				depends: function(element) {
					return $.trim($('#startPrice').val()).length > 0;
				}
			},
			min: 1,
			max: 100000
		},
		endPrice: {
			price: {
				depends: function(element) {
					return $.trim($('#endPrice').val()).length > 0;
				}
			},
			min: 1,
			max: 100000
		},
		customOrderNum: {
			required: {
				depends: function(element) {
					return $('#txtCustomOrderNum').prev(':radio').attr('checked');
				}
			},
			digits: true,
			min: 1,
			max: 500
		},
		/*pcOrderNum: {
			digits: true,
			min: 0
		},*/searchItemTotalNum:{
			digits: true,
			min: 0
		},
		wirelessOrderNum: {
			digits: true,
			min: 0,
		},
		extraRewardIngot: {
			required: {
				depends: function(element) {
					return $('#cbExtraIngot').hasClass('inputCheckBox-checked');
				}
			},
			digits: true,
			min: function(element) {
				if ($('#cbExtraIngot').hasClass('inputCheckBox-checked')) {
					return 1;
				}
				return 1;
			},
			max: 500
		}
	},
	success: function(label, element) {
		$e = $(element);
		var inputName = $e.attr('name');
		var isEmpty = $.trim($e.val()).length <= 0;
		if (isEmpty && (inputName == 'itemDisplayPrice' || inputName == 'startPrice' || inputName == 'endPrice')) {
			return;
		}
		if (inputName == 'itemSubwayPicUrl') {
			flushSubwayItemImg();
		}
			if (inputName == 'itemBuyNum' || inputName == 'itemPrice') {
			var itemBuyNum = parseInt($('#itemBuyNum').val());
			var itemPrice = parseFloat($('#itemPrice').val());
			CDT.orderItemPrice = itemPrice * itemBuyNum;
		}
		label.html(Tr.okLeft);
	},
	// 配置发生错误时滚动页面定位出错元素
	focusInvalid: false,
	invalidHandler: function(form, validator) {
		if (!validator.numberOfInvalids())
			return;
		$('html, body').animate({
			scrollTop: $(validator.errorList[0].element).offset().top
		}, 2000);
	}
	});

	CDT.FORM_VALIDATOR = validator;
	return validator;
}





// 设置搜索关键词参数（加入请求参数的map中）
function setSearchPlanParam(paramMap) {
	// 将包含淘宝、天猫搜索关键词的区域DIV加入数组中
	var wordPlanBoxes = [];
	if ($('#ckSearchTaobao').hasClass('inputCheckBox-checked')) {
		$.merge(wordPlanBoxes, $('#tbSearchWordBox .wordPlan'));
	}
	if ($('#ckSearchTmall').hasClass('inputCheckBox-checked')) {
		$.merge(wordPlanBoxes, $('#tmSearchWordBox .wordPlan'));
	}
	if ($('#ckSearchJd').hasClass('inputCheckBox-checked')) {
		$.merge(wordPlanBoxes, $('#jdSearchWordBox .wordPlan'));
	}

	// 遍历筛选中目标数据
	// 手动计算索引，避免引入null对象到后端list
	var i = 0;
	$(wordPlanBoxes).each(function(n) {
		// 跳过关键词为空的项目
		var word = $.trim($(this).find('input[name="word"]').val());
		if (word.length <= 0) {
			return;
		}
		// 构造请求参数前缀
		var indexPrefix = 'task.searchPlans[' + i + ']';
		paramMap[indexPrefix + '.word'] = word;
		paramMap[indexPrefix + '.inTmall'] = $(this).parents('.panelBox').attr('data-in-tmall');
		var skus = [];
		$(this).find('input[name="skus"]').each(function(i) {
			if ($(this).val().length > 0) {
				skus.push($(this).val());
			}
		});
		paramMap[indexPrefix + '.skus'] = skus.join(',');
		i++;
	});
	
	var wordPlanBoxesNum = [];
	$.merge(wordPlanBoxesNum, $('#searchPlanTotalNum .sec'));
	var j=0;
	$(wordPlanBoxesNum).each(function(n) {
		var totalNum = $.trim($(this).find('input[name="searchItemTotalNum"]').val());
		var indexPrefix = 'task.searchPlans[' + j + ']';
		paramMap[indexPrefix + '.totalNum'] = totalNum;
		j++;
	});
	
	
	var wordPlanBoxesFlowNum = [];
	$.merge(wordPlanBoxesFlowNum, $('#flowCharge .sec'));
	var k=0;
	$(wordPlanBoxesFlowNum).each(function(n) {
		var totalNum = $.trim($(this).find('input[name="searchFlowNum"]').val())==""?0:$.trim($(this).find('input[name="searchFlowNum"]').val());
		var indexPrefix = 'task.searchPlans[' + k + ']';
		paramMap[indexPrefix + '.flowNum'] = totalNum;
		k++;
	});
	
}





//佣金计算
function commissionTable(price) {
		return CDT.baseOrderFee;
}





function initTaskPayFee() {
	// 定义变量
	//CDT.pcOrderNum = parseFloat($('#pcOrderNum').val(), 10);
	CDT.mobileOrderNum = parseFloat($('#wirelessOrderNum').val(), 10);
	//CDT.totalOrderNum = CDT.pcOrderNum + CDT.mobileOrderNum;
	CDT.totalOrderNum = CDT.mobileOrderNum;
	
	setTaskFee();
	/*alert('111');*/
	if ($('#AddFen a').hasClass('inputCheckBox-checked')) {
		
		$('.AddFenIngot').text('1')
	}
	
	if ($('.textGoodCommWord').val()!="") {
		
		
		$('.goodCommentKwd').text('1')
	}

	if ($('#ckUploadPic').hasClass('inputCheckBox-checked')) {
		$('.goodCommentPic').text('1');
	}

	$('.totalOrderNum').text(CDT.totalOrderNum);
	$('.speedTaskIngot').text($('input[name="speedTaskIngot"]:checked').val());
	$('.rewardIngotNum').text($('#extraRewardIngot').val());
	$('.buyTimeIntervalIngot').text(
			$('input[name="buyTimeInterval"]:checked').val());
}



//设置任务费用
function setTaskFee() {
/*	CDT.orderItemPrice = parseFloat($('#txtItemPrice').val(), 10) * parseInt($('#itemBuyNum').val(),10);*/
	CDT.orderItemPrice = 1 * 1;
	CDT.commissionFee = (commissionTable(CDT.orderItemPrice)-0.5);     //第一个金币
	CDT.mobileOrderFee= CDT.commissionFee + CDT.mobileExtIngot;
	var mobileOrderFee = CDT.commissionFee + CDT.mobileExtIngot;//第2个金币0.5+0.5=1
	//var fee = CDT.commissionFee * CDT.pcOrderNum + mobileOrderFee * CDT.mobileOrderNum;
	var fee =  mobileOrderFee * CDT.mobileOrderNum;
	
	//=0.5*0+1*x手机单数
	$('.itemPrice').number(CDT.orderItemPrice,2);
	$('.spPcUnitPrice').text(CDT.commissionFee);//费用总计：第一个单位
	$('.spWirelessUnitPrice').text(mobileOrderFee);//费用总计：第二个单位
	$('#taskStep-3 .spSumCost').number(fee, 2);//总费用
}





// 直通车图片填写后显示图片
function flushSubwayItemImg() {
//	alert('sss7');
	var url = $.trim($('#txtItemSubwayPicUrl').val());
	if (url.length <= 0) {
		return;
	}
	var img = '<img id="imgSubway" name="itemImg" src="' + url + '" />';
	$('#imgSubway').parent().html(img);
}





/*第二步：填写商品信息*/
function initSecondStepBind() {
	// 输入商品链接后检测商品基本信息
	$('#btnAutoFillItemInfo').click(function() {
		var $itemUrl = $('#txtItemUrl'),url = $.trim($itemUrl.val());
		if (!Tr.regs.url.test(url)) {
			if (!$itemUrl.hasClass('errorFocus')) {
				alert('请先输入商品链接！');
			}
			return;
		}
		// 校验通过
		var platform = $('#taskStep-1 .platform.selectedCtb').attr('value');
		var taskType=$("#taskStep-1 .taskType.selectedCtb").attr('value');
		Tr.get('/task/item', {
			url: url,
			p: platform,
			taskType:taskType
		}, function(data) {
			if (data.code == 555) {
				alert(data.msg);
				return;
			}
			if (data.code != 200) return;
			$('#itemInfo').show();
			var item = data.results;
			$('#txtItemTitle').val(item.title);
			$('#txtItemPicUrl').val(item.imgUrl).attr('itemPic', item.itemPic);
			// $('#txtItemPrice').val(item.price);
			$('#itemImgWrapperInStep1').html('<img id="itemImg" name="itemImg" src="' + item.imgUrl + '" />');
		});
	});

	//添加搜索关键词方案
	$(document).on('click', '.plusBlockBtn', function() {
		var $me = $(this),
			$tmp = CDT.searchWordPlanTempl,
			$wordBox = $('#' + $me.attr('on') + 'SearchWordBox');
		if ($wordBox.find('.subwayPanel').length == 3) {
			$me.parent().hide();
		}
		$wordBox.append($tmp).find('.close').tipsy({
			gravity: 's',
			html: true,
			opacity: 0.7,
			fallback: ''
		});
	});
	
	
	
	$(document).on('click', '.subwayplusBlockBtn', function() {
		
		var index=$(".subwayPancel").length+2;
		if(index>9){
			return false;
		}
		var html="<div class='subwayBoxs subwayPancel'>"+
						"<div class='panelLine sec subway' >"+
							"<span class='ls floatLeft itemPic'>素材图<span class='red bold'>*</span></span>"
							+"<input type='text' class='inputText floatLeft ls long required ' id='txtItemSubwayPicUrl"+index+"' name='itemSubwayPicUrl"+index+"' value='' itemPic=''/>"
							+"<div class='floatLeft' id='subwayBox"+index+"'>"
							+"<input type='button' id='btnPickfiles"+index+"' value='选择并上传素材图' />"
							+"</div>"
							+"</div>"
							+"<a href='javascript:;' class='close iconfont subwayClose'>&#xf0011;</a>"
							+"<div class='pic-box subway' id='imgSubwayWrapperInStep"+index+"'>"
							+"<img id='imgSubway"+index+"' name='itemImg"+index+"' src='' /></div></div>";
		$(this).parent().before(html);
		initUploader();
	});
	/*评论图模块*/
$(document).on('click', '.commentplusBlockBtn', function() {
		var index=$(".commentPicPancel").length+1;
		if(index>4){
			return false;
		}
		var html="<div class='commentPic commentPicPancel'>"+
						"<div class='panelLine sec comment' >"+
							"<span class='ls floatLeft itemPic'>评论图<span class='red bold'>*</span></span>"
							+"<input type='text' class='inputText floatLeft ls long required' id='txtCommentPicUrl"+index+"' name='itemCommentPicUrl"+index+"' value='' itemPic=''/>"
							+"<div class='floatLeft' id='CommentBox"+index+"'>"
							+"<input type='button' id='btnCommentfiles"+index+"' value='选择并上传评论图' />"
							+"</div>"
							+"</div>"
							+"<a href='javascript:;' class='close iconfont commentClose'>&#xf0011;</a>"
							+"<div class='pic-box subway' id='imgCommentWrapperInStep"+index+"'>"
							+"<img id='imgComment"+index+"' name='imgComment"+index+"' src='' /></div></div>";
		$(this).before(html);
		initUploaderComment();
	});

	// 删除搜素关键词方案
	$(document).on('click', '.legendPanel .close', function() {
		var $me = $(this);
		$me.parents('.panelBox').next().show();
		$(this).parents('.legendPanel').remove();
	});
	
	// 删除搜素关键词方案
	$(document).on('click', '.subwayPancel .close', function() {
		$(this).parents('.subwayPancel').remove();
	});

	// 删除评论图
	$(document).on('click', '.commentPicPancel .close', function() {
		$(this).parents('.commentPicPancel').remove();
	});

		// 使用淘宝搜索框查找商品
	$('#ckSearchTaobao').click(function() {
		var $me = $(this);
		if ($me.hasClass('inputCheckBox-checked')) {
			$(this).removeClass('inputCheckBox-checked');
			$('#tbSearchWordBox').hide();
			$('.panelBox1').hide();
			return;
		}
		$(this).addClass('inputCheckBox-checked');
		$('#tbSearchWordBox').show();
		if ($('#tbSearchWordBox .legendPanel').length < 6) {
			$('#tbSearchBtnBox').show();
		}
	});
	// 使用天猫搜索框查找商品
	$('#ckSearchTmall').click(function() {
		var $me = $(this);
		if ($me.hasClass('inputCheckBox-checked')) {
			$(this).removeClass('inputCheckBox-checked');
			$('#tmSearchWordBox').hide();
			$('.panelBox2').hide();
			return;
		}
		$(this).addClass('inputCheckBox-checked');
		$('#tmSearchWordBox').show();
		if ($('#tmSearchWordBox .legendPanel').length < 6) {
			$('#tmSearchBtnBox').show();
		}
	});
	
	
	/*第二步是否添加图文评价*/
	$('#ckUploadPic').click(function() {
		var $me = $(this);
		if ($me.hasClass('inputCheckBox-checked')) {
			$(this).removeClass('inputCheckBox-checked');
			CDT.goodCommentPicPrice=0;
			$(".orderNum").attr("disabled",false);
			$("#radCustomOrderNum").attr("checked",false);
			$("#txtCustomOrderNum").val("");
			$("#keyOrderNum").text(10);
			$("#txtCustomOrderNum").attr("disabled",false);
			//$("#pcOrderNum").val(1);
			$(".commentPancel").hide();
			$('.goodCommentPic').text('0');
			return;
		}
		$(this).addClass('inputCheckBox-checked');
		$(".orderNum").attr("disabled",true);
		CDT.totalOrderNum=1;
		CDT.goodCommentPicPrice=1;
		$("#radCustomOrderNum").attr("checked",true);
		$("#txtCustomOrderNum").val(1);
		$("#keyOrderNum").text(1);
		$("#txtCustomOrderNum").attr("disabled",true);
		//$("#pcOrderNum").val(1);
		$('.goodCommentPic').text('1');
		$(".commentPancel").show();
		
	});
	
	$('#checkCommBox').click(function() {
		var $me = $(this);
		if ($me.hasClass('inputCheckBox-checked')) {
			$(this).removeClass('inputCheckBox-checked');
			CDT.goodCommentKwdPrice=0;
			$('.goodCommentKwd').text('0');
			CDT.CALC_STEP_4.calc3();
			CDT.CALC_STEP_4.calc4();
			return;
		}
		$(this).addClass('inputCheckBox-checked');
		$('.goodCommentKwd').text('1');
		CDT.goodCommentKwdPrice=1;
		CDT.CALC_STEP_4.calc3();
		CDT.CALC_STEP_4.calc4();
	});

	// 宝贝所在地
	$('#btnRegionPicker').click(function() {
		var $flo = $('#regionFloatDiv');
		if ($flo.css('visibility') == 'visible') {
			$flo.css({
				visibility: 'hidden'
			});
			return;
		}
		var $me = $(this);
		$('#regionFloatDiv').css({
			visibility: 'visible',
			left: $me.position().left,
			top: $me.position().top + 32
		});
	});

	$('body').click(function(evt) {
		var e = window.event || evt,
			obj = $(e.srcElement || e.target);
		if (!obj.is('#regionFloatDiv,#regionFloatDiv *') && !obj.is('#btnRegionPicker')) {
			$('#regionFloatDiv').css('visibility', 'hidden');
		}
	});

	$(document).on('click', '.address', function() {
		var me = $(this).text().replace(/[\[\]]/g, '');
		$('#btnRegionPicker').attr('data-value', me).text(me);
		$('#regionFloatDiv').css({
			visibility: 'hidden'
		});
		$('#spLocation').attr('data-value', me).text(me);
	});

	// 回车添加多条订单留言	
	$(document).on('keydown', '#orderMessages .orderMsg5', function() {
		if(event.keyCode == '13') {
			var msg = $.trim($(this).val());
			if (Tr.countWords(msg) > 15) {
				alert('留言内容太长，请控制在15个字以内！');
				return false;
			}
			
			if (CDT.orderMsgCount >= 10) {
				alert('最多指定10条留言！');
				return false;
			}
			for (var i = 0; i < $('.orderMsg').length; i++) {
				if (!$('#orderMessages .orderMsg').eq(i).val()){
					alert('请填写完第一条留言内容再增加');
					return false;
				}
			}
			CDT.orderMsgCount++;
			var orderMsgInput = '<div class="panelLine sec"><input type="text" class="orderMsg orderMsg5 inputText floatLeft ls long" name="orderMsg' + CDT.orderMsgCount + '"></div>';
			$(this).parent().after(orderMsgInput).next().find('.orderMsg5').focus();
		}
	});
}



//价格校验
function priceCheck(){
    var price = parseFloat($('#pay .spRow-total-1').text().replace(/,/g,''),10)+parseFloat($('#pay .spRow-total-4').text().replace(/,/g,''),10);
    var ingot=parseFloat(($('span.ingot').text() || 0),10);
    var pledge = parseFloat($('span.pledge').text(),10);
    Tr.checkprice(price ,ingot,pledge);
}




$(function() {
	// console.log('--page load--'+CDT.taskIdStr);
	var isSys=$('input[name="expressType"]:checked').val();
	CDT.freight=isSys;
	$(".totalIngot").text(CDT.freight);
	// 初始化任务进度
	CDT.currStep = App.initStep;
	setTargetStep(CDT.currStep);

	if(CDT.currStep == 5){
		CDT.len = $('.stepFinish').length;
	} 
	

	// 初始化计算任务费用所需的相关参数
	initTaskPayFee();

	initSecondStepBind();

	//直接跳转步骤
	$(document).on('click','.stepFinish',function(){
		if(App.initStep == 5){
			return;
		}
		var step = $(this).parent('.stepWaiting').index();		
		setTargetStep(step+1);
		var $stepFa = $('.stepWrapper .stepWaiting');
		for (var i = step+1; i <5; i++) {
			$stepFa.eq(i).html((i+1) + $stepFa.eq(i).find('.caption').outerHTML());
		}     	      	
		CDT.currStep = step+1;
		if ($('#speedExamine a').hasClass('inputCheckBox-checked')) {
			$('.priorIngot').text('1');
		}
		if ($('#AddFen a').hasClass('inputCheckBox-checked')) {
			$('.AddFenIngot').text('1');
		}
		
		if($('#publishTime a').hasClass('inputCheckBox-checked')){
			$('.publishIntervalIngot').text('5');
		}
		if($('#ckUploadPic').hasClass('inputCheckBox-checked')){
			$('.goodCommentPic').text('1');
		}
		if($('#checkCommBox').hasClass('inputCheckBox-checked')){
			$('.goodCommentKwd').text('1');
		}
		if($('#cbExtraIngot').hasClass('inputCheckBox-checked')){
			var ingot = parseInt($('#extraRewardIngot').val(), 10);
			$('.rewardIngotNum').text(ingot);
			$('.totalExtraRewardIngot').number(ingot * CDT.totalOrderNum, 2);
		}
		CDT.CALC_STEP_4.calc1();
		CDT.CALC_STEP_4.calc2();
		CDT.CALC_STEP_4.calc3();
		CDT.CALC_STEP_4.calc4();
		
	});

	
	
	// 搜索关键词方案：从原始片段复制一个空白副本作为模板
	CDT.searchWordPlanTempl = $('#keywordTemp').remove().val();
	CDT.dianpuTempl = $('#dianpuTempl').remove().val();
	Mustache.parse(CDT.dianpuTempl);

    priceCheck();
 
 
    $.validator.addMethod('checkItemmsg', function(value, element) {
		var flag = !0,
			inputs = $('.orderMsg').not(element);
			value = $.trim(value);
		$.each(inputs, function(i) {
			if(value == inputs.eq(i).val() && value) {
				flag = !1;
			}
		});
		return flag;
	}, Tr.error('订单留言不能重复！'));


	/*下一步*/
	$('.buttonSection .next').click(function() {
		// 检查输入
		if (!getFormValidator().form()) {
			return;
		}
		var googdComment=$("#taskStep-1 .commentType.selectedCtb").attr("value");
		var plat=$("#taskStep-1 .platform.selectedCtb").attr("value");
		
		var shopType=$("#taskStep-1 .taskType.selectedCtb").attr("value");
		if (CDT.currStep == 1) {
			//alert('11');
			// 店铺存在且已选择
			if ($('#dianpuContainer .shop.selectedCtb').length <= 0) {
				alert('必须先选择店铺！');
				return;
			}
			
		}

		// 检查第二步输入：商品信息
		if (CDT.currStep == 2) {
			//alert('222');
			setTaskFee();
	       
	     
	    }

		// 检查第三步输入：刷单数量
		if (CDT.currStep == 3) {
			//alert('33');
			// 检查订单分布设置是否正确
			// 为下一步准备数据
			//CDT.totalOrderNum = parseFloat($('#pcOrderNum').val(),10)+parseFloat($('#wirelessOrderNum').val(),10);
			CDT.totalOrderNum =parseFloat($('#wirelessOrderNum').val(),10);
			//alert('CDT.totalOrderNum'+CDT.totalOrderNum);
			$('.totalOrderNum').text(CDT.totalOrderNum);
			if($('#cbExtraIngot').hasClass('inputCheckBox-checked')){
				var ingot = parseInt($('#extraRewardIngot').val(), 10);
				//alert("ingot"+ingot);
				$('.rewardIngotNum').text(ingot);
				$('.totalExtraRewardIngot').number(ingot * CDT.totalOrderNum, 2);
				//alert("===="+ingot * CDT.totalOrderNum);
			}
			
			initUploaderComment();//初始化上传评论图片
			CDT.CALC_STEP_4.calc1();
			CDT.CALC_STEP_4.calc2();
			CDT.CALC_STEP_4.calc3();
			CDT.CALC_STEP_4.calc4();
			
			
			var wordPlanBoxesNum = [];
			var html="";
			var totalFlow=0;
			$.merge(wordPlanBoxesNum, $('#searchPlanTotalNum .sec'));
			$(wordPlanBoxesNum).each(function(n) {
				var totalNum = $.trim($(this).find('input[name="searchItemTotalNum"]').val());
				var key = $.trim($(this).find('.keyText').text());
				var flowNum = $.trim($(this).find('input[name="searchItemFlowNum"]').val());
				if(flowNum!="")
				totalFlow+=parseInt(flowNum);
				html += "<div class='panelLine sec'>"
					+ "<span class='floatLeft ls keyText'>"+key+"<span class='red'>"+totalNum+"</span>单</span>"
					+ "<input type='text' class='inputText floatLeft ls short' name='searchFlowNum'  value='"+flowNum+"'/>"
					+ "<span class='floatLeft ls'>个访客</span></div>";
			});
			$("#flowCharge").html("");
			$("#flowCharge").append(html);
			if(parseInt(CDT.flowNum)>=totalFlow)
				$("#flowTip").html("您目前剩余流量是<span class='red'>"+CDT.flowNum+"</span>个,任务发布花费<span class='red'>"+totalFlow+"</span>个流量,任务发布后剩余流量是<span class='red'>"+(parseInt(CDT.flowNum)-totalFlow)+"</span>个");
			else if(parseInt(CDT.flowNum)<totalFlow)
				$("#flowTip").html("<a class='red' href='/user/money'>您目前流量不足已支付，请先充值后再发布或者调整关键词对应的访客后再发布！</a>");
			else
				$("#flowTip").html("您目前剩余流量是<span class='red'>"+CDT.flowNum+"</span>个");
		}
		$("input[name='searchFlowNum']").keyup(function() {
			var wordPlanBoxesFlowNum = [];
			$.merge(wordPlanBoxesFlowNum, $('#flowCharge .sec'));
			var totalFlow=0;
			$(wordPlanBoxesFlowNum).each(function(n) {
				var totalNum = $.trim($(this).find('input[name="searchFlowNum"]').val());
				if(totalNum!="")
				totalFlow+=parseInt(totalNum);
			});
			if(parseInt(CDT.flowNum)>=totalFlow)
				$("#flowTip").html("您目前剩余流量是<span class='red'>"+CDT.flowNum+"</span>个,任务发布花费<span class='red'>"+totalFlow+"</span>个流量,任务发布后剩余流量是<span class='red'>"+(parseInt(CDT.flowNum)-totalFlow)+"</span>个");
			else if(totalFlow>parseInt(CDT.flowNum))
				$("#flowTip").html("<a class='red' href='/user/money'>您目前流量不足已支付，请先充值后再发布或者调整关键词对应的访客后再发布！</a>");
			else
				$("#flowTip").html("<span class='red'>您输入错误！</span>");
		});
		
		
		// 第三步之前直接跳下一步，第四步需保存任务成后才能进入下一步
		if(CDT.currStep <4 ){
//			alert('sss');
			//initTaskPayFee();
			
			setTargetStep(++CDT.currStep);
			return;
		}
		// 第四步：增值服务，创建任务
		if (CDT.currStep == 4) {
			
			//alert('第四步');
			var textGoodCommWord="";
			$(".textGoodCommWord").each(function(){
				//alert('textGoodCommWord');
				if($(this).val()!="")
				textGoodCommWord+=$(this).val()+",";
			});	
			var itemSubwayPicUrl="";
			$(".subwayBoxs input[type='text']").each(function(){
				itemSubwayPicUrl+=$(this).val()+",";
			});
			
			var p = {
				'task.platform': $('#taskStep-1 .platform.selectedCtb').attr('value'),
				'task.type': $('#taskStep-1 .taskType.selectedCtb').attr('value'),
				'task.shopId': $('#dianpuContainer .shop.selectedCtb').attr('value'),
				'task.shopName': $('#dianpuContainer .shop.selectedCtb').text(),
				'task.itemTitle': $('#txtItemTitle').val(),
				'task.itemUrl': $('#txtItemUrl').val(),
				'task.itemPicUrl': $('#txtItemPicUrl').val(),
				'task.itemPic': $('#txtItemPicUrl').attr('itemPic'),
				
				'task.itemSubwayPicUrl': itemSubwayPicUrl,
				
				
				'task.qq':$('#qq').val(),
				// 刷单数量
				//'task.pcOrderNum': $('#pcOrderNum').val(),
				'task.mobileOrderNum': $('#wirelessOrderNum').val(),
				'task.taskRequest':$.trim($("#taskRequest").val()),
				// 增值服务
				'task.sysRefund':$('#sysRefund .inputCheckBox').hasClass('inputCheckBox-checked'),
				'task.speedExamine': $('#speedExamine').find('.inputCheckBox').hasClass('inputCheckBox-checked'),
				'task.AddFen': $('#AddFen').find('.inputCheckBox').hasClass('inputCheckBox-checked'),
				
			};
			
				
			
			
			// 增值服务
			p['task.speedTaskIngot'] = 0;
			if ($('#speedTaskIngot input:checked').val()) {
				p['task.speedTaskIngot'] = $('#speedTaskIngot input:checked').val();
			}
			p['task.extraRewardIngot'] = 0;
			if ($('#cbExtraIngot').hasClass('inputCheckBox-checked')) {
				//alert(''+$('#extraRewardIngot').val());
				p['task.extraRewardIngot'] = $('#extraRewardIngot').val();
			}
			p['task.goodCommentWords'] = "";
			if ($('#checkCommBox').hasClass('inputCheckBox-checked')) {
				p['task.goodCommentWords'] = $.trim(textGoodCommWord);
			}
			p['task.publishTimerInterval'] = 0;
			p['task.publishTimerValue'] = 0;
			if ($('#publishTime .inputCheckBox').hasClass('inputCheckBox-checked')) {
				p['task.publishTimerInterval'] = $('#selPublishInterval').val();
				p['task.publishTimerValue'] = $('#selPublishTimerValue').val();
			}
			p['task.buyTimeInterval'] = $('#buyTimeInterval input:checked').val();
			
			p['task.delaySpan'] = $('#selDelaySpan').val();
			
			//alert('设置id');
			
			// 设置ID
			if (CDT.taskIdStr.length > 0) {
				p['task.id'] = CDT.taskIdStr;
			}else{
				p['task.id'] = $('#btnConfirmPayment').attr('data-tid');
			}
			//alert('设置id');
			// console.log('--save--'+CDT.taskIdStr);
			Tr.post('/task/save2', p, function(data) {
				//alert('提交保存');
				if (data.code == 800101) {
					alert('待支付的订单不能进行修改！');
				}
				if (data.code == 8001) {
					alert(data.msg);
				}
				if (data.code != 200) {
					//alert(''+data.code);
					return;
					
				}
				CDT.taskIdStr = data.results;
				//alert('任务id:'+CDT.taskIdStr);
				// console.log(CDT.taskIdStr+'---');

			//校验价格
			priceCheck();

				// 进入下一步
		setTargetStep(++CDT.currStep);
	});
		}
	});


	/*上一步*/
	$('.prev').click(function() {
		if(App.initStep == 5){
			alert('待支付的订单不能进行修改！');
			return;
		}
		// 第五步：确认付款
		//为上一步准备数据
		if ($('#speedExamine a').hasClass('inputCheckBox-checked')) {   //加速审核
			$('.priorIngot').text('1');
		}
		
		if ($('#AddFen a').hasClass('inputCheckBox-checked')) {   //加速审核
			$('.AddFenIngot').text('1');
		}
		
		if($('#publishTime a').hasClass('inputCheckBox-checked')){    //任务发布间隔
			$('.publishIntervalIngot').text('5');
		}
		if($('#cbExtraIngot').hasClass('inputCheckBox-checked')){
			var ingot = parseInt($('#extraRewardIngot').val(), 10);
			//alert('ingot'+ingot);
			$('.rewardIngotNum').text(ingot);
			$('.totalExtraRewardIngot').number(ingot * CDT.totalOrderNum, 2);
		}
		CDT.CALC_STEP_4.calc1();
		CDT.CALC_STEP_4.calc2();
		CDT.CALC_STEP_4.calc3();
		CDT.CALC_STEP_4.calc4();
		$errBox = $('#taskStep-5 .errBox');
		if ($errBox.length > 0) {
			alert('这里出错了');
			$errBox.hide();
		}	

		setTargetStep(--CDT.currStep);
	});

	
	
	
	
	/*第一步：选任务*/
	$(document).on('click', '#taskStep-1 .checkTextBtn', function() {
		// 选择平台时切换店铺
		if ($(this).hasClass('platform')) {
			var platform = $(this).attr('value');
			//alert(''+platform);
			loadDianpu(platform);
		}
		$(this).parent().children('.selectedCtb').removeClass('selectedCtb');
		$(this).addClass('selectedCtb');
		var p=$('#taskStep-1 .platform.selectedCtb').attr('value');
		if(p=='WEIXIN'){
			$("#taskStep-1 span[value='PENGYOUQUAN']").show();
			$("#taskStep-1 span[value='TOUPIAO']").hide();
			$("#taskStep-1 span[value='QUNFA']").hide();
			$("#taskStep-1 span[value='SEARCH']").hide();
			$("#taskStep-1 span[value='TWOCODE']").hide();
			$("#taskStep-1 span[value='SUBWAY']").hide();
		}else if(p=='TAOBAO'||p=='TMALL'){
			$("#taskStep-1 span[value='PENGYOUQUAN']").hide();
			$("#taskStep-1 span[value='SEARCH']").show();
			$("#taskStep-1 span[value='TWOCODE']").show();
			$("#taskStep-1 span[value='SUBWAY']").show();
		}else{
			$("#taskStep-1 span[value='PENGYOUQUAN']").hide();
			$("#taskStep-1 span[value='TOUPIAO']").hide();
			$("#taskStep-1 span[value='QUNFA']").hide();
			$("#taskStep-1 span[value='SEARCH']").hide();
			$("#taskStep-1 span[value='TWOCODE']").hide();
			$("#taskStep-1 span[value='SUBWAY']").hide();
		}
		if($(this).hasClass('shop')) {
			$('#dianpuContainer').attr('curr-shop',$(this).attr('value'));
		}
		//选择类型
		if($('.taskType').eq(0).hasClass('selectedCtb')){
			$('.subway').show();
			$('#subwayPancel').show();
		}else{
			$('.subway').hide();
			$('#subwayPancel').hide();
		}
	});
	

	
	
	
	
	
	
	
	
	/*第二步：填写商品信息*/
	//判断链接是否含有http://
		$('input[name ="itemUrl"]').on('keyup',function(){
			$(this).val(Tr.checkurl($(this).val()));
		});	

	$('.itemPic').tipsy({
		gravity: 's',
		html: true,
		opacity: 0.7,
		fallback: '便于买手识别任务商品'
	});

	


	
	/*第三步：设置刷单数量*/
	$('input[name="r_tade_num"]:radio').click(function() {
		var count = $(this).attr('data-count');
		//alert('count--'+count);//根据单选按钮获得单数
		if (!count) {
			//自定义模式
			CDT.orderCountModal = 'custom';
			$('#wirelessOrderNum').val($('#txtCustomOrderNum').val());
		}
		
		else {
			//alert('ss');
			CDT.orderCountModal = 'speci';
			CDT.totalOrderNum = count;
			$('#wirelessOrderNum').val(count);
			$('#keyOrderNum').text(count);
		}
		
		
		//设置文本框显示
		//$('#pcOrderNum').val(0);
		CDT.mobileOrderNum = count;
		
		//设置价格
		$('.num_spPcOrderCount').text(0);
		$('.num_spWirelessUnitPrice').text(count);//设置第二个单位的单数
		
		//alert('数量 ：'+count+'*单位:'+CDT.mobileOrderFee+'总价'+(CDT.mobileOrderFee * count));
		$('.spSumCost').number(CDT.mobileOrderFee * count, 2);
		//console.log('--设置totalOrderNum');
	});

	// 自定义数量
	$('#radCustomOrderNum').click(function() {
		$('#txtCustomOrderNum').focus();
		//$('#pcOrderNum').val(0);
		$('.spPcOrderCount').text(0);
	});
	//获取焦点自动改变订单选择
	$('#txtCustomOrderNum').focus(function(){
		CDT.orderCountModal = 'custom';
       $('input[name="r_tade_num"]').eq(6).attr('checked','checked');
	});
	// 填写自定义数量时修改订单分布
	$('#txtCustomOrderNum').keyup(function() {
		if (CDT.orderCountModal != 'custom') return;
		if ($(this).hasClass('invalid')) {
			return;
		}
		$('#wirelessOrderNum').val($(this).val());
		//$("#keyOrderNum").text($(this).val());
		$('.num_spWirelessUnitPrice').text($(this).val());
		//$('#pcOrderNum').val(0);
		
		//var pcOrderNum =commissionTable(CDT.orderItemPrice)*parseFloat($('#pcOrderNum').val(),10);
		var wirelessOrderNum=(commissionTable(CDT.orderItemPrice))*parseFloat($('#wirelessOrderNum').val(),10);
		//var spSumCost = pcOrderNum + wirelessOrderNum;
		var spSumCost =wirelessOrderNum;
		//alert('spSumCost'+spSumCost);
		if(!$('#txtCustomOrderNum').val()){
			//$('#pcOrderNum').val(0);
        	$('.spSumCost').text('0');
        }else{
			$('.spSumCost').text(spSumCost);
        }
        //console.log('--设置customTotalOrderNum');
	}).blur(function() {
		if (CDT.orderCountModal != 'custom') return;
		CDT.totalOrderNum = parseInt($(this).val(), 10);
	});
	
	
	

	/*第四步：增值服务*/
	//平台直接返款
	$('#sysRefund a').click(function(){
		var platformRefund = parseInt($('#platformRefund .itemPrice').text(), 10),
		    spPlatformRefundNum =sysRefundCount(platformRefund) * CDT.totalOrderNum;
		if (!$(this).hasClass('inputCheckBox-checked')) {
			$(this).addClass('inputCheckBox-checked');
			$('#platformRefund').show();
			$('.spPlatformRefundNum').number(spPlatformRefundNum, 2);
			//动态显示金币和总价的增减
			CDT.CALC_STEP_4.calc3();
			CDT.CALC_STEP_4.calc4();
			return;
		}
		$(this).removeClass('inputCheckBox-checked');
		$('#platformRefund').hide();
		$('.spPlatformRefundNum').number(0, 2);
		CDT.CALC_STEP_4.calc3();
		CDT.CALC_STEP_4.calc4();
	});
	
	
	//提升任务完成速度
	$('input:radio[name="speedTaskIngot"]').click(function() {
		$('.speedTaskIngot').text($(this).val());
		//动态显示金币和总价的增减
		CDT.CALC_STEP_4.calc3();
		CDT.CALC_STEP_4.calc4();
	});

	$("#txtItemUrl").change(function(){
		$("#txtItemTitle").val("");
		$("#txtItemPicUrl").val("");
		$("#itemImg").attr("src","");
	});
	// 输入完毕后，检查佣金输入值，自动计算费用
	$('#extraRewardIngot').keyup(function() {
		var ingot = parseInt($(this).val(), 10);
		if (isNaN(ingot)) {
			ingot = 0;
		}

		if (ingot > 0) {
			$('#cbExtraIngot').addClass('inputCheckBox-checked');
		} else {
			$('#cbExtraIngot').removeClass('inputCheckBox-checked');
		}

		$('.rewardIngotNum').text(ingot);
		$('.totalExtraRewardIngot').number(ingot * CDT.totalOrderNum, 2);
		//动态显示金币和总价的增减
		CDT.CALC_STEP_4.calc3();
		CDT.CALC_STEP_4.calc4();
	});
	// 切换加赏勾选
	$('#cbExtraIngot').click(function() {

		if (!$(this).hasClass('inputCheckBox-checked')) {
			$(this).addClass('inputCheckBox-checked');
			if ($('#extraRewardIngot').val().length <= 0) {
				$('#extraRewardIngot').val(1);
			}
			var ingot = parseInt($('#extraRewardIngot').val(), 10);
			$('.rewardIngotNum').text(ingot);
			$('.totalExtraRewardIngot').number(ingot * CDT.totalOrderNum, 2);
			//动态显示金币和总价的增减
			CDT.CALC_STEP_4.calc3();
			CDT.CALC_STEP_4.calc4();
			return;
		}
		$('#extraRewardIngot').val('');
		$(this).removeClass('inputCheckBox-checked');
		$('.rewardIngotNum,.totalExtraRewardIngot').text('0');
		CDT.CALC_STEP_4.calc3();
		CDT.CALC_STEP_4.calc4();
	});
	// 优先审单
	$('#speedExamine').click(function() {
		var $ckBox = $(this).children('.inputCheckBox');
		if ($ckBox.hasClass('inputCheckBox-checked')) {
			$ckBox.removeClass('inputCheckBox-checked');
			$('.priorIngot').text('0');
			//动态显示金币和总价的增减
			CDT.CALC_STEP_4.calc3();
			CDT.CALC_STEP_4.calc4();
			return;
		}
		$ckBox.addClass('inputCheckBox-checked');
		$('.priorIngot').text('1');
		//动态显示金币和总价的增减
		CDT.CALC_STEP_4.calc3();
		CDT.CALC_STEP_4.calc4();
	});

	$('#AddFen').click(function() {
		var $ckBox = $(this).children('.inputCheckBox');
		if ($ckBox.hasClass('inputCheckBox-checked')) {
			$ckBox.removeClass('inputCheckBox-checked');
			$('.AddFenIngot').text('0');
			//动态显示金币和总价的增减
			CDT.CALC_STEP_4.calc3();
			CDT.CALC_STEP_4.calc4();
			return;
		}
		$ckBox.addClass('inputCheckBox-checked');
		$('.AddFenIngot').text('1');
		//动态显示金币和总价的增减
		CDT.CALC_STEP_4.calc3();
		CDT.CALC_STEP_4.calc4();
	});
	
	//若没有在编辑的任务默认选择第一个平台，第一个选择类型
	if (!$('.platform').hasClass('selectedCtb')) {
		$('.platform').eq(0).addClass('selectedCtb');
	}
	if (!$('.taskType').hasClass('selectedCtb')) {
		$('.taskType').eq(0).addClass('selectedCtb');
	}
	var platform = $('.platform.selectedCtb').attr('value');
	loadDianpu(platform);
	
	
	//点击任务发布间隔
	$('#publishTime').click(function() {
		var $me = $(this).children('.inputCheckBox');
		if ($me.hasClass('inputCheckBox-checked')) {
			$me.removeClass('inputCheckBox-checked');
			$('.publishIntervalIngot').text('0');
			//动态显示金币和总价的增减
			CDT.CALC_STEP_4.calc3();
			CDT.CALC_STEP_4.calc4();
			return;
		}
		$me.addClass('inputCheckBox-checked');
		$('.publishIntervalIngot').text('5');
		//动态显示金币和总价的增减
		CDT.CALC_STEP_4.calc3();
		CDT.CALC_STEP_4.calc4();
	});
	//选择时间周期
	$('input:radio[name="buyTimeInterval"]').click(function() {
		$('.buyTimeIntervalIngot').text($(this).val());
		//动态显示金币和总价的增减
		CDT.CALC_STEP_4.calc3();
		CDT.CALC_STEP_4.calc4();
	});
	var commentWordInput = '<input type="text" class="inputText floatLeft ls middle textGoodCommWord" value="" name="goodcomword" />';
	$(document).on('keyup', '.textGoodCommWord', function() {
		var text = $.trim($(this).val());
		if (event.keyCode == '13') {
			if (text.length > 650) {
				alert('好评词语太长，请控制在650个字以内！');
				return false;
			}
			if (CDT.commentWordCount == 650) {
				alert('最多指定650个词语！');
				return false;
			}
			$(commentWordInput).insertAfter($('.textGoodCommWord').last()).focus();
			CDT.commentWordCount++;
			return;
		}
		if (!text && $('.textGoodCommWord').length > 1) {
			$(this).remove();
			CDT.commentWordCount--;
		}
	});

	/*第五步：确认付款*/
	$('#btnConfirmPayment').click(function() {
		//alert('确认付款');
		// 为“待编辑”任务设置id
		if (CDT.taskIdStr.length <= 0) {
			CDT.taskIdStr = $(this).attr('data-tid');
		}
		//alert('CDT.taskIdStr '+CDT.taskIdStr);
		// console.log('--cofirm--'+CDT.taskIdStr);
		//alert('设置支付方式');
		// 设置支付方式
		var useIngot = false,usePledge = false, other = false;
		if($('#payType .ingot').attr('checked')){
			useIngot = true;
		}
		if($('#payType .pledge').attr('checked')){
			usePledge = true;
		}

		if($('#platformSelect input:radio[name="pay_type"]:checked').val()){
			other = true;
		}
		//alert('使用网银支付吗？');
		if(other){
			if(!confirm('确认用网银支付？')){
				return;
			}
			var win = window.open(),openDtf = function(url){
				setTimeout(function(){
					win.location = url;
				}, 200);
			};
		}
		//alert('提交申请');
		Tr.post('/task/publish/confirmPay2', {
			'tidStr': CDT.taskIdStr,
			'useIngot': useIngot,
			'usePledge': usePledge,
			'other': other
		}, function(data) {
			if (data.code == 800101){
				alert('该任务已支付过~~');
				window.location.href='/seller/tasks';
			}
			if (data.code == 8001){
				alert(data.msg);				
			} 
			if (data.code != 200) return;
			if(other){
				alert('other');
				// 弹出模态窗口：支付成功 or 支付遇到问题
				var bank = $('#payType .bank:checked').val();
				var url = '/user/pay?type=TASK&p=' + bank + '&id=' + data.results;
				Tr.popup('bankPayment');
				openDtf(url);
				return;
			}
			alert('支付成功');
			window.location.href='/seller/tasks2';
		});

	});

	//银行选择小图标
	$(document).on('click', '#taskStep-5 .pay', function() {
		$(this).find('input').attr('checked','checked');
		$('input[type="checkbox"]').removeAttr('checked');
	});
	$(document).on('click', '#platformSelect .checkTextBtn', function() {
		var platform = $(this).attr('value');
		$(this).parent().children('.selectedCtb').removeClass('selectedCtb').end().end().addClass('selectedCtb');
		$('#' + platform +'Bind').show().siblings().hide();
	});
	$('input[type="checkbox"]').click(function(){
		$('input[type="radio"]').removeAttr('checked');
		$('.pay').removeClass('selectedCtb');
	});
	//网银支付弹窗支付结果的两个方式
	$('#btnbankpay').click(function(){
    	$('#bankPayment').hide();
    	location.href='/seller/tasks2';
    });
    $('#btnpayerror').click(function(){
    	$('#bankPayment').hide();
    });
   
});








