CDT = {
	rowTemp: null,
	currPageNo: 1,
	pageSize: 20,
	name: null,
	qq: null,
	mobile: null,
	email:null,
	type: null,
	status: null,
	currUid: null,
};

//加载管理员用户列表
function loadAccount(pageNo) {
	Tr.get('/admin/adminAccount/adminList', {
		'vo.id': $.trim($('#uid').val()),
		'vo.name': $.trim($('#name').val()),
		'vo.type': $.trim($('#type').val()),
		'vo.qq': $.trim($('#qq').val()),
		'vo.email': $.trim($('#email').val()),
		'vo.mobile': $.trim($('#mobile').val()),
		'vo.status': $.trim($('#status').val()),
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
			admintype: function() {
				if(this.type == "SUPERADMIN") {
					return '超级管理员';
				}
				if(this.type == "ADMIN") {
					return '管理员';
				}
				if(this.type == "FINANCE") {
					return '财务';
				}
				if(this.type == "SERVICE") {
					return '客服';
				}
			},
			adminStatus: function() {
				if (this.status == 'VALID') {
					return '可登录账号';
				}
				if (this.status == 'INVALID') {
					return '不可登录账号';
				}
				return '账号被冻结';
			},

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
	CDT.modifyMsgFormContent = $('#modifyMsgWnd').html();
    //修改资料弹出窗
	$(document).on('click', '#queryBtn', function() {
		loadAccount(1);
	});
    $(document).on('click', '.modifyMsg', function() {
    	CDT.currUid = $(this).attr('data-uid');
    	CDT.nick = $(this).attr('data-nick');
    	Tr.get('/admin/adminAccount/adminMsg', {
    		uid: CDT.currUid
    	}, function(data) {
    		if (data.code != 200) {
				return
			};
    		//表单数据回填
    		var id = data.results.id;
			$('#oldId').html(id);
			var name = data.results.name;
			$('#oldName').val(name);
			var type = data.results.type;
			if(type == 'SUPERADMIN') {
				$("#oldType").get(0).selectedIndex=0;
			}else if(type == 'ADMIN') {
				$("#oldType").get(0).selectedIndex=1;
			}else if(type == 'FINANCE') {
				$("#oldType").get(0).selectedIndex=2;
			}else {
				$("#oldType").get(0).selectedIndex=3;
			}
			var qq = data.results.qq;
			$('#oldQq').val(qq);
			var email = data.results.email;
			$('#oldEmail').val(email);
			var mobile = data.results.mobile;
			$('#oldMobile').val(mobile);
			var status = data.results.status;
			if(status == 'VALID') {
				$("#oldStatus").get(0).selectedIndex=0;
			}else {
				$("#oldStatus").get(0).selectedIndex=1;
			}
			var message = data.results.message;
			$('#oldMsg').val(message);
    	});    		
    	Tr.popup('modifyMsgWnd');
    	$('.txtRecharge').val('');
    	$('#txtMemo').val('');
    	$('#amount-error').remove();
    	$('#txtMemo-error').remove();
    	$('#error').html('');
    });
    //修改管理员信息
    $(document).on('click', '#btnModifyMsg', function() {
    	Tr.post('/admin/adminAccount/adminModify', {
    		'admin.id' : $('#oldId').html(),
    		'admin.name' : $('#oldName').val(),
    		'admin.password' : $('#oldPass').val(),
    		'admin.type' : $("#oldType").val(),
    		'admin.qq' : $('#oldQq').val(),
    		'admin.email' : $('#oldEmail').val(),
    		'admin.mobile' : $('#oldMobile').val(),
    		'admin.status' : $("#oldStatus").val(),
    		'admin.message' : $('#oldMsg').val()
    	}, function(data) {
    		if (data.code != 200) {
				return
			};
			alert("修改成功");
			$('#oldPass').val(""),
			$('#modifyMsgWnd').hide();
			loadAccount(1);
    	});
    });
    //增加管理员账号弹出窗 
    $(document).on('click', '#addAdminBtn', function() {
    	Tr.popup('addAdminWnd');
    });
    //增加管理员账号
    $(document).on('click', '#btnAddAccount', function() {
    	Tr.get('/admin/adminAccount/adminInsert', {
    		'admin.name' : $('#newName').val(),
    		'admin.password':$('#newPass').val(),
    		'admin.type' : $("#newType").val(),
    		'admin.qq' : $('#newQq').val(),
    		'admin.email' : $('#newEmail').val(),
    		'admin.mobile' : $('#newMobile').val(),
    		'admin.status' : $("#newStatus").val(),
    		'admin.message' : $('#newMsg').val()
    	}, function(data) {
    		if (data.code != 200) {
				return
			};
			alert("增加管理员成功");
			$('#newName').val("");
			$('#newPass').val("");
			$("#newType").val(1);
			$('#newQq').val("");
			$('#newEmail').val("");
			$('#newMobile').val("");
			$("#newStatus").val(1);
			$('#newMsg').val("");
			$('#addAdminWnd').hide();
			loadAccount(1);
    	});
    });
    //校验
    $.extend($.validator.messages, {
		required: Tr.error('必填'),
		name: Tr.error('请输入合法的姓名'),
		password: Tr.error('请输入合法的密码'),
		type: Tr.error('请选择一个类型'),
		qq: Tr.error('请输入合法的qq'),
		email: Tr.error('请输入合法的邮箱'),
		mobile: Tr.error('请输入合法的电话'),
		message: Tr.error('请输入合法的备注')
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

}

$(function() {
	CDT.rowTemp = $('#rowTemp').remove().val();
	Mustache.parse(CDT.rowTemp);
	initBase();

	loadAccount(1);
});