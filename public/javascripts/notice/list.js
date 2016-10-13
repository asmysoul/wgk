CDT = {
	noticeTpl: ''
};

function loadPage() {
	Tr.get('/notice/list', {
		's.pageNo': 1
	}, function(data) {
		if (data.code != 200) return;
		if (data.results.length <= 0) {
			$('#noticeListBox').addClass('warnBox').html('<span class="iconfont">&#xf00b6;</span>没有公告!');
			return;
		}
		var output = Mustache.render(CDT.noticeTpl, $.extend(data, {
			createtime: function() {
				return new Date(this.createTime).Format('yyyy-MM-dd hh:mm:ss');
			}
		}));
		$('#noticeListBox').removeClass('warnBox').html(output);
	});
}

$(function() {
	CDT.noticeTpl = $('#noticeTpl').remove().val();
	Mustache.parse(CDT.noticeTpl);

	loadPage();
});