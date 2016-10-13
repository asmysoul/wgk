CDT = {

};

$(function() {
	var $last = $('.stepFinish').last().parents('.stepWaiting').next('.stepWaiting');
	var basic = $last.find('.stepNum').text();
	$last.prepend('<div class="stepOn" stepbasic="'+basic+'">'+basic+'</div>');
	$last.find('.stepNum').remove();

	var step = $('.stepOn').attr('stepbasic');
	var $currStep = $('[step="' + step + '"]');
	if ($currStep.length <= 0) {
		// $('.state').hide();
	} else {
		$currStep.show();//.siblings().hide();
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
			'isSubtraction' : false
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