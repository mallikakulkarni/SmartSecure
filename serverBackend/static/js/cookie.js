function writeSessionCookie (cookieName, cookieValue) {
    document.cookie = escape(cookieName) + "=" + escape(cookieValue) + "; path=/";
}

function getCookieValue (cookieName) {
  var exp = new RegExp (escape(cookieName) + "=([^;]+)");
  if (exp.test (document.cookie + ";")) {
    exp.exec (document.cookie + ";");
    return unescape(RegExp.$1);
  }
  else return false;
}

function set_cookie() {
    apprise('Enter user name (e.g: joe):', {'input':true}, function(r) {
	if(r) {
	    var c_name = "user";
	    writeSessionCookie (c_name, r);   
	    window.location = '/eman/';
	}
    });
}
