CDT = {
	quyuTemp: null,
	trowTemp: null,
	currPageNo: 1,
	pageSize: 20
};

//加载买号
function loadAccount(pageNo) {
	var userNick = $.trim($('#userNick').val()),
		name = $.trim($('#name').val()),
		type = $('#type').val();
		no = $('#no').val();
	Tr.get('/admin/fund/account/list', {
		'vo.no': no,
		'vo.nick': userNick,
		'vo.name': name,
		'vo.type': type,
		'vo.pageNo': pageNo,
		'vo.pageSize': CDT.pageSize
	}, function(data) {
		if (data.code != 200) {
			return;
		}
		if (data.results.length <= 0) {
			$('.normTable').hide();
			$('#noMsg').show();
			$('.pagin-btm').hide();
			return;
		}
		var output = Mustache.render(CDT.trowTemp, $.extend(data, {
			createtime: function() {
				return new Date(this.createTime).Format('yyyy-MM-dd hh:mm:ss');
			},
			isTenpay: function() {
				return this.type == 'TENPAY';
			},
			isAlipay: function() {
				return this.type == 'ALIPAY';
			},
			isBank: function(){
				return this.type!='TENPAY' && this.type !='ALIPAY'
			}
		}));
		$('.normTable').show();
		$('#noMsg').hide();
		$('#accountContainer').html(output);
		$('.pagin-btm').show();
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

function bindBankAccount(bid,uid){
	var name = $('#txtBankName').val(),
	no = $('#txtBankNo').val(),
	openingBank = $('#txtOpeningBank').val(),
	address = $('#address').val(),
	type = $('#txtBankType').val();
	Tr.post('/admin/fund/account/save', {
		'account.id': bid,
		'account.no': no,
		'account.name': name,
		'account.userId': uid,
		'account.type': type,
		'account.openingBank': openingBank,
		'account.address': address
	}, function(data) {
		if (data.code == 800101) {
			alert(data.msg);
		}
		if (data.code != 200) {
			return;
		}
		alert('修改成功!');
		loadAccount(1);
	});
}

function bindAccount(bid,name,no,type){
	Tr.post('/admin/fund/account/save', {
		'account.id': bid,
        'account.no': no,
        'account.name': name,
        'account.type': type
		}, function(data) {
			if (data.code != 200) {
				return;
			}
			alert('修改成功！');
			loadAccount(1);
			;
		});
}

function initBase() {
	$(document).on('click', '#queryBtn', function() {
		loadAccount(1);
	});

	$(document).on('click', '.J_edit', function() {
		var id = $(this).parents('tr').attr('data-bid');
		$('#btnSave').attr('data-bid', id);
		$('#btnSave').attr('data-uid', $(this).attr('data-uid'));
		$('#oldAccount').text($(this).attr('data-no'));
		$('#oldName').text($(this).attr('data-name'));
		Tr.popup('editWnd');
	});

	$(document).on('click','.J_bankEdit',function(){
		var id = $(this).parents('tr').attr('data-bid'),
		    $me = $(this).parents('tr');
		$('#btnBankSave').attr('data-bid', id);
		$('#btnBankSave').attr('data-uid', $(this).attr('data-uid'));
		$('#txtBankName').val($me.find('.name').text());
		$('#txtBankType').val($me.find('.bankType').attr('type'));
		$('#txtBankNo').val($me.find('.no').text());
		$('#txtOpeningBank').val($me.find('.openingBank').text());
		$('#oldAddress').text($me.find('.oldAddress').text());
		var $me = $('.province'),
		$city = $me.siblings('.province').eq(0),
		$region = $me.siblings('.city').eq(0);
		$city.find('option').eq(0).nextAll('option').remove();
		$region.find('option').eq(0).nextAll('option').remove();
		$('#first').val($city.find('option').eq(0).text());
		Tr.popup('bankEditWnd');
		Tr.get('/admin/region', {
			id: 1
		}, function(data) {
			if (data.code != 200) return;
			var output = Mustache.render(CDT.quyuTemp, data);
			$('#first').append(output);
		});
	});
	//选择区域 绑定银行卡 地址栏
	$(document).on('change', '#first', function() {
		var $me = $(this);
		var id = $me.find('option:selected').attr('rid');
		$('#address').val('');
		if (!$me.find('option:selected').val()) {
			return;
		}
		Tr.get('/admin/region', {
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
			var add = $.trim($('#first').val()) +","+ $.trim($('#second').val());
			$('#address').val(add);
		} else {
			$('#address').val('');
		}
	});

    $(document).on('click', '.J_alipayEdit', function() {
		var id = $(this).parents('tr').attr('data-bid');
		$('#btnAlipaySave').attr('data-bid', id);
		$('#btnAlipaySave').attr('data-uid', $(this).attr('data-uid'));
		$('#oldAlipayAccount').text($(this).attr('data-no'));
		$('#oldAlipayName').text($(this).attr('data-name'));
		Tr.popup('alipayEditWnd');
	});

	$('#btnBankSave').click(function(){
		var bid=$(this).attr('data-bid'),
        uid = $(this).attr('data-uid');
		bindBankAccount(bid,uid);
		$('#bankEditWnd').hide();
	});




	//审核不通过
	$('#btnSave').click(function() {
		var bid = $(this).attr('data-bid'),
		    no = $('#newAccount').val(),
		    name = $('#newName').val(),
		    type = 'TENPAY';
		if ($.trim(no).length <= 0) {
			alert('请输入新的财付通账号！');
			return;
		}
		if (!confirm('确认要提交么？')) {
			return;
		}
		bindAccount(bid,name,no,type);
		$('#editWnd').hide();
		$('#oldAccount,#newAccount').val('');
	});
	$('#btnAlipaySave').click(function(){
        var bid = $(this).attr('data-bid'),
		    no = $('#newAlipayAccount').val(),
		    name = $('#newAlipayName').val(),
		    type = 'ALIPAY';
		if ($.trim(no).length <= 0) {
			alert('请输入新的支付宝账号！');
			return;
		}
		if (!confirm('确认要提交么？')) {
			return;
		}
		bindAccount(bid,name,no,type);
		$('#alipayEditWnd').hide();
		$('#oldAlipayAccount,#newAlipayAccount').val('');
	});
}

$(function() {
	CDT.trowTemp = $('#trowTemp').remove().val();
	Mustache.parse(CDT.trowTemp);
	CDT.quyuTemp = $('#quyuTemp').remove().val();
	Mustache.parse(CDT.quyuTemp);

	initBase();

	loadAccount(1);
});