CDT={
	orderTakenContainerTemp:null,
	orderTemp: null,
	currPageNo: 1,
	pageSize: 20,
	countType: 'userCount'
};

//加载金币奖励统计
function loadReward(pageNo){
	Tr.get('/admin/listRewardCount', {
		'vo.countType': CDT.countType,
		'vo.pageNo': pageNo,
		'vo.pageSize': CDT.pageSize
	}, function(data) {
		if(data.code != 200) return;
		if (data.results.length <= 0) {
			$('#noMsg').show();
			$('.pagin-btm').hide();
			return;
		}
		var output = Mustache.render(CDT.orderTemp, $.extend(data, {
			rewardCountStr:function(){
				return this.rewardCount==0 ? 0: this.rewardCount/100 + "元";
			}
		}));
		$('#noMsg').hide();
		$('.pagin-btm').show();
		$('#recordContainer').html(output);
		$('.pagination').pagination(data.totalCount, {
			items_per_page: CDT.pageSize,
			num_display_entries: 5,
			current_page: pageNo,
			num_edge_entries: 2,
			callback: loadRewardCallBack,
			callback_run: false
		});
		CDT.currPageNo = pageNo;
	});
}

function initBase() {
	
	//点击tab框
	$(document).on('click', '.foundTypeSwitchWrapper .foundTab', function(){
		$(this).addClass('focus').siblings().removeClass('focus');
		CDT.countType= $(this).attr('type');
		loadReward(1);
		
	});
	loadReward(1);
}
function loadRewardCallBack(index, jq) {
	loadReward(index + 1);
}

$(function() {
	CDT.orderTemp = $('#orderTemp').remove().val();
	Mustache.parse(CDT.orderTemp);
	
	initBase();
});
