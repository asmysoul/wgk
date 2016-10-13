CDT = {
	uptoken: null,
	token: null
};
function initBaseBind(){
	
	/*切换买手账号*/
	$(document).on('click','#accountBtnList .swi-btn',function(){
		$(this).addClass('on').siblings().removeClass('on');
		parseShop($(this).text());
	});

	/*切换商家店铺*/
	$(document).on('click','#shopBtnList .swi-btn',function(){
		$(this).addClass('on').siblings().removeClass('on');
		loadTask();
	});

	/*提交评价截图*/
	$(document).on('click', '.rate', function() {
		var id = $(this).attr('taskId'),
			src = $('.imgContent[taskId=' + id + ']').attr('src'),
			picUrls = [];
		if (!$('.itemPicBox').find('img').attr('src')) {
			alert('亲，别忘了上传好评截图啊，O(∩_∩)O~');
			return;
		}
		picUrls.push(src);

		if(!confirm('亲，再仔细检查下截图是否正确，确定要提交么？')){
			return;
		}
		Tr.post('/buyer/task/confirmRecvGoods', {
			'vo.picUrls': picUrls,
			id: id
		}, function(data) {
			if (data.code != 200) return;

			alert('操作成功！我们会立即通知商家进行退款，亲们耐心等待哈，O(∩_∩)O~');

			//删除该任务数据
			$('.taskCell[taskId="' + id + '"]').remove();
			$.each(App.tasks.data, function(i, n) {
				if (n.id == id) {
					App.tasks.data.splice($.inArray(n, App.tasks.data), 1);
				}
			});
		});
	});

	/*修复上传失败*/
	$(document).on('click', '.uploadFailed', function() {
		initUploader($(this).attr('data-id'), true);
		alert('系统已尝试进行自动修复，请再试一次。若问题依旧，请联系客服解决！');
	});
	
	$(document).on('mouseover', '.btn-imgComm-model', function() {
		var id=$(this).attr('data-id');
		var model=".imgComm-model"+id;
		if(App.platform=='TAOBAO'){
			$(model).attr("src","/public/images/taobao-comm.png");
		}else if (App.platform=='TMALL') {
			$(model).attr("src","/public/images/tmall-comm.png");
		}else if (App.platform=='JD') {
			$(model).attr("src","/public/images/jd-comm.png");
		}else if (App.platform=='MOGUJIE') {
			$(model).attr("src","/public/images/mogujie-comm.png");
		}
		$(model).show();
	});
	$(document).on('mouseout', '.btn-imgComm-model', function() {
		var id=$(this).attr('data-id');
		var model=".imgComm-model"+id;
		$(model).hide();
	});
	
}

function parseAccount(){
	var accounts = [];
	$.each(App.tasks.data,function(i,n){
		if($.inArray(n.buyerAccountNick,accounts)==-1){
			accounts.push(n.buyerAccountNick);
		}
	});
	var accountBtnHtml = '';
	$.each(accounts,function(i,n){
		accountBtnHtml = accountBtnHtml + '<a class="swi-btn'+(i==0?' on':'')+'" href="javascript:;">'+n+'</a>';
	});
	$('#accountBtnList').html(accountBtnHtml);
	if(accounts.length>0)
		parseShop(accounts[0]);
}
function parseShop(account){
	var shops = [],shopBtnHtml = '';
	$.each(App.tasks.data,function(i,n){
		if(n.buyerAccountNick == account && $.inArray(n.shopName,shops)==-1){
			shops.push(n.shopName);
		}
	});
	$.each(shops,function(i,n){
		shopBtnHtml = shopBtnHtml + '<a class="swi-btn'+(i==0?' on':'')+'" href="javascript:;">'+n+'</a>';
	});
	$('#shopBtnList').html(shopBtnHtml);

	loadTask();
}
function loadTask(){
	var account = $('#accountBtnList .on').text(),shop = $('#shopBtnList .on').text(),data = {results:[]};
	$.each(App.tasks.data,function(i,n){
		if(n.buyerAccountNick == account && n.shopName == shop){
			data.results.push(n);
		}
	});
	var output = Mustache.render(CDT.taskRowTempl, $.extend(data,{
		commentPic:function(){
			return this.goodCommentImg==null?"":this.goodCommentImg.split(",");
		}
	}));
	$('#taskContainer').html(output);

	$.each(App.tasks.data,function(i,n){
		initUploader(n.id, false);
	});
}

//初始化上传图片函数
function initUploader(id,force) {
	// 先获取token
	Tr.post('/user/upload/token', {
		force:force
	}, function(data) {
		if (data.code != 200) return;
		// 初始化SDK
		CDT.uptoken = data.results;

		var option = Tr.uploadOption();
		option.domain = 'http://' + App.QnTaskFileBucket + '.qiniudn.com/';
		option.uptoken = CDT.uptoken;
		option.max_retries = 0;
		var newButtonId = 'btnPickfiles' + id;
		option.browse_button = newButtonId;

		// 首次上传失败后，二次刷新token时要先重建上传按钮
		// $uploadBtn = $('#' + newButtonId);
		if (force) {
			var button = '<input type="button" class="middle ls floatLeft" id="' + newButtonId + '" value="上传评论截图" />';
			$('#' + newButtonId).parent().html(button);
		}

		option.init = {
			'FileUploaded': function(up, file, info) {
				var res = $.parseJSON(info);
				var sourceLink = up.getOption('domain') + res.key;
				$('.itemPicBox').html('<img src="'+sourceLink+'" class="imgContent" taskId="' + id + '">');
			},
			'Error': function(up, err, errTip) {
				var $uploadFailedBtn = $('#' + newButtonId).parent().next('.uploadFailed');
				if (force) {
					alert('上传失败！请联系管理员解决！');
					$uploadFailedBtn.hide();
					return;
				}
				alert('上传失败！');
				$uploadFailedBtn.show();
			}
		};
		new QiniuJsSDK().uploader(option);
	});
}

$(function() {
	CDT.taskRowTempl = '{{#results}}'+$('#taskTempl').remove().html()+'{{/results}}';
	Mustache.parse(CDT.taskRowTempl);

	initBaseBind();

	parseAccount();
});
