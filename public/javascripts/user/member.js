CDT = {
	membertime:null
};
function initBaseBind() {
	$('#btnConfirmCharge').click(function() {
	//TODO  设置支付方式
	var month = $('#memberPeriod').find('input:checked').val();
	var token = $('input[name="authenticityToken"]').val();
		var useIngot = false,usePledge = false;
		if($('#payType .ckIngot').attr('checked')){
			useIngot = true;
		}
		if($('#payType .ckPledge').attr('checked')){
			usePledge = true;
		}

		var other = !useIngot && !usePledge;
		//console.log(other);
		if(other){
			if(!confirm('确认用网银支付？')){
				return;
			}
			var win = window.open(),openDtf = function(url){
				setTimeout(function(){
					win.location = url;
				}, 200);
			};
		}		
		Tr.post('/user/member/charge', {
			month: month,
			useIngot:useIngot,
			usePledge:usePledge,
			authenticityToken: token
		}, function(data) {
			if (data.code == 800101 || data.code == 8001){
				alert(data.msg);
			} 
			if (data.code != 200) return;
			if(other){
				//TODO 弹出模态窗口：支付成功 or 支付遇到问题
				var bank = $('#platformSelect input:checked').val();
				var url = '/user/pay?type=MEMBER&p=' + bank + '&id=' + data.results;
				Tr.popup('bankPayment');
				openDtf(url);
				return;
			}
			alert('充值会员成功！');
			window.location = '/user';
		});

	});
}
function membertime(){
	
}


$(function() {
	initBaseBind();
	//网银支付弹窗支付结果的两个方式
	$('#btnbankpay').click(function(){
		$('#bankPayment').hide();
		location.href='/user';
	});
	$('#btnpayerror').click(function(){
		$('#bankPayment').hide();
	});
    var time = Date.parseDate($('.time').text(), 'Y-m-d');
		time.setMonth(time.getMonth()+1);
		var newmonth =time.getMonth()+1;
		if(newmonth =='0'){
			newmonth=12;
		}

		date = time.getFullYear() + '-'+ newmonth + '-'+ time.getDate();
		$('.date').html(date);
});

$(':radio[name="r_period"]').each(function(){		
	if($(this).is(':checked')){
		$('.mon').append($(this).val());
		var Renew = parseFloat($(this).parent('div').find('span').eq(1).text());
		var ingot = parseFloat($('span.ingot').text());
		var pledge = parseFloat($('span.pledge').text());
		Tr.checkprice(Renew ,ingot,pledge);
	}

	$(this).click(function(){
		CDT.membertime = Date.parseDate($('.time').text(), 'Y-m-d');
		if($(this).is(':checked')){
			$('.mon').html($(this).val());
			var Renew = parseFloat($(this).parent('div').find('span').eq(1).text());
			var ingot = parseFloat($('span.ingot').text());
			var pledge = parseFloat($('span.pledge').text());
			Tr.checkprice(Renew ,ingot,pledge);
			var time = Date.parseDate($('.time').text(), 'Y-m-d');
			var newtime =  parseFloat($(this).val());
			time.setMonth(time.getMonth()+newtime);
			var newmonth =time.getMonth()+1;
			if(newmonth =='0'){
				newmonth=12;
			}

			date = time.getFullYear() + '-'+ newmonth + '-'+ time.getDate();
			$('.date').html(date);
		}
		
	});
});
//选中
$(function(){
	$(document).on('click', '.pay', function() {
		$(this).find('input').attr('checked','checked');
		$('input[type="checkbox"]').removeAttr('checked');
	});
	$(document).on('click', '#platformSelect .checkTextBtn', function() {
		var platform = $(this).attr('value');
		$(this).parent().children('.selectedCtb').removeClass('selectedCtb').end().end().addClass('selectedCtb');
		$('#' + platform +'Bind').show().siblings().hide();
	});
	$('input[type="checkbox"]').click(function(){
		$('input[name="pay_type"]').removeAttr('checked');
		$('.pay').removeClass('selectedCtb');
	});
	
});
