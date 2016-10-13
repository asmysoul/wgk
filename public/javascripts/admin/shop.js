CDT = {
	quyuTemp: null,
	trowTemp: null,
	countShops: '',
	currPageNo: 1,
	pageSize:10
};

//加载店铺列表
function loadShop(pageNo) {
	var startDate = ($.trim($('#regTimeStart').val()))?$.trim($('#regTimeStart').val()) + ' 00:00:00' : '',
	    endDate = ($.trim($('#regTimeEnd').val()))? $.trim($('#regTimeEnd').val()) + ' 00:00:00':'';
	Tr.get('/admin/listShop', {
		'vo.userNick': $.trim($('#userNick').val()),
		'vo.nick': $.trim($('#nick').val()),
		'vo.createTimeStart': startDate,
		'vo.createTimeEnd': endDate,
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
		var output = Mustache.render(CDT.trowTemp, $.extend(data, {
			createtime: function() {
				return new Date(this.createTime).Format('yyyy-MM-dd hh:mm:ss');
			},
			platformName: function() {
				return this.platformTitle;
			},
			platIcon: function() {
				return 'plat_' + this.platform;
			}
		}));
		$('.normTable').show();
		$('#noMsg').hide();
		$('.pagin-btm').show();
		$('#shopContainer').html(output);
		$('.pagination').pagination(data.totalCount, {
			items_per_page: CDT.pageSize,
			num_display_entries: 5,
			current_page: pageNo,
			num_edge_entries: 2,
			callback: loadShopCallBack,
			callback_run: false
		});
		CDT.currPageNo = pageNo;
	});
}

function loadShopCallBack(index, jq) {
	loadShop(index + 1);
}

function initBase(){
	$(document).on('click', '#shopContainer .updateShopBtn', function(){
		var shopId=$.trim($(this).parent().parent().attr("bid"));
		var platform=$.trim($(this).parent().parent().children().eq(1).attr("data-value"));
		var shopNick=$.trim($(this).parent().parent().children().eq(2).text());
		var shopAddress=$.trim($(this).parent().parent().children().eq(5).text());
		var sellerName=$.trim($(this).parent().parent().children().eq(3).text());
		var url=$.trim($(this).parent().parent().children().eq(8).text());
		var mobile = $.trim($(this).parent().parent().children().eq(4).text());
		var street = $.trim($(this).parent().parent().children().eq(6).text());
		var branch = $.trim($(this).parent().parent().children().eq(7).text());
		$("#shopId").val(shopId);
		$("#shopPlatform").val(platform);
		$("#shopNick").val(shopNick);
		$("#shopAddress").val(shopAddress);
		$("#shopUrl").val(url);
		$('#sellerName').val(sellerName);
		$('#mobile').val(mobile);
		$('#street').val(street);
		$('#branch').val(branch);
		var state=shopAddress.split("-")[0];
		var city=shopAddress.split("-")[1];
		var region=shopAddress.split("-")[2];
		initQuyu(state, city, region)
		Tr.popup('editShop');
		//绑定店铺
		/* 校验器 */
		// 自定义提示消息
	    $.extend($.validator.messages, {
		    required: Tr.error('必填'),
		    url:Tr.error('格式不正确'),
		    nick: Tr.error('格式不正确'),
		    equalTo: Tr.error('两次输入卡号不相同'),
		    shopNick:Tr.error('旺旺ID重复'),
		    telPhone:Tr.error('电话号码不正确'),
	    });
	    $.validator.addMethod('nick', function(value) {
	    	var flag=true;
	    	flag=Tr.validateName(value);
			return flag;
		});
		$.validator.addMethod("telPhone", function(value) {    
			var length = value.length;
			if(length>13) return false;
			return ((/^(\d{13})$/.test(value)) || (/^((\d{3,4})|(\d{3,4})-)+(\d{5,8})?$/.test(value)));      
		});
		// 校验规则
		var validator = $('#editShopForm').validate({
			rules: {
				wangzhi: {
					url:true,
					maxlength:200
				},
				nick:{
					nick:true
				},
				third:{
					required: {
						depends: function(element) {
							return $('#thrid option').length!=1 ;
						}			
					}
				},
				sellerName:{
					minlength:2,
					maxlength:20
				},
				mobile: {
					telPhone:true
				},
				street:{
					minlength:2,
					maxlength:200
				},
				branch:{
					minlength:4,
					maxlength:8
				}
			}
		});
		
		
		
	});
	
	$("#btnSave").click(function(){
		Tr.get('/admin/updateShop', {
			'vo.id': $("#shopId").val(),
			'vo.sellerName':$('#sellerName').val(),
			'vo.mobile':$('#mobile').val(),
			'vo.street':$('#street').val(),
			'vo.branch':$('#branch').val(),
			'vo.platform':$("#shopPlatform").val(),
			'vo.nick':$("#shopNick").val(),
			'vo.address':$("#first").val()+"-"+$("#second").val()+"-"+$("#thrid").val(),
			'vo.url':$("#shopUrl").val()
		}, function(data) {
			if (data.code != 200){
				return;
			}
			$("#editShop").hide();
			alert("操作成功");
			loadShop(1);
		});
	});
	
	$(document).on('change', '.province', function() {
		var $me = $(this),
		$city = $me.siblings('.city').eq(0),
		$region = $me.siblings('.region').eq(0);
		var id = $me.find('option:selected').attr('rid');
		$city.find('option').eq(0).nextAll('option').remove();
		$region.find('option').eq(0).nextAll('option').remove();
		if (!$me.find('option:selected').val()) {
			return;
		}
		Tr.get('/admin/region', {
			id: id
		}, function(data) {
			if (data.code != 200) return;
			var output = Mustache.render(CDT.quyuTemp, data);
			$city.append(output);
		});
	});

	$(document).on('change', '.city', function() {
		var $me = $(this),
		$region = $me.siblings('.region').eq(0);
		var id = $me.find('option:selected').attr('rid');
		$region.find('option').eq(0).nextAll('option').remove();
		if (!$me.find('option:selected').val()) {
			return;
		}
		Tr.get('/admin/region', {
			id: id
		}, function(data) {
			if (data.code != 200) return;
			var output = Mustache.render(CDT.quyuTemp, data);
			$region.append(output);
		});
	});
	//初始化地址数据
	function initQuyu(state, city, region) {
		var $me = $('.province'),
			$city = $me.siblings('.city').eq(0),
			$region = $me.siblings('.region').eq(0);
			var id = $me.find('option:selected').attr('rid');
			$city.find('option').eq(0).nextAll('option').remove();
			$region.find('option').eq(0).nextAll('option').remove();
		Tr.get('/admin/region', {
			id: 1
		}, function(data) {
			if (data.code != 200) return;
			var output = Mustache.render(CDT.quyuTemp, data);
			$('#first').append(output);
			$('#first').val(state);

			var id = $('#first').find('option:selected').attr('rid');
			Tr.get('/admin/region', {
				id: id
			}, function(data) {
				if (data.code != 200) return;
				var output = Mustache.render(CDT.quyuTemp, data);
				$('#second').append(output);
				$('#second').val(city);
				var cid = $('#second').find('option:selected').attr('rid');
				Tr.get('/admin/region', {
					id: cid
				}, function(data) {
					if (data.code != 200) return;
					var output = Mustache.render(CDT.quyuTemp, data);
					$('#thrid').append(output);
					$('#thrid').val(region);
				});

			});
		});	
	}
	$(document).on('click', '#queryBtn', function(){
		loadShop(1);
	});
	$('.inputDate').datePicker({
		startDate: '2016-01-01',
		endDate: '2018-01-01',
		clickInput: true,
		verticalOffset: 35
	});
}


$(function() {
	CDT.trowTemp = $('#trowTemp').remove().val();
	Mustache.parse(CDT.trowTemp);
	CDT.quyuTemp = $('#quyuTemp').remove().val();
	Mustache.parse(CDT.quyuTemp);
	initBase();
	loadShop(1);
});