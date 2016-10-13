CDT = {
	
};

//加载列表

function initBase() {
	
	$(document).on('click', '#queryBtn', function() {
		var buyerAndSellerTime = $('#buyerAndSellerTime').val();
			buyerAndShopTime = $('#buyerAndShopTime').val();
			buyerAcountAndShopTime = $('#buyerAcountAndShopTime').val();
			buyerAcountAndItemTime = $('#buyerAcountAndItemTime').val();
		Tr.post('/seller/sellerConfig/modifyLimitMessage', {
			'config.buyerAndSellerTime': buyerAndSellerTime,
			'config.buyerAndShopTime': buyerAndShopTime,
			'config.buyerAcountAndShopTime': buyerAcountAndShopTime,
			'config.buyerAcountAndItemTime': buyerAcountAndItemTime
		}, function(data) {
			if(data.code!=200) return;
			alert('修改成功');
		});
	});
}

$(function() {
	
	initBase();
	
});