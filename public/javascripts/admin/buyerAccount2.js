CDT = {
	trowTemp:null,
    quyuTemp: null,
    currentId: '',
	currPageNo:1,
	pageSize:5
};

//加载买号
function loadAccount(pageNo) {
	var userNick = $.trim($('#userNick').val()),
		accountNick = $.trim($('#nick').val()),
		status = $('#status').val();
	Tr.get('/admin/listAllAccount2', {
		'vo.userNick': userNick,
		'vo.accountNick': accountNick,
		'vo.status': status,
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
			accountStatus: function() {
				if (this.status == 'WAIT_EXAMINE') {
					return '待审核';
				}
				if (this.status == 'EXAMINING') {
					return '审核中';
				}
				if (this.status == 'NOT_PASS') {
					return '审核未通过';
				}
				if (this.status == 'EXAMINED') {
					return '审核通过';
				}
			},
			isPass: function() {
				return this.status == 'EXAMINED';
			},
			notPass:function() {
				return this.status != 'EXAMINED';
			},
			createtime: function() {
				return new Date(this.createTime).Format('yyyy-MM-dd hh:mm:ss');
			},
			modifytime: function() {
				return this.modifyTime == null ? '' : new Date(this.modifyTime).Format('yyyy-MM-dd hh:mm:ss');
			},
			platformName: function() {
				return this.platformTitle;
			},
			platIcon: function() {
				return 'plat_' + this.platform;
			},
			dateTimeStr:function(){
				return new Date(this.dateTime).Format('yyyy-MM-dd hh:mm:ss'); 
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
//初始化地址数据
function initQuyu(state, city, region) {
	var $me = $('.province'),
		$city = $me.siblings('.city').eq(0),
		$region = $me.siblings('.region').eq(0);
		var id = $me.find('option:selected').attr('rid');
		$city.find('option').eq(0).nextAll('option').remove();
		$region.find('option').eq(0).nextAll('option').remove();
	Tr.get('/admin/region', {
		id: 1
	}, function(data) {
		if (data.code != 200) return;
		var output = Mustache.render(CDT.quyuTemp, data);
		$('#first').append(output);
		$('#first').val(state);

		var id = $('#first').find('option:selected').attr('rid');
		Tr.get('/admin/region', {
			id: id
		}, function(data) {
			if (data.code != 200) return;
			var output = Mustache.render(CDT.quyuTemp, data);
			$('#second').append(output);
			$('#second').val(city);
			var cid = $('#second').find('option:selected').attr('rid');
			Tr.get('/admin/region', {
				id: cid
			}, function(data) {
				if (data.code != 200) return;
				var output = Mustache.render(CDT.quyuTemp, data);
				$('#thrid').append(output);
				$('#thrid').val(region);
			});

		});
	});	
}








function modifyAccount(){
	var params = {
		'vo.nick': $.trim($('#txtNick').val()),
		'vo.consignee':$.trim($('#txtConsignee').val()),
		'vo.state': $('#first').val(),
		'vo.city': $('#second').val(),
		'vo.region': $('#thrid').val(),
		'vo.address': $.trim($('#txtAddress').val()),
		'vo.mobile': $.trim($('#txtMobile').val()),
		'vo.id': CDT.currentId
	};
	CDT.NeedDisbale = true;
	Tr.post('/admin/buyerAccount/modify2', params, function(data) {
		if (data.code != 200) return;
		$('#bindAccountWnd').hide();
		$('#bindAccountWnd input').val('');
		alert('修改成功！');
		$('#modifyAccountWnd').hide();
		loadAccount(1);
	});
}




function validateOptionsSet(){
	// 自定义提示消息
    $.extend($.validator.messages, {
	    required: Tr.error('必填'),
    });
	// 校验规则
	var options = {
		onkeyup: false,
		rules: {
			thrid:{
				required: {
					depends: function(element) {
						if($('#modifyAccountWnd').is(':visible')){
							return $('#thrid option').length!=1;
						}												
					}			
				}
			}
		},
		// 不显示成功的小对号
		success: function (label, element) {
		}
	};
	return options;
}



function initBase() {
	$(document).on('click', '#queryBtn', function() {
		loadAccount(1);
	});

	$(document).on('click', '.J_notpass', function() {
		var id = $(this).parents('tr').attr('data-bid');
		$('#btnRefuse').attr('data-bid', id);
		Tr.popup('refuseReasonWnd');
	});
	$(document).on('click','.modifyBtn',function(){
		var id = $(this).attr('data-bid');
		CDT.currentId = id;
		$('#spUserNick').text($(this).parents('tr').find('.userNick').text());
		$('#txtNick').val($(this).parents('tr').find('.nick').text());
		$('#txtConsignee').val($(this).parents('tr').find('.consignee').text());
		initQuyu($(this).parents('tr').find('.state').text(), $(this).parents('tr').find('.city').text(), $(this).parents('tr').find('.region').text());
		$('#txtAddress').val($(this).parents('tr').find('.address').text());
		$('#txtMobile').val($(this).parents('tr').find('.mobile').text());
		
		Tr.popup('modifyAccountWnd');
	});
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
		Tr.get('/admin/region', {
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
		Tr.get('/admin/region', {
			id: id
		}, function(data) {
			if (data.code != 200) return;
			var output = Mustache.render(CDT.quyuTemp, data);
			$region.append(output);
		});
	});
	(function(op){
		var validator = $('#modifyForm').validate(op);
		$('#btnAccountSave').click(function(){
			if(!confirm('确认要提交么？')){
				return;
			}
			if(validator.form()){
				modifyAccount();
			}
		});
	})(validateOptionsSet());
	
	
	
	
	
	
	$(document).on('click', '.J_pass', function() {
		var id = $(this).attr('data-bid');
		if(!confirm('确认要提交么？')){
			return;
		}
		Tr.post('/admin/buyerAccount/exmine2', {
			'ba.id': id,
			'ba.status': 'EXAMINED'
		}, function(data) {
			if (data.code != 200) {
				return;
			}
			alert('审核成功！');
			loadAccount(1);
		});
	});

	
	
	
	
	
	
	//审核不通过
	$('#btnRefuse').click(function() {
		var id = $(this).attr('data-bid')
			memo = $('#txtRefuseReason').val();
		if($.trim(memo).length<=0){
			alert('必须填写审核不通过的理由！');
			return;
		}
		if(!confirm('确认要提交么？')){
			return;
		}
		Tr.post('/admin/buyerAccount/exmine2', {
			'ba.id': id,
			'ba.status': 'NOT_PASS',
			'ba.memo': memo
		}, function(data) {
			if (data.code != 200) {
				return;
			}
			alert('审核成功！');
			loadAccount(1);
			$('#refuseReasonWnd').hide();
			$('#txtRefuseReason').val('');
		});
	});
}

$(function() {
	CDT.trowTemp = $('#trowTemp').remove().val();
	Mustache.parse(CDT.trowTemp);
	CDT.quyuTemp = $('#quyuTemp').remove().val();
	Mustache.parse(CDT.quyuTemp);
	initBase();
    validateOptionsSet();
	loadAccount(1);
});
