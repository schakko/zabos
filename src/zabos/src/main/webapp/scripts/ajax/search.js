/**
 * Schleifen nach Pattern suchern
 */
function out_findSchleifenByPatternMitAusloeseberechtigung(objMethodRequest) {
	var szPattern = getObject("inputSearch").value;
	var objData = {
		"szPattern" : szPattern
	};
	zabosAjaxRequest('FindSchleifenByPatternMitAusloeseberechtigung', objData,
			objMethodRequest, true);
}

/**
 * Eingehende Antworten vom AJAX-Server
 * 
 * @param objData
 *            Daten in der JSON-Syntax
 */
function in_findSchleifenByPatternMitAusloeseberechtigung(objData) {
	resetSelectList('selectSchleifenAvailable');
	for ( var i = 0, m = objData.arrSchleifen.length; i < m; i++) {
		if (isValueElementInList('selectSchleifenSendTo',
				objData.arrSchleifen[i].iId) == false) {
			addElementToSelectList('selectSchleifenAvailable', new Option(
					objData.arrSchleifen[i].szDisplayName,
					objData.arrSchleifen[i].iId));
		}
	}
	hideObject('indicator', true);
}

/**
 * Personen nach Pattern suchern
 */
function out_findPersonenByPattern(objMethodRequest, inputElement) {
	if (inputElement == undefined){
		inputElement = "inputSearch";
	}
	
	var szPattern = getObject(inputElement).value;

	var objData = {
		"szPattern" : szPattern
	};
	zabosAjaxRequest('FindPersonByPattern', objData, objMethodRequest, true);
}
/**
 * Person nach Id finden
 */
function out_findPersonById(iIdUser, objMethodRequest) {
	var objData = {
		"iIdUser" : iIdUser
	};
	zabosAjaxRequest('FindPersonById', objData, objMethodRequest);
}

/**
 * Person nach Id finden
 */
function in_findPersonById(objData) {
	var PersonenDetails = getObject("PersonenDetails");

	PersonenDetails.innerHTML = "";
	
	// Fehler ist aufgetreten, z.B. fehlende Berechtigung
	if (objData.szError) {
		PersonenDetails.innerHTML = objData.szError;
		return;
	}

	/* Content erstellen */

	var email = "keine Angabe";
	var telefonNummer = "";

	if (objData.objPerson.szEmail)
		email = objData.objPerson.szEmail;

	if (objData.objPerson.objTelefon)
		telefonNummer = objData.objPerson.objTelefon.szTelefonNummer;

	var innerTable = "" + "<table class=\"text inner\">" + "	<tr>"
			+ "		<td class=\"text_bold inner_option\">Benutzername</td>"
			+ "		<td class=\"text\">"
			+ objData.objPerson.szName
			+ "</td>"
			+ "	</tr>"
			+ "	<tr>"
			+ "		<td class=\"text_bold inner_option\">Name </td>"
			+ "		<td class=\"text\"> "
			+ objData.objPerson.szDisplayName
			+ "</td>"
			+ "	</tr>"
			+ "	<tr>"
			+ "		<td class=\"text_bold inner_option\">ID </td>"
			+ "		<td class=\"text\">"
			+ objData.objPerson.iId
			+ "</td>"
			+ "	</tr>"
			+ "	<tr>"
			+ "		<td class=\"text_bold inner_option\">Bev. Tel-Nr. </td>"
			+ "		<td class=\"text\">"
			+ telefonNummer
			+ "</td>"
			+ "	</tr>"
			+ "	<tr>"
			+ "		<td class=\"text_bold inner_option\">Email </td>"
			+ "		<td class=\"text\">"
			+ email
			+ "</td>"
			+ "	</tr>"
			+ "	<tr>"
			+ "		<td class=\"inner_option\"></td>"
			+ "		<td class=\"text\"><input title=\"Klicken Sie auf die Schaltfläche, um die Einstellunge dieser Person zu bearbeiten\" id=\"inputPersonenDetails\" name=\"inputPersonenDetails\" class=\"helpEvent button\" type=\"submit\" value=\"Bearbeiten\" onclick=\"window.location.href='/zabos/controller/person/?PersonId="
			+ objData.objPerson.iId
			+ "'\"></td>"
			+ "	</tr>"
			+ "	<tr>"
			+ "		<td class=\"inner_option\"></td>"
			+ "		<td class=\"text\"><input title=\"Klicken Sie auf die Schaltfläche, um sich anzeigen zu lassen, in welchen Einheiten dieser Person eine Rolle zugeordnet ist.\" id=\"btnHierachie\" name=\"btnHierachie\" class=\"helpEvent button\" type=\"button\" value=\"Vererbung zeigen\" onClick=\"window.location.href='/zabos/controller/hierarchie/?tab=person_rolle&amp;PersonId="
			+ objData.objPerson.iId + "'\"></td>" + "	</tr>" + "</table>";

	PersonenDetails.innerHTML = innerTable;
}

/**
 * Eingehende Antworten vom AJAX-Server
 * 
 * @param objData
 *            Daten in der JSON-Syntax
 */
function in_schleifenEditRollen(objData) {
	resetSelectList('selectPersonenAvailable');
	// SelectBox mit den Organisationen updaten
	for ( var i = 0, m = objData.arrPersonen.length; i < m; i++) {
		if (isValueElementInList('selectPersonenAssigned',
				objData.arrPersonen[i].iId) == false) {
			addElementToSelectList('selectPersonenAvailable', new Option(
					objData.arrPersonen[i].szDisplayName,
					objData.arrPersonen[i].iId));
		}
	}
	hideObject('indicatorSP', true);
}

/**
 * Eingehende Antworten vom AJAX-Server
 * 
 * @param objData
 *            Daten in der JSON-Syntax
 */
function in_systemListPersonen(objData) {

	resetSelectList("selectPersonen");
	// SelectBox mit den Organisationen updaten
	for ( var i = 0, m = objData.arrPersonen.length; i < m; i++) {
		addElementToSelectList('selectPersonen', new Option(
				objData.arrPersonen[i].szDisplayName,
				objData.arrPersonen[i].iId));

		// Anfuegen des onclick-Events
		onclick = "javascript:out_findPersonById(" + objData.arrPersonen[i].iId
				+ ",in_findPersonById)";
		// getObject("selectPersonen").options[i].setAttribute("onclick",onclick);

	}
	hideObject('indicator', true);
}
/**
 * Letzten x Alarmierungen finden
 */
function out_findLetzteAlarmierungenMitBerechtigung(objMethodRequest) {
	var objData = {};
	zabosAjaxRequest('FindLetzteAlarmierungenMitBerechtigung', objData,
			objMethodRequest);
}

/**
 * Eingehende Antworten vom AJAX-Server
 * 
 * @param objData
 *            Daten in der JSON-Syntax
 */
function in_findLetzteAlarmierungenMitBerechtigung(objData) {

	/* Objekt laden */
	var iframe = getObject("iframe");

	/* Alten Inhalt entfernen */
	iframe.innerHTML = "";

	for ( var i = 0, m = objData.arrAlarmierungen.length; i < m; i++) {

		var contentExists = iframe.innerHTML; // Aktuellen Inhalt Speichern

		/* Datumsberechnung */
		var ausloesungDatum = timestampToDate(objData.arrAlarmierungen[i].tsAusloesung)

		var contentEntwarnung = "";
		if (objData.arrAlarmierungen[i].tsEntwarnung != "0") {
			contentEntwarnung = "" + "	<tr>"
					+ "		<td class=\"base text_bold text_red\">ENTWARNT "/* +objData.arrAlarmierungen[i].tsEntwarnung */
					+ // Entwarnt
					"		</td>" + "	</tr>";
		}

		var strURL = "/zabos/controller/report/?tab=object&JSESSIONID="
				+ JSESSIONID + "&AlarmId=" + objData.arrAlarmierungen[i].iId;

		var istAktiv = objData.arrAlarmierungen[i].bAlarmIstAktiv;
		if (istAktiv == true) {
			var icoAktiv = "<img style=\"padding-left:2px; padding-top:1px; padding-right:2px;\" src=\"../../images/icoAktiv.gif\" alt=\"Alarm ist aktiv\">"; // 
		} else {
			var icoAktiv = " ";
		}

		var contentSchleifen = "";
		
		if (objData.arrAlarmierungen[i].arrSchleifen) {
			for ( var j = 0, n = objData.arrAlarmierungen[i].arrSchleifen.length; j < n; j++) {
				contentSchleifen += objData.arrAlarmierungen[i].arrSchleifen[j].szKuerzel;

				if ((j + 1) != n) {
					contentSchleifen += ", ";
				}
			}
		} else {
			contentSchleifen = "Fehler: Keine Schleifen!!!";
		}

		var innerContent = "" + "<table class=\"inner\">"
				+ "	<tr style=\"cursor: pointer;\" onclick=\"popup('"
				+ strURL
				+ "','History','700','550')\">"
				+ "		<td class=\"base text_bold\"><span style=\"text-decoration:underline;\">"
				+ icoAktiv
				+ "Alarm: "
				+ objData.arrAlarmierungen[i].iAlarmReihenfolge.toString(16)
				+ "</span> "
				+ "		</td>"
				+ "	</tr>"
				+ contentEntwarnung
				+ "	<tr>"
				+ "		<td class=\"text\"><div style=\"width:100px\"><strong>"
				+ ausloesungDatum
				+ "</div></strong>"
				+ "		</td>"
				+ "	</tr>"
				+ "   <tr>"
				+ "       <td class=\"text\">"
				+ contentSchleifen
				+ "       </td>"
				+ "   </tr>"
				+ "	<tr>"
				+ "		<td class=\"text\">Antworten total: "
				+ (objData.arrAlarmierungen[i].iAlarmAntwortenJa
						+ objData.arrAlarmierungen[i].iAlarmAntwortenNein + objData.arrAlarmierungen[i].iAlarmAntwortenSpaeter)
				+ // Antworten total
				"		</td>"
				+ "	</tr>"
				+ "	<tr>"
				+ "		<td class=\"text\">J:<span class=\"text_green\"> "
				+ objData.arrAlarmierungen[i].iAlarmAntwortenJa
				+ " </span> N:<span class=\"text_red\"> "
				+ objData.arrAlarmierungen[i].iAlarmAntwortenNein
				+ "</span> S:<span class=\"text_blue\"> "
				+ objData.arrAlarmierungen[i].iAlarmAntwortenSpaeter
				+ " </span>" + // Antworten einzeln
				"		</td>" + "	</tr>" + "	<tr class=\"v_spacer\">" + "		<td>" + // Spacer
				"		</td>" + "	</tr>";

		iframe.innerHTML = contentExists + innerContent;

	}
}