CDT = {
	taskCellTemp: null,
	currPageNo: 1,
	pageSize: 20,
	authenticityToken: ''
};

//加载任务
function loadTask(pageNo) {
	var platform = $('.platform').val(),
		shopId = $('#shop').val(),
		orderId = $.trim($('#orderId').val()),
		buyerAccountNick = $('#buyerAccountId').val();
	Tr.get('/seller/task/listWaitSendGoods', {
		'vo.platform': platform,
		'vo.shopId': shopId,
		'vo.orderId': orderId,
		'vo.buyerAccountNick': buyerAccountNick,
		'vo.pageNo': pageNo,
		'vo.pageSize': CDT.pageSize
	}, function(data) {
		if (data.code != 200) {
			return;
		}
        if (data.results.length <= 0) {
			$('#taskContainer').addClass('warnBox').html('<span class="iconfont">&#xf00b6;</span>没有任务!');
			return;
		}
		var output = Mustache.render(CDT.taskCellTemp, $.extend(data,{
			platIcon: function() {
				return 'plat_' + this.platform;
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

//初始化店铺信息
function initShop(platform) {
	$('#shop').find('option').eq(0).nextAll('option').remove();
	Tr.get('/user/shop/' + platform, {
		platform: platform
	}, function(data) {
		if (data.code != 200) {
			return;
		}
		var output = '';
		$.each(data.results, function(i, n) {
			output = output + '<option value="' + n.id + '">' + n.nick + '</option>';
		});
		$('#shop').append(output);
	});
}

function initBase() {
	$(document).on('change', '.platform', function() {
		var $me = $(this),
		platform = $me.val();
		initShop(platform);
	});

	//单个确认发货
	$(document).on('click', '.btnConfirmDeli', function() {
		var $me = $(this),
			tid = $me.parents('.taskCell').eq(0).find('.inputCheckBox').attr('tid');
		Tr.post('/seller/task/sendGood', {
			'buyerTask.id': tid,
			authenticityToken: CDT.authenticityToken
		}, function(data) {
			if (data.code != 200) {
				return;
			}
			alert('确认成功');
			loadTask(1);
		});
	});
	//批量确认发货
	$('#btnConfirmAll').click(function() {
		var ids = [],
			inputs = $('#taskContainer .inputCheckBox-checked');
		$.each(inputs, function(i) {
			ids.push(inputs.eq(i).attr('tid'));
		});
		if (ids.length == 0) {
			alert('您没有选择任务');
			return;
		}

		Tr.post('/seller/task/sendGood', {
			'ids': ids,
			authenticityToken: CDT.authenticityToken
		}, function(data) {
			if (data.code != 200) {
				return;
			}
			alert('确认成功');
			loadTask(1);
		});
	});

	var rules = {
		rules: {
		},
		success: function(label, element) {
		}

	};
	var validator = $('#queryForm').validate(rules);
	$('#queryTask').click(function() {
		if(validator.form()){
			loadTask(1);
		}
	});
}

$(function() {
	$(document).on('click','.taskWrapper .inputCheckBox',function(){
		if($(this).hasClass('inputCheckBox-checked')){
			$(this).removeClass('inputCheckBox-checked').parents('.taskCell').removeClass('chosen');
			return;
		}
		$(this).addClass('inputCheckBox-checked').parents('.taskCell').addClass('chosen');
	});

	$('.lnkChoseAll').click(function(){
		if($(this).hasClass('inputCheckBox-checked')){
			$('.taskWrapper .inputCheckBox').removeClass('inputCheckBox-checked').parents('.taskCell').removeClass('chosen');
			$(this).removeClass('inputCheckBox-checked');
			return;
		}
		$('.taskWrapper .inputCheckBox').addClass('inputCheckBox-checked').parents('.taskCell').addClass('chosen');
		$(this).addClass('inputCheckBox-checked');
	});

	CDT.authenticityToken = $('input[name="authenticityToken"]').val();
	CDT.taskCellTemp = $('#taskCellTemp').remove().val();
	Mustache.parse(CDT.taskCellTemp);

	initBase();
	initShop($('.platform').val());
	loadTask(1);
	$.extend($.validator.messages, {
		number: Tr.errorLeft('格式不正确')
	});
});

