CDT = {
	platform:App.platform,
	taskType:App.taskType
};

function switchTaskStatusInfo(){
	var $last = $('.stepFinish').last().parents('.stepWaiting').next('.stepWaiting');
	var basic = $last.find('.stepNum').text();
	$last.prepend('<div class="stepOn" stepbasic="' + basic + '">' + basic + '</div>');
	$last.find('.stepNum').remove();

	var step = $('.stepOn').attr('stepbasic');
	// console.log('current step is: ' + step);
	if (step <= 4) {
		$('[step="1"]').show();
		return;
	}

	$('.state').hide();
	var $currStep = $('[step="' + step + '"]');
	$currStep.show();
}
//聚划算和其他平台步骤
function switchTaskStatusInfoByJHS(){
	$("#last_jhs_step").text(7);
	$(".stepWaiting").css("margin-left","15%");
	$(".stepWaiting").eq(0).css("margin-left","");
	var $last = $('.stepFinish').last().parents('.stepWaiting').next('.stepWaiting');
	var basic = $last.find('.stepNum').text();
	$last.prepend('<div class="stepOn" stepbasic="' + basic + '">' + basic + '</div>');
	$last.find('.stepNum').remove();

	var step = $('.stepOn').attr('stepbasic');
	// console.log('current step is: ' + step);
	if (step <= 2) {
		$('[step="1"]').show();
		return;
	}
	if (step >2&&step<5) {
		$('[step="5"]').show();
		return;
	}

	$('.state').hide();
	var $currStep = $('[step="' + step + '"]');
	$currStep.show();
}

$(function() {
	if((CDT.platform=='TAOBAO'||CDT.platform=='TMALL')&&CDT.taskType!='JHS'){
		switchTaskStatusInfo();
	}
	else{
		switchTaskStatusInfoByJHS()
	}

	/*管理员为买手退款*/
	$(document).on('click', '#btnConfirmRefund', function() {
		var id = $(this).attr('data-id'),
			transNo = $.trim($('#txtTransNo').val());
		if (!transNo) {
			alert('请输入交易号！');
			return;
		}
		if (parseFloat(transNo.length) != 28 || isNaN(transNo)) {
			alert('交易号格式错误！');
			return;
		}
		if(!confirm('请再次确认要提交么？')){
			return;
		}
		Tr.post('/admin/buyerTask/confirmRefund', {
			'id': id,
			'transNo': transNo,
			'isSubtraction':false
		}, function(data) {
			if(data.code==555){
				alert('操作失败！');
			}
			if (data.code != 200) return;
			alert('退款成功！');

			$currStep.show().siblings().hide();
		});
	});

	/* 撤销“待平台退款”的任务 */
	$(document).on('click', '#J_cancelSysRefund', function() {
		var $button = $(this);
		var id = $button.attr('data-tid');
		if (!confirm('确定要撤销么？')) {
			return;
		}
		Tr.post('/buyer/task/sysRefund/cancel', {
			'id': id
		}, function(data) {
			if (data.code == 555) {
				alert('操作失败！');
			}
			if (data.code != 200) return;
			alert('操作成功！');

			window.location.replace('/buyer/task/' + id);
			// $button.parent().hide();
		});
	});
});