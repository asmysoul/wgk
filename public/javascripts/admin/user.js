CDT = {
	rowTemp: null,
	currPageNo: 1,
	pageSize: 20,
	uid: null,
	nick: null,
	qq: null,
	mobile: null,
	email:null,
	type: null,
	status: null,
	currUid: null,
	dockingMessage: null,
	adminName:App.adminName
};

//加载用户列表
function loadAccount(pageNo) {
	var startDate = ($.trim($('#regTimeStart').val()))?$.trim($('#regTimeStart').val()) + ' 00:00:00' : '',
	    endDate = ($.trim($('#regTimeEnd').val()))? $.trim($('#regTimeEnd').val()) + ' 00:00:00':'';
	if($("#jumpStatus").val()=="SELLER") {
		$("#SELLER").attr("selected", "selected");
	    $("#jumpStatus").remove();
	}
	Tr.get('/admin/user/list', {
		'vo.id': $.trim($('#uid').val()),
		'vo.nick': $.trim($('#nick').val()),
		'vo.qq': $.trim($('#qq').val()),
		'vo.mobile': $.trim($('#mobile').val()),
		'vo.email': $.trim($('#email').val()),
		'vo.adminName': $.trim($('#adminName').val()),
		'vo.type': $.trim($('#type').val()),
		'vo.status': $.trim($('#status').val()),
		'vo.vipStatus': $.trim($('#vipStatus').val()),
		'vo.registTimeStart': startDate,
		'vo.registTimeEnd':endDate,
		'vo.pageNo': pageNo,
		'vo.pageSize': CDT.pageSize
	}, function(data) {
		if (data.code != 200) return;
		if (data.results.length <= 0) {
			$('.normTable').hide();
			$('.pagin-btm').hide();
			$('#noMsg').show();
			return;
		}
		var output = Mustache.render(CDT.rowTemp, $.extend(data, {
			usertype: function() {
				return (this.type == 'SELLER') ? '商家' : '买手';
			},
			isSeller: function() {
				return (this.type == 'SELLER');
			},
			userstatus: function() {
				if (this.status == 'INACTIVE') {
					return '注册后未激活';
				}
				if (this.status == 'ACTIVE') {
					return '已激活，尚未开通会员';
				}
				if (this.status == 'VALID') {
					return '已开通会员';
				}
				if (this.status == 'INVALID') {
					return '会员已过期';
				}
				return '账号被冻结';
			},
			vipstatus: function() {
				if (this.vipStatus == 'NORMAL') {
					return '普通用户';
				}
				if (this.vipStatus == 'VIP1') {
					return 'VIP一级';
				}
				if (this.vipStatus == 'VIP2') {
					return 'VIP二级';
				}
				if (this.vipStatus == 'VIP3') {
					return 'VIP三级';
				}
				if (this.vipStatus == 'SPECIAL') {
					return '合作用户';
				}
				if (this.vipStatus == 'LOW') {
					return '降权用户';
				}
			},
			regtime: function() {
				if(!this.registTime){
					return '——';
				}else{
					return new Date(this.registTime).Format('yyyy-MM-dd hh:mm:ss');
				}
			},
			ingotStr: function(){
				return this.ingot == 0 ? 0 : $.number(this.ingot/100,2);
			},
			depositStr: function(){
				return this.deposit == 0 ? 0 : $.number(this.deposit/100,2);
			},
			pledgeStr: function(){
				return this.pledge == 0 ? 0 : $.number(this.pledge/100,2);
			},
			isInactive: function(){
				return this.status == 'INACTIVE';
			},
			isName:function(){
				return(this.name==CDT.adminName | CDT.adminName == 'admin' | this.name==0 | this.name==null);
			}
		}));
		$('.normTable').show();
		$('#noMsg').hide();
		$('.pagin-btm').show();
		$('#accountContainer').html(output);


		$('.pagination').pagination(data.totalCount, {
			items_per_page: CDT.pageSize,
			num_display_entries: 5,
			current_page: pageNo,
			num_edge_entries: 2,
			callback: loadAccountCallBack,
			callback_run: false
		});
		CDT.currPageNo = pageNo;
	});
}

function loadAccountCallBack(index, jq) {
	loadAccount(index + 1);
}


function initBase() {
	CDT.modifyMessageFormContent = $('#modifyMessageForm').html();
	CDT.dockingNameFormContent = $('#dockingNameForm').html();
	// 查询用户
	$(document).on('click', '#queryBtn', function() {
		loadAccount(1);
	});
	$('.inputDate').datePicker({
		startDate: '2016-01-01',
		endDate: '2018-01-01',
		clickInput: true,
		verticalOffset: 35
	});
    //充值弹出框
    $(document).on('click', '.Recharge', function() {
    	CDT.currUid = $(this).attr('data-uid');
    	CDT.nick = $(this).attr('data-nick');
		// 获取用户当前余额
		Tr.get('/admin/user/balance', {
			uid: CDT.currUid
		}, function(data) {
			if (data.code != 200) {
				return
			};
			var ingot = data.results.ingot;
			$('#currInogt').text($.number(ingot / 100, 2));
			var pledge = data.results.pledge;
			$('#currPledge').text($.number(pledge / 100, 2));
		});

    	$('#INGOT input').attr('checked','checked');
    	$('.recharge').attr('checked','checked');
    	if($(this).parent().parent().find('.usertype').text()=='买手'){
    		$('#PLEDGE').hide();
    	}else{
    		$('#PLEDGE').show();
    	}
    	Tr.popup('RechargeWnd');
    	$('.txtRecharge').val('');
    	$('#txtMemo').val('');
    	$('#amount-error').remove();
    	$('#txtMemo-error').remove();
    	$('#error').html('');
    });
    //校验
    $.extend($.validator.messages, {
		required: Tr.error('必填'),
		number: Tr.error('请输入合法的数字'),
		email: Tr.error('请输入合法的邮箱')
	});
    // 自定义校验充值格式
	var validator1 = $('#rechargeForm').validate({
		onkeyup: false,
		rules: {			
			'amount': {
				'number': true
			},
			'memo': {
				'required':true
			}
		},
	});
	$('#txtMemo').blur(function() {
		var memo = $.trim($('#txtMemo').val());
		if (memo.length <= 0) {
			if (!$('#txtMemo-error').html()) {
				$('#error').html(Tr.error('必填'));
			} else {
				$('#txtMemo-error').html(Tr.error('必填'));
			}
		} else {
			$('#error').html('');
		}
	});

	// 用户充值 扣款
	$(document).on('click', '#btnCharge', function() {
		var $parent = $('#Recharge').parent();
		var amount = $('.txtRecharge').val();
		if ($.trim(amount).length <= 0) {
			alert('充值金额不能为空！');
			return;
		}
		var memo = $.trim($('#txtMemo').val());
		if (memo.length <= 0) {
			if(!$('#txtMemo-error').html()){
                $('#error').html(Tr.error('必填'));
			}
			return;
		}

		if (!confirm('高危操作！会改变用户的账户金额！确定要充值？')) {
			return;
		}
		if($('input[name="fundPractice"]:checked').hasClass('recharge')){
			Tr.post('/admin/user/recharge', {
				uid: CDT.currUid,
				type: $('input[name="Recharge"]:checked').attr('data-type'),
				yuan: amount,
				memo: memo
			}, function(data) {
				if (data.code != 200) return;
				alert('充值成功！');

			});
		}
		if($('input[name="fundPractice"]:checked').hasClass('deduct')){
			Tr.post('/admin/money/cc', {
				nick: CDT.nick,
				type: $('input[name="Recharge"]:checked').attr('data-type'),
				yuan: amount,
				memo: memo
			}, function(data) {
				if (data.code != 200) return;
				alert('扣款成功！');
			});
		}
		$('#RechargeWnd').hide();
	});
	
	
	$(document).on('click', '#depositBtnCharge', function() {
		var amount = $('.txtDepositRecharge').val();
		if ($.trim(amount).length <= 0) {
			alert('充值数额不能为空！');
			return;
		}
		var memo = $.trim($('#txtDepositMemo').val());
		if (memo.length <= 0) {
			if(!$('#txtMemo-error').html()){
                $('#error').html(Tr.error('必填'));
			}
			return;
		}

		if (!confirm('高危操作！会改变用户的账户金额！确定要充值？')) {
			return;
		}
		var type="";
		if($('input[name="fundPracticef"]:checked').hasClass('depositRecharge')){
			type="recharge";
		}else if($('input[name="fundPracticef"]:checked').hasClass('depositDeduct')){
			type="deduct";
		}
		Tr.get('/admin/user/rechargeDeposit', {
			userId:CDT.currUid,
			type: type,
			amount: amount,
			memo: memo
		}, function(data) {
			if (data.code != 200){ 
				alert(data.msg);
				return;
				}
			alert('充值成功！');

		});
		
		$('#depositWnd').hide();
	});
	
	$(document).on('click', '#flowBtnCharge', function() {
		var amount = $('.txtFlowRecharge').val();
		if ($.trim(amount).length <= 0) {
			alert('充值数额不能为空！');
			return;
		}
		var memo = $.trim($('#txtFlowMemo').val());
		if (memo.length <= 0) {
			if(!$('#txtMemo-error').html()){
                $('#error').html(Tr.error('必填'));
			}
			return;
		}

		if (!confirm('高危操作！会改变用户的账户金额！确定要充值？')) {
			return;
		}
		var type="";
		if($('input[name="fundPracticef"]:checked').hasClass('flowRecharge')){
			type="recharge";
		}else if($('input[name="fundPracticef"]:checked').hasClass('flowDeduct')){
			type="deduct";
		}
		Tr.get('/admin/user/rechargeFlow', {
			userId:CDT.currUid,
			type: type,
			amount: amount,
			memo: memo
		}, function(data) {
			if (data.code != 200){ 
				alert(data.msg);
				return;
				}
			alert('充值成功！');

		});
		
		$('#flowWnd').hide();
	});
	
	//商家金币转化为押金
	$(document).on('click', '#btnIngotToPledge', function() {
		Tr.post('/admin/seller/ingotToPledge', {
			uid: CDT.currUid
		}, function(data) {
			if(data.code == 34521) alert('该操作不能对买手进行！！！');
			if(data.code == 8001) alert(data.msg);
			if (data.code != 200) return;
			alert('操作成功！');
		});
		$('#RechargeWnd').hide();
	});
	//用户清理个人信息缓存
	$(document).on('click', '#btnCleanCache',function() {
		Tr.post('/admin/user/cleanIngotCache', {
			uid: CDT.currUid
		}, function(data) {
			if(data.code !=200) return;
			alert('清理成功！');
		});
	});
	//管理员给用户做会员延期操作
	$(document).on('click', '#btnMemberDelay', function() {
		Tr.post('/admin/user/memberDelay', {
			uid: CDT.currUid,
			memberDelayDuration: $('#memberDelayDuration').val()
		}, function(data) {
			if(data.code !=200) return;
			alert('延期成功');
		});
		$('#memberDelayWnd').hide();
	});
	//信息修改弹出框
	$(document).on('click', '.modifyMessage', function() {
		$('#modifyMessageForm').html(CDT.modifymessageFormContent);
    	CDT.currUid = $(this).attr('data-uid');
    	Tr.get('/admin/user/userMsg', {
    		uid: CDT.currUid,
    	}, function(data) {
    		if (data.code != 200) return;
    		if (data.results.length <= 0) {
    			return;
    		}
    		var qq = data.results.qq;
			$('#newQQ').val(qq);
			var mobile = data.results.mobile;
			$('#newMobile').val(mobile);
			var email = data.results.email;
			$('#newEmail').val(email);
			var vipStatus = data.results.vipStatus;
			if(vipStatus == 'NORMAL') {
				$("#newVipStatus").get(0).selectedIndex=0;
			}else if(vipStatus == 'VIP1') {
				$("#newVipStatus").get(0).selectedIndex=1;
			}else if(vipStatus == 'VIP2') {
				$("#newVipStatus").get(0).selectedIndex=2;
			}else if(vipStatus == 'VIP3') {
				$("#newVipStatus").get(0).selectedIndex=3;
			}else if(vipStatus == 'SPECIAL'){
				$("#newVipStatus").get(0).selectedIndex=4;
			}else {
				$("#newVipStatus").get(0).selectedIndex=5;
			}
			var status = data.results.status;
			if(status == 'INACTIVE') {
				$("#newStatus").get(0).selectedIndex=0;
			}else if(status == 'ACTIVE') {
				$("#newStatus").get(0).selectedIndex=1;
			}else if(status == 'VALID') {
				$("#newStatus").get(0).selectedIndex=2;
			}else if(status == 'INVALID') {
				$("#newStatus").get(0).selectedIndex=3;
			}else {
				$("#newStatus").get(0).selectedIndex=4;
			}
	    	Tr.popup('modifyMessageWnd');
    	});
    });		
	//对接备注菜单弹出框
	$(document).on('click', '.dockingMessage', function() {
		$('#dockingNameForm').html(CDT.dockingNameFormContent);
    	CDT.currUid = $(this).attr('data-uid');
    	Tr.get('/admin/user/dockingMessage', {
    		uid: CDT.currUid,
    	}, function(data) {
    		if (data.code != 200) return;
    		if (data.results.length <= 0) {
    			return;
    		}
    		var name = data.results.name;
			$('#newDockingName').val(name);
			var dockingMessage = data.results.dockingMessage;
			$('#newDockingMessage').val(dockingMessage);
	    	Tr.popup('dockingNameWnd');
    	});
    });
	
	//会员延期弹出框
	$(document).on('click', '.MemberDelay', function() {
    	CDT.currUid = $(this).attr('data-uid');
    	CDT.nick = $(this).attr('data-nick');
		// 获取用户到期时间
		Tr.get('/admin/user/memberDueTime', {
			uid: CDT.currUid
		}, function(data) {
			if (data.code != 200) {
				return
			};
			var memberDueTime = data.results.memberDueTime;
			$('#memberDueTime').val(memberDueTime);
		});
    	Tr.popup('memberDelayWnd');
    });
	//规则限制窗口
	$(document).on('click', '.limit', function() {
    	CDT.currUid = $(this).attr('data-uid');
    	CDT.nick = $(this).attr('data-nick');
		// 获取用户限制设置
		Tr.get('/admin/seller/limitMessage', {
			sellerId: CDT.currUid
		}, function(data) {
			if (data.code != 200) return;
			var buyerAndSellerTime = data.results.buyerAndSellerTime;
			$('#buyerAndSellerTime').val(buyerAndSellerTime);
			var buyerAndShopTime = data.results.buyerAndShopTime;
			$('#buyerAndShopTime').val(buyerAndShopTime);
			var buyerAcountAndShopTime = data.results.buyerAcountAndShopTime;
			$('#buyerAcountAndShopTime').val(buyerAcountAndShopTime);
			var buyerAcountAndItemTime = data.results.buyerAcountAndItemTime;
			$('#buyerAcountAndItemTime').val(buyerAcountAndItemTime);
		});
    	Tr.popup('limitWnd');
    });
	//流量充值、扣除窗口
	$(document).on('click', '.flowBtn', function() {
		CDT.currUid = $(this).attr('data-uid');
    	Tr.popup('flowWnd');
    });
	
	//流量充值、扣除窗口
	$(document).on('click', '.RechargeDeposit', function() {
		CDT.currUid = $(this).attr('data-uid');
    	Tr.popup('depositWnd');
    });
	//取消对接
	$(document).on('click', '.cancelDocking', function() {
    	CDT.currUid = $(this).attr('data-uid');
    	Tr.post('/admin/user/cancelDocking', {
    		uid: CDT.currUid
    	}, function(data) {
    		if (data.code != 200) return;
    		//alert("取消对接成功！");
    		loadAccount(1);
    	});
    });			  
    //自定义校验充值格式
	var validator = $('#modifyEmailForm').validate({
		onkeyup: false,
		rules: {
			'modifyEmail': {
				'email': true
			}
		},
	});
    //信息修改
	$(document).on('click', '#btnModifyMessage', function() {
		var Email = $.trim($('#newEmail').val());
			QQ = $('#newQQ').val();
			Mobile = $('#newMobile').val();
			VipStatus = $('#newVipStatus').val();
			status = $('#newStatus').val();
			password = $.trim($('#newPassword').val()); 
			
			if (password !='' && password.length < 6) {
				alert('密码要大于6位');
			} else {
				Tr.post('/admin/user/edit', {
					uid: CDT.currUid,
					email: Email,
					qq : QQ,
					mobile : Mobile,
					vipStatus : VipStatus,
					status : status,
					password : password
				}, function(data) {
					if (data.code != 200) return;
					alert('修改成功！');
					$('#modifyMessageWnd').hide();
					loadAccount(1);
				});
			}
			
	});
	//对接备注修改
	$(document).on('click', '#btnDockingMessage', function() {
		var DockingMessage = $('#newDockingMessage').val();
		Tr.post('/admin/user/editDockingMessage', {
			dockingMessage: DockingMessage,
			uid: CDT.currUid
		}, function(data) {
			if (data.code != 200) return;
			//alert('操作成功！');
			$('#dockingNameWnd').hide();
			loadAccount(1);
		});
	});
	//修改规则限制
	$(document).on('click', "#btnLimit", function() {
		var BuyerAndSellerTime = $("#buyerAndSellerTime").val();
			BuyerAndShopTime = $("#buyerAndShopTime").val();
			BuyerAcountAndShopTime = $("#buyerAcountAndShopTime").val();
			BuyerAcountAndItemTime = $("#buyerAcountAndItemTime").val();
		Tr.post('/admin/seller/modifyLimitMessage', {
			'config.sellerId': CDT.currUid,
			'config.buyerAndSellerTime': BuyerAndSellerTime,
			'config.buyerAndShopTime': BuyerAndShopTime,
			'config.buyerAcountAndShopTime': BuyerAcountAndShopTime,
			'config.buyerAcountAndItemTime': BuyerAcountAndItemTime
		}, function(data) {
			alert('修改成功');
			$('#limitWnd').hide();
		});
	});
	//重新发送邮件
	$(document).on('click', '#accountContainer .J_resendMail', function() {
		var nick = $(this).attr('data-nick');
		if (!confirm('确定要重新发送激活邮件给用户：' + nick + '？')) {
			return;
		}
		Tr.post('/regist/resend', {
			email: $(this).attr('data-email'),
		}, function(data) {
			if (data.code == 800101) {
				alert('操作被系统拒绝！');
			}
			if (data.code != 200) return;

			alert('激活邮件已重新发送！');
		});
	});

	// 重置支付密码
	$(document).on('click', '#accountContainer .J_resetPayPass', function() {
		var $tr = $(this).parent().parent('tr');
		if (!confirm('确定要清除用户：' + $tr.attr('data-nick') + '的支付密码么？该操作不可恢复！')) {
			return;
		}
		Tr.post('/admin/user/paypass/reset', {
			uid: $tr.attr('data-uid'),
		}, function(data) {
			if (data.code == 800101) {
				alert('操作被系统拒绝！');
			}
			if (data.code != 200) return;
			alert('该用户的支付密码已清除，联系用户重新设置新密码');
		});
	});
}

$(function() {
	CDT.rowTemp = $('#rowTemp').remove().val();
	Mustache.parse(CDT.rowTemp);
	initBase();

	loadAccount(1);
});