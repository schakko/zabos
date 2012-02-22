		
// Speichert alle Elemente, die ausgeklappt worden sind. Wird benötigt, wenn der Inhalt neu gelanden wird 
// um den alten Zustand wiederherzustellen
var arrShowGroup = new Array();

// Ist die Konfiguration geöffnet?
var bConfigurationOpened = false;

// Sobald die Konfiguration geöffnet wird muss das
// Aktualisierungsintervall gestoppt werden
function toggleConfigurationOpened() {
	bConfigurationOpened = !bConfigurationOpened;
	disableInterval(bConfigurationOpened);
}

/*
 * Funktion zum De- und Reaktivieren des Timers für die Reportaktualisierung:
 * Sobald ein Eingabefeld ausgewählt oder die Report-Konfiguration geändert wird
 * muss der Timer ausgesetzt werden, da sonst eine Benutzer-Eingabe unmöglich
 * wird. Ist die Eingabe beedent, muss er reaktiviert werden
 * 
 * @param boolean bActive Gibt an, ob das Interval aktiviert (true) oder
 * deaktivert (false) werden soll
 */
function disableInterval(bDisableInterval) {
	if(bDisableInterval) {
		window.clearInterval(timer);
		// Hinweis, dass der Timer deaktiviert worden ist entfernen
		getObject('notificationChange').style.display = 'block'
	} else {
		// Die angepassten Reportdaten an den Server übermitteln
		out_setReportOptionen();
		timer = window.setInterval("out_checkAlarmReportByBereich()", intInterval);
		// Sofortiger Durchlauf nach Reaktivierung
		out_checkAlarmReportByBereich();
		// Hinweis setzen, dass der Timer deaktiviert worden ist
		getObject('notificationChange').style.display = 'none';
	}
}

/**
 * Empfängt die Antwort nach dem Speichern eines Personenkommmentars
 * 
 * @param objData
 *            Daten in der JSON-Syntax
 */
function in_setLeitungsKommentar(objData) {
	// Sofern kein Fehler aufgetreten ist wird keine Antwort erwartet
	if (objData.error != null) {
		alert("Beim Speichern des Kommentars ist ein Fehler aufgetreten. Fehler: " + objData.error);
	}
}

/**
 * Empfängt die Antwort nach dem Speichern der Alarmreport-Konfiguration
 * 
 * @param objData
 *            Daten in der JSON-Syntax
 */
function in_setReportOptionen(objData) {
	// Sofern kein Fehler aufgetreten ist wird keine Antwort erwartet
	if (objData.error != null) {
		alert("Beim Speichern der Konfiguration ist ein Fehler aufgetreten. Fehler: " + objData.error);
	}
}

/**
 * Die angepasste Liste der anzuzeigenden Bereiche übermitteln
 */
function out_setReportOptionen() {

	// JSON-Object setzen
	var objData = {"arrReportOptionen":[]};

	// Index für Anzahl der Einzutragenden Bereiche
	var iFunktionstraeger = 0;
	
	// Form, die die benötigten Daten beinhält
	var form = document.frmSetReportOptionen;

	for (var i = 0, k = form.checkbox.length; i < k; i++) {
		if(form.checkbox[i].checked) {
			var val = form.checkbox[i].value;
			objData.arrReportOptionen.push(val);
		}
	}

	// Abfrage als POST absetzen, da die Datenmenge relativ groß ist
	zabosAjaxRequest('SetReportOptionen', objData, in_setReportOptionen, true);
}

/**
 * Alle verfügbaren Funktionsträger und Bereiche für die Konfiguration ermitteln
 */ 
function out_findFunktionstraegerMitBereichen() {

	// Daten setzen - es werden hier keine Daten benötigt
	var objData = {};
	
	// Abfrage starten
	zabosAjaxRequest('FindFunktionstraegerMitBereichen',objData,in_findFunktionstraegerMitBereichen);
}

/*
 * Funktion zum Speichern der Kommentare für bestimmte Personen
 * 
 * @param integer iPersonId Die ID der Person, für die ein Kommentar Kommentar
 * gespeichert werrden soll
 */
function out_setLeitungsKommentar(iPersonId) {
	
	var szValue = getObject(iPersonId).value;

	var objData = {'iAlarmId':ALARM_ID, 
					'iPersonId':iPersonId,
					'szLeitungsKommentar':szValue
					};
	
	zabosAjaxRequest('SetLeitungsKommentar',objData,in_setLeitungsKommentar);

	// Da beim Focus der Eingabebox das Intervall gestoppt wird, dieses
	// wieder starten
	disableInterval(false);
}

/**
 * Blendet die Detailansicht des Alarms ein oder aus
 * 
 */
function showDetails() {
	if (getObject('detailsContent').style.display == "none") {
		getObject('detailsContent').style.display = "block";
	} else {
		getObject('detailsContent').style.display = "none";
	}
}

/**
 * Eingehende Antworten vom AJAX-Server
 * 
 * @param objData
 *            Daten in der JSON-Syntax
 */
function in_checkAlarmReport(objData) {
	if (objData.error == null) {
	
		document.getElementById("antworten_ja").innerHTML = objData.iAlarmAntwortenJa;
		document.getElementById("antworten_nein").innerHTML = objData.iAlarmAntwortenNein;
		document.getElementById("antworten_spaeter").innerHTML = objData.iAlarmAntwortenSpaeter;
		document.getElementById("antworten_unbekannt").innerHTML = objData.iAlarmAntwortenUnbekannt;

		var divContent = getObject("divContent");
		divContent.innerHTML = "";	
								
		var arrSchleifen = objData.arrSchleifen;

		for (var i = 0, m = objData.arrSchleifen.length; i < m; i++)
		{

    	    var arrAlarmiertePersonenJa = arrSchleifen[i].arrPersonen.arrAlarmiertePersonenJa;
    	    var arrAlarmiertePersonenNein = arrSchleifen[i].arrPersonen.arrAlarmiertePersonenNein;
    	    var arrAlarmiertePersonenSpaeter = arrSchleifen[i].arrPersonen.arrAlarmiertePersonenSpaeter;
    	    var arrAlarmiertePersonenUnbekannt = arrSchleifen[i].arrPersonen.arrAlarmiertePersonenUnbekannt;

    	    var contentPersonenJa = "";
    	    var contentPersonenNein = "";		    	    
    	    var contentPersonenSpaeter = "";
    	    var contentPersonenUnbekannt = "";

	        for (var j = 0, n = arrAlarmiertePersonenJa.length; j < n; j++)
	        {
	          contentPersonenJa = contentPersonenJa + arrAlarmiertePersonenJa[j].szDisplayName + " (" + getFunktionstraegerKuerzel(arrAlarmiertePersonenJa[j]) + ")<br>";
	        }
	        for (var j = 0, n = arrAlarmiertePersonenNein.length; j < n; j++)
	        {
	          contentPersonenNein = contentPersonenNein + arrAlarmiertePersonenNein[j].szDisplayName + " (" + getFunktionstraegerKuerzel(arrAlarmiertePersonenNein[j]) + ")<br>";
	        }
	        for (var j = 0, n = arrAlarmiertePersonenSpaeter.length; j < n; j++)
	        {
	          contentPersonenSpaeter = contentPersonenSpaeter + arrAlarmiertePersonenSpaeter[j].szDisplayName + " (" + getFunktionstraegerKuerzel(arrAlarmiertePersonenSpaeter[j]) + ")<br>";
	        }
	        for (var j = 0, n = arrAlarmiertePersonenUnbekannt.length; j < n; j++)
	        {
	          contentPersonenUnbekannt = contentPersonenUnbekannt + arrAlarmiertePersonenUnbekannt[j].szDisplayName + " (" + getFunktionstraegerKuerzel(arrAlarmiertePersonenUnbekannt[j]) + ")<br>";
	        }

			var newState = ''+    
				'<table class="popup_inner_large">'+
				'	<tr class="list_head">'+
				'		<td class="h_spacer"></td>'+
    			'		<td class="inner_option_large">Schleife alamiert</td>'+
	   			'		<td class="text_bold">Empf&auml;nger R&uuml;ckmeldung</td>'+
	  			'	</tr>'+
				'	<tr class="v_spacer">'+
				'		<td colspan="3"></td>'+
				'	</tr>'+
				'	<tr>'+
				'		<td class="h_spacer"></td>'+						
				'		<td valign="top" class="inner_option_large">'+
				'			<table>'+
				'				<tr>'+
				'					<td>'+
				'						<strong>' + arrSchleifen[i].szDisplayName + '</strong><br><br>'+
				'						Organisation:<br> ' + arrSchleifen[i].objOrganisation.szDisplayName + '<br><br>'+
				'						Organisationseinheit:<br> ' + arrSchleifen[i].objOrganisationseinheit.szName + '<br>'+
				'					</td>'+
				'				</tr>'+
				'			</table>'+
				'		</td>'+
				'		<td valign="top">'+
				'			<table class="inner">'+
				'				<tr>'+
				'					<td class="tdgreen h_spacer">'+
				contentPersonenJa+
				'					</td>'+
				'				</tr>'+
				'				<tr>'+
				'					<td class="tdred h_spacer">'+
				contentPersonenNein+
				'					</td>'+
				'				</tr>'+
				'				<tr>'+
				'					<td class="tdblue h_spacer">'+
				contentPersonenSpaeter+
				'					</td>'+
				'				</tr>'+
				'				<tr>'+
				'					<td class="tdwhite h_spacer">'+
				contentPersonenUnbekannt+
				'					</td>'+
				'				</tr>'+
				'			</table>'+
				'		</td>'+
				'	</tr>'+
				'</table>'+
				'<table>'+
				'	<tr class="v_spacer">'+
				'		<td></td>'+
				'	</tr>'+
				'</table>';

			var lastState = divContent.innerHTML;		      
			divContent.innerHTML = lastState + newState;
				
		}
		document.getElementById("alarm_aktiv").innerHTML = (objData.bAlarmIstAktiv == true) ? ("Ja") : ("Nein");
      
	} else {
		alert(objData.error);
    }
}



/**
 * Klinikum-Report abfragen
 */
function out_checkAlarmReportByBereich() {

	// Daten setzen
	var objData = {'iAlarmId':ALARM_ID};
	
	// Abfrage starten
	zabosAjaxRequest('GetAlarmReportByBereichUndFunktionstraeger',objData,in_checkAlarmReportByBereich);
}


/**
 * Stellt Daten zu einem Alarm im Report dar
 * 
 * @param objData
 *            Daten in der JSON-Syntax
 */
function in_checkAlarmReportByBereich(objData) {
	// Daten in Rückanwort vorhanden
	if (objData.error == null) 
	{
		
		var arrFunktionstraegerOk = new Array();
   	
		/*
		 * Array, in welches die Anzahl der insgesamt in einem Funktionsträger
		 * alarmierten Personen gespeichert werden. Zudem wir aufgenommen,
		 * wieviele Personen davon mit "ja" geantwortet haben.
		 */ 
		var arrFunktionstraegerStatistik = new Array();
   	
		// Container für die darzustellenden Daten
		var contentNeu = '';
		
		// Anzahl der entsprechend zurückgemeldeten Personen. Total ^=
		// Soll
		var bereichePositiv = 0;
		var bereicheGesamt = 0;
		var personenGesamtSoll = 0;
		var personenGesamt = 0;
		
		// Anzeigen der Zahlen der Rückmeldungen, Alarmierten
		getObject("topPositiv").innerHTML = objData.iAlarmAntwortenJa;
		getObject("topAlarmiert").innerHTML = objData.iAlarmPersonenTotal;

		// Zeitpunkt, an dem der Alarm deaktiviert oder nachalarmiert
		// wird
		var nachalarmierungDeaktivierungZeitpunkt = objData.tsAlarmDeaktivierungNachalarmierung;
		
		var stringNachalarmierungDeaktivierungZeitpunkt = "unbekannt";
		
		// Eintragen des Zeitpunkts für Oberfläche vorbereiten
		if (objData.bAlarmIstAktiv) 
		{
			if (nachalarmierungDeaktivierungZeitpunkt != 0)
			{
				stringNachalarmierungDeaktivierungZeitpunkt = timestampToDate(nachalarmierungDeaktivierungZeitpunkt);
			}
		}
		else
		{
			stringNachalarmierungDeaktivierungZeitpunkt = "-";
		}
		
		// Links zu dem Print-Report erstellen
		if (objData.linkToReport != "")
		{
			getObject("linkToReport").innerHTML = objData.linkToReport;
			getObject("reportStatus").innerHTML = objData.linkToReport;
		            	getObject("reportStatusIcon").href = getObject("reportStatus").childNodes[0].href;
		} else {
			getObject("linkToReport").innerHTML = "Report kann erst nach Ablauf erzeugt werden";
			getObject("reportStatus").innerHTML = "Alarmiere...<a href=''></a>";
		}
		
		getObject("nachalarmierungDeaktivierungZeitpunkt").innerHTML = stringNachalarmierungDeaktivierungZeitpunkt;
		
		// Array mit den sichtbaren Schleifen
		var arrSchleifen = objData.arrSchleifen;
		
		for (var i = 0, m = arrSchleifen.length; i < m; i++) 
		{
			var objSchleife = arrSchleifen[i];
			var schleifeId = objSchleife.iId;
			contentNeu += '<div><strong>' + objSchleife.szDisplayName + '</strong><br />';
			
			// Array der alarmierten Funktionsträger
			var arrFunktionstraeger = objData.arrSchleifen[i].arrFunktionstraeger;
			
			// Funktionsträger durchlaufen
			for (var j = 0, n = arrFunktionstraeger.length; j < n; j++)
			{
				var contentFunktionstraeger = "";
	   		
				// Eigenschaften des Funktionsträgers ermitteln
				var funktionstraegerKuerzel = arrFunktionstraeger[j].szKuerzel;
				var funktionstraegerBeschreibung = arrFunktionstraeger[j].szBeschreibung;
				var funktionstraegerId = arrFunktionstraeger[j].iId;
				var funktionstraegerPersonenJa = 0; // Muss ggf am Ende
													// eingefügt werden
				var funktionstraegerPersonenGesamt = 0;
				
				// Anzeige Funktionsträger erstellen
				contentFunktionstraeger += '<div class="group">'+
				' <div id="' + schleifeId + "_" + funktionstraegerId + '" class="groupTitle status_fail" onClick="showGroup(\'' + schleifeId + "_" + funktionstraegerId + '\');">' +
				'<div style="width:615px;float:left;">' + funktionstraegerKuerzel + ' - ' + funktionstraegerBeschreibung + '</div><span id="' + schleifeId + "_" + funktionstraegerId + 'count">lade</span></div>';
	
				// Bereiche des Funktionsträgers ermitteln
				var arrBereiche = arrFunktionstraeger[j].arrBereich;
				
				// Bereiche durchlaufen
				
				// Variable, die festhält ob alle Bereiche positiv
					// zurückmelden
				var bereicheOk = true;
				
				// Variable, die festhält ob Bereiche des Funktonsträgers
					// sichtbar sind
				var funktionstraegerIstSichtbar = false;
				
				for (var k = 0, p = arrBereiche.length; k < p; k++)
				{
					var contentBereich = "";
					var bereich = arrBereiche[k];
					var statusSector = "status_ok";
					// Name des Bereichs ermitteln
					var istSichtbar = bereich.istSichtbar;
					
        			var bereichId = bereich.iId;
					var bereichName = bereich.szName;
					var bereichBeschreibung = bereich.szBeschreibung;
					var bereichPersonenJa = bereich.arrAlarmiertePersonenJa.length;
					var bereichPersonenSoll = bereich.iSollstaerke;
					var bereichPersonenGesamt = 0 + bereich.arrAlarmiertePersonenUnbekannt.length 
						+ bereich.arrAlarmiertePersonenJa.length 
						+ bereich.arrAlarmiertePersonenNein.length;
					
					// Berechnen, wieviele Bereiche insgesamt alarmiert
						// wurden
					// Nötig für spätere Anzeige des Status
					bereicheGesamt++;
					
					// Gesamtsoll der Personen berechnen
					personenGesamtSoll += bereichPersonenSoll;
						
					// Hochzählen aller Personen für Funktionsträger
					funktionstraegerPersonenGesamt += bereichPersonenSoll;
						
	        		// Ist das Soll an positiven Rückmeldungen erreicht?
	        		if(bereichPersonenSoll > bereichPersonenJa)
	        		{
	        			bereicheOk = false;
	        			statusSector = "status_fail";
	        		} 
	        		else 
	        		{
	        			// Hochzählen der positiv gemeldeten Personen
	        			bereichePositiv++;
	        		}	
	        		
	        		// Ist ein Bereich sichtbar ist auch FT sichtbar
	        		funktionstraegerIstSichtbar = true;
	        		
	        		// Anzeige Bereiche erstellen
	        		contentBereich += ' <div class="sector">'+
					' <div id="' + schleifeId + "_" + funktionstraegerId + '_' + bereichId + '" class="sectorTitle ' + statusSector + '" onClick="showGroup(\'' + schleifeId + "_" + funktionstraegerId + '_' + bereichId + '\');">' +
					'	<div style="width:580px;float:left;">' + bereichName + '</div><span>' + bereichPersonenJa + ' / ' + bereichPersonenSoll + '</span></div>';
	
	    			// Array der Personen, die mit "Ja" geantwortet haben
	        		var arrPersonenJa = bereich.arrAlarmiertePersonenJa;
			        		
	        		// Hochzählen der positiv gemeldeten Personen für
					// Funktionsträger
	        		funktionstraegerPersonenJa += arrPersonenJa.length;
	
	        		// Personen Ja durchlaufen
	        		// Jede Person wird durchlaufen und in das Ergebnis
					// eingetragen
	        		for (var l = 0, q = arrPersonenJa.length; l < q; l++)
		        	{
	        			// Aktuelle Person
	        			var person = arrPersonenJa[l];
	        			
	        			// Daten der Person ermitteln
	        			var vorname = person.szVorname; 
	        			var nachname = person.szNachname;
	        			var personId = person.iId;
	        			var personKommentar = "";
			        			
	        			if (person.objPersonInAlarm.szKommentarLeitung) {
	        				personKommentar = person.objPersonInAlarm.szKommentarLeitung;
		        		}
	
	        			personenGesamt++;
	        			// Inhalt schreiben
	        			contentBereich += ''+
	    				' 	<div class="personTitle status_ok"><div style="width:275px;float:left;">' + nachname + ', ' + vorname + '</div><span><input type="text" class="personComment" name="comment" id="' + personId + '" value="' + personKommentar + '" onFocus="disableInterval(true)"></input><input type="button" value="OK" class="personCommentOk" onClick="out_setLeitungsKommentar(\'' + personId + '\')"></span></div>';
		        	}
			        		
	        		var arrPersonenUnbekannt = bereich.arrAlarmiertePersonenUnbekannt;
	        		
	        		// Personen Unbekannt durchlaufen
	        		for (var l = 0, q = arrPersonenUnbekannt.length; l < q; l++)
		        	{
	        			var person = arrPersonenUnbekannt[l];
	        			
	        			var vorname = person.szVorname; 
	        			var nachname = person.szNachname;
	        			var personId = person.iId;
	        			var personKommentar = "";
			        			
	        			if(person.objPersonInAlarm.szKommentarLeitung) {
	        				personKommentar = person.objPersonInAlarm.szKommentarLeitung;
		        		}
	        			personenGesamt++;
	        			contentBereich += ''+
	    				' 	<div class="personTitle status_pending"><div style="width:275px;float:left;">' + nachname + ', ' + vorname + '</div><span><input type="text" class="personComment" name="comment" id="' + personId + '" value="' + unescape(personKommentar) + '" onFocus="disableInterval(true)"></input><input type="button" value="OK" class="personCommentOk" onClick="out_setLeitungsKommentar(\'' + personId + '\')"></span></div>';
		        	}
			        		
	        		var arrPersonenNein = bereich.arrAlarmiertePersonenNein;
	
	        		// Personen Nein durchlaufen
	        		for (var l = 0, q = arrPersonenNein.length; l < q; l++)
		        	{
	        			var person = arrPersonenNein[l];
	        			
	        			var vorname = person.szVorname; 
	        			var nachname = person.szNachname;
	        			var personId = person.iId;
	        			var personKommentar = "";
	        			personenGesamt++;
			        			
	        			if(person.objPersonInAlarm.szKommentarLeitung) {
	        				personKommentar = person.objPersonInAlarm.szKommentarLeitung;
		        		}
	        			contentBereich += ''+
	    				' 	<div class="personTitle status_fail"><div style="width:275px;float:left;">' + nachname + ', ' + vorname + '</div><!-- span><input type="text" class="personComment" name="comment" id="' + personId + '" value="' + personKommentar + '" onFocus="disableInterval(true)"></input><input type="button" value="OK" class="personCommentOk" onClick="out_setLeitungsKommentar(\'' + personId + '\')"></span --></div>';
		        	}
	
	        		// Sektor schließen
	        		contentBereich +='</div>';

	        		if (istSichtbar) {
						contentFunktionstraeger += contentBereich;
	        		}
			    }
			    
				contentFunktionstraeger +='</div>';
			        	
			    // Wenn alle Bereiche des Funktionsträgers positiv
				// zurückgemeldet haben, wird der FT positiv gekennzeichnet
			    if(bereicheOk) {
			    	arrFunktionstraegerOk.push(schleifeId + "_" + funktionstraegerId);
			    } 
			        	
			    // Daten zum Status der Personen und positiven Rückmeldungen
				// für
				// späteres Eintragen in Ergebnis notieren
			    // alert(funktionstraegerId + ' - ' +
				// funktionstraegerPersonenJa
				// + ' - ' + funktionstraegerPersonenGesamt);
			        	
			    var arrFunktionstraegerAktuell = new Array(schleifeId + "_" + funktionstraegerId, funktionstraegerPersonenJa, funktionstraegerPersonenGesamt);
			    arrFunktionstraegerStatistik.push(arrFunktionstraegerAktuell);
			        	
		        // Sollte kein Bereich innerhalb des Funktionsträgers
				// sichtbar
				// sein
		        // wird dieser komplett NICHT angezeigt
		        if(!funktionstraegerIstSichtbar) {
		        	contentFunktionstraeger = "";
		        } else {
		        	contentNeu += contentFunktionstraeger;
		        }
			} // for arrFunktionstraeger
			
			contentNeu += "</div>"; // div für die Schleife
		} // for arrSchleifen
				
				
		// Header des Alarmreports entsprechend des aktuellen Status
		// aller
		// Bereiche einfärben
		if(bereichePositiv >= bereicheGesamt) {
			getObject("head_status").className += "status_ok";				
		} else {
			getObject("head_status").className += "status_fail";
		}

		// *contentNeu += '</div>';
		getObject("notifactionArea").innerHTML = contentNeu;
		// alert(contentNeu);
			
		// arrFunktionstraegerOk durchgehen und alle postiven Einträge
		// kennzeichnen
		for (var i = 0, k = arrFunktionstraegerOk.length; i < k; i++) {
			document.getElementById(arrFunktionstraegerOk[i]).className += "groupTitle status_ok";
		}
		// arrFunktionstraeger durchgehen und alle Werte im Ergebnis
		// eintragen
		for (var i = 0, k = arrFunktionstraegerStatistik.length; i < k; i++) {
			var schleifeIdFunktionstraegerId = arrFunktionstraegerStatistik[i][0];
			var alarmiertJa = arrFunktionstraegerStatistik[i][1];
			var alarmiertGesamt = arrFunktionstraegerStatistik[i][2];
			var countId = schleifeIdFunktionstraegerId + "count";
					
			if(document.getElementById(countId)) {
				getObject(countId).innerHTML = "" + alarmiertJa + " / " + alarmiertGesamt;
			}
		}

		// getObject("alarm_aktiv").innerHTML = (objData.bAlarmIstAktiv
		// == true) ? ("Ja") : ("Nein");
			
		if(objData.bAlarmIstAktiv == true) {
			getObject("alarm_aktiv").innerHTML = "Ja";
		} else {
			getObject("alarm_aktiv").innerHTML = "Nein";
			getObject("entwarnen").innerHTML = '<div id="entwarnStatus" style="padding-top:16px;">Alarm wurde beendet</div>';
		}
				
		getObject("alarm_nachaktiviert").innerHTML = (objData.bIstNachalarmiert == true) ? ("Ja") : ("Nein");
		getObject("topSoll").innerHTML = personenGesamtSoll;

		getObject("numAnzahlPassenderPersonen").innerHTML = personenGesamt;
				
		if(objData.iAlarmAntwortenJa >= personenGesamtSoll) {
			getObject("topPositiv").className = "detailsElement topPositiv_ok";
		} else {
			getObject("topPositiv").className = "detailsElement topPositiv_fail";
		}
				
		// Sollte dies mindetens der zweite Durchlauf sein werden alle
		// zuvor
		// geöffneten
		// Elemente wieder geöffnet
		showGroupAll();
	} else {
		alert(objData.error);
    }
}
		


/**
 * Stellt im Alarm-Report alle Funktionsträger und Bereiche sowie eine
 * Konfigurationsmöglichkeit dar
 * 
 * @param objData
 *            Daten in der JSON-Syntax
 */
function in_findFunktionstraegerMitBereichen(objData) {
	
	// TODO: Abfrage erstellen für Checkboxen
	// TODO: Korrekte Ausgabe nach Vorlage in object.klinikum.jsp inkl.
	// Formular
	
	if (objData.error == null) {
		
    	// Container für die darzustellenden Daten
		var contentNeu = '';
		
		// Header setzen
		contentNeu = '<form name="frmSetReportOptionen"><div class="group">'+
			'<div id="configHeader" class="groupTitle status_non" onClick="showGroup(\'configHeader\');toggleConfigurationOpened();">Anzuzeigende Bereiche auswählen</div>'
		
		var arrFunktionstraeger = objData.arrFunktionstraeger;
		
		// Funktionsträger durchlaufen
		for (var j = 0, n = arrFunktionstraeger.length; j < n; j++)
        {
        	// Eigenschaften des Funktionsträgers ermitteln
			var funktionstraegerKuerzel = arrFunktionstraeger[j].szKuerzel;
			var funktionstraegerBeschreibung = arrFunktionstraeger[j].szBeschreibung;
			var funktionstraegerId = arrFunktionstraeger[j].iId;
			
        	// Anzeige Funktionsträger erstellen
        	contentNeu += '<div class="sector">'+
			' <div id="c' + funktionstraegerId + '" class="sectorTitle status_non" onClick="showGroup(\'c' + funktionstraegerId + '\');"><!-- input type="checkbox" value="001100" onClick="showGroup(\'c' + funktionstraegerId + '\');" style="margin-right:10px;" --><span style="vertical-align:top;">' + funktionstraegerKuerzel + ' - ' + funktionstraegerBeschreibung + '</span></div>';

			// Bereiche des Funktionsträgers ermitteln
        	var arrBereiche = arrFunktionstraeger[j].arrBereich;
        	
        	// Bereiche durchlaufen

        	for (var i = 0, m = arrBereiche.length; i < m; i++)
        	{
        		var bereich = arrBereiche[i];
        		var istSichtbar = bereich.istSichtbar;
        		var bereichId = bereich.iId;
        		var bereichName = bereich.szName;
        		var bereichBeschreibung = bereich.szBeschreibung;
        		
        		var checked = "";
        		
        		if(istSichtbar){
        			checked='checked="checked"';
        		}
        		// Anzeige Bereiche erstellen
        		contentNeu += ' <div class="personTitle">'+
				' <div class="sectorTitle status_non"><input type="checkbox" ' + checked + 'name="checkbox" value="' + funktionstraegerId + '_' + bereichId + '" style="margin-right:10px;">' + bereichName + '</div>';
        		
        		// Sektor schließen
        		contentNeu +='</div>';
        		
        	}
        	contentNeu +='</div>';
        	
        }
		
		contentNeu += '</div></form>';
		getObject("configurationArea").innerHTML = contentNeu;
		
	} else {
		alert(objData.error);
    }
}