var CDT = {
	flowNum:App.flowNum,
	totalFlow:0
};
/*第一步：选任务*/
	$(document).on('click', '#taskStep-1 .checkTextBtn', function() {
		// 选择平台时切换店铺
		if ($(this).hasClass('platform')) {
			var platform = $(this).attr('value');
		}
		$(this).parent().children('.selectedCtb').removeClass('selectedCtb');
		$(this).addClass('selectedCtb');
		var p=$('#taskStep-1 .platform.selectedCtb').attr('value');
		//选择类型
		
	});
	$(function(){
		initBase();
		$("#txtItemUrl").blur(function(){
			var platform = $('.platform.selectedCtb').attr('value');
			var url=$("#txtItemUrl").val();
			if("TAOBAO"==platform){
				if(url!=""&&(url.indexOf("taobao")<0&&url.indexOf("tmall")<0)){
					alert("商品地址和所选平台不匹配！");
					$("#txtItemUrl").val("");
				}
			}
		});
		
		$('.inputDate').datePicker({
			startDate:'2016-01-01',
			endDate: '2018-01-01',
			clickInput: true,
			verticalOffset: 35
		});
		
	});
	
	function initBase(){
		var validator = $('#addTrafficForm').validate({
			onkeyup : false,
			rules : {
				'txtItemUrl' : {
					required : true
				},
				'times' : {
					required : true,
					number:true
				},
				'beginTime' : {
					required : true
				},
				'endTime' : {
					required : true
				},keyWords:{
					required : true
				}
			},
			messages : {
				'txtItemUrl' : {
					required : Tr.error('必填')
				},
				'times' : {
					required : Tr.error('必填'),
					number:Tr.error('必须为数字')
				},
				'beginTime' : {
					required : Tr.error('必填')
				},
				'endTime' : {
					required : Tr.error('必填')
				},keyWords:{
					required : Tr.error('必填')
				}
			}
		});
		
		$("#publishSave").click(function(){
			var txtItemUrl=$("#txtItemUrl").val();
			var platform=$('.platform.selectedCtb').attr('value');
			var type=$('.type.selectedCtb').attr('value');
			var $times=$("#clickTimes").val();
			var $beginTime=$("#beginTime").val();
			var $endTime=$("#endTime").val();
			if(validator.form()){
				var flow=parseInt($times);
				if(type=="MOBILE"){
					flow=flow*3;
				}
				if(confirm("确定发布吗？")){
					var p={
							'vo.type':platform+type,
							'vo.beginTime':$beginTime,
							'vo.endTime':$endTime,
							'platform':platform,
							'url':txtItemUrl
					};
				getParams(p);
				if(CDT.flowNum<CDT.totalFlow){
					return alert("流量不足，请先充值后再发布！");
				}
				Tr.get('/seller/saveFlow',p, function(data) {
					if(data.code!=200){
						return alert(data.msg);
					}
					alert("发布任务成功");
					location.href=location.href;
				});
			}
			}
		});
		
		
		$(document).on('click', '.legendPanel .close', function() {
			var $me = $(this);
			$me.parents('.panelBox').next().show();
			$(this).parents(".wordPlan").remove();
		});
		
		$(document).on('blur', '.clickTime', function() {
			var type=$('.type.selectedCtb').attr('value');
			var wordPlanBoxesNum = [];
			$.merge(wordPlanBoxesNum, $('.legendPanel .wordPlan'));
			var count=0;
			$(wordPlanBoxesNum).each(function(n) {
				var times = $.trim($(this).find('input[name="times"]').val());
				if(type=="MOBILE"){
					times=parseInt(times)*3;
				}
				count+=parseInt(times);
			});
			if(count>0){
			$(".payFlow").text(count);
			if((parseInt(CDT.flowNum)-count)<0){
				$(".clickTime").val("");
				$(".residueFlow").text(CDT.flowNum);
				$(".payFlow").text(0);
				return alert("流量不足请充值！");
				
			}
			$(".residueFlow").text(parseInt(CDT.flowNum)-count);
			}
		});
		
		$(document).on('click', '.plusBlockBtn', function() {
			var index=$(".wordPlan").length+1;
			if(index>4){
				return false;
			}
			var html="<div class='panelLine wordPlan'>"
					+"<span class='floatLeft ls'> 关键词 <span class='red bold'>*</span>"+
					"</span> <input type='text' class='inputText floatLeft ls middle required' name='word' />" +
					"<span class='floatLeft ls'>点击次数</span> <input type='text' class='inputText floatLeft clickTime ls short required' name='times' />"+
								"<a href='javascript:;' class='close iconfont' original-title=''>&#xf0011;</a></div>";
			$(this).parent().before(html);
			
		});
		
		
	}
	
	function getParams(paramMap){
		var type=$('.type.selectedCtb').attr('value');
		var wordPlanBoxesNum = [];
		$.merge(wordPlanBoxesNum, $('.legendPanel .wordPlan'));
		var j=0;
		$(wordPlanBoxesNum).each(function(n) {
			var words = $.trim($(this).find('input[name="word"]').val());
			var times = $.trim($(this).find('input[name="times"]').val());
			var indexPrefix = 'vo.listKeyWords[' + j + ']';
			paramMap[indexPrefix + '.keywords'] = words;
			paramMap[indexPrefix + '.clickTimes'] = times;
			if(type=="MOBILE"){
				times=parseInt(times)*3;
			}
			CDT.totalFlow+=parseInt(times);
			j++;
		});
	}
	