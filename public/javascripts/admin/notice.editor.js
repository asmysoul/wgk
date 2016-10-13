/*公告编辑页面*/
var CDT = {
    token: null,
    uptoken: null,
};

function initBase(){
}

function initUploader() {
    // 先获取token
    Tr.get('/admin/upload/token', {}, function(data) {
        if (data.code != 200) return;
        // 初始化SDK
        CDT.uptoken = data.results;
        var option = Tr.uploadOption();
        option.domain = 'http://' + App.QnNoticeFileBucket + '.jzniu.cn/';
        option.uptoken = CDT.uptoken;
        option.init = {
            'BeforeUpload': function(up, file) {
                // 检查文件类型

            },
            'UploadProgress': function(up, file) {
                // 每个文件上传时,处理相关的事情，转菊花、显示进度等
            },
            'FileUploaded': function(up, file, info) {
                var domain = up.getOption('domain');
                var res = $.parseJSON(info);
                var sourceLink = domain + res.key;
                $('#urlData').attr('style', 'display:true');
                $('#urlData').attr('url', sourceLink);
                //复制
                $('a#urlData').zclip({ 
                    path:'/public/javascripts/ZeroClipboard.swf', 
                    copy:$('a#urlData').attr('url') 
                });
            },
            'Error': function(up, err, errTip) {
                //上传出错时,处理相关的事情
                alert('上传失败！');
            },
            'Key': function(up, file) {
                // 若想在前端对每个文件的key进行个性化处理，可以配置该函数
                // 该配置必须要在 unique_names: false , save_key: false 时才生效
                var key = "";
                // do something with key here
                return key
            }
        }
        Qiniu.uploader(option);

    });
}

$(function() {
    var editor_a = UE.getEditor('contentEditor', {
        initialFrameHeight: 500,
        // textarea: 'n.content'
    });

    //--自动切换提交地址----
    var doc = document,
        version = editor_a.options.serverUrl || editor_a.options.imageUrl || "php",
        form = doc.getElementById("form");

    if (version.match(/php/)) {
        form.action = "./server/getContent.php";
    } else if (version.match(/net/)) {
        form.action = "./server/getContent.ashx";
    } else if (version.match(/jsp/)) {
        form.action = "./server/getContent.jsp";
    } else if (version.match(/asp/)) {
        form.action = "./server/getContent.asp";
    }

    initBase();
    initUploader();
});