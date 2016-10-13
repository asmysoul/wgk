lastScrollY = 0;
var graySrc = false;
var InterTime = 1;
var maxWidth = -1;
var minWidth = -128;
var numInter = 8;
var BigInter;
var SmallInter;
var o = null;
var i = 0;
wwwjzniucn = function(id, _top, _left) {
    var me = id.charAt ? document.getElementById(id) : id, d1 = document.body, d2 = document.documentElement;
    d1.style.height = d2.style.height = '100%'; me.style.top = _top ? _top + 'px' : 0; me.style.left = _left + "px"; 
    me.style.position = 'absolute';
    me.style.display = 'block';
    setInterval(function() { me.style.top = parseInt(me.style.top) + (Math.max(d1.scrollTop, d2.scrollTop) + _top - parseInt(me.style.top)) * 0.1 + 'px'; }, 10 + parseInt(Math.random() * 20));
    return arguments.callee;
};
$(document).ready(function() {
    var html = '';
    html += '<div id="wwwjzniucn" style="z-index:2;display:none;" onmouseover=toBig() onmouseout=toSmall()>';
    html += '    <div class="services">';
    html += '        <div class="con">';
    html += '        	<ul>';
    html += '            	<li class="qq">';
    html += '                	<p><a href="javascript:;" class="qq"><a target="_blank" href="http://wpa.qq.com/msgrd?v=3&uin=954502368&site=qq&menu=yes"><img border="0" src="http://wpa.qq.com/pa?p=2:954502368:41" alt="兼职牛商家客服-妍雅" title="兼职牛商家客服-妍雅"/></a></p>';
    
    html += '                 </li>';
	html += '            	<li class="qq2">';
    html += '                	<p><a href="javascript:;" class="qq"><a target="_blank" href="http://wpa.qq.com/msgrd?v=3&uin=954502368&site=qq&menu=yes"><img border="0" src="http://wpa.qq.com/pa?p=2:954502368:41" alt="兼职牛买手客服-kittly" title="兼职牛买手客服-kittly"/></a></p>';
    
    html += '                 </li>';
	
	
    html += '            </ul>';
    html += '        </div>';
    html += '    </div>';
    html += '    <a href="http://www.jzniu.cn" title="QQ客服不在找微信" class="wx" target="_blank">QQ客服不在找微信</a>';
    html += '    <div class="Obtn"></div>';
    html += '</div>';

    $(document.body).append(html);

    o = document.getElementById("wwwjzniucn");
    i = parseInt(o.style.left);

    wwwjzniucn('wwwjzniucn', 500, -128);
});

function Big() {
    if (parseInt(o.style.left) < maxWidth) {
        i = parseInt(o.style.left);
        i += numInter;
        o.style.left = i + "px";
        if (i == maxWidth)
            clearInterval(BigInter);
    }
    if (!graySrc) {
        $(o).find("img").each(function() {
            $(this).attr("src", $(this).attr("Original"));
        });
        graySrc = true;
    }
}
function toBig() {
    clearInterval(SmallInter);
    clearInterval(BigInter);
    BigInter = setInterval(Big, InterTime);
}
function Small() {
    if (parseInt(o.style.left) > minWidth) {
        i = parseInt(o.style.left);
        i -= numInter;
        o.style.left = i + "px";

        if (i == minWidth)
            clearInterval(SmallInter);
    }
}
function toSmall() {
    clearInterval(SmallInter);
    clearInterval(BigInter);
    SmallInter = setInterval(Small, InterTime);

}