function getFlashMovieObject(movieName) {
	if (window.document[movieName]) {
		return window.document[movieName];
	}
	if (navigator.appName.indexOf("Microsoft Internet") == -1) {
		if (document.embeds && document.embeds[movieName])
			return document.embeds[movieName];
	} else
	{
		return document.getElementById(movieName);
	}
}
$(function() {
	swfobject.embedSWF('/public/images/JSocket.swf', 'sto_core', '100%', '30',
            '10.0.0', '/public/images/expressInstall.swf', null, { wmode: 'transparent', allowScriptAccess: 'always',swliveconnect:'true' }, null,
            function(){
            	
            });


	$('#btnTake').click(function(){
		var x = swfobject.getObjectById('sto_core'),names = x.getlso().split(','),newName = $('#txtToTake').val();
		for(var i in names){
			if(names[i] == newName) return;
		}
		x.setlso(names.join(',')+','+$('#txtToTake').val());
	});
	$('#btnIfTake').click(function(){
		var x = swfobject.getObjectById('sto_core'),names = x.getlso();
		alert(names);
	});

});