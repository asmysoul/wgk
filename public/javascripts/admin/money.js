CDT = {
	memberRecordTemp: null,
	pledgeRecordTemp: null,
	withdrawRecordTemp: null,
	currPageNo:1,
	pageSize:20,
	type:'withdraw',
	isReward: false
};
function initBase() {
	// 查询用户
	$(document).on('click', '#queryBtn', function() {
		loadMemberRecord(1);
	});
	$('.inputDate').datePicker({
		startDate: '2016-01-01',
		endDate: '2018-01-01',
		clickInput: true,
		verticalOffset: 35
	});
	$(document).on('click', '.foundTypeSwitchWrapper .foundTab', function(){
		$(this).addClass('focus').siblings().removeClass('focus');
		CDT.type= $(this).attr('type');
		CDT.isReward = $(this).attr('isReward');
		loadMemberRecord(1);
		
		if ($(this).attr("type") == "pledge") {
			$('#count1').removeAttr('style');
			$('#exportBtn').removeAttr('style');
		}else {
			$('#count1').attr('style','visibility:hidden');
			$('#exportBtn').attr('style','visibility:hidden');
		}
	});
	//默认显示第一个资金记录
	loadMemberRecord(1);
	
	//导出用户押金记录
	$("#exportBtn").click(function(){
		var count = $("#count").val();
		var userNick = $("#userNick").val(); 
		if($.trim($('#userNick').val())==""||count==""){
			return alert("请输入用户昵称且输入正确的记录条数后再试！");
		}
		var params="count="+count+"&vo.userNick="+userNick;
		location.href="/admin/exportPledge?"+params;
	});
}
//加载资金记录
function loadMemberRecord(pageNo) {
	var createTimeStart = ($.trim($('#createTimeStart').val())) ? $.trim($('#createTimeStart').val()) + ' 00:00:00' : '',
	    createTimeEnd = ($.trim($('#createTimeEnd').val())) ? $.trim($('#createTimeEnd').val()) + ' 00:00:00' : '';
	Tr.get('/admin/money/list', {
		'vo.type': CDT.type,
		'vo.pageNo': pageNo,
		'vo.userNick': $.trim($('#userNick').val()),
		'vo.taskId': $.trim($('#taskId').val()),
		'vo.createTimeStart':createTimeStart,
		'vo.createTimeEnd': createTimeEnd,
		'vo.amountStart': $.trim($('#amountStart').val()),
		'vo.amountEnd': $.trim($('#amountEnd').val()),
		'vo.sign': $('#status').val(),
		'vo.isReward': CDT.isReward,
		'vo.pageSize': CDT.pageSize
	}, function(data) {
		if (data.code != 200) return;
		var output = '';
		if (CDT.type == 'withdraw') {
			if (data.results.length <= 0) {
				output = '<div class="iconfont warnBox">&#xf00b6;</span>没有记录!';
			} else {
				output = Mustache.render(CDT.withdrawRecordTemp, $.extend(data, {
					applyTimeStr: function() {
						return new Date(this.applyTime).Format('yyyy-MM-dd hh:mm:ss');
					},
					modifyTimeStr: function() {
						return new Date(this.modifyTime).Format('yyyy-MM-dd hh:mm:ss');
					},
					endTimeStr: function() {
						return new Date(this.endTime).Format('yyyy-MM-dd hh:mm:ss');
					},
					amountStr: function() {
						return $.number(this.amount / 100, 2);
					},
					statusStr: function() {
						if (this.status == 'WAIT') {
							return '待处理';
						}
						if (this.status == 'FINISHED') {
							return '已退款';
						}
						if (this.status == 'PROCESSING') {
							return '处理中';
						}
					}
				}));
			}
		}
		if (CDT.type == 'member') {
			if (data.results.length <= 0) {
				output = '<div class="iconfont warnBox">&#xf00b6;没有记录!</div>';
			} else {
				output = Mustache.render(CDT.memberRecordTemp, $.extend(data, {
					createTimeStr: function() {
						return new Date(this.createTime).Format('yyyy-MM-dd hh:mm:ss');
					},
					endTimeStr: function() {
						return new Date(this.endTime).Format('yyyy-MM-dd hh:mm:ss');
					},
					amountStr: function() {
						return $.number(this.amount / 100, 2);
					},
					ignotStr: function() {
						return $.number(this.ignot / 100, 2);
					}
				}));
			}
		}
		if (CDT.type == 'flow') {
			if (data.results.length <= 0) {
				output = '<div class="iconfont warnBox">&#xf00b6;没有记录!</div>';
			} else {
				output = Mustache.render(CDT.flowRecordTemp, $.extend(data, {
					createTimeStr: function() {
						return new Date(this.createTime).Format('yyyy-MM-dd hh:mm:ss');
					},
					plusAmountStr: function() {
						if (this.sign == 'PLUS')
							return this.amount + '个';
					},
					minusAmountStr: function() {
						if (this.sign == 'MINUS')
							return this.amount + '个';
					},
					balanceStr: function() {
						return this.balance + "个";
					}
				}));
			}
		}
		if (CDT.type == 'pledge' || CDT.type == 'ingot' || CDT.type == 'premium') {
			if (data.results.length <= 0) {
				output = '<div class="iconfont warnBox">&#xf00b6;没有记录!</div>';
			} else {
				output = Mustache.render(CDT.pledgeRecordTemp, $.extend(data, {
					createTimeStr: function() {
						return new Date(this.createTime).Format('yyyy-MM-dd hh:mm:ss');
					},
					plusAmountStr: function() {
						if (this.sign == 'PLUS')
							return $.number(this.amount / 100, 2) + '元';
					},
					minusAmountStr: function() {
						if (this.sign == 'MINUS')
							return $.number(this.amount / 100, 2) + '元';
					},
					balanceStr: function() {
						return $.number(this.balance / 100, 2);
					}
				}));
			}
		}
		if(CDT.type == 'deposit') {
			if(data.results.length <= 0) {
				output = '<div class="iconfont warnBox">&#xf00b6;没有记录!</div>';
			}else {
				output = Mustache.render(CDT.depositRecoderTemp, $.extend(data, {
					createTimeStr: function() {
						return new Date(this.createTime).Format('yyyy-MM-dd hh:mm:ss');
					},
					buyerTaskId: function() {
						return this.taskId;
					},
					plusAmountStr: function() {
						if (this.sign == 'PLUS')
							return $.number(this.amount / 100, 2) + '元';
					},
					minusAmountStr: function() {
						if (this.sign == 'MINUS')
							return $.number(this.amount / 100, 2) + '元';
					},
					balanceStr: function() {
						return $.number(this.balance / 100, 2);
					}
				}));
			}
		}
		$('#recordContainer').html(output);
		$('.pagination').pagination(data.totalCount, {
			items_per_page: CDT.pageSize,
			num_display_entries: 5,
			current_page: pageNo,
			num_edge_entries: 2,
			callback: loadMemberRecordCallBack,
			callback_run: false
		});
		CDT.currPageNo = pageNo;
	});
}

function loadMemberRecordCallBack(index, jq) {
	loadMemberRecord(index + 1);
}
$(function(){
	CDT.pledgeRecordTemp = $('#pledgeRecordTemp').remove().val();
	Mustache.parse(CDT.pledgeRecordTemp);

	CDT.memberRecordTemp = $('#memberRecordTemp').remove().val();
	Mustache.parse(CDT.memberRecordTemp);
	CDT.withdrawRecordTemp = $('#withdrawRecordTemp').remove().val();
	Mustache.parse(CDT.withdrawRecordTemp);
	CDT.depositRecoderTemp = $('#depositRecordTemp').remove().val();
	Mustache.parse(CDT.depositRecordTemp);
	CDT.flowRecordTemp = $('#flowRecordTemp').remove().val();
	Mustache.parse(CDT.flowRecordTemp);
	
	initBase();
});
