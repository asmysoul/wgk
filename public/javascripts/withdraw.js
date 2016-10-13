CDT = {
	quyuTemp: null,
	oldAlipayTemp: null,
	oldTenpayTemp: null,
	oldBankTemp: null,
	taskRowTemp: null,
	currPageNo: 1,
	pageSize: 20,
	type: 'withdraw',
	isReward: false,
	userType: null,
	buyerWidthdrawFee: 0.05,
	sellerWidthdrawFee: 0.003
};

//三种方式绑定账号
function bindAccount() {
	var platform = $('#platformSelect .selectedCtb').attr('value');
	var userId = $('#platformSelect').attr('userId');
	$('.validate').remove();
	if (platform == 'alipay') {
		var name = $('#txtAlipayName').val();
		var no = $('#txtAlipayNo').val();
		var id = $('#alipayMark').attr('fid');
		if (id) {
			alert('您已绑定过不能修改，如有特殊情况请联系管理员');
			return;
		}
		Tr.post('/user/money/fundAccount/save', {
			'account.id': id,
			'account.no': no,
			'account.name': name,
			'account.userId': userId,
			'account.type': 'ALIPAY'
		}, function(data) {
			if (data.code == 800101) {
				alert(data.msg);
			}
			if (data.code != 200) {
				return;
			}
			alert('绑定成功!');

			//更新当前绑定信息
			$('#alipayId').html(data.results.no);
			if (id) {
				$('#alipayMark').html(data.results.no);
			} else {
				$('.bindFirstAli').remove();
				var output1 = Mustache.render(CDT.oldAlipayTemp, data);
				$('#alipayBind').prepend(output1);
			}
			$('#alipayBind input').val('');

		});
	}

	if (platform == 'tenpay') {
		var name = $('#txtTenpayName').val();
		var no = $('#txtTenpayNo').val();
		var id = $('#tenpayMark').attr('fid');
		if (id) {
			alert('您已绑定过不能修改，如有特殊情况请联系管理员');
			return;
		}
		Tr.post('/user/money/fundAccount/save', {
			'account.id': id,
			'account.no': no,
			'account.name': name,
			'account.userId': userId,
			'account.type': 'TENPAY'
		}, function(data) {
			if (data.code == 800101) {
				alert(data.msg);
			}
			if (data.code != 200) {
				return;
			}
			alert('绑定成功!');

			//更新当前绑定信息
			$('#tenpayId').html(data.results.no);
			if (id) {
				$('#tenpayMark').html(data.results.no);
			} else {
				$('.bindFirstTen').remove();
				var output2 = Mustache.render(CDT.oldTenpayTemp, data);
				$('#tenpayBind').prepend(output2);
			}
			$('#tenpayBind input').val('');

		});
	}

	if (platform == 'bank') {
		var name = $('#txtBankName').val(),
			no = $('#txtBankNo').val(),
			id = $('#bankMark').attr('fid'),
			openingBank = $('#txtOpeningBank').val(),
			address = $('#address').val(),
			type = $('#txtBankType').val();
		$('.quyu').remove();
		if (!address) {
			$('#quyu').append('<span style="color:red" class="quyu"><span class="iconfont">&#xf0155;</span>&nbsp;必填</span>');
			return;
		}
		if (id) {
			alert('您已绑定过不能修改，如有特殊情况请联系管理员');
			return;
		}
		Tr.post('/user/money/fundAccount/save', {
			'account.id': id,
			'account.no': no,
			'account.name': name,
			'account.userId': userId,
			'account.type': type,
			'account.openingBank': openingBank,
			'account.address': address
		}, function(data) {
			if (data.code == 800101 || data.code == 8001) {
				alert(data.msg);
			}
			if (data.code != 200) {
				return;
			}
			alert('绑定成功!');

			//更新当前绑定信息
			$('#bankId').html(data.results.no);
			$('#first').find('option').eq(0).attr('selected', 'selected');
			$('#second').find('option').eq(0).attr('selected', 'selected');
			if (id) {
				$('#bankMark').html(data.results.no);
			} else {
				$('.bindFirstBank').remove();
				var output3 = Mustache.render(CDT.oldBankTemp, data);
				$('#bankBind').prepend(output3);
			}
			$('#bankBind input').val('');
			
		});
	}
	window.location = '/user/withdraw';
}
//加载平台已返款任务列表
function loadTask(pageNo) {
	Tr.get('/buyer/tasks/listRefund', {
		'vo.module': 'SYS_REFUND_WITHDRAW',
		'vo.pageNo': pageNo,
		'vo.pageSize': CDT.pageSize
	}, function(data) {
		if (data.code != 200) {
			return;
		}
		//todo
		if (data.results.length <= 0) {
			$('#taskContainer').addClass('warnBox').html('<span class="iconfont">&#xf00b6;</span>没有任务!');
			return;
		}
		var output = Mustache.render(CDT.taskRowTemp, $.extend(data, {
			platIcon: function() {
				return 'plat_' + this.platform;
			},
			paidFeeStr: function() {
				return $.number(this.paidFee / 100, 2);
			},
			modifyTimeStr: function() {
				return new Date(this.modifyTime).Format('yyyy-MM-dd hh:mm');
			}
		}));
		$('#taskContainer').removeClass('warnBox').html(output);
		
		$('.pagination').pagination(data.totalCount, {
			items_per_page: CDT.pageSize,
			num_display_entries: 5,
			current_page: pageNo,
			num_edge_entries: 2,
			callback: loadTaskCallBack,
			callback_run: false
		});
		CDT.currPageNo = pageNo;
	});
}
function loadTaskCallBack(index, jq) {
	loadTask(index + 1);
}

function initBase() {
	/* 提现/退款账号：选择不同的账号显示修改区域 */
	$(document).on('click', '#platformSelect .checkTextBtn', function() {
		var platform = $(this).attr('value');
		$('#' + platform + 'Bind').show().siblings().hide();
		$(this).addClass('selectedCtb').siblings().removeClass('selectedCtb');

	});

	/*绑定退款、提现账号*/
	var validator = $('#bindForm').validate({
		rules: {
			name: {
				minlength: 2
			},
			alipayNO: 'alipayNo',
			tenpayNO: 'tenpayNO',
			bankNO: 'bankNo',
			checkBankNO: {
				equalTo: '#txtBankNo'
			},
			opengingBank: {
				minlength: 3
			}
		}
	});
	// 自定义校验充值格式
	var validator1 = $('#fm1').validate({
		onkeyup: false,
		rules: {
			'password': {
				required: true,
				maxlength: 20
			},
			'ChargeVal1': {
				digits: true,
				min: 100,
				price: true
			}
		},
		messages: {
			'ChargeVal1': {
				digits: Tr.error('必须为整数！')
			}
		}
	});
   
	$(document).on('click', '#bindForm .bindingBtn', function() {
		if (!validator.form()) {
			return;
		}
		var bindType = $(this).parents('.bindMsg').attr('id');
		Tr.popup('confirmPlatformWnd');
		if(bindType == 'alipayBind'){
			$('#alipayBindTemp').show();
			$('#tenpayBindTemp,#bankBindTemp').hide();
			$('#alipayName').text($('#txtAlipayName').val());
			$('#alipayNo').text($('#txtAlipayNo').val());
		}
		if(bindType == 'tenpayBind'){
			$('#tenpayBindTemp').show();
			$('#alipayBindTemp,#bankBindTemp').hide();
			$('#tenpayName').text($('#txtTenpayName').val());
			$('#tenpayNo').text($('#txtTenpayNo').val());
		}
		if(bindType == 'bankBind'){
			$('#bankBindTemp').show();
			$('#tenpayBindTemp,#alipayBindTemp').hide();
            $('#bankName').text($('#txtBankName').val());
            $('#bankType').html('<span id="#bankType"class="bold"><img src="/public/images/bank/'+$('#txtBankType').val()+'.gif"/></span>');
            $('#bankNO').text($('#txtBankNo').val());
            $('#quyuFirst').text($('#first').val());
            $('#quyuSecond').text($('#second').val());
            $('#spOpeningBank').text($('#txtOpeningBank').val());
		}	
			
	});

	$(document).on('click','#confirmPlatformWnd #btnConfirm',function(e){
		bindAccount();
		
	});
	$(document).on('click','#confirmPlatformWnd #btnCancel',function(){
		$('#confirmPlatformWnd').hide();
	});

	/* 押金、佣金提现：提交提现申请 */
	$(document).on('click', '#applyWithdraw', function() {
		if (!validator1.form()) {
			return;
		}
		var amount = $('#txtWithdrawAmount').val(),
			payPass = $('#pword').val();
		Tr.post('/user/withdraw/apply', {
			'amount': amount,
			'payPass': payPass
		}, function(data) {
			if (data.code == 800101 || data.code == 8001) {
				alert(data.msg);
			}
			if (data.code != 200) return;

			alert('提现申请已提交平台!');
			//更新当前提现信息
			window.location = '/user/withdraw';
		});
	});

	// 卖家提现区域，银行卡“立即绑定”按钮
	$(document).on('click', '#btnBindWithdraw', function(e) {
		//$('.taskListType.pc0').addClass('task-family-on').siblings().removeClass('task-family-on');
		$('#boxWithdraw').hide();
		// 将“银行卡”选中
		$('#platformSelect .moneyAccountBox').removeClass('selectedCtb').eq(2).addClass('selectedCtb');
		$('#tenpayBind').hide();
		$('#bankBind').show();
		$('#boxBindAccount').show();
	});



	//选择区域 绑定银行卡 地址栏
	$(document).on('change', '#first', function() {
		var $me = $(this);
		var id = $me.find('option:selected').attr('rid');
		$('#address').val('');
		$('.quyu').remove();
		$('#second').find('option').eq(0).nextAll('option').remove();
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
		if ($(this).val()) {
			var add = $.trim($('#first').val()) +','+ $.trim($('#second').val());
			$('#address').val(add);
			$('.quyu').remove();
			$('#quyu').append('<span class="iconfont quyu" style="color:green">&#xf0156;</span>');
		} else {
			$('#address').val('');
		}
	});

	$(document).on('click', '.fundTab', function() {
		$(this).addClass('focus').siblings().removeClass('focus');
		CDT.type = $(this).attr('type');
		CDT.isReward = $(this).attr('isReward');
		loadMemberRecord(1);
	});

	/*实时计算提现的实际到账金额*/
	$(document).on('keyup', '#txtWithdrawAmount', function() {
		var feePercent = CDT.sellerWidthdrawFee;
		if (CDT.userType == 'BUYER') {
			feePercent = CDT.buyerWidthdrawFee;
		}
		var chargeVal1 = parseFloat($.trim($('#txtWithdrawAmount').val()));
		if (isNaN(chargeVal1)) {
			$('#spRealWithdrawAmount').html(0);
			return;
		}
		var money = chargeVal1 - chargeVal1 * feePercent;
		$('#spRealWithdrawAmount').html(money);
	});

	/* 切换Tap */
	$(document).on('click', '.withdrawOpTab', function() {
		var id_toshow = $(this).addClass('focus').siblings().removeClass('focus').end().attr('for');
		$('.withdraw-setting-panel').hide();
		if (id_toshow == 'boxCapitalWithdraw') {
			//console.log('buyer deposit withdraw');
			loadTask(1);
		}
		$('#' + id_toshow).show();
	});

	//选中当前
	$(document).on('click','.taskWrapper .inputCheckBox',function(){
		if($(this).hasClass('inputCheckBox-checked')){
			$(this).removeClass('inputCheckBox-checked').parents('.taskCell').removeClass('chosen');
			return;
		}
		$(this).addClass('inputCheckBox-checked').parents('.taskCell').addClass('chosen');
	});


	//买手本金提现操作
	$(document).on('click',"#applyDepositWithdraw",function(){

		var ids = [],
			inputs = $('.taskCell .inputCheckBox-checked');
		$.each(inputs, function(i) {
			ids.push(inputs.eq(i).attr('data-tid'));
		});
		if (ids.length == 0) {
			alert('您没有选择任务');
			return;
		}
		Tr.post('/buyer/withdraw/apply', {
			'tids': ids,
			'payPass': $("#payPass").val()
		}, function(data) {
			if (data.code == 800101 || data.code == 8001) {
				alert(data.msg);
			}
			if (data.code != 200) {
				return;
			}
			alert('确认成功');
			loadTask(1);
		});
	});

	$('.lnkChoseAll').click(function(){
		if($(this).hasClass('inputCheckBox-checked')){
			$('.taskWrapper .inputCheckBox').removeClass('inputCheckBox-checked').parents('.taskCell').removeClass('chosen');
			$('.lnkChoseAll').removeClass('inputCheckBox-checked');
			return;
		}
		$('.taskWrapper .inputCheckBox').addClass('inputCheckBox-checked').parents('.taskCell').addClass('chosen');
		$('.lnkChoseAll').addClass('inputCheckBox-checked');
	});
}


$(function() {
	CDT.userType = App.userType;

	CDT.quyuTemp = $('#quyuTemp').remove().val();
	Mustache.parse(CDT.quyuTemp);

	CDT.oldAlipayTemp = $('#oldAlipayTemp').remove().val();
	Mustache.parse(CDT.oldAlipayTemp);

	CDT.oldTenpayTemp = $('#oldTenpayTemp').remove().val();
	Mustache.parse(CDT.oldTenpayTemp);

	CDT.oldBankTemp = $('#oldBankTemp').remove().val();
	Mustache.parse(CDT.oldBankTemp);

	CDT.taskRowTemp = $('#taskRowTemp').remove().val();
	Mustache.parse(CDT.taskRowTemp);

	$.extend($.validator.messages, {
		required: Tr.error('必填'),
		minlength: Tr.error('至少{0}个字符'),
		mobile: Tr.error('格式不正确'),
		equalTo: Tr.error('两次输入卡号不相同'),
		maxlength: Tr.error('不能超过{0}个字符'),
		number: Tr.error('请输入合法的数字')
	});
	$.validator.addMethod('alipayNo', function(value) {
		return Tr.regs.alipay.test($('#txtAlipayNo').val());
	}, Tr.error('格式错误！'));

	$.validator.addMethod('tenpayNO', function(value) {
		return Tr.regs.tenpay.test($('#txtTenpayNo').val());
	}, Tr.error('格式错误！'));

	$.validator.addMethod('bankNo', function(value) {
		return Tr.regs.bank.test($('#txtBankNo').val());
	}, Tr.error('格式错误！'));
	$.validator.addMethod('price', function(value) {
		var flag = true;
		if (parseFloat($('#txtWithdrawAmount').val()) > parseFloat($('#spRefundCount').text())) {
			flag = false;
		}
		return flag;
	}, Tr.error('不能超过可提现金额！'));
	$.validator.addMethod('Recharge', function(value) {
		var flag = true;
		if (parseFloat($('#txtChargeVal').val()) < 1) {
			flag = false;
		}
		return flag;
	}, Tr.error('金额不能小于1元！'));


	initBase();

});