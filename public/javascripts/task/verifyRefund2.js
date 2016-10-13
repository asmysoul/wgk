CDT = {
	taskCellTemp: null,
	currPageNo: 1,
	pageSize: 20,
	authenticityToken: ''
};
//加载任务
function loadTask(pageNo) {
	var platform = $('#platform').val(),
		orderId = $('#orderId').val(),
		nick = $('#buyerAccountNick').val(),
		status = 'REFUNDING';
	Tr.get('/buyer/task/list2', {
		'vo.platform': platform,
		'vo.orderId': orderId,
		'vo.buyerAccountNick': nick,
		'vo.status': status,
		'vo.pageNo': pageNo,
		'vo.pageSize': CDT.pageSize
	}, function(data) {
		if (data.code != 200) {
			return;
		}
		//todo
		if (data.results.length <= 0) {
			$('#taskContainer').addClass('warnBox').html('<span class="iconfont">&#xf00b6;</span>没有推广!');
			return;
		}
		var output = Mustache.render(CDT.taskCellTemp, $.extend(data, {
			platIcon: function() {
				return 'plat_' + this.platform;
			},
			paidFeeFor: function() {
				return $.number(this.paidFee / 100, 2);
			},
			rewardIngotFor: function() {
				return $.number(this.rewardIngot / 100, 2);
			},
			transNoStr:function() {
				return this.transNo==null ? "商家退款延时，平台已将钱转到平台账户内，请注意查收":this.transNo;
			}
		}));
		$('#taskContainer').removeClass('warnBox').html(output);
		loadButtomData();
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

//列表底端统计信息渲染
function loadButtomData() {
	var inputs = $('#taskContainer .inputCheckBox-checked'),
		paidFeeCount = 0,
		rewardIngotCount = 0,
		count = 0;
	$.each(inputs, function(i) {
		paidFeeCount = paidFeeCount + parseInt(inputs.eq(i).attr('paidFee'));
		rewardIngotCount = rewardIngotCount + parseInt(inputs.eq(i).attr('rewardIngot'));
		count++;
	});

	$('#taskCount').text(count);
	$('#paidFeeCount').text($.number(paidFeeCount / 100, 2));
	$('#rewardIngotCount').text($.number(rewardIngotCount / 100, 2));
}

function initBase() {
	$('#platform').val(App.currplatform);
	//查询
	$('#queryTask').click(function() {
		loadTask(1);
	});

	//汇总数据 供展示
	$(document).on('click', '.inputCheckBox', function() {
		loadButtomData();
	});

	//单个核实
	$(document).on('click', '.btnConfirmRefund', function() {
		var $select = $(this).parents('.taskCell').find('.inputCheckBox'),
		id = $select.attr('tid'),
		taskId = $select.attr('pid');
		Tr.post('/buyer/task/confirmRefund2', {
			'bt.id': id,
			'bt.status': 'FINISHED',
			'bt.taskId': taskId,
			authenticityToken: CDT.authenticityToken
		}, function(data) {
			if (data.code != 200) {
				return;
			}
			alert('核实成功！');
			loadTask(1);
		});
	});

	//批量核实
	$('#btnConfirmAll').click(function() {
		var ids = [],
			inputs = $('#taskContainer .inputCheckBox-checked');
		$.each(inputs, function(i) {
			ids.push(inputs.eq(i).attr('tid'));
		});
		if (ids.length <= 0) {
			alert('请选择要核实的任务');
			return;
		}
		Tr.post('/buyer/task/batchCheckRefund', {
			'bt.ids': ids,
			authenticityToken: CDT.authenticityToken
		}, function(data) {
			if (data.code != 200) {
				return;
			}
			alert('批量核实成功');
			loadTask(1);
		});
	});
}

//渲染底部退款账号
function loadAccount() {
	Tr.get('/user/money/fundAccount/get', {
		'account.type': 'TENPAY'
	}, function(data) {
		if (data.code != 200) return;
		$('#account').text(data.results.no);
	});
}

$(function() {
	$(document).on('click', '.taskWrapper .inputCheckBox', function() {
		if ($(this).hasClass('inputCheckBox-checked')) {
			$(this).removeClass('inputCheckBox-checked').parents('.taskCell').removeClass('chosen');
			return;
		}
		$(this).addClass('inputCheckBox-checked').parents('.taskCell').addClass('chosen');
	});

	$('.lnkChoseAll').click(function() {
		if ($(this).hasClass('inputCheckBox-checked')) {
			$('.taskWrapper .inputCheckBox').removeClass('inputCheckBox-checked').parents('.taskCell').removeClass('chosen');
			$(this).removeClass('inputCheckBox-checked');
			return;
		}
		$('.taskWrapper .inputCheckBox').addClass('inputCheckBox-checked').parents('.taskCell').addClass('chosen');
		$(this).addClass('inputCheckBox-checked');
	});

	CDT.taskCellTemp = $('#taskCellTemp').remove().val();
	Mustache.parse(CDT.taskCellTemp);
	CDT.authenticityToken = $('input[name="authenticityToken"]').val();
	initBase();
	loadAccount();

	loadTask(1);
});