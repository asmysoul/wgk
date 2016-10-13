CDT = {
	rowTemp: null,
	currPageNo: 1,
	pageSize: 20
};

//加载列表
function loadItems(pageNo) {
	Tr.get('/admin/invite/list', {
		'vo.inviteNick': $.trim($('#inviteNick').val()),
		'vo.nick': $.trim($('#nick').val()),
		'vo.type': $.trim($('#type').val()),
		'vo.memberOpenTimeStart': $.trim($('#memberOpenTimeStart').val()),
		'vo.memberOpenTimeEnd': $.trim($('#memberOpenTimeEnd').val()),
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
			memberTime: function() {
				if (!this.memberOpenTime) {
					return '——';
				}
				return new Date(this.memberOpenTime).Format('yyyy-MM-dd hh:mm:ss');
			},
			regtime: function(){
				return new Date(this.createTime).Format('yyyy-MM-dd hh:mm:ss');
			},
			rewardIngotStr: function(){
				if(!this.rewardIngot){
					return '——'
				}
				return $.number(this.rewardIngot/100, 2);
			}
		}));
		$('#noMsg').hide();
		$('.normTable').show();
		$('.pagin-btm').show();
		$('#accountContainer').html(output);

		$('.pagination').pagination(data.totalCount, {
			items_per_page: CDT.pageSize,
			num_display_entries: 5,
			current_page: pageNo,
			num_edge_entries: 2,
			callback: loadItemsCallBack,
			callback_run: false
		});
		CDT.currPageNo = pageNo;
	});
	
}

function loadItemsCallBack(index, jq) {
	loadItems(index + 1);
}


function initBase() {
	// 查询用户
	$(document).on('click', '#queryBtn', function() {
		loadItems(1);
	});
	$("#memberOpenTimeStart").datetimepicker({format:'Y-m-d H:i:s',formatDate:'Y-m-d'});
	$("#memberOpenTimeEnd").datetimepicker({format:'Y-m-d H:i:s',formatDate:'Y-m-d'});
}

$(function() {
	CDT.rowTemp = $('#rowTemp').remove().val();
	Mustache.parse(CDT.rowTemp);
	initBase();

	loadItems(1);
});