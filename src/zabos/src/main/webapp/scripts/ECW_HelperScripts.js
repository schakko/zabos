	/**
	 * ECW_HelperScripts
	 * Sammlung von Hilfsscripten, die für die Ajax/HTML-Kommunikation benötigt werden
	 * @version 	 0.1
	 * @package 	 smsalarm
	 * @subpackage javascript
	 * @author 		 Christopher Klein <ckl@ecw.de> 
	 * @author 		 Marc Biebusch <mbi@ecw.de> 
	 * @author     EDV Consulting Wohlers GmbH, http://www.ecw.de
	 * @copyright  EDV Consulting Wohlers GmbH, 2006
	 */
	
	/**
	 * Liefert das angegebene Objekt zur?ck
	 * 
	 * @param objectName
	 *            Name des Objekts
	 * @return object
	 */
 
	function getObject(objectName)
	{
	  var returnObject = document.getElementById(objectName);
	  if (returnObject == null) {
	    alert("Objekt mit dem Namen "+objectName+" existiert nicht.");
	  }
	  
	  return returnObject;
	}
	
	// Array Remove - By John Resig (MIT Licensed)
	Array.prototype.remove = function(from, to) {
	  var rest = this.slice((to || from) + 1 || this.length);
	  this.length = from < 0 ? this.length + from : from;
	  return this.push.apply(this, rest);
	};
	
	/**
	 * F?llt eine Select-Liste mit den Werten, die über den Parameter hashData
	 * übergeben wurden
	 * 
	 * @param string
	 *            objectName Select-Liste
	 * @param object
	 *            hashData Objekt als Hash
	 */
	function fillSelectListWithHashData(objectName,hashData)
	{
		// var objectToFill = getObject(objectName);
		
		document.forms[0].elements[objectName].length = null;
		
		var i = 0;
		
		for (idData in hashData)
		{
		  document.forms[0].elements[objectName].length = i + 1;
			document.forms[0].elements[objectName].options[i].value = idData;
			document.forms[0].elements[objectName].options[i].text = hashData[idData];

			i++;
		}
	}


	
	/**
	 * Verschiebt die selektierten Elemente aus objSource in objTarget ein und
	 * entfernt eben diese aus objSource
	 * 
	 * @param string
	 *            objSource Quell-Select-Box
	 * @param string
	 *            objTarget Ziel-Select-Box
	 */
	function moveSelectedEntries(szObjSource, szObjTarget)
	{
	    var objSource, objTarget;
	    
	    objSource = getObject(szObjSource);
	    objTarget = getObject(szObjTarget);
	    
		var i, m, currentEntry, iNewTargetLength;
		
		var SelectedOption = objSource.selectedIndex;
		
		// alert("objSource"+objSource+"L?nge: "+tmpSource.length);
		
		for (i = 0, m = objSource.length; i < m; i)
		{
			// Erstes Element der Liste laden
			currentEntry = objSource.options[i];
			
			// Ggw. Element ist ausgew?hlt
			if (currentEntry.selected)
			{
				// Neues Element im Ziel anlegen
				iNewTargetLength = objTarget.length + 1;
				objTarget.length = iNewTargetLength;
				
				objTarget.options[(iNewTargetLength - 1)].text = currentEntry.text;
				objTarget.options[(iNewTargetLength - 1)].value = currentEntry.value;
				
				// Altes Element loeschen
				objSource.options[i] = null;
				
				// Zaehler auf 0 setzen, da wir gerade ein Element verschoben
				// haben und von vorne anfangen m?ssen
				i = 0;
				// Laenge der Quelle neue setzen. Aus o.g. Grund
				m = objSource.length;
			}
			else
			{
				// N?chstes Element testen
				i++;
			}

		}
		
		sortSelectList(objTarget);

		if (SelectedOption >= objSource.length)
		{
			SelectedOption = objSource.length-1;
		}
		objSource.selectedIndex = SelectedOption;
	}

	/**
	 * Sortiert die uebergebene Liste
	 * 
	 * @param string
	 *            objSource Name des Quell-Objekts
	 */
	function sortSelectList(objSource)
	{
		
		var hashList = new Object();
		var arrToSort = Array(objSource.length);
		var currentEntry;

		for (var i = 0, m = objSource.length; i < m; i++)
		{
			currentEntry = objSource.options[i];
			hashList[currentEntry.text] = currentEntry.value;
			
			arrToSort[i] = currentEntry.text;
		}
		
		// Liste sortieren
		arrToSort.sort();
		
		// Alle Elemente loeschen
		objSource.length = null;
		objSource.length = arrToSort.length;
		
		for (i = 0, m = arrToSort.length; i < m; i++)
		{
			objSource.options[i].text = arrToSort[i];
			objSource.options[i].value = hashList[arrToSort[i]];
		}
	}
			
	/**
	 * Oeffnet ein neues Fenster
	 * 
	 * @param string
	 *            url URL
	 * @param string
	 *            name Name des Fensters
	 * @param int
	 *            width Breite
	 * @param int
	 *            height H?he
	 */
	var children=Array();

	/**
	 * Oeffnet ein neues Fenster, notiert dies innerhalb des Arrays children
	 * 
	 * @param string
	 *            url Zu ladenden URL
	 * @param string
	 *            Name des zu oeffnenden Fensters
	 * @param int
	 *            width Breite des Fensters
	 * @param int
	 *            height Hoehe des Fensters
	 */
	function popup(url,name,width,height) 
	{
		if (null == name)
		{ 
			name = 'ZABOS';
		}
		if (null == height)
		{ 
			height = 700;
		}
		if (null == width)
		{ 
			width = 500;
		}

		zaboswindow = window.open(url,name,'height=' + height + ',width=' + width + ',resizable=yes,scrollbars=yes,status=yes');
		children[children.length] = zaboswindow;
		
		if (window.focus) 
		{
			zaboswindow.focus()
		}
		return false;
	}

	/**
	 * Schliesst alle geoeffneten Fenster
	 */
	function popupClose() {
		for(var n=0;n<children.length;n++)
			{children[n].close();
		} 
	}
	
	/**
	 * oeffnet einen Hinweistext
	 * 
	 * @param int
	 *            id Unsinniger Parameter, kann jedoch dazu verwendet werden das
	 *            Zielelement zu benennen
	 */
	function Aenderung (id)
	{
		var divNotice = getObject("notice");
		
		divNotice.style.height = "22px";
		divNotice.style.backgroundColor = "#fff";
		divNotice.style.paddingTop = "5px";		
		divNotice.style.paddingLeft = "5px";				

		divNotice.innerHTML = "Es wurden &Auml;nderungen vorgenommen. Klicken Sie auf &quot;Speichern&quot; um diese zu &uuml;bernehmen";
	}
	
	/**
	 * Wählt alle Einträge der Select-Liste aus, da sonst Java/JSP nicht damit
	 * klarkommt
	 * 
	 * @param string
	 *            objSource Name der Select-Liste, die ausgewählt werden soll
	 */
	function selectAllEntries(objSource)
	{
	  var objSelectList = getObject(objSource);
	  
	  for (var i = 0, m = objSelectList.length; i < m; i++)
	  {
		objSelectList.options[i].selected=true;
	  }
	  
	  return true;
	}
  
    // Globale für die ggw. Action die auf einer Seite aufgerufen wurde
    var currentTab;
    
    
    /**
	 * Zeigt die "Teil"-Seite einer View mit dem übergebenen Parameter an und
	 * blendet die anderen Seiten aus
	 * 
	 * @param string
	 *            actionName ID der Action, die angezeigt werden soll
	 */
    function showTab(tabName) {
      var objNextTab, objCurrentTab, objErrorLayer;
    
      // ggw. Tab auf normal setzen
      if (null != currentTab) {
        objCurrentTab = getObject(currentTab);
      
        // Error-Layer verstecken
        objErrorLayer = getObject("errors");
        if (objErrorLayer != null) {
          objErrorLayer.style.display = "none";
        }
        
        if (null != objCurrentTab) {
          objCurrentTab.style.display = "none";
          getObject("tab_"+currentTab).className = "head_normal";
        }
      } 
      
      // N?chsten Tab highlighten
      objNextTab = getObject(tabName);
    
      if (null != objNextTab) {
        objNextTab.style.display = "block";
        getObject("tab_"+tabName).className = "head_highlight";
      
        currentTab = tabName;
      }
    }
    
    /**
	 * Loescht alle Elemente einer Select-Liste
	 * 
	 * @param string
	 *            objName Name des Objekts, dessen Liste geloescht werden soll
	 */
    function resetSelectList(objName) {
      var objSelectListe = getObject(objName);
      while (objSelectListe.length > 0) {
        objSelectListe.options[objSelectListe.length-1] = null;
      }
    }
    
    /**
	 * Fügt ein Element zu einer Select-Liste hinzu
	 * 
	 * @param string
	 *            objName Name der Select-Liste, die zu bef?llen ist
	 * @param object.Option
	 *            Objekt vom Typ Option
	 */
    function addElementToSelectList(objName,objOption) {
      getObject(objName).options[getObject(objName).length] = objOption;
    }
    
    /**
	 * Liefert den ggw. selektierten Wert der übergeben Liste zurück
	 * 
	 * @param string
	 *            objName Name der Select-Liste, die überprüft werden soll
	 * @param mixed
	 *            value Wert der Liste
	 */
    function getValueOfSelectList(objName) {
      var returnValue = null;
      
      if (getObject(objName).selectedIndex >= 0) { 
        returnValue = getObject(objName).options[getObject(objName).selectedIndex].value;
      }
      
      if (returnValue == null) {
        returnValue = 0;
      }
      
      return returnValue;
    }
    
    /**
	 * Überprüft, ob ein Element mit dem Wert value in der angegebenen
	 * Select-Liste existiert Liefert true bzw. false zurück
	 * 
	 * @param string
	 *            objName Name der Select-Liste, die ?berpr?ft werden soll
	 * @param string
	 *            value Wert
	 * @return boolean true|false
	 */
    function isValueElementInList(objName, value) {
      if (getIndexOfElementInList(objName, value) >= 0) {
        return true;
      }
      
      return false;
    }
    
    /**
	 * Liefert den Index des Elements zurück, dessen Wert value ist
	 * 
	 * @param string
	 *            objName Name der Select-Liste
	 * @param string
	 *            value Wert
	 * @return int -1 wenn nicht gefunden, andernfalls >= 0
	 */
    function getIndexOfElementInList(objName,value) {
      var returnValue = -1;
      
      for (var i = 0, m = getObject(objName).length; i < m; i++) {
        if (getObject(objName).options[i].value == value) {
          return i;
        }
      }
      
      return returnValue;
    }
    
    /**
	 * Aktiviert bzw. deaktiviert ein Element (setzt dessen Eigenschaft auf
	 * disabaled/enabled)
	 * 
	 * @param string
	 *            objCheckboxName Name der Checkbox, die überprüft werden soll
	 * @param string
	 *            objTargetName Name des Elements, dass je nach Status
	 *            deaktiviert/aktiviert werden soll
	 * @param boolean
	 *            toggleActiveCheckbox Wenn true: dann ist das objTargetName
	 *            immer aktiviert, wenn auch die Checkbox aktiviert ist. Ist die
	 *            Checkbox deaktiviert, dann wird das Element auch deaktiviert.
	 *            Wenn false: Invertiertes Verhalten
	 */
    function toggleActivationOnElement(_objCheckboxName,_objTargetName,_toggleActiveCheckbox) {
      var objCheckbox = getObject(_objCheckboxName);
      var objTarget = getObject(_objTargetName);
      
      if (objCheckbox.type != 'checkbox') {
        alert("Element muss vom Typ Checkbox sein");
      } else {
        if (objCheckbox.checked == true) {
          objTarget.disabled = _toggleActiveCheckbox;
        }  else {
          objTarget.disabled = !_toggleActiveCheckbox;
        }
        
        if (objCheckbox.checked == false) {
          objTarget.disabled = !_toggleActiveCheckbox;
        }  else {
          objTarget.disabled = _toggleActiveCheckbox;
        }
      }
    }

    var currentTelefonAdded = 1;
    
	function addTelefonnummer()
	{
		var table = getObject("tablePerson");
		var tr = document.createElement("tr");
		var tdl = document.createElement("td");
			tdl.className = "inner_option_large text";
	
		var textOption = document.createTextNode("Weitere Nummer");

		var tdr = document.createElement("td");	
			tdr.className = "text";
		
		currentTelefonAdded++;
		
		var input = document.createElement("input");
		input.type ="text";
		input.name = "arrTextTelefonNummern";
		input.className = "input";	
		input.size= "30";
		input.value ="0049";
		input.defaultValue = "0049";	
	
		tr.appendChild(tdl);
		tdl.appendChild(textOption);
		tr.appendChild(tdr);		
		tdr.appendChild(input);

		table.appendChild(tr);
	}	 
	
	function loadPersonDetails()
	{
		var PersonenDetails = getObject("PersonenDetails");
		var table = document.createElement("table");
		
		var tr = document.createElement("tr");
		var tr2 = document.createElement("tr");	
			
		var td = document.createElement("td");
			td.className = "text";
		// td.setAttribute("class","text");
			
		var span = document.createElement("span");
			span.className = "text_bold";
		// span.setAttribute("class","text_bold");
					
		var br = document.createElement("BR");	
	
		/* Content erstellen */
		var Person = document.createTextNode(objData.arrPersonen.szName);	

		/* Appending */
		PersonenDetails.appendChild(table);	
			
		table.appendChild(tr);
			tr.appendChild(td);
				td.appendChild(span);
				span.appendChild(Person);
				td.appendChild(br);	
		table.appendChild(tr);
			tr.appendChild(td);
				td.appendChild(br);
		
	}
	
	var openindex = 0;
	
	function hover(hoverin)
	{	
		var rights_headline = getObject("rights_headline");	
		
		if (hoverin)
		{
			rights_headline.style.backgroundColor = "#DDD";
		} else
		{
			rights_headline.style.backgroundColor = "#EEE";			
		}
	}	
		
	function resize(reload)
	{
		var cookiename = "ZABOSresizeRt"
		var rights = getObject("rights");
		var rights_headline = getObject("rights_headline");	
		var text1 = document.createTextNode("Rechte im aktuellen Kontext");	
		var text0 = document.createTextNode("Ihre Rechte anzeigen");		
		
		if (reload == true)															// Seite
																					// wurde
																					// neu
																					// geladen.
																					// Nur
																					// lesen,
																					// ausfuehren.
		{		
			if (checkcookie(cookiename))												// Cookie
																						// vorhanden
			{
				var openindex = checkcookie(cookiename);

				if (openindex == 0)														// Rechte
																						// offen
				{
						rights.style.height = "12px";
						rights.style.overflow = "hidden";
						rights_headline.innerHTML="";
						rights_headline.appendChild(text0);							
				} else 																	// Rechte
																						// zu
				{		
						rights.style.height = "auto";
						rights.style.overflow = "visible";						
						rights_headline.innerHTML="";
						rights_headline.appendChild(text1);	
				}
			} else																		// KEIN
																						// Cookie
																						// vorhanden
			{
				rights.style.height = "auto";												// Rechte
																							// offen,
																							// speichern
				rights.style.overflow = "visible";
				openindex = 0;
				setCookie(cookiename, openindex);	
			}
		} else																		// Seite
																					// wurde
																					// NICHT
																					// neu
																					// geladen.
																					// Aendern,
																					// speichern.
		{	
				var openindex = checkcookie(cookiename);
				
				if (openindex == 0)														// Rechte:
																						// offen
																						// > zu
				{
					rights.style.height = "auto";
					rights.style.overflow = "visible";
					rights_headline.innerHTML="";
					rights_headline.appendChild(text1);		
								
					openindex = 1;
					setCookie(cookiename, openindex);
			} else																		// Rechte:
																						// zu >
																						// offen
				{
					rights.style.height = "12px";
					rights.style.overflow = "hidden";
					rights_headline.innerHTML="";
					rights_headline.appendChild(text0);	
										
					openindex = 0;
					setCookie(cookiename, openindex);					
				}	
		}
	}
	/*
	 * Setzt einen Cookie @param String name Name des Cookies @param String
	 * value Zu schreibender Wert des Cookies
	 */	
	function setCookie(name, value) 
	{
    	document.cookie = name + "=" + escape(value) + "; expires=" + cookie_live() + "; path=/";
	}
	/*
	 * Liest den Wert des Cookies aus @param name Name des Cookies
	 */	
	function checkcookie(name) 
	{
	    var dc = document.cookie;
	    var prefix = name + "=";
	    var begin = dc.indexOf("; " + prefix);
	    if (begin == -1) {
	        begin = dc.indexOf(prefix);
	        if (begin != 0) return false;
	    } else {
	        begin += 2;
	    }
	    var end = document.cookie.indexOf(";", begin);
	    if (end == -1) {
	        end = dc.length;
	    }
	    return unescape(dc.substring(begin + prefix.length, end));
	} 
	/*
	 * Bestimmt Lebensdauer des Cookie
	 */	
	function cookie_live() 
	{
		var date=new Date();
		date.setDate(date.getDate()+ "90");
		var gmt=date.toGMTString();
		var k1=gmt.indexOf(" ");
		var k2=gmt.indexOf(" ", k1+1);
		var k3=gmt.indexOf(" ", k2+1);
		var str=gmt.substring(0,k2)+" "+gmt.substring(k2+1,k3)+" "+gmt.substring(k3+3,gmt.length);
	
		return str;
	}
	
	/*
	 * Markiert den letzen Eintrag einer Select-Liste @param String object Id
	 * der zu bearbeitenden Liste
	 */	
	function resetList (object) {		

		var list = getObject(object);
		var last = list.length;
		last--;
	
		list[last].selected = true;
	
	}
	/*
	 * Setzt alle Elemente der Alarmierungsseite wieder auf Urpsrungswerte
	 * zurück
	 */	
	function resetAlarmierung () {	// Setzt die Alarmierungsseite zurück
		
		resetList('selectOrganisationen');
		resetList('selectOrganisationseinheiten');
		
		var liste = getObject('selectSchleifenSendTo');
		var laenge = liste.length;
		laenge++;

		for (var i=0; i<=laenge;laenge--)
		{
			liste.options[laenge] = null;
		} 
		
		out_findSchleifenByPatternMitAusloeseberechtigung(in_findSchleifenByPatternMitAusloeseberechtigung);
	}	
	/*
	 * Gibt die aktuelle Zeit im Layer id=Uhr aus
	 */	
	function ZeitAnzeigen () 
	{
	
	var Uhr = getObject('Uhr');
	  var Wochentagname = new Array("Sonntag", "Montag", "Dienstag", "Mittwoch",
	                                "Donnerstag", "Freitag", "Samstag");
	  var Jetzt = new Date();
	  var Tag = Jetzt.getDate();
	  var Monat = Jetzt.getMonth() + 1;
	  var Jahr = Jetzt.getYear();
	  if (Jahr < 999)
	    Jahr += 1900;
	  var Stunden = Jetzt.getHours();
	  var Minuten = Jetzt.getMinutes();
	  var Sekunden = Jetzt.getSeconds();
	  var WoTag = Jetzt.getDay();
	  var Vortag = (Tag < 10) ? "0" : "";
	  var Vormon = (Monat < 10) ? ".0" : ".";
	  var Vorstd = (Stunden < 10) ? "0" : "";
	  var Vormin = (Minuten < 10) ? ":0" : ":";
	  var Vorsek = (Sekunden < 10) ? ":0" : ":";
	  var Datum = Vortag + Tag + Vormon + Monat + "." + Jahr;
	  var Uhrzeit = Vorstd + Stunden + Vormin + Minuten + Vorsek + Sekunden;
	  var Gesamt = Uhrzeit;
	

      Uhr.innerHTML = Gesamt;	    
	  window.setTimeout("ZeitAnzeigen()", 1000);

	}
	/*
	 * Versteckt alle Select-Elemente der Auslösungsseite für den IE, zeigt den
	 * Bestätigungsdialog an
	 */	
	function confirmAlarmOpen () 
	{
		/*
		 * DEPRECATED 
		 * Dieser Workaround war für den IE 6 notwendig, welcher ab dieser ZABOS-Version
		 * nicht mehr unterstützt wird. Der Code ist somit obsolet. 
		// Ausblenden der Select-Elemente fuer den IE < 7
		if (navigator.appName == "aaMicrosoft Internet Explorer" && navigator.appVersion.substring(22,23) < "7")  
		{
			var selectSchleifenSendTo = getObject("selectSchleifenSendTo");
			var selectOrganisationen = getObject("selectOrganisationen");
			var selectOrganisationseinheiten = getObject("selectOrganisationseinheiten");
			var selectSchleifenAvailable = getObject("selectSchleifenAvailable");

			selectSchleifenSendTo.style.visibility = "hidden";
			selectOrganisationen.style.visibility = "hidden";
			selectOrganisationseinheiten.style.visibility = "hidden";
			selectSchleifenAvailable.style.visibility = "hidden";
		}
		 */
		getObject("confirmAlarmShadow").style.display = "block";
		getObject("confirmAlarm").style.display = "block";
	}
	/*
	 * Zeigt alle Select-Elemente der Auslösungsseite für den IE, blendet den
	 * Bestätigungsdialog wieder aus
	 */	
	function confirmAlarmClose () 
	{
		/*
		 * DEPRECATED 
		 * Dieser Workaround war für den IE 6 notwendig, welcher ab dieser ZABOS-Version
		 * nicht mehr unterstützt wird. Der Code ist somit obsolet. 
		// Einblenden der Select-Elemente fuer den IE
		if (navigator.appName == "aaMicrosoft Internet Explorer")  
		{
			var selectSchleifenSendTo = getObject("selectSchleifenSendTo");
			var selectOrganisationen = getObject("selectOrganisationen");
			var selectOrganisationseinheiten = getObject("selectOrganisationseinheiten");					
			var selectSchleifenAvailable = getObject("selectSchleifenAvailable");			
	
			selectSchleifenSendTo.style.visibility = "visible";
			selectOrganisationen.style.visibility = "visible";
			selectOrganisationseinheiten.style.visibility = "visible";
			selectSchleifenAvailable.style.visibility = "visible";
		}
		*/
		getObject("confirmAlarm").style.display = "none";
		getObject("confirmAlarmShadow").style.display = "none";	
	}	
	/*
	 * Versteckt alle Select-Elemente der Auslösungsseite für den IE, zeigt den
	 * Bestätigungsdialog an
	 */	
	function confirmEntwarnungOpen () 
	{
		getObject("confirmAlarmShadow").style.display = "block";
		getObject("confirmAlarm").style.display = "block"
	}
	/*
	 * Setzt die bestätigte Entwarnugn ab
	 */	
	function confirmEntwarnung () 
	{
		confirmAlarmClose();
		document.frmEntwarnen.submit();
	}
	
	/*
	 * Setzt den Bestätigten Alarm ab
	 */	
	function confirmAlarm () 
	{
		// Modaldialog schließen
		confirmAlarmClose();
		// Alle Einträge der zu übermittelnden Liste markieren
		selectAllEntries('selectSchleifenSendTo');
		// Form submit
		document.forms.frmAlarm.submit();
	}
	/*
	 * Testet auf welchem Browser die Anwendung ausgeführt wird, und verhindet
	 * ggf. den Login
	 */	
	function checkBrowser ()
	{
	
		var txtNoSupport = ""+
		"<div class=\"text_bold\">"+
		"Fehler: "+
		"Ihr Browser ist nicht kompatibel mit dieser Anwendung!<br><br></div>"+
		"<div class=\"text\">"+
		"Bitte verwenden sie einen der folgenden unterst&uuml;tzten Browser:<br><br>"+
		"<a href=\"http://www.microsoft.com/windows/ie_intl/de/download/default.mspx\">Internet Explorer ab Version 7</a><br>"+
		"<a href=\"http://www.mozilla-europe.org/de/products/firefox/\">Mozilla Firefox ab Version 3.x</a><br>"+
		"<a href=\"http://www.opera.com/lang/de/\">Opera ab Version 9</a>";
			
		if(whichBrs() != true)
		{
			var formLogin = getObject('formLogin');
			formLogin.innerHTML = null;
			formLogin.innerHTML = txtNoSupport;
		}
			
			
	}
	/*
	 * Prueft auf welchem Browser die Anwendung ausgefuehrt wird und gibt bei
	 * bestandenem Test true zurueck.
	 */		
	function whichBrs() 
	{
		var agt=navigator.userAgent.toLowerCase();
		
		//if ((agt.indexOf("opera") != -1) || (agt.indexOf("firefox") != -1) || (agt.indexOf("applewebkit") != -1)) 
		if ((agt.indexOf("opera") != -1) || (agt.indexOf("firefox") != -1)) 
		{
			return true; // Firefox, Opera oder WebKit wird verwendet
		}
		else
		{
			if (agt.indexOf("msie") != -1)
			{
				if  (parseInt(navigator.appVersion.substring(22,23)) > 6)
				{
					return true; 	// Neuer als IE 7 wird verwendet
				} 
			}
			return false;
		}
		return false;
	}
	/*
	 * Prüft ob Opera verwendet wird (benötigt für script.aculo.us)
	 */
	function brsIsOpera() 
	{
		var agt=navigator.userAgent.toLowerCase();
		
		if ((agt.indexOf("opera") != -1)) 
		{
			return true; // Firefox oder Opera wird verwendet
		}
		return false
	}
	/*
	 * Testet ob Javascript verf?gbar ist. Wird das SCript ausgef?hrt, erscheint
	 * die Loginseite
	 */		
	function checkJS()
	{
		var messageNoJS = getObject('messageNoJS');
		var formLogin = getObject('formLogin');
		
		messageNoJS.style.display = "none";
		formLogin.style.display = "block";
	}
	
	/**
	 * Erzeugt den Button um eine neue Person mit der passenden Rolle anzulegen
	 * 
	 * @param int
	 *            RolleId Id der Rolle
	 * @param int
	 *            KontextId Id des Kontext, siehe
	 *            de.ecw.zabos.frontend.types.KontextType
	 * @param int
	 *            Id der Organisation
	 * @param int
	 *            Id der OE
	 * @param int
	 *            Id der Schleife
	 */
	function out_inputNeuePerson (RolleId,KontextId,oId,oeId,sId)
	{
		var outId = "";
		var inputNeuePerson = getObject('inputNeuePerson');
		inputNeuePerson.innerHTML = null;
		
		// Abfrage in welchem Kontext die Person angelegt werden soll; Anpassen
		// der Objekt-Id fuer den Link
		if (KontextId == 2) {
			outId = "OrganisationId="+oId+"&amp;";
		} else if (KontextId == 3) {
			outId = "OrganisationseinheitId="+oeId+"&amp;";
		} else if (KontextId == 4) {
			outId = "SchleifeId="+sId+"&amp;";
		}
		
		// Link bauen und ausgeben
		var innerHTML = "<input name=\"inputNeuePersonJS\" class=\"button\" type=\"button\" value=\"Neue Person\" "+
		" onclick=\"window.location.href='/zabos/controller/person/?PersonId=0&amp;"+ outId +"RolleId="+RolleId+"'\">";	
		
		inputNeuePerson.innerHTML = innerHTML;
	}
	/**
	 * Sobald die Checkbox unchecked ist, werden der Speichern-Button
	 * freigegeben, die Eingaben und ggf. Fehlermeldungen entfernt
	 * 
	 * @param String
	 *            item Checkbox
	 * @param String
	 *            pwd1 Erster Wert
	 * @param String
	 *            pwd2 Zweiter Wert
	 * @param String
	 *            out Ausgabeobjekt
	 * @param String
	 *            btn Zu entfernendes Objekt
	 */		
	function passwordUncheck(item,pwd1,pwd2,out,btn) {
		var checkbox = getObject(item);

		var password1 = getObject(pwd1);
		var password2 = getObject(pwd2);		
		var output = getObject(out);
		var button = getObject(btn);
		
		if (checkbox.checked == false) {
			output.innerHTML = "";
			button.style.visibility = "visible";	
			password1.value = "";
			password2.value = "";		
		}
	}
		
	/**
	 * Vergleicht zwei Werte und gibt gegebenenfalls einen Fehler aus und
	 * entfernt ein Object (wie Speichern-Button)
	 * 
	 * @param String
	 *            pwd1 Erster Wert
	 * @param String
	 *            pwd2 Zweiter Wert
	 * @param String
	 *            out Ausgabeobjekt
	 * @param String
	 *            btn Zu entfernendes Objekt
	 */	
	function passwordConfirm(pwd1,pwd2,out,btn)
	{
		var password1 = getObject(pwd1);
		var password2 = getObject(pwd2);
		var output = getObject(out);
		var button = getObject(btn);
		
		if (password1.value != "" && password2.value != "")
		{
			if (password1.value != password2.value) {
				output.innerHTML = "Die Eingaben sind nicht identisch!";
				button.style.visibility = "hidden";
			} else {
				output.innerHTML = "";
				button.style.visibility = "visible";
			}
		}
		
		if (password1.value == "" && password2.value == "")
		{
			output.innerHTML = "Es muss ein Password vergeben werden!";
			button.style.visibility = "hidden";
		}
		if (password1.value != "" && password1.value.length < 4 && password2.value.length < 4 && password2.value != "")
		{
			output.innerHTML = "Das Password muss mind. 4 Zeichen lang sein!";
			button.style.visibility = "hidden";
		}
		if ((password1.value != "" && password2.value == "") || (password1.value == "" && password2.value != "")) 
		{
			button.style.visibility = "hidden";		
		}
	}


	/**
	 * Kovertiert String von ANSI nach UTF, fuer Internet Explorer
	 * 
	 * @param String
	 *            rohtext Zu konvertierender Eingabestring
	 */	
	function encode_utf8(rohtextin) {
		// dient der Normalisierung des Zeilenumbruchs
		var rohtext = rohtextin.replace(/\r\n/g,"\n");
		var utftext = "";

		var agt=navigator.userAgent.toLowerCase();
		
		// 2006-07-03 CKL: Fix für Opera - ganz wichtig!!!
		if (agt.indexOf("msie") != -1 && (parseInt(navigator.appVersion.substring(22,23)) != 7) && agt.indexOf("opera") == -1)
		{
		
			for(var n=0; n<rohtext.length; n++)
			{
				// ermitteln des Unicodes des aktuellen Zeichens
				var c=rohtext.charCodeAt(n);
				// alle Zeichen von 0-127 => 1byte
		
				if (c<128)
					utftext += String.fromCharCode(c);
					// alle Zeichen von 127 bis 2047 => 2byte
				else if((c>127) && (c<2048)) 
				{
					utftext += String.fromCharCode((c>>6)|192);
					utftext += String.fromCharCode((c&63)|128);}
				// alle Zeichen von 2048 bis 66536 => 3byte
				else {
					utftext += String.fromCharCode((c>>12)|224);
					utftext += String.fromCharCode(((c>>6)&63)|128);
					utftext += String.fromCharCode((c&63)|128);
				}
			}
			return utftext;
		} else
		return rohtextin;
	} 	
	var ProbeterminAlt = true;
	
	function checkPastDate(time) {
	
		var objDateNow = new Date();		

		if (ProbeterminAlt == true) {
			if (time > Date.parse(objDateNow)) {
				writeln("</div>");
				ProbeterminAlt = false;
			} 
		}

	}
	/**
	 * Überprüft, ob das eingebenene Datum in der Zukunft liegt Spezielle
	 * Funktion für Report
	 */	
	function checkDate () {
		var textDatum = getObject("textDatum").value;
		var Tag = textDatum.substring(0,2);
		var Monat = textDatum.substring(3,5);
			Monat = parseInt(Monat) - 1;
		var Jahr = textDatum.substring(6);
		
		var objDate = new Date(Jahr, Monat, Tag);
		var objDateNow = new Date();

		var DateParsed = Date.parse(objDate);
		var DateNowParsed = Date.parse(objDateNow);
		
		DateParsed = parseInt(DateParsed);
		DateNowParsed = parseInt(DateNowParsed);

		if (DateParsed > DateNowParsed)
			alert("Das gewählte Datum liegt in der Zukunft!");
		else 
			document.forms.frmSelectDatum.submit();
	}
	/**
	 * Überprüft, ob das eingebenene Datum in der Zukunft liegt Spezielle
	 * Funktion für Report > StepFuture
	 */	
	function checkDateFuture () {
		var textDatum = getObject("textDatum").value;
		var Tag = textDatum.substring(0,2);
		var Monat = textDatum.substring(3,5);
			Monat = parseInt(Monat) - 1;
		var Jahr = textDatum.substring(6);
		
		var objDate = new Date(Jahr, Monat, Tag);
		var objDateNow = new Date();

		var DateParsed = Date.parse(objDate);
		var DateNowParsed = Date.parse(objDateNow);
		
		DateParsed = parseInt(DateParsed);
		DateNowParsed = parseInt(DateNowParsed);

		if (DateParsed > DateNowParsed) {
			alert("Das gewählte Datum liegt in der Zukunft!");
		} else {
			document.forms.frmSelectDatum.action +='&selectStep=future';
			document.forms.frmSelectDatum.submit();
		}
	}
	
	/**
	 * Blendet einen Layer über einen Button ein und wieder aus. Der Text des
	 * Buttons wird dabei ebenfalls geändert.
	 * 
	 * @param String
	 *            div Id des Layers, der ein und ausgeblendet werden soll
	 * @param String
	 *            btn Id des Buttons, der geändert werden soll
	 * @param String
	 *            btnText1 Text für "ausblenden" des Buttons
	 * @param String
	 *            btnText2 Text für "einblenden" des Buttons
	 */	
	var divShow = false;
	function showDivAlteTermine(div,btn,btnText1,btnText2) {
		var objToShow = getObject(div);
		var btnToChange = getObject(btn);

		if (divShow == false) {
			objToShow.style.display = "block";
			btnToChange.value = btnText1;
			divShow = true;
		} else {
			objToShow.style.display = "none";
			divShow = false;
			btnToChange.value = btnText2;
		}
	
	}
	/**
	 * Markiert alle Checkboxen innerhalb einer Div-Box
	 * 
	 * @param String
	 *            sourcediv Id der Div, in der sich die Inputfelder befinden
	 * @param String
	 *            checkbox Checkbox, die geprüft werden soll (Auslöser der
	 *            Aktion)
	 */	
	function selectAllCheckbox(sourcediv,checkbox) {
		var div = document.getElementById(sourcediv);
		var arr = div.getElementsByTagName("input");

		if (checkbox.type != 'checkbox') {
	        alert("Element muss vom Typ Checkbox sein");
		} else {
			if (checkbox.checked == true) {
				for (var i = 0, m = arr.length; i < m; i++) {
					if (arr[i].type != 'checkbox') {
						alert("Element muss vom Typ Checkbox sein");
			        } else {
						arr[i].checked = true;
					}
				}
			} else {
				for (var i = 0, m = arr.length; i < m; i++) {
					if (arr[i].type != 'checkbox') {
						alert("Element muss vom Typ Checkbox sein");
			        } else {
						arr[i].checked = false;
					}
				}
			}
		}
	}
	
	/**
	 * Wenn in dem objPerson-Objekt der Funktionsträger definiert ist, wird
	 * dessen Kürzel zurückegliefert, ansonsten "ND"
	 * 
	 * @param objPerson
	 * @returns
	 */
	function getFunktionstraegerKuerzel(objPerson)
	{
		if (objPerson.objFunktionstraeger)
			return objPerson.objFunktionstraeger.szKuerzel;
		
		return "ND";
	}
	
	/*
	 * Ermittelt den Status des Ergolgs eines Alarms, gibt den entsprechenden
	 * Stil zurück
	 * 
	 */
	function getStatus(int1,int2) {
		// Volle Anzahl erreicht
		if (int1 == int2) {
			// grün
			return "";
		// weniger erreicht
		} else {
			// rot
			return "";
		}
	}

	/**
	 * Fuellt die Zelle mit der Id _idOfCell mit den eubergebenen Benutzern
	 * 
	 * @param _arrObjUser
	 *            Array mit den Objekten User (iId, szNachname, szVorname)
	 * @param _idOfCell
	 *            Id der Zelle, die gefuellt werden soll
	 */
	function fillCellWithUsers(_arrObjUser,_idOfCell) {
		var outText = "";
	    if (_arrObjUser != null) {
	      for (var i = 0, m = _arrObjUser.length; i < m; i++) {
	        outText += _arrObjUser[i].szNachname+", "+arrObjUser[i].szVorname+"<br />";
	      }
	      document.getElementById(_idOfCell).innerHTML = outText;
	    }
	}
	function invis(object,operator) {
		if (operator)
			document.getElementById(object).style.display = "none";
		else
			document.getElementById(object).style.display = "block";
	}
	function hideObject(object,operator) {
		if (operator)
			getObject(object).style.visibility = "hidden";
		else
			getObject(object).style.visibility = "visible";
	}

	var lastHelpText = "";
	
	function showHelp(id,img) {
		var title = document.getElementById(id).title;
	
		if (img) {
			document.getElementById('helpLayer').innerHTML = '<div id="helpInfo" style="display:none" class="help helpImg">'+title+'</div>';
			if (brsIsOpera()) {
				document.getElementById('helpInfo').style.display = "block";
			} else {
				new Effect.Appear('helpInfo', {duration: 0.6})
			}
		} else {
			document.getElementById('helpLayer').innerHTML = '<div id="helpInfo" style="display:none" class="help helpNoImg">'+title+'</div>'; 
			if (brsIsOpera()) {
				document.getElementById('helpInfo').style.display = "block";
			} else {
				new Effect.Appear('helpInfo', {duration: 0.6})
			}
		}
	}
	function showHelp2(id,img) {
		var title = id.title;

		lastHelpText = document.getElementById('helpInfo').innerHTML;

		if (img) {
			document.getElementById('helpLayer').innerHTML = '<div id="helpInfo" style="display:none" class="help helpImg">'+title+'</div>';
			if (brsIsOpera()) {
				document.getElementById('helpInfo').style.display = "block";
			} else {
				new Effect.Appear('helpInfo', {duration: 0.6})
			}
		} else {
			document.getElementById('helpLayer').innerHTML = '<div id="helpInfo" style="display:none" class="help helpNoImg">'+title+'</div>'; 
			if (brsIsOpera()) {
				document.getElementById('helpInfo').style.display = "block";
			} else {
				new Effect.Appear('helpInfo', {duration: 0.6})
			}
		}
	}
	function setInvisHelp() {
		if (!checkcookie('ZABOShelpStatus')) {
			 setCookie('ZABOShelpStatus','visible');
		} else {
			if(checkcookie('ZABOShelpStatus') == 'invisible') {
				invis('helpTable',true);
			}
		}
	}
	function invisHelp(object) {
		if (checkcookie('ZABOShelpStatus') == 'visible') {
			invis(object,true);
			setCookie('ZABOShelpStatus','invisible');
		} else {
			invis(object,false);
			setCookie('ZABOShelpStatus','visible');
		}
	}
	function setHelpByClass() {
		/*
		 * Alternativ Parameter: Class > Angabe der zu suchenden Klasse; OMOver >
		 * Angabe der Funktion für OnMouseOver; OMOut > Angabe der Funktion für
		 * OnMouseOut;
		 */

		var allElements = document.getElementsByTagName('*');

		// Alle Elemente durchsuchen

		for (var i=0;i<allElements.length;i++) {

		    // Element hat CSS-Klasse? Wenn ja, ist es die gesuchte?
			var containsClass = allElements[i].className.search(/helpEvent+/);

		    if (containsClass != -1) {

		        // Title verfügt über Inhalt?
		        if (allElements[i].title && allElements[i].title != "") {
		            // Event-Anfügen
		            allElements[i].onmouseover = function(){
		            								showHelp2(this,true);
		            							};
		            allElements[i].onmouseout = function(){
													showLastHelp();
												};
		        }
		    }
		} 
	}
	function showLastHelp() {
		document.getElementById('helpLayer').innerHTML = '<div id="helpInfo" style="display:none" class="help helpNoImg">' + lastHelpText + '</div>'
		if (brsIsOpera()) {
			document.getElementById('helpInfo').style.display = "block";
		} else {
			new Effect.Appear('helpInfo', {duration: 0.6})
		}
	}
	/**
	 * Klappt ein Objekt in der Detailview der Alarmierung abhänging vom
	 * aktuellen Stauts auf oder zu object node Verstecktes Element, welches die
	 * Größeninformation für die zugeklappten Elemente durch Stil-Klasse vorhält
	 * 
	 * @param object
	 *            obj: Zu änderndes Object
	 */
	function showGroup(obj) {
		
		// Referenzhöhe eines geschlossenen Elementes feststellen
		// Dadurch kann die Höhe per CCS angepasst werden
		var height = getObject('groupSample').style.height;
		
		// Zu öffnendes oder zu schließendes Elternelement
		node = getObject(obj).parentNode;
		
		// Prüfen ob das Element geschlossen oder offen ist
		if (node.style.height == height ) { // Ist zusammengeklappt
			node.style.height = "100%";
			
			// Element muss dem Array der geöffneten Elemente hinzugefügt werden
			arrShowGroup.push(obj);
			
		} else {	// Ist ausgeklappt
			node.style.height = height;
			
			// Das Element muss aus dem Array der geöffneten Elemente enfernt
			// werden
			for (var i = 0, k = arrShowGroup.length; i < k; i++) {
				// Den Eintrag in dem Array ausfindig machen und entfernen
				if(arrShowGroup[i] == obj) {
					arrShowGroup.remove(i);					
				}
			}
		}
	}
	
	/**
	 * Öffnet alle Elemente, die nach dem Ajax-Refresh geöffnet waren object
	 * arrShowGroup Verwendet das globale Array um die zu öffnenden Elemente
	 * ausfindig zu macehn
	 */
	function showGroupAll() {
		
		// Sämtliche Elemente des Array mit den gespeicherten IDs durchlaufen
		for (var i = 0, k = arrShowGroup.length; i < k; i++) {
			// Zu öffnende Elternode ermitteln
			
			if(document.getElementById(arrShowGroup[i])) {
				node = getObject(arrShowGroup[i]).parentNode;
				
				// Node aufklappen
				node.style.height = "100%";
			}
		}
	}
	
	/**
	 * Entfernt einen Zuweisungseintrag in der bereicheFunktionstraeger.jsp
	 * 
	 * @param object
	 *            obj: Child ersten Grades des zu entfernendes Objekts
	 */
	function removeZuweisung(obj) {
		obj.parentNode.parentNode.parentNode.parentNode.removeChild(obj.parentNode.parentNode.parentNode);
	}
	/**
	 * Fügt einen Zuweisungseintrag in der bereicheFunktionstraeger.jsp hinzu
	 * 
	 */
	function addZuweisung() {
		if(document.getElementById("keineZuweisung")) {
			getObject("keineZuweisung").parentNode.removeChild(getObject("keineZuweisung"));
		}
		// getObject('bereicheContainer').innerHTML +=
		// getObject('bereicheFunktionstraegerNeu').innerHTML;
		
		// Inhalt für Zuweisungselement erzeugen
		var content = getObject('bereicheFunktionstraegerNeu').innerHTML;
		
		// Neue Node generieren, die angehangen werden kann
		var node = document.createElement("div");
		
		// Inhalt in Node schreiben
		node.innerHTML = content;
		
		// Die neue Node an bestehenden Inhalt anhängen
		getObject('bereicheContainer').appendChild(node);
	}
	
	/**
	 * Konvertiert einen Timestamp in das Datums-Format
	 * 
	 * @param timestamp
	 * @returns {String}
	 */
	function timestampToDate(timestamp) {
		var date = new Date(timestamp);

		var Stunden = date.getHours();
		var StdAusgabe = ((Stunden < 10) ? "0" + Stunden : Stunden);

		var Minuten = date.getMinutes();
		var MinAusgabe = ((Minuten < 10) ? "0" + Minuten : Minuten);
		
		var Sekunden = date.getSeconds();
		var SekAusgabe = ((Sekunden < 10) ? "0" + Sekunden : Sekunden);

		var Tag = date.getDay();
		var Wochentag = new Array("So", "Mo", "Di", "Mi", "Do", "Fr", "Sa");
		var dateUhrzeit = StdAusgabe + ":" + MinAusgabe + ":" + SekAusgabe;
		var dateTag = date.getDate();
		if (dateTag < 10)
			dateTag = "0" + dateTag;
		var dateMonat = date.getMonth();
		dateMonat++;
		if (dateMonat < 10)
			dateMonat = "0" + dateMonat;
		var dateJahr = date.getFullYear().toString();
		var dateDatum = dateTag + "." + dateMonat + "." + dateJahr.slice(2, 4);
		
		return Wochentag[Tag] + ", " + dateDatum + " " + dateUhrzeit;
	}