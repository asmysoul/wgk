CDT = {
	trowTemp:null,
	currPageNo:1,
	pageSize:5
};

//加载买号
function loadAccount(pageNo) {
	Tr.get(' /admin/buyerAccount/list', {
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
		var output = Mustache.render(CDT.trowTemp, $.extend(data,{
			createtime: function(){
				return new Date(this.createTime).Format('yyyy-MM-dd hh:mm:ss');
			},
			modifytime: function(){
				return this.modifyTime == null ? '':new Date(this.modifyTime).Format('yyyy-MM-dd hh:mm:ss');
			},
			platformName: function() {
				return this.platformTitle;
			},
			platIcon: function() {
				return 'plat_' + this.platform;
			}
			,
			dateTimeStr:function(){
				return new Date(this.dateTime).Format('yyyy-MM-dd hh:mm:ss'); 
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
	$(document).on('click','.refuse',function(){
		var id = $(this).parents('tr').attr('bid');
		$('#btnRefuse').attr('bid',id);
		Tr.popup('refuseReasonWnd');
	});

	//审核通过
	$(document).on('click', '.access', function() {
		var id = $(this).parents('tr').attr('bid'),
		status = 'EXAMINED',
		authenticityToken = $("input[name='authenticityToken']").val();
		if (!confirm('审核通过不能再次修改，是否确认审核通过？')) {
			return;
		}
		Tr.post('/admin/buyerAccount/exmine', {
			'ba.id': id,
			'ba.status': status,
			authenticityToken: authenticityToken
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
		var id = $(this).attr('bid'),
			status = 'NOT_PASS',
			memo = $('#txtReason').val(),
			authenticityToken = $("input[name='authenticityToken']").val();
		Tr.post('/admin/buyerAccount/exmine', {
			'ba.id': id,
			'ba.status': status,
			'ba.memo': memo,
			authenticityToken: authenticityToken
		}, function(data) {
			if (data.code != 200) {
				return;
			}
			alert('审核成功！');
			loadAccount(1);
			$('#refuseReasonWnd').hide();
			$('#txtReason').val('');
		});
	});
}
$(function() {
	CDT.trowTemp = $('#trowTemp').remove().val();
	Mustache.parse(CDT.trowTemp);
	initBase();

	loadAccount(1);
});
