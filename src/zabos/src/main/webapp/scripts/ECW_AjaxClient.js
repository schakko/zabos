/**
 * ECW_AjaxClient
 * 
 * Version 1.0.1
 * ö
 * @version 1.0.1
 * @package ECW_Ajax
 * @author Christoph Steindorff <cst@ecw.de>
 * @author Marc Biebusch <mbi@ecw.de>
 * @author EDV Consulting Wohlers GmbH, http://www.ecw.de
 * @copyright EDV Consulting Wohlers GmbH, 2010
 */

// Variable zum Speichern des letzten http-Status
lastState = 666;

/*
 * The global object JSON contains two methods.
 * 
 * JSON.stringify(value) takes a JavaScript value and produces a JSON text. The
 * value must not be cyclical.
 * 
 * JSON.parse(text) takes a JSON text and produces a JavaScript value. It will
 * return false if there is an error.
 */
var JSON = function() {
	var m = {
		'\b' : '\\b',
		'\t' : '\\t',
		'\n' : '\\n',
		'\f' : '\\f',
		'\r' : '\\r',
		'"' : '\\"',
		'\\' : '\\\\'
	}, s = {
		'boolean' : function(x) {
			return String(x);
		},
		number : function(x) {
			return isFinite(x) ? String(x) : 'null';
		},
		string : function(x) {
			if (/["\\\x00-\x1f]/.test(x)) {
				x = x.replace(/([\x00-\x1f\\"])/g, function(a, b) {
					var c = m[b];
					if (c) {
						return c;
					}
					c = b.charCodeAt();
					return '\\u00' + Math.floor(c / 16).toString(16)
							+ (c % 16).toString(16);
				});
			}
			return '"' + x + '"';
		},
		object : function(x) {
			if (x) {
				var a = [], b, f, i, l, v;
				if (x instanceof Array) {
					a[0] = '[';
					l = x.length;
					for (i = 0; i < l; i += 1) {
						v = x[i];
						f = s[typeof v];
						if (f) {
							v = f(v);
							if (typeof v == 'string') {
								if (b) {
									a[a.length] = ',';
								}
								a[a.length] = v;
								b = true;
							}
						}
					}
					a[a.length] = ']';
				} else if (typeof x.valueOf == 'function') {
					a[0] = '{';
					for (i in x) {
						v = x[i];
						f = s[typeof v];
						if (f) {
							v = f(v);
							if (typeof v == 'string') {
								if (b) {
									a[a.length] = ',';
								}
								a.push(s.string(i), ':', v);
								b = true;
							}
						}
					}
					a[a.length] = '}';
				}
				return a.join('');
			}
			return 'null';
		}
	};
	return {
		copyright : '(c)2005 JSON.org',
		license : 'http://www.crockford.com/JSON/license.html',
		/*
		 * Stringify a JavaScript value, producing a JSON text.
		 */
		stringify : function(v) {
			var f = s[typeof v];
			if (f) {
				v = f(v);
				if (typeof v == 'string') {
					return v;
				}
			}
			return null;
		},
		/*
		 * Parse a JSON text, producing a JavaScript value. It returns false if
		 * there is a syntax error.
		 */
		parse : function(text) {
			try {
				return !(/[^,:{}\[\]0-9.\-+Eaeflnr-u \n\r\t]/.test(text
						.replace(/"(\\.|[^"\\])*"/g, '')))
						&& eval('(' + text + ')');
			} catch (e) {
				return false;
			}
		}
	};
}();

/** ***************************************************** */

/**
 * httpRequest()
 * 
 * Setzt einen XMLHttpRequest ab.
 * 
 * @param string
 *            uri URI des aufzurufenden AJAX-Servers
 * @param object
 *            objData JS-Objekt mit den Daten, die an den AJAX-Server gesendet
 *            werden sollen
 * @param function
 *            ResponseHandler Name der Funktion die die Antworten vom
 *            AJAX-Server empfangen soll
 * @return
 */
function httpRequest(uri, objData, ResponseHandler, isPost) {
	var ajax = new AjaxClass;
	ajax.httpRequest(uri, objData, ResponseHandler, isPost);
}

/**
 * Erstellt eine Abfrage an das ZABOS-Ajax-Gateway
 * 
 * @param methodName
 * @param objData
 * @param ResponseHandler
 */
function zabosAjaxRequest(methodName, objData, ResponseHandler, isPost) {
	httpRequest(AJAX_DISPATCHER + "?JSESSIONID=" + JSESSIONID + "&method="
			+ methodName, objData, ResponseHandler, isPost);
}
/**
 * class AjaxClass()
 * 
 * Klasse die von httpRequest() benutzt wird, um einen XMLHttpRequest
 * abzusetzen.
 */
function AjaxClass() {

	var http = AjaxClass_createRequestObject();
	var externalResponseHandler;
	var errorMessageShown = false;

	this.httpRequest = AjaxClass_httpRequest;
	this.handleResponse = AjaxClass_handleResponse;

	function AjaxClass_createRequestObject() {

		var objReq;
		try {
			objReq = new ActiveXObject("Msxml2.XMLHTTP");
		} catch (e) {
			try {
				objReq = new ActiveXObject("Microsoft.XMLHTTP");
			} catch (ee) {
				objReq = null;
			}
		}
		if (!objReq && typeof XMLHttpRequest != "undefined") {
			objReq = new XMLHttpRequest();
		}

		if (!objReq)
			alert('could not initialize XMLHttpRequest');
		return objReq;
	}

	/**
	 * AjaxClass_httpRequest()
	 * 
	 * Funktion zum Abrufen der Informationen
	 * 
	 * @param uri -
	 *            URI der abzurufenden Seite
	 * @param value -
	 *            Wert des Parameters
	 * @param targetID -
	 *            ID des Elements, dass die Ausgabe anzeigen soll
	 */
	function AjaxClass_httpRequest(uri, objData, ResponseHandler, isPost) {
		
		if (isPost == undefined){
			isPost = false;
		}
		
		// Default-Postfix 
		var postfix = "?", completeURI, httpBodyData = null, httpMethod = "GET";

		// alert('request absetzen');
		externalResponseHandler = ResponseHandler;

		// Ueberpruefen, welches Anhaengsel wir an die URI pasten muessen
		// URI der Form http://domain/?data1=wert1... => ?-Postfix
		if (uri.match(/\?/)) {
			postfix = "&";
		}

		// Timestamp erzeugen. Ohne Timestamp werden die AJAX-Requests im Internet Explorer 8 gecachet.
		var timestamp = new Date().getTime();
		
		completeURI = uri + postfix + 'ts=' + timestamp;
		
		// Komplette URI zusammenbauen
		// Fix: encodeURIComponent zum korrekten Kodieren der Daten
		// Siehe http://www.buildblog.de/2009/05/01/ajax-und-umlaute-das-ewig-wahrende-utf-8-problem-und-seine-losung/
		if (isPost == false) {
			completeURI += '&data=' + encodeURIComponent(JSON.stringify(objData));
		}
		else {
			httpMethod = "POST";
		}
		
		// alert(completeURI);
		http.open(httpMethod, completeURI, true);

		// setRequestHeader muss *nach* http.open() und *vor* http.send() aufgerufen werden
		http.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");

		// Handler definieren
		http.onreadystatechange = this.handleResponse;
		
		if (isPost) {
			httpBodyData = "data=" + JSON.stringify(objData);
			http.setRequestHeader("Connection", "close");
			http.setRequestHeader("Content-length", httpBodyData.length);
		}

		// Daten senden. Bei POST ist dieses Feld != null
		http.send(httpBodyData);
	}
	/**
	 * handleResponse()
	 * 
	 * verarbeitet die Antworten des Requests
	 */
	function AjaxClass_handleResponse() {
		/*
		 * Das XMLHttpRequest-Objekt hat eine Eigenschaft readyState. Diese kann
		 * folgende Werte annehmen: 0: Uninitialized 1: Loading 2: Loaded 3:
		 * Interactive 4: Finished
		 */
		if (http.readyState == 4) { // Finished?
			try {
				if (http.status == 200) { // Status OK?
					lastState = 200;
					var objResult = eval('(' + http.responseText + ')'); // JSON:
					errorMessageShown = false;
					externalResponseHandler(objResult);
				} else if (http.status == 0) {
					if (lastState != 0) {
						lastState = 0;
						viewErrorMessage("Fehler: Die Verbindung mit ZABOS wurde getrennt (Fehler 0). \nMelden Sie sich bitte neu an!");
					}
				} else if (http.status == 503) {
					if (lastState != 503) {
						lastState = 503;
						viewErrorMessage("ZABOS ist nicht verfügbar (Fehler 503). \nKontaktieren Sie Ihren System-Administrator!");
					}
				} else {
					viewErrorMessage("Es ist ein Fehler aufgetreten: \nHTTP-Status "
							+ http.status);
					lastState = http.status;
				}
			} catch (e) {
				viewErrorMessage("Bei der Kommunikation mit dem ZABOS-Server ist ein Fehler aufgetreten: " + e);
			}
		}
	}
	
	function viewErrorMessage(msg)
	{
		errorMessageShown = true;
		
		if (typeof console != "undefined") {
			console.exception(msg);
		}
		else {
			alert(msg);
		}
	}

}